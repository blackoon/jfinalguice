package com.hylanda.web.config;

import com.hylanda.web.controller.ApiController;
import com.hylanda.web.controller.MainController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;

public class WebConfig extends JFinalConfig{

	
	@Override
	public void configConstant(Constants me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/", MainController.class, "template");
//		me.add("/api", ApiController.class, "template");
		me.add("/api", ApiController.class);
	}

	@Override
	public void configPlugin(Plugins me) {
		configIoc(me);
	}

	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new GuiceInterceptor());
		
	}

	@Override
	public void configHandler(Handlers me) {
		// TODO Auto-generated method stub
		
	}
	
	private void configIoc(Plugins me) {
		GuicePlugin guicePlugin=new GuicePlugin();
		//TODO:
		//guicePlugin.bind(BaseService.class, DbService.class);
		
		
		me.add(guicePlugin);
	}

}
