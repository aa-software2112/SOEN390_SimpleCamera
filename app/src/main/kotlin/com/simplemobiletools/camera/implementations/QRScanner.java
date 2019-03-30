package com.simplemobiletools.camera.implementations;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.simplemobiletools.camera.interfaces.MyPreview;

public class QRScanner implements Runnable {

    /** Necessary for attempting to take a photo */
    private static MyPreview cameraPreview = null;

    /** Holds the next photo to be run through the QR processor */
    public static Bitmap qrPhoto = null;

    /** Scheduling flags */
    public static boolean qr_in_progress = false;
    private static boolean qr_scheduled = false;

    /** Necessary for processing background-thread tasks */
    private HandlerThread handlerThread = null;

    /** Used for running the QR scan with a timeout on-hold */
    private Handler handler = null;

    /** Holding the barcode detector instance */
    BarcodeDetector barcodeDetector = null;

    public static void setQrPhoto(Bitmap image)
    {
        QRScanner.qrPhoto = image;
    }

    public static void setCameraPreview(MyPreview cameraPreview)
    {
        QRScanner.cameraPreview = cameraPreview;
    }

    public QRScanner(Context appContext)
    {
        this.handlerThread = new HandlerThread("BackgroundRunner");
        this.handlerThread.start();
        this.handler = new Handler(this.handlerThread.getLooper());
        this.barcodeDetector = new BarcodeDetector.Builder(appContext)
                                        .setBarcodeFormats(Barcode.QR_CODE)
                                        .build();
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

        QRScanner.qr_in_progress = true;

        /** 1. Reset the photo
         *  2. Take photo
         *  3. Delay to make sure it was taken
         *  4. If image is NULL after wait, don't evaluatre QR, otherwise attempt evaluation
         */
        QRScanner.qrPhoto = null;

        /** No camera preview, can't perform QR */
        if (QRScanner.cameraPreview == null)
            return;

        QRScanner.cameraPreview.tryTakePicture();

        synchronized (this) {
            try {
                this.wait(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Running callback");

        /** Attempt to extract the string from the last taken QR photo */
        String qrString = null;
        qrString = this.extractURL();

        QRScanner.qr_in_progress = false;

    }

    public String extractURL()
    {

        /** No photo exists */
        if (QRScanner.qrPhoto == null)
        {
            return "";
        }

        if (!this.barcodeDetector.isOperational())
        {
            return "";
        }

        Frame frame = new Frame.Builder().setBitmap(QRScanner.qrPhoto).build();

        /** Get the barcodes from the image */
        SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

        if (barcodes.size() > 0 )
        {
            return barcodes.valueAt(0).rawValue;
        }
        else
        {
            return "";
        }

    }




}
