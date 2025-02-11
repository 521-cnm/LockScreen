package com.mengxiao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class UseScreenTime {
    public static final Logger logger = LoggerFactory.getLogger(UseScreenTime.class);
    private long totalTime;
    private long lastTime;
    private Timer timer;

    public UseScreenTime(){
        this.totalTime = 0;
        this.lastTime = System.currentTimeMillis();
        this.timer = new Timer();
    }
    //是否锁屏
    public static boolean isScreenLocked(){
        logger.info("开始检查是否锁屏......");
        Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq LogonUI.exe\"");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                if (line.contains("LogonUI.exe")){//找到说明已经锁屏
                    logger.info("已锁屏");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("检查锁屏出现异常:"+e.getMessage());
        }finally {
            if (process !=null)
                process.destroy();
            if (reader !=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("reader关闭异常："+e.getMessage());
                }
            }
        }
        logger.info("未锁屏");
        return false;
    }
    public void useTime(){
        logger.info("记录屏幕使用时间");
        Properties properties = new Properties();
        String  lockTime = "0";
        try {
            InputStream file = UseScreenTime.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(file);
             lockTime = properties.getProperty("lockTime");
             logger.info("配置的锁屏时间为："+lockTime+"分钟");
        } catch (Exception e) {
            logger.error("获取配置文件异常："+e.getMessage());
            return;
        }
        String finalLockTime = lockTime;
        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (!isScreenLocked()){
                    totalTime += (currentTime - lastTime);
                    logger.info("目前屏幕使用时间为："+totalTime/1000+"秒");
                }
                lastTime = currentTime;
                if ((Long.parseLong(finalLockTime)*60)<(totalTime/1000)){
                    logger.info("===开始执行锁屏操作===");
                    LockScreenController lock = new LockScreenController();
                    lock.lockWork();
                }
            }
        };
        timer.scheduleAtFixedRate(timeTask,0,1000);
    }
}
