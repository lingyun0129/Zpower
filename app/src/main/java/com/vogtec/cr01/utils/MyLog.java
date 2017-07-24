package com.vogtec.cr01.utils;

import android.util.Log;

import com.vogtec.cr01.GlobalVars;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日志相关工具类：打印日志、存储日志、删除日志历史
 * 
 * @author BaoHang
 * @version 1.0
 * @data 2012-2-20
 */
public class MyLog {

	private final static String tag = MyLog.class.getCanonicalName();

	// 存储日志到文件中的开关
	private static Boolean MYLOG_SAVE_TO_FILE = false;
	// 日志打印开关
	private static Boolean MYLOG_SWITCH = true;
	// 日志打印级别
	private static char MYLOG_TYPE = 'v';
	// 日志存储路径
	public static String MYLOG_PATH_SDCARD_DIR = "/mnt/asec/cr01/log";
	// 历史日志保存时间
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;
	// 日志文件名称
	private static String MYLOG_FILE_NAME = "SKSLog.txt";
	// 日志时间
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	// 日志文件夹时间
	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");

	private static MySyslogThread mSyslogThread = null;

	public MyLog() {

	}

	/**
	 * 相关初始化
	 * 
	 * @param dir
	 *            应用根路径
	 */
	public static void init(String dir) {
		MYLOG_PATH_SDCARD_DIR = dir + "/syslogs/" + logfile.format(new Date()) + "/";
		MyLog.d(tag, "日志存储路径" + MYLOG_PATH_SDCARD_DIR);
		MyLog.d(tag, "Open the syslog?" + MYLOG_SAVE_TO_FILE);

		if (MYLOG_SAVE_TO_FILE) {
			writeSystemLogToFile();
		}

	}

	public static void w(String tag, Object msg) {
		log(tag, msg.toString(), 'w');
	}

	public static void e(String tag, Object msg) {
		log(tag, msg.toString(), 'e');
	}

	public static void d(String tag, Object msg) {
		log(tag, msg.toString(), 'd');
	}

	public static void i(String tag, Object msg) {
		log(tag, msg.toString(), 'i');
	}

	public static void v(String tag, Object msg) {
		log(tag, msg.toString(), 'v');
	}

	public static void w(String tag, String text) {
		log(tag, text, 'w');
	}

	public static void e(String tag, String text) {
		log(tag, text, 'e');
	}

	public static void d(String tag, String text) {
		log(tag, text, 'd');
	}

	public static void i(String tag, String text) {
		log(tag, text, 'i');
	}

	public static void v(String tag, String text) {
		log(tag, text, 'v');
	}

	/**
	 * 根据tag, msg和等级，输出日志
	 * 
	 * @param tag
	 * @param msg
	 * @param level
	 * @return void
	 * @since v 1.0
	 */
	private static void log(String tag, String msg, char level) {
		if (MYLOG_SWITCH) {
			if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.e(tag, msg);
			} else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.w(tag, msg);
			} else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.d(tag, msg);
			} else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.i(tag, msg);
			} else {
				Log.v(tag, msg);
			}
//			if (MYLOG_SAVE_TO_FILE)
//				writeLogtoFile(String.valueOf(level), tag, msg);
		}
	}

	/**
	 * 将本应用日志写入文件
	 * 
	 * @return
	 */
	private static void writeLogtoFile(String mylogtype, String tag, String text) {

		if (MYLOG_PATH_SDCARD_DIR == null || MYLOG_PATH_SDCARD_DIR.startsWith("null")) {
			return;
		}

		Date nowtime = new Date();
		String needWriteFile = logfile.format(nowtime);
		String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype + "    " + tag + "    " + text;
		FileWriter filerWriter = null;
		try {
			File dir = new File(MYLOG_PATH_SDCARD_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(MYLOG_PATH_SDCARD_DIR, needWriteFile + "_" + MYLOG_FILE_NAME);
			// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			filerWriter = new FileWriter(file, true);
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.close();

		} catch (FileNotFoundException f) {
			MyLog.d(tag, "日志文件不存在，日志停止写入文件");
			f.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (filerWriter != null) {
				try {
					filerWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将系统所有日志写入文件
	 * 注：长时间写入会导致存储文件过多，占用大量存储空间
	 */
	public static void writeSystemLogToFile() {
		if (mSyslogThread == null) {
			mSyslogThread = new MySyslogThread();
			mSyslogThread.start();
		} else if (mSyslogThread.getState() == State.TERMINATED || mSyslogThread.getState() == State.TIMED_WAITING) {
			mSyslogThread = new MySyslogThread();
			mSyslogThread.start();
		}
	}

	/**
	 * 删除日志文件
	 */
	public static void delFile() { 
		String needDelFile = logfile.format(getDateBefore());
		File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFile + MYLOG_FILE_NAME);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 */
	private static Date getDateBefore() {
		Date nowtime = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowtime);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}

	/**
	 * 日志打印总开关
	 * 
	 * @param mYLOG_SWITCH
	 */
	public static void setMYLOG_SWITCH(Boolean mYLOG_SWITCH) {
		MYLOG_SWITCH = mYLOG_SWITCH;
	}

	public static Boolean getMYLOG_WRITE_TO_FILE_SYS() {
		return MYLOG_SAVE_TO_FILE;
	}

	/**
	 * 是否将日志保存到文件中
	 * 
	 * @param mMYLOG_SAVE_TO_FILE
	 */
	public static void setMYLOG_WRITE_TO_FILE_SYS(Boolean mMYLOG_SAVE_TO_FILE) {
		MYLOG_SAVE_TO_FILE = mMYLOG_SAVE_TO_FILE;
		if (MYLOG_SAVE_TO_FILE) {
			writeSystemLogToFile();
		}
	}

	static class MySyslogThread extends Thread {

		public MySyslogThread() {
			super("Syslog");
		}

		@Override
		public void run() {
			super.run();
			String comm = "logcat";

			Process process;
			InputStreamReader isr = null;
			FileWriter filerWriter = null;
			try {
				process = Runtime.getRuntime().exec(comm);
				isr = new InputStreamReader(process.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String res = "";
				File dir = new File(MYLOG_PATH_SDCARD_DIR);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				int fileNum = 0;
				int loglines = 0;
				SimpleDateFormat appStartTime = new SimpleDateFormat("HHmmss");
				if (GlobalVars.mAppStartTime == 0) {
					GlobalVars.mAppStartTime = System.currentTimeMillis();
				}
				File file = new File(MYLOG_PATH_SDCARD_DIR,
						appStartTime.format(GlobalVars.mAppStartTime) + "_systemLogs_0.log");
				filerWriter = new FileWriter(file, true);
				BufferedWriter bufWriter = new BufferedWriter(filerWriter);
				while ((res = br.readLine()) != null) {
					if (!MYLOG_SAVE_TO_FILE) {
						return;
					}
					//每个日志文件最多存储15000行
					if (loglines > 15000) {
						fileNum++;
						loglines = 0;
						file = new File(MYLOG_PATH_SDCARD_DIR,
								appStartTime.format(GlobalVars.mAppStartTime) + "_systemLogs_" + fileNum + ".log");
						filerWriter = new FileWriter(file, true);
						bufWriter = new BufferedWriter(filerWriter);
					}

					Date nowtime = new Date();
					String needWriteMessage = myLogSdf.format(nowtime) + "-" + res;
					bufWriter.write(needWriteMessage);
					bufWriter.newLine();
					loglines++;
				}

				bufWriter.close();
			} catch (FileNotFoundException f) {
				MyLog.d(tag, "日志文件不存在־");
				MYLOG_SAVE_TO_FILE = false;
				f.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (isr != null) {
						isr.close();
					}
					if (filerWriter != null) {
						filerWriter.close();
					}
				} catch (Exception e2) {
				}
			}
		}
	}

}
