package com.mengxiao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LockScreenController {
    public static final Logger logger = LoggerFactory.getLogger(LockScreenController.class);
    public void lockWork(){
        logger.info("开始锁屏......");
        String command = "rundll32.exe user32.dll,LockWorkStation";
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Exception e) {
            logger.error("锁屏异常："+e.getMessage());
        }
    }
}
