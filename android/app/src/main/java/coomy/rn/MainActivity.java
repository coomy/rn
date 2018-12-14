package coomy.rn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.eclipsesource.v8.V8;

public class MainActivity extends AppCompatActivity {

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
        Log.e("CoomyRn", "JS result is : " + result);
        runtime.release();
    }
}
