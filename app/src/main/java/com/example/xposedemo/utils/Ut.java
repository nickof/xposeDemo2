package com.example.xposedemo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MainActivity;
import com.example.xposedemo.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class Ut {

    protected static final String PREFS_FILE = "gank_device_id.xml";
    protected static final String PREFS_DEVICE_ID = "gank_device_id";
    private static final String TAG = "DeviceUtils";
    protected static String uuid;


//
    public static void getSystemPermission(){
        // android:sharedUserId="android.uid.system"
        //https://www.jianshu.com/p/b26201f2b4bb
        //https://blog.csdn.net/u013491946/article/details/78191591  androidStudio??????
    }


    public static char rLetter(){
        int c='a'+(int)(Math.random()*26);
        return  (char)c;
    }

    public static String rLetterN(int n){

        StringBuilder sb=new StringBuilder("");
        for (int i=0;i<n;i++){
            char c= (char) ('a'+(int)(Math.random()*26));
            sb.append(c);
        }

        String ret=sb.toString();
        Log.d(TAG, "rLetterN: "+ret);
        return  ret;
    }

    /**
     * ????????????????????????
     *     ????????????????????????CSDN?????????????????????????????????????????????????????????CC 4.0 BY-SA???????????????????????????????????????????????????????????????
     *     ???????????????https://blog.csdn.net/qq_35703234/article/details/58585536
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isRunning ( Context context,String serviceName) {

        ActivityManager mAM= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices =
                mAM.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            String className = runningService.service.getClassName();
            if (serviceName.equals(className)){
                return true;
            }
        }
        return false;
    }

   // https://blog.csdn.net/weixin_35997133/article/details/117314861

    public static void stopAppByForce(Context context ,String packageName) {
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = null;
        try {
            method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void startApplicationWithPackageName(String packagename, Context context) {

        // ?????????????????????APP?????????????????????Activities???services???versioncode???name??????
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // ?????????????????????CATEGORY_LAUNCHER???????????????Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // ??????getPackageManager()???queryIntentActivities????????????
        List<ResolveInfo> resolveinfoList =context. getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = ??????packname
            String packageName = resolveinfo.activityInfo.packageName;
            // ??????????????????????????????APP???LAUNCHER???Activity[???????????????packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // ??????ComponentName??????1:packagename??????2:MainActivity??????
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }


    /**
     *
     * @param activity
     * @param packageName
     */
    public static String getActivities(Context activity, String packageName)
    {
        Intent localIntent = new Intent("android.intent.action.MAIN", null);
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> appList =  activity.getPackageManager().queryIntentActivities(localIntent, 0);
        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo resolveInfo = appList.get(i);
            String packageStr = resolveInfo.activityInfo.packageName;
            if (packageStr.equals(packageName)) {
                //??????????????????????????????Activity
                android.util.Log.e("", "" + resolveInfo.activityInfo.name);
                return resolveInfo.activityInfo.name;
                //break;
            }
        }
        return null;
    }

    public static String getIPAddress(Context context) {

        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//????????????2G/3G/4G??????
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//????????????????????????
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP( wifiInfo.getIpAddress() );//??????IPV4??????
                return ipAddress;
            }
        } else {
            //?????????????????????,???????????????????????????
        }
        return null;
    }



    /**
     * join list with joiner
     * @param list
     * @param joiner
     * @return
     */
    public static String joinListString( List<String> list,  String joiner  ){
        if (list.size()<1)
            return null;

        StringBuilder sb=new StringBuilder( list.get(0) );
        for (int i=1;i<list.size();i++){
            sb.append( joiner );
            sb.append( list.get( i ) );
        }
        return sb.toString();



    }

    //
    public static void  stopAppByKill(Context context ,String packageName) {
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.killBackgroundProcesses(packageName);
    }
    /**
     * ????????????int?????????IP?????????String??????
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    /**
     * ????????????
     *
     * @param path
     * @param list
     * @throws IOException
     */
    public static void writeLines(String path, List<String> list) {

        File fout = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (String string :
                    list) {
                bw.write(string);
                bw.newLine();
            }
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "writeLines: FileNotFoundException" + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "writeLines: IOException" + e);
        }


    }

    /**
     * @param min
     * @param max
     * @return
     */
    public static int r_(int min, int max) {
        int randomNumber;
        randomNumber = (int) (((max - min + 1) * Math.random() + min));
        return randomNumber;
    }

    public static String r_num_n(int n,int min,int max) {

        StringBuilder sb=new StringBuilder("");
        for (int i=0;i<n;i++){
            sb.append( r_(min,max) );
        }
        Log.d(TAG, "r_num_n: "+sb.toString());
        return sb.toString();

    }

    public static String r_num_n(int n) {

       StringBuilder sb=new StringBuilder("");
       for (int i=0;i<n;i++){
            sb.append( r_(0,9) );
       }
        Log.d(TAG, "r_num_n: "+sb.toString());
       return sb.toString();

    }

    /**
     * ???list????????????n?????????
     *
     * @param listObj
     * @param n
     * @return
     */
    public static <T> List<T> r_list(List<T> listObj, int n) {
        Collections.shuffle(listObj);
        List<T> listRet = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listRet.add(listObj.get(i));
        }
        return listRet;
    }



    /**
     * ??????txt???????????????????????????list???
     */
    public static List<String> readLines(String path) {
        List<String> list = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            list = new ArrayList<String>();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.trim().length() > 2) {
                    list.add(str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "getFileContext: FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "getFileContext: IOException");
        }
        return list;
    }



    /***
     * ???bean????????????json ?????????txt
     * @param o
     * @param pathJson
     */
    public static void WriteBean2Json(Object o, String pathJson ){

        Class c=o.getClass();
        Field[] fields=c.getDeclaredFields();
        Log.d(TAG, "WriteBean2Json: "+c.getName() );
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON( o );
        Ut.fileWriterTxt (pathJson, jsonObject.toJSONString() );
        Log.d(TAG, "WriteBean2Json: json="+jsonObject.toJSONString()  );

    }

    /**
     * @param path ???????????????
     */
    public static void crFolder( String path ) {


        File file = new File( path );
        if (!file.exists()) {
            file.mkdir();
        }

    }

    /**
     * ????????????????????????
     * @param path
     * @return
     */
    public static boolean crFolderEx(String path){

        if (path.indexOf("/")<0 )
        {
            Log.d(TAG, "crFolderEx: ??????????????????");
            return false;
        }

        String[] strings=path.split("/");

        String strConect="";
        strConect=strings[0] ;
        crFolder( strConect );

        if ( strings.length>1 ) {
            for (int i = 1; i < strings.length; i++) {
                strConect = strConect + "/" + strings[i];
                Log.d(TAG, "crFolderEx: "+strConect);
                crFolder( strConect );
            }
        }
        return true;

    }

    /**
     *  ????????????true??? { "key1","key2".. }  ,???????????? ???????????????????????????
     * @param pathJsonTxt  txt?????? txtJson?????? { "key":boolean }
     * @return
     */
    public static List<String>  getSelectedJobjByPath ( String pathJsonTxt ) {

        String json= readFileToString( pathJsonTxt );
        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=null;

        List<String> listRet=null;

        if( jsonObject ==null)
            return null;

        listRet=new ArrayList<>();
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                Log.d(TAG, "returnSelectedJobj: "+entry.getKey()  );
                    listRet.add( entry.getKey() );
            }
        }
        return listRet;
    }


    /**
     *  ????????????true??? { "key1","key2".. }  ,???????????? ???????????????????????????
     * @param json  txt?????? txtJson?????? { "key":boolean }
     * @return
     */
    public static List<String>  getSelectedJobjByJson ( String json ) {

        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=null;

        List<String> listRet=null;
        if( jsonObject ==null)
            return null;

        listRet=new ArrayList<>();
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                Log.d(TAG, "returnSelectedJobj: "+entry.getKey()  );
                listRet.add( entry.getKey() );
            }
        }
        return listRet;

    }


    public static boolean[] listBooleanToArray( List<Boolean> listBoolean ){

        if ( listBoolean.size()<1 )
            return null;
        boolean[] selected=new boolean[ listBoolean.size() ];
        Iterator<Boolean> iterator=listBoolean.iterator();
        int i=0;
        while ( iterator.hasNext() ) {
            selected[i]=iterator.next().booleanValue();
            i=i+1;
        }

        return selected;
    }

    /**
     *
     * @param context
     * @return
     */
    public static List<String> getPackageNames( Context context ){

        PackageManager packageManager = context.getPackageManager();
        //???????????????????????????????????????
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //??????????????????????????????????????????
        List<String> packageNames = new ArrayList<>();
        //???pinfo????????????????????????????????????pName list???
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add( packName );
            }
        }
        return packageNames;

    }

    /**
     * ??????
     * @param context
     * @param items  new String[]{ "????????????" }
     */
    private void dialogShow(final Context context, String[] items ){

        final String[] items2=items;

        //dialog????????????
        AlertDialog.Builder builder=new AlertDialog.Builder(context);  //??????????????????
        builder.setTitle("settingPath"); //????????????
        builder.setIcon(R.mipmap.ic_launcher);

        //?????????????????????id??????
        //???????????????????????????????????????????????????????????????builder.setMessage()?????????????????????????????????
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(context, items2[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("??????",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }



    /**
     * ??????????????????
     * @param properties
     * @return
     */
    public  static Object getSystemProperties( String properties )  {
        String arch = "";//cpu??????
        Method get;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            get = clazz.getDeclaredMethod("get", new Class[]{String.class});
            return get.invoke(clazz, new Object[]{properties});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  null;
    }

    public static void fileWriterTxt(String filePath,String content) {
        FileWriter fwriter = null;
        try {
            // true???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            fwriter = new FileWriter( filePath );
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * ??????uuid
     * @param context
     * @return
     */
    static public String getUDID( Context context )
    {
        if( uuid ==null ) {
            synchronized ( MainActivity.class ) {
                if( uuid == null) {

                     SharedPreferences prefs = context.getSharedPreferences( PREFS_FILE, 0);
                     String id = prefs.getString( PREFS_DEVICE_ID, null );
                    Log.d(TAG, "getUDID: LOCAL"+id );
                    id=null;

                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = id;
                    } else {

                        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                                Log.d(TAG, "getUDID: if"+uuid );
                            } else {
                                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                                Log.d(TAG, "getUDID: ELSE" );
                                uuid = deviceId!=null ? UUID.nameUUIDFromBytes( deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();


                    }
                }
            }
        }
        return uuid;
    }


    //http://web.rslnano.com/yhapi.ashx?act=getPhoneCode&token=83f6f31d1c8b6566bbbc8886c04d8406_647&mobile=65330483&iid=3211
    public static String readAssetsTxt( Context context,String fileName ){

        String text=null;
        try {
            InputStream is = context.getAssets().open( fileName );
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            text = new String(buffer, "GB2312");
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
        return text;





    }



    public static String  rLetterCapital(int n){

        StringBuilder stringBuilder=new StringBuilder("");

        for (int i=0;i<n;i++){
            //char c=(char)(int)(Math.random()*26+97);
            int ii=(int)Math.round(Math.random()*26);
            int j=(int)'A'+ii;
            char c=(char)j;
            stringBuilder.append(c);
        }
        Log.d(TAG, "rLetterCapital: ="+stringBuilder.toString() );
        return  stringBuilder.toString();

    }

    public static boolean copyAssetsFile(Context context, String assetsPath, String savePath)  {
        // assetsPath ???????????? /assets
        try{
            InputStream is = context.getAssets().open(assetsPath);
            FileOutputStream fos = new FileOutputStream(new File(savePath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// ????????????????????????
                // buffer??????
                fos.write(buffer, 0, byteCount);// ???????????????????????????????????????
            }
            fos.flush();// ???????????????
            is.close();
            fos.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * ???????????????????????????????????????????????????
     */
    public static String getGoogleAdId(Context context) throws Exception {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return "Cannot call in the main thread, You must call in the other thread";
        }
        PackageManager pm = context.getPackageManager();
        pm.getPackageInfo("com.android.vending", 0);
        AdvertisingConnection connection = new AdvertisingConnection();
        Intent intent = new Intent(
                "com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                AdvertisingInterface adInterface = new AdvertisingInterface(
                        connection.getBinder());
                return adInterface.getId();
            } finally {
                context.unbindService(connection);
            }
        }
        return "";
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<>(1);

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        IBinder getBinder() throws InterruptedException {
            if (this.retrieved)
                throw new IllegalStateException();
            this.retrieved = true;
            return this.queue.take();
        }
    }

    public static final class AdvertisingInterface implements IInterface {
        private IBinder binder;

        AdvertisingInterface(IBinder pBinder) {
            binder = pBinder;
        }

        public IBinder asBinder() {
            return binder;
        }

        public String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean)
                throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }

    /**
     * ???txt????????????
     * @param FILE_IN txt??????
     * @return  ?????????string
     */
    public static String readFileToString ( String FILE_IN ) {
        String str="";
        File file=new File( FILE_IN );

        try {
            FileInputStream in=new FileInputStream(file);
            // size ?????????????????? ????????????????????????
            int size=in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            str=new String(buffer,"UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    public static String txt2String(String path){

        File file=new File(path);
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//????????????BufferedReader??????????????????
            String s = null;
            while((s = br.readLine())!=null){//??????readLine????????????????????????
                result.append(System.lineSeparator()+s );
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * ??????map
     * @param params
     * @param Delimiter ?????????
     * @return
     */
    public static String  join_map2str (Map<String, String> params, String Delimiter ) {
        List<String> keys = new ArrayList<String>(params.keySet());
        // Collections.sort(keys);//?????????????????????, ?????????????????????????????????
        StringBuilder prestrSB = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
//            if (encode) {
//                try {
//                    value = URLEncoder.encode(value, "GBK");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
            if (i == keys.size() - 1) {// ?????????????????????????????????&??????
                prestrSB.append(key).append("=").append(value);
            } else {
                prestrSB.append(key).append("=").append(value).append(Delimiter);
            }
        }
        return prestrSB.toString();
    }

}

