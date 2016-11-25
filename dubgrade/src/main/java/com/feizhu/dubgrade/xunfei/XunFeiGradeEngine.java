package com.feizhu.dubgrade.xunfei;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.feizhu.dubgrade.GradeConfig;
import com.feizhu.dubgrade.GradeEngine;
import com.feizhu.dubgrade.GradeResult;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by zhouhl on 2016/11/11.
 * 科大讯飞打分引擎
 */

public class XunFeiGradeEngine implements GradeEngine {

    private static final String TAG = "XunFeiGradeEngine";

    private static final String languages[] = {"en_us", "zh_cn"};
    private SpeechEvaluator mIse;

    private GradeConfig mGradeConfig;
    private ResultListener mResultListener;

    @Override
    public boolean init(Context context, GradeConfig gradeConfig) {
        SpeechUtility.createUtility(context.getApplicationContext(), gradeConfig.appId);
        Setting.setShowLog(gradeConfig.isDebug);
        mIse = SpeechEvaluator.createEvaluator(context.getApplicationContext(), null);
        setParams();
        return mIse != null;
    }

    @Override
    public int start(String content) {
        if (mIse != null) {
            return mIse.startEvaluating(content, null, mEvaluatorListener);
        } else {
            return ERROR_CODE;
        }
    }

    @Override
    public void writeAudio(byte[] data, int size) {
        mIse.writeAudio(data, 0, size);
    }

    @Override
    public void stop() {
        if (mIse != null && mIse.isEvaluating()) {
            mIse.stopEvaluating();
        }
    }

    @Override
    public void destroy() {
        if (mIse != null) {
            mIse.destroy();
            mIse = null;
        }
    }

    @Override
    public void setResultListener(ResultListener resultListener) {
        mResultListener = resultListener;
    }

    private EvaluatorListener mEvaluatorListener = new EvaluatorListener() {

        @Override
        public void onResult(EvaluatorResult result, boolean isLast) {
            if (isLast) {
                String lastResult = result.getResultString();
                if (mGradeConfig.isDebug) {
                    Log.d(TAG, lastResult);
                }
                if (mResultListener != null) {
                    XmlResultParser resultParser = new XmlResultParser();
                    Result ret = resultParser.parse(lastResult);
                    if (ret != null) {
                        mResultListener.onResult(ret);
                    } else {
                        mResultListener.onError(-1, "结果解析失败!");
                    }
                }
            }
        }

        @Override
        public void onError(SpeechError error) {
            if (mResultListener != null) {
                mResultListener.onError(error.getErrorCode(), error.getMessage());
            }
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            if (mGradeConfig.isDebug) {
                Log.d(TAG, "onBeginOfSpeech");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            if (mGradeConfig.isDebug) {
                Log.d(TAG, "onEndOfSpeech");
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                if (mGradeConfig.isDebug) {
                    Log.d(TAG, "session id =" + sid);
                }
            }
        }
    };

    private void setParams() {
        // 设置评测语言,默认英语
        String language = languages[0];
        // 设置需要评测的类型
        String category = "read_sentence";
        // 设置结果等级（中文仅支持complete）
        String result_level = "complete";
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        String vad_bos = "5000";
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        String vad_eos = "1800";
        // 语音输入超时时间，即用户最多可以连续说多长时间；
        String speech_timeout = "-1";

        mIse.setParameter(SpeechConstant.LANGUAGE, language);
        mIse.setParameter(SpeechConstant.ISE_CATEGORY, category);
        mIse.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mIse.setParameter(SpeechConstant.VAD_BOS, vad_bos);
        mIse.setParameter(SpeechConstant.VAD_EOS, vad_eos);
        mIse.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, speech_timeout);
        mIse.setParameter(SpeechConstant.RESULT_LEVEL, result_level);
        mIse.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIse.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
//        mIse.setParameter(SpeechConstant.ISE_AUDIO_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/ise.wav");
    }
}
