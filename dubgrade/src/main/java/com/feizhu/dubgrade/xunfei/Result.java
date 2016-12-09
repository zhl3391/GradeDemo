package com.feizhu.dubgrade.xunfei;

import com.feizhu.dubgrade.StringGradeResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Result</p>
 * <p>Description: 评测结果</p>
 * <p>Company: www.iflytek.com</p>
 *
 * @author iflytek
 * @date 2015年1月12日 下午4:58:38
 */
public class Result extends StringGradeResult implements Serializable {
    /**
     * 评测语种：en（英文）、cn（中文）
     */
    public String language;
    /**
     * 评测种类：read_syllable（cn单字）、read_word（词语）、read_sentence（句子）
     */
    public String category;
    /**
     * 开始帧位置，每帧相当于10ms
     */
    public int beg_pos;
    /**
     * 结束帧位置
     */
    public int end_pos;
    /**
     * 评测内容
     */
    public String content;
    /**
     * 总得分
     */
    public float total_score;

    public float accuracy_score;

    public float fluency_score;

    public float integrity_score;
    /**
     * 时长（cn）
     */
    public int time_len;
    /**
     * 异常信息（en）
     */
    public String except_info;
    /**
     * 是否乱读（cn）
     */
    public boolean is_rejected;
    /**
     * xml结果中的sentence标签
     */
    public ArrayList<Sentence> sentences;

    @Override
    public List<WordResult> getWordResultList() {
        List<WordResult> wordResultList = new ArrayList<>();
        if (sentences != null) {
            for (Sentence sentence : sentences) {
                if (sentence.words != null) {
                    for (final Word word : sentence.words) {
                        WordResult wordResult = new WordResult() {
                            @Override
                            public String getWord() {
                                return word.content;
                            }

                            @Override
                            public int getScore() {
                                return (int) (word.total_score * 20);
                            }

                            @Override
                            public String toString() {
                                return getWord() + " = " + getScore();
                            }
                        };
                        wordResultList.add(wordResult);
                    }
                }
            }
        }
        return wordResultList;
    }

    @Override
    public int getTotalScore() {
        return (int) (total_score * 20);
    }

    @Override
    public void setTotalScore(int score) {
        total_score = score;
    }

    @Override
    public int getIntegrityScore() {
        return (int) (integrity_score * 20);
    }

    @Override
    public void setIntegrityScore(int score) {
        integrity_score = score;
    }

    @Override
    public int getAccuracyScore() {
        return (int) (accuracy_score * 20);
    }

    @Override
    public void setAccuracyScore(int score) {
        accuracy_score = score;
    }

    @Override
    public int getFluencyScore() {
        return (int) (fluency_score * 20);
    }

    @Override
    public void setFluencyScore(int score) {
        fluency_score = score;
    }

    @Override
    public int getRhythmScore() {
        return 0;
    }

    @Override
    public void setRhythmScore(int score) {

    }

    @Override
    public String getText() {
        return content;
    }
}
