package com.m.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.m.util.InputCheckUtil;
import com.m.util.RequestUtil;

/**
 * 統一過濾web參數防止xss攻擊,黑箱修正使用
 *

	<filter>
		<filter-name>XssFilter</filter-name>
		<filter-class>com.m.filter.XssFilter</filter-class>
		<init-param>
			<param-name>warningPage</param-name>
			<param-value></param-value><!-- 警示頁面位置 -->
		</init-param>
		<init-param>
			<param-name>FILTER_MODE</param-name>
			<param-value>false</param-value><!-- true: 過濾模式，false: 阻斷模式 -->
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>XssFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>	

 */
public class XssFilter implements Filter {
	private static Log LOGGER = LogFactory.getLog(XssFilter.class);
	
	private static final String FLAG_REENTRY = XssFilter.class.getName(); // 避免重入
	private boolean isFilterMode = false;
	private String warningPage = "";

	public void init(FilterConfig config) throws ServletException {
		String filterMode = config.getInitParameter("FILTER_MODE"); // 阻斷模式(false), 過濾模式(true)
		if (filterMode != null) {
			isFilterMode = Boolean.valueOf(filterMode).booleanValue();
		}
		String warningPageParam = config.getInitParameter("warningPage");
		if(InputCheckUtil.notEmptyString(warningPageParam)){
			warningPage = warningPageParam;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			ServletRequest targetRequest = httpRequest;

			if (httpRequest.getAttribute(FLAG_REENTRY) == null) {
				httpRequest.setAttribute(FLAG_REENTRY, Boolean.TRUE);
				if("true".equals(httpRequest.getAttribute("EscapeXssFilter"))){
					// 跳脫檢查
					LOGGER.debug("跳脫檢查:"+httpRequest.getRequestURI());
				}				
				else if (!isFilterMode) { // 阻斷模式
					//LOGGER.debug("uri:"+httpRequest.getRequestURI());

					Enumeration<?> e = httpRequest.getParameterNames();
					while (e.hasMoreElements()) {
						String name = (String) e.nextElement();
						String value = httpRequest.getParameter(name);
						//LOGGER.debug(String.format("%s %s",name,value));

						RequestUtil requestUtil = new RequestUtil(httpRequest);
						if (requestUtil.haveXSS(value)) {
							LOGGER.info("input invalid:"+InputCheckUtil.validateLogString(value));
							if(InputCheckUtil.notEmptyString(warningPage)){
								// 導向警示頁
								((HttpServletResponse)response).sendRedirect(requestUtil.getContextPath()+warningPage);	
							}
							else{
								// 顯示警示訊息
								response.setContentType("text/html;charset=utf-8");
								response.getWriter().write("輸入不合法，有XSS攻擊字元");
							}						

							return;
						}
					}
				} else { // 過濾模式
					// 過濾模式下tomcat有用到RequestDispatcher include的地方會失效
					// 要額外調整RequestDispatcher include的地方由RequestUtil取得原request才能正常include
					targetRequest = new RequestUtil(httpRequest);
				}
			}

			chain.doFilter(targetRequest, response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		} 
	}

	public void destroy() {
	}

}
