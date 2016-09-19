package com.v5zhu.quartz.api;



public class Provider {
    public static void main(String[] args) throws Exception {
        String env = "development";
        if (args.length > 0) {
            try {
                int cmd = Integer.parseInt(args[0]);
                switch (cmd) {
                    case 0:
                        env = "test";
                        break;
                    case 1:
                        env = "production";
                        break;
                    case 2:
                        env = "preview";
                        break;
                    default:
                        env = "development";
                        break;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        System.setProperty("spring.profiles.active", env);
        //使用dubbo默认配置 自动加载META-INF/spring目录下的所有Spring配置。
        String[] ars = new String[]{};
        com.alibaba.dubbo.container.Main.main(ars);
    }
}
