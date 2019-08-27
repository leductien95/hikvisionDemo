package com.jlin.hikvisondemo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.jlin.hikvisondemo.jna.HCNetSDKJNAInstance;
import com.jlin.hikvisondemo.jna.JNATest;
import com.jlin.hikvisondemo.utils.CrashUtil;
import com.jlin.hikvisondemo.widget.PlaySurfaceView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "MainActivity";

    private static final String IP_ADDRESS = "192.168.1.200";
    private static final int PORT = 8000;
    private static final String ACCOUNT = "admin";
    private static final String PASSWORD = "Cunkou520";

    private NET_DVR_DEVICEINFO_V30 netDvrDeviceInfoV30 = null;

    private int loginId = -1; // return by NET_DVR_Login_v30
    private int playID = -1; // return by NET_DVR_RealPlay_V40
    private int playbackID = -1; // return by NET_DVR_PlayBackByTime

    private int startChan = 0; // start channel number
    private int chanNum = 0; // channel number
    private static PlaySurfaceView[] playView = new PlaySurfaceView[4];

    private SurfaceView surfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashUtil crashUtil = CrashUtil.getInstance();
        crashUtil.init(this);
        setContentView(R.layout.activity_main);

        if (!initSdk()) {
            this.finish();
            return;
        }
        surfaceView = findViewById(R.id.sur_player);
        surfaceView.getHolder().addCallback(this);

        /*
         * 登录设备
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    loginId = loginDevice();
                    if (loginId < 0) {
                        Log.e(TAG, "This device login failed!");
                        return;
                    } else {
                        Log.i(TAG, "loginId=" + loginId);
                    }
                    ExceptionCallBack exception = getExceptionCbf();

                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exception)) {
                        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                        return;
                    }
                    Log.i(TAG, "Login success");
                } catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                }
            }
        }, 800);

        /*
         *开启预览
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSinglePreview();
            }
        }, 2000);

        /*
         * 开启人脸侦测
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JNATest.TEST_Config(loginId, new JNATest.DataCallBack() {
                    @Override
                    public void callback(int score) {
                        System.out.println("MainActivity：" + "callback" + "==== " + score);
                    }
                });
            }
        }, 3000);

    }

    private boolean initSdk() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3,
                Environment.getExternalStorageDirectory().getPath() + "/sdklog/", true);
        return true;
    }

    private int loginDevice() {
        int iLogID;
        iLogID = loginNormalDevice();
        return iLogID;
    }

    private int loginNormalDevice() {
        netDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(IP_ADDRESS, PORT, ACCOUNT, PASSWORD, netDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }

        if (netDvrDeviceInfoV30.byChanNum > 0) {
            startChan = netDvrDeviceInfoV30.byStartChan;
            chanNum = netDvrDeviceInfoV30.byChanNum;
        } else if (netDvrDeviceInfoV30.byIPChanNum > 0) {
            startChan = netDvrDeviceInfoV30.byStartDChan;
            chanNum = netDvrDeviceInfoV30.byIPChanNum + netDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        if (chanNum > 1) {
            ChangeSingleSurFace(false);
        } else {
            ChangeSingleSurFace(true);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");
        return iLogID;
    }

    private void ChangeSingleSurFace(boolean bSingle) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        for (int i = 0; i < 4; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getM_iHeight() - (i / 2) * playView[i].getM_iHeight();
                params.leftMargin = (i % 2) * playView[i].getM_iWidth();
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                addContentView(playView[i], params);
                playView[i].setVisibility(View.INVISIBLE);

            }
        }

        if (bSingle) {
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.INVISIBLE);
            }
            playView[0].setParam(metric.widthPixels * 2);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[3].getM_iHeight() - (3 / 2) * playView[3].getM_iHeight();
            params.leftMargin = 0;
            // params.
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            playView[0].setLayoutParams(params);
            playView[0].setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.VISIBLE);
            }

            playView[0].setParam(metric.widthPixels);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[0].getM_iHeight() - (0 / 2) * playView[0].getM_iHeight();
            params.leftMargin = (0 % 2) * playView[0].getM_iWidth();
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            playView[0].setLayoutParams(params);
        }
    }

    private void startMultiPreview() {
        for (int i = 0; i < 4; i++) {
            playView[i].startPreview(loginId, startChan + i);
        }
    }

    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < 4; i++) {
            playView[i].stopPreview();
        }
        playID = -1;
    }

    private void startSinglePreview() {
        if (playbackID >= 0) {
            Log.i(TAG, "Please stop playback first");
            return;
        }

        Log.i(TAG, "m_iStartChan:" + startChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = startChan;
        previewInfo.dwStreamType = 0; // main stream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView[0].m_hHolder;

        playID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(loginId, previewInfo, null);
        System.out.println("MainActivity：" + "startSinglePreview" + "==== " + playID);
        if (playID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
    }

    private void stopSinglePreview() {
        if (playID < 0) {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }

        if (HCNetSDKJNAInstance.getInstance().NET_DVR_CloseSound()) {
            Log.e(TAG, "NET_DVR_CloseSound Succ!");
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(playID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        Log.i(TAG, "NET_DVR_StopRealPlay succ");
        playID = -1;
    }

    private ExceptionCallBack getExceptionCbf() {
        return new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created");
        if (-1 == playID && -1 == playbackID) {
            return;
        }
        playView[0].m_hHolder = holder;
        Surface surface = holder.getSurface();
        if (surface.isValid()) {
            if (playID != -1) {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(playID, 0, holder)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            } else {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(playbackID, 0, holder)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!");
        if (-1 == playID && -1 == playbackID) {
            return;
        }
        if (holder.getSurface().isValid()) {
            if (playID != -1) {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(playID, 0, null)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            } else {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(playbackID, 0, null)) {
                    Log.e(TAG, "Player setVideoWindow failed!");
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("playID", playID);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        playID = savedInstanceState.getInt("playID");
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }
}
