package com.example.demo.tool;

import cn.hutool.core.io.file.FileReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
* * 对比两边文本数据差异
*/
public class CompareTwo {

    static final String path1 = "D:\\myworkplace\\test\\src\\main\\resources\\static\\other\\bak2.txt";

    static final String path2 = "D:\\myworkplace\\test\\src\\main\\resources\\static\\other\\bak1.txt";

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

        System.out.println("原始列表数据量textList:" + textList.size());
//        System.out.println("去重后的列表数据量： " + textUniqueList.size());
//        textUniqueList.removeAll(bakUniqueList);
        System.out.println("原始列表数据量bakList:" + bakList.size());
//        System.out.println("大集合中除去小集合的其他数据 : " + textUniqueList);

        boolean all = textUniqueList.containsAll(bakUniqueList);
        System.out.println("大集合是否包含小集合 : " + all);
    }
}
