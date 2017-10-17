package com.feizhu.dubgrade.xiansheng;

import android.content.Context;
import android.util.Log;

import com.constraint.CoreProvideTypeEnum;
import com.feizhu.dubgrade.GradeConfig;
import com.feizhu.dubgrade.GradeEngine;
import com.feizhu.dubgrade.GradeResult;
import com.feizhu.dubgrade.GradeResultImpl;
import com.feizhu.dubgrade.WordFormat;
import com.xs.BaseSingEngine;
import com.xs.SingEngine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.feizhu.dubgrade.GradeConfig.CORE_TYPE_PRED;
import static com.feizhu.dubgrade.GradeConfig.CORE_TYPE_SENT;

/**
 * Created by zhouhl on 2017/10/17.
 * 先声打分引擎
 */

public class XianShengGradeEngine implements GradeEngine, BaseSingEngine.ResultListener {

    private static final String TAG = "XianShengGradeEngine";

    private SingEngine mSingEngine;
    private GradeConfig mGradeConfig;
    private ResultListener mResultListener;

    private int mIndex;

    @Override
    public boolean init(Context context, GradeConfig gradeConfig) {
        mGradeConfig = gradeConfig;
        mSingEngine = SingEngine.newInstance(context.getApplicationContext());
        mSingEngine.setListener(this);
        mSingEngine.setServerType(CoreProvideTypeEnum.CLOUD);
        mSingEngine.setLogEnable(mGradeConfig.isDebug ? 1 : 0);
        JSONObject cfg_init;
        try {
            cfg_init = mSingEngine.buildInitJson(gradeConfig.appKey, gradeConfig.secretKey);
            mSingEngine.setNewCfg(cfg_init);
            mSingEngine.newEngine();
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public int start(String content, int index, String courseId) {
        mIndex = index;
        if (mGradeConfig.wordFormat != null) {
            for (WordFormat wordFormat : mGradeConfig.wordFormat) {
                content = wordFormat.format(content);
            }
        }
        try {
            JSONObject request = new JSONObject();
            if (GradeConfig.CORE_TYPE_WORD == mGradeConfig.coreType) {
                request.put("coreType", "en.word.score");
            } else if (CORE_TYPE_SENT == mGradeConfig.coreType) {
                request.put("coreType", "en.sent.score");
            } else if (CORE_TYPE_PRED == mGradeConfig.coreType) {
                request.put("coreType", "en.pred.exam");
            }
            request.put("outputPhones", 1);
            request.put("refText", content);
            request.put("rank", mGradeConfig.scoreType);
            JSONObject startCfg = mSingEngine.buildStartJson(mGradeConfig.userId, request);
            mSingEngine.setStartCfg(startCfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void writeAudio(byte[] data, int size) {
    }

    @Override
    public void writeAudio(String pcmPath) {
        mSingEngine.startWithPCM(pcmPath);
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        mSingEngine.delete();
    }

    @Override
    public void setResultListener(ResultListener resultListener) {
        mResultListener = resultListener;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onResult(JSONObject jsonObject) {
        if (mGradeConfig.isDebug) {
            Log.d(TAG, jsonObject.toString());
        }
        mResultListener.onResult(parseJson(jsonObject), mIndex);
    }

    @Override
    public void onEnd(int i, String s) {

    }

    @Override
    public void onUpdateVolume(int i) {

    }

    @Override
    public void onFrontVadTimeOut() {

    }

    @Override
    public void onBackVadTimeOut() {

    }

    @Override
    public void onRecordingBuffer(byte[] bytes, int i) {

    }

    @Override
    public void onRecordLengthOut() {

    }

    @Override
    public void onReady() {

    }

    @Override
    public void onPlayCompeleted() {

    }

    @Override
    public void onRecordStop() {

    }

    private GradeResult parseJson(JSONObject jsonObject) {
        GradeResult result = new GradeResultImpl();
        JSONObject resultJson;
        try {
            resultJson = jsonObject.getJSONObject("result");
            result.setTotalScore(resultJson.optInt("overall"));
            result.setAccuracyScore(resultJson.optInt("accuracy"));
            result.setIntegrityScore(resultJson.optInt("integrity"));
            List<GradeResult.WordResult> wordResultList = new ArrayList<>();
            JSONArray details = resultJson.getJSONArray("details");
            switch (mGradeConfig.coreType) {
                case CORE_TYPE_PRED:
                    result.setFluencyScore(resultJson.getInt("fluency"));
                    result.setText(jsonObject.getJSONObject("refText").optString("lm"));
                    break;
                case CORE_TYPE_SENT:
                    JSONObject fluencyJson = resultJson.getJSONObject("fluency");
                    result.setFluencyScore(fluencyJson.optInt("overall"));
                    result.setText(jsonObject.optString("refText"));
                    break;
            }
            for (int i=0; i<details.length(); i++) {
                JSONObject wordJson = details.getJSONObject(i);
                switch (mGradeConfig.coreType) {
                    case CORE_TYPE_PRED:
                        JSONArray wordsArray = wordJson.getJSONArray("words");
                        for (int j=0; j<wordsArray.length(); j++) {
                            GradeResultImpl.Word word = new GradeResultImpl.Word();
                            word.word = wordsArray.getJSONObject(j).optString("text");
                            word.score = wordsArray.getJSONObject(j).optInt("score");
                            wordResultList.add(word);
                        }
                        break;
                    case GradeConfig.CORE_TYPE_SENT:
                        GradeResultImpl.Word word = new GradeResultImpl.Word();
                        word.score = wordJson.getInt("score");
                        word.word = wordJson.optString("char");
                        wordResultList.add(word);
                        break;
                }
            }
            result.setWordResultList(wordResultList);
        } catch (JSONException e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }
}
