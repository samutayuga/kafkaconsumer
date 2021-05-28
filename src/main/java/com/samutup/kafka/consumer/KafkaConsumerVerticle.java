package com.samutup.kafka.consumer;

import com.samutup.kafka.KafkaConsumerMaster;
import com.samutup.kafka.liveready.AppCheckHandler;
import com.samutup.kafka.liveready.LifenessReadinessCheck;
import com.samutup.kafka.liveready.ServerStartupListener;
import com.samutup.kafka.liveready.ServerStartupListenerImpl;
import com.samutup.kafka.settings.SettingLoader;
import com.samutup.kafka.settings.TweetySetting;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class KafkaConsumerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerVerticle.class);


  private static Handler<RoutingContext> handleAdminRequest() {
    return rc -> {

      if (HttpMethod.DELETE.equals(rc.request().method())) {
        rc.response().setStatusCode(HttpResponseStatus.OK.code()).end();
      } else if (HttpMethod.POST.equals(rc.request().method())) {
        //get elastic search
        //send the job to another verticle
        ConfigRetriever.create(rc.vertx(), SettingLoader.getConfigRetrieverOptions())
            .getConfig(event -> {
              if (event.succeeded()) {
                //deploy verticle
                KafkaConsumerMaster
                    .deploy(rc.vertx(), event.result(), TweetConsumerVerticle.class.getName(),
                        true);

              } else {
                //deploy verticle
              }
            });
        rc.response().setStatusCode(HttpResponseStatus.CREATED.code()).end();
      } else if (HttpMethod.GET.equals(rc.request().method())) {
        /*HttpRequest<Buffer> httpRequest = webClient.request(HttpMethod.GET, SocketAddress
                .inetSocketAddress(tweetySetting.getElasticPort(), tweetySetting.getElasticHost()),
            rc.request().uri());
        Future<HttpResponse<Buffer>> responseFuture = httpRequest.send();
        responseFuture.onSuccess(res -> rc.response().putHeader("Content-Type", "application/json")
            .setStatusCode(res.statusCode()).end(res.bodyAsString()))
            .onFailure(res -> rc.response().setStatusCode(500).end(res.getMessage()));*/
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
          .handler(handleAdminRequest());
      server.requestHandler(router).listen(portNumber, serverStartupListenHandler);
    } catch (Exception exception) {
      LOGGER.error("Unexpected error, config " + config(), exception);
    }
  }
}
