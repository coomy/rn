package coomy.rn;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;

public class J2V8Engine {


    private static J2V8Engine sInstance = null;
    private String mJsFramework = "";
    private V8 mRuntime = null;

    private boolean mIsInitialized = false;

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
        mRuntime = V8.createV8Runtime();
        mJsFramework = loadJsFile("util.js") + "\r\n" + loadJsFile("element.js");

        Object o = mRuntime.executeScript(mJsFramework);
        if (o instanceof Releasable) {
            ((Releasable) o).release();
        }

        mIsInitialized = true;
    }

    public Element make(String scriptFile) {
        if (!mIsInitialized)
            initial();

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
