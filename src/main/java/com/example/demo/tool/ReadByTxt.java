package com.example.demo.tool;

import cn.hutool.core.io.file.FileReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
* * 对比两边数据差异
*/
public class ReadByTxt {

    static final String path1 = "D:\\myworkplace\\test\\src\\main\\resources\\static\\other\\text.txt";

    static final String path2 = "D:\\myworkplace\\test\\src\\main\\resources\\static\\other\\bak.txt";

    public static void main(String[] args) {
        FileReader textFileReader;
        FileReader bakFileReader;
        textFileReader = new FileReader(path1);
        String textResults = textFileReader.readString();
        String[] textResultArr = textResults.split(",");

        bakFileReader = new FileReader(path2);
        String bakResults = bakFileReader.readString();
        String[] bakResultArr = bakResults.split(",");

        List textList = new ArrayList();
        List bakList = new ArrayList();
        for (int i = 0; i < textResultArr.length; i++){
            textList.add(textResultArr[i]);
        }
        for (int i = 0; i < bakResultArr.length; i++){
            bakList.add(bakResultArr[i]);
        }
        Set<Integer> textSet = new HashSet<>(textList);
        List<Integer> textUniqueList = new ArrayList<>(textSet);

        Set<Integer> bakSet = new HashSet<>(bakList);
        List<Integer> bakUniqueList = new ArrayList<>(bakSet);

        System.out.println("原始列表数据量： " + textList.size());
        System.out.println("去重后的列表数据量： " + textUniqueList.size());
        textUniqueList.removeAll(bakUniqueList);
        System.out.println("bakList = " + bakList.size());
        System.out.println("剩余的单位：" + textUniqueList);

    }
}
