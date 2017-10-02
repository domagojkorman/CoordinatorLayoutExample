package co.infinum.coordinatorsandbox

import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.widget.ImageView
import com.mcxiaoke.koi.ext.dpToPx
import com.mcxiaoke.koi.ext.toRadians


class AvatarBehavior : CoordinatorLayout.Behavior<ImageView>() {

    val minimumToolbarHeight = 56.dpToPx()
    var maximumToolbarHeight = 180.dpToPx()
    val minimumImageSize = 32.dpToPx()
    val maximumImageSize = 120.dpToPx()

    val endX = 16.dpToPx()
    val endY = 12.dpToPx()
    val startY = 140.dpToPx()

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: ImageView?, dependency: View?) = R.id.toolbar == dependency?.id

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: ImageView?, dependency: View?): Boolean {
        if (parent == null || child == null || dependency == null) {
            return onDependentViewChanged(parent, child, dependency)
        }

        val translationPercentage = calculatePercentage(dependency.height)
        val curvePercentageY = 1 - calculateNewY(translationPercentage)
        val curvePercentageX = calculateNewX(translationPercentage)

        val centerX = parent.width / 2 - 60.dpToPx()
        val newX = centerX - curvePercentageX * (centerX - endX)
        val newY = startY - curvePercentageY * (startY - endY)

        val newSize = calculateNewSize(translationPercentage)
        val lp = child.layoutParams
        lp.height = newSize
        lp.width = newSize

        child.layoutParams = lp
        child.y = newY.toFloat()
        child.x = newX.toFloat()
        return true
    }

    private fun calculateNewSize(translationPercentage: Float) = ((maximumImageSize - minimumImageSize) * translationPercentage + minimumImageSize).toInt()

    private fun calculateNewY(translationPercentage: Float) = Math.sin(translationPercentage.toAngle().toRadians())

    private fun calculateNewX(translationPercentage: Float) = Math.cos(translationPercentage.toAngle().toRadians())

    private fun Float.toAngle(): Double = (90 * this).toDouble()

    private fun calculatePercentage(toolbarHeight: Int): Float {
        return (toolbarHeight.toFloat() - minimumToolbarHeight) / (maximumToolbarHeight - minimumToolbarHeight)
    }
}