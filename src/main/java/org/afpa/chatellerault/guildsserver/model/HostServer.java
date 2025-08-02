package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.HostServerRepository;
import org.afpa.chatellerault.guildsserver.util.PersistedEntity;

import java.util.UUID;

public class HostServer extends PersistedEntity<HostServerData, HostServerRepository> {
    public HostServer(HostServerData data, HostServerRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
