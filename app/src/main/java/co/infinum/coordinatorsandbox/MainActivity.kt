package co.infinum.coordinatorsandbox

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mcxiaoke.koi.ext.dpToPx
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Glide.with(this).load(R.drawable.selfie).apply(RequestOptions.circleCropTransform()).into(avatarView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MockAdapter()

        val imageViewLp = avatarView.layoutParams as CoordinatorLayout.LayoutParams
        imageViewLp.behavior = AvatarBehavior()

        val nameViewLp = nameView.layoutParams as CoordinatorLayout.LayoutParams
        nameViewLp.behavior = NameBehavior()

        val recyclerViewLp = recyclerView.layoutParams as CoordinatorLayout.LayoutParams
        recyclerViewLp.behavior = RecyclerBehavior(this)

        toolbar.y = 200.dpToPx().toFloat()
    }
}