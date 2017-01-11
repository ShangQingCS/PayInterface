package cn.jeefree.pay.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cn.jeefree.pay.config.PayConfig;
import cn.jeefree.pay.core.MD5;



public class WeChatUtil {
	
	/**
	 * 发送https请求
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return 返回微信服务器响应的信息
	 */
	public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		
		
	
		
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { 
					
					new X509TrustManager() { 

						// 检查客户端证书
						public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						}

						// 检查服务器端证书
						public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						}

						// 返回受信任的X509证书数组
						public X509Certificate[] getAcceptedIssuers() {
							return null;
						}
					}
					
			};
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			return buffer.toString();
		} catch (ConnectException ce) {
			//log.error("连接超时：{}", ce);
		} catch (Exception e) {
			//log.error("https请求异常：{}", e);
		}
		return null;
	}

	
	public static String urlEncodeUTF8(String source){
		String result = source;
		try {
			result = java.net.URLEncoder.encode(source,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Map<String, String> doXMLParse(String strxml) {
		try {
		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

		if(null == strxml || "".equals(strxml)) {
			return null;
		}
		
		Map<String, String> m = new HashMap<String, String>();
		
		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List<?> list = root.getChildren();
		Iterator<?> it = list.iterator();
		while(it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List<?> children = e.getChildren();
			if(children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = WeChatUtil.getChildrenText(children);
			}
			
			m.put(k, v);
		}
		
		//关闭流
		in.close();
		
		return m;
		} catch (Exception e) {
			System.out.println("ConfigUtil转换XML出现异常：\n"+e.getMessage());
			return null;
		}
		
		
	}
	
	/**
	 * 获取子结点的xml
	 * @param children
	 * @return String
	 */
	public static String getChildrenText(List<?> children) {
		StringBuffer sb = new StringBuffer();
		if(!children.isEmpty()) {
			Iterator<?> it = children.iterator();
			while(it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List<?> list = e.getChildren();
				sb.append("<" + name + ">");
				if(!list.isEmpty()) {
					sb.append(WeChatUtil.getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 生成16位随机码
	 */
	public static String CreateNoncestr() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String res = "";
		for (int i = 0; i < 16; i++) {
			Random rd = new Random();
			res += chars.charAt(rd.nextInt(chars.length() - 1));
		}
		return res;
	}
	
	/**
	 * 生成签名
	 */
	@SuppressWarnings("rawtypes")
	public static String createSign(String characterEncoding,SortedMap<Object,Object> parameters){
		StringBuffer sb = new StringBuffer();
		Set<?> es = parameters.entrySet();
		Iterator<?> it = es.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			Object v = entry.getValue();
			if(null != v && !"".equals(v) 
					&& !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + PayConfig.API_KEY);
		
		String sign = MD5.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
		return sign;
	}

	/**
	 * 生成请求的xml
	 */
	@SuppressWarnings("rawtypes")
	public static String getRequestXml(SortedMap<Object,Object> parameters){
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set<?> es = parameters.entrySet();
		Iterator<?> it = es.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)||"sign".equalsIgnoreCase(k)) {
				sb.append("<"+k+">"+"<![CDATA["+v+"]]></"+k+">");
			}else {
				sb.append("<"+k+">"+v+"</"+k+">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	/**
	 * 生成返回微信服务端的xml
	 */
	public static String setXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code
				+ "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}
}
