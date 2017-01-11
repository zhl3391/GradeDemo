package com.feizhu.dubgrade.chisheng;

import com.feizhu.dubgrade.WordFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouhl on 2017"1"9.
 * 驰声格式转换
 */

public class DefaultWordFormat implements WordFormat {

    @Override
    public String format(String text) {
        text = replaceSBC(text);
        text = replaceInvalidCharacter(text);
        text = replaceMiscellaneous(text);

        return text;
    }

    public String replaceSBC(String replaceValue) {
        return replaceValue.replaceAll("Ａ", "A").replaceAll("Ｂ", "B").replaceAll("Ｃ", "C").replaceAll("Ｄ", "D")
        .replaceAll("Ｅ", "E").replaceAll("Ｆ", "F").replaceAll("Ｇ", "G").replaceAll("Ｈ", "H")
        .replaceAll("Ｉ", "I").replaceAll("Ｊ", "J").replaceAll("Ｋ", "K").replaceAll("Ｌ", "L")
        .replaceAll("Ｍ", "M").replaceAll("Ｎ", "N").replaceAll("Ｏ", "O").replaceAll("Ｐ", "p")
        .replaceAll("Ｑ", "Q").replaceAll("Ｒ", "R").replaceAll("Ｓ", "S").replaceAll("Ｔ", "T")
        .replaceAll("Ｕ", "U").replaceAll("Ｖ", "V").replaceAll("Ｗ", "W").replaceAll("Ｘ", "X")
        .replaceAll("Ｙ", "Y").replaceAll("Ｚ", "Z")
        .replaceAll("ａ", "a").replaceAll("ｂ", "b").replaceAll("ｃ", "c").replaceAll("ｄ", "d")
        .replaceAll("ｅ", "e").replaceAll("ｆ", "f").replaceAll("ｇ", "g").replaceAll("ｈ", "h")
        .replaceAll("ｉ", "i").replaceAll("ｊ", "j").replaceAll("ｋ", "k").replaceAll("ｌ", "l")
        .replaceAll("ｍ", "m").replaceAll("ｎ", "n").replaceAll("ｏ", "o").replaceAll("ｐ", "p")
        .replaceAll("ｑ", "q").replaceAll("ｒ", "r").replaceAll("ｓ", "s").replaceAll("ｔ", "t")
        .replaceAll("ｕ", "u").replaceAll("ｖ", "v").replaceAll("ｗ", "w").replaceAll("ｘ", "x")
        .replaceAll("ｙ", "y").replaceAll("ｚ", "z")
        .replaceAll("１", "1").replaceAll("２", "2").replaceAll("３", "3").replaceAll("４", "4")
        .replaceAll("５", "5").replaceAll("６", "6").replaceAll("７", "7").replaceAll("８", "8")
        .replaceAll("９", "9").replaceAll("０", "0")
        .replaceAll("，", ",").replaceAll("。", ".").replaceAll("‘", "'").replaceAll("’", "'")
        .replaceAll("“", "\"").replaceAll("”", "\"").replaceAll("！", "!").replaceAll("？", "?")
        .replaceAll("—", "-").replaceAll("－", "-").replaceAll("；", ";").replaceAll("：", ":")
        .replaceAll("、", ",").replaceAll("﹩", "$").replaceAll("…", "...").replaceAll("／", "/")
        .replaceAll("\\r", " ").replaceAll("\\n", " ");
    }

    public String replaceInvalidCharacter(String replaceValue) {
        return replaceValue.replaceAll("[^a-zA-Z\\d,\\.'\";:!\\?\\-\\$/]+", " ")//替换所有不在合法字符集内的字符
                .replaceAll("^[^a-zA-Z\\d\"]+", "");//去除开头部分符号
    }

    public String replacePunctuationTrim(String replaceValue) { //临时除去标点符号前后空格
        return replace("\\s+[,\\.'\";:!\\?\\-\\$/]\\s+", "\\s", "", replaceValue);
    }

    /**
     * @param regexMatch    匹配正则
     * @param regexReplace  替换正则
     * @param replace       替换成什么
     * @param replaceValue  被替换字符串
     */
    public String replace(String regexMatch, String regexReplace, String replace, String replaceValue) {
        Matcher m = Pattern.compile(regexMatch).matcher(replaceValue);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            String pun = group.replaceAll(regexReplace, replace);
            m.appendReplacement(sb, pun);
        }
        m.appendTail(sb);

        return sb.toString();
    }

    public String replaceRepeatPunctuation(String replaceValue) { //替换多个标点重复出现后为单一标点
        replaceValue = this.replacePunctuationTrim(replaceValue);
        return replaceValue.replaceAll(",[,\\.';:!\\?/]+", ",")
                .replaceAll("\\.[,\\.';:!\\?/]+", ".")
                .replaceAll("'[,\\.';:!\\?/]+", "'")
                .replaceAll("\"[,\\.'\";:!\\?/]+", "\"")
                .replaceAll(";[,\\.';:!\\?/]+", ";")
                .replaceAll(":[,\\.';:!\\?/]+", ":")
                .replaceAll("![,\\.';:!\\?/]+", "!")
                .replaceAll("\\?[,\\.';:!\\?/]+", "?")
                .replaceAll("\\-[,\\.'\";:!\\?\\-\\$/]+", "-")
                .replaceAll("\\$[,\\.'\";:!\\?\\-\\$/]+", "$")
                .replaceAll("/[,\\.'\";:!\\?\\-\\$/]+", "/");
    }

    public String replaceNumberSpace(String replaceValue) { //给数字（标准、比例、负数、时钟写法,年写法）增加前后空格,
        replaceValue = this.replaceRepeatPunctuation(replaceValue);
        replaceValue = replace("\\-\\D", "\\-", " ", replaceValue);//连词符后面一定是数字，否则替换空格
        replaceValue = replace("\\$\\D", "\\$", " ", replaceValue);//美元符后面一定是数字，否则替换空格
        replaceValue = replace("(\\-?)(\\d{1,3})(,\\d{3})+(\\.\\d+)?", ",", "", replaceValue);//货币写法转普通写法
        Matcher m = Pattern.compile("(\\-?)(\\d+)?(:(\\-?)(\\d+)?)+|(\\d{1,4})(\\-\\d{1,2})" +
                "(\\-\\d{1,4})|(\\d{1,4})(/\\d{1,2})(/\\d{1,4})|(\\d{1,4})(\\.\\d{1,2})" +
                "(\\.\\d{1,4})|(\\-?)(\\$?)(\\d+)(\\.\\d+)?").matcher(replaceValue);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            m.appendReplacement(sb, Matcher.quoteReplacement(" " + group));
        }
        m.appendTail(sb);
        replaceValue = sb.toString();
        return replaceValue;
    }

    public String replaceMiscellaneous(String replaceValue) {
        replaceValue = this.replaceNumberSpace(replaceValue);
        replaceValue = this.replacePunctuationTrim(replaceValue);
        return replaceValue.replaceAll("\\.(?=[a-zA-Z])|\\.$", ". ") //句号后添加空格粘连
                .replaceAll("\\s*[\\.]{3,}", "!!!")
                .replaceAll("\\s*[\\.]{2}", ". ")
                .replaceAll("\\s*[!]{3,}", "... ") //以上，为了支持英文省略号
                .replaceAll(",(?=[a-zA-Z])\\d|,$", ", ")
                .replaceAll("\\s*[,]{1,2}", ", ")
                .replaceAll(":(?=[a-zA-Z])|:$", ": ")
                .replaceAll("\\s*[:]{2}", ": ")
                .replaceAll("!(?=[a-zA-Z]\\d)|!$", "! ")
                .replaceAll("\\s*[!]{1,2}", "! ")
                .replaceAll(";(?=[a-zA-Z]\\d)|;$", "; ")
                .replaceAll("\\s*[;]{1,2}", "; ")
                .replaceAll("\\?(?=[a-zA-Z]\\d)|\\?$", "? ")
                .replaceAll("\\s*[\\?]{1,2}", "? ") //以上，分隔符类型标点符号后添加空格粘连
                .replaceAll("\"(.*?)\"", " $& ") //双引号前后加空格，并粘连字母
                .replaceAll("\\s+", " ") //多个相邻空字符替换为一个空格
                .trim();
    }

}
