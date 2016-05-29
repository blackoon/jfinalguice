package com.jfinal.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.indices.IndexMissingException;

import com.jfinal.core.JFinalHelper;
import com.jfinal.plugin.activerecord.MongoDBRowCallback;

public class Mongo2Index {
	private Client client;
	
	public void run() {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name","zzs_cluster").put("client.transport.sniff", true).build();
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						"localhost", 9300));
		
		JFinalHelper.init();
		try {
			doWork();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
		JFinalHelper.destroy();
	}
	
	public void close() {
		client.close();
	}

	private void doWork() throws IOException {
		if(!existsIndex("articles")) {
			create("articles");
		}
		
		
		MongoDBArticle.DAO.find(null, new MongoDBRowCallback<MongoDBArticle>(){
			int i = 0;
			@Override
			public boolean run(MongoDBArticle article) {
				Object bytes = article.getData().get("format_content");
				String content;
				try {
					content = unzip((byte[])bytes);
					article.getData().put("content", HtmlTagUtil.delHTMLTag(content));
					indexArticle(article);
					i++;
					return i < 2000000;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
			
		});
		
	}
	
	protected boolean indexArticle(MongoDBArticle article) {
		// TODO Auto-generated method stub
		Map<String, Object> m = article.getData();
		int type = (Integer)m.get("source_type");
		if(type != 0 && type != 3)
			return true;
		
		String content = (String)m.get("content");
		if(content == null || content.isEmpty())
			content = (String)m.get("title");
		
		long hash = 0;
		/*if(content.length() > 1024) {
			//长文本， 我们抽取关键词计算
			hash = SimHash.computeSimHashForKeywords(content, 50);
		}
		else*/ {
			//短文本，用原来的方法
//			byte[] tmp2 = SimHash.trimSpecialSymbol(content).getBytes();
//			hash = SimHash.computeOptimizedSimHashForBytes(tmp2, 0, tmp2.length);
		}
		String strHash = Long.toString(hash, 16);
		m.put("simhash", strHash);
		
		String json = generateJson(m);
		addIndex("articles", "news", json, (String)m.get("url"));
		
		return true;
	}
	
	private String generateJson(Map<String, Object> article) {
        String json = "";
        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                    .startObject();
            contentBuilder.field("url_crc", article.get("url_crc"));
            contentBuilder.field("title", article.get("title"));
            //contentBuilder.field("content", article.get("content"));
            contentBuilder.field("author", article.get("author"));
            contentBuilder.field("media_name", article.get("media_name"));
            contentBuilder.field("content_media_name", article.get("content_media_name"));
            contentBuilder.field("release_date", article.get("release_date"));
            contentBuilder.field("url", article.get("url"));
            
            contentBuilder.field("simhash", article.get("simhash"));
            json = contentBuilder.endObject().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
	
	private void addIndex(String index, String type, String source, String url) {
		IndexResponse 
		response = client.prepareIndex().setIndex(index).setType(type).setSource(source).execute().actionGet();
		System.out.println(url + " create " + response.isCreated());
	}

	private String unzip(byte[] bytes) throws IOException {
		if(bytes == null)
			return "";
		ByteArrayInputStream tmpIStream = new ByteArrayInputStream(bytes, 0, bytes.length);
		InflaterInputStream inflate = new GZIPInputStream(tmpIStream);
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024); 
		try {  
            int i = 1024;  
            byte[] buf = new byte[i];  
  
            while ((i = inflate.read(buf, 0, i)) > 0) {  
                o.write(buf, 0, i);  
            } 
            inflate.close();
            o.flush();
  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return new String(o.toByteArray()); 
	}
	
	private boolean create(String index) {
		CreateIndexRequestBuilder builder = client.admin().indices().prepareCreate(index);
		try {
			File f = new File("articles.json");
			String source = FileUtils.readFileToString(f, "UTF-8");
			builder.setSource(source);
			CreateIndexResponse r = builder.get();
			return r.isAcknowledged();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
	}

	private boolean existsIndex(String index) {
		
		IndicesExistsRequestBuilder builder = client.admin().indices().prepareExists(index);
		
		try {
			IndicesExistsResponse res = builder.get();
			return res.isExists();
		}catch(IndexMissingException e) {
			return false;
		}
	}
	
	public static void main(String[] args) {
		Mongo2Index index = new Mongo2Index();
		index.run();
	}
}
