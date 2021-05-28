package com.samutup.kafka;

import com.samutup.kafka.consumer.KafkaConsumerVerticle;
import com.samutup.kafka.settings.SettingLoader;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class KafkaConsumerMaster {
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerMaster.class);

  static {
    InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
  }
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(10).setBlockedThreadCheckIntervalUnit(
        TimeUnit.SECONDS));
    ConfigRetriever.create(vertx, SettingLoader.getConfigRetrieverOptions()).getConfig(event -> {
      if (event.succeeded()) {
        //deploy verticle
        deploy(vertx, event.result(), KafkaConsumerVerticle.class.getName());

      } else {
        //deploy verticle
      }
    });
  }

   public static void deploy(Vertx vertx, JsonObject config, String verticleName) {
    DeploymentOptions routerDeploymentOptions = new DeploymentOptions().setConfig(config);
    vertx.deployVerticle(verticleName, routerDeploymentOptions, result -> {
      if (result.succeeded()) {
        LOGGER.info("Successfully deploy the verticle");
      } else if (result.failed()) {
        result.cause().printStackTrace();
        LOGGER.error(result.cause());
        System.exit(-1);
      } else {
        LOGGER.info("result " + result);
      }

    });
  }
}
