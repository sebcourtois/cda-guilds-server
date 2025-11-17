package org.afpa.chatellerault.guildsserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class TradingPostsTest {
    private static final Logger log = LogManager.getLogger(TradingPostsTest.class);

    private final JdbcClient jdbcClient;
    private final ObjectMapper jackObjMapper;

    @Autowired
    public TradingPostsTest(JdbcClient jdbcClient, ObjectMapper jackObjMapper) {
        this.jdbcClient = jdbcClient;
        this.jackObjMapper = jackObjMapper;
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, TradingPostData.TradingPostTable.name);
    }

    @Test
    void serializeEmptyOne() throws Exception {
        String tpName = "Tour de France";
        int population = 500;
        TradingPostData data = TradingPostData.builder()
                .name(tpName)
                .population(population)
                .build();
        TradingPost tradingPost = TradingPosts.create(data);
        String json = this.jackObjMapper.writeValueAsString(tradingPost);
        log.info(json);
        // order is important here
        // map entries order must match properties declaration order in TradingPostData
        var expectedData = new LinkedHashMap<String, Serializable>();
        expectedData.put("entity_type", "trading_post");
        expectedData.put("id", tradingPost.getId().toString());
        expectedData.put("name", tpName);
        expectedData.put("population", population);
        expectedData.put("host_id", null);
        expectedData.put("map_tile_id", null);

        assertThat(json).isEqualTo(
                this.jackObjMapper.writeValueAsString(expectedData)
        );
    }
}