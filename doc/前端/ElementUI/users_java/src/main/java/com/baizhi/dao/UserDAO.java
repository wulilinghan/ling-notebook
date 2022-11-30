package com.baizhi.dao;

import com.baizhi.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDAO {


    //查询所有用户信息
    List<User> findAll();

    //保存用户信息
    void save(User user);

    //根据id删除一个用户
    void delete(String id);


    //修改用户信息
    void update(User user);


    //分页查询
    List<User> findByPage(@Param("start") Integer start,@Param("rows") Integer rows );

    //查询总条数
    Long findTotals();

}
