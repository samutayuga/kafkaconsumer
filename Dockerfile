# Build docker image using the secured image from dockerhub.gemalto.com
FROM adoptopenjdk/openjdk11

RUN mkdir -p /app/lib
RUN mkdir -p /app/bin
RUN mkdir -p /app/etc
RUN mkdir -p /app/etc/kafkatwitter


ENV APP_LIB="/app/lib"
ENV APP_BIN="/app/bin"
ENV APP_ETC="/app/etc"
RUN export PATH=$APP_BIN:$PATH
# Copy libraries...
#COPY ./context/jars $APP_LIB/atmpoc

# Copy application...
ADD target/kafkaconsumer-fat.jar $APP_LIB

# Copy script for running application...
ADD target/classes/start_kafkatwitter.sh /

# Copy config
ADD target/classes/setting.yaml $APP_ETC/kafkatwitter

# Add permissions...
RUN chmod -R 777 /start_kafkatwitter.sh
RUN ls $APP_BIN
# Define entry point...
ENTRYPOINT ["/start_kafkatwitter.sh"]
CMD [""]

