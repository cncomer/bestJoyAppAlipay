package com.bestjoy.app.wxpay;

import android.os.Parcel;
import android.os.Parcelable;

import com.bestjoy.app.pay.PayObject;
import com.bestjoy.app.wxpay.utils.MD5;

import java.util.Random;

/**
 * Created by bestjoy on 15/8/6.
 */
public class WXPayObject extends PayObject {
    private static final String  TAG = "WXPayObject";
    public static final String  PAY_OBJECT_TAG = "WXPay";

    /**微信预下单流水号*/
    public String mPrepayId="";

    public WXPayObject() {
        setPayObjectTag(PAY_OBJECT_TAG);
    }

    /**
     * 商品金额 单位为分
     * @return
     */
    @Override
    public String getPrice() {
        return mPrice;
    }

    /*
    得到分
     */
    public String getFenPrice() {
        double price = Double.parseDouble(mPrice);
        price *=100;
        int priceInt = (int) price;
        return String.valueOf(priceInt);
    }

    @Override
    public String getSignType() {
        return "Sign=WXPay";
    }
//    /**
//     生成签名
//     */
//    public String genPackageSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(getApiKey());
//        Log.e("genPackageSign para=", sb.toString());
//        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e("genPackageSign packageSign=", packageSign);
//        return packageSign;
//    }
//    public String genAppSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(getApiKey());
//
//        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e("orion", appSign);
//        return appSign;
//    }

    public long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 随机字符串, 不长于32位。推荐随机数生成算法
     * @return
     */
    public String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

//    public String genProductArgs() {
//        StringBuffer xml = new StringBuffer();
//
//        try {
//            xml.append("</xml>");
//            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
//            packageParams.add(new BasicNameValuePair("appid", mAppId));
//            packageParams.add(new BasicNameValuePair("body", getBody()));
//            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
//            packageParams.add(new BasicNameValuePair("mch_id", mSeller));
//            packageParams.add(new BasicNameValuePair("nonce_str", genNonceStr()));
//            packageParams.add(new BasicNameValuePair("notify_url", mNotifyUrl));
//            packageParams.add(new BasicNameValuePair("out_trade_no", getOutTradeNo()));
//            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
//            packageParams.add(new BasicNameValuePair("total_fee", getFenPrice()));
//            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
//
//            String sign = genPackageSign(packageParams);
//            packageParams.add(new BasicNameValuePair("sign", sign));
//
//
//            String xmlstring =toXml(packageParams);
//            return new String(xmlstring.toString().getBytes(), "ISO8859-1");
//
//        } catch (Exception e) {
//            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
//            return null;
//        }
//    }
//
//    public String toXml(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<xml>");
//        for (int i = 0; i < params.size(); i++) {
//            sb.append("<"+params.get(i).getName()+">");
//
//
//            sb.append(params.get(i).getValue());
//            sb.append("</"+params.get(i).getName()+">");
//        }
//        sb.append("</xml>");
//
//        Log.e("orion",sb.toString());
//        return sb.toString();
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mPrepayId);
    }

    public static final Parcelable.Creator<WXPayObject> CREATOR = new Parcelable.Creator<WXPayObject>() {
        public WXPayObject createFromParcel(Parcel in) {
            return new WXPayObject(in);
        }

        public WXPayObject[] newArray(int size) {
            return new WXPayObject[size];
        }
    };

    private WXPayObject(Parcel in) {
        super(in);
        mPrepayId = in.readString();
    }
}
