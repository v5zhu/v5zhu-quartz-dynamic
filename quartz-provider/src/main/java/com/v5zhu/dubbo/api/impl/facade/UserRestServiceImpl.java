package com.v5zhu.dubbo.api.impl.facade;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.v5zhu.dubbo.api.UserService;
import com.v5zhu.dubbo.api.facade.UserRestService;
import com.v5zhu.dubbo.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zhuxl@paxsz.com on 2016/7/25.
 */
@Service(protocol = "rest")
@Path("/")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_HTML})
@Produces(MediaType.APPLICATION_JSON)
public class UserRestServiceImpl implements UserRestService {
    private static Logger logger = LoggerFactory.getLogger(UserRestServiceImpl.class);

    @Autowired
    private UserService userService;

    @GET
    @Path("user")
    @Override
    public Response findByLoginName(@QueryParam("loginName") String loginName) {
        logger.info("请求参数:[{}]",loginName);
        UserDto userDto = userService.findByLoginName(loginName);
        logger.info("请求返回的数据:[{}]", JSONObject.toJSONString(userDto));
        return Response.status(Response.Status.OK).entity(userDto).build();
    }
}
