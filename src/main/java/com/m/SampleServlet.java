package com.m;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.m.util.InputCheckUtil;
import com.m.util.RequestUtil;

/**
 * 白箱弱點修正範例
 *
 */
public class SampleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Log LOGGER = LogFactory.getLog(SampleServlet.class);

	private static String dbUsername;
	private static String dbPassword;
	static{
		try{
			Properties prop = new Properties();
			prop.load(SampleServlet.class.getResourceAsStream("/config.properties"));
			dbUsername = prop.getProperty("dbUsername");
			dbPassword = prop.getProperty("dbPassword");
		}
		catch(Exception e){
			LOGGER.error(e.getMessage());
		}
	}
	
	public SampleServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		try{			
			HttpSession session = request.getSession();

			RequestUtil requestUtil = new RequestUtil(request);
			
			//--[DataSource1]網站請求 Web Request[fix]
			call(requestUtil.getParameter("username"),out,request,response,session);
			
			//--[DataSource4]檔案 File
			call(FileUtils.readFileToString(new File("")),out,request,response,session);
			
			//--[DataSource7]資料庫 Database
			Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/crm", dbUsername, dbPassword);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("");
			call(rs.getString(1),out,request,response,session);
			rs.close();
			stmt.close();
			connection.close();						
			
			//--[DataSource11]System Environment 
			call(System.getenv("username"),out,request,response,session);
			
			//--[DataSource13]Session[fix]
			call((String)requestUtil.getSessionAttribute("username"),out,request,response,session);
			
			//----------------------------------------//

			//--[defect8]明文密碼缺失  Hard-Coded Password[fix]
			DriverManager.getConnection("", dbUsername, dbPassword);
			
			//--[defect14]不安全的密碼演算法  Risky Cryptographic Algorithm[fix]
			MessageDigest digester = MessageDigest.getInstance("SHA-256");
			digester.reset();
			digester.update("test".getBytes());			
			byte[] digest = digester.digest();
						
			//--[defect15]不安全的亂數  Insecure Randomness[fix]
			//Random ranGen = new Random();
			SecureRandom ranGen = new SecureRandom();
			ranGen.setSeed((new Date()).getTime());
			int value = ranGen.nextInt();
			out.println(value);
		}
		catch(Exception e){
			//--[defect11]Information Leak of System Data[fix]
			//e.printStackTrace(new PrintStream(out));
			//out.println(e.getMessage());
			//--改用common log統一輸出錯誤訊息, 不將錯誤訊息印到畫面上
			LOGGER.error(e.getMessage(),e);
			
			//--[defect19]Information Leak Through Log Files
			LOGGER.debug("Error: Unable to create network folders dir with path " + System.getProperty("user.home"));
		}
	}
	
	private void call(String untrustedValue,ServletOutputStream out,HttpServletRequest request, HttpServletResponse response,HttpSession session) throws Exception{
		
		//--[defect1]Reflection Injection			
		String ctl = untrustedValue;
		Class cmdClass = Class.forName(ctl + "Command");
		Object ao = cmdClass.newInstance();
		
		//--[defect2]Cross-Site Scripting[fix]
		RequestUtil requestUtil = new RequestUtil(request);
		String username = untrustedValue;
		out.print(requestUtil.replaceXSS(username));
		
		out.print(requestUtil.getRequestURI());
		
		//--[defect3]HTTP Response Splitting[fix]
		String display = untrustedValue;
		Cookie cookie = new Cookie("display",InputCheckUtil.chkCrLfInjection(display));
		response.addCookie(cookie);	
		
		String username2 = untrustedValue;
		String password = untrustedValue;
		
		//--[defect4]XPath Injection[fix]
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile("//users/user[login/text()='" +
				InputCheckUtil.chkXPath(username2) + "' and password/text()='" + InputCheckUtil.chkXPath(password) + "' ]");
		
		DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory1.newDocumentBuilder();;
        Document doc = builder.parse("smartphone.xml");
		expr.evaluate(doc, XPathConstants.NODESET);
		
		//--[defect5]Resource Injection[fix]
		String rName = untrustedValue;
		File rFile = new File(InputCheckUtil.chkFilePath("/usr/local/apfr/reports/" + rName));
		rFile.mkdir();
		
		//--[defect6]SQL Injection[fix]
		// 修改1
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/crm", "root", "1234");
		
		String query = "SELECT * FROM USERS WHERE " + "" +
				"username = ? AND password = ? ";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1,username2);
		stmt.setString(2,password);
		ResultSet rs = stmt.executeQuery();
		rs.close();
		stmt.close();
		
		// 修改2
		// 修改2並不保險, 可參考不含單引號空白之攻擊sql語法		
		String query1 = "SELECT * FROM USERS WHERE " + "" +
				"username = '" + InputCheckUtil.chkSqlInjection(username2) + "' AND password = '" + InputCheckUtil.chkSqlInjection(password) + "'";
		Statement stmt1 = connection.createStatement();
		ResultSet rs1 = stmt1.executeQuery(query1);
		rs1.close();
		stmt1.close();
		
		connection.close();
		
		//--[defect12]LDAP注入  LDAP Injection
		Hashtable<String, String>  env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		DirContext dctx = new InitialDirContext(env);

		SearchControls sc = new SearchControls();
		String[] attributeFilter = {"cn", "mail"};
		sc.setReturningAttributes(attributeFilter);
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String base = "dc=example,dc=com";
		String filter = "(&(sn=" + username2 + ")(userPassword=" + password + "))";			 
		NamingEnumeration<?> results = dctx.search(base, filter, sc);
		
		//--[defect13]Open Redirect
		String url3 = untrustedValue;
		response.sendRedirect(url3);
		
		//--[defect16]Log Forging
		String token = untrustedValue;
		LOGGER.info(token);
		
		//--[defect18]Session Variable Poisoning		
		String url = untrustedValue;
		session.setAttribute("source", url);

		String url2 = (String)session.getAttribute("source");
		//response.sendRedirect(url2);														
		
		// 深度 2
		SampleUtil.call2(untrustedValue,out,response,session);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
