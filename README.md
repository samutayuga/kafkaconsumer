# Kafka Consumer
In a situation where the consumption need to replay for investigation purpose, the offset can be reset.
For example the initial state of partition for a given topic is as below,

`describe a topic`

```shell
kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group tweet_squad --describe
```

```shell
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
tweet_squad     tweety          1          933             933             0               -               -               -
tweet_squad     tweety          0          908             908             0               -               -               -
tweet_squad     tweety          2          920             920             0               -               -               -
```

`LAG` 0 means no more message in the topic is available for the `tweet_squad` consumer group.

To let the same consumer group consuming the messages, reset the offset.

```shell
kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group tweet_squad --reset-offsets --execute --to-earliest --topic tweety
```

If everything is ok, the command will be successfull,

```shell
GROUP                          TOPIC                          PARTITION  NEW-OFFSET     
tweet_squad                    tweety                         0          0              
tweet_squad                    tweety                         1          0              
tweet_squad                    tweety                         2          0   
```

Now run again the `describe` consumer group

```shell
kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --group tweet_squad --describe
```

It should give the result with LAG > 0

```shell
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
tweet_squad     tweety          1          0               933             933             -               -               -
tweet_squad     tweety          0          0               908             908             -               -               -
tweet_squad     tweety          2          0               920             920             -               -               -
                                                                                        
```

and `CURRENT-OFFSET` is 0

Restart the consumer

# Controlling Consumer Liveliness

![Consumer Liveliness](consumer_hearthbeat.png)

* Consumers in a gruop talsk to a Consumer Groups Coordinator
* To detect consumers that are down, there is `heartbeat` mechanism and a poll mechanism
* To avoid issues, consumers are encouraged to process data fast and poll often

# Consumer Heartbeat Thread

* session.timeout.ms (default 10 seconds)
> Hearthbeat are sent periodically to the broker
> If no heartbeat is sent during that period, the consumer is considered dead
> Set even lower to faster consumer rebalances

* heartbeat.interval.ms (default 3 seconds)
> How often to send heartbeats
> Usually set to 1/3rd of session.timeout.ms

* This mechanism is used to detect a consumer application being down

# Consumer Poll Thread
`max.poll.interval.ms` (default 5 minutes)

* Maximum amount of time between two .poll() calls before declaring the consumer dead
* This is particularly relevant for Big Data frameworks like Spark in case processing takes time
* This is used to detect a data processing issue with the consumer

