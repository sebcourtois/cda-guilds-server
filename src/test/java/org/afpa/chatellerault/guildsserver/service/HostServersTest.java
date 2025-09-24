package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.model.HostServer;
import org.afpa.chatellerault.guildsserver.model.HostServerData;
import org.afpa.chatellerault.guildsserver.repository.HostServerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.net.InetAddress;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class HostServersTest {
    private final JdbcClient jdbcClient;

    @Autowired
    public HostServersTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, HostServerData.builder().build().tableName());
        HostServers.setRepository(new HostServerRepository(this.jdbcClient));
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, HostServerData.builder().build().tableName());
    }

    @Test
    void create() throws Exception {
        var data = HostServerData.builder()
                .name("sebserver")
                .ipAddress(InetAddress.getLocalHost())
                .port(55555)
                .build();
        HostServer myHostServer = HostServers.create(data);
        assertThat(myHostServer.getId()).isNotNull();
    }
}