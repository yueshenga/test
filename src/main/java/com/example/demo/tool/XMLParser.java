package com.example.demo.tool;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLParser {

    //Dom解析xml数据
    public static void DomParserXML(){
        try {
            //1 创建DocumentBuilderFactory对象，可以获得dom树的解析器
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            //2 创建通过工程对象，创建DocumentBuilder对象
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            //3 创建File对象，封装需要解析的xml文件
            File f=new File("D:\\myworkplace\\test\\src\\main\\resources\\static\\xml\\search.xml");
            //4 将文件加载进内容，产生dom树对象，获得了需要解析的xml文件对象
            Document document = documentBuilder.parse(f);
            //获得document对象的所有子节点
            NodeList childNodes = document.getChildNodes();
            //获得childNodes集合中的一个节点，文档中的根节点
            Node nodeList = childNodes.item(0);
            //获得node节点的名字
            String nodeName = nodeList.getNodeName();
            //获得node节点的类型(1 元素节点(标签) 3 表示文本节点有值的)
            short nodeType = nodeList.getNodeType();
            //3获得node节点的value(null)
            String nodeValue = nodeList.getNodeValue();
            System.out.println(nodeName+"\t"+nodeType+"\t"+nodeValue);
            //获得search的子节点,有文档结构的兼容性问题，dom认为空的换行也是节点
            NodeList search = nodeList.getChildNodes();
            //遍历search的子节点
            for(int x=0;x<search.getLength();x++) {
                Node n=search.item(x);
                //判断当前遍历的节点是否为空的换行节点
                if(n.getNodeType()!=3) {
//                    System.out.println(n.getNodeType()+"\t"+n.getNodeName());
                    //获得n标签的id属性的值
                    Element e=(Element)n;
                    String id = e.getAttribute("id");

                    System.out.print(id+"\t");
                    //获得n表示的search的所有子节点
                    NodeList info = n.getChildNodes();
//                    System.out.println(info.getLength());
                    //遍历info及节点对象集合，遍历出来的是学生的信息的标签
                    for(int y=0;y<info.getLength();y++) {
                        Node d=info.item(y);
                        if(d.getNodeType()!=3) {
                            //System.out.println(d.getNodeType()+"\t"+d.getNodeName());
                            //获得标签中间的内容
                            String value = d.getTextContent();
                            System.out.print(value+"\t");
                        }
                    }
                    //换行
                    System.out.println();
                }
            }
        }catch (IOException | ParserConfigurationException | SAXException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DomParserXML();
    }
}