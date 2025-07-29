package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;

public class TradingPosts {
    @Setter
    private static TradingPostRepository repository;

    public static TradingPost create(TradingPostData data) {
        repository.create(data);
        return new TradingPost(data, repository);
    }

    public static int delete(TradingPost tradingPost) {
        return repository.delete(tradingPost.data);
    }

}
