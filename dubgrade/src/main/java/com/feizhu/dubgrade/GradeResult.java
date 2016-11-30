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

    /**
     * 完整度分数
     */
    int getIntegrityScore();

    /**
     * 发音分数
     */
    int getAccuracyScore();

    /**
     * 流利度得分
     */
    int getFluencyScore();

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
