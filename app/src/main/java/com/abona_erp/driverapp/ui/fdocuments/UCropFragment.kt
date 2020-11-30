package com.abona_erp.driverapp.ui.fdocuments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.ui.fdocuments.UCropFragmentArgs.Companion.fromBundle
import com.abona_erp.driverapp.ui.fdocuments.callback.BitmapCropCallback
import com.abona_erp.driverapp.ui.fdocuments.model.AspectRatio
import com.abona_erp.driverapp.ui.fdocuments.util.SelectedStateListDrawable
import com.abona_erp.driverapp.ui.fdocuments.view.CropImageView
import com.abona_erp.driverapp.ui.fdocuments.view.GestureCropImageView
import com.abona_erp.driverapp.ui.fdocuments.view.OverlayView
import com.abona_erp.driverapp.ui.fdocuments.view.TransformImageView.TransformImageListener
import com.abona_erp.driverapp.ui.fdocuments.view.UCropView
import com.abona_erp.driverapp.ui.fdocuments.view.widget.AspectRatioTextView
import com.abona_erp.driverapp.ui.fdocuments.view.widget.HorizontalProgressWheelView
import com.abona_erp.driverapp.ui.fdocuments.view.widget.HorizontalProgressWheelView.ScrollingListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
open class UCropFragment : Fragment() {
    private var callback: UCropFragmentCallback? = null
    private var mActiveControlsWidgetColor = 0

    @ColorInt
    private var mRootViewBackgroundColor = 0
    private var mLogoColor = 0
    private var mShowBottomControls = false
    private var mControlsTransition: Transition? = null
    private var mUCropView: UCropView? = null
    private var mGestureCropImageView: GestureCropImageView? = null
    private var mOverlayView: OverlayView? = null
    private var mWrapperStateAspectRatio: ViewGroup? = null
    private var mWrapperStateRotate: ViewGroup? = null
    private var mWrapperStateScale: ViewGroup? = null
    private var mLayoutAspectRatio: ViewGroup? = null
    private var mLayoutRotate: ViewGroup? = null
    private var mLayoutScale: ViewGroup? = null
    private val mCropAspectRatioViews: MutableList<ViewGroup> = ArrayList()
    private var mTextViewRotateAngle: TextView? = null
    private var mTextViewScalePercent: TextView? = null
    private var mBlockingView: View? = null
    private val mCompressFormat = DEFAULT_COMPRESS_FORMAT
    private val mCompressQuality = DEFAULT_COMPRESS_QUALITY
    private val mAllowedGestures = intArrayOf(SCALE, ROTATE, ALL)

    companion object {
        const val DEFAULT_COMPRESS_QUALITY = 90
        val DEFAULT_COMPRESS_FORMAT = CompressFormat.PNG
        const val SCALE = 1
        const val ROTATE = 2
        const val ALL = 3
        const val TAG = "UCropFragment"
        private const val CONTROLS_ANIMATION_DURATION: Long = 50
        private const val SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000
        private const val ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42
        fun newInstance(uCrop: Bundle?): UCropFragment {
            val fragment = UCropFragment()
            fragment.arguments = uCrop
            return fragment
        }

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback =
            when {
                parentFragment is UCropFragmentCallback -> parentFragment as UCropFragmentCallback?
                context is UCropFragmentCallback -> context
                else -> throw IllegalArgumentException(
                    context.toString()
                            + " must implement UCropFragmentCallback"
                )
            }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ucrop_fragment_photobox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setImageData()
        setInitialState()
        addBlockingView(view)
    }

    private fun setupViews(view: View) {
        mActiveControlsWidgetColor =
            ContextCompat.getColor(requireContext(), R.color.ucrop_color_active_controls_color)
        mLogoColor = ContextCompat.getColor(requireContext(), R.color.ucrop_color_default_logo)
        mShowBottomControls = true
        mRootViewBackgroundColor =
            ContextCompat.getColor(requireContext(), R.color.ucrop_color_crop_background)
        initiateRootViews(view)
        if (mShowBottomControls) {
            val wrapper = view.findViewById<ViewGroup>(R.id.controls_wrapper)
            wrapper.visibility = View.VISIBLE
            LayoutInflater.from(context).inflate(R.layout.ucrop_controls, wrapper, true)
            mControlsTransition = AutoTransition()
            mControlsTransition?.duration = CONTROLS_ANIMATION_DURATION
            mWrapperStateAspectRatio = view.findViewById(R.id.state_aspect_ratio)
            mWrapperStateAspectRatio?.setOnClickListener(mStateClickListener)
            mWrapperStateRotate = view.findViewById(R.id.state_rotate)
            mWrapperStateRotate?.setOnClickListener(mStateClickListener)
            mWrapperStateScale = view.findViewById(R.id.state_scale)
            mWrapperStateScale?.setOnClickListener(mStateClickListener)
            mLayoutAspectRatio = view.findViewById(R.id.layout_aspect_ratio)
            mLayoutRotate = view.findViewById(R.id.layout_rotate_wheel)
            mLayoutScale = view.findViewById(R.id.layout_scale_wheel)
            setupAspectRatioWidget(view)
            setupRotateWidget(view)
            setupScaleWidget(view)
            setupStatesWrapper(view)
        } else {
            val params =
                view.findViewById<View>(R.id.ucrop_frame).layoutParams as RelativeLayout.LayoutParams
            params.bottomMargin = 0
            view.findViewById<View>(R.id.ucrop_frame).requestLayout()
        }
    }

    private fun setImageData() {
        val inputUri = fromBundle(requireArguments()).inputUri
        val outputUri = fromBundle(requireArguments()).outputUri
        processOptions()
        try {
            mGestureCropImageView!!.setImageUri(inputUri, outputUri)
        } catch (e: Exception) {
            callback!!.onCropFinish(getError(e))
        }
    }

    @Suppress("DEPRECATION")
    private fun processOptions() {
        // Crop image view options
        mGestureCropImageView!!.maxBitmapSize = CropImageView.DEFAULT_MAX_BITMAP_SIZE
        mGestureCropImageView!!.setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER)
        mGestureCropImageView!!.setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION.toLong())

        // Overlay view options
        mOverlayView!!.isFreestyleCropEnabled = true
        mOverlayView!!.setDimmedColor(ContextCompat.getColor(requireContext(), R.color.ucrop_color_default_dimmed))
        mOverlayView!!.setCircleDimmedLayer(OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER)
        mOverlayView!!.setShowCropFrame(OverlayView.DEFAULT_SHOW_CROP_FRAME)
        mOverlayView!!.setCropFrameColor(ContextCompat.getColor(requireContext(), R.color.ucrop_color_default_crop_frame))
        mOverlayView!!.setCropFrameStrokeWidth(resources.getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width))
        mOverlayView!!.setShowCropGrid(OverlayView.DEFAULT_SHOW_CROP_GRID)
        mOverlayView!!.setCropGridRowCount(OverlayView.DEFAULT_CROP_GRID_ROW_COUNT)
        mOverlayView!!.setCropGridColumnCount(OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT)
        mOverlayView!!.setCropGridColor(ContextCompat.getColor(requireContext(), R.color.ucrop_color_default_crop_grid))
        mOverlayView!!.setCropGridStrokeWidth(resources.getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width))

        // Aspect ratio options
        val aspectRatioX = 0f
        val aspectRatioY = 0f
        val aspectRationSelectedByDefault = 0
        val aspectRatioList = ArrayList<AspectRatio>()
        if (aspectRatioX > 0 && aspectRatioY > 0) {
            if (mWrapperStateAspectRatio != null) {
                mWrapperStateAspectRatio!!.visibility = View.GONE
            }
            mGestureCropImageView!!.targetAspectRatio = aspectRatioX / aspectRatioY
        } else if (aspectRationSelectedByDefault < aspectRatioList.size) {
            mGestureCropImageView!!.targetAspectRatio =
                aspectRatioList[aspectRationSelectedByDefault].aspectRatioX /
                        aspectRatioList[aspectRationSelectedByDefault].aspectRatioY
        } else {
            mGestureCropImageView!!.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO
        }

        // Result bitmap max size options
        val maxSizeX = 0
        val maxSizeY = 0
        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView!!.setMaxResultImageSizeX(maxSizeX)
            mGestureCropImageView!!.setMaxResultImageSizeY(maxSizeY)
        }
    }

    private fun initiateRootViews(view: View) {
        mUCropView = view.findViewById(R.id.ucrop)
        mGestureCropImageView = mUCropView?.cropImageView
        mOverlayView = mUCropView?.overlayView
        mGestureCropImageView!!.setTransformImageListener(mImageListener)
        (view.findViewById<View>(R.id.image_view_logo) as ImageView).setColorFilter(
            mLogoColor,
            PorterDuff.Mode.SRC_ATOP
        )
        view.findViewById<View>(R.id.ucrop_frame).setBackgroundColor(mRootViewBackgroundColor)
    }

    private val mImageListener: TransformImageListener = object : TransformImageListener {
        override fun onRotate(currentAngle: Float) {
            setAngleText(currentAngle)
        }

        override fun onScale(currentScale: Float) {
            setScaleText(currentScale)
        }

        override fun onLoadComplete() {
            mUCropView!!.animate().alpha(1f).setDuration(300).interpolator =
                AccelerateInterpolator()
            mBlockingView!!.isClickable = false
        }

        override fun onLoadFailure(e: Exception) {
            callback!!.onCropFinish(getError(e))
        }
    }

    /**
     * Use [.mActiveControlsWidgetColor] for color filter
     */
    private fun setupStatesWrapper(view: View) {
        val stateScaleImageView = view.findViewById<ImageView>(R.id.image_view_state_scale)
        val stateRotateImageView = view.findViewById<ImageView>(R.id.image_view_state_rotate)
        val stateAspectRatioImageView =
            view.findViewById<ImageView>(R.id.image_view_state_aspect_ratio)
        stateScaleImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateScaleImageView.drawable,
                mActiveControlsWidgetColor
            )
        )
        stateRotateImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateRotateImageView.drawable,
                mActiveControlsWidgetColor
            )
        )
        stateAspectRatioImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateAspectRatioImageView.drawable,
                mActiveControlsWidgetColor
            )
        )
    }

    private fun setupAspectRatioWidget(view: View) {
        var aspectRationSelectedByDefault = 0
        var aspectRatioList = ArrayList<AspectRatio?>()
        if (aspectRatioList.isEmpty()) {
            aspectRationSelectedByDefault = 2
            aspectRatioList = ArrayList()
            aspectRatioList.add(AspectRatio(null, 1f, 1f))
            aspectRatioList.add(AspectRatio(null, 3f, 4f))
            aspectRatioList.add(
                AspectRatio(
                    getString(R.string.ucrop_label_original).toUpperCase(Locale.ROOT),
                    CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO
                )
            )
            aspectRatioList.add(AspectRatio(null, 3f, 2f))
            aspectRatioList.add(AspectRatio(null, 16f, 9f))
        }
        val wrapperAspectRatioList = view.findViewById<LinearLayout>(R.id.layout_aspect_ratio)
        var wrapperAspectRatio: FrameLayout
        var aspectRatioTextView: AspectRatioTextView
        val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        lp.weight = 1f
        for (aspectRatio in aspectRatioList) {
            wrapperAspectRatio =
                layoutInflater.inflate(R.layout.ucrop_aspect_ratio, null) as FrameLayout
            wrapperAspectRatio.layoutParams = lp
            aspectRatioTextView = wrapperAspectRatio.getChildAt(0) as AspectRatioTextView
            aspectRatioTextView.setActiveColor(mActiveControlsWidgetColor)
            aspectRatioTextView.setAspectRatio(aspectRatio!!)
            wrapperAspectRatioList.addView(wrapperAspectRatio)
            mCropAspectRatioViews.add(wrapperAspectRatio)
        }
        mCropAspectRatioViews[aspectRationSelectedByDefault].isSelected = true
        for (cropAspectRatioView in mCropAspectRatioViews) {
            cropAspectRatioView.setOnClickListener { v ->
                mGestureCropImageView!!.targetAspectRatio =
                    ((v as ViewGroup).getChildAt(0) as AspectRatioTextView).getAspectRatio(v.isSelected())
                mGestureCropImageView!!.setImageToWrapCropBounds()
                if (!v.isSelected()) {
                    for (aspectRationViews in mCropAspectRatioViews) {
                        aspectRationViews.isSelected = aspectRationViews === v
                    }
                }
            }
        }
    }

    private fun setupRotateWidget(view: View) {
        mTextViewRotateAngle = view.findViewById(R.id.text_view_rotate)
        (view.findViewById<View>(R.id.rotate_scroll_wheel) as HorizontalProgressWheelView)
            .setScrollingListener(object : ScrollingListener {
                override fun onScroll(delta: Float, totalDistance: Float) {
                    mGestureCropImageView!!.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT)
                }

                override fun onScrollEnd() {
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                }

                override fun onScrollStart() {
                    mGestureCropImageView!!.cancelAllAnimations()
                }
            })
        (view.findViewById<View>(R.id.rotate_scroll_wheel) as HorizontalProgressWheelView).setMiddleLineColor(
            mActiveControlsWidgetColor
        )
        view.findViewById<View>(R.id.wrapper_reset_rotate).setOnClickListener { resetRotation() }
        view.findViewById<View>(R.id.wrapper_rotate_by_angle)
            .setOnClickListener { rotateByAngle() }
        setAngleTextColor(mActiveControlsWidgetColor)
    }

    private fun setupScaleWidget(view: View) {
        mTextViewScalePercent = view.findViewById(R.id.text_view_scale)
        (view.findViewById<View>(R.id.scale_scroll_wheel) as HorizontalProgressWheelView)
            .setScrollingListener(object : ScrollingListener {
                override fun onScroll(delta: Float, totalDistance: Float) {
                    if (delta > 0) {
                        mGestureCropImageView!!.zoomInImage(
                            mGestureCropImageView!!.currentScale
                                    + delta * ((mGestureCropImageView!!.maxScale - mGestureCropImageView!!.minScale) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT)
                        )
                    } else {
                        mGestureCropImageView!!.zoomOutImage(
                            mGestureCropImageView!!.currentScale
                                    + delta * ((mGestureCropImageView!!.maxScale - mGestureCropImageView!!.minScale) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT)
                        )
                    }
                }

                override fun onScrollEnd() {
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                }

                override fun onScrollStart() {
                    mGestureCropImageView!!.cancelAllAnimations()
                }
            })
        (view.findViewById<View>(R.id.scale_scroll_wheel) as HorizontalProgressWheelView).setMiddleLineColor(
            mActiveControlsWidgetColor
        )
        setScaleTextColor(mActiveControlsWidgetColor)
    }

    private fun setAngleText(angle: Float) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle!!.text = String.format(Locale.getDefault(), "%.1f°", angle)
        }
    }

    private fun setAngleTextColor(textColor: Int) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle!!.setTextColor(textColor)
        }
    }

    private fun setScaleText(scale: Float) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent!!.text =
                String.format(Locale.getDefault(), "%d%%", (scale * 100).toInt())
        }
    }

    private fun setScaleTextColor(textColor: Int) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent!!.setTextColor(textColor)
        }
    }

    private fun resetRotation() {
        mGestureCropImageView!!.postRotate(-mGestureCropImageView!!.currentAngle)
        mGestureCropImageView!!.setImageToWrapCropBounds()
    }

    private fun rotateByAngle() {
        mGestureCropImageView!!.postRotate(90.toFloat())
        mGestureCropImageView!!.setImageToWrapCropBounds()
    }

    private val mStateClickListener = View.OnClickListener { v ->
        if (!v.isSelected) {
            setWidgetState(v.id)
        }
    }

    private fun setInitialState() {
        if (mShowBottomControls) {
            if (mWrapperStateAspectRatio!!.visibility == View.VISIBLE) {
                setWidgetState(R.id.state_aspect_ratio)
            } else {
                setWidgetState(R.id.state_scale)
            }
        } else {
            setAllowedGestures(0)
        }
    }

    private fun setWidgetState(@IdRes stateViewId: Int) {
        if (!mShowBottomControls) return
        mWrapperStateAspectRatio!!.isSelected = stateViewId == R.id.state_aspect_ratio
        mWrapperStateRotate!!.isSelected = stateViewId == R.id.state_rotate
        mWrapperStateScale!!.isSelected = stateViewId == R.id.state_scale
        mLayoutAspectRatio!!.visibility =
            if (stateViewId == R.id.state_aspect_ratio) View.VISIBLE else View.GONE
        mLayoutRotate!!.visibility =
            if (stateViewId == R.id.state_rotate) View.VISIBLE else View.GONE
        mLayoutScale!!.visibility =
            if (stateViewId == R.id.state_scale) View.VISIBLE else View.GONE
        changeSelectedTab(stateViewId)
        when (stateViewId) {
            R.id.state_scale -> {
                setAllowedGestures(0)
            }
            R.id.state_rotate -> {
                setAllowedGestures(1)
            }
            else -> {
                setAllowedGestures(2)
            }
        }
    }

    private fun changeSelectedTab(stateViewId: Int) {
        if (view != null) {
            TransitionManager.beginDelayedTransition(
                (requireView().findViewById<View>(R.id.ucrop_photobox) as ViewGroup),
                mControlsTransition
            )
        }
        mWrapperStateScale!!.findViewById<View>(R.id.text_view_scale).visibility =
            if (stateViewId == R.id.state_scale) View.VISIBLE else View.GONE
        mWrapperStateAspectRatio!!.findViewById<View>(R.id.text_view_crop).visibility =
            if (stateViewId == R.id.state_aspect_ratio) View.VISIBLE else View.GONE
        mWrapperStateRotate!!.findViewById<View>(R.id.text_view_rotate).visibility =
            if (stateViewId == R.id.state_rotate) View.VISIBLE else View.GONE
    }

    private fun setAllowedGestures(tab: Int) {
        mGestureCropImageView!!.isScaleEnabled =
            mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == SCALE
        mGestureCropImageView!!.isRotateEnabled =
            mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == ROTATE
    }

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private fun addBlockingView(view: View) {
        if (mBlockingView == null) {
            mBlockingView = View(context)
            val lp = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            mBlockingView!!.layoutParams = lp
            mBlockingView!!.isClickable = true
        }
        (view.findViewById<View>(R.id.ucrop_photobox) as RelativeLayout).addView(mBlockingView)
    }

    private fun cropAndSaveImage() {
        mBlockingView!!.isClickable = true
        requireActivity().onBackPressed()
        mGestureCropImageView!!.cropAndSaveImage(
            mCompressFormat,
            mCompressQuality,
            object : BitmapCropCallback {
                override fun onBitmapCropped(
                    resultUri: Uri,
                    offsetX: Int,
                    offsetY: Int,
                    imageWidth: Int,
                    imageHeight: Int
                ) {
                    callback!!.onCropFinish(
                        getResult(
                            resultUri,
                            mGestureCropImageView!!.targetAspectRatio,
                            offsetX,
                            offsetY,
                            imageWidth,
                            imageHeight
                        )
                    )
                }

                override fun onCropFailure(t: Throwable) {
                    callback!!.onCropFinish(getError(t))
                }
            })
    }

    protected fun getResult(
        uri: Uri?,
        resultAspectRatio: Float,
        offsetX: Int,
        offsetY: Int,
        imageWidth: Int,
        imageHeight: Int
    ): UCropResult {
        return UCropResult(
            Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, offsetX)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, offsetY)
        )
    }

    private fun getError(throwable: Throwable?): UCropResult {
        return UCropResult(Intent().putExtra(UCrop.EXTRA_ERROR, throwable))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.action_log_out).isVisible = false
        menu.findItem(R.id.action_edit_doc).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit_doc) {
            cropAndSaveImage()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class UCropResult(var mResultData: Intent)
}