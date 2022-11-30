package com.example.demo;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SensitiveWordBsTest {

    /**
     * 是否包含
     * @since 0.0.1
     */
    @Test
    public void containsTest() {
        final String text = "五星红旗迎风飘扬，毛主席的画像屹立在天安门前。";
        boolean contains = SensitiveWordBs.newInstance().contains(text);
        System.out.println("contains = " + contains);
    }

    /**
     * 返回所有敏感词
     * @since 0.0.1
     */
    @Test
    public void findAllTest() {
        final String text = "五星红旗迎风飘扬，毛主席的画像屹立在天安门前。";
        List<String> wordList = SensitiveWordBs.newInstance().findAll(text);
        System.out.println("wordList = " + wordList);
        //[五星红旗, 毛主席, 天安门]
    }

    /**
     * 返回所有第一个匹配的敏感词
     * @since 0.0.1
     */
    @Test
    public void findFirstTest() {
        final String text = "五星红旗迎风飘扬，毛主席的画像屹立在天安门前。";
        String word = SensitiveWordBs.newInstance().findFirst(text);
        System.out.println("word = " + word);
        //五星红旗
    }

    /**
     * 默认的替换策略
     * @since 0.0.2
     */
    @Test
    public void replaceTest() {
        final String text = "五星红旗迎风飘扬，毛主席的画像屹立在天安门前。";
        String result = SensitiveWordBs.newInstance().replace(text);
        System.out.println("result = " + result);
        //****迎风飘扬，***的画像屹立在***前。
    }

    /**
     * 自定义字符的替换策略
     */
    @Test
    public void replaceCharTest() {
        final String text = "五星红旗迎风飘扬，毛主席的画像屹立在天安门前。";
        String result = SensitiveWordBs.newInstance().replace(text, '0');
        System.out.println("result = " + result);
        //0000迎风飘扬，000的画像屹立在000前。
    }

    /**
     * 忽略大小写
     */
    @Test
    public void ignoreCaseTest() {
        final String text = "fuCK the bad words.";
        String word = SensitiveWordBs.newInstance().findFirst(text);
        System.out.println("word = " + word);
    }

    /**
     * 忽略半角圆角
     */
    @Test
    public void ignoreWidthTest() {
        final String text = "ｆｕｃｋ the bad words.";
        String word = SensitiveWordBs.newInstance().findFirst(text);
        System.out.println("word = " + word);
    }

    @Test
    public void configTest() {
        SensitiveWordBs wordBs = SensitiveWordBs.newInstance()
                .wordDeny(WordDenys.system())
                .wordAllow(WordAllows.system())
                .init();
        final String text = "五星红旗迎风飘扬，毛主席的画像屹立在天安门前。";
        System.out.println("text = " + text);
        System.out.println("wordBs = " + wordBs.toString());
    }
}
