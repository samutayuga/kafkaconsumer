package com.samutup.kafka.consumer;

import com.google.gson.JsonParser;
import com.samutup.kafka.settings.TweetySetting;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

public class PopularTweetVerticle extends AbstractVerticle {

  private static Properties buildStreamProperties(TweetySetting tweetySetting) {
    Properties config = new Properties();
    String bootstrap = String
        .format("%s:%s", tweetySetting.getBrokerHost(), tweetySetting.getBrokerPort());
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "samutup-popular-01");

    return config;
  }

  private static int getFollowerCounts(String jsonString) {
    try {
      return JsonParser.parseString(jsonString).getAsJsonObject().get("user").getAsJsonObject()
          .get("followers_count").getAsInt();
    } catch (NullPointerException nullPointerException) {
      return 0;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(PopularTweetVerticle.class);

  private static void processStream(TweetySetting tweetySetting) {
    StreamsBuilder streamsBuilder = new StreamsBuilder();
    KStream<String, String> stringKStream = streamsBuilder.stream(tweetySetting.getTopicName());
    KStream<String, String> filteredStream = stringKStream.filter(
        (k, jTweet) -> getFollowerCounts(jTweet) > 10000
    );
    filteredStream.to(tweetySetting.getTopicTarget());
    KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(),
        buildStreamProperties(tweetySetting));
    kafkaStreams.start();

  }

  @Override
  public void start() throws Exception {
    TweetySetting tweetySetting = config().mapTo(TweetySetting.class);
    processStream(tweetySetting);
  }
}
