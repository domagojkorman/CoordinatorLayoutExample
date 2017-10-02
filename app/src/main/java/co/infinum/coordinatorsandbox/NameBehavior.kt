package co.infinum.coordinatorsandbox

import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.widget.TextView
import com.mcxiaoke.koi.ext.dpToPx
import kotlinx.android.synthetic.main.activity_main.view.*


class NameBehavior : CoordinatorLayout.Behavior<TextView>() {

    val minimumToolbarTitleHeight = 56.dpToPx()
    val maximumToolbarTitleHeight = 80.dpToPx()

    val minimumToolbarNameHeight = 140.dpToPx()
    var maximumToolbarNameHeight = 180.dpToPx()

    val bottomPadding = 24.dpToPx()

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: TextView?, dependency: View?) = R.id.toolbar == dependency?.id

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: TextView?, dependency: View?): Boolean {
        if (parent == null || child == null || dependency == null) {
            return onDependentViewChanged(parent, child, dependency)
        }

        child.alpha = calculateNamePercentage(dependency.height)
        dependency.titleView.alpha = calculateTitlePercentage(dependency.height)
        child.y = dependency.y + dependency.height - bottomPadding - child.height
        child.x = (parent.width / 2 - child.width / 2).toFloat()
        return true
    }

    private fun calculateTitlePercentage(toolbarHeight: Int): Float {
        val realPercentage = 1 - (toolbarHeight.toFloat() - minimumToolbarTitleHeight) / (maximumToolbarTitleHeight - minimumToolbarTitleHeight)
        return if (realPercentage > 1) 1f else realPercentage
    }

    private fun calculateNamePercentage(toolbarHeight: Int): Float {
        val realPercentage = (toolbarHeight.toFloat() - minimumToolbarNameHeight) / (maximumToolbarNameHeight - minimumToolbarNameHeight)
        return if (realPercentage < 0) 0f else realPercentage
    }
}