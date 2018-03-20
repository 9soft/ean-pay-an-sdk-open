Android 中集成  SDK
============
 
## 快速体验
导入ean-pay-an-sdk-open整个项目，做如下修改，即可运行该demo。 

修改ClientSDKActivity类中参数YOUR_URL为自己支付服务器端charge生成接口（具体详见服务端开发文档）。

例如：YOUR_URL = "http://192.168.1.100:8088/payment/api_payment";

<font color="red">需要注意: </font>确保服务器端获取charge接口畅通。

## 开始接入
### 步骤1：添加相应的依赖到项目中
#### 使用gradle依赖，将xpay_sdk.aar文件复制到libs目录，增加build.gradle内容。

``` groovy
dependencies {
    compile(name: 'xpay_sdk', ext: 'aar')
    compile fileTree(include: ['*.jar'], dir: 'libs')
    provided files('libs/org.simalliance.openmobileapi.jar')
}
```

### 步骤2：在清单文件中声明所需权限
<font color='red'>(注：有些权限是需要动态注册的,如"READ_PHONE_STATE"权限)</font>

``` xml
    <!-- 通用权限 -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
    
### 步骤3：在清单文件中注册相应的组件
-  SDK所需要注册组件

``` xml
        <activity
                android:name="com.xpay.demoapp.MainActivity"
                android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
        <activity android:name="com.xpay.demoapp.ClientSDKActivity">
        </activity>
```

### 步骤4：获取到charge后，调起支付
#### 获取charge
charge 对象是一个包含支付信息的 JSON 对象，是  SDK 发起支付的必要参数。该参数需要请求用户服务器获得，服务端生成 charge 的方式参考 服务端接入简介。SDK 中的 demo 里面提供了如何获取 charge 的实例方法，供用户参考。

#### 调起支付
因为  已经封装好了相应的调用方法，所以只需要调用支付方法即可调起支付控件：
(<font color='red'>注：该调用方法需要在主线程(UI线程)完成</font>)

- 渠道调起支付方式：

``` java_holder_method_tree
//参数一：Activity  当前调起支付的Activity
//参数二：data  获取到的charge或order的JSON字符串
XPay.createPayment(YourActivity.this, data);
```


### 步骤5：获取支付结果
从 Activity 的 onActivityResult 方法中获得支付结果。支付成功后，用户服务器也会收到  服务器发送的异步通知。 (<font color='red'>ALI-SACN支付该方法无法获取支付结果，最终支付成功请根据服务端异步通知为准</font>)

<font color='red'>注意：</font>ALI-SCAN支付方式不能使用以下方式获取支付接结果。
后台异步程序会将支付结果异步通知到商户服务端，由商户服务端自行推送到移动端。

``` groovy
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //支付页面返回处理
    if (requestCode == XPay.REQUEST_CODE_PAYMENT) {
       String result = data.getExtras().getString("pay_result");
       /* 处理返回值
        * "success" - 支付成功
        * "fail"    - 支付失败
        * "cancel"  - 取消支付
        * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
        * "unknown" - app进程异常被杀死(一般是低内存状态下,app进程被杀死)
        */
       String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
       String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
    }
}
```




