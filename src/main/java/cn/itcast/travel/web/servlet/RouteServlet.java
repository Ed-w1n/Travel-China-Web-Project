package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    RouteService routeService=new RouteServiceImpl();
    private FavoriteService favoriteService=new FavoriteServiceImpl();
    public void PageQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.接受参数
        String currentPageStr=request.getParameter("currentPage");
        String pageSizeStr=request.getParameter("pageSize");
        String cidStr=request.getParameter("cid");

        //接受rname线路名称
        String rname=request.getParameter("rname");
      if(rname!=null&&rname.length()>0&&!"null".equals(rname)){
            rname=new String(rname.getBytes("iso-8859-1"),"utf-8");
        }
        //rname=new String(rname.getBytes("iso-8859-1"),"utf-8");
        //2.处理参数
        int cid=0;
        if (cidStr!=null&&cidStr.length()>0&&!"null".equals(cidStr)){
            cid=Integer.parseInt(cidStr);
        }
        int pageSize=0;//每页显示条数,如果不传递，则认为是第一页
        if (pageSizeStr!=null&&pageSizeStr.length()>0){
            pageSize=Integer.parseInt(pageSizeStr);
        }else {
            pageSize=5;
        }
        int currentPage=0;//当前页面，如果不传递，则默认为第一页
        if (currentPageStr!=null&&currentPageStr.length()>0){
            currentPage=Integer.parseInt(currentPageStr);
        }else{
            currentPage=1;
        }
        //3.调用service查询PageBean对象
        PageBean<Route> pb = routeService.pageQuery(cid, currentPage, pageSize,rname);
        //4.将pageBean对象序列化为Json，返回
        writeValue(pb,response);
    }

    public void favoriteQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.接受参数
        String currentPageStr=request.getParameter("currentPage");
        String pageSizeStr=request.getParameter("pageSize");
        String uidStr=request.getParameter("uid");

        //接受rname线路名称
        String rname=request.getParameter("rname");
        if(rname!=null&&rname.length()>0&&!"null".equals(rname)){
            rname=new String(rname.getBytes("iso-8859-1"),"utf-8");
        }
        //rname=new String(rname.getBytes("iso-8859-1"),"utf-8");

        //2.处理参数
        int uid=0;
        if (uidStr!=null&&uidStr.length()>0&&!"null".equals(uidStr)){
            uid=Integer.parseInt(uidStr);
        }
        int pageSize=0;//每页显示条数,如果不传递，则认为是第一页
        if (pageSizeStr!=null&&pageSizeStr.length()>0){
            pageSize=Integer.parseInt(pageSizeStr);
        }else {
            pageSize=5;
        }
        int currentPage=0;//当前页面，如果不传递，则默认为第一页
        if (currentPageStr!=null&&currentPageStr.length()>0){
            currentPage=Integer.parseInt(currentPageStr);
        }else{
            currentPage=1;
        }
        //3.调用service查询PageBean对象
        PageBean<Route> pb = routeService.favoriteQuery(uid, currentPage, pageSize,rname);
        //4.将pageBean对象序列化为Json，返回
        writeValue(pb,response);
    }

    //根据id寻一个旅游线路的详细信息
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.接受id
        String rid=request.getParameter("rid");
        //2.调用service查询route对象
        Route route=routeService.findOne(rid);
        //3.转为json写回客户端
        writeValue(route,response);
    }
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.获取线路id
        String rid=request.getParameter("rid");
        //2.获取当前登录用户user
        User user= (User) request.getSession().getAttribute("user");
        int uid;
        if (user==null){
            uid=0;
        }else {
            uid=user.getUid();
        }
        //3.调用FavoriteService查询是否收藏
        boolean flag=favoriteService.isFavorite(rid,uid);

        //4.写回客户端
        writeValue(flag,response);
    }
    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //1.获取线路rid
        String rid=request.getParameter("rid");
        //2.获取当前登录用户user
        User user= (User) request.getSession().getAttribute("user");
        int uid;
        if (user==null){
            return;
        }else {
            uid=user.getUid();
        }
        //3.调用service添加
        favoriteService.add(rid,uid);
        routeService.addCount(rid);
    }
    public void favorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        PageBean<Route> pb=routeService.favorite();
        //3.转为json写回客户端
        writeValue(pb,response);
    }
}
