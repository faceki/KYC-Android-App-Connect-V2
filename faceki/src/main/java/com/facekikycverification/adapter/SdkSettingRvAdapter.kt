package com.facekikycverification.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facekikycverification.R
import com.facekikycverification.model.IdsModel

class SdkSettingRvAdapter(
    var stringList: ArrayList<IdsModel>,
    var context: Context,
    var onClickListener: OnClickListener
) : RecyclerView.Adapter<SdkSettingRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.sdk_setting_rv_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ids = stringList[position]
        holder.serial.text = (position + 1).toString()
        holder.title.text = if (ids.idName.lowercase().contains("passport")) ids.idName else ids.idName + " " + ids.side
        holder.icon.setImageResource(ids.sideImageDark)

        /*if (ids.imagePath == "" || ids.uploadingStatus == "pending")
            holder.status.visibility = View.GONE
        else
            if (ids.uploadingStatus == "success")
                holder.status.setImageResource(R.drawable.success)
            else
                holder.status.setImageResource(R.drawable.declined)*/
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serial: TextView = itemView.findViewById(R.id.serial)
        val icon: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.title)
        val status: ImageView = itemView.findViewById(R.id.status)
    }

    interface OnClickListener {
        fun onCLick(strings: ArrayList<IdsModel>?, position: Int)
    }
}