package com.samutup.kafka.consumer;

import com.samutup.kafka.settings.TweetySetting;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class TweetConsumerVerticle extends AbstractVerticle {


  private static String getTweetId(String jsonStr) {
    Map<String, String> objectMap = Json.decodeValue(jsonStr, Map.class);
    if ((objectMap != null) && objectMap.containsKey("id")) {
      return objectMap.get("id");
    }
    return null;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetConsumerVerticle.class);
  static Handler<Throwable> throwableHandler = throwable -> LOGGER.error("error", throwable);

  private static void consumeTopic(RestHighLevelClient restHighLevelClient,
      KafkaConsumer<String, String> consumer, TweetySetting tweetySetting) {
    //new Thread(() -> {
    while (true) {
      try {
        BulkRequest bulkRequest = new BulkRequest();
        AtomicInteger recordCount = new AtomicInteger();
        consumer.poll(Duration.ofMillis(100))
            .onSuccess(records -> records.records()
                .forEach(p -> {
                      String jString = p.value();
                      try {
                        IndexRequest indexRequest = new IndexRequest()
                            //.opType("index")
                            .type(tweetySetting.getIndiceType())
                            .id(getTweetId(p.value())).index(tweetySetting.getIndiceType())
                            .source(Json.decodeValue(jString), XContentType.JSON);
                        bulkRequest.add(indexRequest);
                        recordCount.getAndIncrement();
                      } catch (Exception anyEx) {
                        LOGGER.warn("errror while processing " + jString, anyEx);
                      }

//                      bufferHttpRequest.uri(buildUriWithId(jString, tweetySetting))
//                          .sendJson(Json.decodeValue(jString))
//                          .onSuccess(httpResponseHandler)
//                          .onFailure(throwableHandler);
                    }
                ))
            .onFailure(throwable -> LOGGER.error("Failed processing", throwable));

        if (recordCount.get() > 0) {
          LOGGER.info("receive " + recordCount.get());
          BulkResponse bulkItemResponses = restHighLevelClient
              .bulk(bulkRequest, org.elasticsearch.client.RequestOptions.DEFAULT);
          LOGGER.info("responses" + bulkItemResponses);
          consumer.commit().onSuccess(v -> LOGGER.info("offset is committed"))
              .onFailure(throwableHandler);
        }

      } catch (Exception ioException) {
        LOGGER.error("error", ioException);
      }

    }
    //}).start();

  }

  static KafkaConsumer<String, String> kafkaConsumerBuilder(
      Vertx vertx, TweetySetting tweetySetting) {
    Map<String, String> config = new HashMap<>();
    String bootstrap = String
        .format("%s:%s", tweetySetting.getBrokerHost(), tweetySetting.getBrokerPort());
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, tweetySetting.getConsumerGroup());
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
    config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, String.valueOf(2 * 1024 * 1024));
    return KafkaConsumer.create(vertx, config);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    TweetySetting tweetySetting = config().mapTo(TweetySetting.class);
    LOGGER.info("retrieve settings from yaml " + tweetySetting);
    RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(
        new HttpHost(tweetySetting.getElasticHost(), tweetySetting.getElasticPort(), "http")));

    KafkaConsumer<String, String> consumer = kafkaConsumerBuilder(getVertx(), tweetySetting);

    consumer.subscribe(tweetySetting.getTopicName());
    consumeTopic(restHighLevelClient, consumer, tweetySetting);

  }
}
