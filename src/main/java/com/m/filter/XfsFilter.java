package com.m.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.m.util.InputCheckUtil;

/**
 *  防止Cross-Frame Scripting (XFS) iframe攻擊
 *

 	<filter>
		<filter-name>XfsFilter</filter-name>
		<filter-class>com.m.filter.XfsFilter</filter-class>
		<init-param>
			<param-name>options</param-name>
			<param-value>SAMEORIGIN</param-value>
			<!--  
			· DENY
				Deny all attempts to frame the page
			· SAMEORIGIN
				The page can be framed by another page only if it belongs to the same origin as the page being framed
			· ALLOW-FROM origin
				Developers can specify a list of trusted origins in the origin attribute. 
			-->
		</init-param>		
	</filter>
	<filter-mapping>
		<filter-name>XfsFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>		
 
    對不支援X-Frame-Options之舊版本瀏覽器可採client端xfs防護
 if (top != self) {
   top.location=self.location;
 }
 
 *
 */
public class XfsFilter implements Filter {

	private String options = "DENY";
	
	public void init(FilterConfig config) throws ServletException {
		String optionsParam = config.getInitParameter("options");
		if(InputCheckUtil.notEmptyString(optionsParam)){
			options = optionsParam;
		}
	}
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		httpResponse.setHeader("X-Frame-Options",options);
		chain.doFilter(request, response);
	}
	
	public void destroy() {
	}
	
}
