package com.feizhu.dubgrade.chisheng;

import android.content.Context;
import android.util.Log;

import com.chivox.AIEngine;
import com.feizhu.dubgrade.GradeConfig;
import com.feizhu.dubgrade.GradeEngine;
import com.feizhu.dubgrade.GradeResult;
import com.feizhu.dubgrade.GradeResultImpl;
import com.feizhu.dubgrade.StringGradeResult;
import com.feizhu.dubgrade.WordFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.feizhu.dubgrade.GradeConfig.CORE_TYPE_PRED;
import static com.feizhu.dubgrade.GradeConfig.CORE_TYPE_SENT;


/**
 * Created by zhouhl on 2016/11/11.
 * 驰声打分引擎
 */

public class ChiShengGradeEngine implements GradeEngine {

    public static final String TAG = "ChiShengGradeEngine";

    private GradeConfig mGradeConfig;
    private ResultListener mResultListener;

    private long mEngineId;

    @Override
    public boolean init(Context context, GradeConfig gradeConfig) {
        mGradeConfig = gradeConfig;
        String cfgString = getEngineNewParam();
        mEngineId = AIEngine.aiengine_new(cfgString, context.getApplicationContext());
        return mEngineId != 0;
    }

    @Override
    public int start(String content, final int index, String courseId) {
        if (mEngineId != 0) {
            byte[] userdata = new byte[64];
            byte[] id = new byte[64];
            if (mGradeConfig.wordFormat != null) {
                for (WordFormat wordFormat : mGradeConfig.wordFormat) {
                    content = wordFormat.format(content);
                }
            }
            int ret = AIEngine.aiengine_start(mEngineId, getEngineStartParam(content, mGradeConfig.coreType, courseId),
                    id, new AIEngine.aiengine_callback() {
                @Override
                public int run(byte[] id, int type, byte[] data, int size) {
                    if (type == AIEngine.AIENGINE_MESSAGE_TYPE_JSON) {
                        final String responseString = new String(data, 0, size).trim();
                        if (mGradeConfig.isDebug) {
                            Log.d(TAG, responseString);
                        }
                        if (mResultListener != null) {
                            GradeResult result = parseJson(responseString);
                            if (result != null) {
                                mResultListener.onResult(result, index);
                            } else {
                                JSONObject jsonObject;
                                try {
                                    jsonObject = new JSONObject(responseString);
                                    int errorId = jsonObject.getInt("errId");
                                    mResultListener.onError(errorId, "", index);
                                } catch (JSONException e) {
                                    mResultListener.onError(-1, e.toString(), index);
                                }
                            }
                        }

                    } else if (type == AIEngine.AIENGINE_MESSAGE_TYPE_BIN) { // 仅语音合成时使用

                    }
                    return 0;
                }
            }, userdata);
            if (ret == -1) {
                return ERROR_CODE;
            } else {
                return ret;
            }
        } else {
            return ERROR_CODE;
        }
    }

    @Override
    public void writeAudio(byte[] data, int size) {
        AIEngine.aiengine_feed(mEngineId, data, size);
    }

    @Override
    public void writeAudio(String pcmPath) {

    }

    @Override
    public void stop() {
        AIEngine.aiengine_stop(mEngineId);
    }

    @Override
    public void destroy() {
        AIEngine.aiengine_delete(mEngineId);
        mEngineId = 0;
    }

    @Override
    public void setResultListener(ResultListener resultListener) {
        mResultListener = resultListener;
    }

    private String getEngineNewParam() {
        JSONObject engineParams = new JSONObject();
        try {
            engineParams.put("appKey", mGradeConfig.appKey);
            engineParams.put("secretKey", mGradeConfig.secretKey);
            engineParams.put("provision", mGradeConfig.provisionPath);
            if (mGradeConfig.isDebug) {
                JSONObject profObject = new JSONObject();
                profObject.put("enable", 1);
                profObject.put("output", mGradeConfig.logPath);
                engineParams.put("prof", profObject);
            }

            JSONObject cloudObject = new JSONObject();
            cloudObject.put("server", "ws://cloud.chivox.com:8080");
            engineParams.put("cloud", cloudObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return engineParams.toString();
    }

    private String getEngineStartParam(String refText, int coreType, String courseId) {
        JSONObject engineParams = new JSONObject();
        try {
            engineParams.put("coreProvideType", "cloud");
            // volumeEnable是否返回音量值，默认0不返回
            engineParams.put("volumeEnable", 0);
            JSONObject appObject = new JSONObject();
            appObject.put("userId", mGradeConfig.userId);
            engineParams.put("app", appObject);

            JSONObject audioObject = new JSONObject();
            audioObject.put("audioType", "wav");
            audioObject.put("channel", 1);
            audioObject.put("sampleBytes", 2);
            audioObject.put("sampleRate", 16000);
            engineParams.put("audio", audioObject);

            JSONObject requestObject = new JSONObject();
            if (GradeConfig.CORE_TYPE_WORD == coreType) {
                requestObject.put("coreType", "en.word.score");
            } else if (GradeConfig.CORE_TYPE_SENT == coreType) {
                requestObject.put("coreType", "en.sent.score");
                JSONObject resultObject = new JSONObject();
                JSONObject detailObject = new JSONObject();
                detailObject.put("raw", mGradeConfig.sentRaw);
                detailObject.put("sym", mGradeConfig.sentSymbol);
                resultObject.put("details", detailObject);
                requestObject.put("result", resultObject);
            } else if (CORE_TYPE_PRED == coreType) {
                requestObject.put("coreType", "en.pred.exam");
                requestObject.put("precision", mGradeConfig.predPrecision);
                JSONObject clientParams = new JSONObject();
                clientParams.put("ext_word_details", mGradeConfig.predWordDetail); // 启用单词详细评分
                clientParams.put("ext_phn_details", mGradeConfig.predPhonemeDetails); // 启用单词详细评分
                clientParams.put("ext_subitem_rank4", 0); // 0：不启用,分制跟随rank
                requestObject.put("client_params", clientParams);
            }
            requestObject.put("rank", mGradeConfig.scoreType);
            requestObject.put("attachAudioUrl", 1);
            if (GradeConfig.CORE_TYPE_WORD == coreType || GradeConfig.CORE_TYPE_SENT == coreType) {
                requestObject.put("refText", refText);
            } else {
                JSONObject refTextObject = new JSONObject();
                refTextObject.put("lm", refText);
                requestObject.put("refText", refTextObject);
            }
            requestObject.put("course_id", courseId);
            engineParams.put("request", requestObject);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return engineParams.toString();
    }

    private GradeResult parseJson(String jsonResult) {
        GradeResult result;
        try {
            result = new GradeResultImpl();
            JSONObject jsonObject = new JSONObject(jsonResult);

            JSONObject resultJson = jsonObject.getJSONObject("result");
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
//            JSONObject rhythmJson = resultJson.getJSONObject("rhythm");
//            result.rhythmScore = rhythmJson.optInt("overall");
        } catch (JSONException e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }
}
