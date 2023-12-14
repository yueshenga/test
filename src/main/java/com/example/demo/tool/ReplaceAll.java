package com.example.demo.tool;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* * 文本实现全局替换
*/
public class ReplaceAll {
    public static void main(String[] args) {
        String filePath = "D:\\myworkplace\\test\\src\\main\\resources\\static\\other\\bak.txt"; // 替换为你的txt文件路径
        String searchString = ","; // 要替换的文本
        String replacementString = ",\n"; // 替换后的文本，包含回车符

        try {
            // 读取文件内容
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // 使用正则表达式进行全局替换
            Pattern pattern = Pattern.compile(searchString, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            content = matcher.replaceAll(replacementString);

            // 将修改后的内容写回到txt文件中
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(content);
            fileWriter.close();
            System.out.println("替换完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
