package com.zhl.gradedemo;

import android.Manifest;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.feizhu.dubgrade.GradeConfig;
import com.feizhu.dubgrade.GradeEngine;
import com.feizhu.dubgrade.GradeEngineFactory;
import com.feizhu.dubgrade.GradeResult;

import java.io.File;
import java.util.Date;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private TextView mTvResult;
    private TextView mTvContent;

    private GradeEngine mGradeEngine;
    private GradeConfig mGradeConfig;
    private Recorder mRecorder;

    private Activity mActivity;

    private int mEngineType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        mTvResult = (TextView) findViewById(R.id.tv_result);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        Button btnCreateChiShengEngine = (Button) findViewById(R.id.btn_create_chisheng_engine);
        Button btnCreateXunFeiEngine = (Button) findViewById(R.id.btn_create_xunfei_engine);
        Button btnDestroyEngine = (Button) findViewById(R.id.btn_destroy_engine);
        Button btnStartRecord = (Button) findViewById(R.id.btn_start_record);
        Button btnStopRecord = (Button) findViewById(R.id.btn_stop_record);

        mTvContent.setText("how are you");

        btnCreateXunFeiEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEngineType = GradeEngineFactory.TYPE_XUNFEI;
                MainActivityPermissionsDispatcher.showReadPhoneStateWithCheck(MainActivity.this);
            }
        });

        btnCreateChiShengEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEngineType = GradeEngineFactory.TYPE_CHISHENG;
                MainActivityPermissionsDispatcher.showReadPhoneStateWithCheck(MainActivity.this);
            }
        });

        btnDestroyEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGradeEngine.destroy();
            }
        });

        btnStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ret = mGradeEngine.start(mTvContent.getText().toString());
                if (ret != GradeEngine.ERROR_CODE) {
                    MainActivityPermissionsDispatcher.showRecordAudioWithCheck(MainActivity.this);
                } else {
                    mTvResult.setText("先创建引擎");
                }
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecorder.stop();
                mGradeEngine.stop();
            }
        });

    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    void showReadPhoneState() {
        File provisionFile = FileHelper.extractProvisionOnce(this, "aiengine.provision");
        if (provisionFile == null) {
            return;
        }
        String logFileName = "sdk." + new Date().getTime() + ".log";
        String logPath = FileHelper.getFilesDir(this).getPath() + "/" + logFileName;

        mGradeEngine = GradeEngineFactory.createGradeEngine(mEngineType);
        mGradeEngine.setResultListener(new GradeEngine.ResultListener() {
            @Override
            public void onResult(final GradeResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            mTvResult.setText(result.toString());
                        } else {
                            mTvResult.setText("失败了吧!");
                        }
                    }
                });
            }

            @Override
            public void onError(final int code, String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "评分失败(" + code + ")", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mGradeConfig = new GradeConfig.Builder()
                .setAppId(IgnoreConstants.appId)
                .setAppKey(IgnoreConstants.appKey)
                .setSecretKey(IgnoreConstants.secretKey)
                .setDebug(true)
                .setProvisionPath(provisionFile.getAbsolutePath())
                .setLogPath(logPath)
                .setUserId("DemoUser1")
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean ret = mGradeEngine.init(MainActivity.this, mGradeConfig);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ret) {
                            mTvResult.setText("引擎创建成功!");
                        } else {
                            mTvResult.setText("引擎创建失败!");
                        }
                    }
                });
            }
        }).start();

    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void showRecordAudio() {
        if (mRecorder == null) {
            mRecorder = new Recorder();
        }
        String recordFilePath = FileHelper.getFilesDir(mActivity)
                .getPath() + "/record/" + System.currentTimeMillis() + ".wav";
        mRecorder.start(recordFilePath, new Recorder.Callback() {
            public void run(byte[] data, int size) {
                mGradeEngine.writeAudio(data, size);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
