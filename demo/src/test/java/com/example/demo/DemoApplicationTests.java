package com.example.demo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParser;
import com.jayway.jsonpath.JsonPath;

import bsh.EvalError;
import bsh.Interpreter;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void beanShell() {
	    String expression = "1==1 && 2==3";
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("a", "1");
	    map.put("b", "2");
	    map.put("c", "3");
	    expression = "map.get(\"a\") == \"1\" && map.get(\"b\") == \"2\"";
	    Interpreter interpreter = new Interpreter();
	    try {
	        interpreter.set("map", map);
            Object ret = interpreter.eval(expression);
            System.out.println(ret);
            return;
        } catch (EvalError e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void testJson() throws FileNotFoundException {
//	    JSONObject jsonObject = JSONObject.parseObject("{\n" + 
//	            "    \"phone\": [\n" + 
//	            "        \"12345678\", \n" + 
//	            "        \"87654321\"\n" + 
//	            "    ], \n" + 
//	            "    \"name\": \"xiaoming\", \n" + 
//	            "    \"age\": 100, \n" + 
//	            "    \"address\": {\n" + 
//	            "        \"country\": \"china\", \n" + 
//	            "        \"province\": \"jiangsu\"\n" + 
//	            "    }, \n" + 
//	            "    \"married\": false, \n" + 
//	            "    \"members\": [\n" + 
//	            "        {\n" + 
//	            "            \"brother\": \"jack\"\n" + 
//	            "        }, \n" + 
//	            "        {\n" + 
//	            "            \"sister\": \"alis\"\n" + 
//	            "        }\n" + 
//	            "    ]\n" + 
//	            "}");
//	    jsonObject.get("members");
	}
	
	@Test
	public void strToJavaExp() {
	    String str = "#{data.content[1].$value == 你好 萝卜}";
        String exp = "";
        if (str.startsWith("#{")) {
            str = str.substring(2, str.length() - 1);
        }
        System.out.println(exp);
    }
	
	@Test
	public void testLocalDateTime() {
	    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    LocalDateTime time = LocalDateTime.now();
	    String localTime = df.format(time);
	    LocalDateTime ldt = LocalDateTime.parse("2017-09-28 17:07:05",df);
	    System.out.println("LocalDateTime转成String类型的时间："+localTime);
	    System.out.println("String类型的时间转成LocalDateTime："+ldt);
	}
	
	@Test
    public void testXPath() throws DocumentException {
	    String xpathExpression = "/soap:Envelope/soap:Body";
	    String body = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><getDomesticAirlinesTimeResponse xmlns=\"http://WebXml.com.cn/\"><getDomesticAirlinesTimeResult><xs:schema id=\"Airlines\" xmlns=\"\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\"><xs:element name=\"Airlines\" msdata:IsDataSet=\"true\" msdata:UseCurrentLocale=\"true\"><xs:complexType><xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\"><xs:element name=\"AirlinesTime\"><xs:complexType><xs:sequence><xs:element name=\"Company\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"AirlineCode\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"StartDrome\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"ArriveDrome\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"StartTime\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"ArriveTime\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"Mode\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"AirlineStop\" type=\"xs:string\" minOccurs=\"0\" /><xs:element name=\"Week\" type=\"xs:string\" minOccurs=\"0\" /></xs:sequence></xs:complexType></xs:element></xs:choice></xs:complexType></xs:element></xs:schema><diffgr:diffgram xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\" xmlns:diffgr=\"urn:schemas-microsoft-com:xml-diffgram-v1\"><Airlines xmlns=\"\"><AirlinesTime diffgr:id=\"AirlinesTime1\" msdata:rowOrder=\"0\"><Company>中国国航</Company><AirlineCode>CA1335</AirlineCode><StartDrome>北京首都国际机场</StartDrome><ArriveDrome>南宁吴圩国际机场</ArriveDrome><StartTime>07:20</StartTime><ArriveTime>10:40</ArriveTime><Mode>321</Mode><AirlineStop>0</AirlineStop><Week>123456日</Week></AirlinesTime></Airlines></diffgr:diffgram></getDomesticAirlinesTimeResult></getDomesticAirlinesTimeResponse></soap:Body></soap:Envelope>";
        Document doc = DocumentHelper.parseText(body );
        List<?> ret1 = doc.getRootElement().elements();
        List<?> ret2 = doc.selectNodes(xpathExpression);
        System.out.println(ret1);
        System.out.println(ret2);
    }
	
	@Test
    public void testJsonPath() {
	    String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";
	    Object ret1 = JsonPath.read(json, "$.store.book[*].author");
	    System.out.println(ret1);
	}
	
	@Test
	public void test1() {
	    Map<String, Object> map = new HashMap<String, Object>();
	    
	}
}
