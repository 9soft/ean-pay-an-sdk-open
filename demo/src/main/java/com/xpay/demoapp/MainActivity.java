package com.xpay.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.xpay.sdk.XPay;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * @author : 402536196@qq.com
 * Time: 2018-03-19 19:42
 */
public class MainActivity extends Activity {
    private  static  String YOUR_SERVER_URL = "http://106.15.61.30:8087/payment/api_payment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.client_sdk:
                Intent intent = new Intent(MainActivity.this, ClientCashierActivity.class);
                startActivity(intent);
                break;
            case R.id.client_cashier:
                Map map = new HashMap<String,Object>();
                map.put("url",YOUR_SERVER_URL);
                map.put("subject","测试商品名称");
                map.put("body","测试商品信息描述");
                map.put("amount","1");
                map.put("remark","备注信息");
                map.put("channel","ALI_SCAN,ALI_APP,WX_APP,UN_APP");
                JSONObject data = new JSONObject(map);
                XPay.createCashier(MainActivity.this, data.toString());
                break;
            default:
                break;
        }
    }
}
