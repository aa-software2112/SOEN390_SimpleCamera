package com.simplemobiletools.camera.activities

import android.app.Activity
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.* // ktlint-disable no-wildcard-imports
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.simplemobiletools.camera.BuildConfig
import com.simplemobiletools.camera.extensions.config
import com.simplemobiletools.camera.extensions.navBarHeight
import com.simplemobiletools.camera.helpers.* // ktlint-disable no-wildcard-imports
import com.simplemobiletools.camera.implementations.MyCameraImpl
import com.simplemobiletools.camera.interfaces.MyPreview
import com.simplemobiletools.camera.views.CameraPreview
import com.simplemobiletools.camera.views.FocusCircleView
import com.simplemobiletools.commons.extensions.* // ktlint-disable no-wildcard-imports
import com.simplemobiletools.commons.helpers.* // ktlint-disable no-wildcard-imports
import com.simplemobiletools.commons.models.Release
import kotlinx.android.synthetic.main.activity_main.* // ktlint-disable no-wildcard-imports
import android.os.CountDownTimer
import com.simplemobiletools.camera.implementations.OnSwipeTouchListener
import com.simplemobiletools.camera.R
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.location.Location
import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.view.View
import android.location.Geocoder
import android.location.Address
import android.os.HandlerThread
import android.util.Log
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.simplemobiletools.camera.implementations.QRScanner
import java.util.Locale

class MainActivity : SimpleActivity(), PhotoProcessor.MediaSavedListener {
    private val FADE_DELAY = 6000L // in milliseconds
    private val COUNTDOWN_INTERVAL = 1000L
    private val BURSTMODE_INTERVAL_BETWEEN_CAPTURES = 100L // in milliseconds

    lateinit var mTimerHandler: Handler
    private lateinit var mOrientationEventListener: OrientationEventListener
    private lateinit var mFocusCircleView: FocusCircleView
    private lateinit var mFadeHandler: Handler
    private lateinit var mCameraImpl: MyCameraImpl
    internal lateinit var mBurstHandler: Handler
    internal lateinit var mBurstRunnable: Runnable
    internal lateinit var mBurstModeSetup: Runnable

    private var mSupportedFilter: IntArray? = null
    private var mPreview: MyPreview? = null
    private var mPreviewUri: Uri? = null
    internal var mIsInPhotoMode = false
    internal var mIsCameraAvailable = false
    internal var mIsVideoCaptureIntent = false
    private var mIsHardwareShutterHandled = false
    private var mCurrVideoRecTimer = 0
    var mLastHandledOrientation = 0
    internal var mIsInCountdownMode = false
    internal var mCountdownTime = 0
    internal var mBurstEnabled = false

    /** QR Scanner */
    internal lateinit var mQrScanner: QRScanner
    internal lateinit var mCameraSource: CameraSource

    internal var mFusedLocationClient: FusedLocationProviderClient? = null
    internal var mLastLocation: Location? = null
    internal var addressFirstLine: String? = null
    internal var addressSecondLine: String? = null
    internal var addressCoordinates: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        appLaunched(BuildConfig.APPLICATION_ID)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        initVariables()
        tryInitCamera()
        supportActionBar?.hide()
        checkWhatsNewDialog()
        setupOrientationEventListener()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onResume() {
        super.onResume()
        if (hasStorageAndCameraPermissions()) {
            mPreview?.onResumed()
            resumeCameraItems()
            setupPreviewImage(mIsInPhotoMode)
            scheduleFadeOut()
            mFocusCircleView.setStrokeColor(getAdjustedPrimaryColor())

            if (mIsVideoCaptureIntent && mIsInPhotoMode) {
                handleTogglePhotoVideo()
                checkButtons()
            }
            toggleBottomButtons(false)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (hasStorageAndCameraPermissions()) {
            mOrientationEventListener.enable()
        }
        handleGridLine()
        handleGPS()
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (!hasStorageAndCameraPermissions() || isAskingPermissions) {
            return
        }

        mFadeHandler.removeCallbacksAndMessages(null)

        hideTimer()
        mOrientationEventListener.disable()

        if (mPreview?.getCameraState() == STATE_PICTURE_TAKEN) {
            toast(R.string.photo_not_saved)
        }
        mPreview?.onPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPreview = null
    }

    private fun initVariables() {
        mIsInPhotoMode = config.initPhotoMode
        mIsCameraAvailable = false
        mIsVideoCaptureIntent = false
        mIsHardwareShutterHandled = false
        mCurrVideoRecTimer = 0
        mLastHandledOrientation = 0
        mCameraImpl = MyCameraImpl(applicationContext)
        mIsInCountdownMode = false
        mCountdownTime = 0
        mBurstHandler = Handler()
        mQrScanner = QRScanner(this.getApplicationContext());


        mBurstModeSetup = Runnable {
            // runs only once, that is after holding shutter button for 2 sec
            if (!mIsInCountdownMode && mIsInPhotoMode) {
                mBurstEnabled = true
                handleShutter()
            }
        }

        mBurstRunnable = object : Runnable {
            override fun run() {
                mPreview?.tryTakePicture()
                mBurstHandler.postDelayed(this, BURSTMODE_INTERVAL_BETWEEN_CAPTURES)
            }
        }

        if (config.alwaysOpenBackCamera) {
            config.lastUsedCamera = mCameraImpl.getBackCameraId().toString()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_CAMERA && !mIsHardwareShutterHandled) {
            mIsHardwareShutterHandled = true
            shutterPressed()
            true
        } else if (!mIsHardwareShutterHandled && config.volumeButtonsAsShutter && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            mIsHardwareShutterHandled = true
            shutterPressed()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mIsHardwareShutterHandled = false
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun hideIntentButtons() {
        toggle_photo_video.beGone()
        settings.beGone()
    }

    @SuppressLint("MissingPermission")
    private fun tryInitCamera() {
        handlePermission(PERMISSION_CAMERA) {
            if (it) {
                handlePermission(PERMISSION_WRITE_STORAGE) {
                    if (it) {
                        initializeCamera()

                        /** QR code */
                        var barcodeDetector = BarcodeDetector.Builder(getApplicationContext())
                                .setBarcodeFormats(Barcode.QR_CODE)
                                .build()

                        if (!barcodeDetector.isOperational())
                        {
                            System.out.println("Barcode Detector not working");
                        }

                        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
                            override fun release()
                            {}

                            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                                System.out.println(p0?.detectedItems);
                                System.out.println("Detection");

                            }

                        })

                        var fps: Float = 20.0f;
                        mCameraSource = CameraSource.Builder(getApplicationContext(), barcodeDetector)
                                .setFacing(CameraSource.CAMERA_FACING_BACK)
                                .build();

                        try {
                            mCameraSource.start();
                        } catch(e: Exception)
                        {
                            e.printStackTrace();
                        }



                    } else {
                        toast(R.string.no_storage_permissions)
                        finish()
                    }
                }
            } else {
                toast(R.string.no_camera_permissions)
                finish()
            }
        }
    }

    private fun isImageCaptureIntent() = intent?.action == MediaStore.ACTION_IMAGE_CAPTURE || intent?.action == MediaStore.ACTION_IMAGE_CAPTURE_SECURE

    private fun checkImageCaptureIntent() {
        if (isImageCaptureIntent()) {
            hideIntentButtons()
            val output = intent.extras?.get(MediaStore.EXTRA_OUTPUT)
            if (output != null && output is Uri) {
                mPreview?.setTargetUri(output)
            }
        }
    }

    private fun checkVideoCaptureIntent() {
        if (intent?.action == MediaStore.ACTION_VIDEO_CAPTURE) {
            mIsVideoCaptureIntent = true
            mIsInPhotoMode = false
            hideIntentButtons()
            shutter.setImageResource(R.drawable.ic_video_rec)
        }
    }

    private fun initializeCamera() {
        setContentView(R.layout.activity_main)
        initButtons()

        (btn_holder.layoutParams as RelativeLayout.LayoutParams).setMargins(0, 0, 0, (navBarHeight + resources.getDimension(R.dimen.activity_margin)).toInt())

        checkVideoCaptureIntent()

        mPreview = CameraPreview(this, camera_texture_view, mIsInPhotoMode)
        /** QR scanner must maintain an instance of the preview
         * to capture an image
         */
        QRScanner.setCameraPreview(mPreview)

        view_holder.addView(mPreview as ViewGroup)
        checkImageCaptureIntent()
        mPreview?.setIsImageCaptureIntent(isImageCaptureIntent())

        val imageDrawable = if (config.lastUsedCamera == mCameraImpl.getBackCameraId().toString()) R.drawable.ic_camera_front else R.drawable.ic_camera_rear
        toggle_camera.setImageResource(imageDrawable)

        mFocusCircleView = FocusCircleView(applicationContext)
        view_holder.addView(mFocusCircleView)

        mTimerHandler = Handler()
        mFadeHandler = Handler()
        setupPreviewImage(true)

        val initialFlashlightState = if (config.turnFlashOffAtStartup) FLASH_OFF else config.flashlightState
        mPreview!!.setFlashlightState(initialFlashlightState)
        updateFlashlightState(initialFlashlightState)
    }

    internal fun initButtons() {

        System.out.println("Initializing Buttons");
        toggle_camera.setOnClickListener { toggleCamera() }

        swipe_area.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {
            override fun onSwipeLeft() {
                showLastMediaPreview()
            }

            override fun onSwipeTop() {
                toggleFilterScrollArea(false)
            }

            override fun onSwipeBottom() {
                toggleFilterScrollArea(true)
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                /** Must call super here in order to
                 * keep swipes working
                 */
                super.onTouch(v, event);


                if (MotionEvent.ACTION_DOWN == event.getAction())
                {
                   System.out.println("ACTION_DOWN")
                    mQrScanner.scheduleQR(3000);

                   return true;
                }
                else if (MotionEvent.ACTION_UP == event.getAction())
                {
                   System.out.println("ACTION_UP")
                    mQrScanner.cancelQr();
                   return true;
                }

                return true;
            }

        })

        toggle_flash.setOnClickListener { toggleFlash() }
        settings.setOnClickListener { launchSettings() }
        toggle_photo_video.setOnClickListener { handleTogglePhotoVideo() }
        change_resolution.setOnClickListener { handleChangeResolutionDialog() }
        countdown_toggle.setOnClickListener { toggleCountdownTimer() }
        countdown_time_selected.setOnClickListener { toggleCountdownTimer() }
        btn_short_timer.setOnClickListener { setCountdownMode(TIMER_SHORT) }
        btn_medium_timer.setOnClickListener { setCountdownMode(TIMER_MEDIUM) }
        btn_long_timer.setOnClickListener { setCountdownMode(TIMER_LONG) }

        shutter.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // will start mBurstModeSetup after holding for 2 seconds
                        mBurstHandler.postDelayed(mBurstModeSetup, 2000)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        mBurstHandler.removeCallbacks(mBurstRunnable)
                        mBurstHandler.removeCallbacks(mBurstModeSetup)
                        if (!mBurstEnabled) {
                            /* regular shutterPressed() actions get executed if thumb was released
                                 within 2 secs */
                            shutterPressed()
                        }

                        mBurstEnabled = false
                        toggleBurstModeButton()

                        return true
                    }
                }
                return false
            }
        })

        filter_none.beGone()
        filter_mono.beGone()
        filter_negative.beGone()
        filter_solarize.beGone()
        filter_sepia.beGone()
        filter_posterize.beGone()
        filter_whiteboard.beGone()
        filter_blackboard.beGone()
        filter_aqua.beGone()
    }

    private fun toggleCamera() {
        if (checkCameraAvailable()) {
            mPreview!!.toggleFrontBackCamera()
        }
    }

    internal fun setCountdownMode(time: Int) {
        if (checkCameraAvailable()) {
            mCountdownTime = time
            mIsInCountdownMode = true
            toggleCountdownModeIcon(mCountdownTime)
        }
    }

    internal fun unsetCountdownMode() {
        mCountdownTime = 0
        mIsInCountdownMode = false
        toggleCountdownModeIcon(mCountdownTime)
    }

    private fun toggleCountdownTimer() {
        when {
            mIsInCountdownMode -> unsetCountdownMode()
            countdown_toggle.alpha == .5f -> fadeInButtons()
            else -> toggleCountdownTimerDropdown()
        }
    }

    internal fun toggleCountdownModeIcon(time: Int) {
        if (mIsInCountdownMode) {
            countdown_cancel.beVisible()
            countdown_toggle.beInvisible()
            countdown_time_selected.beVisible()
            countdown_time_selected.text = time.toString()
            toggleCountdownTimerDropdown()
        } else {
            countdown_cancel.beInvisible()
            countdown_time_selected.beInvisible()
            countdown_toggle.beVisible()
            countdown_time_selected.text = time.toString()
        }
    }

    internal fun toggleFilterScrollArea(hide: Boolean) {
        if (mIsInPhotoMode && !mIsInCountdownMode && !hide) {
            filter_scroll_area.beVisible()
            hideNotAvailableFilters(mPreview?.getAvailableFilters()!!)
        } else filter_scroll_area.beInvisible()
    }

    private fun showLastMediaPreview() {
        if (mPreviewUri != null) {
            val path = applicationContext.getRealPathFromURI(mPreviewUri!!)
                    ?: mPreviewUri!!.toString()
            openPathIntent(path, false, BuildConfig.APPLICATION_ID)
        }
    }

    private fun toggleFlash() {
        if (checkCameraAvailable() && toggle_flash.alpha == 1f) {
            mPreview?.toggleFlashlight()
        } else {
            fadeInButtons()
        }
    }

    internal fun toggleCountdownTimerDropdown() {
        var countdownDropdown = countdown_times
        if (countdownDropdown.visibility == View.INVISIBLE) countdownDropdown.beVisible() else countdownDropdown.beInvisible()
    }

    internal fun toggleBurstModeButton() {
        if (mBurstEnabled) {
            shutter.beGone()
            burst.beVisible()
        } else {
            burst.beGone()
            shutter.beVisible()
        }
    }

    fun updateFlashlightState(state: Int) {
        config.flashlightState = state
        val flashDrawable = when (state) {
            FLASH_OFF -> R.drawable.ic_flash_off
            FLASH_ON -> R.drawable.ic_flash_on
            else -> R.drawable.ic_flash_auto
        }
        toggle_flash.setImageResource(flashDrawable)
    }

    fun updateCameraIcon(isUsingFrontCamera: Boolean) {
        toggle_camera.setImageResource(if (isUsingFrontCamera) R.drawable.ic_camera_rear else R.drawable.ic_camera_front)
    }

    internal fun shutterPressed() {
        if (checkCameraAvailable()) {
            handleShutter()
        }
    }

    internal fun handleShutter() {
        if (mIsInPhotoMode && mBurstEnabled && !mIsInCountdownMode) {
            toggleBurstModeButton()
            mBurstHandler.post(mBurstRunnable)
        } else if (mIsInPhotoMode && !mIsInCountdownMode) {
            toggleBottomButtons(true)
            mPreview?.tryTakePicture()
        } else if (mIsInPhotoMode && mIsInCountdownMode) {
            toggleBottomButtons(true)
            toggleTopButtons(true)
            toggleFilterScrollArea(true)
            startCountdown()
        } else {
            mPreview?.toggleRecording()
        }
    }

    fun toggleBottomButtons(hide: Boolean) {
        runOnUiThread {
            val alpha = if (hide) 0f else 1f
            shutter.animate().alpha(alpha).start()
            toggle_camera.animate().alpha(alpha).start()
            toggle_photo_video.animate().alpha(alpha).start()
            filter_scroll_area.animate().alpha(alpha).start()
            shutter.isClickable = !hide
            toggle_camera.isClickable = !hide
            toggle_photo_video.isClickable = !hide
            filter_scroll_area.isClickable = !hide
        }
    }

    fun toggleTopButtons(hide: Boolean) {
        runOnUiThread {
            if (hide) {
                settings.beInvisible()
                change_resolution.beInvisible()
                toggle_flash.beInvisible()
                last_image.beInvisible()
                swipe_area.beInvisible()
            } else {
                settings.beVisible()
                change_resolution.beVisible()
                toggle_flash.beVisible()
                last_image.beVisible()
                swipe_area.beVisible()
            }
            settings.isClickable = !hide
            change_resolution.isClickable = !hide
            toggle_flash.isClickable = !hide
            last_image.isClickable = !hide
        }
    }

    private fun launchSettings() {
        if (settings.alpha == 1f) {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        } else {
            fadeInButtons()
        }
    }

    private fun handleTogglePhotoVideo() {
        handlePermission(PERMISSION_RECORD_AUDIO) {
            if (it) {
                togglePhotoVideo()
            } else {
                toast(R.string.no_audio_permissions)
                if (mIsVideoCaptureIntent) {
                    finish()
                }
            }
        }
    }

    private fun handleChangeResolutionDialog() {
        if (change_resolution.alpha == 1f) mPreview?.showChangeResolutionDialog() else fadeInButtons()
    }

    private fun togglePhotoVideo() {
        if (!checkCameraAvailable()) {
            return
        }

        if (mIsVideoCaptureIntent) {
            mPreview?.tryInitVideoMode()
        }

        mPreview?.setFlashlightState(FLASH_OFF)
        unsetCountdownMode() // Disable the countdown when you toggle photo/video mode
        hideTimer()
        mIsInPhotoMode = !mIsInPhotoMode
        config.initPhotoMode = mIsInPhotoMode
        showToggleCameraIfNeeded()
        checkButtons()
        // toggleBottomButtons(false)
    }

    internal fun checkButtons() {
        if (mIsInPhotoMode) {
            initPhotoMode()
        } else {
            tryInitVideoMode()
        }
    }

    private fun initPhotoMode() {
        toggle_photo_video.setImageResource(R.drawable.ic_video)
        shutter.setImageResource(R.drawable.ic_shutter)
        countdown_toggle.beVisible()
        mPreview?.initPhotoMode()
        setupPreviewImage(true)
    }

    internal fun tryInitVideoMode() {
        if (mPreview?.initVideoMode() == true) {
            initVideoButtons()
        } else {
            if (!mIsVideoCaptureIntent) {
                toast(R.string.video_mode_error)
            }
        }
    }

    private fun initVideoButtons() {
        toggle_photo_video.setImageResource(R.drawable.ic_camera)
        countdown_toggle.beInvisible()
        countdown_times.beInvisible()
        filter_scroll_area.beInvisible()
        showToggleCameraIfNeeded()
        shutter.setImageResource(R.drawable.ic_video_rec)
        setupPreviewImage(false)
        mPreview?.checkFlashlight()
    }

    private fun setupPreviewImage(isPhoto: Boolean) {
        val uri = if (isPhoto) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val lastMediaId = getLatestMediaId(uri)
        if (lastMediaId == 0L) {
            return
        }

        mPreviewUri = Uri.withAppendedPath(uri, lastMediaId.toString())

        runOnUiThread {
            if (!isDestroyed) {
                val options = RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)

                Glide.with(this)
                        .load(mPreviewUri)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade())
            }
        }
    }

    private fun scheduleFadeOut() {
        if (!config.keepSettingsVisible) {
            mFadeHandler.postDelayed({
                fadeOutButtons()
            }, FADE_DELAY)
        }
    }

    private fun fadeOutButtons() {
        fadeAnim(settings, .5f)
        fadeAnim(change_resolution, .5f)
        fadeAnim(last_image, .5f)
        fadeAnim(toggle_flash, .5f)
        fadeAnim(countdown_toggle, .5f)
        fadeAnim(countdown_time_selected, .5f)
        fadeAnim(countdown_times, .0f)
        fadeAnim(btn_short_timer, .0f)
        fadeAnim(btn_medium_timer, .0f)
        fadeAnim(btn_long_timer, .0f)
    }

    private fun fadeInButtons() {
        fadeAnim(settings, 1f)
        fadeAnim(change_resolution, 1f)
        fadeAnim(last_image, 1f)
        fadeAnim(toggle_flash, 1f)
        fadeAnim(countdown_toggle, 1f)
        fadeAnim(countdown_time_selected, 1f)
        fadeAnim(countdown_times, 1f)
        fadeAnim(btn_short_timer, 1f)
        fadeAnim(btn_medium_timer, 1f)
        fadeAnim(btn_long_timer, 1f)
        scheduleFadeOut()
    }

    private fun fadeAnim(view: View, value: Float) {
        view.animate().alpha(value).start()
        view.isClickable = value != .0f
    }

    private fun hideNavigationBarIcons() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
    }

    private fun showTimer() {
        video_rec_curr_timer.beVisible()
        setupTimer()
    }

    private fun hideTimer() {
        video_rec_curr_timer.text = 0.getFormattedDuration()
        video_rec_curr_timer.beGone()
        mCurrVideoRecTimer = 0
        mTimerHandler.removeCallbacksAndMessages(null)
    }

    private fun setupTimer() {
        runOnUiThread(object : Runnable {
            override fun run() {
                video_rec_curr_timer.text = mCurrVideoRecTimer++.getFormattedDuration()
                mTimerHandler.postDelayed(this, 1000L)
            }
        })
    }

    internal fun startCountdown() {
        /* Starts the countdown timer and calls tryTakePicture() if it reaches 0. */
        object : CountDownTimer(mCountdownTime * COUNTDOWN_INTERVAL, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                /* Cancels the countdown */
                if (!mIsInCountdownMode) {
                    toggleBottomButtons(false)
                    toggleTopButtons(false)
                    cancel()
                }
                countdown_time_selected.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                mPreview?.tryTakePicture()
                unsetCountdownMode()
            }
        }.start()
    }

    private fun resumeCameraItems() {
        showToggleCameraIfNeeded()
        hideNavigationBarIcons()

        if (!mIsInPhotoMode) {
            initVideoButtons()
        }
    }

    private fun showToggleCameraIfNeeded() {
        toggle_camera?.beInvisibleIf(mCameraImpl.getCountOfCameras() <= 1)
    }

    private fun hasStorageAndCameraPermissions() = hasPermission(PERMISSION_WRITE_STORAGE) && hasPermission(PERMISSION_CAMERA)

    private fun setupOrientationEventListener() {
        mOrientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                if (isDestroyed) {
                    mOrientationEventListener.disable()
                    return
                }

                val currOrient = when (orientation) {
                    in 75..134 -> ORIENT_LANDSCAPE_RIGHT
                    in 225..289 -> ORIENT_LANDSCAPE_LEFT
                    else -> ORIENT_PORTRAIT
                }

                if (currOrient != mLastHandledOrientation) {
                    val degrees = when (currOrient) {
                        ORIENT_LANDSCAPE_LEFT -> 90
                        ORIENT_LANDSCAPE_RIGHT -> -90
                        else -> 0
                    }

                    animateViews(degrees)
                    mLastHandledOrientation = currOrient
                }
            }
        }
    }

    private fun animateViews(degrees: Int) {
        val views = arrayOf<View>(toggle_camera, toggle_flash, toggle_photo_video, change_resolution, shutter, settings, countdown_toggle, countdown_time_selected, countdown_times)
        for (view in views) {
            rotate(view, degrees)
        }
    }

    private fun rotate(view: View, degrees: Int) = view.animate().rotation(degrees.toFloat()).start()

    internal fun checkCameraAvailable(): Boolean {
        if (!mIsCameraAvailable) {
            toast(R.string.camera_unavailable)
        }
        return mIsCameraAvailable
    }

    fun setFlashAvailable(available: Boolean) {
        if (available) {
            toggle_flash.beVisible()
        } else {
            toggle_flash.beInvisible()
            toggle_flash.setImageResource(R.drawable.ic_flash_off)
            mPreview?.setFlashlightState(FLASH_OFF)
        }
    }

    fun setIsCameraAvailable(available: Boolean) {
        mIsCameraAvailable = available
    }

    fun setRecordingState(isRecording: Boolean) {
        runOnUiThread {
            if (isRecording) {
                shutter.setImageResource(R.drawable.ic_video_stop)
                toggle_camera.beInvisible()
                showTimer()
            } else {
                shutter.setImageResource(R.drawable.ic_video_rec)
                showToggleCameraIfNeeded()
                hideTimer()
            }
        }
    }

    fun videoSaved(uri: Uri) {
        setupPreviewImage(false)
        if (mIsVideoCaptureIntent) {
            Intent().apply {
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                setResult(Activity.RESULT_OK, this)
            }
            finish()
        }
    }

    fun drawFocusCircle(x: Float, y: Float) = mFocusCircleView.drawFocusCircle(x, y)

    override fun mediaSaved(path: String) {
        rescanPaths(arrayListOf(path)) {
            setupPreviewImage(true)
            Intent(BROADCAST_REFRESH_MEDIA).apply {
                putExtra(REFRESH_PATH, path)
                `package` = "com.simplemobiletools.gallery"
                sendBroadcast(this)
            }
        }

        if (isImageCaptureIntent()) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun checkWhatsNewDialog() {
        arrayListOf<Release>().apply {
            add(Release(33, R.string.release_33))
            add(Release(35, R.string.release_35))
            add(Release(39, R.string.release_39))
            add(Release(44, R.string.release_44))
            add(Release(46, R.string.release_46))
            add(Release(52, R.string.release_52))
            checkWhatsNew(this, BuildConfig.VERSION_CODE)
        }
    }

    fun getPhotoTaken(): Boolean? {
        return this.mPreview?.getUITestPhotoTaken()
    }

    internal fun handleGridLine() {
        if (!config.gridLineVisible) {
            hideGridLine()
        } else
            showGridLine()
    }

    internal fun hideGridLine() {
        gridline.beInvisible()
    }

    internal fun showGridLine() {
        gridline.beVisible()
    }

    internal fun handleGPS() {
        if (!config.gpsTaggingOn) {
            addressFirstLine = ""
            addressSecondLine = ""
            addressCoordinates = ""
        } else
            stampGPS()
    }

    /**
     * Need to have permission first to be able to get location: TURN ON LOCATION FOR APP
     */
    @SuppressLint("MissingPermission")
    internal fun stampGPS() {

        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>
        var addressNumber: String
        var addressStreet: String
        var addressProvince: String
        var addressCountry: String
        var latitude: Double
        var longitude: Double

        mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {

                        // Record location information into variable mLastLocation
                        mLastLocation = task.result

                        // Get the latitude and longitude from mLastLocation
                        latitude = mLastLocation!!.latitude
                        longitude = mLastLocation!!.longitude

                        // Transform latitude and longitude into address -- maxResults = 1 just because we want to fetch 1 address. Can be changed to more if desired
                        addresses = geocoder.getFromLocation(latitude, longitude, 1)

                        // Parse the first address in the array
                        addressNumber = addresses[0].featureName
                        addressStreet = addresses[0].thoroughfare
                        addressFirstLine = addressNumber + " " + addressStreet

                        addressProvince = addresses[0].adminArea
                        addressCountry = addresses[0].countryCode
                        addressSecondLine = addressProvince + ", " + addressCountry

                        addressCoordinates = latitude.toString().dropLast(3) + "N," + longitude.toString().dropLast(3) + "E"
                    }
                }
    }

    fun colorEffectFilter(v: View) {
        try {
            var index = 0
            when (v.id) {
                R.id.filter_none -> {
                    index = 0
                }
                R.id.filter_mono -> {
                    index = 1
                }
                R.id.filter_negative -> {
                    index = 2
                }
                R.id.filter_solarize -> {
                    index = 3
                }
                R.id.filter_sepia -> {
                    index = 4
                }
                R.id.filter_posterize -> {
                    index = 5
                }
                R.id.filter_whiteboard -> {
                    index = 6
                }
                R.id.filter_blackboard -> {
                    index = 7
                }
                R.id.filter_aqua -> {
                    index = 8
                }
            }
            mPreview?.previewFilter(index)
        } catch (ex: Exception) {
        }
    }

    fun hideNotAvailableFilters(intArr: IntArray) {

        for (i in intArr.indices) {
            when (i) {
                0 -> {
                    filter_none.beVisible()
                }
                1 -> {
                    filter_mono.beVisible()
                }
                2 -> {
                    filter_negative.beVisible()
                }
                3 -> {
                    filter_solarize.beVisible()
                }
                4 -> {
                    filter_sepia.beVisible()
                }
                5 -> {
                    filter_posterize.beVisible()
                }
                6 -> {
                    filter_whiteboard.beVisible()
                }
                7 -> {
                    filter_blackboard.beVisible()
                }
                8 -> {
                    filter_aqua.beVisible()
                }
            }
        }
    }

    fun testPreviewFilterWrapper(index: Int): Boolean {
        return this.mPreview!!.previewFilter(index)
    }
}
