package com.samutup.kafka.consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.samutup.kafka.settings.TweetySetting;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class TweetConsumerVerticle extends AbstractVerticle {


  private static String getTweetId(String jsonStr) {
    JsonElement twJsonObj = JsonParser.parseString(jsonStr);
    if (twJsonObj.isJsonObject()) {
      if (twJsonObj.getAsJsonObject().has("id")) {
        return twJsonObj.getAsJsonObject().get("id").getAsString();
      }
    } else {
      LOGGER.warn(jsonStr + " Is not a json string" + twJsonObj);
    }

    return null;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(TweetConsumerVerticle.class);

  private static void consumeTopic(RestHighLevelClient restHighLevelClient,
      KafkaConsumer<String, String> consumer, TweetySetting tweetySetting) {

    while (true) {
      try {
        BulkRequest bulkRequest = new BulkRequest();
        consumer.poll(Duration.ofMillis(100)).forEach(p -> {
              String jString = p.value();
              try {
                bulkRequest.add(new IndexRequest()
                    .id(getTweetId(p.value()))
                    .index(tweetySetting.getIndice())
                    .source(jString, XContentType.JSON));
              } catch (Exception anyEx) {
                LOGGER.warn("error while processing " + jString, anyEx);
              }
            }
        );
        if (bulkRequest.numberOfActions() > 0) {
          BulkResponse bulkItemResponses = restHighLevelClient
              .bulk(bulkRequest, org.elasticsearch.client.RequestOptions.DEFAULT);
          consumer.commitSync();
          Thread.sleep(1000l);
          LOGGER.info(
              "Successfully processed tweets with ID:" + Arrays.stream(bulkItemResponses.getItems())
                  .map(
                      BulkItemResponse::getId)
                  .collect(Collectors.joining(",")));
        }

      } catch (Exception ioException) {
        LOGGER.error("error", ioException);
      }

    }
  }

  static KafkaConsumer<String, String> kafkaConsumerBuilder(TweetySetting tweetySetting) {
    Properties config = new Properties();
    String bootstrap = String
        .format("%s:%s", tweetySetting.getBrokerHost(), tweetySetting.getBrokerPort());
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, tweetySetting.getConsumerGroup());
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
    config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, String.valueOf(2 * 1024 * 1024));
    return new KafkaConsumer<String, String>(config);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    TweetySetting tweetySetting = config().mapTo(TweetySetting.class);
    LOGGER.info("retrieve settings from yaml " + tweetySetting);
    String uri = String.format("%s%s", tweetySetting.getIndice(),
        tweetySetting.getIndiceType());
    RestClientBuilder clientBuilder = RestClient.builder(
        new HttpHost(tweetySetting.getElasticHost(), tweetySetting.getElasticPort(), "http"));
    RestHighLevelClient restHighLevelClient = new RestHighLevelClient(clientBuilder);

    KafkaConsumer<String, String> consumer = kafkaConsumerBuilder(tweetySetting);

    consumer.subscribe(Collections.singleton(tweetySetting.getTopicName()));
    consumeTopic(restHighLevelClient, consumer, tweetySetting);

  }
}
