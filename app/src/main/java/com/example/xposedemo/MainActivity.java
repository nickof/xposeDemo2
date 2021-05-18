package com.example.xposedemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DirectAction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.bean.BaseInfo;
import com.example.xposedemo.fake.FackBase;
import com.example.xposedemo.utils.Common;
import com.example.xposedemo.utils.Ut;
import com.example.xposedemo.utils.SharedPref;
import com.example.xposedemo.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    TelephonyManager tm;
    TelecomManager tm2;
    private TextView viewById;
    private String string;
    private   Context context =  null ;
    private Button bt_new;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewById=findViewById(R.id.textView);
        viewById.setText("no data");

        //setSelectedPackges();
        ui();

            //testHook();
            Ut.copyAssetsFile(context,"cpuinfo","/sdcard/cpuinfo" );
            String path=Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, "onCreate: path="+path);

        deviceLog();
        // String jsonStr= Utils.readFileToString(Environment.getExternalStorageDirectory ()+"/device.txt");

        String jsonStr= Utils.readFileToString( Common.DEVICE_PATH );

        if (jsonStr!=""&&jsonStr!=null){

            JSONObject jsonObjectPara;
            Log.d(TAG, "onCreate: jsonStr="+jsonStr );
            Map<String,String> map=JSONObject.parseObject(jsonStr,
                    new TypeReference< Map<String, String> >(){});
            string=Utils.join_map2str( map,"\r\n");
            Log.d(TAG, "onCreate: mapStr="+string );
            viewById.setText(string);

        }

        Log.d(TAG, "onCreate: run finish");


    }

    public void setSelectedPackges() {
        String json= Ut.readFileToString( HookShare.pathPackages );
        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=new JSONObject();
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                jobj2.put( entry.getKey(),true );
            }
        }
        Ut.fileWriterTxt( HookShare.pathSelectedPackages,jobj2.toJSONString() );
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: " );
        if ( HookShare.boolIsPackageEmpty()!=true )
            dialogShow( new String[]{ "未指定app" } );
        super.onStop();

    }

    private void deviceLog() {

        TelephonyManager  telephonyManager =( TelephonyManager )getApplicationContext(). getSystemService( Context.TELEPHONY_SERVICE );
        Log.d(TAG, "onCreate:getSubscriberId"+telephonyManager.getSubscriberId() );
        Log.d(TAG, "onCreate:getDeviceId"+telephonyManager.getDeviceId  () );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            Log.d(TAG, "onCreate: getDeviceId="+telephonyManager.getDeviceId(0) );
            Log.d(TAG, "onCreate: getSubscriberId="+telephonyManager. getSubscriberId () );
            Configuration configuration=getResources().getConfiguration();
            //Log.d(TAG,  "colorMode-"+configuration.colorMode );
            Log.d(TAG,  "configAll densityDpi-"+configuration.densityDpi );
            Log.d(TAG,  "configAll fontScale-"+configuration.fontScale );
            Log.d(TAG,  "configAll hardKeyboardHidden-"+configuration.hardKeyboardHidden );
            Log.d(TAG,  "configAll keyboard-"+configuration.keyboard );
            Log.d(TAG,  "configAll orientation-"+configuration.orientation );
            Log.d(TAG,  "configAll screenHeightDp-"+configuration.screenHeightDp );
            Log.d(TAG,  "configAll screenLayout-"+configuration.screenLayout );
            Log.d(TAG,  "configAll screenWidthDp-"+configuration.screenWidthDp );
            Log.d(TAG,  "configAll smallestScreenWidthDp-"+configuration.smallestScreenWidthDp );
            Log.d(TAG,  "configAll uiMode-"+configuration.uiMode );
            Log.d(TAG,  "configAll keyboardHidden-"+configuration.keyboardHidden );
            Log.d(TAG,  "configAll navigation-"+configuration.navigation );
            Log.d(TAG,  "configAll navigationHidden-"+configuration.navigationHidden );
            Log.d(TAG,  "configAll touchscreen-"+configuration.touchscreen );
            Log.d(TAG, "onCreate: getConfiguration mccmnc="+configuration.mcc+configuration.mnc );
            Log.d(TAG, "onCreate: getConfiguration="+configuration.toString()  );
            Log.d(TAG, "onCreate: getSystemProperties-ro.product.cpu.abi-"+ Ut.getSystemProperties("ro.product.cpu.abi")  );
            Log.d(TAG, "onCreate: getSystemProperties-ro.build.description-"+ Ut.getSystemProperties("ro.build.description")  );
            Log.d(TAG, "onCreate: getSystemProperties-gsm.version.baseband-"+Ut.getSystemProperties("gsm.version.baseband")  );


            Log.d(TAG, "onCreate:"   ) ;
            Log.d(TAG, "onCreate: build model-"+Build.MODEL );
            Log.d(TAG, "onCreate: build MANUFACTURER-"+Build.MANUFACTURER );
            Log.d(TAG, "onCreate: build BRAND-"+Build.BRAND );
            Log.d(TAG, "onCreate: build HARDWARE-"+Build.HARDWARE );
            Log.d(TAG, "onCreate: build BOARD-"+Build.BOARD );
            Log.d(TAG, "onCreate: build SERIAL-"+Build.SERIAL );
            Log.d(TAG, "onCreate: build DEVICE-"+Build.DEVICE );
            Log.d(TAG, "onCreate: build FINGERPRINT-"+Build.FINGERPRINT );
            Log.d(TAG, "onCreate: build DISPLAY-"+Build.DISPLAY );
            Log.d(TAG, "onCreate: build ID-"+Build.ID  );
            Log.d(TAG, "onCreate: build HOST-"+Build.HOST  );
            Log.d(TAG, "onCreate: build TAGS-"+Build.TAGS  );
            Log.d(TAG, "onCreate: build TIME-"+Build.TIME  );
            Log.d(TAG, "onCreate: build .VERSION.INCREMENTAL-"+Build.VERSION.INCREMENTAL  );
            Log.d(TAG, "onCreate: build .VERSION.RELEASE-"+Build.VERSION.RELEASE  );
            Log.d(TAG, "onCreate: build .VERSION.CODENAME-"+Build.VERSION.CODENAME  );

           // Log.d(TAG, "onCreate: build TAGS-"+Build.   );
            Log.d(TAG, "onCreate: build getRadioVersion-"+Build.getRadioVersion()  );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "onCreate: build getSerial-"+Build.getSerial() );
            }
            Log.d(TAG, "onCreate: build BOOTLOADER-"+Build.BOOTLOADER  );
            Log.d(TAG, "onCreate: build HOST-"+Build.HOST  );
            Log.d(TAG, "onCreate: build TAGS-"+Build.TAGS  );
            Log.d(TAG, "onCreate: build TYPE-"+Build.TYPE  );
            Log.d(TAG, "onCreate: build RADIO-"+Build.RADIO  );
            Log.d(TAG, "onCreate: build TIME-"+Build.TIME  );
            //Log.d(TAG, "onCreate: build HOST-"  );

        }
    }

    public void ui(){

        //
        Button bt_permission=(Button) findViewById(R.id.bt_permission);
        bt_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPermissionManager1();
            }
        });

        //settingPath
        Button bt_showPath=( Button )findViewById(R.id.bt_showPath );
        bt_showPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPath();
            }
        });

        //packages
        Button bt_package=  (Button)  findViewById(R.id.bt_package);
        bt_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectPackageName();
            }
        });

//device
        bt_new=(Button) findViewById(R.id.bt_new);
        bt_new.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseInfo baseInfo = FackBase.getInstance();
                HookShare.WriteBean2Json( baseInfo );
                dialogShowDevice();
            }
        });


    }


    private  void dialogSelectPackageName(){

        List<String> listStringPackages =Ut.getPackageInfo( MainActivity.this );
        List<Boolean> listBooleanPackages=new ArrayList<>();

        String jsonTxtPackages=Ut.readFileToString( HookShare.pathPackages );
        final JSONObject jsonObject;

        if ( jsonTxtPackages=="" ){
            {Log.d(TAG, "dialogSelectPackageName: first run");
            jsonObject=new JSONObject();
            for ( String string : listStringPackages  ) {
                jsonObject.put( string,false );
                listBooleanPackages.add(false);
            }
            Ut.fileWriterTxt( HookShare.pathPackages ,jsonObject.toJSONString() );}
        }else {
            Log.d(TAG, "dialogSelectPackageName: read last");
            jsonObject=JSON.parseObject( jsonTxtPackages );
            for ( String string :
                    listStringPackages  ) {
                Log.d(TAG, "dialogSelectPackageName: k,v="+string+","+jsonObject.getBoolean( string )  );
                listBooleanPackages.add ( jsonObject.getBoolean (string) );
            }
        }

        final String items[]=listStringPackages.toArray( new String[ listStringPackages.size() ] );
       // final Boolean[] selectedTmp=listBooleanPackages.toArray( new Boolean[listBooleanPackages.size()] ) ;

        boolean[] selected=new boolean[ listStringPackages.size() ];
        boolean tp=false;

        Iterator<Boolean> iterator=listBooleanPackages.iterator();
        int i=0;
        while ( iterator.hasNext() ) {
            selected[i]=iterator.next().booleanValue();
            i=i+1;
        }

        Log.d(TAG, "dialogSelectPackageName: selected-size=" +selected.length );
        final AlertDialog.Builder builder=new AlertDialog.Builder( MainActivity.this );  //先得到构造器
        Log.d( TAG, "dialogSelectPackageName: alertBuild" );
        builder.setTitle("appPackage"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可

        for ( boolean b :
         selected    ) {
            Log.d(TAG, "dialogSelectPackageName: boolean="+ b);
        }


        List<String> packageSeleted=new ArrayList<>();
            builder.setMultiChoiceItems(items,selected,new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // dialog.dismiss();
                    if( isChecked )
                        jsonObject.put( items[which],true );
                        else
                        jsonObject.put( items[which],false );
                    //Toast.makeText(MainActivity.this, items[which]+isChecked, Toast.LENGTH_SHORT).show();
                }
            });


            builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Ut.fileWriterTxt( HookShare.pathPackages,jsonObject.toJSONString() );
                    dialog.dismiss();
                    //Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
                    //android会自动根据你选择的改变selected数组的值。
                    /*
                        for (int i=0;i<selected.length;i++){
                        Log.e("hongliang",""+selected[i]);
                    }
                    */
                }
            });

            AlertDialog alertDialo=builder.create();
            alertDialo.show();

    }

    private void dialogShowDevice () {

        String json=Ut.readFileToString( HookShare.pathDeviceJson );
        JSONObject jobj=  JSON.parseObject ( json);

        ArrayList<String> list=new ArrayList<>();
        for (Map.Entry entry : jobj.entrySet()) {
            list.add( entry.getKey ()+":"+entry.getValue() );
        }

       final String items[]= list.toArray( new String[ list.size() ] );

        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);  //先得到构造器
        builder.setTitle("参数"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);

        //设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("返回",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();

    }

    private void dialogShow( String[] items ){

        final String[] items2=items;

        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);  //先得到构造器
        builder.setTitle("settingPath"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);

        //设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, items2[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("返回",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void dialogShowPath(){

        final String items[]= { "phone:sdcard/yztc/device.txt","Base:/sdcard/deviceJson.txt","/sdcard/packages.txt" };
        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);  //先得到构造器
        builder.setTitle("settingPath"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);

        //设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("返回",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    public void testHook(){

        context=getApplicationContext();
        Pattern p = Pattern.compile("a*b");
        Matcher m = p.matcher("aaaaab");
        boolean b = m.matches();
        Log.d(TAG, "testHook: matches=true*patter=*"+b +m.pattern() );

    }

    public void  startPermissionManager1(){
        Intent intent=new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData( Uri.parse("package:jp.naver.line.android") );
        //intent.setFlags(0x10008000);
        intent.setComponent( new ComponentName("com.android.settings","com.android.settings.applications.InstalledAppDetailsTop" )   );
        startActivity(intent);
    }

    public void insert(String json){
        //①获取内容解析者
/*
        ContentResolver resolver = getContentResolver();
        Uri url = Uri.parse( Common.URI+"insert" );
        Log.d(TAG, "insert: =" +Common.URI+"insert" );
        ContentValues values = new ContentValues();
        values.put("device",json  );
        Uri insert = resolver.insert(url, values);
        System.out.println(insert);
*/
    }

    public void query(){
        //获取内容解析者
/*        ContentResolver contentResolver = getContentResolver();
        Uri uri =Uri.parse(Common.URI+"query");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
//	Cursor cursor = database.rawQuery("select * from info", null);

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("device"));
            String phone = cursor.getString(cursor.getColumnIndex("json"));
            System.out.println("name="+name+"phone"+phone);
        }*/

    }

    public String toastMessage() {
        Log.d(TAG, "toastMessage: toast-run");
         return "按钮未被劫持";
    }

    public  void saveImsi(){

        Log.d(TAG, "saveImsi: ");
        SharedPref mySP = new SharedPref( getApplicationContext() );
//    mySP.setSharedPref("IMSI","460017932859596");
//    mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
//    mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
//    mySP.setSharedPref("networktor","46001" ); // 网络运营商类型
//    mySP.setSharedPref("Carrier","中国联通" );// 网络类型名
//    mySP.setSharedPref("CarrierCode","46001" ); // 运营商
//    mySP.setSharedPref("simopename","中国联通" );// 运营商名字
//    mySP.setSharedPref("gjISO", "cn");// 国家iso代码
//    mySP.setSharedPref("CountryCode","cn" );// 手机卡国家

        mySP.setSharedPref("IMSI","250127932859596");
        mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
        mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
        mySP.setSharedPref("networktor","25012" ); // 网络运营商类型
        mySP.setSharedPref("Carrier","China Mobile/Peoples" );// 网络类型名
        mySP.setSharedPref("CarrierCode","25012" ); // 运营商
        mySP.setSharedPref("simopename","Baykal Westcom" );// 运营商名字
        mySP.setSharedPref("gjISO", "ru");// 国家iso代码
        mySP.setSharedPref("CountryCode","ru" );// 手机卡国家
        System.out.println( "run-ok" );



    }


}
