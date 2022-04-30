package com.joblist.Data.Helper;

import android.view.View;

public class Utils {
    public static void preventTwoClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(() -> view.setEnabled(true), 500);
    }
}
