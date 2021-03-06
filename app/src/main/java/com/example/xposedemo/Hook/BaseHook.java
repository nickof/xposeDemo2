package com.example.xposedemo.Hook;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.bean.BaseInfo;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

import de.robv.android.xposed.XC_MethodHook;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Member;


public class BaseHook {

    private static final String TAG = "BaseHook";
    public  BaseInfo baseInfo;
    public BaseHook(XC_LoadPackage.LoadPackageParam loadPackageParam){
        hookAll(  baseInfo,loadPackageParam  );

    }

    public void hookMethod(final String className, final ClassLoader classLoader, final String methodName, final String result){
        XposedHelpers.findAndHookMethod(className, classLoader, methodName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod( MethodHookParam param ) throws Throwable {
                super.afterHookedMethod( param );
                param.setResult( result );
                XposedBridge.log("result is " + result  );
            }
        });

    }


    //public static Field findField(Class<?> clazz, String fieldName)
    /**
     *
     */
    public void findFieldHook(Class<?> clazz, String fieldName, Object obj, Object value ){
       
    }

    /**
     *
     * @param baseInfo
     */
    private void hookAll(final BaseInfo baseInfo, XC_LoadPackage.LoadPackageParam loadPackageParam) {

        String json= MyFile.readFileToString( HookShare.pathDeviceJson );
        final JSONObject jsonObject= JSON.parseObject  ( json );

        hookMethod("android.bluetooth.BluetoothAdapter", loadPackageParam.classLoader,"getAddress", String.valueOf(jsonObject.get("bluemac")) );
        hookMethod("android.bluetooth.BluetoothDevice", loadPackageParam.classLoader,"getAddress", (String)(jsonObject.get("bluemac"))  );
        hookMethod("android.os.Build", loadPackageParam.classLoader,"getRadioVersion", (String) jsonObject.get("radioVersion"));
        //??????field???
        try {
            XposedHelpers.findField(Build.class,"BOARD").set(null,jsonObject.get( "board" ) );
            XposedHelpers.findField(Build.class, "SERIAL").set(null,  jsonObject.get("serial") ); //???????????????
            XposedHelpers.findField(Build.class, "BRAND").set(null, jsonObject.get("brand")  ); // ????????????
            XposedHelpers.findField(Build.class, "CPU_ABI").set(null, jsonObject.get("cpu_abi")  );
           //XposedHelpers.findField(Build.class, "CPU_ABI2").set(null, jsonObject.get("cpu_abi2")  );
            XposedHelpers.findField(Build.class, "DEVICE").set(null, jsonObject.get("device") );
            XposedHelpers.findField(Build.class, "DISPLAY").set(null, jsonObject.get("display")  );
            XposedHelpers.findField(Build.class, "FINGERPRINT").set(null, jsonObject.get("fingerprint")  );
            XposedHelpers.findField(Build.class, "HARDWARE").set(null, jsonObject.get("hardware") );
            XposedHelpers.findField(Build.class, "ID").set(null, jsonObject.get("id") );
            XposedHelpers.findField(Build.class, "MANUFACTURER").set(null, jsonObject.get("manufacturer") );
            XposedHelpers.findField(Build.class, "MODEL").set(null, jsonObject.get("model") );
            XposedHelpers.findField(Build.class, "PRODUCT").set(null, jsonObject.get("product") );
            XposedHelpers.findField(Build.class, "BOOTLOADER").set(null, jsonObject.get("bootloader") ); //??????????????????
            XposedHelpers.findField(Build.class, "HOST").set(null, jsonObject.get("host") );  // ??????????????????
            XposedHelpers.findField(Build.class, "TAGS").set(null, jsonObject.get("tags") );  //??????build?????????
            XposedHelpers.findField(Build.class, "TYPE").set(null, jsonObject.get("type") ); //??????????????????
            XposedHelpers.findField(Build.VERSION.class, "INCREMENTAL").set(null, jsonObject.get("incremental")  ); //?????????????????????
            XposedHelpers.findField(Build.VERSION.class, "RELEASE").set(null, jsonObject.get("release")  );
            XposedHelpers.findField(Build.VERSION.class, "SDK").set(null, jsonObject.get("sdk") );
            XposedHelpers.findField(Build.VERSION.class, "CODENAME").set(null, "REL"); //???????????? ??????????????????
            XposedHelpers.findField(Build.class, "TIME").set(null, Long.parseLong((String) jsonObject.get("buildtime") )   );  // ????????????build

        } catch (Throwable e)
        {
            XposedBridge.log("?????? Build ??????!" + e.getMessage());
        }

       try {

            XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPackageParam.classLoader, "get", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String baseBand = (String) param.args[0];
                    if("gsm.version.baseband".equals(baseBand)||"no message".equals(baseBand)){
                        Log.d(TAG, "afterHookedMethod: baseBand="+jsonObject.get("baseBand")  );
                        param.setResult( jsonObject.get("baseBand") );
                    }

                }
            });

            XposedHelpers.findAndHookMethod(Settings.Secure.class, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //accessibility_captioning_locale
                    Object obj=param.getResult();
                    if ( obj==null )
                        Log.d(TAG, "afterHookedMethod: systemHook="+param.args[1]+"-result=null");
                    else
                        Log.d(TAG, "afterHookedMethod: systemHook="+param.args[1]+"-result="+obj.toString() );
                    //+"result="+param.getResult().toString() );
                    if (param.args[1].equals(Settings.Secure.ANDROID_ID)){
                        Log.d(TAG, "afterHookedMethod: android_id="+jsonObject.get("android_id")  );
                        param.setResult( jsonObject.get("android_id") );
                    }

                }

            });
        } catch (Throwable e) {
            XposedBridge.log("?????? androidid ??????!" + e.getMessage());
        }

        Class<?> cls = null;
        try {
            cls = Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(cls != null){
            for (Member mem : cls.getDeclaredMethods()) {
                XposedBridge.hookMethod(mem, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.beforeHookedMethod(param);
                        // ?????????KEY
                        if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.build.description")) {
                            Log.d(TAG, "afterHookedMethod: discription="+jsonObject.get("discription")  );
                            param.setResult(  jsonObject.get("discription") );
                        }
                    }
                });
            }
        }


    }



}
