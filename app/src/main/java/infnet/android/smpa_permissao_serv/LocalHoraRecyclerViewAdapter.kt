package infnet.android.smpa_permissao_serv

import android.location.Location
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import infnet.android.smpa_permissao_serv.placeholder.PlaceholderContent.PlaceholderItem
import infnet.android.smpa_permissao_serv.databinding.FragmentLocalhoraBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class LocalHoraRecyclerViewAdapter(
    private val values: MutableList<Location>
) : RecyclerView.Adapter<LocalHoraRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentLocalhoraBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        val latilong = item.latitude.toString()+", "+item.longitude.toString()
        holder.latiLongItem.text = latilong
        holder.dataListItem.text = SimpleDateFormat("dd.MM.yyyy HH:mm").format(
            Date(item.time)
        )
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLocalhoraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val latiLongItem: TextView = binding.latiLongItem
        val dataListItem: TextView = binding.dataListItem

        override fun toString(): String {
            return super.toString() + " '" + latiLongItem.text + "'"
        }
    }

}