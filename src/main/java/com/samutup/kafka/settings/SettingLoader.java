package com.samutup.kafka.settings;

import com.google.common.collect.Lists;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.util.List;

public class SettingLoader {

    private static final List<String> DEFAULT_CONFIG_LIST = Lists.newArrayList("setting");

    private static final String DEFAULT_CONFIG_PATH = System.getenv("APP_ETC") + File.separator + "kafkatwitter";

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingLoader.class);

    public static ConfigRetrieverOptions getConfigRetrieverOptions() {
        // read configuration in the same directory, or if not found in the classpath
        // if exists, read & override configuration from kubernetes config folder
        LOGGER.info("Retrieving the configuration,location" + DEFAULT_CONFIG_PATH);
        ConfigRetrieverOptions options = new ConfigRetrieverOptions();
        DEFAULT_CONFIG_LIST.forEach(file -> {
            String format = String.format("%s%s%s.yaml", DEFAULT_CONFIG_PATH, File.separator, file);
            options.addStore(new ConfigStoreOptions().setType("file").setFormat("yaml").setOptional(true).setConfig(new JsonObject().put("path", format)));
        });
        return options;
    }
}
