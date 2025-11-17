package org.afpa.chatellerault.guildsserver.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectSerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@JsonComponent
public class TradingPosts {
    @Setter
    private static TradingPostRepository repository;

    public static TradingPost create(TradingPostData data) throws SQLException {
        repository.create(data);
        return new TradingPost(data, repository);
    }

    public static int delete(TradingPost tradingPost) throws SQLException {
        return repository.delete(tradingPost.getData());
    }

    public static Optional<TradingPost> findById(UUID someId) {
        Optional<TradingPostData> tradingPostData = repository.findById(someId);
        return tradingPostData.map((TradingPostData data) -> new TradingPost(data, repository));
    }

    public static Stream<TradingPost> findAll() {
        return repository.findAll().map(data -> new TradingPost(data, repository));
    }

    public static class Serializer extends JsonObjectSerializer<TradingPost> {
        @Override
        protected void serializeObject(
                TradingPost entity, JsonGenerator jgen, SerializerProvider provider
        ) throws IOException {
            TradingPostData data = entity.getData();
            jgen.writeStringField("entity_type", "trading_post");
            jgen.writeStringField("id", Objects.toString(data.getId(), null));
            jgen.writeStringField("name", data.getName());
            jgen.writeNumberField("population", data.getPopulation());
            jgen.writeStringField("host_id", Objects.toString(data.getHostId(), null));
            jgen.writeStringField("map_tile_id", Objects.toString(data.getMapTileId(), null));
        }
    }
}
