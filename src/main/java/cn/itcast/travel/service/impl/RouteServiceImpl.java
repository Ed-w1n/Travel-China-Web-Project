package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.FavoriteDao;
import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.RouteImgDao;
import cn.itcast.travel.dao.SellerDao;
import cn.itcast.travel.dao.impl.FavoriteImpl;
import cn.itcast.travel.dao.impl.RouteDaoImpl;
import cn.itcast.travel.dao.impl.RouteImgImpl;
import cn.itcast.travel.dao.impl.SellerImpl;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.RouteImg;
import cn.itcast.travel.domain.Seller;
import cn.itcast.travel.service.RouteService;

import java.util.List;

public class RouteServiceImpl implements RouteService {
    private RouteDao routeDao=new RouteDaoImpl();
    private RouteImgDao routeImgDao=new RouteImgImpl();
    private SellerDao sellerDao=new SellerImpl();
    private FavoriteDao favoriteDao=new FavoriteImpl();
    @Override
    public PageBean<Route> pageQuery(int cid, int currentPage, int pageSize, String rname) {
        //封装PageBean
        PageBean<Route> pb=new PageBean<Route>();
        //设置当前页码
        pb.setCurrentPage(currentPage);
        //设置每页显示条数
        pb.setPageSize(pageSize);
        //设置总记录数
        int totalCount=routeDao.findTotalCount(cid,rname);
        pb.setTotalCount(totalCount);
        //设置当前页的数据集合
        int start=(currentPage-1)*pageSize;
        List<Route> list=routeDao.findByPage(cid, start, pageSize,rname);
        pb.setList(list);
        //设置总页数
        int totalPage=totalCount%pageSize==0?totalCount/pageSize:(totalCount/pageSize)+1;
        pb.setTotalPage(totalPage);
        return pb;
    }

    @Override
    public Route findOne(String rid) {
        //1.根据id去route中查询route对象
        Route route = routeDao.findOne(Integer.parseInt(rid));
        //2.根据route的id查询图片集合信息
        List<RouteImg> routeImgList = routeImgDao.findByRid(Integer.parseInt(rid));
        //2.2将集合设置到route对象
        route.setRouteImgList(routeImgList);
        //3.根据route的sid(商家id)查询商家对象
        Seller seller = sellerDao.findById(route.getSid());
        route.setSeller(seller);
        //4.查询收藏次数
        int count=favoriteDao.findCoundById(route.getRid());
        route.setCount(count);
        return route;
    }

    @Override
    public void addCount(String rid) {
        routeDao.updateCount(Integer.parseInt(rid));
    }

    @Override
    public PageBean<Route> favorite() {
        //封装PageBean
        PageBean<Route> pb=new PageBean<Route>();
        List<Route> list=routeDao.favorite();
        pb.setList(list);
        return pb;
    }

    @Override
    public PageBean<Route> favoriteQuery(int uid, int currentPage, int pageSize, String rname) {
        //封装PageBean
        PageBean<Route> pb=new PageBean<Route>();
        //设置当前页码
        pb.setCurrentPage(currentPage);
        //设置每页显示条数
        pb.setPageSize(pageSize);
        //设置总记录数
        int totalCount=routeDao.findTotalFavoriteCount(uid);
        pb.setTotalCount(totalCount);
        //设置当前页的数据集合
        int start=(currentPage-1)*pageSize;
        List<Route> list=routeDao.findFavoriteByPage(uid, start, pageSize,rname);
        pb.setList(list);
        //设置总页数
        int totalPage=totalCount%pageSize==0?totalCount/pageSize:(totalCount/pageSize)+1;
        pb.setTotalPage(totalPage);
        return pb;
    }
}
