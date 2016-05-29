package com.jfinal.plugin.activerecord;

@SuppressWarnings("rawtypes")
public interface MongoDBRowCallback<M extends MongoDBModel> {
	public boolean run(M m);
}
