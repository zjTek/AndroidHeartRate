package com.powerband.testapiandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.powerband.apiheartrate.base.FBKApiBaseMethod
import com.powerband.apiheartrate.base.FBKBasicInfoCallBack
import com.powerband.apiheartrate.base.FBKBleBaseInfo
import com.powerband.apiheartrate.heartrate.FBKApiHeartRate
import com.powerband.apiheartrate.heartrate.FBKHearRateCallBack
import com.powerband.testapiandroid.command.CommandBtnAdapter
import com.powerband.testapiandroid.databinding.ActivityCommandDemoBinding

class CommandDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommandDemoBinding
    lateinit var adapter: CommandBtnAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommandDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSDK()
        initAdapter()
    }

    private fun initAdapter() {
        adapter = CommandBtnAdapter(
            arrayListOf(
                "获取电量",
                "Manufacture",
                "Model",
                "Hardware",
                "Firmware",
                "Software",
                "SysId",
                "同步时间",
                "序列号",
                "实时步频",
                "实时血氧"
            )
        ) { index, name ->
            handleEvent(index, name)
        }
        binding.btnListView.layoutManager = GridLayoutManager(this, 3)
        binding.btnListView.adapter = adapter
        binding.logView.movementMethod = ScrollingMovementMethod.getInstance()
        binding.sendThreshold.setOnClickListener {
            setThreshold()
        }
    }

    private fun handleEvent(index: Int, str: String) {
        if (index < 8) logInfo("开始 $str:")
        when (index) {
            0 -> FBKApiHeartRate.readDeviceBatteryPower()
            1 -> FBKApiHeartRate.readManufacturerName()
            2 -> FBKApiHeartRate.readModelString()
            3 -> FBKApiHeartRate.readHardwareVersion()
            4 -> FBKApiHeartRate.readFirmwareVersion()
            5 -> FBKApiHeartRate.readSoftwareVersion()
            6 -> FBKApiHeartRate.readSystemId()
            7 -> FBKApiHeartRate.syncTime()
            else -> {
                Toast.makeText(this,"功能开发中",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setThreshold() {
        val valueStr = binding.thresholdTxtView.text.toString()
        if (valueStr.isNotEmpty()) {
            val valueInt = valueStr.toInt()
            logInfo("设置心跳阈值 $valueStr:")
            FBKApiHeartRate.setDeviceThreshold(valueInt)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun logInfo(str: String) {
        runOnUiThread{
            binding.logView.apply {
                append("$str \n")
                val offset = lineCount * lineHeight
                if (offset > height) {
                    scrollTo(0,offset - height -20)
                }
            }
        }
    }

    private fun initSDK(){
        FBKApiHeartRate.setBasicInfoCallBack(object : FBKBasicInfoCallBack {
            override fun batteryPower(value: Int, baseMethod: FBKApiBaseMethod?) {
                logInfo("电量为：$value")
            }

            override fun protocolVersion(version: String?, baseMethod: FBKApiBaseMethod?) {
                
            }

            override fun firmwareVersion(version: String?, baseMethod: FBKApiBaseMethod?) {
                logInfo("固件为：$version")
            }

            override fun hardwareVersion(version: String?, baseMethod: FBKApiBaseMethod?) {
                logInfo("硬件为：$version")
            }

            override fun softwareVersion(version: String?, baseMethod: FBKApiBaseMethod?) {
                logInfo("软件为：$version")
            }

            override fun privateVersion(
                paramMap: Map<String?, String?>?,
                baseMethod: FBKApiBaseMethod?
            ) {}

            override fun privateMacAddress(
                paramMap: Map<String?, String?>?,
                baseMethod: FBKApiBaseMethod?
            ) {
                
            }

            override fun bleConnectInfo(version: String?, baseMethod: FBKApiBaseMethod?) {
                
            }

            override fun deviceSystemID(data: ByteArray?, baseMethod: FBKApiBaseMethod?) {
                logInfo("systemID：${data}")
            }

            override fun deviceModelString(version: String?, baseMethod: FBKApiBaseMethod?) {
                logInfo("Model：$version")
            }

            override fun deviceSerialNumber(version: String?, baseMethod: FBKApiBaseMethod?) {
                
            }

            override fun deviceManufacturerName(version: String?, baseMethod: FBKApiBaseMethod?) {
                logInfo("manufacture：$version")
            }

            override fun deviceBaseInfo(baseInfo: FBKBleBaseInfo?, baseMethod: FBKApiBaseMethod?) {
                
            }

            override fun deviceTimeSynced() {
                logInfo("时间已同步")
            }

            override fun deviceThresholdChanged() {
                logInfo("阈值已更新")
            }

            override fun deviceOxygen(id: Int?, baseMethod: FBKApiBaseMethod?) {

            }

            override fun deviceStepFrequency(id: Int?, baseMethod: FBKApiBaseMethod?) {
            }

        }).setHeartRateCallBack(javaClass.name,object : FBKHearRateCallBack{
            override fun deviceHeartRate(rate: Int, baseMethod: FBKApiBaseMethod?) {
                logInfo("收到心率值$rate")
            }
        })
    }

    override fun onDestroy() {
        FBKApiHeartRate.removeHeartRateCallBack(javaClass.name)
        super.onDestroy()
    }
}