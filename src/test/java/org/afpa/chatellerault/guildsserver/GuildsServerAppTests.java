package org.afpa.chatellerault.guildsserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
class GuildsServerAppTests {
    private static final Logger LOG = LogManager.getLogger(GuildsServerAppTests.class);

    private final JdbcClient jdbcClient;

    @Autowired
    public GuildsServerAppTests(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

}
