package com.v5zhu.dubbo.api;

import com.v5zhu.dubbo.dto.UserDto;

/**
* Created by zhuxl on 2015/5/20.
*/
public interface UserService {

    /**根据用户名查询用户
     * @param loginName
     * @return
     */
    UserDto findByLoginName(String loginName);

}
