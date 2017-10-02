package co.infinum.coordinatorsandbox

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.mcxiaoke.koi.ext.inflate
import com.mcxiaoke.koi.ext.onClick
import com.mcxiaoke.koi.ext.toast
import kotlinx.android.synthetic.main.mock_item.view.*


class MockAdapter : RecyclerView.Adapter<MockAdapter.ViewHolder>() {

    override fun getItemCount() = 20

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) {
            return
        }

        holder.itemView.textView.text = "Item ${position + 1}"
        holder.itemView.onClick { holder.itemView.context.toast("Item number ${position + 1} clicked") }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        if (parent == null) {
            return null
        }

        return ViewHolder(parent.context.inflate(R.layout.mock_item, parent, false))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}