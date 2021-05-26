package com.samutup.kafka.liveready;

import com.samutup.kafka.settings.TweetySetting;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class ServerStartupListenerImpl implements ServerStartupListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStartupListenerImpl.class);

    private Promise<Void> startFuture;

    private Integer portNumber;

    private boolean isReady = false;

    private final TweetySetting tweetySetting;

    public ServerStartupListenerImpl(Promise<Void> startFuture, Integer portNumber, TweetySetting tweetySetting) {
        this.startFuture = startFuture;
        this.portNumber = portNumber;
        this.tweetySetting = tweetySetting;
    }

    /**
     * For status check on server readiness or liveness if app is ok to service request. Note that the
     * implementation should not block for long period to get the status. The status may be updated
     * asynchronously and the implementation can just retrieve the current status.
     */
    @Override
    public boolean isOk() {
        return isReady;
    }

    /**
     * Something has happened, so handle it.
     *
     * @param event the event to handle
     */
    @Override
    public void handle(AsyncResult<HttpServer> event) {
        isReady = event.succeeded();
        if (isReady) {
            LOGGER.info("http server running at port " + portNumber);
            LOGGER.info("Settings " + tweetySetting);
            startFuture.complete();
        } else {
            LOGGER.error("Could not start http server");
            startFuture.fail(event.cause());
        }
        //clear startFuture, portNumber since there are no longer needed
        startFuture = null;
        portNumber = null;
    }
}
