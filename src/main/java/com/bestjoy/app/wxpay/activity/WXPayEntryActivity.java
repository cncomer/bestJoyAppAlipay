package com.bestjoy.app.wxpay.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bestjoy.app.alipay.R;
import com.bestjoy.app.wxpay.utils.MyWXUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	private int mPayResultCode = BaseResp.ErrCode.ERR_USER_CANCEL;
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = MyWXUtils.getInstance().getIWXAPI();
		api.handleIntent(getIntent(), this);

    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
			builder.setTitle(R.string.pay_result_tip);
			if (mPayResultCode == BaseResp.ErrCode.ERR_OK) {
				builder.setMessage(getString(R.string.pay_result_success_callback_msg));
			} else if (mPayResultCode == BaseResp.ErrCode.ERR_COMM) {
				builder.setMessage(getString(R.string.pay_result_failed_callback_msg, resp.errStr, String.valueOf(resp.errCode)));
			} else if (mPayResultCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
				builder.setMessage(getString(R.string.pay_result_cancel_callback_msg));
			}

			builder.show();
		}
	}

	@Override
	public void finish() {
		if (BaseResp.ErrCode.ERR_OK == mPayResultCode) {
			setResult(Activity.RESULT_OK);
		} else if (mPayResultCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
			setResult(Activity.RESULT_CANCELED);
		}
		super.finish();
	}
}