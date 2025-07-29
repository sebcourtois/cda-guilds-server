package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;

public class Caravans {
    @Setter
    private static CaravanRepository repository;

    public static Caravan create(CaravanData data) {
        repository.create(data);
        return new Caravan(data, repository);
    }

    public static int delete(Caravan caravan) {
        return repository.delete(caravan.data);
    }
}
