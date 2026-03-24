package com.zhitao.train.common.log.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

    private static final long START_TIME = System.currentTimeMillis();
    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    private final Environment env;

    public StartupLogger(Environment env) {
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logStartup(ApplicationReadyEvent event) {
        try {
            long startupTime = System.currentTimeMillis() - START_TIME;

            String appName =
                    env.getProperty("spring.application.name", "UnknownApp");

            String moduleName =
                    env.getProperty("module.name", "DefaultModule");

            String profile =
                    env.getProperty("spring.profiles.active", "default");

            String port =
                    env.getProperty("server.port", "default");


            log.info(
                    "APPLICATION_STARTUP module={} app={} profile={} port={} startupTime={}ms",
                    moduleName,
                    appName,
                    profile,
                    port,
                    startupTime
            );

        } catch (Exception e) {

            log.error("Startup log error", e);

        }
    }
}