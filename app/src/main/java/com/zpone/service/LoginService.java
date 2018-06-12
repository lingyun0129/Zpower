package com.zpone.service;

/**
 * 处理登陆相关功能操作
 * 注：相关方法根据实际需要添加
 * 
 * @author guzhicheng
 *
 */
public class LoginService {
	
	private final static String tag = LoginService.class.getCanonicalName();
	
	private String mUsername;
	private String mPass;
	
	private static LoginService mService;
	
	private LoginService(){}
	
	public static LoginService getService(){
		if(mService == null){
			mService = new LoginService();
		}
		return mService;
	}
	public boolean haveSavedInfo(){
		return false;
	}
	/**
	 * 登录
	 * @return
	 */
	public boolean login(){
		
		return false;
	}
	
	/**
	 * 注销
	 * @return
	 */
	public boolean logout(){
		
		return false;
	}

	/**
	 * 是否已登录
	 * @return
	 */
	public boolean isLogin(){
		
		return false;
	}

	public String getmUsername() {
		return mUsername;
	}

	public void setmUsername(String mUsername) {
		this.mUsername = mUsername;
	}

	public String getmPass() {
		return mPass;
	}

	public void setmPass(String mPass) {
		this.mPass = mPass;
	}
	
}
