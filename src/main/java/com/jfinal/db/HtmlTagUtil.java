package com.jfinal.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/** 
 * @author zhangy
 * @E-mail:blackoon88@gmail.com 
 * @qq:846579287
 * @version 创建时间：2015年7月10日 下午4:33:20 
 * note
 */
public class HtmlTagUtil {
	private static Pattern p_script = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>", Pattern.CASE_INSENSITIVE);
	private static Pattern p_style = Pattern.compile("<style[^>]*?>[\\s\\S]*?<\\/style>", Pattern.CASE_INSENSITIVE);;
	private static Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

	/**
	 * 去掉html标签
	 * 
	 * @param htmlStr
	 *            包含html标签的字符串
	 * @return
	 */
	public static String delHTMLTag(String htmlStr) {
		//处理转义字符
		htmlStr =  StringEscapeUtils.unescapeHtml(htmlStr);
		
		StringBuilder s = new StringBuilder();
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll("");

		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll("");
		
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll("");
		return s.append(htmlStr.trim()).toString();
	}


}

