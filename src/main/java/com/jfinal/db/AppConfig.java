package com.jfinal.db;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.plugin.monogodb.MongodbPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 试用jfinal 实现 数据入库
 * 这里配置 mysql， mongodb等， 不配置其他插件
 * @author zhouzusheng
 *
 */
public class AppConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configRoute(Routes me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin dsMysql = new DruidPlugin("jdbc:mysql://localhost:3306/test?characterEncoding=UTF-8", "root", "");
		me.add(dsMysql);

		ActiveRecordPlugin arpMysql = new ActiveRecordPlugin("mysql", dsMysql);
		me.add(arpMysql);
		
		arpMysql.addMapping("urltrace_20151008", NewsArticle.class);
		
		MongodbPlugin mongodbPlugin = new MongodbPlugin("192.168.0.96", 27017, "data201509");
		me.add(mongodbPlugin);
		
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configHandler(Handlers me) {
		// TODO Auto-generated method stub

	}

}
