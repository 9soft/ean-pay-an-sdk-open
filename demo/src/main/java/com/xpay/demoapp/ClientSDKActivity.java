package com.xpay.demoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.xpay.sdk.XPay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientSDKActivity extends Activity implements OnClickListener {
    /**
     * 开发者需要填一个服务端URL 该URL是用来请求支付需要的charge。务必确保，URL能返回json格式的charge对象。
     */
    private static String YOUR_URL = "http://106.15.61.30:8087/payment/api_payment";
    /**
     * 支付宝移动支付
     */
    private static final String ALI_APP_ALI = "ALI_APP_ALI";
    /**
     * 支付宝扫码支付
     */
    private static final String ALI_SCAN_ALLINSYB = "ALI_SCAN_ALLINSYB";
    /**
     * 微信APP支付
     */
    private static final String WX_APP_WX = "WX_APP_WX";

    private Button aliPayScanAllinpayButton;
    private Button wxAppButton;
    private Button aliPayAppButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_sdk);

        aliPayScanAllinpayButton = (Button) findViewById(R.id.aliPayScanAllinpayButton);
        wxAppButton = (Button) findViewById(R.id.wxPayAppButton);
        aliPayAppButton = (Button) findViewById(R.id.aliPayAppButton);

        aliPayScanAllinpayButton.setOnClickListener(ClientSDKActivity.this);
        wxAppButton.setOnClickListener(ClientSDKActivity.this);
        aliPayAppButton.setOnClickListener(ClientSDKActivity.this);
    }

    @Override
    public void onClick(View view) {
        int amount = 1;

        if (view.getId() == R.id.aliPayAppButton) {
            new PaymentTask().execute(new PaymentRequest(ALI_APP_ALI, amount));
        } else if (view.getId() == R.id.aliPayScanAllinpayButton) {
            new PaymentTask().execute(new PaymentRequest(ALI_SCAN_ALLINSYB, amount));
        } else if (view.getId() == R.id.wxPayAppButton) {
            new PaymentTask().execute(new PaymentRequest(WX_APP_WX, amount));
        }
    }

    class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {

        @Override
        protected void onPreExecute() {
            //按键点击之后的禁用，防止重复点击
            aliPayScanAllinpayButton.setOnClickListener(null);
            wxAppButton.setOnClickListener(null);
            aliPayAppButton.setOnClickListener(null);
        }

        @Override
        protected String doInBackground(PaymentRequest... pr) {
            PaymentRequest paymentRequest = pr[0];
            String data = null;
            try {
                StringBuffer formStr = new StringBuffer();
                formStr.append("channel=" + paymentRequest.channel).
                        append("&sub_channel=" + paymentRequest.channel).
                        append("&amount=" + paymentRequest.amount).
                        append("&subject=测试商品名称&body=测试商品信息描述&remark=Android移动支付测试");
                data = postForm(YOUR_URL, formStr.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        /**
         * 获得服务端的charge，调用sdk。
         */
        @Override
        protected void onPostExecute(String data) {
            if (null == data) {
                showMsg("请求出错", "请检查URL", "URL无法获取charge");
                return;
            }
            Log.d("charge", data);
            //参数一：Activity  当前调起支付的Activity
            //参数二：data  获取到的charge或order的JSON字符串
            XPay.createPayment(ClientSDKActivity.this, data);
        }

    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到支付平台服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        aliPayScanAllinpayButton.setOnClickListener(ClientSDKActivity.this);
        wxAppButton.setOnClickListener(ClientSDKActivity.this);
        aliPayAppButton.setOnClickListener(ClientSDKActivity.this);
        //支付页面返回处理
        if (requestCode == XPay.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null != msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null != msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new Builder(ClientSDKActivity.this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private static String postForm(String urlStr, String formParams) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        byte[] bypes = formParams.toString().getBytes();
        conn.getOutputStream().write(bypes);// 输入参数

        if (conn.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
        return null;
    }

    class PaymentRequest {
        String channel;
        String sub_channel;
        int amount;
        String subject;
        String body;
        String remark;

        public PaymentRequest(String channel, int amount) {
            this.channel = channel;
            this.amount = amount;
        }

        public PaymentRequest(String channel, String sub_channel, int amount, String subject, String body, String remark) {
            this.channel = channel;
            this.amount = amount;
            this.sub_channel = sub_channel;
            this.subject = subject;
            this.body = body;
            this.remark = remark;
        }
    }
}
