package com.feizhu.dubgrade;

import java.util.List;

/**
 * Created by zhouhl on 2016/11/14.
 * 打分结果
 */

public interface GradeResult {

    /**
     * 每个单词分数
     */
    List<WordResult> getWordResultList();

    /**
     * 总分
     */
    int getTotalScore();

    void setTotalScore(int score);

    /**
     * 完整度分数
     */
    int getIntegrityScore();

    void setIntegrityScore(int score);

    /**
     * 发音分数
     */
    int getAccuracyScore();

    void setAccuracyScore(int score);

    /**
     * 流利度得分
     */
    int getFluencyScore();

    void setFluencyScore(int score);

    /**
     * 节奏得分
     */
    int getRhythmScore();

    void setRhythmScore(int score);

    /**
     * 原始文字
     */
    String getText();

    String toString();

    interface WordResult {

        String getWord();

        int getScore();

        String toString();
    }
}
