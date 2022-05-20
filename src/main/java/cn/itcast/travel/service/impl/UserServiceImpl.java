package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService {
    private UserDao userDao= new UserDaoImpl();
    //注册新用户
    @Override
    public boolean regist(User user) {
        //1.根据用户名查询用户对象
        User u=userDao.findByUsername(user.getUsername());
        //判断U是否为null
        if (u!=null){
            //用户名存在，注册失败
            return false;
        }
        //2.保存用户信息
        //2.1设置用户唯一的字符串
        user.setCode(UuidUtil.getUuid());
        //2.2设置激活状态
        user.setStatus("Y");
        userDao.save(user);
        return true;
    }

    @Override
    public boolean active(String code) {
        //1.根据激活码查询用户对象
        User user=userDao.findByCode(code);
        if (user!=null){
            //2.调用dao的修改激活状态的方法
            userDao.updataStatus(user);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }

    @Override
    public boolean edit(User user) {
        //1.根据用户名查询用户对象
        User u=userDao.findByCode(user.getCode());
        //判断U是否为null
        if (u==null){
            return false;
        }
        return userDao.edit(user);
    }
}
