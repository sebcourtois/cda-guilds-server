package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@Service
public class Caravans {
    @Setter
    private static CaravanRepository repository;

    public static Caravan create(CaravanData data) throws SQLException {
        repository.create(data);
        return new Caravan(data, repository);
    }

    public static int delete(Caravan caravan) {
        return repository.delete(caravan.getData());
    }

    public static Caravan getByName(String someName) throws NoSuchElementException {
        var caravanData = repository.findByName(someName);

        if (caravanData.isEmpty()) throw new NoSuchElementException(
                "No such %s named: '%s'".formatted(Caravan.class.getName(), someName)
        );

        return new Caravan(caravanData.get(), repository);
    }

}
