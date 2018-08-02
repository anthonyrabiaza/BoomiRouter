package com.boomi.proserv.b2b.router;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MimeParser {
	
	Multipart multipart;

	public Multipart getMultipart() {
		return multipart;
	}

	public int getNumberParts() throws Exception {
		return multipart.getCount();
	}

	public MimeParser(String content) throws Exception {
		this(new ByteArrayInputStream(content.getBytes()));
	}
	
	public MimeParser(InputStream stream) throws Exception {
		Properties props = System.getProperties(); 
		Session session = Session.getInstance(props, null);
		MimeMessage message = new MimeMessage(session, stream);
		Object msgContent = message.getContent();

		if (msgContent instanceof Multipart) {
			multipart = (Multipart) msgContent;
		}
	}
	
	public InputStream getElement(int index) throws Exception {
		return multipart.getBodyPart(index).getInputStream();
	}

}
