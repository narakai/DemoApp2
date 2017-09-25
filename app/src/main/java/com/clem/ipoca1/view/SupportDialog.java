package com.clem.ipoca1.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clem.ipoca1.R;
import com.clem.ipoca1.activity.MainActivity;
import com.clem.ipoca1.core.preferences.UserPreferences;
import com.clem.ipoca1.spa.ToastUtils;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;


/**
 * Created by laileon on 2017/2/18.
 */

public class SupportDialog extends DialogFragment {
    public static final String TAG = "SupportDialog";
    private static final String PAYPAL_USER = "narakai.lai@gmail.com";
    private static final String PAYPAL_CURRENCY_CODE = "USD";
    private static final String CODE = "aex01345nkd6asuyktsv9f0";


    public static void show(MainActivity mainActivity) {
        SupportDialog dialog = new SupportDialog();
        dialog.show(mainActivity.getSupportFragmentManager(), "[ABOUT_DIALOG]");
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.donation)
                .content(R.string.buyacoffee)
                .positiveText(R.string.ok)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        buyAcoffee();
                        UserPreferences.setBuyme();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
    }

    private void buyAcoffee() {
        if (AlipayZeroSdk.hasInstalledAlipayClient(getActivity())) {
            AlipayZeroSdk.startAlipayClient(getActivity(), CODE);
        } else {
            ToastUtils.showShort(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.support_fail_because_not_install));
        }
    }

    /**
     * Donate button with PayPal by opening browser with defined URL For possible parameters see:
     * https://developer.paypal.com/webapps/developer/docs/classic/paypal-payments-standard/integration-guide/Appx_websitestandard_htmlvariables/
     */
//    private void donatePayPalOnClick() {
//        Uri.Builder uriBuilder = new Uri.Builder();
//        uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr");
//        uriBuilder.appendQueryParameter("cmd", "_donations");
//        uriBuilder.appendQueryParameter("business", PAYPAL_USER);
//        uriBuilder.appendQueryParameter("lc", "US");
//        uriBuilder.appendQueryParameter("item_name", "Donate");
//        uriBuilder.appendQueryParameter("no_note", "1");
//        // uriBuilder.appendQueryParameter("no_note", "0");
//        // uriBuilder.appendQueryParameter("cn", "Note to the developer");
//        uriBuilder.appendQueryParameter("no_shipping", "1");
//        uriBuilder.appendQueryParameter("currency_code", PAYPAL_CURRENCY_CODE);
//        Uri payPalUri = uriBuilder.build();
//        // Start your favorite browser
//        try {
//            Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
//            startActivity(viewIntent);
//        } catch (ActivityNotFoundException e) {
//            ToastUtils.showShort(R.string.no_browser);
//        }
//    }
}