package com.boomi.proserv.b2b.router;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import com.boomi.document.scripting.DataContextImpl;
import com.boomi.execution.ExecutionUtil;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class BoomiRouter {

	private static final String AUTHORIZATION_NAME 				= "Authorization";
	private static final String AUTHORIZATION_VALUE_PREFIX 		= "Basic ";
	private static final String AUTHORIZATION_VALUE_SEPARATOR 	= ":";
	
	private static final String EMPTY 							= "";
	
	private static final String POST_METHOD 					= "POST";
	private static final String IN_HEADER 						= "document.dynamic.userdefined.inheader_";

	public void handle(DataContextImpl dataContext) throws Exception {

		InputStream stream 		= dataContext.getStream(0);
		Properties props 		= dataContext.getProperties(0);
		String streamContent	= BoomiRouterUtils.inputStreamToString(stream);

		MimeParser mimeParser 	= new MimeParser(streamContent);

		Document doc 			= BoomiRouterUtils.parse(mimeParser.getElement(Integer.valueOf(BoomiRouter.getProperties().getProperty(BoomiRouter.class.getName() + ".ID.elementNumber"))));
		String duns 			= BoomiRouterUtils.getFirstNodeTextContent(doc, BoomiRouter.getProperties().getProperty(BoomiRouter.class.getName() + ".ID.xpath"));
		String targetHost 		= BoomiRouter.getProperties().getProperty(BoomiRouter.class.getName() + ".mapping." + duns + ".target_host");
		String operation 		= BoomiRouter.getProperties().getProperty(BoomiRouter.class.getName() + ".mapping." + duns + ".operation");
		String user 			= BoomiRouter.getProperties().getProperty(BoomiRouter.class.getName() + ".mapping." + duns + ".user");
		String password 		= BoomiRouter.getProperties().getProperty(BoomiRouter.class.getName() + ".mapping." + duns + ".password");
		String url 				= "http://" + targetHost + "/b2b/" + operation;

		getLogger().info("DUNS found : " + duns);
		getLogger().info("Target URL : " + url);
		
		HttpURLConnection post 	= (HttpURLConnection) new URL(url).openConnection();

		post.setRequestMethod(POST_METHOD);
		post.setDoOutput(true);
		
		//Pass-through of HTTP Headers
		for (String key : props.keySet().toArray(new String[0])) {
			getLogger().fine("Found Property : " + key + " with value " + props.getProperty(key));
			if(key.startsWith(IN_HEADER)) {
				post.setRequestProperty(key.replace(IN_HEADER, EMPTY), props.getProperty(key));
			}
		}
		
		//B2B Requires Auth
		post.setRequestProperty(AUTHORIZATION_NAME, AUTHORIZATION_VALUE_PREFIX + Base64.encode((user + AUTHORIZATION_VALUE_SEPARATOR + password).getBytes()));
		
		post.getOutputStream().write(streamContent.getBytes("UTF-8"));
		int responseCode 		= post.getResponseCode();
		
		//logger.info("Response code: " + postRC)
		if(responseCode >= 200 && responseCode <= 299) {
			//logger.info("Response: " + post.getInputStream().getText())
			dataContext.storeStream(post.getInputStream(), props);
			getLogger().info("Called succesfully delegated");
		} else {
			getLogger().severe("Error during the call. Response is: " + BoomiRouterUtils.inputStreamToString(post.getInputStream()));
		}
		
	}

	private static Properties getProperties() {
		Properties properties = new Properties();
		try {
			String location = new File(EMPTY).getAbsoluteFile().toString() + "/conf/";
			properties.load(new FileInputStream(location+ "boomirouter.properties"));
		} catch (IOException e) {
			getLogger().warning(e.getMessage());
		}
		return properties;
	}

	private static Logger getLogger() {
		try {
			return ExecutionUtil.getBaseLogger();
		} catch (Exception e){
			return Logger.getLogger(BoomiRouter.class.getName());
		}
	}
}
