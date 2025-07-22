package org.afpa.chatellerault.guildsserver.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@lombok.Data
@lombok.Builder
public class TradingPost {
    UUID id;
    String name;

    @lombok.Builder.Default
    int posX = 0;
    @lombok.Builder.Default
    int posY = 0;
    @lombok.Builder.Default
    int population = 0;
    @lombok.Builder.Default
    UUID hostId = null;

    static public TradingPost fromTableRow(ResultSet row) throws SQLException {
        return TradingPost.builder()
                .id(row.getObject("id", UUID.class))
                .name(row.getString("name"))
                .posX(row.getInt("location_x"))
                .posY(row.getInt("location_y"))
                .hostId(row.getObject("id_host", UUID.class))
                .build();
    }
}
