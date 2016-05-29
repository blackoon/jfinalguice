package com.hylanda.web.controller;

import com.jfinal.core.Controller;

public class MainController extends Controller{
	public void index() {
		renderVelocity("index.html");
	}
	public void multi() {
		renderVelocity("multi.html");
	}
	public void single() {
		renderVelocity("single.html");
	}
}
