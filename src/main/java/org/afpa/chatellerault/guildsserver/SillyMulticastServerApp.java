package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;

@Profile("!test")
//@org.springframework.stereotype.Component
public class SillyMulticastServerApp implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(SillyMulticastServerApp.class);

    @Override
    public void run(ApplicationArguments args) {
        Thread.ofPlatform().start(new SillyMulticastServer());
    }
}
