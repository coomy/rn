package coomy.rn;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static String getFile(Context ctx, String fileName) {
        String result = "";
        try {
            InputStream in = ctx.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        V8 runtime = V8.createV8Runtime();
        int result = runtime.executeIntegerScript(
                "var hello = 'hello' \n" +
                "var world = 'world' \n" +
                 "hello.concat(world).length"
        );

        JavaVoidCallback callback = new JavaVoidCallback() {
            @Override
            public void invoke(V8Object v8Object, V8Array v8Array) {
                if (v8Array.length() > 0) {
                    Object arg1 = v8Array.get(0);
                    Log.e("CoomyRn", "js callback args : " + arg1);
                    if (arg1 instanceof Releasable) {
                        ((Releasable) arg1).release();
                    }
                }
                if (v8Object instanceof Releasable) {
                    ((Releasable) v8Object).release();
                }
                if (v8Array instanceof Releasable) {
                    ((Releasable) v8Array).release();
                }
            }
        };

        runtime.registerJavaMethod(callback, "androidCallback");
        runtime.executeScript("androidCallback('hello j2v8')");

        Log.e("CoomyRn", "JS result is : " + result);

        Object retUtil = runtime.executeScript(getFile(this, "util.js") + "\r\n" + getFile(this, "element.js"));
//        Object retElement = runtime.executeScript(getFile(this, "element.js"));
        if (retUtil instanceof Releasable) {
            ((Releasable) retUtil).release();
        }
//        if (retElement instanceof Releasable) {
//            ((Releasable) retElement).release();
//        }
        Log.e("Coomy", String.valueOf(runtime.getObjectReferenceCount()));

        String res = runtime.executeStringScript(getFile(this,"main.js"));

        Log.e("Coomy", "Result : " + res);

        Gson gson = new GsonBuilder().registerTypeAdapter(Element.class, new ElementTokenizer()).create();
        String Json = "{\"tagName\":\"div\",\"props\":{\"id\":\"container\"},\"childrens\":[{\"tagName\":\"h1\",\"props\":{\"style\":\"color: red\"},\"childrens\":[\"simple virtal dom\"]},{\"tagName\":\"p\",\"props\":{},\"childrens\":[\"hello world\"]}]}";
        Element el = gson.fromJson(Json, Element.class);
        String jsonnn = gson.toJson(el);

        Log.e("Coomy", "Result : " + el.getTagName() + jsonnn);


        J2V8Engine engine = J2V8Engine.getInstance();
        Element eee = engine.make("main.js");
        Log.e("Coomy", "Result : " + eee.getTagName());
        engine.release();

    }
}
