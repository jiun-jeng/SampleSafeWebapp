package com.m.util;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.filters.SecurityWrapperRequest;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * web參數xss過濾工具
 * 使用esapi SecurityWrapperRequest避免直接存取HttpServletRequest
 * 輸入過濾部分改使用owasp AntiSamy處理以避免esapi過於嚴格的過濾行為
 *
 */
public class RequestUtil extends HttpServletRequestWrapper {
	private static final Log LOGGER = LogFactory.getLog(RequestUtil.class);

	private SecurityWrapperRequest secureRequest;
	private HttpServletRequest originalRequest;

	private AntiSamy as;

	public RequestUtil(HttpServletRequest httpRequest) throws PolicyException{		
		super(httpRequest);
		//LOGGER.debug(httpRequest.getClass().getName());
		if(httpRequest instanceof RequestUtil){
			// XssFilter過濾模式下,避免兩次wrap
			RequestUtil util = (RequestUtil)httpRequest;
			this.originalRequest = util.getRequest();
			this.secureRequest = (SecurityWrapperRequest)util.getSecureRequest();
			as = util.getAntiSamy();			
		}
		else{
			this.originalRequest = httpRequest;
			this.secureRequest = new SecurityWrapperRequest(httpRequest);
	
			String root = httpRequest.getSession().getServletContext().getRealPath("/");
			Policy policy = Policy.getInstance(new File(root+"/WEB-INF/antisamy-ebay-1.4.4.xml"));
			as = new AntiSamy(policy);
		}
	}

	public HttpServletRequest getSecureRequest(){
		return secureRequest;
	}
	
	public HttpServletRequest getRequest(){
		return originalRequest;
	}
	
	public AntiSamy getAntiSamy(){
		return as;
	}

	public String getHeader(String name){
		return secureRequest.getHeader(name);
	}

	public String getParameter(String pname){
		//String value = secureRequest.getParameter(pname);
		//return value;	

		String value = originalRequest.getParameter(pname);
		if(value == null){			
			return null;
		}		
		else{
			return getCleanValue(value);
		}
	}

	private String getCleanValue(String value) {
		if(value != null){
			try{
				CleanResults cr = as.scan(value);
				if(cr.getNumberOfErrors() > 0){
					LOGGER.info("input invalid:"+value);
					return cr.getCleanHTML();
				}
				else{
					if(havePartialScript(value)){
						LOGGER.info("input invalid:"+value);
						return replacePartialScript(value);
					}
					else{
						return value;
					}
				}
			}
			catch(Exception e){
				LOGGER.error(e.getMessage(),e);
			}
		}
		
		return "";
	}
	
	public boolean havePartialScript(String value){
		if(value != null){
			if(value.contains("=") && (
			   value.contains("src") || value.contains("onload") || value.contains("onmouseover") ||
			   value.contains("onkeypress") || value.contains("onfocus")
			)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public String replacePartialScript(String value){
		if(value != null){
			return value.replace("=","").
					replace("src","").replace("onload","").
					replace("onmouseover","").replace("onkeypress","").replace("onfocus","");
		}
		else{
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<?,?> getParameterMap() {
		Map<String,String> map = super.getParameterMap();
		
		for (Iterator<?> i = map.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = getCleanValue((String) map.get(key));
			map.put(key, value);
		}
			
		return map;
	}
	
	public String[] getParameterValues(String name){
		String[] values = originalRequest.getParameterValues(name);
		if(values != null){
			ArrayList<String> list = new ArrayList<String>();
			for(String value:values){
				list.add(getCleanValue(value));
			}
			
			String[] result = new String[list.size()];
			list.toArray(result);
			return result;
		}
		else{
			return values;
		}
	}

	public HttpSession getSession(){		
		return secureRequest.getSession();
	}

	public Object getSessionAttribute(String name){		
		Object value = originalRequest.getSession().getAttribute(name);
		if(value == null){
			return null;
		}
		else if(value instanceof String){						
			return getCleanValue((String)value);			
		}		
		else{
			return value;
		}
	}

	public Enumeration<?> getParameterNames(){
		return secureRequest.getParameterNames();
	}

	public Object getAttribute(String name){
		//Object value = secureRequest.getAttribute(name);
		//return value;

		Object value = originalRequest.getAttribute(name);
		if(value == null){
			return null;
		}
		else if(value instanceof String){						
			return getCleanValue((String)value);			
		}		
		else{
			return value;
		}
	}

	public void setAttribute(String name, Object o){		
		secureRequest.setAttribute(name,o);
	}

	public String getContextPath(){
		return secureRequest.getContextPath();				
	}

	public void setCharacterEncoding(String env) throws java.io.UnsupportedEncodingException{
		secureRequest.setCharacterEncoding(env);
	}

	public String getServerName(){
		return secureRequest.getServerName();
	}

	public int getServerPort(){
		return secureRequest.getServerPort();
	}

	public String getRemoteAddr(){
		return secureRequest.getRemoteAddr();
	}

	public String getRequestURI(){
		return secureRequest.getRequestURI();
	}

	public String getQueryString(){	
		try {
			String str = originalRequest.getQueryString();	
			if(str != null){
				str = URLDecoder.decode(str,"utf-8");
				str = getCleanValue(str);
				return URLEncoder.encode(str,"utf-8");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return "";
	}

	public boolean isSecure(){
		return secureRequest.isSecure();
	}

	public RequestDispatcher getRequestDispatcher(String path){
		// 使用原request以避免esapi限制path位置只能在web-inf下
		return originalRequest.getRequestDispatcher(path);		
	}

	public void removeAttribute(String name){
		secureRequest.removeAttribute(name);
	}

	public boolean haveXSS(String input) throws ScanException, PolicyException{
		// 檢查是否有xss攻擊字元
		CleanResults cr = as.scan(input);
		if(cr.getNumberOfErrors() > 0){
			return true;
		}
		else if(havePartialScript(input)){
			return true;
		}
		else{
			return false;
		}
	}

	public String replaceXSS(String input){	
		// 移除xss攻擊字元
		return getCleanValue(input);
	}
}
