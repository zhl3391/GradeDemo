package com.feizhu.dubgrade;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by zhouhl on 2016/12/2.
 */
public class DefaultPunctuationFormatTest {
    
    @Test
    public void format() throws Exception {
        DefaultPunctuationFormat defaultPunctuationFormat = new DefaultPunctuationFormat();
        
        String text = "， 。 ： ！ “ ” ‘ ’ ？ （ ）、";
        
        text = defaultPunctuationFormat.format(text);

        System.out.println(text);
        
        assertTrue(text.equals(", . : ! \" \" ' ' ? ( )/"));
    }

}