package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import org.afpa.chatellerault.guildsserver.core.TableConfig;
import org.afpa.chatellerault.guildsserver.core.TableConfigField;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TradingPostData {
    UUID id;
    String name;

    @lombok.Builder.Default
    int population = 0;

    UUID hostId;
    UUID mapTileId;

    public static final class TradingPostTable extends TableConfig<TradingPostData> {
        public static final String
                name = "trading_post";
        public static final List<TableConfigField<TradingPostData, ?>>
                fields = List.of(
                TableConfigField.builder(
                        "id", UUID.class,
                        TradingPostData::getId,
                        TradingPostData::setId
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableConfigField.builder(
                        "name", String.class,
                        TradingPostData::getName,
                        TradingPostData::setName
                ).build(),
                TableConfigField.builder(
                        "population", Integer.class,
                        TradingPostData::getPopulation,
                        TradingPostData::setPopulation
                ).build(),
                TableConfigField.builder(
                        "id_host", UUID.class,
                        TradingPostData::getHostId,
                        TradingPostData::setHostId
                ).build(),
                TableConfigField.builder(
                        "location", UUID.class,
                        TradingPostData::getMapTileId,
                        TradingPostData::setMapTileId
                ).build()
        );

        public TradingPostTable() {
            super(name, fields);
        }
    }


}
