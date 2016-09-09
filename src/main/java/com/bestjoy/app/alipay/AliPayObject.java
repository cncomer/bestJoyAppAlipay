package com.bestjoy.app.alipay;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bestjoy.app.pay.PayObject;


public class AliPayObject extends PayObject {
	
	public static final String  TAG = "AliPayObject";
	/**这个值主要用来从bundle对象中读取支付方式*/
	public static final String  PAY_OBJECT_TAG = "AliPay";
	public static final String PARTNER = "2088511405307200";
	public static final String SELLER = "2922750265@qq.com";
	public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOnGvf2jUfG+/r01" +
            "vLQmunYh6/VOFItnP1fZjSPRY+ePWefAQ2/zuKe71ISs+jjPqRR+xdqkNX0bdUV7" +
            "0tFzQpJVqZJKxfqCw028CcgXmbXeKe9iLBV8Od+5yqj4k3FufY6D6DFd2wLVh1YR" +
            "HCenHIJ1lhiO5DW/43Y72bXj9YuZAgMBAAECgYAqnp0MnLXXyOZQQHfCsDzWRKDr" +
            "++wq7gvSTEs6+HWqRawyYA7rXheQHOJFvfNwYAuHPQV9Muq9gEAFBuoMXWMJSXIF" +
            "ZEby6qY+oUhTjIZN0hspipPI0YdtaGzWsbUtjnqCyIv50M7acOkJo2oJnlbPUEjc" +
            "ZAdXor39ltj1e1/YMQJBAPr6dlFReiXe90RdMH1vDRbiG5kewpajgA38wW3DE1rM" +
            "Z44ErHg8MoEjN3UYPnDKJ16yenIZFZLnmo1iZmVNSQsCQQDudCtaH2n1rbdo3qA9" +
            "euoTn1bgVdfL9NfVENvU84WuC3U6pohO7+gTLlP+aUcNALrBWiH+M90yF1y8H8yH" +
            "4oxrAkB6A4vD6t18TdJeTQPG51cwucn6/eZR/7jAlBGhgAYHzXnGpGUIqxNOjsVm" +
            "StwIT28mbZRzpdhELA6KD72CIDmTAkAwQ6E2t58J1CSIfG8won1YhWrfM2Dfbmp1" +
            "PNlSDAxlwvnyahyy5Yoyv7DOkN6JujkSw7yXqEXASsxBY6pRYsCLAkBmqIRtI03B" +
            "j1JVzeXC8epc8xevVeSTLkZTPplpu1N/4+pMNOCGhINEa4gL6FiwIBZXBw7GE8yJ" +
            "rdJeQrxEK5g7";
	public static final String RSA_PUBLIC = "";

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public static void getSDKVersion(Activity context) {
		PayTask payTask = new PayTask(context);
		String version = payTask.getVersion();
		Toast.makeText(context, version, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo() {
		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + getPartner() + "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + getSeller() + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + getSubject()  + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + getBody() + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + getPrice() + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + getNotifyUrl() + "\"";

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=" + "\"" + getTradeOvertime() + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url="+ "\"" + getReturnUrl() + "\"";;

		// 调用银行卡支付，需配置此参数，参与签名， 固定值
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	@Override
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}
	
	public static final Parcelable.Creator<AliPayObject> CREATOR = new Parcelable.Creator<AliPayObject>() {
		public AliPayObject createFromParcel(Parcel in) {
		    return new AliPayObject(in);
		}
		
		public AliPayObject[] newArray(int size) {
		    return new AliPayObject[size];
		}
	};

	private AliPayObject(Parcel in) {
		super(in);
	}
	
	public AliPayObject() {
		mPartner = PARTNER;
		mSeller = SELLER;
		mRsaPrivate = RSA_PRIVATE;
		mNotifyUrl = "http://notify.msp.hk/notify.htm";
		mReturnUrl = "";
		mTradeOvertime = "30m";

		setPayObjectTag(PAY_OBJECT_TAG);
	}

}
