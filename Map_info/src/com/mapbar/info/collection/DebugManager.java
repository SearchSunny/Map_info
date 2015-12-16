package com.mapbar.info.collection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.mapbar.info.collection.util.SdcardUtil;

import android.os.Environment;
/**
 * 日志管理
 * @author miaowei
 *
 */
public class DebugManager {

	private static String debugFile;
	private static OutputStream os;

	public static void println(String word) {
		writeDebug(word);
	}

	public static void saveExceptionLog(Exception e, String name) {
		e.printStackTrace();
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		DebugManager.println(error);
	}

	public static void close() {
		if (os != null) {
			try {
				os.close();
				os = null;
				debugFile = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void writeDebug(String word) {
		if (debugFile == null) {
			File fl = new File(SdcardUtil.getSdcardCollInfoNO() + "/log");
			if (!fl.exists()) {
				fl.mkdir();
			}

			File flDebug = null;
			debugFile = SdcardUtil.getSdcardCollInfoNO() + "/log" + "/" + System.currentTimeMillis() + ".txt";
			flDebug = new File(debugFile);
			try {
				flDebug.createNewFile();
				os = new FileOutputStream(flDebug, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (os != null) {
			try {
				word = word + "\n";
				os.write(word.getBytes());
				os.flush();
				DebugManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
