package com.feizhu.dubgrade;

import android.content.Context;

/**
 * Created by zhouhl on 2016/11/11.
 * 打分引擎
 */

public interface GradeEngine {

    int ERROR_CODE = 888;

    boolean init(Context context, GradeConfig gradeConfig);

    int start(String content);

    void writeAudio(byte[] data, int size);

    void stop();

    void destroy();

    void setResultListener(ResultListener resultListener);

    interface ResultListener {

        void onResult(GradeResult result);
    }
}
