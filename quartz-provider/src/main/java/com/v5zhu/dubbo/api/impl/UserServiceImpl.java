package com.v5zhu.dubbo.api.impl;


import com.v5zhu.dubbo.api.UserService;
import com.v5zhu.dubbo.dao.repository.mybatis.UserMybatisDao;
import com.v5zhu.dubbo.dto.UserDto;
import com.v5zhu.dubbo.po.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springside.modules.mapper.BeanMapper;

/**
 * Created by zhuxl@paxsz.com on 2016/7/25.
 */
@SuppressWarnings("ALL")
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserMybatisDao userMybatisDao;

    @Override
    public UserDto findByLoginName(String loginName) {
        User user=userMybatisDao.findByLoginName(loginName);
        if (user!=null)
            return BeanMapper.map(user,UserDto.class);
        return null;
    }
}
