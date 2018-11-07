package justbucket.videolib.graphics

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator

/**
 * An animated drawable to mark selected videos
 */
class SelectorCheckDrawable : Drawable() {

    companion object {
        private const val DURATION = 200
        private val DECELERATE_INTERPOLATOR = DecelerateInterpolator()
    }

    private val paint = Paint(ANTI_ALIAS_FLAG)
    private val path = Path()
    private val pathMeasure = PathMeasure()

    private var start: Long = 0
    private var isSelected = false

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.SQUARE
        paint.strokeJoin = Paint.Join.MITER
        paint.color = Color.WHITE
    }

    override fun draw(canvas: Canvas) {
        val dt = System.currentTimeMillis() - start
        var value = when {
            start == 0L -> 1f
            dt < 0 -> 0f
            else -> Math.min(dt.toFloat() / DURATION, 1f)
        }
        value = DECELERATE_INTERPOLATOR.getInterpolation(value)
        val bounds = bounds
        canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        canvas.drawColor(Color.argb(((if (isSelected) value else 1f - value) * 0x80).toInt(), 0, 0, 0))
        val size: Int
        val width = bounds.width()
        val height = bounds.height()
        size = when {
            width > height -> {
                canvas.translate(((width - height) / 2).toFloat(), 0F)
                height
            }
            height > width -> {
                canvas.translate(0F, ((height - width) / 2).toFloat())
                width
            }
            else -> width
        }
        val strokeSize = 0.03f
        paint.strokeWidth = strokeSize * size

        path.moveTo(0.39f * size, 0.5f * size)
        path.rLineTo(0.08f * size, 0.08f * size)
        path.rLineTo(0.14f * size, -0.14f * size)
        pathMeasure.setPath(path, false)
        path.rewind()
        var length = pathMeasure.length
        if (isSelected) {
            pathMeasure.getSegment(0f, value * length, path, true)
        } else {
            pathMeasure.getSegment(value * length, length, path, true)
        }
        canvas.drawPath(path, paint)
        path.rewind()

        val append = if (isSelected) (1f - value) * 90f else value * -90f - 180f
        paint.strokeWidth = strokeSize * size
        path.arcTo(0.3f * size, 0.3f * size, 0.7f * size, 0.7f * size, 270f + append, -180f, true)
        path.arcTo(0.3f * size, 0.3f * size, 0.7f * size, 0.7f * size, 90f + append, -180f, false)
        pathMeasure.setPath(path, false)
        path.rewind()
        length = pathMeasure.length
        if (isSelected) {
            pathMeasure.getSegment(0f, value * length, path, true)
        } else {
            pathMeasure.getSegment(value * length, length, path, true)
        }
        canvas.drawPath(path, paint)
        path.rewind()

        canvas.restore()
        if (value < 1f) {
            invalidateSelf()
        }
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(cf: ColorFilter?) {}

    fun setSelected(selected: Boolean, animate: Boolean) {
        if (this.isSelected != selected) {
            start = if (animate) {
                System.currentTimeMillis()
            } else {
                0L
            }
            this.isSelected = selected
            invalidateSelf()
        }
    }
}