package com.samutup.kafka.consumer;

import com.samutup.kafka.liveready.AppCheckHandler;
import com.samutup.kafka.liveready.LifenessReadinessCheck;
import com.samutup.kafka.liveready.ServerStartupListener;
import com.samutup.kafka.liveready.ServerStartupListenerImpl;
import com.samutup.kafka.settings.TweetySetting;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;

public class KafkaConsumerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerVerticle.class);

  static KafkaConsumer<String, String> kafkaConsumerBuilder(
      Vertx vertx, TweetySetting tweetySetting) {
    Map<String, String> config = new HashMap<>();
    String bootstrap = String
        .format("%s:%s", tweetySetting.getBrokerHost(), tweetySetting.getBrokerPort());
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("group.id", tweetySetting.getConsumerGroup());
    config.put("auto.offset.reset", "earliest");
    config.put("enable.auto.commit", "false");
    return KafkaConsumer.create(vertx, config);
  }

  Handler<HttpResponse<Buffer>> httpResponseHandler = bufferHttpResponse -> LOGGER
      .info(bufferHttpResponse.bodyAsString());
  Handler<Throwable> throwableHandler = throwable -> LOGGER.error("error", throwable);

  private void consumeTopic(HttpRequest<Buffer> bufferHttpRequest,
      KafkaConsumer<String, String> consumer) {
    new Thread(() -> {
      while (true) {
        consumer.poll(Duration.ofMillis(100))
            .onSuccess(records -> records.records()
                .forEach(p -> bufferHttpRequest.sendJson(Json.decodeValue(p.value()))
                    .onSuccess(httpResponseHandler)
                    .onFailure(throwableHandler)))
            .onFailure(throwable -> LOGGER.error("Failed processing", throwable));
      }
    }).start();


  }

  private static Handler<RoutingContext> handleAdminRequest(
      WebClient webClient, TweetySetting tweetySetting) {
    return rc -> {

      if (HttpMethod.DELETE.equals(rc.request().method())) {
        rc.response().setStatusCode(HttpResponseStatus.OK.code()).end();
      } else if (HttpMethod.POST.equals(rc.request().method())) {
        //get elastic search
        HttpRequest<Buffer> httpRequest = webClient.request(HttpMethod.PUT, SocketAddress
                .inetSocketAddress(tweetySetting.getElasticPort(), tweetySetting.getElasticHost()),
            rc.request().uri());
        httpRequest.sendJson(Json.decodeValue(rc.getBodyAsString()))
            .onSuccess(bufferHttpResponse -> LOGGER.info(
                "OK " + bufferHttpResponse.statusCode() + bufferHttpResponse.statusMessage()
                    + bufferHttpResponse.bodyAsString()));
        rc.response().setStatusCode(HttpResponseStatus.CREATED.code()).end();
      } else if (HttpMethod.GET.equals(rc.request().method())) {
        HttpRequest<Buffer> httpRequest = webClient.request(HttpMethod.GET, SocketAddress
                .inetSocketAddress(tweetySetting.getElasticPort(), tweetySetting.getElasticHost()),
            rc.request().uri());
        Future<HttpResponse<Buffer>> responseFuture = httpRequest.send();
        responseFuture.onSuccess(res -> rc.response().putHeader("Content-Type", "application/json")
            .setStatusCode(res.statusCode()).end(res.bodyAsString()))
            .onFailure(res -> rc.response().setStatusCode(500).end(res.getMessage()));
      } else {
        rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
      }

    };

  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    try {
      TweetySetting tweetySetting = config().mapTo(TweetySetting.class);
      RequestOptions requestOptions = new RequestOptions().setHost(tweetySetting.getElasticHost())
          .setPort(tweetySetting.getElasticPort())
          .setURI(String.format("%s%s", tweetySetting.getIndice(), tweetySetting.getIndiceType()));
      HttpRequest<Buffer> elasticPutMessage = WebClient.create(vertx)
          .request(HttpMethod.POST, requestOptions);

      WebClient webClient = WebClient.create(vertx);
      LOGGER.info("retrieve settings from yaml " + tweetySetting);
      int portNumber = tweetySetting.getPort();
      //list all path
      ServerStartupListener serverStartupListenHandler = new ServerStartupListenerImpl(startPromise,
          portNumber, tweetySetting);
      // register readiness and liveness check
      //new AppCheckHandler[]{serverStartupListenHandler}
      LifenessReadinessCheck
          .registerReadinessCheck(router, new AppCheckHandler[]{serverStartupListenHandler});
      LifenessReadinessCheck.registerLivenessCheck(router, null);
      //call kafka
      KafkaConsumer<String, String> consumer = kafkaConsumerBuilder(vertx, tweetySetting);
      consumer.subscribe(tweetySetting.getTopicName());
      consumeTopic(elasticPutMessage, consumer);

      // create server
      HttpServer server = vertx.createHttpServer();
      // tweetListener.listen(kafkaProducer, tweetySetting.getTopicName());

      router.route().handler(BodyHandler.create())
          .handler(handleAdminRequest(webClient, tweetySetting));
      server.requestHandler(router).listen(portNumber, serverStartupListenHandler);
    } catch (Exception exception) {
      LOGGER.error("Unexpected error, config " + config(), exception);
    }
  }
}
