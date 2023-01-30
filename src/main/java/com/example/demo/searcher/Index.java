package com.example.demo.searcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//通过这个类在内存中来构造出索引结构
public class Index {
    //保存索引文件的路径
    private static final String INDEX_PATH ="D:\\myworkplace\\doc_searcher_index\\";

    private ObjectMapper objectMapper = new ObjectMapper();

    //使用数组下标表示 DocId
    private ArrayList<DocInfo> forwardIndex = new ArrayList<>();


    //使用哈希表 来表示倒排索引 key就是词 value就是一组和词关联的文章
    private HashMap<String,ArrayList<Weight>> invertedIndex = new HashMap<>();

    //新创建2个锁对象
    private Object locker1 = new Object();
    private Object locker2 = new Object();

    //这个类需要提供的方法
    //1.给定一个docId ,在正排索引中，查询文档的详细信息
    public DocInfo getDocInfo(int docId){
        return forwardIndex.get(docId);
    }
    //2.给定一词，在倒排索引中，查哪些文档和这个文档词关联
    public List<Weight> getInverted(String term){
        return invertedIndex.get(term);
    }
    //3.往索引中新增一个文档
    public  void addDoc(String title,String url,String content){
        //新增文档操作，需要同时给正排索引和倒排索引新增信息
        //构建正排索引
        DocInfo docInfo =  buildForward(title,url,content);
        //构建倒排索引
        buildInverted(docInfo);

    }


    private void buildInverted(DocInfo docInfo) {
        //搞一个内部类避免出现2个哈希表
        class WordCnt{
            //表示这个词在标题中 出现的次数
            public int titleCount ;
            // 表示这个词在正文出现的次数
            public int contentCount;

        }
        //统计词频的数据结构
        HashMap<String,WordCnt> wordCntHashMap =new HashMap<>();

        //1，针对文档标题进行分词
        List<Term> terms =  ToAnalysis.parse( docInfo.getTitle()).getTerms();
        //2. 遍历分词结果,统计每个词出现的比例
        for (Term term : terms){
            //先判定一个term这个词是否存在，如果不存在，就创建一个新的键值对，插入进去，titleCount 设为1
            //gameName()的分词的具体的词
            String word = term.getName();
            //哈希表的get如果不存在默认返回的是null
            WordCnt wordCnt =  wordCntHashMap.get(word);
            if (wordCnt == null){
                //词不存在
                WordCnt newWordCnt = new WordCnt();
                newWordCnt.titleCount =1;
                newWordCnt.contentCount = 0;
                wordCntHashMap.put(word,newWordCnt);
            }else{
                //存在就找到之前的值，然后加1
                wordCnt.titleCount +=1;
            }
            //如果存在，就找到之前的值，然后把对应的titleCount +1

        }
        //3. 针对正文进行分词
        terms = ToAnalysis.parse(docInfo.getContent()).getTerms();
        //4. 遍历分词结果,统计每个词出现的次数

        for (Term term : terms) {
            String word = term.getName();
            WordCnt wordCnt = wordCntHashMap.get(word);
            if (wordCnt == null){
                WordCnt newWordCnt = new WordCnt();
                newWordCnt.titleCount = 0;
                newWordCnt.contentCount = 1;
                wordCntHashMap.put(word,newWordCnt);
            }else {
                wordCnt.contentCount +=1;

            }
        }
        //5. 把上面的结果汇总到一个HasMap里面
        //  最终的文档的权重，设置为标题的出现次数 * 10 + 正文中出现的次数
        //6.遍历当前的HashMap，依次来更新倒排索引中的结构。
        //并不是全部代码都是可以for循环的，只有这个对象是”可迭代的“,实现Iterable 接口才可以
        // 但是Map并没有实现，Map存在意义，是根据key查找value，但是好在Set实现了实现Iterable，就可以把Map转换为Set
        //本来Map存在的是戒键值对，可以根据key快速找到value，
        //Set这里存的是一个把 键值对 打包在一起的类 称为Entry(条目)
        //转成Set之后，失去了快速根据key快速查找value的只这样的能力，但是换来了可以遍历
        synchronized (locker2){
            for(Map.Entry<String,WordCnt> entry:wordCntHashMap.entrySet()){
                //先根据这里的词去倒排索引中查一查词
                //倒排拉链
                List<Weight> invertedList  =  invertedIndex.get(entry.getKey());
                if (invertedList == null){
                    //如果为空，插入一个新的键值对
                    ArrayList<Weight> newInvertedList =new ArrayList<>();
                    Weight weight = new Weight();
                    weight.setDocId(docInfo.getDocId());
                    //权重计算公式：标题中出现的次数* 10 +正文出现的次数
                    weight.setWeight(entry.getValue().titleCount * 10 + entry.getValue().contentCount);
                    newInvertedList.add(weight);
                    invertedIndex.put(entry.getKey(),newInvertedList);
                }else{
                    //如果非空 ，就把当前的文档，构造出一个Weight 对象，插入到倒排拉链的后面
                    Weight weight = new Weight();
                    weight.setDocId(docInfo.getDocId());
                    //权重计算公式：标题中出现的次数* 10 +正文出现的次数
                    weight.setWeight(entry.getValue().titleCount * 10 + entry.getValue().contentCount);
                    invertedList.add(weight);
                }
            }
        }

    }
    private DocInfo buildForward(String title, String url, String content) {
        DocInfo docInfo =new DocInfo();
        docInfo.setTitle(title);
        docInfo.setUrl(url);
        docInfo.setContent(content);
        synchronized (locker1){
            docInfo.setDocId(forwardIndex.size());
            forwardIndex.add(docInfo);
        }

        return docInfo;
    }

    //4.把内存中的索引结构保存到磁盘中
    public void save(){
        long beg = System.currentTimeMillis();
        //使用2个文件。分别保存正排和倒排
        System.out.println("保存索引开始");
        //1.先判断一下索引对应的目录是否存在
        File indexPathFile =new File(INDEX_PATH);
        if (!indexPathFile.exists()){
            //如果路径不存在
            //mkdirs()可以创建多级目录
            indexPathFile.mkdirs();
        }
        //正排索引文件
        File forwardIndexFile = new File(INDEX_PATH+"forward.txt");
        //倒排索引文件
        File invertedIndexFile = new File(INDEX_PATH+"inverted.txt");
        try {
            //writeValue的有个参数可以把对象写到文件里
            objectMapper.writeValue(forwardIndexFile,forwardIndex);
            objectMapper.writeValue(invertedIndexFile,invertedIndex);
        }catch (IOException e){
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("保存索结束 !消耗时间"+(end - beg)+"ms");
    }
    //5.把磁盘中的索引数据加载到内存中
    public void load(){
        long beg = System.currentTimeMillis();
        System.out.println("加载索引开始");
        //1.设置加载索引路径
        //正排索引
        File forwardIndexFile = new File(INDEX_PATH+"forward.txt");
        //倒排索引
        File invertedIndexFile = new File(INDEX_PATH+"inverted.txt");
        try{
            //readValue（）2个参数，从那个文件读，解析是什么数据
            forwardIndex = objectMapper.readValue(forwardIndexFile, new TypeReference<ArrayList<DocInfo>>() {});
            invertedIndex = objectMapper.readValue(invertedIndexFile, new TypeReference<HashMap<String, ArrayList<Weight>>>() {});
        }catch (IOException e){
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("加载索引结束 ! 消耗时间"+(end -beg)+"ms");
    }

    public static void main(String[] args) {
        Index index = new Index();
        index.load();
        System.out.println("索引加载完成");
    }

}

