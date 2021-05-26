package com.samutup.kafka.liveready;

import io.vertx.ext.web.Router;

public class LifenessReadinessCheck {
    private LifenessReadinessCheck() {
    }

    public static void registerReadinessCheck(Router router, AppCheckHandler[] appCheckHandlers) {
        //readiness checks
        registerCheck("/readiness", router, appCheckHandlers);
    }

    public static void registerLivenessCheck(Router router, AppCheckHandler[] appCheckHandlers) {
        //liveness checks
        registerCheck("/liveness", router, appCheckHandlers);
    }

    private static void registerCheck(String uriPath, Router router, AppCheckHandler[] appCheckHandlers) {
        router.get(uriPath).handler(rc -> {
            int statusCode = 200;

            if (appCheckHandlers != null) {
                for (AppCheckHandler appCheckHandler : appCheckHandlers) {
                    if (!appCheckHandler.isOk()) {
                        statusCode = 500;
                        break;
                    }
                }
            }

            rc.response().setStatusCode(statusCode).end();
        });
    }
}
