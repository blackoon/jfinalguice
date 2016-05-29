package com.jfinal.core;

import javax.servlet.ServletContext;

import com.jfinal.config.JFinalConfig;
import com.jfinal.core.JFinal;
import com.jfinal.db.AppConfig;

public class JFinalHelper {
	public static JFinalConfig config;
	public static ServletContext context;
	
	public static final JFinal jfinal = JFinal.me();
	public static void  init() {
		if(config == null)
			config = new AppConfig();
		if(context == null)
			context = new DummyServletContext();
		
//		jfinal.init(config, context);
		config.afterJFinalStart();
	}
	
	public static void destroy() {
		config.beforeJFinalStop();
//		jfinal.stopPlugins();
	}
}
