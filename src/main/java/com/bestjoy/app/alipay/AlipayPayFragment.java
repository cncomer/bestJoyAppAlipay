package com.bestjoy.app.alipay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bestjoy.app.pay.PayFragment;
import com.bestjoy.app.pay.PayObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by bestjoy on 15/8/6.
 */
public class AlipayPayFragment extends PayFragment{
    public static final String TAG = "AlipayPayFragment";
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;
    private Handler mHandler = null;
    private ProgressDialog mJumpToPayWaitDialog;

    public static PayFragment newInstance(Bundle args) {
        PayFragment payFragment = new AlipayPayFragment();
        payFragment.setArguments(args);
        return payFragment;
    }

    @Override
    public void initPayObject() {

        mPayObject = PayObject.getPayObjectFromBundle(getArguments());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        if (mJumpToPayWaitDialog != null) {
                            mJumpToPayWaitDialog.dismiss();
                        }
                        Result resultObj = new Result((String) msg.obj);
                        final String resultStatus = resultObj.resultStatus;

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.pay_result_tip);

                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            builder.setMessage(getString(R.string.pay_result_success_callback_msg));
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Log.e(TAG, "pay result:支付结果确认中");
                            }
                            builder.setMessage(getString(R.string.pay_result_failed_callback_msg, resultObj.result, resultStatus));
                        }
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (TextUtils.equals(resultStatus, "9000")) {
                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();
                                }
                            }
                        });
                        builder.show();
                        break;
                    }
                    case SDK_CHECK_FLAG: {
                        Toast.makeText(getActivity(), "检查结果为：" + msg.obj,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                        break;
                }
            };
        };

    }


    @Override
    public void pay() {
        if (mJumpToPayWaitDialog == null) {
            mJumpToPayWaitDialog = new ProgressDialog(getActivity());
            mJumpToPayWaitDialog.setMessage(getString(R.string.wait_jump_to_pay_page));
            mJumpToPayWaitDialog.setCancelable(true);
        }
        mJumpToPayWaitDialog.show();
        String orderInfo = mPayObject.getOrderInfo();
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + mPayObject.getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(getActivity());
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
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, mPayObject.getSignPrivate());
    }

}
