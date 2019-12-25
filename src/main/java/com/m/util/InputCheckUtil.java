package com.m.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.WindowsCodec;

/**
 * 輸入過濾工具
 *
 */
public class InputCheckUtil {
	private static final Log LOGGER = LogFactory.getLog(InputCheckUtil.class);
	
	public static String chkSqlInjection(String s){
		// 跳脫單引號以防止sql injection
		if(s.indexOf("'")>-1){
			LOGGER.warn("user input ' in sql");
			s=s.replaceAll("'","''");
		}
		return s;
	}
	
	public static String chkFreeSql(String s){
		return s;
	}
	
	public static String chkCrLfInjection(String s){
		// 移除換行字元以避免http header假造
        if(s.indexOf('\n')>-1 || s.indexOf('\r')>-1){
            LOGGER.warn("cr lf in input ");
            s=s.replace('\n',' ').replace('\r',' ');
        }
        return s;
    }
	
	public static String chkFilePath(String input) throws Exception{
		// 偵測回上一層字元以避免任意路徑存取
		if(input.indexOf( ".." ) != -1 ){
        	LOGGER.warn(input);
        	throw new Exception("file path invalid:"+input);
		}
		else{
			return input;
		}
	}
	
	public static String validateLogString(String input) {
		if(input != null){
			String escapedToken = input.replace('\n', ' ').replace('\r', ' ');
		    escapedToken = StringEscapeUtils.escapeHtml4(escapedToken);
		    return escapedToken;
		}
		else{
			return null;
		}
	}
	
	public static String chkWinCommand(String input){
		// 防止command injection
		return ESAPI.encoder().encodeForOS(new WindowsCodec(),input);
	}
	
	public static String chkXPath(String input){
		// 防止xpath injection
		return  ESAPI.encoder().encodeForXPath(input);
	}
	
	public static boolean notEmptyString(String s) {
		if (s != null && !s.equals("")) {
			return true;
		} else {
			return false;
		}
	}
}
