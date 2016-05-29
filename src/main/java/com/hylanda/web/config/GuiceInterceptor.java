package com.hylanda.web.config;

import java.lang.reflect.Field;

import com.google.inject.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;

public class GuiceInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		// TODO /*
		/* 得到拦截的controller 判断是否有依赖注入的属性
		 */
		Controller controller = ai.getController();
		Field[] fields = controller.getClass().getDeclaredFields();
		for (Field field : fields) {
			Object bean = null;
			if (field.isAnnotationPresent(Inject.class))
				bean = GuicePlugin.getInstance(field.getType());
			else
				continue;
			
			try {
				if (bean != null) {
					field.setAccessible(true);
					field.set(controller, bean);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		ai.invoke();

		
	}

}
