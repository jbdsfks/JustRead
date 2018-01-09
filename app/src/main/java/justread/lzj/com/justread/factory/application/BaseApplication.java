package justread.lzj.com.justread.factory.application;

import android.app.Application;

import justread.lzj.com.justread.factory.console.SwitchTool;
import justread.lzj.com.justread.thirdparty.logger.LogLevel;
import justread.lzj.com.justread.thirdparty.logger.Logger;
import justread.lzj.com.justread.thirdparty.logger.Settings;

/**
 * Created by 83827 on 2017/12/15.
 */
public class BaseApplication extends Application {

    private static final String MY_TAG = "ReadFragment";
    private static boolean isFirst = true;

    private static class uniqueInstance {
        private static BaseApplication instance;
    }

    public static BaseApplication getInstance() {
        return uniqueInstance.instance;
    }

    public static BaseApplication getContext() {
        return getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (isFirst) {
            isFirst = false;
            uniqueInstance.instance = this;
            initLogger();
            initData();
        }
    }

    protected void initData(){

    }

    private void initLogger() {

        /*
        Logger
            .init(MY_TAG)                   // default PRETTYLOGGER or use just init()
            .methodCount(2)                 // default 2
            .hideThreadInfo()               // default shown
            .logLevel(LogLevel.NONE)        // default LogLevel.FULL
            .methodOffset(2)                // default 0
            .logTool(new AndroidLogTool()); // custom log tool, optional
        */

        Settings mLoggerSettings = Logger.init(MY_TAG);
        mLoggerSettings.methodCount(1);
        mLoggerSettings.hideThreadInfo();
        if (SwitchTool.isDebug) {
            mLoggerSettings.logLevel(LogLevel.FULL);
        } else {
            mLoggerSettings.logLevel(LogLevel.NONE);
        }
    }
}

