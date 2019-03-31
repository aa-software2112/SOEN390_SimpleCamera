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
import android.util.SparseArray;

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

    /** The context of the main activity, and the
     * activity itself. These will be used to run context-based
     * API within the nested thread in this class
     */
    private Context context = null;
    private MainActivity activity = null;

    /** Necessary for attempting to take a photo */
    private static CameraPreview cameraPreview = null;
    private static boolean canTakePhoto = true;


    /** Holds the next photo to be run through the QR processor */
    private static Queue<Bitmap> bitmapQueue = null;

    /** Scheduling flags */
    public static boolean qr_requested = true;
    public static boolean qr_in_progress = false;
    private static boolean qr_scheduled = false;

    /** Necessary for processing background-thread tasks */
    private HandlerThread handlerThread = null;

    /** Used for running the QR scan with a timeout on-hold */
    private Handler handler = null;
    private static Handler UIHandler = null;

    /** Holding the barcode detector instance */
    private static FirebaseVisionBarcodeDetectorOptions  options = null;
    private static FirebaseVisionBarcodeDetector detector = null;

    private static FirebaseVisionBarcode barcode = null;


    private static QRScanner singletonInstance = null;

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
    {
        this.init();
    }

    public QRScanner setContext(Context context)
    {
        this.context = context;
        return QRScanner.getInstance();
    }

    public QRScanner setApplication(MainActivity activity)
    {
        this.activity = activity;
        return QRScanner.getInstance();
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
                ((AlertDialog)m.obj).show();
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

    }


    public void addQrPhoto(Bitmap image)
    {
        QRScanner.bitmapQueue.add(image);
    }

    public static void canTakePhoto() {QRScanner.canTakePhoto = true;}

    public static void setCameraPreview(MyPreview cameraPreview)
    {
        QRScanner.cameraPreview = (CameraPreview) cameraPreview;
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

        QRScanner.qr_requested = true;
        QRScanner.qr_in_progress = true;

        /** 1. Reset the photo
         *  2. Take photo
         *  3. Delay to make sure it was taken
         *  4. If image is NULL after wait, don't evaluatre QR, otherwise attempt evaluation
         */

        /** No camera preview, can't perform QR */
        if (QRScanner.cameraPreview == null)
            return;

        if(QRScanner.cameraPreview.isInPreviewMode()) {
            System.out.println("Taking photo");
            QRScanner.cameraPreview.tryTakePicture();
        }

        System.out.println("Running callback");

        QRScanner.qr_in_progress = false;

    }

    public static String extractURL()
    {



        /** No photo exists */
        if (QRScanner.bitmapQueue.isEmpty())
        {
            return "";
        }

        while(!QRScanner.bitmapQueue.isEmpty()) {
            Bitmap bitmap = QRScanner.bitmapQueue.poll();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            Task<List<FirebaseVisionBarcode>> result = QRScanner.detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {

                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {

                            /** Only process a single barcode - ignore the rest */
                            if (barcodes.size() == 0)
                            {
                                return;
                            }

                            /** Barcode to process */
                            QRScanner.barcode = barcodes.get(0);

                            System.out.println(barcode.getRawValue());

                            /** Redirect user to web page if it is a URL */
                            if(barcode.getValueType() == FirebaseVisionBarcode.TYPE_URL)
                            {
                                /** Create an alert dialog */
                                AlertDialog alert = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
                                        .setTitle(R.string.qr_alert_title)
                                        .setMessage("Would you like to view this link?\n\t" + barcode.getRawValue())
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

                                QRScanner.UIHandler.sendMessage(Message.obtain(QRScanner.UIHandler,
                                        0, alert));

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });


        }

        return "";
    }




}
