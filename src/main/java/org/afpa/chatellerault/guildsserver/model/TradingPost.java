package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.afpa.chatellerault.guildsserver.core.BaseEntity;

import java.util.UUID;

public class TradingPost extends BaseEntity<TradingPostData> {

    public TradingPost(TradingPostData data, TradingPostRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
