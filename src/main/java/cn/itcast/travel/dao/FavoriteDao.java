package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Favorite;

public interface FavoriteDao {
    public Favorite findByRidAndUid(int rid, int uid);
    //根据rid查询收藏次数
    int findCoundById(int rid);

    void add(int rid, int uid);
}
