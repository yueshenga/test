package com.example.demo.tool;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GetWord {

    //读取WORD文件
    public static String readDoc(String path) throws IOException {
        String result = "";
        //首先判断文件中的是doc/docx
        try {
            if (path.endsWith(".doc")) {
                InputStream is = new FileInputStream(path);
                WordExtractor re = new WordExtractor(is);
                result = re.getText();
                re.close();
            } else if (path.endsWith(".docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(path);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                result = extractor.getText();
                extractor.close();
            } else {
                System.out.println("此文件不是word文件");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        String readDoc = null;
        try {
            readDoc = readDoc("D:\\myworkplace\\test\\src\\main\\resources\\static\\other\\湖北省财政厅预算管理一体化项目公司成员简历-北京大数元-万胜.docx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(readDoc);
    }
}
