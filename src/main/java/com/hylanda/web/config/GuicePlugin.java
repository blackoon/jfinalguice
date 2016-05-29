package com.hylanda.web.config;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.binder.ScopedBindingBuilder;
import com.jfinal.plugin.IPlugin;

public class GuicePlugin implements IPlugin {

	private static Injector guice;
//	private Client client;
	
	
	private class BindItem {
		
		@SuppressWarnings("rawtypes")
		BindItem(Class type) {
			this.type = type;
			this.instance = null;
		}
		
		BindItem(Object object) {
			this.type = null;
			this.instance = object;
		}
		
		@SuppressWarnings({  "rawtypes" })
		Class  type;
		
		Object instance;
	}

	// 绑定注入的map
	@SuppressWarnings("rawtypes")
	private HashMap<Class, BindItem> bindMap;

	/**
	 * 默认构造函数 初始化绑定注入map
	 */
	@SuppressWarnings("rawtypes")
	public GuicePlugin() {
		bindMap = new HashMap<Class, BindItem>();
	}

	/**
	 * 封装guice中的getInstance
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getInstance(Class<T> clazz) {
		return guice.getInstance(clazz);
	}

	/**
	 * 绑定依赖
	 * 
	 * @param bindSrc
	 * @param bindTo
	 */
	public void bind(Class<?> bindSrc, Class<?> bindTo) {
		bindMap.put(bindSrc, new BindItem(bindTo));
	}
	
	/**
	 * 绑定依赖
	 * 
	 * @param bindSrc
	 * @param bindTo
	 */
	public void bindInstance(Class<?> bindSrc, Object bindTo) {
		bindMap.put(bindSrc, new BindItem(bindTo));
	}

	@Override
	public boolean start() {

		guice = Guice.createInjector(new Module() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void configure(Binder binder) {
				for (Entry<Class, BindItem> entry : bindMap.entrySet()) {
					BindItem value = entry.getValue();
					if(value.instance == null)
						binder.bind(entry.getKey()).to(value.type)
								.asEagerSingleton();
					else
						binder.bind(entry.getKey()).toInstance(value.instance);
				}
				bindPackage(binder, "com.hylanda");
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			private void bindPackage(Binder binder, String pack) {
				Set<Class<?>> classes = PackageScanner
						.getClasses(pack);
				for (Class<?> clz : classes) {
					AnnoService serviceClz = clz
							.getAnnotation(AnnoService.class);
					if (serviceClz != null) {
						if (bindMap.get(serviceClz) == null) {
							ScopedBindingBuilder r;

							Class<?> clz2 = serviceClz.value();
							if (clz2 == null || NullInterface.class.equals(clz2)) {
								r = binder.bind(clz);
							} else {
								r = binder.bind((Class) clz2).to(clz);
							}
							if (serviceClz.singleton()) {
								r.asEagerSingleton();
							}
						}
					}
				}
			}
		});

		return true;
	}

	@Override
	public boolean stop() {
//		client.close();
		return true;
	}

}
