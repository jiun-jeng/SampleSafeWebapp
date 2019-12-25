package com.m.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 跳過XssFilter檢查
 *

	<filter>
		<filter-name>EscapeXssFilter</filter-name>
		<filter-class>com.m.filter.EscapeXssFilter</filter-class>		
	</filter>
	<filter-mapping>
		<filter-name>EscapeXssFilter</filter-name>
		<url-pattern>/some-url</url-pattern>
	</filter-mapping>	

 */
public class EscapeXssFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {		
		request.setAttribute("EscapeXssFilter","true");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}

}
