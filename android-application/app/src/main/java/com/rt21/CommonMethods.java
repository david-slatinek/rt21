package com.rt21;


import android.content.Context;
import android.widget.Toast;

public class CommonMethods {
    // this class stores custom made static methods to avoid duplicating their implementation in multiple classes

    /**
     *use it as CommonMethods.displayToastShort("Message", getApplicationContext());
     * @param text your message text
     * @param context getApplicationContext()
     */
    public static void displayToastShort(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


    /**
     * CommonMethods.displayToastLong("Message", getApplicationContext());
     * @param text your message text
     * @param context getApplicationContext()
     */
    public static void displayToastLong(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
