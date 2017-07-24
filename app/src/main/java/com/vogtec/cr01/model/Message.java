package com.vogtec.cr01.model;

/**
 * 通过蓝牙传输的消息的数据格式
 * 
 * 注：数据格式依据实际情况再修改
 * 
 * @author guzhicheng
 *
 */
public class Message {
	
	public int msgid;
	public int dataType;
	public byte[] data;
}
