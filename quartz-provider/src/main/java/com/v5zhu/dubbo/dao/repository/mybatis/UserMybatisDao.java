package com.v5zhu.dubbo.dao.repository.mybatis;


import com.v5zhu.dubbo.commons.repository.MyBatisRepository;
import com.v5zhu.dubbo.po.entity.User;

/**
 * Created by zhuxl on 2015/5/20.
 */

@MyBatisRepository
public interface UserMybatisDao {
    User findByLoginName(String loginName);

}
