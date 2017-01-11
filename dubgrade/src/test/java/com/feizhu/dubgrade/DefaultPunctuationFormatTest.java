package com.feizhu.dubgrade;

import com.feizhu.dubgrade.chisheng.DefaultWordFormat;

import org.junit.Test;

import static android.R.attr.text;
import static org.junit.Assert.*;

/**
 * Created by zhouhl on 2016/12/2.
 * 标点符号转换测试
 */
public class DefaultPunctuationFormatTest {
    
    @Test
    public void format() throws Exception {
        DefaultPunctuationFormat defaultPunctuationFormat = new DefaultPunctuationFormat();
        DefaultWordFormat defaultWordFormat = new DefaultWordFormat();
        
        String text = "， 。 ： ！ “ ” ‘ ’ ？ （ ）、";
        System.out.println(text);
        text = defaultPunctuationFormat.format(text);
        System.out.println(text);
        assertTrue(text.equals(", . : ! \" \" ' ' ? ( )/"));

        text = "ＡＡ";
        System.out.println(text);
        text = defaultWordFormat.replaceSBC(text);
        System.out.println(text);
        assertTrue(text.equals("AA"));

        text = " , dfdf , ";
        System.out.println(text);
        text = defaultWordFormat.replacePunctuationTrim(text);
        System.out.println(text);

        text = ",.dfd,,hjkj!,";
        System.out.println(text);
        text = defaultWordFormat.replaceRepeatPunctuation(text);
        System.out.println(text);

        text = ",,dd";
        System.out.println(text);
        text = defaultWordFormat.replaceInvalidCharacter(text);
        System.out.println(text);

        text = "test2016-12-01 18:05";
        System.out.println(text);
        text = defaultWordFormat.replaceNumberSpace(text);
        System.out.println(text);

    }

}