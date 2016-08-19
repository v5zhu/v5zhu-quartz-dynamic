/**
 * Copyright 1999-2014 dangdang.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.v5zhu.dubbo.extension;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

/**
 * @author we9.li@changhong.com
 */
@Priority(Priorities.USER)
public class TraceFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("Request filter invoked");
//        System.out.println(requestContext.getHeaders().get("Content-Type").contains("application/x-www-form-urlencoded"));


       /* InputStream is = requestContext.getEntityStream();
        // 从post流中读取
        if (requestContext.getHeaders().get("Content-Type").contains("application/x-www-form-urlencoded")) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String packet = sb.toString();
            if (packet != null) {
                JSONObject object = new JSONObject();
                String[] params = packet.split("&");
                for (String param : params) {
                    String[] items = param.split("=");
                    if (items.length > 0) {
                        String key = items[0];
                        String value = items[1];
                        if (value != null && value.indexOf(",") != -1) {
                            String[] values = items[1].split(",");
                            JSONArray arr = (JSONArray) JSON.toJSON(values);
                            object.put(key, arr);

                        } else {
                            object.put(key, value);
                        }
                    }

                }
                requestContext.setProperty("packet", object.toJSONString());
            }
            System.out.println("getPacket receive String =" + packet);
        }*/

    }

    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        System.out.println("Response filter invoked");
    }
}