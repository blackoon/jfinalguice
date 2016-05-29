package com.hylanda.web.controller;

import java.io.IOException;

import com.google.inject.Inject;
import com.hylanda.web.model.AppQuery;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class ApiController extends Controller {

	@Inject
	private AppQuery appQuery;

	@ActionKey("/api/search")
	public void search() throws IOException {
		String para = this.getPara("para");
		renderText(appQuery.queryByString(para));
	}
}
