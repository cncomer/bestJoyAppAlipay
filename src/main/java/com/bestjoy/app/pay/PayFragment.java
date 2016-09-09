package com.bestjoy.app.pay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bestjoy.app.alipay.R;

/**
 * Created by bestjoy on 15/8/6.
 */
public abstract  class PayFragment extends Fragment implements View.OnClickListener{

    protected PayObject mPayObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPayObject();
    }

    public void setPayObject(PayObject payObject) {
        mPayObject = payObject;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pay_external, container, false);
        TextView textview = (TextView) view.findViewById(R.id.subject);
        textview.setText(mPayObject.mSubject);

        textview = (TextView) view.findViewById(R.id.body);
        textview.setText(mPayObject.mBody);

        textview = (TextView) view.findViewById(R.id.price);
        textview.setText(getActivity().getString(R.string.format_yuan, mPayObject.mPrice));

        view.findViewById(R.id.pay).setOnClickListener(this);
        view.findViewById(R.id.button_back).setOnClickListener(this);

        return view;
    }

    public abstract void initPayObject();
    public abstract void pay();

    public void cancelPay() {
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pay) {
            pay();
        } else if (id == R.id.button_back) {
            cancelPay();
        }
    }
}
