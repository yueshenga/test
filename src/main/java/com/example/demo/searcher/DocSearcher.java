package com.example.demo.searcher;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DocSearcher {
    private static final String STOP_WORD_PATH ="D:\\myworkplace\\doc_searcher_index\\stop_word.txt";

    //使用这个HashSet 来保存停用词
    private HashSet<String> stopWords = new HashSet<>();

    private Index index = new Index();

    public DocSearcher() {
        //一开始要加载
        index.load();
        loadStopWords();
    }

    //完成整个搜索过程的方法
    //参数（输入部分）就是用户给出的查询词
    //返回值（输出部分）就是搜索结果的集合
    public List<Result> search(String query) {
        //1.[分词]针对query这个查询词进行分词
        List<Term> oldTerms = ToAnalysis.parse(query).getTerms();
        List<Term> terms = new ArrayList<>();
        //针对分词结果，使用暂停词表
        for (Term term : oldTerms){

            if (stopWords.contains(term.getName())){
                //是暂停词就不拷贝
                continue;
            }
            terms.add(term);
        }

        //2.[触发]针对分词结果来查倒排
        //二维数组
        List<List<Weight>> termResult = new ArrayList<>();
        for (Term term : terms) {
            String word = term.getName();
            List<Weight> invertedList = index.getInverted(word);
            if (invertedList == null) {
                //说明词不存在
                continue;
            }
            termResult.add(invertedList);
        }
        //3.[合并]针对多个分词结果触发出的相同文档进行权重合并 去重
        List<Weight> allTermResult = mergeResult(termResult);

        // 4.[排序]针对触发的结果按照权重降序排序
        allTermResult.sort(new Comparator<Weight>() {
            @Override
            public int compare(Weight o1, Weight o2) {
                //降序排序 return o2.getWeight-01.getWeight  升序反之
                return o2.getWeight() - o1.getWeight();
            }
        });
        //5.[包装结果]针对排序的结果，去查正排，构造出要返回的数据
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

    //进行合并的时候把多个行合并成为一行，
    //合并过程要操作二维数组的每个元素 所以我们把行和列创建好
    static class Pos{
        public int row;
        public int col;

        public Pos(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
    private List<Weight> mergeResult(List<List<Weight>> source) {

        //1.先针对每一行进行排序（按照id进行升序）不然没办法合并
        for(List<Weight> curRow :source){
            curRow.sort(new Comparator<Weight>() {
                @Override
                public int compare(Weight o1, Weight o2) {
                    return o1.getDocId() - o2.getDocId();
                }
            });
        }

        //2.借助优先队列，针对这些进行合并
        // target 表示合并的结果
        List<Weight> target = new ArrayList<>();
        // 搞一个优先队列 要找到对应的Weight对象位置
//        优先队列存在的意义就是:为了能够找出每一行对应的最小的docid对象，把最小的对象插入到target里面，同时把对应的下标给往后移动
        PriorityQueue<Pos> queue = new PriorityQueue<>(new Comparator<Pos>() {
            @Override
            public int compare(Pos o1, Pos o2) {
                // 找到Weight对象 然后再根据Weight 的docId来排序

//                第一个对象
                Weight w1 = source.get(o1.row).get(o1.col);
                Weight w2 = source.get(o2.row).get(o2.col);
                return w1.getDocId() - w2.getDocId();
            }
        });

        // 初始化队列，把每一行的第一个元素放到队列中
        for (int row = 0; row<source.size();row++){
            queue.offer(new Pos(row,0));  //把每一行的第一个元素放到队列中
        }
        // 循环取队首元素 (当前若干行最小的元素)
        while (!queue.isEmpty()){
            Pos minPos = queue.poll();
            //获取最小weight对象
            Weight curWeight = source.get(minPos.row).get(minPos.col);
            //判断当前取到的对象，是否和前一个插入到target中的结果是相同的 docId
            // 如果是就合并
            if (target.size() >0){
                //取出上次插入的元素
                Weight lastWeight = target.get(target.size()-1);
                if (lastWeight.getDocId() == curWeight.getDocId()){
                    //遇到了相同的文档
                    lastWeight.setWeight(lastWeight.getWeight()+curWeight.getWeight());
                }else{
                    //如果不相同的话
                    target.add(curWeight);
                }
            }else {
                // 如果当前是空着的 直接插入即可
                target.add(curWeight);
            }
            //        把当前的元素处理完了之后，要把对应这个元素的光标往后移动 取下一个元素
            Pos newPos = new Pos(minPos.row, minPos.col+1);
            if (newPos.col >= source.get(newPos.row).size()){
                //如果移动光标之后，超过了这一行的列数，就说明到达了末尾 处理完毕

                continue;
            }
//            优先队列 自己放到合适的地方
            queue.offer(newPos);
        }
        return target;
    }

    private String GenDesc(String content, List<Term> terms) {
        //先遍历结果，看看哪个结果是在content中存在
        int firstPos = -1;
        for (Term term : terms) {
            String word = term.getName();

            //因为分词结果是会把正文转成小写，所以我们要把查询词也转成小写

            //为了搜索结果独立成词 所以加" "
            content = content.toLowerCase().replaceAll("\\b" + word + "\\b"," " + word +" ");
            firstPos =content.toLowerCase().indexOf(" " + word + " ");
            if (firstPos >= 0){
                break;
            }

        }
        if(firstPos ==-1){
            if(content.length() > 160){
                return content.substring(0,160)+"...";
            }
            //所有的分词结果都不在正文中存在 极端情况
            return content;
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
        //在此处加上一个替换操作，把描述中的分词结果相同的部分，给加上一层<i>标签，就可以通过replace的方式实现
        for (Term term : terms){
            String word = term.getName();
            //只有是全部是一个查询词 才加上 i 标签  正则规则(?i)大小写全部匹配
            desc =  desc.replaceAll("(?i)"+word+" ","<i> " + word + " </i>");
        }
        return desc;
    }

    public void loadStopWords (){
        System.out.println("加载暂停词表");
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(STOP_WORD_PATH))){
            while (true){
                String line = bufferedReader.readLine();
                if (line == null){
                    //读取文件完毕
                    break;
                }
                stopWords.add(line);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
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

