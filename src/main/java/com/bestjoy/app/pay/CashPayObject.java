package com.bestjoy.app.pay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 现金支付对象
 * Created by bestjoy on 15/8/28.
 */
public class CashPayObject extends PayObject{
    public static final String  TAG = "CashPayObject";
    /**这个值主要用来从bundle对象中读取支付方式*/
    public static final String  PAY_OBJECT_TAG = "CashPay";


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Parcelable.Creator<CashPayObject> CREATOR = new Parcelable.Creator<CashPayObject>() {
        public CashPayObject createFromParcel(Parcel in) {
            return new CashPayObject(in);
        }

        public CashPayObject[] newArray(int size) {
            return new CashPayObject[size];
        }
    };

    private CashPayObject(Parcel in) {
        super(in);
    }

    public CashPayObject() {
        mTradeOvertime = "30m";

        setPayObjectTag(PAY_OBJECT_TAG);
    }
}
