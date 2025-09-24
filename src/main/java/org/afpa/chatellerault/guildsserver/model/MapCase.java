package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.MapCaseRepository;
import org.afpa.chatellerault.guildsserver.util.PersistedEntity;

import java.util.UUID;

public class MapCase extends PersistedEntity<MapCaseData, MapCaseRepository> {
    public MapCase(MapCaseData data, MapCaseRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
