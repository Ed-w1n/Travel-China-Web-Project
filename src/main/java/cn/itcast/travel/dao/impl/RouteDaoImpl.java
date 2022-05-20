package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.util.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class RouteDaoImpl implements RouteDao {
    private JdbcTemplate template=new JdbcTemplate(JDBCUtils.getDataSource());
    @Override
    public int findTotalCount(int cid,String rname) {
        //String sql="select count(*) from tab_route where cid= ?";
        //1.定义sql模板
        String sql="select count(*) from tab_route where 1=1 ";
        StringBuilder sb=new StringBuilder(sql);
        List params=new ArrayList();
        //2.判断参数是否有值
        if (cid!=0){
            sb.append(" and cid = ?");
            params.add(cid);
        }
        if (rname!=null&&rname.length()>0&&!"null".equals(rname)){
            sb.append(" and rname like ?");
            params.add("%"+rname+"%");
        }
        sql=sb.toString();
        return template.queryForObject(sql,Integer.class,params.toArray());
    }

    @Override
    public int findTotalFavoriteCount(int uid) {
        //String sql="select count(*) from tab_route where cid= ?";
        //1.定义sql模板
        String sql="SELECT COUNT(*) FROM tab_route r, tab_favorite f WHERE r.rid = f.rid AND f.uid = ";
        StringBuilder sb=new StringBuilder(sql);
        sb.append(uid);
        sql=sb.toString();
        return template.queryForObject(sql,Integer.class);
    }

//    SELECT * FROM tab_route r, tab_favorite f WHERE r.`rid` = f.`rid` AND f.uid = 1;

    @Override
    public List<Route> findByPage(int cid, int start, int pageSize,String rname) {
        String sql="select * from tab_route where 1=1 ";
        StringBuilder sb=new StringBuilder(sql);
        List params=new ArrayList();
        //2.判断参数是否有值
        if (cid!=0){
            sb.append(" and cid = ?");
            params.add(cid);
        }
        if (rname!=null&&rname.length()>0&&!"null".equals(rname)){
            sb.append(" and rname like ?");
            params.add("%"+rname+"%");
        }
        sb.append(" limit ? , ?");
        sql=sb.toString();
        params.add(start);
        params.add(pageSize);
        List<Route> route = template.query(sql, new BeanPropertyRowMapper<Route>(Route.class), params.toArray());
        return route;
    }

    @Override
    public Route findOne(int rid) {
        String sql="select * from tab_route where rid = ?";
        return template.queryForObject(sql, new BeanPropertyRowMapper<Route>(Route.class),rid);
    }

    @Override
    public List<Route> findFavoriteByPage(int uid, int start, int pageSize, String rname) {
        String sql="SELECT * FROM tab_route r, tab_favorite f WHERE r.rid = f.rid AND f.uid = ";
        StringBuilder sb=new StringBuilder(sql);
        sb.append(uid);

        List params=new ArrayList();

        sb.append(" limit ? , ?");
        sql=sb.toString();
        params.add(start);
        params.add(pageSize);
        List<Route> route = template.query(sql, new BeanPropertyRowMapper<Route>(Route.class), params.toArray());
        return route;
    }

    @Override
    public void updateCount(int rid) {
        String sql1="select count from tab_route where rid=?";
        int num=template.queryForInt(sql1,rid)+1;
        String sql="update tab_route set count=? where rid=?";
        template.update(sql,num,rid);
    }

    @Override
    public List<Route> favorite() {
        String sql="select * from tab_route order by count DESC limit 0,8";
        List<Route> route = template.query(sql, new BeanPropertyRowMapper<Route>(Route.class));
        return route;
    }
}
