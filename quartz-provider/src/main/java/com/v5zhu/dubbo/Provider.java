package com.v5zhu.dubbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Provider {
    private  static Logger logger = LoggerFactory.getLogger(Provider.class);
    public static void main(String[] args) throws Exception {
        String env = "development";
        if (args.length > 0) {
            try {
                int cmd = Integer.parseInt(args[0]);
                switch (cmd) {
                    case 0:
                        env = "development";
                        break;
                    case 1:
                        env = "test";
                        break;
                    case 2:
                        env = "preview";
                        break;
                    default:
                        env = "production";
                        break;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        System.setProperty("spring.profiles.active", env);
        logger.info("===========================================================================");
        logger.info("                     服务将在[{}]环境启动                  ", env);
        logger.info("===========================================================================");
        String[] ars = {};
        com.alibaba.dubbo.container.Main.main(ars);
    }
}
