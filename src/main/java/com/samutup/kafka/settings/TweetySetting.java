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
  private String brokerHost;
  private String brokerPort;
  private int requestPerConn = 5;
  private CompressionType compressionType = CompressionType.none;
  private int lingerMs;
  private int batchSizeKb = 16;
  private int retryBackoffMs = 100;
  private int deliveryTimeOutMs = 120000;
  private String restProduce;
  private String elasticHost;
  private int elasticPort;
  private String indice;

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

  public String getRestProduce() {
    return restProduce;
  }

  public void setRestProduce(String restProduce) {
    this.restProduce = restProduce;
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


  public int getRetryBackoffMs() {
    return retryBackoffMs;
  }

  public void setRetryBackoffMs(int retryBackoffMs) {
    this.retryBackoffMs = retryBackoffMs;
  }

  public int getDeliveryTimeOutMs() {
    return deliveryTimeOutMs;
  }

  public void setDeliveryTimeOutMs(int deliveryTimeOutMs) {
    this.deliveryTimeOutMs = deliveryTimeOutMs;
  }

  public int getRequestPerConn() {
    return requestPerConn;
  }

  public void setRequestPerConn(int requestPerConn) {
    this.requestPerConn = requestPerConn;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }

  public void setCompressionType(CompressionType compressionType) {
    this.compressionType = compressionType;
  }

  public int getLingerMs() {
    return lingerMs;
  }

  public void setLingerMs(int lingerMs) {
    this.lingerMs = lingerMs;
  }

  public int getBatchSizeKb() {
    return batchSizeKb;
  }

  public void setBatchSizeKb(int batchSizeKb) {
    this.batchSizeKb = batchSizeKb;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
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
    return port == that.port && requestPerConn == that.requestPerConn && lingerMs == that.lingerMs
        && batchSizeKb == that.batchSizeKb && retryBackoffMs == that.retryBackoffMs
        && deliveryTimeOutMs == that.deliveryTimeOutMs && elasticPort == that.elasticPort
        && Objects.equal(topicName, that.topicName)
        && Objects.equal(brokerHost, that.brokerHost)
        && Objects.equal(brokerPort, that.brokerPort)
        && compressionType == that.compressionType && Objects
        .equal(restProduce, that.restProduce) && Objects
        .equal(elasticHost, that.elasticHost) && Objects
        .equal(indice, that.indice);
  }

  @Override
  public int hashCode() {
    return Objects
        .hashCode(port, topicName, brokerHost, brokerPort, requestPerConn, compressionType,
            lingerMs,
            batchSizeKb, retryBackoffMs, deliveryTimeOutMs, restProduce, elasticHost, elasticPort,
            indice);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("port", port)
        .add("topicName", topicName)
        .add("brokerHost", brokerHost)
        .add("brokerPort", brokerPort)
        .add("requestPerConn", requestPerConn)
        .add("compressionType", compressionType)
        .add("lingerMs", lingerMs)
        .add("batchSizeKb", batchSizeKb)
        .add("retryBackoffMs", retryBackoffMs)
        .add("deliveryTimeOutMs", deliveryTimeOutMs)
        .add("restProduce", restProduce)
        .add("elasticHost", elasticHost)
        .add("elasticPort", elasticPort)
        .add("indice", indice)
        .toString();
  }
}
