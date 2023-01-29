package com.example.demo;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.List;

/*
* 分词测试类
*/
public class TestAnsj {
    public static void main(String[] args) {
        //准备一个比较长的话 用来分词
        String str ="小明毕业于清华大学，后来又去蓝翔技校和新东方去深照,擅长使用计算机控制挖掘机来炒菜。";
        ToAnalysis analysis = new ToAnalysis();
        //Term就表示一个分词结果
        List<Term> terms = analysis.parse(str).getTerms();
        for (Term term :terms){
            System.out.println(term.getName());
        }
    }
}

