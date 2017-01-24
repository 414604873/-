package com.xhydh.utils;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.content.Context;
import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;
//»’÷æ≈‰÷√¿‡
public class LogUtils {

	public LogUtils() {

	}

	public static void configLog() {
		final LogConfigurator logConfigurator = new LogConfigurator();

		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ File.separator + "xhydh_log.log");
		// Set the root log level
		logConfigurator.setRootLevel(Level.DEBUG);
		// Set log level of a specific logger
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.configure();
	}

}
