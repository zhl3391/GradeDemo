package com.feizhu.dubgrade;

import java.util.List;

/**
 * Created by zhouhl on 2017/10/17.
 */

public class GradeResultImpl extends StringGradeResult{

    private String text;
    private int totalScore;
    private int integrityScore;
    private int accuracyScore;
    private int fluencyScore;
    private int rhythmScore;
    private List<WordResult> wordResultList;

    public List<WordResult> getWordResultList() {
        return wordResultList;
    }

    @Override
    public void setWordResultList(List<WordResult> wordResultList) {
        this.wordResultList = wordResultList;
    }

    @Override
    public int getTotalScore() {
        return totalScore;
    }

    @Override
    public void setTotalScore(int score) {
        totalScore = score;
    }

    @Override
    public int getIntegrityScore() {
        return integrityScore;
    }

    @Override
    public void setIntegrityScore(int score) {
        integrityScore = score;
    }

    @Override
    public int getAccuracyScore() {
        return accuracyScore;
    }

    @Override
    public void setAccuracyScore(int score) {
        accuracyScore = score;
    }

    @Override
    public int getFluencyScore() {
        return fluencyScore;
    }

    @Override
    public void setFluencyScore(int score) {
        fluencyScore = score;
    }

    @Override
    public int getRhythmScore() {
        return rhythmScore;
    }

    @Override
    public void setRhythmScore(int score) {
        rhythmScore = score;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    public static class Word implements GradeResult.WordResult {

        public String word;
        public int score;

        @Override
        public String getWord() {
            return word;
        }

        @Override
        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return word + " = " + score;
        }
    }
}
