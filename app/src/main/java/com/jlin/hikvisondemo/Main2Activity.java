package com.jlin.hikvisondemo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.jlin.hikvisondemo.jna.HCNetSDKJNAInstance;
import com.jlin.hikvisondemo.utils.CrashUtil;
import com.jlin.hikvisondemo.widget.PlaySurfaceView;

public class Main2Activity extends Activity {
    private static final String TAG = "MainActivity";

    private static final String IP_ADDRESS_1 = "192.168.1.200";
    private static final String IP_ADDRESS_2 = "192.168.1.146";

    private static final int PORT = 8000;
    private static final String ACCOUNT = "admin";
    private static final String PASSWORD = "Cunkou520";

    private NET_DVR_DEVICEINFO_V30 netDvrDeviceInfoV30 = null;
    private int loginId1 = -1; // return by NET_DVR_Login_v30
    private int playID1 = -1; // return by NET_DVR_RealPlay_V40
    private int playbackID1 = -1; // return by NET_DVR_PlayBackByTime
    private int startChan1 = 0; // start channel number
    private int chanNum1 = 0; // channel number
    private static PlaySurfaceView[] playView1 = new PlaySurfaceView[4];
    private SurfaceView surfaceView1 = null;

    private int loginId2 = -1; // return by NET_DVR_Login_v30
    private int playID2 = -1; // return by NET_DVR_RealPlay_V40
    private int playbackID2 = -1; // return by NET_DVR_PlayBackByTime
    private int startChan2 = 0; // start channel number
    private int chanNum2 = 0; // channel number
    private static PlaySurfaceView[] playView2 = new PlaySurfaceView[4];
    private SurfaceView surfaceView2 = null;

    TextView tvCapture1;
    TextView tvCapture2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashUtil crashUtil = CrashUtil.getInstance();
        crashUtil.init(this);
        setContentView(R.layout.activity_main2);

        tvCapture1 = findViewById(R.id.tv_capture_1);
        tvCapture2 = findViewById(R.id.tv_capture_2);

        if (netDvrDeviceInfoV30 == null) {
            netDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        }

        if (!initSdk()) {
            this.finish();
            return;
        }
        surfaceView1 = findViewById(R.id.sur_player1);
        surfaceView1.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceView1.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                Log.i(TAG, "11111 surface is created");
                if (-1 == playID1 && -1 == playbackID1) {
                    return;
                }
                playView1[0].m_hHolder = holder;
                Surface surface = holder.getSurface();
                if (surface.isValid()) {
                    if (playID1 != -1) {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(playID1, 0, holder)) {
                            Log.e(TAG, "11111 Player setVideoWindow failed!");
                        }
                    } else {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(playbackID1, 0, holder)) {
                            Log.e(TAG, "11111 Player setVideoWindow failed!");
                        }
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "11111 surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "11111 Player setVideoWindow release!");
                if (-1 == playID1 && -1 == playbackID1) {
                    return;
                }
                if (holder.getSurface().isValid()) {
                    if (playID1 != -1) {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(playID1, 0, null)) {
                            Log.e(TAG, "11111 Player setVideoWindow failed!");
                        }
                    } else {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(playbackID1, 0, null)) {
                            Log.e(TAG, "11111 Player setVideoWindow failed!");
                        }
                    }
                }
            }
        });

        surfaceView2 = findViewById(R.id.sur_player2);
        surfaceView2.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceView2.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                Log.i(TAG, "22222 surface is created");
                if (-1 == playID2 && -1 == playbackID2) {
                    return;
                }
                playView2[0].m_hHolder = holder;
                Surface surface = holder.getSurface();
                if (surface.isValid()) {
                    if (playID2 != -1) {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(playID2, 0, holder)) {
                            Log.e(TAG, "22222 Player setVideoWindow failed!");
                        }
                    } else {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(playbackID2, 0, holder)) {
                            Log.e(TAG, "22222 Player setVideoWindow failed!");
                        }
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "22222 surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "22222 Player setVideoWindow release!");
                if (-1 == playID2 && -1 == playbackID2) {
                    return;
                }
                if (holder.getSurface().isValid()) {
                    if (playID2 != -1) {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(playID2, 0, null)) {
                            Log.e(TAG, "22222 Player setVideoWindow failed!");
                        }
                    } else {
                        if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(playbackID2, 0, null)) {
                            Log.e(TAG, "22222 Player setVideoWindow failed!");
                        }
                    }
                }
            }
        });

        /*
         * 登录设备
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    loginId1 = loginDevice1();
                    if (loginId1 < 0) {
                        Log.e(TAG, "11111 This device login failed!");
                        return;
                    } else {
                        Log.i(TAG, "11111 loginId=" + loginId1);
                    }
                    ExceptionCallBack exception = getExceptionCbf();

                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exception)) {
                        Log.e(TAG, "11111 NET_DVR_SetExceptionCallBack is failed!");
                        return;
                    }
                    Log.i(TAG, "11111 Login success");
                } catch (Exception err) {
                    Log.e(TAG, "11111 error: " + err.toString());
                }
            }
        }, 800);

        /*
         * 登录设备
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    loginId2 = loginDevice2();
                    if (loginId2 < 0) {
                        Log.e(TAG, "22222 This device login failed!");
                        return;
                    } else {
                        Log.i(TAG, "22222 loginId=" + loginId2);
                    }
                    ExceptionCallBack exception = getExceptionCbf();

                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exception)) {
                        Log.e(TAG, "22222 NET_DVR_SetExceptionCallBack is failed!");
                        return;
                    }
                    Log.i(TAG, "22222 Login success");
                } catch (Exception err) {
                    Log.e(TAG, "22222 error: " + err.toString());
                }
            }
        }, 800);

        /*
         *开启预览
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSinglePreview1();
            }
        }, 2000);

        /*
         *开启预览
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSinglePreview2();
            }
        }, 2000);

        tvCapture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePictureBlock(playID1, "/sdcard/capblock1.jpg", 0)) {
                    Log.i(TAG, "11111 NET_DVR_CapturePictureBlock Succ!");
                } else {
                    Log.e(TAG, "11111 NET_DVR_CapturePictureBlock fail! Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            }
        });

        tvCapture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePictureBlock(playID2, "/sdcard/capblock2.jpg", 0)) {
                    Log.i(TAG, "22222 NET_DVR_CapturePictureBlock Succ!");
                } else {
                    Log.e(TAG, "22222 NET_DVR_CapturePictureBlock fail! Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            }
        });
    }

    /**
     * @return true - success;false - fail
     * @fn initeSdk
     * @author zhuzhenlei
     * @brief SDK init
     */
    private boolean initSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }

    private int loginDevice1() {
        int iLogID;
        iLogID = loginNormalDevice1();
        return iLogID;
    }

    private int loginNormalDevice1() {
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(IP_ADDRESS_1, PORT, ACCOUNT, PASSWORD, netDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "11111 NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }

        if (netDvrDeviceInfoV30.byChanNum > 0) {
            startChan1 = netDvrDeviceInfoV30.byStartChan;
            chanNum1 = netDvrDeviceInfoV30.byChanNum;
        } else if (netDvrDeviceInfoV30.byIPChanNum > 0) {
            startChan1 = netDvrDeviceInfoV30.byStartDChan;
            chanNum1 = netDvrDeviceInfoV30.byIPChanNum + netDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        if (chanNum1 > 1) {
            ChangeSingleSurFace1(false);
        } else {
            ChangeSingleSurFace1(true);
        }
        Log.i(TAG, "11111 NET_DVR_Login is Successful!");
        return iLogID;
    }

    private void ChangeSingleSurFace1(boolean bSingle) {
        for (int i = 0; i < 4; i++) {
            if (playView1[i] == null) {
                playView1[i] = new PlaySurfaceView(this);
                playView1[i].setParam(surfaceView1.getMeasuredWidth());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView1[i].getM_iHeight() - (i / 2) * playView1[i].getM_iHeight();
                params.leftMargin = (i % 2) * playView1[i].getM_iWidth();
                params.gravity = Gravity.BOTTOM | Gravity.START;
                addContentView(playView1[i], params);
                playView1[i].setVisibility(View.INVISIBLE);
            }
        }

        if (bSingle) {
            for (int i = 0; i < 4; ++i) {
                playView1[i].setVisibility(View.INVISIBLE);
            }
            playView1[0].setParam(surfaceView1.getMeasuredWidth());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = (int) surfaceView1.getY();
            params.gravity = Gravity.TOP | Gravity.START;
            playView1[0].setLayoutParams(params);
            playView1[0].setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < 4; ++i) {
                playView1[i].setVisibility(View.VISIBLE);
            }

            playView1[0].setParam(surfaceView1.getMeasuredWidth());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView1[0].getM_iHeight();
            params.leftMargin = 0;
            params.gravity = Gravity.BOTTOM | Gravity.START;
            playView1[0].setLayoutParams(params);
        }
    }

    private void startSinglePreview1() {
        if (playbackID1 >= 0) {
            Log.i(TAG, "11111 Please stop playback first");
            return;
        }

        Log.i(TAG, "11111 m_iStartChan:" + startChan1);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = startChan1;
        previewInfo.dwStreamType = 0; // main stream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView1[0].m_hHolder;

        playID1 = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(loginId1, previewInfo, null);
        if (playID1 < 0) {
            Log.e(TAG, "11111 NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
    }

    private int loginDevice2() {
        int iLogID;
        iLogID = loginNormalDevice2();
        return iLogID;
    }

    private int loginNormalDevice2() {
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(IP_ADDRESS_2, PORT, ACCOUNT, PASSWORD, netDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "22222 NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }

        if (netDvrDeviceInfoV30.byChanNum > 0) {
            startChan2 = netDvrDeviceInfoV30.byStartChan;
            chanNum2 = netDvrDeviceInfoV30.byChanNum;
        } else if (netDvrDeviceInfoV30.byIPChanNum > 0) {
            startChan2 = netDvrDeviceInfoV30.byStartDChan;
            chanNum2 = netDvrDeviceInfoV30.byIPChanNum + netDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        if (chanNum2 > 1) {
            ChangeSingleSurFace2(false);
        } else {
            ChangeSingleSurFace2(true);
        }
        Log.i(TAG, "22222 NET_DVR_Login is Successful!");
        return iLogID;
    }

    private void ChangeSingleSurFace2(boolean bSingle) {
        for (int i = 0; i < 4; i++) {
            if (playView2[i] == null) {
                playView2[i] = new PlaySurfaceView(this);
                playView2[i].setParam(surfaceView2.getMeasuredWidth());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView2[i].getM_iHeight() - (i / 2) * playView2[i].getM_iHeight();
                params.leftMargin = (i % 2) * playView2[i].getM_iWidth();
                params.gravity = Gravity.BOTTOM | Gravity.START;
                addContentView(playView2[i], params);
                playView2[i].setVisibility(View.INVISIBLE);
            }
        }

        if (bSingle) {
            for (int i = 0; i < 4; ++i) {
                playView2[i].setVisibility(View.INVISIBLE);
            }
            playView2[0].setParam(surfaceView2.getMeasuredWidth());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = (int) surfaceView2.getY();
            params.gravity = Gravity.TOP | Gravity.START;
            playView2[0].setLayoutParams(params);
            playView2[0].setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < 4; ++i) {
                playView2[i].setVisibility(View.VISIBLE);
            }

            playView2[0].setParam(surfaceView2.getMeasuredWidth());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView2[0].getM_iHeight();
            params.leftMargin = 0;
            params.gravity = Gravity.BOTTOM | Gravity.START;
            playView2[0].setLayoutParams(params);
        }
    }

    private void startSinglePreview2() {
        if (playbackID2 >= 0) {
            Log.i(TAG, "22222 Please stop playback first");
            return;
        }

        Log.i(TAG, "22222 m_iStartChan:" + startChan2);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = startChan2;
        previewInfo.dwStreamType = 0; // main stream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView2[0].m_hHolder;

        playID2 = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(loginId2, previewInfo, null);
        if (playID2 < 0) {
            Log.e(TAG, "22222 NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
    }

    private ExceptionCallBack getExceptionCbf() {
        return new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("playID1", playID1);
        outState.putInt("playID2", playID2);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        playID1 = savedInstanceState.getInt("playID1");
        playID2 = savedInstanceState.getInt("playID2");
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }
}
