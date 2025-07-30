package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.HostServerRepository;

import java.util.UUID;

public class HostServer extends BaseEntity<HostServerData, HostServerRepository> {
    public HostServer(HostServerData data, HostServerRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
