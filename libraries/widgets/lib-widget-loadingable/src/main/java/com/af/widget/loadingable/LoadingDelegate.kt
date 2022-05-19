package com.lyy.ev.operation.widget.loading

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewConfiguration
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnChildScrollUpCallback
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.lyy.ev.operation.R
import com.lyy.log.LogService
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

/**
 * =================================================================================================
 *     __
 *    / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *   / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *  / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 *             /____/                  /____/
 * =================================================================================================
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-04-25
 */
class LoadingDelegate(private val loadingable: Loadingable) {

    companion object {
        private const val TAG = "loading"
    }

    // Maps to ProgressBar.Large style
    val LARGE = CircularProgressDrawable.LARGE

    // Maps to ProgressBar default style
    val DEFAULT = CircularProgressDrawable.DEFAULT

    val DEFAULT_SLINGSHOT_DISTANCE = -1

    @VisibleForTesting
    val CIRCLE_DIAMETER = 40

    @VisibleForTesting
    val CIRCLE_DIAMETER_LARGE = 56

    private val LOG_TAG = SwipeRefreshLayout::class.java.simpleName

    private val MAX_ALPHA = 255
    private val STARTING_PROGRESS_ALPHA = (.3f * MAX_ALPHA).toInt()

    private val DECELERATE_INTERPOLATION_FACTOR = 2f
    private val INVALID_POINTER = -1
    private val DRAG_RATE = .5f

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private val MAX_PROGRESS_ANGLE = .8f

    private val SCALE_DOWN_DURATION = 150

    private val ALPHA_ANIMATION_DURATION = 300

    private val ANIMATE_TO_TRIGGER_DURATION = 200

    private val ANIMATE_TO_START_DURATION = 200

    // Default offset in dips from the top of the view to where the progress spinner should stop
    private val DEFAULT_CIRCLE_TARGET = 64

    private var mTarget // the target of the gesture
            : View? = null
    var mListener: OnRefreshListener? = null
    var mRefreshing = false
    private var mTouchSlop = 0
    private var mTotalDragDistance = -1f

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private val mTotalUnconsumed = 0f
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)

    // Used for calls from old versions of onNestedScroll to v3 version of onNestedScroll. This only
    // exists to prevent GC costs that are present before API 21.
    private val mNestedScrollingV2ConsumedCompat = IntArray(2)
    private val mNestedScrollInProgress = false

    private var mMediumAnimationDuration = 0
    var mCurrentTargetOffsetTop = 0

    private val mInitialMotionY = 0f
    private val mInitialDownY = 0f
    private val mIsBeingDragged = false
    private var mActivePointerId = INVALID_POINTER

    // Whether this item is scaled up rather than clipped
    var mScale = false

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private val mReturningToStart = false
    private var mDecelerateInterpolator: DecelerateInterpolator? = null
    private val LAYOUT_ATTRS = intArrayOf(
        R.attr.enabled
    )

    var mCircleView: CircleImageView? = null
    private var mCircleViewIndex = -1

    protected var mFrom = 0

    var mStartingScale = 0f

    protected var mOriginalOffsetTop = 0

    var mSpinnerOffsetEnd = 0

    var mCustomSlingshotDistance = 0

    var mProgress: CircularProgressDrawable? = null

    private var mScaleAnimation: Animation? = null

    private var mScaleDownAnimation: Animation? = null

    private var mAlphaStartAnimation: Animation? = null

    private var mAlphaMaxAnimation: Animation? = null

    private var mScaleDownToStartAnimation: Animation? = null

    var mNotify = false

    private var mCircleDiameter = 0

    // Whether the client has set a custom starting position;
    var mUsingCustomStart = false

    private val mChildScrollUpCallback: OnChildScrollUpCallback? = null

    /** @see .setLegacyRequestDisallowInterceptTouchEventEnabled
     */
    private val mEnableLegacyRequestDisallowInterceptTouch = false

    private val mRefreshListener: Animation.AnimationListener =
        object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                LogService.d(TAG, "动画开始")
            }
            override fun onAnimationRepeat(animation: Animation) {
                LogService.d(TAG, "动画重复")
            }
            override fun onAnimationEnd(animation: Animation?) {
                LogService.d(TAG, "动画结束")
                if (mRefreshing) {
                    // Make sure the progress view is fully visible
                    mProgress?.alpha = MAX_ALPHA
                    mProgress?.start()
                    if (mNotify) {
                        mListener?.onRefresh()
                    }
                    mCurrentTargetOffsetTop = mCircleView!!.top
                } else {
                    reset()
                }
            }
        }


    fun init(attrs: AttributeSet?) {

        mTouchSlop = ViewConfiguration.get(this.loadingable.getContext()).scaledTouchSlop

        mMediumAnimationDuration = this.loadingable.getContext().resources.getInteger(
            android.R.integer.config_mediumAnimTime
        )

        this.loadingable.setWillNotDraw(false)
        mDecelerateInterpolator =
            DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)

        val metrics: DisplayMetrics = this.loadingable.getContext().resources.displayMetrics
        mCircleDiameter = (CIRCLE_DIAMETER * metrics.density).toInt()

        createProgressView()
        this.loadingable.setChildrenDrawingOrderEnabledPublic(true)
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerOffsetEnd = (DEFAULT_CIRCLE_TARGET * metrics.density).toInt()
        mTotalDragDistance = mSpinnerOffsetEnd.toFloat()

        mOriginalOffsetTop = -mCircleDiameter.also { mCurrentTargetOffsetTop = it }
        moveToStart(1.0f)

        val a: TypedArray = this.loadingable.getContext().obtainStyledAttributes(attrs, LAYOUT_ATTRS)
        setEnabled(a.getBoolean(0, true))
        a.recycle()
    }

    fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return if (mCircleViewIndex < 0) {
            i
        } else if (i == childCount - 1) {
            // Draw the selected child last
            mCircleViewIndex
        } else if (i >= mCircleViewIndex) {
            // Move the children after the selected child earlier one
            i + 1
        } else {
            // Keep the children before the selected child the same
            i
        }
    }

    fun onLayout() {
        val width: Int = this.loadingable.getMeasuredWidth()
        val height: Int = this.loadingable.getMeasuredHeight()
        if (this.loadingable.getChildCount() == 0) {
            return
        }
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }
        val child: View = mTarget!!
        val childLeft: Int = this.loadingable.getPaddingLeft()
        val childTop: Int = this.loadingable.getPaddingTop()
        val childWidth: Int = width - this.loadingable.getPaddingLeft() - this.loadingable.getPaddingRight()
        val childHeight: Int = height - this.loadingable.getPaddingTop() - this.loadingable.getPaddingBottom()
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        val circleWidth = mCircleView!!.measuredWidth
        val circleHeight = mCircleView!!.measuredHeight
        mCircleView!!.layout(
            width / 2 - circleWidth / 2, mCurrentTargetOffsetTop,
            width / 2 + circleWidth / 2, mCurrentTargetOffsetTop + circleHeight
        )
    }

    fun onMeasure() {
        if (mTarget == null) {
            ensureTarget()
        }
        if (mTarget == null) {
            return
        }
        mTarget?.measure(
            MeasureSpec.makeMeasureSpec(
                this.loadingable.getMeasuredWidth() - this.loadingable.getPaddingLeft() - this.loadingable.getPaddingRight(),
                MeasureSpec.EXACTLY
            ), MeasureSpec.makeMeasureSpec(
                this.loadingable.getMeasuredHeight() - this.loadingable.getPaddingTop() - this.loadingable.getPaddingBottom(), MeasureSpec.EXACTLY
            )
        )
        mCircleView?.measure(
            MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mCircleDiameter, MeasureSpec.EXACTLY)
        )
        mCircleViewIndex = -1
        // Get the index of the circle view.
        for (index in 0 until this.loadingable.getChildCount()) {
            if (this.loadingable.getChildAt(index) === mCircleView) {
                mCircleViewIndex = index
                break
            }
        }
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    fun isRefreshing() = this.mRefreshing

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    fun setRefreshing(refreshing: Boolean) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing
            val endTarget = if (!mUsingCustomStart) {
                mSpinnerOffsetEnd + mOriginalOffsetTop
            } else {
                mSpinnerOffsetEnd
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop)
            mNotify = false
            startScaleUpAnimation(mRefreshListener)
        } else {
            setRefreshing(refreshing, false)
        }
    }

    //

    fun reset() {
        mCircleView?.clearAnimation()
        mProgress?.stop()
        mCircleView?.visibility = View.GONE
        setColorViewAlpha(MAX_ALPHA)
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0f)
        } else {
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop)
        }
        mCurrentTargetOffsetTop = mCircleView!!.top
    }

    fun setEnabled(enabled: Boolean) {
        this.loadingable.setEnabled(enabled)
        if (!enabled) {
            reset()
        }
    }

    private fun setColorViewAlpha(targetAlpha: Int) {
        mCircleView!!.background.alpha = targetAlpha
        mProgress!!.alpha = targetAlpha
    }

    /**
     * Pre API 11, this does an alpha animation.
     * @param progress
     */
    fun setAnimationProgress(progress: Float) {
        mCircleView!!.scaleX = progress
        mCircleView!!.scaleY = progress
    }

    fun setTargetOffsetTopAndBottom(offset: Int) {
        mCircleView!!.bringToFront()
        ViewCompat.offsetTopAndBottom(mCircleView!!, offset)
        mCurrentTargetOffsetTop = mCircleView!!.top
    }

    /**
     * The refresh indicator starting and resting position is always positioned
     * near the top of the refreshing content. This position is a consistent
     * location, but can be adjusted in either direction based on whether or not
     * there is a toolbar or actionbar present.
     *
     *
     * **Note:** Calling this will reset the position of the refresh indicator to
     * `start`.
     *
     *
     * @param scale Set to true if there is no view at a higher z-order than where the progress
     * spinner is set to appear. Setting it to true will cause indicator to be scaled
     * up rather than clipped.
     * @param start The offset in pixels from the top of this view at which the
     * progress spinner should appear.
     * @param end The offset in pixels from the top of this view at which the
     * progress spinner should come to rest after a successful swipe
     * gesture.
     */
    fun setProgressViewOffset(scale: Boolean, start: Int, end: Int) {
        mScale = scale
        mOriginalOffsetTop = start
        mSpinnerOffsetEnd = end
        mUsingCustomStart = true
        reset()
        mRefreshing = false
    }

    /**
     * @return The offset in pixels from the top of this view at which the progress spinner should
     * appear.
     */
    fun getProgressViewStartOffset(): Int {
        return mOriginalOffsetTop
    }

    /**
     * @return The offset in pixels from the top of this view at which the progress spinner should
     * come to rest after a successful swipe gesture.
     */
    fun getProgressViewEndOffset(): Int {
        return mSpinnerOffsetEnd
    }

    /**
     * The refresh indicator resting position is always positioned near the top
     * of the refreshing content. This position is a consistent location, but
     * can be adjusted in either direction based on whether or not there is a
     * toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than where the progress
     * spinner is set to appear. Setting it to true will cause indicator to be scaled
     * up rather than clipped.
     * @param end The offset in pixels from the top of this view at which the
     * progress spinner should come to rest after a successful swipe
     * gesture.
     */
    fun setProgressViewEndTarget(scale: Boolean, end: Int) {
        mSpinnerOffsetEnd = end
        mScale = scale
        mCircleView!!.invalidate()
    }

    /**
     * Sets the distance that the refresh indicator can be pulled beyond its resting position during
     * a swipe gesture. The default is [.DEFAULT_SLINGSHOT_DISTANCE].
     *
     * @param slingshotDistance The distance in pixels that the refresh indicator can be pulled
     * beyond its resting position.
     */
    fun setSlingshotDistance(@Px slingshotDistance: Int) {
        mCustomSlingshotDistance = slingshotDistance
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    fun setSize(size: Int) {
        if (size != CircularProgressDrawable.LARGE && size != CircularProgressDrawable.DEFAULT) {
            return
        }
        val metrics: DisplayMetrics = this.loadingable.getContext().resources.displayMetrics
        mCircleDiameter =
            if (size == androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE) {
                (CIRCLE_DIAMETER_LARGE * metrics.density).toInt()
            } else {
                (CIRCLE_DIAMETER * metrics.density).toInt()
            }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView?.setImageDrawable(null)
        mProgress?.setStyle(size)
        mCircleView?.setImageDrawable(mProgress)
    }

    private fun createProgressView() {
        mCircleView = CircleImageView(this.loadingable.getContext())
        mProgress = CircularProgressDrawable(this.loadingable.getContext())
        mProgress?.setStyle(CircularProgressDrawable.DEFAULT)
        mCircleView?.setImageDrawable(mProgress)
        mCircleView?.visibility = View.GONE
        this.loadingable.addView(mCircleView!!)
    }

    fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom + ((mOriginalOffsetTop - mFrom) * interpolatedTime).toInt()
        val offset = targetTop - mCircleView!!.top
        setTargetOffsetTopAndBottom(offset)
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    fun setOnRefreshListener(listener: OnRefreshListener?) {
        mListener = listener
    }

    private fun startScaleUpAnimation(listener: Animation.AnimationListener) {
        mCircleView!!.visibility = View.VISIBLE
        mProgress!!.alpha = MAX_ALPHA
        mScaleAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(interpolatedTime)
            }
        }
        mScaleAnimation?.duration = mMediumAnimationDuration.toLong()
        mCircleView?.setAnimationListener(listener)
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(mScaleAnimation)
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener)
            } else {
                startScaleDownAnimation(mRefreshListener)
            }
        }
    }

    fun startScaleDownAnimation(listener: Animation.AnimationListener?) {
        mScaleDownAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mScaleDownAnimation?.duration = SCALE_DOWN_DURATION.toLong()
        mCircleView?.setAnimationListener(listener)
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(mScaleDownAnimation)
    }

    private fun startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress!!.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress!!.alpha, MAX_ALPHA)
    }

    private fun startAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation {
        val alpha: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mProgress?.alpha = (startingAlpha + (endingAlpha - startingAlpha) * interpolatedTime).toInt()
            }
        }
        alpha.duration = ALPHA_ANIMATION_DURATION.toLong()
        // Clear out the previous animation listeners.
        mCircleView?.setAnimationListener(null)
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(alpha)
        return alpha
    }

    private val mAnimateToCorrectPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val endTarget: Int = if (!mUsingCustomStart) {
                mSpinnerOffsetEnd - abs(mOriginalOffsetTop)
            } else {
                mSpinnerOffsetEnd
            }
            val targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mCircleView!!.top
            setTargetOffsetTopAndBottom(offset)
            mProgress!!.arrowScale = 1 - interpolatedTime
        }
    }

    private fun animateOffsetToCorrectPosition(from: Int, listener: Animation.AnimationListener) {
        mFrom = from
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = ANIMATE_TO_TRIGGER_DURATION.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        mCircleView?.setAnimationListener(listener)
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(mAnimateToCorrectPosition)
    }

    private fun ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (i in 0 until this.loadingable.getChildCount()) {
                val child: View = this.loadingable.getChildAt(i)!!
                if (child != mCircleView) {
                    mTarget = child
                    break
                }
            }
        }
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    fun setDistanceToTriggerSync(distance: Int) {
        mTotalDragDistance = distance.toFloat()
    }

    private fun isAnimationRunning(animation: Animation?): Boolean {
        return animation != null && animation.hasStarted() && !animation.hasEnded()
    }

    private fun moveSpinner(overscrollTop: Float) {
        mProgress!!.arrowEnabled = true
        val originalDragPercent = overscrollTop / mTotalDragDistance
        val dragPercent = min(1f, abs(originalDragPercent))
        val adjustedPercent = (dragPercent - .4).coerceAtLeast(0.0).toFloat() * 5 / 3
        val extraOS = abs(overscrollTop) - mTotalDragDistance
        val slingshotDist = if (mCustomSlingshotDistance > 0) mCustomSlingshotDistance.toFloat() else (if (mUsingCustomStart) mSpinnerOffsetEnd - mOriginalOffsetTop else mSpinnerOffsetEnd).toFloat()
        val tensionSlingshotPercent = 0f.coerceAtLeast(extraOS.coerceAtMost(slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - (tensionSlingshotPercent / 4).toDouble().pow(2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2
        val targetY = mOriginalOffsetTop + (slingshotDist * dragPercent + extraMove).toInt()
        // where 1.0f is a full circle
        if (mCircleView!!.visibility != View.VISIBLE) {
            mCircleView!!.visibility = View.VISIBLE
        }
        if (!mScale) {
            mCircleView!!.scaleX = 1f
            mCircleView!!.scaleY = 1f
        }
        if (mScale) {
            setAnimationProgress(1f.coerceAtMost(overscrollTop / mTotalDragDistance))
        }
        if (overscrollTop < mTotalDragDistance) {
            if (mProgress!!.alpha > STARTING_PROGRESS_ALPHA
                && !isAnimationRunning(mAlphaStartAnimation)
            ) {
                // Animate the alpha
                startProgressAlphaStartAnimation()
            }
        } else {
            if (mProgress!!.alpha < MAX_ALPHA && !isAnimationRunning(
                    mAlphaMaxAnimation
                )
            ) {
                // Animate the alpha
                startProgressAlphaMaxAnimation()
            }
        }
        val strokeStart = adjustedPercent * .8f
        mProgress!!.setStartEndTrim(
            0f,
            MAX_PROGRESS_ANGLE.coerceAtMost(strokeStart)
        )
        mProgress!!.arrowScale = 1f.coerceAtMost(adjustedPercent)
        val rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f
        mProgress!!.progressRotation = rotation
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop)
    }

    private fun finishSpinner(overscrollTop: Float) {
        if (overscrollTop > mTotalDragDistance) {
            setRefreshing(true, true /* notify */)
        } else {
            // cancel refresh
            mRefreshing = false
            mProgress!!.setStartEndTrim(0f, 0f)
            var listener: Animation.AnimationListener? = null
            if (!mScale) {
                listener = object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        if (!mScale) {
                            startScaleDownAnimation(null)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                }
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener)
            mProgress!!.arrowEnabled = false
        }
    }

    private fun animateOffsetToStartPosition(from: Int, listener: Animation.AnimationListener?) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener)
        } else {
            mFrom = from
            mAnimateToStartPosition.reset()
            mAnimateToStartPosition.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mCircleView!!.setAnimationListener(listener)
            }
            mCircleView!!.clearAnimation()
            mCircleView!!.startAnimation(mAnimateToStartPosition)
        }
    }

    private val mAnimateToStartPosition: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    private fun startScaleDownReturnToStartAnimation(
        from: Int,
        listener: Animation.AnimationListener?
    ) {
        mFrom = from
        mStartingScale = mCircleView!!.scaleX
        mScaleDownToStartAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val targetScale = mStartingScale + -mStartingScale * interpolatedTime
                setAnimationProgress(targetScale)
                moveToStart(interpolatedTime)
            }
        }
        mScaleDownToStartAnimation?.duration = SCALE_DOWN_DURATION.toLong()
        if (listener != null) {
            mCircleView!!.setAnimationListener(listener)
        }
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleDownToStartAnimation)
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }


    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds
     */
    fun setColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        val context: Context = this.loadingable.getContext()
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i])
        }
        setColorSchemeColors(*colorRes)
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    fun setColorSchemeColors(@ColorInt vararg colors: Int) {
        ensureTarget()
        mProgress!!.setColorSchemeColors(*colors)
    }
}