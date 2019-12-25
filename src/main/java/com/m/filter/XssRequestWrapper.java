package com.m.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.owasp.validator.html.PolicyException;

import com.m.util.RequestUtil;

public class XssRequestWrapper extends HttpServletRequestWrapper {

	private boolean uploadMode = false;
	private RequestUtil requestUtil = null;

	public XssRequestWrapper(HttpServletRequest request) throws PolicyException {
		super(request);

		String contentType = request.getContentType();
		if (contentType != null) {
			this.uploadMode = contentType.startsWith("multipart/form-data");	
		}
		
		this.requestUtil = new RequestUtil(request);
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (!uploadMode) {
			if (value != null) {
				value = requestUtil.replaceXSS(value);
			}
		}
		return value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<?,?> getParameterMap() {
		Map<String,String> map = super.getParameterMap();
		if (!uploadMode && map !=null) {
			for (Iterator<?> i = map.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				String value = requestUtil.replaceXSS((String) map.get(key));
				map.put(key, value);
			}
		}
		return map;
	}

	@Override	
	public String[] getParameterValues(String name) {
		String original[] = super.getParameterValues(name);
		String rv[] = original;
		if (!uploadMode && original != null) {
			List<String> values = new ArrayList<String>();
			for (int i = 0; i < original.length; i++) {
				values.add(requestUtil.replaceXSS(original[i]));
			}
			rv = (String[]) values.toArray(new String[0]);
		}

		return rv;
	}

}
