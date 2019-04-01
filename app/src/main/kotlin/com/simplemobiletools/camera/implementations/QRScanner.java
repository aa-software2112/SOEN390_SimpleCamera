package com.simplemobiletools.camera.implementations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.simplemobiletools.camera.activities.MainActivity;
import com.simplemobiletools.camera.activities.SimpleActivity;
import com.simplemobiletools.camera.interfaces.MyPreview;
import com.simplemobiletools.camera.R;
import com.simplemobiletools.camera.views.CameraPreview;

import java.io.DataInput;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import androidx.annotation.NonNull;

import static androidx.core.content.ContextCompat.startActivity;

public class QRScanner implements Runnable {

    public static int SHOW_PROGRESS_BAR = 1;
    public static int HIDE_PROGRESS_BAR = 2;

    /** The context of the main activity, and the
     * activity itself. These will be used to run context-based
     * API within the nested thread in this class
     */
    private Context context = null;
    private MainActivity activity = null;

    /** Necessary for attempting to take a photo */
    private CameraPreview cameraPreview = null;

    /** Holds the next photo to be run through the QR processor */
    private static Queue<Bitmap> bitmapQueue = null;

    /** Alert dialogs */
    private AlertDialog alert = null;

    /** Scheduling flags */
    public static boolean qr_requested = true;
    private static boolean qr_scheduled = false;

    /** Necessary for processing background-thread tasks */
    private HandlerThread handlerThread = null;

    /** Used for running the QR scan with a timeout on-hold */
    private Handler handler = null;
    private Handler UIHandler = null;

    /** Holding the barcode detector instance */
    private FirebaseVisionBarcodeDetectorOptions  options = null;
    private FirebaseVisionBarcodeDetector detector = null;
    private FirebaseVisionBarcode barcode = null;
    private static QRScanner singletonInstance = null;

    /** Progress bar */
    private ProgressBar qrProgressBar = null;

    /** Testing vars */
    public static boolean addedQrPhotoTest = false;
    public static boolean scanPhotoTest = false;

    /** This class must be a singleton because it deals with
     * the application interface and camera, and having multiple instances of these
     * interactions will add multiple levels of uncertainty and synchronization issues.
     * @return QRScanner singleton
     */
    public static QRScanner getInstance()
    {
        if (singletonInstance == null)
        {
            singletonInstance = new QRScanner();
        }

        return singletonInstance;
    }

    private QRScanner()
    { }

    public QRScanner build()
    {
        this.init();
        return QRScanner.getInstance();
    }

    public boolean isBuilt()
    {
        return this.bitmapQueue != null ? true : false;
    }

    public QRScanner setContext(Context context)
    {
        this.context = context;
        return QRScanner.getInstance();
    }

    public boolean isContextSet()
    {
        return this.context != null ? true : false;
    }


    public QRScanner setApplication(MainActivity activity)
    {
        this.activity = activity;
        return QRScanner.getInstance();
    }

    public boolean isApplicationSet()
    {
        return this.activity != null ? true : false;
    }

    private void init()
    {
        /** A queue that will hold all bitmaps taken to be processed for QR purposes */
        this.bitmapQueue = new ConcurrentLinkedQueue<Bitmap>();

        /** A new loop to run threads on a non-UI looper so that UI does not stall during requets */
        this.handlerThread = new HandlerThread("BackgroundRunner");
        this.handlerThread.start();
        this.handler = new Handler(this.handlerThread.getLooper());

        /** Need to be able to send requests to the original UI looper for displaying
         * popup/alert windows; they can only be created through the main looper
         */
        this.UIHandler = new Handler(Looper.getMainLooper()) {

            /** A custom implementation for handling messages for this looper,
             * it is merely used to display the alert dialog
             * @param m The message received on this handler
             */
            @Override
            public void handleMessage(Message m)
            {
                if (m.what == QRScanner.HIDE_PROGRESS_BAR) qrProgressBar.setVisibility(View.GONE);
                else if (m.what == QRScanner.SHOW_PROGRESS_BAR) qrProgressBar.setVisibility(View.VISIBLE);
                else {
                    barcode = (FirebaseVisionBarcode) m.obj;
                    alert.setMessage("Would you like to view this link?\n\t" + barcode.getRawValue());
                    alert.show();
                }
            }
        };

        /** Setup Firebase options ahead of time so that they don't need
         * to be re-instantiated every time a barcode is attempted to be read
         */
        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();

        detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);


        /** Create an alert dialog */
        this.alert = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(R.string.qr_alert_title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent toInternet = new Intent(Intent.ACTION_VIEW, Uri.parse(barcode.getRawValue()) );
                        toInternet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(toInternet);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        /** Load progress bar */
        this.qrProgressBar = activity.findViewById(R.id.qrProgressBar);
    }

    /** Adds a bitmap to the queue for possible searches */
    public void addQrPhoto(Bitmap image)
    {
        QRScanner.bitmapQueue.add(image);
        // for testing
        addedQrPhotoTest = true;
    }

    /** Sets the camera for taking photos */
    public QRScanner setCameraPreview(MyPreview cameraPreview)
    {
        this.cameraPreview = (CameraPreview) cameraPreview;
        return QRScanner.getInstance();
    }

    public boolean isCameraPreviewSet()
    {
        return this.cameraPreview != null ? true : false;
    }

    public static boolean isQrScheduled(){
        return qr_scheduled;
    }

    public synchronized void scheduleQR(int milliseconds)
    {
        /** Runs this thread in a certain amount of milliseconds */
        if (!QRScanner.qr_scheduled) {
            QRScanner.qr_scheduled= true;
            this.handler.postDelayed(this, milliseconds);
        }
    }

    public synchronized void cancelQr()
    {
        /** Cancels the running of the thread */
        if (QRScanner.qr_scheduled)
        {
            QRScanner.qr_scheduled = false;
            this.handler.removeCallbacks(this);
        }

    }

    @Override
    public void run() {

        this.UIHandler.sendMessage(Message.obtain(UIHandler, QRScanner.SHOW_PROGRESS_BAR));

        /** No camera preview, can't perform QR */
        if (this.cameraPreview == null)
            return;

        QRScanner.qr_requested = true;

        /** 1. Reset the photo
         *  2. Take photo
         *  3. Delay to make sure it was taken
         *  4. If image is NULL after wait, don't evaluatre QR, otherwise attempt evaluation
         */

        if(this.cameraPreview.isInPreviewMode()) {
            this.cameraPreview.tryTakePicture();
        }
        else
        {
            this.UIHandler.sendMessage(Message.obtain(UIHandler, QRScanner.HIDE_PROGRESS_BAR));
        }


        /** Shut the spinner after 10 seconds if some error occurs */
        this.UIHandler.sendMessageAtTime(Message.obtain(UIHandler, QRScanner.HIDE_PROGRESS_BAR),
                SystemClock.uptimeMillis() + 10000);

    }

    public void scanPhotos()
    {
        /** Scan all taken photos and if a QR code is found, prompt the user
         * to navigate to the URL
         */
        while(!QRScanner.bitmapQueue.isEmpty()) {
            // for testing
            scanPhotoTest = true;

            /** Take a bitmap from the image queue and attempt to scan it */
            Bitmap bitmap = this.bitmapQueue.poll();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            /** Scan the image */
            Task<List<FirebaseVisionBarcode>> result = this.detector.detectInImage(image)
                    /** When scan was successful (detection not guaranteed) */
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {

                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {

                            /** Only process a single barcode - ignore the rest */
                            if (barcodes.size() == 0) {
                                UIHandler.sendMessage(Message.obtain(UIHandler, QRScanner.HIDE_PROGRESS_BAR));
                                return;
                            }

                            FirebaseVisionBarcode bcode = barcodes.get(0);

                            /** Redirect user to web page if it is a URL */
                            if(bcode.getValueType() == FirebaseVisionBarcode.TYPE_URL)
                            {

                                /** Barcode to process - if many are found in the photo,
                                 * only take the first. This message will send the
                                 * barcode to the UI thread handler to execute the
                                 * alert menu for redirection
                                 * */
                                UIHandler.sendMessage(Message.obtain(UIHandler,
                                        0, bcode));
                            }

                            UIHandler.sendMessage(Message.obtain(UIHandler, QRScanner.HIDE_PROGRESS_BAR));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            UIHandler.sendMessage(Message.obtain(UIHandler, QRScanner.HIDE_PROGRESS_BAR));
                            e.printStackTrace();
                        }
                    });
        }




    }




}
