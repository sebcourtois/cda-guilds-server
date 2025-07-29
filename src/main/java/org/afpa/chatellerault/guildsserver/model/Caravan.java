package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;

public class Caravan extends BaseEntity<CaravanData, CaravanRepository> {
    public Caravan(CaravanData data, CaravanRepository repository) {
        super(data, repository);
    }
}
