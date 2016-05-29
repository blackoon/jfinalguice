package com.jfinal.db;

import com.jfinal.plugin.activerecord.MongoDBModel;

public class MongoDBArticle extends MongoDBModel<MongoDBArticle>{

	private String name;
	private String[] columns;
	
	public static final MongoDBArticle DAO = new MongoDBArticle("data", null);
	
	public MongoDBArticle(String name, String[] columns) {
		this.name = name;
		this.columns = columns;
	}
	
	public MongoDBArticle() {
		
	}
	
	@Override
	protected String getName() {
		return name;
	}

	@Override
	protected String[] getColumns() {
		return columns;
	}

}
