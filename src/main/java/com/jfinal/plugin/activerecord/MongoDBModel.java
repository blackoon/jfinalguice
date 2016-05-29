package com.jfinal.plugin.activerecord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public abstract class MongoDBModel<M extends MongoDBModel> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected abstract String getName();
	protected abstract String[] getColumns();
	
	protected Map<String, Object> data;
	
	public Map<String, Object> getData() {
		return data;
	}
	
	public M findOne() {
		Class<M> modelClass = (Class<M>)getClass();
		
		String[] columns = getColumns();
		if(columns == null || columns.length == 0) {
			DBObject object = MongoKit.getCollection(getName()).findOne();
			try {
				M ar = modelClass.newInstance();
				Map<String, Object> map = object.toMap();
				ar.data = new HashMap<String, Object>();
				ar.data.putAll(map);
				return ar;
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
		} else {
			BasicDBObject fields = new BasicDBObject();
			for(String column : columns)
				fields.put(column, 1);
			DBObject object = MongoKit.getCollection(getName()).findOne(new BasicDBObject(), fields);
			try {
				M ar = modelClass.newInstance();
				ar.data = new HashMap<String, Object>();
				for(String column : columns) {
					ar.data.put(column, object.get(column) );
				}
				return ar;
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public  final void find(DBObject where, MongoDBRowCallback<M> callback) {
		Class<M> modelClass = (Class<M>)getClass();
		String[] columns = getColumns();
		if(columns == null || columns.length == 0) {
			DBCursor cursor = MongoKit.getCollection(getName()).find(where);
			try {
				while(cursor.hasNext()) {
					DBObject object = cursor.next();
					M ar = modelClass.newInstance();
					ar.data = new HashMap<String, Object>();
					ar.data.putAll(object.toMap());
					if(!callback.run(ar))
						break;
					ar.data.clear();
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
			
		} else {
			BasicDBObject fields = new BasicDBObject();
			for(String column : columns)
				fields.put(column, 1);
			DBCursor cursor = MongoKit.getCollection(getName()).find(new BasicDBObject(), fields);
			try {
				while(cursor.hasNext()) {
					DBObject object = cursor.next();
					M ar = modelClass.newInstance();
					ar.data = new HashMap<String, Object>();
					for(String column : columns) {
						ar.data.put(column, object.get(column) );
					}
					if(!callback.run(ar))
						break;
					ar.data.clear();
				}
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
	}

}
