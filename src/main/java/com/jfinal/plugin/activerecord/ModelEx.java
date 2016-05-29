package com.jfinal.plugin.activerecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;



@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ModelEx<M extends ModelEx>  extends Model<M>{
	private static final long serialVersionUID = 1L;

	protected Config getConfig() {
		return DbKit.getConfig(getClass());
	}
	
	public void query(String where, RowCallback<M> callback) {
		Config config = getConfig();
		Connection conn = null;
		try {
			conn = config.getConnection();
			Class<M> modelClass = (Class<M>)getClass();
			
			Table table = TableMapping.me().getTable(modelClass);
			String sql = "select * from " + table.getName();
			if(where != null && !where.isEmpty()) {
				sql += " where " + where;
			}
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			
			
			ModelBuilderEx.build(rs, modelClass, callback);
			
			DbKit.closeQuietly(rs, pst);
		} catch (Exception e) {
			throw new ActiveRecordException(e);
		} finally {
			config.close(conn);
		}
	}
}
