package com.bestjoy.app.alipay;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExternalFragment extends Fragment {

	public static final String TAG = "ExternalFragment";
	private AliPayObject mAliPayObject;
	
	public void setPayObject(AliPayObject aliPayObject) {
		mAliPayObject = aliPayObject;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pay_external, container, false);
		TextView textview = (TextView) view.findViewById(R.id.subject);
		textview.setText(mAliPayObject.mSubject);
		
		textview = (TextView) view.findViewById(R.id.body);
		textview.setText(mAliPayObject.mBody);
		
		textview = (TextView) view.findViewById(R.id.price);
		textview.setText(getActivity().getString(R.string.format_yuan, mAliPayObject.mPrice));
		
		return view;
	}
}
