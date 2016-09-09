package com.bestjoy.app.pay;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.bestjoy.app.alipay.AliPayObject;
import com.bestjoy.app.alipay.SignUtils;
import com.bestjoy.app.wxpay.WXPayObject;


public class PayObject implements Parcelable{

	private static final String  TAG = "PayObject";
	public static final String EXTRA_PAY_TYPE = "extra_pay_type";
	private String mPayObjectTag = "";
	public String mPartner;
	public String mSeller;
	public String mOutTradeNo = "";
	public String mRsaPrivate;
	public String mNotifyUrl = "";
	public String mReturnUrl = "";
	public String mTradeOvertime = "";

	public String mSubject="";
	public String mBody = "";
	public String mDetail = "";
	public String mPrice = "";
	public String mApiKey = "";
	public String mAppId = "";
	public String mCouponId="";
	public String getPartner() {
		return mPartner;
	}

	public String getSeller() {
		return mSeller;
	}
	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 *
	 */
	public String getOutTradeNo() {
		return mOutTradeNo;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 *
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, getSignPrivate());
	}

	/**
	 * get the sign type we use. 获取签名方式
	 *
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
	/**
	 * get the sign type we use. 获取签名秘钥
	 *
	 */
	public String getSignPrivate() {
		return mRsaPrivate;
	}
	/**
	 * 获取API密钥
	 *
	 */
	public String getApiKey() {
		return mApiKey;
	}
	/**
	 * 设置API密钥
	 *
	 */
	public void setApiKey(String apiKey) {
		mApiKey = apiKey;
	}
	/**
	 * 服务器异步通知页面路径
	 * @return
	 */
	public String getNotifyUrl() {
		return mNotifyUrl;
	}
	/**
	 * 设置未付款交易的超时时间
	 * 默认30分钟，一旦超时，该笔交易就会自动被关闭。
	 * 取值范围：1m～15d。
	 * m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
	 * 该参数数值不接受小数点，如1.5h，可转换为90m。
	 * @return
	 */
	public String getTradeOvertime() {
		return mTradeOvertime;
	}
	/**
	 * 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
	 * @return
	 */
	public String getReturnUrl() {
		return mReturnUrl;
	}
	// 商品名称
	public String getSubject() {
		return mSubject;
	}
	/**
	 * 商品详情
	 *
	 */
	public String getBody() {
		return mBody;
	}
	/**
	 * 商品金额
	 * @return
	 */
	public String getPrice() {
		return mPrice;
	}

	/**
	 * create the order info. 创建订单信息
	 *
	 */
	public String getOrderInfo() {
		// 合作者身份ID
		return null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mPayObjectTag);
		dest.writeString(getPartner());
		dest.writeString(getSeller());
		dest.writeString(getOutTradeNo());
		dest.writeString(getNotifyUrl());
		dest.writeString(getTradeOvertime());
		dest.writeString(getReturnUrl());
		dest.writeString(getSubject());
		dest.writeString(getBody());
		dest.writeString(getPrice());
		dest.writeString(mRsaPrivate);
		dest.writeString(mApiKey);
		dest.writeString(mAppId);
		dest.writeString(mDetail);
		dest.writeString(mCouponId);
	}

	public static final Creator<PayObject> CREATOR = new Creator<PayObject>() {
		public PayObject createFromParcel(Parcel in) {
		    return new PayObject(in);
		}

		public PayObject[] newArray(int size) {
		    return new PayObject[size];
		}
	};

	public PayObject(Parcel in) {
		mPayObjectTag = in.readString();
		mPartner = in.readString();
		mSeller = in.readString();
		mOutTradeNo = in.readString();
		mNotifyUrl = in.readString();
		mTradeOvertime = in.readString();
		mReturnUrl = in.readString();
		mSubject = in.readString();
		mBody = in.readString();
		mPrice = in.readString();
		mRsaPrivate = in.readString();
		mApiKey = in.readString();
		mAppId = in.readString();
		mDetail = in.readString();
		mCouponId = in.readString();
	}

	public PayObject(String partner,
					 String seller,
					 String outTradeNo,
					 String notifyUrl,
					 String tradeOvertime,
					 String returnUrl,
					 String subject,
					 String body,
					 String price,
					 String payObjectTag) {
		mPartner = partner;
		mSeller = seller;
		mOutTradeNo = outTradeNo;
		mNotifyUrl = notifyUrl;
		mTradeOvertime = tradeOvertime;
		mReturnUrl = returnUrl;
		mSubject = subject;
		mBody = body;
		mPrice = price;
		mPayObjectTag = payObjectTag;
	}

	public PayObject() {}

	/**
	 * 设置支付方式标记
	 * @param payObjectTag
	 */
	public void setPayObjectTag(String payObjectTag) {
		mPayObjectTag = payObjectTag;
	}
	public String getPayObjectTag() {
		return mPayObjectTag;
	}

	public static <T extends Parcelable> void addToBundle(Bundle bundle, String payObjectTag, T data) {
		bundle.putString(EXTRA_PAY_TYPE, payObjectTag);
		bundle.putParcelable(TAG, data);
	}
	public static String getPayObjectTag(Bundle bundle) {
		return  bundle.getString(EXTRA_PAY_TYPE);
	}

	public static PayObject getPayObjectFromBundle(Bundle bundle) {
		String mPayTye = getPayObjectTag(bundle);
		if (WXPayObject.PAY_OBJECT_TAG.equals(mPayTye)) {
			WXPayObject wxPayObject = bundle.getParcelable(TAG);
			return wxPayObject;
		} else if (AliPayObject.PAY_OBJECT_TAG.equals(mPayTye)) {
			AliPayObject aliPayObject = bundle.getParcelable(TAG);
			return aliPayObject;
		} else if (CashPayObject.PAY_OBJECT_TAG.equals(mPayTye)) {
			CashPayObject cashPayObject = bundle.getParcelable(TAG);
			return cashPayObject;
		}
		return bundle.getParcelable(TAG);

	}
}
