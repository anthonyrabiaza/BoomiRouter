package com.boomi.proserv.b2b.router;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

class TestMimeParser {

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	@Test
	void test() {
		MimeParser mimeParser;
		try {
			mimeParser = new MimeParser(TestMimeParser.readFile(new File("").getAbsoluteFile().toString() + "/test/RN.xml", Charset.defaultCharset()));
			if(mimeParser.getNumberParts()==4) {
				Document doc = BoomiRouterUtils.parse(mimeParser.getElement(1));
				assertEquals("123456787", BoomiRouterUtils.getFirstNodeTextContent(doc, "/DeliveryHeader/messageReceiverIdentification/PartnerIdentification/GlobalBusinessIdentifier"));
			}
		} catch (Exception e) {
			fail(e);
		}
		
	}

}
