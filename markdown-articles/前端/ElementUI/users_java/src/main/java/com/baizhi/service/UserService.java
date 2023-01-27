package com.baizhi.service;

import com.baizhi.entity.User;

import java.util.List;

public interface UserService {

    //查询所有方法
    List<User> findAll();

    //保存用户信息
    void save(User user);

    //根据id删除用户信息
    void delete(String id);

    //更新用户信息
    void update(User user);

    //分页查询
    List<User> findByPage(Integer pageNow,Integer rows );

    //查询总条数
    Long findTotals();
}
