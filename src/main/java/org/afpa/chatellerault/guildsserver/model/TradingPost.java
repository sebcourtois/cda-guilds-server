package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;

public class TradingPost extends BaseEntity<TradingPostData, TradingPostRepository> {

    public TradingPost(TradingPostData data, TradingPostRepository repository) {
        super(data, repository);
    }
}
