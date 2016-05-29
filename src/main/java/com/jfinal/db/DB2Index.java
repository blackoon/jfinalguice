package com.jfinal.db;

import java.io.File;
import java.io.IOException;

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
import com.jfinal.plugin.activerecord.RowCallback;

public class DB2Index {
	
	private Client client;

	public void run() {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name","zzs_cluster").put("client.transport.sniff", true).build();
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						"localhost", 9300));
		
		JFinalHelper.init();
		doWork();
		close();
		JFinalHelper.destroy();
	}
	
	private void doWork() {
		if(!existsIndex("articles")) {
			create("articles");
		}
		
		NewsArticle.dao.query("", new RowCallback<NewsArticle>() {
			
			@Override
			public boolean run(NewsArticle m) {
				String content = m.getStr("content");
				long hash = 0;
				/*if(content.length() > 1024) {
					//长文本， 我们抽取用好的特征词方法（分词加词频——
					hash = SimHash.computeSimHashForWords(content);
				}
				else*/ {
					//短文本，用原来的方法，滑动窗口
//					byte[] tmp2 = SimHash.trimSpecialSymbol(content).getBytes();
//					hash = SimHash.computeOptimizedSimHashForBytes(tmp2, 0, tmp2.length);
				}
				String strHash = Long.toString(hash, 16);
//				m.put("simhash", strHash);
				
				String json = generateJson(m);
				addIndex("articles", "news", json, (String)m.get("url"));
				return true;
			}
		});
		
	}
	
	public void close() {
		client.close();
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
	
	private void addIndex(String index, String type, String source, String url) {
		IndexResponse 
		response = client.prepareIndex().setIndex(index).setType(type).setSource(source).execute().actionGet();
		System.out.println(url + " create " + response.isCreated());
	}

	
	private String generateJson(NewsArticle article) {
        String json = "";
        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                    .startObject();
            contentBuilder.field("url_crc", article.get("url_crc"));
            contentBuilder.field("title", article.get("title"));
            contentBuilder.field("content", article.get("content"));
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

	public static void main(String[] args) {
		DB2Index index = new DB2Index();
		index.run();
	}
}
