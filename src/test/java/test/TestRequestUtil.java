package test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.m.util.RequestUtil;

public class TestRequestUtil {
	private static final Log LOGGER = LogFactory.getLog(TestRequestUtil.class);
	
	@Test
	public void test1()throws Exception {
		String dirtyInput = "2\" onmouseover=prompt(914270) bad=\"";
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("p1",dirtyInput);
		RequestUtil requestUtil = new RequestUtil(request);
		// 偵測
		LOGGER.info("haveXSS:"+requestUtil.haveXSS(dirtyInput));
		// 淨化
		LOGGER.info("clean:"+requestUtil.getParameter("p1"));

	}

}
