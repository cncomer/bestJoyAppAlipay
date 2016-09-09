package com.bestjoy.app.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class AliPayActivity extends FragmentActivity {

	private static final String TAG = "AliPayActivity";
	private AliPayObject mAliPayObject;
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(AliPayActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(AliPayActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();
						setResult(Activity.RESULT_CANCELED);
						finish();
					} else {
						Toast.makeText(AliPayActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(AliPayActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!checIntent()) {
			Log.d(TAG, "checIntent() return false");
			finish();
			return;
		}
		initLayout();
	}
	/**
	 * 子类可以覆写这个方法来自定义布局界面
	 */
	protected void initLayout() {
		setContentView(R.layout.pay_main);
		ExternalFragment externalFragment = new ExternalFragment();
		externalFragment.setPayObject(mAliPayObject);
		this.getSupportFragmentManager().beginTransaction().replace(R.id.content, externalFragment, ExternalFragment.TAG).commit();
	}
	
	private boolean checIntent() {
		mAliPayObject = getIntent().getParcelableExtra(AliPayObject.TAG);
		return mAliPayObject != null;
	}
	
	public static final void startActivity(Context context, AliPayObject aliPayObject) {
		Intent intent =  new Intent(context, AliPayActivity.class);
		intent.putExtra(AliPayObject.TAG, aliPayObject);
		context.startActivity(intent);
	}
	
	public static Intent createIntent(Context context, AliPayObject aliPayObject) {
		Intent intent =  new Intent(context, AliPayActivity.class);
		intent.putExtra(AliPayObject.TAG, aliPayObject);
		return intent;
	}
	
	/**
	 * 取消支付
	 */
	public void cancel(View view) {
		finish();
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(View view) {
		String orderInfo = mAliPayObject.getOrderInfo();
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + mAliPayObject.getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(AliPayActivity.this);
				// 调用支付接口
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask(AliPayActivity.this);
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, mAliPayObject.getSignPrivate());
	}

}
