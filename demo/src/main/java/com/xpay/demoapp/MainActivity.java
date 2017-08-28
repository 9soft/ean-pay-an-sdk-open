package com.xpay.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.xpay.sdk.XPay;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private  static  String URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View v) {
        Class clz = null;
        switch (v.getId()) {
            case R.id.client_sdk:
                clz = ClientSDKActivity.class;
                Intent intent = new Intent(MainActivity.this, clz);
                startActivity(intent);
                break;
            case R.id.client_cashier:
//                Map map = new HashMap<String,Object>();
//                map.put("url",URL);
//                map.put("subject","测试商品名称");
//                map.put("body","测试商品信息描述");
//                map.put("amount","1");
//                map.put("remark","备注信息");
//                map.put("channel","ALI_SCAN,ALI_APP,WX_APP,UN_APP");
//                JSONObject data = new JSONObject(map);
//                XPay.createCashier(MainActivity.this, data.toString());
                break;
            default:
                break;
        }
    }
}
