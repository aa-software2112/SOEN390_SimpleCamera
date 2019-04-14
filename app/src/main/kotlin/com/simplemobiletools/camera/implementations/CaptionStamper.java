package com.simplemobiletools.camera.implementations;

import android.app.Activity;
import android.content.Context;
import android.content.SyncStats;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import com.simplemobiletools.camera.R;

import com.simplemobiletools.camera.activities.MainActivity;
import com.simplemobiletools.camera.interfaces.MyPreview;
import com.simplemobiletools.camera.views.CameraPreview;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CaptionStamper {

    private static Context context = null;
    private static Activity activity = null;
    private static CameraPreview cameraPreview = null;
    private static ConcurrentLinkedDeque<String> captionRequestQueue = new ConcurrentLinkedDeque<>();

    public static void setContext(Context c)
    {
        CaptionStamper.context = c;
    }

    public static void setActivity(Activity a)
    {
        CaptionStamper.activity = a;
    }

    public static void setCameraPreview(MyPreview p)
    {
        CaptionStamper.cameraPreview = (CameraPreview) p;
    }

    /** If there is a string to be stamped on an image,
     * this method will apply the stamp to the image
     * @param b The bitmap image to apply a stamp over
     */
    public static Bitmap addCaptionToImage(Bitmap b)
    {
        /** Don't apply caption if there are none requested */
        if (captionRequestQueue.isEmpty())
        {
            return b;
        }

        b = b.copy(Bitmap.Config.ARGB_8888, true);

        /** Create canvas in order to paint over the bitmap image */
        Canvas canvas = new Canvas(b);

        int width = b.getWidth();
        int height = b.getHeight();

        /** The text painter */
        float fontSize = 125F;
        Paint painter = new Paint();
        painter.setTextSize(fontSize);
        painter.setTypeface(Typeface.SANS_SERIF);
        painter.setColor(Color.WHITE);

        String captionText = captionRequestQueue.poll();
        String captionToWrite = "";

        /** Keeps track of the characters to write, and number of
         * lines written in the loop below
         */
        int numCharactersToWrite = 0;
        int numLinesWritten = 0;

        /** Stores the lines for post-writing, so that distance from
         * bottom of screen can be calculated for proper formatting
         */
        ArrayList<String> linesToWrite = new ArrayList<>();

        /** Parse the lines of text */
        while (!captionText.isEmpty()) {
            /** Check how many characters fit in the bitmap
             * given the painter text size
             */
            numCharactersToWrite = painter.breakText(
                    captionText,
                    true,
                    width - fontSize,
                    null
            );

            /** Get the caption to write based on screen width */
            captionToWrite = captionText.substring(0, numCharactersToWrite).trim();

            /** Check if the string was split mid-word and insert a hyphen */
            if ((captionText.length() > captionToWrite.length()) && (captionText.length() > numCharactersToWrite))
            {

                /** The next character is not a whitespace */
                if (captionText.charAt(numCharactersToWrite) != ' ')
                {
                    captionToWrite = captionToWrite + "-";
                }

            }

            linesToWrite.add(captionToWrite);

            /** Get the next section of the string to use in */
            captionText = captionText.substring(numCharactersToWrite, captionText.length());
        }

        /** Write the lines to the bitmap */
        for(String lineToWrite: linesToWrite)
        {

            /** Draw the caption */
            canvas.drawText(lineToWrite,
                    50F,
                    (float)(height - (linesToWrite.size() - numLinesWritten)*fontSize),
                    painter
            );

            numLinesWritten++;
        }

        return b;
    }

    public void showKeyboard()
    {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(activity.findViewById(R.id.caption_input),
                        InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard()
    {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(activity.findViewById(R.id.caption_input).getWindowToken(),
                        InputMethodManager.SHOW_IMPLICIT);
    }

    public void performStamp(String stampText)
    {
        /** Create a stamp request with a string -
         * the bitmap will be linked later, once the photo is taken
         */
        CaptionStamper.captionRequestQueue.add(stampText);

        /** Attempt to take a photo */
        this.cameraPreview.tryTakePicture();

    }



}
