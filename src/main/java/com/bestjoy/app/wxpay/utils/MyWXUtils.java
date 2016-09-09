package com.bestjoy.app.wxpay.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by bestjoy on 15/5/12.
 */
public class MyWXUtils {
    private static final MyWXUtils INSTANCE = new MyWXUtils();

    /**支持朋友圈分享,可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈*/
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private Context mContext;
    public static final String STitle = "showmsg_title";
    public static final String SMessage = "showmsg_message";
    public static final String BAThumbData = "showmsg_thumb_data";
    public String mAppId = "";
    /**API KEY*/
    public String mApiKey = "";
    /**商户ID*/
    public String mSellerMchId = "";
    private IWXAPI mApi;
    private boolean mRegisteAppResult = false;
    private MyWXUtils(){}

    public static class AppRegister extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyWXUtils.getInstance() != null) {
                MyWXUtils.getInstance().registerAppToWx(context);
            }

        }
    }

    public void setContext(Context context, String appId, String apiKey, String mchId) {
        mContext = context;
        mAppId = appId;
        mApiKey = apiKey;
        mSellerMchId = mchId;
        mApi = WXAPIFactory.createWXAPI(context, null);
    }

    public void setContext(Context context, String appId) {
        mContext = context;
        mAppId = appId;
        mApi = WXAPIFactory.createWXAPI(context, null);
    }

    public static MyWXUtils getInstance() {
        return INSTANCE;
    }

    public String getAppId() {
        return mAppId;
    }
    public String getApiKey() {
        return mApiKey;
    }
    public String getSellerMchId() {
        return mSellerMchId;
    }

    public IWXAPI getIWXAPI() {
        return mApi;
    }

    public void registerAppToWx(Context context) {
        if (mApi != null) {
            mRegisteAppResult = mApi.registerApp(mAppId);
        }
    }

    /**
     * 是否支持朋友圈分享
     * @return
     */
    public boolean isSupportWXSceneTimeline() {

        if (hasRegisterApp()) {
            return mApi.getWXAppSupportAPI() >= TIMELINE_SUPPORTED_VERSION;
        }
        return false;
    }

    public boolean hasRegisterApp() {
        return mRegisteAppResult;
    }


    /**
     * 发送到朋友圈
     * @param title
     * @param message
     * @param thumb
     * @param url
     * @param scene 朋友圈如SendMessageToWX.Req.WXSceneTimeline
     */
    public void sendToWX(String title, String message, Bitmap thumb, String url, int scene) {
        if (hasRegisterApp()) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = title;
            msg.description = message;
            msg.thumbData = Util.bmpToByteArray(thumb, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = scene;
            mApi.sendReq(req);
        }
    }

    public void sendTimeLine(SendMessageToWX.Req req) {
        if (hasRegisterApp()) {
            mApi.sendReq(req);
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    private String mPayUnifiedorderUrl;
    /**获取微信下单接口*/
    public String getPayUnifiedorderUrl() {
        return mPayUnifiedorderUrl;
    }

    public void setPayUnifiedorderUrl(String payUnifiedorderUrl) {
        mPayUnifiedorderUrl = payUnifiedorderUrl;
    }
    private String mPayUnifiedorderDesPassword;
    public String getPayUnifiedorderDesPassword() {
        return mPayUnifiedorderDesPassword;
    }
    public void setPayUnifiedorderDesPassword(String payUnifiedorderDesPassword) {
        mPayUnifiedorderDesPassword = payUnifiedorderDesPassword;
    }

    /**
     *
     * @param state 用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
     * @param scope 应用授权作用域，如获取用户个人信息则填写snsapi_userinfo  snsapi_base
     */
    public void requestWxLogin(String state, String scope, String appId) {
        if (TextUtils.isEmpty(appId)) {
            appId = this.mAppId;
        }
        if (TextUtils.isEmpty(scope)) {
            scope = "snsapi_userinfo";
        }
        if (TextUtils.isEmpty(state)) {
            state = "demo_wechat_login_state";
        }
        if (hasRegisterApp()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = scope;
            req.state = state;
            mApi.sendReq(req);
        }
    }
}
