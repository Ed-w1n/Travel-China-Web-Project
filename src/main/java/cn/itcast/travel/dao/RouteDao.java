package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Route;

import java.util.List;

public interface RouteDao {
    public int findTotalCount(int cid, String rname);
    public List<Route> findByPage(int cid, int start, int pageSize, String rname);
    public Route findOne(int rid);
    public List<Route> findFavoriteByPage(int uid, int start, int pageSize,String rname);
    public int findTotalFavoriteCount(int uid);

    void updateCount(int parseInt);

    List<Route> favorite();
}
