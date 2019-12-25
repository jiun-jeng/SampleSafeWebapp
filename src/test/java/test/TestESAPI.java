package test;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.owasp.esapi.ESAPI;

public class TestESAPI {
	private static final Log LOGGER = LogFactory.getLog(TestESAPI.class);
	
	private String dirtyInput = "<div><b>'1中文'</b><script>alert('1中文');</script></div>";
	
	@Test
	public void test1()throws Exception {
		// 偵測
		LOGGER.info("is valid?:"+ESAPI.validator().isValidInput("",dirtyInput,"HTTPParameterValue",2000,true));
	}
	
	@Test
	public void test2()throws Exception {
		// 淨化
		LOGGER.info(ESAPI.validator().getValidInput("",dirtyInput,"HTTPParameterValue",2000,true));
	}
	
	@Test
	public void test3()throws Exception {
		// 淨化
		LOGGER.info(ESAPI.encoder().encodeForHTML(dirtyInput));
	}
	
	@Test
	public void test4()throws Exception {
		// 淨化
		LOGGER.info(StringEscapeUtils.escapeHtml4(dirtyInput));
	}

}
