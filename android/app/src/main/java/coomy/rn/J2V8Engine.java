package coomy.rn;

import android.os.Handler;
import android.os.HandlerThread; // TODO:android
import android.os.Looper;
import android.os.Message;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;

public class J2V8Engine {

    private static String _TAG_ = "J2V8";
    private static J2V8Engine sInstance = null;
    private String mJsFramework = "";
    private V8 mRuntime = null;

    private HandlerThread mThread = new HandlerThread(_TAG_);
    private Handler mHandler = null;
    private Message mMessage = null;

    private boolean mIsInitialized = false;

    public class J2V8ThreadHandler extends Handler {

        public static final int MSG_INIT = 1;
        public static final int MSG_RENDER = 2;

        J2V8ThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    mRuntime = V8.createV8Runtime();
                    mJsFramework = loadJsFile("util.js") + "\r\n" + loadJsFile("element.js");

                    Object o = mRuntime.executeScript(mJsFramework);
                    if (o instanceof Releasable) {
                        ((Releasable) o).release();
                    }

                    mIsInitialized = true;
                    break;
                case MSG_RENDER:
                    break;
            }
        }
    }

    public static J2V8Engine getInstance() {
        if (sInstance == null) {
            synchronized (J2V8Engine.class) {
                if (sInstance == null) {
                    sInstance = new J2V8Engine();
                }
            }
        }
        return sInstance;
    }

    public J2V8Engine() {
        initial();
    }

    // must called
    private void initial() {
        if (mThread == null)
            mThread = new HandlerThread(_TAG_);

        if (!mThread.isAlive())
            mThread.start();

        mHandler = new J2V8ThreadHandler(mThread.getLooper());

        mMessage = mHandler.obtainMessage();
        mMessage.what = J2V8ThreadHandler.MSG_INIT;
        mHandler.sendMessage(mMessage);
    }

    public Element make(String scriptFile) {
        if (!mIsInitialized)
            initial();

//        mMessage = mHandler.obtainMessage();
//        mMessage.what = J2V8ThreadHandler.MSG_RENDER;
//        mMessage
//        mHandler.sendMessage(mMessage);
        // 带返回值的怎么玩？？？

        String res = mRuntime.executeStringScript(loadJsFile(scriptFile));

        Gson gson = new GsonBuilder().registerTypeAdapter(
                Element.class, new ElementTokenizer()).create();
        return gson.fromJson(res, Element.class);
    }

    public void release() {
        if (!mIsInitialized)
            return;

        mJsFramework = "";
        mRuntime.release();
        mRuntime = null;

        mIsInitialized = false;
    }

    public static String loadJsFile(String fileName) {
        String result = "";
        try {
            InputStream in = ContextHolder.getContext().getResources().getAssets().open(fileName);
            int lenght = in.available();
            byte[] buffer = new byte[lenght];
            in.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
