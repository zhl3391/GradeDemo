package com.feizhu.dubgrade;

/**
 * Created by zhouhl on 2016/11/11.
 * 打分配置
 */

public class GradeConfig {

    public static final int CORE_TYPE_WORD = 0; //单词
    public static final int CORE_TYPE_SENT = 1; //句子
    public static final int CORE_TYPE_PRED = 2; //段落

    public final String appId;
    public final String appKey;
    public final String secretKey;
    public final String provisionPath;
    public final String logPath;
    public final String userId;
    public final int coreType;             // ↑↑↑↑↑↑↑文字内容类型↑↑↑↑↑↑↑
    public final int scoreType;            // 句子评分返回显示单词原格式
    public final int sentRaw;              // 句子评分结果显示标点符号格式
    public final int sentSymbol;           // 段落评分精度1和0.5两种
    public final int predPrecision;        // 段落评分返回精确到单词
    public final int predWordDetail;       // 段落评分返回精确到因素
    public final int predPhonemeDetails;

    public final boolean isDebug;

    public final PunctuationFormat punctuationFormat;

    public GradeConfig() {
        this(new Builder());
    }

    private GradeConfig(GradeConfig.Builder builder) {
        this.appId = builder.appId;
        this.appKey = builder.appKey;
        this.secretKey = builder.secretKey;
        this.provisionPath = builder.provisionPath;
        this.logPath = builder.logPath;
        this.isDebug = builder.isDebug;
        this.userId = builder.userId;
        this.scoreType = builder.scoreType;
        this.sentRaw = builder.sentRaw;
        this.sentSymbol = builder.sentSymbol;
        this.predPrecision = builder.predPrecision;
        this.predWordDetail = builder.predWordDetail;
        this.predPhonemeDetails = builder.predPhonemeDetails;
        this.coreType = builder.coreType;
        this.punctuationFormat = builder.punctuationFormat;
    }

    public static final class Builder {

        String appId;
        String appKey;
        String secretKey;
        String provisionPath;
        String logPath;
        String userId;
        int coreType;
        int scoreType;
        int sentRaw;
        int sentSymbol;
        int predPrecision;
        int predWordDetail;
        int predPhonemeDetails;
        boolean isDebug;
        PunctuationFormat punctuationFormat;

        public Builder() {
            this.setCoreType(GradeConfig.CORE_TYPE_SENT)
                    .setSentRaw(0)
                    .setSentSymbol(0)
                    .setPredPrecision(1)
                    .setPredPhonemeDetails(0)
                    .setPredWordDetail(0)
                    .setScoreType(100)
                    .setPunctuationFormat(new DefaultPunctuationFormat());
        }

        public GradeConfig build() {
            return new GradeConfig(this);
        }

        public Builder setAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder setProvisionPath(String provisionPath) {
            this.provisionPath = provisionPath;
            return this;
        }

        public Builder setLogPath(String logPath) {
            this.logPath = logPath;
            return this;
        }

        public Builder setDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        public Builder setScoreType(int scoreType) {
            this.scoreType = scoreType;
            return this;
        }

        public Builder setSentRaw(int sentRaw) {
            this.sentRaw = sentRaw;
            return this;
        }

        public Builder setSentSymbol(int sentSymbol) {
            this.sentSymbol = sentSymbol;
            return this;
        }

        public Builder setPredPrecision(int predPrecision) {
            this.predPrecision = predPrecision;
            return this;
        }

        public Builder setPredWordDetail(int predWordDetail) {
            this.predWordDetail = predWordDetail;
            return this;
        }

        public Builder setPredPhonemeDetails(int predPhonemeDetails) {
            this.predPhonemeDetails = predPhonemeDetails;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setCoreType(int coreType) {
            this.coreType = coreType;
            return this;
        }

        public Builder setPunctuationFormat(PunctuationFormat punctuationFormat) {
            this.punctuationFormat = punctuationFormat;
            return this;
        }
    }

}
