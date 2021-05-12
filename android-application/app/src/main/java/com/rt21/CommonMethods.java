package com.rt21;


import android.content.Context;
import android.widget.Toast;

public class CommonMethods {
    // this class stores custom made static methods to avoid duplicating their implementation in multiple classes

    /**
     *use it as CommonMethods.displayToastShort(getApplicationContext(), "Message");
     * @param context getApplicationContext()
     * @param text your message text
     */
    public static void displayToastShort(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * use it as CommonMethods.displayToastLong(getApplicationContext(), "Message");
     * @param context getApplicationContext()
     * @param text your message text
     */
    public static void displayToastLong(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
