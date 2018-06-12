package com.zpone.model;

/**
 * 处理message相关的操作：消息解析、消息组装等
 * 
 * @author guzhicheng
 *
 */
public class MessageFactory {

	/**
	 * 将接收到的byte数据解析成对应的消息
	 * 主要提供给LoginService等service层使用，不推荐UI层直接使用
	 * 注：相关方法根据实际需要添加
	 * 
	 * @param data
	 * @return
	 */
	public static Message parseMessage(byte[] data){
		//解析数据
		
		
		return null;
	}
	
	/**
	 * 将message组装成byte数据
	 * @return
	 */
	public static byte[] makeMessage(Message msg){
		
		return null;
	}
}
