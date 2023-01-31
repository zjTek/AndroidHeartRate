package com.powerband.testapiandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.powerband.apiheartrate.ble.fbkBleDevice.FBKBleDevice
import com.powerband.apiheartrate.ble.fbkBleDevice.FBKBleDeviceStatus

class BleDeviceAdapter(
    var deviceList: MutableList<FBKBleDevice>,
    var block: ((position: Int, device: FBKBleDevice) -> Unit)
) : RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameLbl = view.findViewById<TextView>(R.id.bleName)
        val macLbl = view.findViewById<TextView>(R.id.macAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.device_ble_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dev = deviceList[position]
        val nameStr = if (dev.connectStatus == FBKBleDeviceStatus.BleConnected) {
            "${dev.deviceName}(已连接)"
        } else {
            dev.deviceName
        }
        holder.nameLbl.text = nameStr
        holder.macLbl.text = dev.macAddress
        holder.itemView.setOnClickListener {
            block(position, dev)
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

}