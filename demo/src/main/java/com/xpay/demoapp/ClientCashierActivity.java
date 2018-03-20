package com.xpay.demoapp;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.xpay.sdk.XPay;

/**
 * 客户收银台DEMO页面
 *
 * @author 402536196@qq.com
 */
public class ClientCashierActivity extends Activity implements OnClickListener {
    /**
     * 开发者需要填一个商户服务端的URL,该URL是用来获取支付需要的支付凭证charge。
     * 主要注意，调用URL能够返回json格式的charge对象。
     */
    public static final String CHARGE_SERVER_URL = "http://106.15.61.30:8087/payment/api_payment";

    /**
     * 支付宝移动支付
     */
    private static final String ALI_APP_ALI = "ALI_APP_ALI";
    /**
     * 支付宝扫码支付通联渠道
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

        aliPayScanAllinpayButton.setOnClickListener(ClientCashierActivity.this);
        wxAppButton.setOnClickListener(ClientCashierActivity.this);
        aliPayAppButton.setOnClickListener(ClientCashierActivity.this);
    }

    @Override
    public void onClick(View view) {
        PaymentItem paymentItem = new PaymentItem();
        //支付金额，单位为分
        paymentItem.setAmount(1);
        paymentItem.setSubject("测试商品名称");
        paymentItem.setBody("测试商品信息描述");
        paymentItem.setRemark("Android移动支付测试备注信息");

        if (view.getId() == R.id.aliPayAppButton) {
            paymentItem.setChannel(ALI_APP_ALI);
        } else if (view.getId() == R.id.aliPayScanAllinpayButton) {
            paymentItem.setChannel(ALI_SCAN_ALLINSYB);
        } else if (view.getId() == R.id.wxPayAppButton) {
            paymentItem.setChannel(WX_APP_WX);
        }
        new PaymentTask().execute(paymentItem);
    }

    class PaymentTask extends AsyncTask<PaymentItem, Void, String> {
        @Override
        protected void onPreExecute() {
            //按键点击之后的禁用，防止重复点击
            aliPayScanAllinpayButton.setOnClickListener(null);
            wxAppButton.setOnClickListener(null);
            aliPayAppButton.setOnClickListener(null);
        }

        @Override
        protected String doInBackground(PaymentItem... pr) {
            PaymentItem paymentItem = pr[0];
            String data = null;
            try {
                StringBuffer formStr = new StringBuffer();
                formStr.append("channel=" + paymentItem.getChannel())
                        .append("&sub_channel=" + paymentItem.getChannel())
                        .append("&amount=" + paymentItem.getAmount())
                        .append("&subject=" + paymentItem.getSubject())
                        .append("&body=" + paymentItem.getBody())
                        .append("&remark=" + paymentItem.getRemark());
                data = HttpRequestUtil.sendGet(CHARGE_SERVER_URL, formStr.toString());
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
            //获取到的charge或order的JSON字符串
            XPay.createPayment(ClientCashierActivity.this, data);

        }
    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到支付平台服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        aliPayScanAllinpayButton.setOnClickListener(ClientCashierActivity.this);
        wxAppButton.setOnClickListener(ClientCashierActivity.this);
        aliPayAppButton.setOnClickListener(ClientCashierActivity.this);

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
                String errorMsg = data.getExtras().getString("error_msg");
                String extraMsg = data.getExtras().getString("extra_msg");
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
        Builder builder = new Builder(ClientCashierActivity.this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

}
