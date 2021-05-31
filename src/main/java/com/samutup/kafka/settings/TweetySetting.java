package com.samutup.kafka.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TweetySetting {

  private int port;
  private String topicName;
  private String topicTarget;
  private String brokerHost;
  private String brokerPort;
  private String consumerGroup;
  private String elasticHost;
  private int elasticPort;
  private String indice;
  private String indiceType;

  public String getElasticHost() {
    return elasticHost;
  }

  public void setElasticHost(String elasticHost) {
    this.elasticHost = elasticHost;
  }

  public int getElasticPort() {
    return elasticPort;
  }

  public void setElasticPort(int elasticPort) {
    this.elasticPort = elasticPort;
  }

  public String getIndice() {
    return indice;
  }

  public void setIndice(String indice) {
    this.indice = indice;
  }

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public String getBrokerHost() {
    return brokerHost;
  }

  public void setBrokerHost(String brokerHost) {
    this.brokerHost = brokerHost;
  }

  public String getBrokerPort() {
    return brokerPort;
  }

  public void setBrokerPort(String brokerPort) {
    this.brokerPort = brokerPort;
  }

  public String getConsumerGroup() {
    return consumerGroup;
  }

  public void setConsumerGroup(String consumerGroup) {
    this.consumerGroup = consumerGroup;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getIndiceType() {
    return indiceType;
  }

  public void setIndiceType(String indiceType) {
    this.indiceType = indiceType;
  }

  public String getTopicTarget() {
    return topicTarget;
  }

  public void setTopicTarget(String topicTarget) {
    this.topicTarget = topicTarget;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TweetySetting that = (TweetySetting) o;
    return port == that.port && elasticPort == that.elasticPort && Objects
        .equal(topicName, that.topicName) && Objects
        .equal(topicTarget, that.topicTarget) && Objects
        .equal(brokerHost, that.brokerHost) && Objects
        .equal(brokerPort, that.brokerPort) && Objects
        .equal(consumerGroup, that.consumerGroup) && Objects
        .equal(elasticHost, that.elasticHost) && Objects
        .equal(indice, that.indice) && Objects
        .equal(indiceType, that.indiceType);
  }

  @Override
  public int hashCode() {
    return Objects
        .hashCode(port, topicName, topicTarget, brokerHost, brokerPort, consumerGroup, elasticHost,
            elasticPort, indice, indiceType);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("port", port)
        .add("topicName", topicName)
        .add("topicTarget", topicTarget)
        .add("brokerHost", brokerHost)
        .add("brokerPort", brokerPort)
        .add("consumerGroup", consumerGroup)
        .add("elasticHost", elasticHost)
        .add("elasticPort", elasticPort)
        .add("indice", indice)
        .add("indiceType", indiceType)
        .toString();
  }
}
