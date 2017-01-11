package cn.jeefree.pay.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UtilOther {
	
	public static String getLocalIP(){
		
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}
	
	/**
	 * 
	 * 功能描述：去除字符串首部为"0"字符
	  
	 * @param str 传入需要转换的字符串
	 * @return 转换后的字符串
	 */
	public static String removeZero(String str){   
	   	char  ch;  
	   	String result = "";
	   	if(str != null && str.trim().length()>0 && !str.trim().equalsIgnoreCase("null")){				
	   		try{			
				for(int i=0;i<str.length();i++){
					ch = str.charAt(i);
					if(ch != '0'){						
						result = str.substring(i);
						break;
					}
				}
			}catch(Exception e){
				result = "";
			}	
		}else{
			result = "";
		}
	   	return result;
			
	}
	
	/**
	 * 
	 * 功能描述：金额字符串转换：单位分转成单元
	  
	 * @param str 传入需要转换的金额字符串
	 * @return 转换后的金额字符串
	 */	
	public static String fenToYuan(Object o) {
		if(o == null)
			return "0.00";
		String s = o.toString();
		int len = -1;	
		StringBuilder sb = new StringBuilder();
		if (s != null && s.trim().length()>0 && !s.equalsIgnoreCase("null")){
			s = removeZero(s);
			if (s != null && s.trim().length()>0 && !s.equalsIgnoreCase("null")){
				len = s.length();
				int tmp = s.indexOf("-");
				if(tmp>=0){
					if(len==2){
						sb.append("-0.0").append(s.substring(1));
					}else if(len==3){
						sb.append("-0.").append(s.substring(1));
					}else{
						sb.append(s.substring(0, len-2)).append(".").append(s.substring(len-2));				
					}						
				}else{
					if(len==1){
						sb.append("0.0").append(s);
					}else if(len==2){
						sb.append("0.").append(s);
					}else{
						sb.append(s.substring(0, len-2)).append(".").append(s.substring(len-2));				
					}					
				}
			}else{
				sb.append("0.00");
			}
		}else{
			sb.append("0.00");
		}
		return sb.toString();		
	}
	/**
	 * 
	 * 功能描述：金额字符串转换：单位元转成单分
	 
	 * @param str 传入需要转换的金额字符串
	 * @return 转换后的金额字符串
	 */		
	public static String yuanToFen(Object o) {
		if(o == null)
			return "0";
		String s = o.toString();
		int posIndex = -1;
		String str = "";
		StringBuilder sb = new StringBuilder();
		if (s != null && s.trim().length()>0 && !s.equalsIgnoreCase("null")){
			posIndex = s.indexOf(".");
			if(posIndex>0){
				int len = s.length();
			    if(len == posIndex+1){
					str = s.substring(0,posIndex);
					if(str == "0"){
				    	str = "";
				    }
				    sb.append(str).append("00");
				}else if(len == posIndex+2){
				    str = s.substring(0,posIndex);
				    if(str == "0"){
				    	str = "";
				    }
				    sb.append(str).append(s.substring(posIndex+1,posIndex+2)).append("0");
				}else if(len == posIndex+3){
					str = s.substring(0,posIndex);
					if(str == "0"){
				    	str = "";
				    }
					sb.append(str).append(s.substring(posIndex+1,posIndex+3));
				}else{
					str = s.substring(0,posIndex);
					if(str == "0"){
				    	str = "";
				    }
					sb.append(str).append(s.substring(posIndex+1,posIndex+3));
				}
			}else{
				sb.append(s).append("00");
			}
		}else{
			sb.append("0");
		}
		str = removeZero(sb.toString());
		if(str != null && str.trim().length()>0 && !str.trim().equalsIgnoreCase("null")){
			return str;
		}else{
			return "0";
		}
	}
	
	
	/**
	 * 首字母变小写
	 */
	public static String firstCharToLowerCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'A' && firstChar <= 'Z') {
			char[] arr = str.toCharArray();
			arr[0] += ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	
	/**
	 * 首字母变大写
	 */
	public static String firstCharToUpperCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z') {
			char[] arr = str.toCharArray();
			arr[0] -= ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	
	/**
	 * 字符串为 null 或者为  "" 时返回 true
	 */
	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim());
	}
	
	/**
	 * 字符串不为 null 而且不为  "" 时返回 true
	 */
	public static boolean notBlank(String str) {
		return str != null && !"".equals(str.trim());
	}
	
	public static boolean notBlank(String... strings) {
		if (strings == null)
			return false;
		for (String str : strings)
			if (str == null || "".equals(str.trim()))
				return false;
		return true;
	}
	
	public static boolean notNull(Object... paras) {
		if (paras == null)
			return false;
		for (Object obj : paras)
			if (obj == null)
				return false;
		return true;
	}
	
	public static String toCamelCase(String stringWithUnderline) {
		if (stringWithUnderline.indexOf('_') == -1)
			return stringWithUnderline;
		
		stringWithUnderline = stringWithUnderline.toLowerCase();
		char[] fromArray = stringWithUnderline.toCharArray();
		char[] toArray = new char[fromArray.length];
		int j = 0;
		for (int i=0; i<fromArray.length; i++) {
			if (fromArray[i] == '_') {
				// 当前字符为下划线时，将指针后移一位，将紧随下划线后面一个字符转成大写并存放
				i++;
				if (i < fromArray.length)
					toArray[j++] = Character.toUpperCase(fromArray[i]);
			}
			else {
				toArray[j++] = fromArray[i];
			}
		}
		return new String(toArray, 0, j);
	}
	
	public static String join(String[] stringArray) {
		StringBuilder sb = new StringBuilder();
		for (String s : stringArray)
			sb.append(s);
		return sb.toString();
	}
	
	public static String join(String[] stringArray, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<stringArray.length; i++) {
			if (i>0)
				sb.append(separator);
			sb.append(stringArray[i]);
		}
		return sb.toString();
	}

}
