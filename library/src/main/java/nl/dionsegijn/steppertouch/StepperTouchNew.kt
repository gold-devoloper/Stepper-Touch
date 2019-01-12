package nl.dionsegijn.steppertouch

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Path
import android.graphics.RectF
import androidx.dynamicanimation.animation.SpringAnimation
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.stepper_touch.view.*

/**
 * Created by dionsegijn on 3/19/17.
 */
class StepperTouchNew : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { handleAttrs(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { handleAttrs(attrs) }

    // Drawing properties
    private val clipPath = Path()
    private var rect: RectF? = null

    // Animation properties
    private val stiffness: Float = 200f
    private val damping: Float = 0.6f
    private var startX: Float = 0f


    // Indication if tapping positive and negative sides is allowed
    private var isTapEnabled: Boolean = false
    private var isTapped: Boolean = false

    // Style properties
    private var stepperBackground = R.color.stepper_background
    private var stepperActionColor = R.color.stepper_actions
    private var stepperActionColorDisabled = R.color.stepper_actions_disabled
    private var stepperTextColor = R.color.stepper_text
    private var stepperButtonColor = R.color.stepper_button
    private var stepperTextSize = 20
    private var allowNegative = true
    private var allowPositive = true

    init {
        LayoutInflater.from(context).inflate(R.layout.stepper_touch, this, true)
        clipChildren = true

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            elevation = 4f
        }
    }

    private fun handleAttrs(attrs: AttributeSet) {
        val styles: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.StepperTouch, 0, 0)

        try {
            stepperBackground = styles.getResourceId(R.styleable.StepperTouch_stepperBackgroundColor, R.color.stepper_background)
            stepperActionColor = styles.getResourceId(R.styleable.StepperTouch_stepperActionsColor, R.color.stepper_actions)
            stepperActionColorDisabled = styles.getResourceId(R.styleable.StepperTouch_stepperActionsDisabledColor, R.color.stepper_actions_disabled)
            stepperTextColor = styles.getResourceId(R.styleable.StepperTouch_stepperTextColor, R.color.stepper_text)
            stepperButtonColor = styles.getResourceId(R.styleable.StepperTouch_stepperButtonColor, R.color.stepper_button)
            stepperTextSize = styles.getDimensionPixelSize(R.styleable.StepperTouch_stepperTextSize, stepperTextSize)
            allowNegative = styles.getBoolean(R.styleable.StepperTouch_stepperAllowNegative, true)
            allowPositive = styles.getBoolean(R.styleable.StepperTouch_stepperAllowPositive, true)
        } finally {
            styles.recycle()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isTapped) {
                    startX = event.x
                }
                startX = event.x
                parent.requestDisallowInterceptTouchEvent(true)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isTapped) {
                    viewCounter.translationX = event.x - startX
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                isTapped = false
//                if (viewCounter.translationX > viewCounter.width * 0.5 && allowPositive) viewStepper.add()
//                else if (viewStepper.translationX < -(viewStepper.width * 0.5) && allowNegative) viewStepper.subtract()

                if (viewCounter.translationX != 0f) {
                    val animX = SpringAnimation(viewCounter, SpringAnimation.TRANSLATION_X, 0f)
                    animX.spring.stiffness = stiffness
                    animX.spring.dampingRatio = damping
                    animX.start()
                }
                return true
            }
            else -> {
                parent.requestDisallowInterceptTouchEvent(false)
                return false
            }
        }
    }
}
