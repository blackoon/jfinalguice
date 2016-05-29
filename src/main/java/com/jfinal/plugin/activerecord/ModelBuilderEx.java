package com.jfinal.plugin.activerecord;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import com.jfinal.plugin.activerecord.ModelBuilder;

public class ModelBuilderEx extends ModelBuilder{
	@SuppressWarnings("rawtypes")
	public static <M  extends ModelEx> void  build(ResultSet rs, Class<M> modelClass, RowCallback<M> callback) throws SQLException, InstantiationException, IllegalAccessException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String[] labelNames = new String[columnCount + 1];
		int[] types = new int[columnCount + 1];
		buildLabelNamesAndTypes(rsmd, labelNames, types);
		
		while (rs.next()) {
			M ar = modelClass.newInstance();
			Map<String, Object> attrs = ar.getAttrs();
			for (int i=1; i<=columnCount; i++) {
				Object value;
				if (types[i] < Types.BLOB)
					value = rs.getObject(i);
				else if (types[i] == Types.CLOB)
					value = handleClob(rs.getClob(i));
				else if (types[i] == Types.NCLOB)
					value = handleClob(rs.getNClob(i));
				else if (types[i] == Types.BLOB)
					value = handleBlob(rs.getBlob(i));
				else
					value = rs.getObject(i);
				
				attrs.put(labelNames[i], value);
			}
			if(!callback.run(ar)) {
				break;
			}
		}
		
	}
	
	private static final void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types) throws SQLException {
		for (int i=1; i<labelNames.length; i++) {
			labelNames[i] = rsmd.getColumnLabel(i);
			types[i] = rsmd.getColumnType(i);
		}
	}
}
