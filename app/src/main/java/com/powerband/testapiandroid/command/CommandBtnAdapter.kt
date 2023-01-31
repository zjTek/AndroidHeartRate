package com.powerband.testapiandroid.command

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.powerband.testapiandroid.R

class CommandBtnAdapter(
    private val btnList: List<String>,
    private var block: ((position: Int, title: String) -> Unit)
) : RecyclerView.Adapter<CommandBtnAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mBtn = view.findViewById<Button>(R.id.button2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.btn_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mBtn.setOnClickListener {
            block.invoke(position, btnList[position])
        }
        holder.mBtn.text = btnList[position]
    }

    override fun getItemCount(): Int {
        return btnList.size
    }

}