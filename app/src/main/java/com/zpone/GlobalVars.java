package com.zpone;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * 专门存储一些全局的变量
 * 
 * @author guzhicheng
 *
 */
public class GlobalVars {

	/**
	 * 应用本次启动时间
	 */
	public static long mAppStartTime = 0;
	/**
	 * 应用根目录
	 */
	public static String mAppRootDir;
	
	public static int mMainFragId;
	public static int mMainFrag2Id;
	public static FragmentActivity mMainActivity;
	public static Fragment mMainFragment;
	
}
