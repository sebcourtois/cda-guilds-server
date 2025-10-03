package org.afpa.chatellerault.guildsserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

class AzgaarWorldsTest {

    @Test
    void loadFromJson() throws Exception {
        InputStream jsonStream = new ClassPathResource("azgaar_world.json").getInputStream();
        var azWorld = AzgaarWorlds.loadFromJson(jsonStream);
    }
}