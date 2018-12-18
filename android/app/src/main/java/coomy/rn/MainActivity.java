package coomy.rn;

import android.content.Context;
import android.content.res.AssetManager;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            }
        };

        runtime.registerJavaMethod(callback, "androidCallback");
        runtime.executeScript("androidCallback('hello j2v8')");

        Log.e("CoomyRn", "JS result is : " + result);

//        String readJS = getFromAssets("test1.js");
//        runtime.executeScript(readJS);
////        runtime.executeScript(getFromAssets("test2.js"));
//        String res = runtime.executeStringScript(getFromAssets("test2.js"));

//        String res = runtime.executeStringScript("bbb()");
//        Log.e("CoomyRn", "JS result is : " + res);

        runtime.executeScript(getFile(this, "util.js"));
        runtime.executeScript(getFile(this, "element.js"));

        String res = runtime.executeStringScript(getFile(this,"main.js"));

        Log.e("Coomy", "Result : " + res);

        Gson gson = new GsonBuilder().registerTypeAdapter(Element.class, new ElementDeserializer()).create();
        String Json = "{\"tagName\":\"div\",\"props\":{\"id\":\"container\"},\"childrens\":[{\"tagName\":\"h1\",\"props\":{\"style\":\"color: red\"},\"childrens\":[\"simple virtal dom\"],\"count\":1},{\"tagName\":\"p\",\"props\":{},\"childrens\":[\"hello world\"],\"count\":1}],\"count\":15}";
        Element el = gson.fromJson(Json, Element.class);

        Log.e("Coomy", "Result : " + el.tagName);

        runtime.release();
    }
}
