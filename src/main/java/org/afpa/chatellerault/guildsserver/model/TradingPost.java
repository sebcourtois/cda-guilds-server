package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.afpa.chatellerault.guildsserver.util.PersistedEntity;

import java.util.UUID;

public class TradingPost extends PersistedEntity<TradingPostData, TradingPostRepository> {

    public TradingPost(TradingPostData data, TradingPostRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
