package com.v5zhu.dubbo.api.facade;

import javax.ws.rs.core.Response;

/**
 * Created by zhuxl@paxsz.com on 2016/7/25.
 */
public interface UserRestService {
    Response findByLoginName(String loginName);
}
