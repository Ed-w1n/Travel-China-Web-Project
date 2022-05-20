package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
        UserService service=new UserServiceImpl();
    public void activeUserServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取激活码
        String code=request.getParameter("code");
        if (code!=null){
            //UserService service=new UserServiceImpl();
            boolean flag=service.active(code);
            //3.判断标记
            String msg= null;
            if (flag){
                //激活成功
                msg="Active successfully，please <a href='login.html'>Login</a>";
            }else{
                //激活失败
                msg="Active failly, please contact the IT supporter";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }

    public void exitServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.销毁session
        request.getSession().invalidate();
        //2.跳转登录页面
        response.sendRedirect(request.getContextPath()+"/login.html");
    }

    public void findUserServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.从session中获取登录用户
        Object user=request.getSession().getAttribute("user");
        /*if(user == null){
            User user0 = (User) user;
            user0.setUid(-1);
            writeValue(user0,response);
        }*/
        writeValue(user,response);
    }

    public void loginServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//1.获取用户名和密码数据
        Map<String,String[]> map=request.getParameterMap();
        //2.封装User对象
        User user=new User();
        try{
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用Service查询
        //UserService service=new UserServiceImpl();
        User u=service.login(user);
        ResultInfo info=new ResultInfo();
        //4.判断对象是否为null
        if(u==null){
            //用户名密码错误
            info.setFlag(false);
            info.setErrorMsg("Username or Password is wrong");
        }
        //5.判断用户是否激活
        if (u!=null&&"N".equals(u.getStatus())){
            //用户尚未激活
            info.setFlag(false);
            info.setErrorMsg("Your account is not active, please active it first");
        }
        //6.判断登录成功
        if (u!=null&&"Y".equals(u.getStatus())){
            request.getSession().setAttribute("user",u);//登录成功标记
            //登录成功
            info.setFlag(true);
        }
        //响应数据
        ObjectMapper mapper=new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),info);
    }

    public void registUserServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//获取验证码
        String code=request.getParameter("check");
        //从session中获取验证码
        HttpSession session=request.getSession();
        String checkcode_server= (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");
        //比较
        if (checkcode_server==null||!checkcode_server.equalsIgnoreCase(code)){
            ResultInfo info=new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("Wrong verficated code");
            //将info对象序列化为json
            ObjectMapper mapper=new ObjectMapper();
            String json=mapper.writeValueAsString(info);
            //将json数据协会客户端
            //设置content.type
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            return;
        }
        //1.获取数据
        Map<String,String[]> map=request.getParameterMap();
        //2.封装对象
        User user=new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service完成注册
        //UserService service=new UserServiceImpl();
        boolean flag=service.regist(user);
        ResultInfo info=new ResultInfo();
        //4.响应结果
        if (flag){
            //注册成功
            info.setFlag(true);
        }else{
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("Username is already existed");
        }
        //将info对象序列化为json
        ObjectMapper mapper=new ObjectMapper();
        String json=mapper.writeValueAsString(info);
        //将json数据协会客户端
        //设置content.type
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }

    public void editUserServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取验证码
        String code=request.getParameter("check");
        //从session中获取验证码
        HttpSession session=request.getSession();
        String checkcode_server= (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");
        //比较
        if (checkcode_server==null||!checkcode_server.equalsIgnoreCase(code)){
            ResultInfo info=new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("Wrong verficated code");
            //将info对象序列化为json
            ObjectMapper mapper=new ObjectMapper();
            String json=mapper.writeValueAsString(info);
            //将json数据协会客户端
            //设置content.type
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            return;
        }
        //1.获取数据
        Map<String,String[]> map=request.getParameterMap();
        //2.封装对象
        User user=new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service完成注册
        //UserService service=new UserServiceImpl();
        boolean flag=service.edit(user);
        ResultInfo info=new ResultInfo();
        //4.响应结果
        if (flag){
            //注册成功
            info.setFlag(true);
        }else{
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("Edit failly");
        }
        //将info对象序列化为json
        ObjectMapper mapper=new ObjectMapper();
        String json=mapper.writeValueAsString(info);
        //将json数据协会客户端
        //设置content.type
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }
}
