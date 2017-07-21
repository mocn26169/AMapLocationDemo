package com.amap.location.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyChooseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_choose);
    }

    public void gaode(View view) {
        startActivity(new Intent(this, StartActivity.class));
    }

    public void gps(View view) {
        startActivity(new Intent(this, MyGPSActivity.class));
    }

    public void mix(View view) {
        startActivity(new Intent(this, MixActivity.class));
    }

}
