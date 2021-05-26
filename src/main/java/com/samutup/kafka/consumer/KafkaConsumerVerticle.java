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
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KafkaConsumerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerVerticle.class);
  static Consumer<KafkaConsumerRecord<String, String>> recordConsumer = consumerRecord -> LOGGER
      .info(
          "processing key=" + consumerRecord.key() + " value=" + consumerRecord.value()
              + " partition=" + consumerRecord.partition()
              + " offset=" + consumerRecord.offset());

  static KafkaConsumer<String, String> kafkaConsumerBuilder(String bootstrap, String group,
      Vertx vertx) {
    Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", bootstrap);
    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("group.id", group);
    config.put("auto.offset.reset", "earliest");
    config.put("enable.auto.commit", "false");
    return KafkaConsumer.create(vertx, config);
  }

  @FunctionalInterface
  interface Transform<R, T, C> {

    C apply(R clientBuilder, T t);
  }

  private Handler<RoutingContext> contextHandler = rc -> {

    if (HttpMethod.DELETE.equals(rc.request().method())) {
      rc.response().setStatusCode(HttpResponseStatus.OK.code()).end();
    } else if (HttpMethod.POST.equals(rc.request().method())) {
      //get elastic search

      rc.response().setStatusCode(HttpResponseStatus.CREATED.code()).end();
    } else {
      rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
    }

  };

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
        responseFuture.onSuccess(res -> rc.response().putHeader("Content-Type","application/json").setStatusCode(res.statusCode()).end(res.bodyAsString()))
            .onFailure(res->rc.response().setStatusCode(500).end(res.getMessage()));
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
