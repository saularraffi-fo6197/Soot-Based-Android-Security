package edu.cs.wayne;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser {
	
	public static void printFunctionsAndParams() { 
		
		File xmlFile = new File("functionsList.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			NodeList functionList = document.getElementsByTagName("function");
			
			for (int i = 0; i < functionList.getLength(); i++) {
				Node funcNode = functionList.item(i);
				Element func = (Element) funcNode;
				System.out.println("Function: " + func.getAttribute("name"));
				
				NodeList paramList = func.getElementsByTagName("param");
				
				for (int j = 0; j < paramList.getLength(); j++) {
					Node paramNode = paramList.item(j);
					Element param = (Element) paramNode;
					System.out.println("   *Param: " + param.getAttribute("name"));
				}
				System.out.println("");
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
