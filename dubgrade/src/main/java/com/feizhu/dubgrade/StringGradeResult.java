package com.feizhu.dubgrade;

/**
 * Created by zhouhl on 2016/11/15.
 * 显示结果文字
 */

public abstract class StringGradeResult implements GradeResult {

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("text = ").append(getText()).append("\n")
                .append("totalScore = ").append(getTotalScore()).append("\n")
                .append("integrityScore = ").append(getIntegrityScore()).append("\n")
                .append("accuracyScore = ").append(getAccuracyScore()).append("\n")
                .append("fluencyScore = ").append(getFluencyScore()).append("\n")
                .append("rhythmScore = ").append(getRhythmScore()).append("\n");
        if (getWordResultList() != null) {
            for (WordResult wordResult : getWordResultList()) {
                stringBuilder.append(wordResult.toString()).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
