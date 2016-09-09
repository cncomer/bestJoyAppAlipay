package com.bestjoy.app.alipay;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


public class PayDemoActivity extends FragmentActivity implements View.OnClickListener{

	private Context mContext;
	private static final int REQUEST_PAY = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.pay_demo_activity);
		findViewById(R.id.button_pay).setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {
		AliPayObject aliPayObject = new AliPayObject();
		aliPayObject.mOutTradeNo = "12121212121212121";
		aliPayObject.mNotifyUrl = "http://www.dzbxk.com/alipay/alipayback.ashx";
		aliPayObject.mBody = "测试支付功能";
		aliPayObject.mPrice = "0.1";
		aliPayObject.mSubject = "测试DEMO";
		aliPayObject.mReturnUrl = "http://m.alipay.com";

		startActivityForResult(AliPayActivity.createIntent(mContext, aliPayObject), REQUEST_PAY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_PAY) {
				//支付成功，我们需要查询
			}
		} else {
			if (requestCode == REQUEST_PAY) {
				//支付失败
			}
		}
	}
}
