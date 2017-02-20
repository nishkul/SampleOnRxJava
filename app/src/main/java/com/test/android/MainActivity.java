package com.test.android;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.test.android.dto.Pojo;
import com.test.android.rjx.RxEventBus;
import com.test.android.services.CustomService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public Button start, stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button)findViewById(R.id.button);
        stop = (Button)findViewById(R.id.button2);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        Fragment fragment = new MyFragmet();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.toString())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RxEventBus.getEventBus().sendToBus(new Pojo(data, resultCode));
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v==start) {
            //To call a Service or an another Activity we will need Intents (We will discuss about this later)
            Intent intent = new Intent(this, CustomService.class);
            this.startService(intent);
        }
        else {
            Intent intent = new Intent(this, CustomService.class);
            this.stopService(intent);
        }

    }
}
