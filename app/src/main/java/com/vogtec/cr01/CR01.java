package com.vogtec.cr01;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.vogtec.cr01.utils.MyLog;

import java.io.File;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

public class CR01 extends Application {
	
	private final static String tag = CR01.class.getCanonicalName();


	private static CR01 mInstance;
	public CR01() {
		Log.i(tag, "CR01()");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(tag, "onCreate()");
		mInstance = this;
		//记录应用本次启动时间
		//测试
		GlobalVars.mAppStartTime = System.currentTimeMillis();
		String state = Environment.getExternalStorageState();
		if(state != null && state.equals("mounted")){
			GlobalVars.mAppRootDir = Environment.getExternalStorageDirectory()+"/cr01";
			File root = new File(GlobalVars.mAppRootDir);
			if(!root.exists()){
				root.mkdir();
			}
//			MyLog.setMYLOG_WRITE_TO_FILE_SYS(true);
			MyLog.init(GlobalVars.mAppRootDir);
		}

		Fragmentation.builder()
				// 设置 栈视图 模式为 悬浮球模式   SHAKE: 摇一摇唤出   NONE：隐藏
				.stackViewMode(Fragmentation.NONE)
				// ture时，遇到异常："Can not perform this action after onSaveInstanceState!"时，会抛出
				// false时，不会抛出，会捕获，可以在handleException()里监听到
				.debug(BuildConfig.DEBUG)
				// 线上环境时，可能会遇到上述异常，此时debug=false，不会抛出该异常（避免crash），会捕获
				// 建议在回调处上传至我们的Crash检测服务器
				.handleException(new ExceptionHandler() {
					@Override
					public void onException(Exception e) {
						// 以Bugtags为例子: 手动把捕获到的 Exception 传到 Bugtags 后台。
						// Bugtags.sendException(e);
					}
				})
				.install();

	}

	public static CR01 getInstance(){
		return mInstance;
	}

}
