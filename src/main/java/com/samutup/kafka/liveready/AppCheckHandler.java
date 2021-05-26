package com.samutup.kafka.liveready;

public interface AppCheckHandler {
    /**
     * For status check on server readiness or liveness if app is ok to service request. Note that the
     * implementation should not block for long period to get the status. The status may be updated
     * asynchronously and the implementation can just retrieve the current status.
     */
    boolean isOk();
}
