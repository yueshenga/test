package com.example.demo.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by Lin
 * Description:
 * User: Administrator
 * Date: 2022-12-15
 * Time: 19:15
 */
public class Parser {
    //先指定一个加载文档的路径 ,由于是固定路径 我们使用 static 类属性 不需要变final
    private static final String INPUT_PATH  ="D:\\myworkplace\\doc_searcher_index\\docs\\api";     // 只需要api文件夹下的文件

    //创建一个Index实例
    private Index index =new Index();

    //为了避免线程不安全
    private AtomicLong t1 = new AtomicLong(0);
    private AtomicLong t2 = new AtomicLong(0);

    //通过这个方法实现单线程制作索引
    public  void run(){
        long beg = System.currentTimeMillis();
        System.out.println("索引制作开始");
        //整个Parser类的入口
        //1.根据指定的路径去枚举出该路径中所有的文件（所有子目录的html文件），这个过程需要把全部子目录的文件全部获取到
        ArrayList<File> fileList = new ArrayList<>();
        enumFile(INPUT_PATH,fileList);
        //测试枚举时间
        long endEnumFile = System.currentTimeMillis();
        System.out.println("枚举文件完毕 时间"+(endEnumFile - beg));

        //2.针对上面罗列出的路径，打开文件，读取文件内容，并进行解析.并构建索引
        for (File f :fileList){
            //通过这个方法解析单个HTML文件
            System.out.println("开始解析:" + f.getAbsolutePath());
            parseHTML(f);
        }
        long endFor = System.currentTimeMillis();
        System.out.println("循环遍历文件完毕 时间"+(endFor - endEnumFile)+"ms");
        //3.  把内存中构造好的索引数据结构，保存到指定的文件中
        index.save();
        long end = System.currentTimeMillis();
        System.out.println("索引制作完毕，消耗时间："+(end - beg) + "ms");
    }

    //通过这个方法实现多线程制作索引
    public void runByThread() throws InterruptedException {
        long beg =System.currentTimeMillis();
        System.out.println("索引制作开始！");

        //1.，枚举全部文件
        ArrayList<File> files = new ArrayList<>();
        enumFile(INPUT_PATH,files);
        //2.循环遍历文件 此处为了通过多线程制作索引，就直接引入线程池
        CountDownLatch latch = new CountDownLatch(files.size());
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for(File f:files){
            //添加任务submit到线程池
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("解析"+f.getAbsolutePath());
                    parseHTML(f);
                    //保证所有的索引制作完再保存索引
                    latch.countDown();
                }
            });
        }
        //latch.await()等待全部countDown完成，才阻塞结束。
        latch.await();
        executorService.shutdown();
        //3.保存索引 ，可能存在还没有执行完的情况
        index.save();
        long end =System.currentTimeMillis();
        System.out.println("索引制作结束！时间"+(end - beg)+"ms");
        System.out.println("t1：" +t1+" , " +"t2："+t2);
    }
    //通过这个方法解析单个HTML文件
    private void parseHTML(File f) {
//        1. 解析出HTML标题
        String title  = parseTitle(f);
//        2. 解析出HTML对应的文章
        String url = parseUrl(f);
//        3. 解析出HTML对应的正文（有正文才有后续的描述）
        //纳秒级别时间
        long beg = System.nanoTime();
        String content = parseContentRegex(f);
        long mid = System.nanoTime();
        // 4.  解析的信息加入到索引当中
        index.addDoc(title,url,content);
        long end = System.nanoTime();
        t1.addAndGet(mid -beg);
        t2.addAndGet(end - mid);

    }
    private String readFile(File f){
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))){
            StringBuilder content = new StringBuilder();
            while(true){
                int ret = bufferedReader.read();
                if (ret==-1){
                    break;
                }
                char c = (char) ret;
                if (c=='\n' || c == '\r'){
                    c= ' ';
                }
                content.append(c);
            }
            return content.toString();
        }catch (IOException e){
            e.printStackTrace();
        }return  "";
    }

    public String parseContentRegex(File f){
        //1.先把整个文件都读取到String里面
        String content = readFile(f);

        //2.替换script标签
        content = content.replaceAll("<script.*?>(.*?)</script>"," ");

        //3.替换普通的HTML标签
        content = content.replaceAll("<.*?>"," ");
        //4.使用正则把多个空格，合并成一个空格
        content = content.replaceAll("\\s+"," ");
        return content;
    }

    public String parseContent(File f) {
        //先按照一个字符一个字符来读取，以< 和 > 来控制拷贝数据的开关
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f),1024 *1024)) {
            //加上一个开关
            boolean isCopy = true;
            //还准备一个保存结果的StringBuilder
            StringBuilder content  = new StringBuilder();
            while (true){
                //read int类型 读到最后返回-1
                int ret = bufferedReader.read();
                if (ret == -1){
                    //表示文件读完了
                    break;
                }
                //不是-1就是合法字符
                char c = (char) ret;
                if (isCopy){
                    //打开的状态可以拷贝
                    if (c == '<'){
                        isCopy =false;
                        continue;
                    }
                    //判断是否是换行
                    if (c == '\n' || c == '\r'){
//                        是换行就变成空格
                        c = ' ';
                    }
                    //其他字符进行拷贝到StringBuilder中
                    content.append(c);
                }else{
                    //
                    if (c=='>'){
                        isCopy= true;
                    }
                }
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String parseUrl(File f) {
        //固定的前缀
        String path = "https://docs.oracle.com/javase/8/docs/api/";
        //只放一个参数的意思是:前面一段都不需要，取后面的一段
        String path2=   f.getAbsolutePath().substring(INPUT_PATH.length());
        return path + path2;
    }

    private String parseTitle(File f) {
        //获取文件名
        String name =  f.getName();
        return name.substring(0,name.length()-".html".length());
    }

    //第一个参数表示从那个目录开始进行遍历，第二个目录表示递归得到的结果
    private void enumFile(String inputPath, ArrayList<File> fileList) {
        //我们需要把String类型的路径变成文件类 好操作点
        File rootPath = new File(inputPath);
        //listFiles()类似于Linux的ls把当前目录中包含的文件名获取到
        //使用listFiles只可以看见一级目录，想看到子目录需要递归操作
        File[] files = rootPath.listFiles();
        for (File file : files) {
            //根据当前的file的类型，觉得是否递归
            //如果file是普通文件就把file加入到listFile里面
            //如果file是一个目录 就递归调用enumFile这个方法，来进一步获取子目录的内容
            if (file.isDirectory()){
                //根路径要变
                enumFile(file.getAbsolutePath(),fileList);
            }else {
                //只针对HTML文件
                if(file.getAbsolutePath().endsWith(".html")){
                    //普通HTML文件
                    fileList.add(file);
                }

            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //通过main方法来实现整个制作索引的过程
        Parser parser = new Parser();
        parser.runByThread();
    }

}

