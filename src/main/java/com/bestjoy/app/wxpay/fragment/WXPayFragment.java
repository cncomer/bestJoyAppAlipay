package com.bestjoy.app.wxpay.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.bestjoy.app.alipay.R;
import com.bestjoy.app.pay.PayFragment;
import com.bestjoy.app.pay.PayObject;
import com.bestjoy.app.wxpay.WXPayObject;
import com.bestjoy.app.wxpay.utils.DES;
import com.bestjoy.app.wxpay.utils.MD5;
import com.bestjoy.app.wxpay.utils.MyWXUtils;
import com.bestjoy.app.wxpay.utils.Util;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 微信支付  预支付接口
 /v3/wxUnifiedorder.ashx

 需要参数
 /// <summary>
 /// 商品描述
 /// 商品或支付单简要描述
 /// </summary>
 public string body { get; set; }

 /// <summary>
 /// 商品详情
 /// 商品名称明细列表
 /// </summary>
 public string detail { get; set; }


 /// <summary>
 /// 商户订单号
 /// </summary>
 public string out_trade_no { get; set; }


 返回结果如下

 <xml>
 <return_code><![CDATA[SUCCESS]]></return_code>
 <return_msg><![CDATA[OK]]></return_msg>
 <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
 <mch_id><![CDATA[10000100]]></mch_id>
 <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>
 <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
 <result_code><![CDATA[SUCCESS]]></result_code>
 <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
 <trade_type><![CDATA[JSAPI]]></trade_type>

 <api_key><![CDATA[xxxxxxxxxxxxxxxx]]></trade_type>
 </xml>

 使用其中的数据签名后调起微信支付客户端
 * </pre>
 * Created by bestjoy on 15/8/6.
 */
public class WXPayFragment extends PayFragment{
    public static final String TAG = "WXPayFragment";

    private Handler mHandler = null;
    final IWXAPI msgApi = MyWXUtils.getInstance().getIWXAPI();
    private WXPayObject mWXPayObject;
    private ProgressDialog mJumpToPayWaitDialog;

    public static PayFragment newInstance(Bundle args) {
        PayFragment payFragment = new WXPayFragment();
        payFragment.setArguments(args);
        return payFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    default:
                        break;
                }
            };
        };

    }

    @Override
    public void initPayObject() {
        mPayObject = PayObject.getPayObjectFromBundle(getArguments());
        mWXPayObject = (WXPayObject) mPayObject;
    }

//    private PayReq genPayReq() {
//        PayReq req = new PayReq();
//        req.appId = mWXPayObject.mAppId;
//        req.partnerId = mWXPayObject.mPartner;
//        req.prepayId = resultunifiedorder.get("prepay_id");
//        req.packageValue = mWXPayObject.getSignType();
//        req.nonceStr = mWXPayObject.genNonceStr();
//        req.timeStamp = String.valueOf(mWXPayObject.genTimeStamp());
//
//
//        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
//        signParams.add(new BasicNameValuePair("appid", req.appId));
//        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
//        signParams.add(new BasicNameValuePair("package", req.packageValue));
//        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
//        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
//        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
////        signParams.add(new BasicNameValuePair("package", req.packageValue));
//        req.sign = mWXPayObject.genPackageSign(signParams);
//
//
//        Log.e("genPayReq orion", signParams.toString());
//
//        return req;
//    }

    /**
     生成签名
     */
    public String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(mWXPayObject.getApiKey());
//        Log.e("genPackageSign para=", sb.toString());
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e("genPackageSign packageSign=", packageSign);
        return packageSign;
    }

    @Override
    public void pay() {
        if (mJumpToPayWaitDialog == null) {
            mJumpToPayWaitDialog = new ProgressDialog(getActivity());
            mJumpToPayWaitDialog.setTitle(R.string.pay_result_tip);
            mJumpToPayWaitDialog.setMessage(getString(R.string.wxpay_getting_prepayid));
            mJumpToPayWaitDialog.setCancelable(true);
        }
        mJumpToPayWaitDialog.show();
        new GetPrepayIdTask().execute();
    }


    /**
     * <xml>
     *     <return_code>
     *         <![CDATA[FAIL]]>
     *     </return_code>
           <return_msg>
              <![CDATA[invalid total_fee]]>
           </return_msg>
     */
    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

        @Override
        protected void onPostExecute(Map<String,String> result) {
            hideWaitPayFinishDialog();
            String errorMessage = null;
            if (result == null) {
                errorMessage = "Could not get any response";
            } else {
                String returnCode = result.get("return_code");
                if ("SUCCESS".equalsIgnoreCase(returnCode)) {
                    String resultCode = result.get("result_code");

                    if("SUCCESS".equalsIgnoreCase(resultCode)) {
                        Log.d(TAG, "GetPrepayIdTask prepay_id\n" + result.get("prepay_id") + "\n\n");
                        PayReq req = new PayReq();
                        req.appId = result.get("appid");
                        req.partnerId = result.get("mch_id");
                        req.prepayId = result.get("prepay_id");
                        req.packageValue = mWXPayObject.getSignType();
                        req.nonceStr = mWXPayObject.genNonceStr();
                        req.timeStamp = String.valueOf(mWXPayObject.genTimeStamp());
                        try {
                            mWXPayObject.setApiKey(DES.deCrypto(result.get("api_key"), MyWXUtils.getInstance().getPayUnifiedorderDesPassword()));
                            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
                            signParams.add(new BasicNameValuePair("appid", req.appId));
                            signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
                            signParams.add(new BasicNameValuePair("package", req.packageValue));
                            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
                            signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
                            signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
                            req.sign = genPackageSign(signParams);
                            msgApi.sendReq(req);
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMessage = e.getMessage();
                        }

                    } else {
                        errorMessage = result.get("err_code_des");
                    }

                } else {
                    errorMessage = result.get("return_msg");

                }
            }
            if (!TextUtils.isEmpty(errorMessage)) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(errorMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hideWaitPayFinishDialog();
        }

        @Override
        protected Map<String,String> doInBackground(Void... params) {

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            JSONObject queryObject = new JSONObject();
            try {
                queryObject.put("body", mWXPayObject.getSubject());
                queryObject.put("detail", mWXPayObject.getBody());
                queryObject.put("out_trade_no", mWXPayObject.getOutTradeNo());
                queryObject.put("couponid", mWXPayObject.mCouponId);
                param.add(new BasicNameValuePair("para", queryObject.toString()));
//                Log.e("GetPrepayIdTask orion",params.toString());
                byte[] buf = Util.httpPost(MyWXUtils.getInstance().getPayUnifiedorderUrl(), param);

                if (buf != null) {
                    String content = new String(buf);
//                    Log.e("GetPrepayIdTask orion", content);
                    Map<String,String> xml= decodeXml(content);
                    return xml;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



    public Map<String,String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName=parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if("xml".equals(nodeName)==false){
                            //实例化student对象
                            xml.put(nodeName,parser.nextText().trim());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion",e.toString());
        }
        return null;

    }



    public void hideWaitPayFinishDialog() {
        if (mJumpToPayWaitDialog != null) {
            mJumpToPayWaitDialog.dismiss();
        }
    }

}
