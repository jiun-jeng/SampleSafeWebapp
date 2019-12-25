package test;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

public class TestAntiSamy {
	private static final Log LOGGER = LogFactory.getLog(TestAntiSamy.class);
	
	@Test
	public void test1()throws Exception {
		String dirtyInput;		
		dirtyInput = "<div><b>'1中文'</b><script>alert('1中文');</script></div>";
		doCheck(dirtyInput);
	}
	
	@Test
	public void test2()throws Exception {
		String dirtyInput;
		dirtyInput = "2\" onmouseover=prompt(914270) bad=\"";
		doCheck(dirtyInput);
	}
	
	private static void doCheck(String dirtyInput)throws Exception{
		LOGGER.info("--");
		LOGGER.info("dirtyInput:"+dirtyInput);
		LOGGER.info("--");
		
		Policy policy = Policy.getInstance(new File("src/main/resources/antisamy-ebay-1.4.4.xml"));
		AntiSamy as = new AntiSamy(policy);
		
		// 偵測
		CleanResults cr = as.scan(dirtyInput);
		LOGGER.info("NumberOfErrors:"+cr.getNumberOfErrors());
		List<String> errors = cr.getErrorMessages();
		for(String error:errors){
			LOGGER.error(error);
		}
		// 淨化
		LOGGER.info("clean:"+cr.getCleanHTML());
	}

}
