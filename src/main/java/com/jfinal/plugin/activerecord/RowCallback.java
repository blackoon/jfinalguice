package com.jfinal.plugin.activerecord;

@SuppressWarnings("rawtypes")
public interface RowCallback<M extends ModelEx> {
	public boolean run(M m);
}
