package com.mapbar.info.collection.util;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * 解析XML数据
 * 电子眼类型
 * @author miaowei
 *
 */
public class ParseXmlService {
	/**
	 * 解析电子眼类型数据
	 * @param inStream 
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> parseXml(InputStream inStream) throws Exception  
    {  
        HashMap<String, String> hashMap = new HashMap<String, String>();  
        // 实例化一个文档构建器工厂  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        // 通过文档构建器工厂获取一个文档构建器  
        DocumentBuilder builder = factory.newDocumentBuilder();  
        // 通过文档通过文档构建器构建一个文档实例  
        Document document = builder.parse(inStream);  
        //获取XML文件根节点  
        Element root = document.getDocumentElement();  
        //获得所有子节点  
        NodeList childNodes = root.getChildNodes();  
        for (int j = 0; j < childNodes.getLength(); j++)  
        {  
            //遍历子节点  
            Node childNode = (Node) childNodes.item(j); 
            //类型为元素节点
            if (childNode.getNodeType() == Node.ELEMENT_NODE)  
            { 
                Element childElement = (Element) childNode; 
                //
                if ("name".equals(childElement.getNodeName()))  
                {  
                	LogPrint.Print("childElement.getNodeValue()===="+j+childElement.getFirstChild().getNodeValue());
                    hashMap.put(""+childElement.getAttribute("key"),childElement.getFirstChild().getNodeValue());  
                }  
            }  
        }  
        return hashMap;  
    }  
}
