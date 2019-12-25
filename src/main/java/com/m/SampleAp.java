package com.m;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.m.util.InputCheckUtil;

/**
 * 白箱弱點修正範例
 *
 */
public class SampleAp {
	private static final Log LOGGER = LogFactory.getLog(SampleAp.class);
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ScriptException 
	 */
	public static void main(String[] args) throws IOException, ScriptException {
		//--[defect7]Command Injection[fix]
		//--find "test" c:/tmp/test.txt &  dir
		if(args.length <= 0) {
			System.out.println("No arguments");
			System.exit(1);
		}
		
		Runtime runtime = Runtime.getRuntime();
		String cmd = "find \"test\" " + " " + InputCheckUtil.chkWinCommand(args[0]);
		LOGGER.info(cmd);
		Process process = runtime.exec(cmd); 
		
		String encoding = "ms950";
		MsgGrabber grabber = new MsgGrabber(process.getInputStream(),encoding);
		MsgGrabber errorGrabber = new MsgGrabber(process.getErrorStream(),encoding);
		
		grabber.start();
		errorGrabber.start();
			
		int exitVal = -1;
		try {
			exitVal = process.waitFor();
		} catch (InterruptedException ex) {
			LOGGER.error(ex.getMessage(),ex);
		}
		LOGGER.debug("exitValue: " + exitVal);
		
		grabber.stopAction();
		errorGrabber.stopAction();	
		
		LOGGER.debug(grabber.getMsg());
		LOGGER.debug(errorGrabber.getMsg());	

		//--[defect]Code Injection(Command Injection)[fix]
		//--hello.');print('abc.
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		String content = "print('"+ InputCheckUtil.chkWinCommand(args[0]) + "')";
		LOGGER.info(content);
		System.out.println(engine.eval(content));
		
	}

}
