package org.afpa.chatellerault.guildsserver.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectSerializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@JsonComponent
public class Caravans {
    @Setter
    private static CaravanRepository repository;

    public static Caravan create(CaravanData data) throws SQLException {
        repository.create(data);
        return new Caravan(data, repository);
    }

    public static int delete(Caravan caravan) throws SQLException {
        return repository.delete(caravan.getData());
    }

    public static Caravan getByName(String someName) throws NoSuchElementException {
        Optional<CaravanData> caravanData = repository.findByName(someName);
        if (caravanData.isEmpty()) throw new NoSuchElementException(
                "No such %s named: '%s'".formatted(Caravan.class.getName(), someName)
        );
        return new Caravan(caravanData.get(), repository);
    }

    public static class Serializer extends JsonObjectSerializer<Caravan> {
        @Override
        protected void serializeObject(
                Caravan entity, JsonGenerator jgen, SerializerProvider provider
        ) throws IOException {
            CaravanData data = entity.getData();
            jgen.writeStringField("entity_type", "caravan");
            jgen.writeStringField("id", Objects.toString(data.getId(), null));
            jgen.writeStringField("name", data.getName());
            jgen.writeStringField("destination_id", Objects.toString(data.getDestinationId(), null));
            jgen.writeStringField("map_tile_id", Objects.toString(data.getMapTileId(), null));
        }
    }
}
