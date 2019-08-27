package com.jlin.hikvisondemo.jna;

import android.graphics.Bitmap;
import android.os.Environment;

import com.jlin.hikvisondemo.jna.HCNetSDKByJNA.NET_DVR_ALARMER;
import com.jlin.hikvisondemo.utils.CommonMethod;
import com.sun.jna.Pointer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class JNATest {

    private static JNATest jnaTest = new JNATest();
    public int m_lAlarmIDV41 = -1;

    public static class FMSGCallBack implements HCNetSDKByJNA.FMSGCallBack {
        @Override
        public void invoke(int lCommand, NET_DVR_ALARMER pAlarmer,
                           Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            // TODO Auto-generated method stub
            System.out.println("alarm type:" + lCommand);
            if (lCommand == HCNetSDKByJNA.COMM_ALARM_V30) {
                HCNetSDKByJNA.NET_DVR_ALARMINFO_V30 struAlarmInfo = new HCNetSDKByJNA.NET_DVR_ALARMINFO_V30(pAlarmInfo);
                struAlarmInfo.read();
                System.out.println("COMM_ALARM_V30 alarm type:" + struAlarmInfo.dwAlarmType);
            } else if (lCommand == HCNetSDKByJNA.COMM_ALARM_V40) {
                HCNetSDKByJNA.NET_DVR_ALARMINFO_V40 struAlarmInfo = new HCNetSDKByJNA.NET_DVR_ALARMINFO_V40(pAlarmInfo);
                struAlarmInfo.read();
                System.out.println("COMM_ALARM_V40 alarm type:" + struAlarmInfo.struAlarmFixedHeader.dwAlarmType);
            } else if (lCommand == HCNetSDKByJNA.COMM_UPLOAD_PLATE_RESULT) {
                HCNetSDKByJNA.NET_DVR_PLATE_RESULT struAlarmInfo = new HCNetSDKByJNA.NET_DVR_PLATE_RESULT(pAlarmInfo);
                struAlarmInfo.read();
                try {
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
                    String date = sDateFormat.format(new java.util.Date());
                    FileOutputStream file = new FileOutputStream("/mnt/sdcard/" + date + ".bmp");
                    file.write(struAlarmInfo.pBuffer1.getPointer().getByteArray(0, struAlarmInfo.dwPicLen), 0, struAlarmInfo.dwPicLen);
                    file.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("COMM_UPLOAD_PLATE_RESULT license:" + CommonMethod.toValidString(new String(struAlarmInfo.struPlateInfo.sLicense)));
            } else if (lCommand == HCNetSDKByJNA.COMM_ITS_PLATE_RESULT) {
                HCNetSDKByJNA.NET_ITS_PLATE_RESULT struAlarmInfo = new HCNetSDKByJNA.NET_ITS_PLATE_RESULT(pAlarmInfo);
                struAlarmInfo.read();
                System.out.println("COMM_ITS_PLATE_RESULT license:" + CommonMethod.toValidString(new String(struAlarmInfo.struPlateInfo.sLicense)));
            } else if (lCommand == HCNetSDKByJNA.COMM_ALARM_RULE) {
                HCNetSDKByJNA.NET_VCA_RULE_ALARM struAlarmInfo = new HCNetSDKByJNA.NET_VCA_RULE_ALARM(pAlarmInfo);
                struAlarmInfo.read();
                if (struAlarmInfo.struRuleInfo.wEventTypeEx == HCNetSDKByJNA.ENUM_VCA_EVENT_EXIT_AREA) {
                    HCNetSDKByJNA.NET_VCA_AREA struExit = new HCNetSDKByJNA.NET_VCA_AREA(struAlarmInfo.struRuleInfo.uEventParam.getPointer());
                    struExit.read();
                }
                System.out.println("COMM_ALARM_RULE rule name:" + CommonMethod.toValidString(new String(struAlarmInfo.struRuleInfo.byRuleName)));
            } else if (lCommand == HCNetSDKByJNA.COMM_VEHICLE_CONTROL_ALARM) {
                HCNetSDKByJNA.NET_DVR_VEHICLE_CONTROL_ALARM struAlarmInfo = new HCNetSDKByJNA.NET_DVR_VEHICLE_CONTROL_ALARM(pAlarmInfo);
                struAlarmInfo.read();
                System.out.println("NET_DVR_VEHICLE_CONTROL_ALARM license:" + struAlarmInfo.byListType + "byPlateType:" + struAlarmInfo.byPlateType +
                        "sLicense:" + CommonMethod.toValidString(new String(struAlarmInfo.sLicense)));
            } else if (lCommand == HCNetSDKByJNA.COMM_UPLOAD_FACESNAP_RESULT) {
                HCNetSDKByJNA.NET_VCA_FACESNAP_RESULT struFaceSnapAlarm = new HCNetSDKByJNA.NET_VCA_FACESNAP_RESULT(pAlarmInfo);
                struFaceSnapAlarm.read();
                System.out.println("FMSGCallBack：" + "invoke" + "==== " + struFaceSnapAlarm.dwFaceScore);

                mDataCallBack.callback(struFaceSnapAlarm.dwFaceScore);
//                System.out.println("COMM_UPLOAD_FACESNAP_RESULT dwFacePicID:" + struFaceSnapAlarm.dwFacePicID
//                        + "\nFaceScore:" + struFaceSnapAlarm.dwFaceScore
//                        + "\ndwBackgroundPicLen:" + struFaceSnapAlarm.dwBackgroundPicLen);
//                //人脸抓拍图片保存
//                if (struFaceSnapAlarm.dwFacePicLen > 0 && struFaceSnapAlarm.dwFaceScore > 40 && struFaceSnapAlarm.pBuffer1 != null) {
//                    byte[] b = struFaceSnapAlarm.pBuffer1.getPointer().getByteArray(0, struFaceSnapAlarm.dwFacePicLen);
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                    saveBitmapToLocal(bitmap);
//                }
            } else if (lCommand == HCNetSDKByJNA.COMM_ALARM_PDC) {
                HCNetSDKByJNA.NET_DVR_PDC_ALRAM_INFO struAlarmPdc = new HCNetSDKByJNA.NET_DVR_PDC_ALRAM_INFO(pAlarmInfo);
                struAlarmPdc.read();
                System.out.println("COMM_ALARM_PDC dwSnapFacePicID:" + struAlarmPdc.dwEnterNum);
            } else if (lCommand == HCNetSDKByJNA.COMM_ALARM_FACE_DETECTION) {
                HCNetSDKByJNA.NET_DVR_FACE_DETECTION struFaceDetect = new HCNetSDKByJNA.NET_DVR_FACE_DETECTION(pAlarmInfo);
                struFaceDetect.read();
                System.out.println("COMM_ALARM_FACE_DETECTION byFacePicNum:" + struFaceDetect.byFacePicNum);
            } else if (lCommand == HCNetSDKByJNA.COMM_SNAP_MATCH_ALARM) {
                HCNetSDKByJNA.NET_VCA_FACESNAP_MATCH_ALARM struFaceSnapMatchAlarm = new HCNetSDKByJNA.NET_VCA_FACESNAP_MATCH_ALARM(pAlarmInfo);
                struFaceSnapMatchAlarm.read();
                System.out.println("COMM_SNAP_MATCH_ALARM dwSnapFacePicID:" + struFaceSnapMatchAlarm.byMatchPicNum);
            }
        }
    }

    private static void saveBitmapToLocal(Bitmap bitmap) {
        try {
            // 创建文件流，指向该路径，文件名叫做fileName
            File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
            // file其实是图片，它的父级File是文件夹，判断一下文件夹是否存在，如果不存在，创建文件夹
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                // 文件夹不存在
                fileParent.mkdirs();// 创建文件夹
            }
            // 将图片保存到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final HCNetSDKByJNA.FMSGCallBack fMSFCallBack = new FMSGCallBack();

    public void Test_Alarm_V41(int iUserID) {
        if (m_lAlarmIDV41 < 0) {
            Pointer pUser = null;
            if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRMessageCallBack_V30(fMSFCallBack, pUser)) {
                System.out.println("NET_DVR_SetDVRMessageCallBack_V30 failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            }

            HCNetSDKByJNA.NET_DVR_SETUPALARM_PARAM arlarmParam = new HCNetSDKByJNA.NET_DVR_SETUPALARM_PARAM();
            arlarmParam.dwSize = arlarmParam.size();
            arlarmParam.byRetDevInfoVersion = 1;
            arlarmParam.byFaceAlarmDetection = 1;  // 1 - COMM_ALARM_FACE_DETECTION; 0 - COMM_UPLOAD_FACESNAP_RESULT
            arlarmParam.write();
            m_lAlarmIDV41 = HCNetSDKJNAInstance.getInstance().NET_DVR_SetupAlarmChan_V41(iUserID, arlarmParam.getPointer());
        } else {
            HCNetSDKJNAInstance.getInstance().NET_DVR_CloseAlarmChan_V30(m_lAlarmIDV41);
            m_lAlarmIDV41 = -1;
        }
    }

    private static DataCallBack mDataCallBack;

    public static void TEST_Config(int iUserID, DataCallBack dataCallBack) {
        jnaTest.Test_Alarm_V41(iUserID);
        mDataCallBack = dataCallBack;
    }

    public interface DataCallBack{
        void callback(int score);
    }
}


