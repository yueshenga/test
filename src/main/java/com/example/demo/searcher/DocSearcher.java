package com.example.demo.searcher;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class DocSearcher {

    private Index index = new Index();

    public DocSearcher() {
        //一开始要加载
        index.load();
    }

    //完成整个搜索过程的方法
    //参数（输入部分）就是用户给出的查询词
    //返回值（输出部分）就是搜索结果的集合
    public List<Result> search(String query) {
        //1.[分词]针对query这个查询词进行分词
        List<Term> terms = ToAnalysis.parse(query).getTerms();

        //2.[触发]针对分词结果来查倒排
        List<Weight> allTermResult = new ArrayList<>();
        for (Term term : terms) {
            String word = term.getName();
            List<Weight> invertedList = index.getInverted(word);
            if (invertedList == null) {
                //说明词不存在
                continue;
            }
            allTermResult.addAll(invertedList);
        }
        // 3.[排序]针对触发的结果按照权重降序排序
        allTermResult.sort(new Comparator<Weight>() {
            @Override
            public int compare(Weight o1, Weight o2) {
                //降序排序 return o2.getWeight-01.getWeight  升序反之
                return o2.getWeight() - o1.getWeight();
            }
        });
        //4.[包装结果]针对排序的结果，去查正排，构造出要返回的数据
        List<Result> results = new ArrayList<>();
        for (Weight weight : allTermResult) {
            DocInfo docInfo = index.getDocInfo(weight.getDocId());
            Result result = new Result();
            result.setTitle(docInfo.getTitle());
            result.setUrl(docInfo.getUrl());
            result.setDesc(GenDesc(docInfo.getContent(),terms));
            results.add(result);
        }
        return results;
    }

    private String GenDesc(String content, List<Term> terms) {
        //先遍历结果，看看哪个结果是在content中存在
        int firstPos = -1;
        for (Term term : terms) {
            String word = term.getName();

            //因为分词结果是会把正文转成小写，所以我们要把查询词也转成小写

            //为了搜索结果独立成词 所以加" "
            firstPos =content.toLowerCase().indexOf(" " + word + " ");
            if (firstPos >= 0){
                break;
            }

            if(firstPos ==-1){
                //所有的分词结果都不在正文中存在 极端情况
                return content.substring(0,160)+"...";
            }
        }
        //从firstPos 作为基准，往前找60个字符，作为描述的起始位置
        String desc ="";
        //如果当前位置少于60个字符开始位置就是第一个 否则开始位置 在查询词前60个
        int descBeg = firstPos < 60 ?  0 : firstPos -60;
        if (descBeg+160 > content.length()){
            //判断是否超过正文长度
            //从开始位置到最后
            desc = content.substring(descBeg);
        }else {
            desc  =content.substring(descBeg,descBeg + 160)+"...";
        }
        return desc;
    }

    public static void main(String[] args) {
        DocSearcher docSearcher = new DocSearcher();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("->");
            String query = scanner.next();
            List<Result> results = docSearcher.search(query);
            for (Result result : results) {
                System.out.println("======================================");
                System.out.println(result);
            }
        }
    }
}

