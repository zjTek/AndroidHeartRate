package com.powerband.testapiandroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo.WindowLayout
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.powerband.apiheartrate.base.FBKApiBaseMethod
import com.powerband.apiheartrate.ble.fbkBleDevice.FBKBleDevice
import com.powerband.apiheartrate.ble.fbkBleDevice.FBKBleDeviceStatus
import com.powerband.apiheartrate.heartrate.FBKApiHeartRate
import com.powerband.apiheartrate.heartrate.FBKBleCallBack
import com.powerband.apiheartrate.heartrate.FBKHearRateCallBack
import com.powerband.testapiandroid.databinding.ActivityMainBinding
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: BleDeviceAdapter
    private var selectedIndex = -1
    private val TAG = "FBKApiHeartRate"
    private var progressHud: ProgressDialog? = null
    private var rateList: MutableList<HearRateRecord> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        initSDK()
        initClickEvent()
    }

    private fun initAdapter() {
        adapter = BleDeviceAdapter(arrayListOf()) { position, device ->
            showHUD()
            selectedIndex = position
            FBKApiHeartRate.connectBluetooth(device.bleDevice)
        }
        binding.bleListView.layoutManager = LinearLayoutManager(this)
        binding.bleListView.adapter = adapter
    }

    @SuppressLint("NewApi")
    private fun initClickEvent() {
        binding.btnScan.setOnClickListener {
            checkBlePermission()
        }
        binding.goTestPage.setOnClickListener {
            startActivity(Intent(this, CommandDemoActivity::class.java))
        }

        binding.btnDisconnect.setOnClickListener {
            FBKApiHeartRate.disconnectBle()
        }

        binding.clearRate.setOnClickListener {
            rateList.clear()
            binding.clearRate.isEnabled = false
            binding.exportExl.isEnabled = false
        }
        binding.exportExl.setOnClickListener {
            exportExcel()
        }
        binding.switch1.setOnCheckedChangeListener { _, v ->
            if (v) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
        binding.btnConnect.setOnClickListener {
            connectByMac()
        }

    }

    private fun showHUD() {
        if (progressHud == null) {
            progressHud = ProgressDialog.show(this, "请稍后", "", true, true)
        }
        progressHud?.show()
    }

    private fun connectByMac() {
        if (binding.textViewMac.text.toString().isNotEmpty()) {
            showHUD()
            FBKApiHeartRate.connectBluetooth(binding.textViewMac.text.toString())
        } else {
            Toast.makeText(this@MainActivity, "Mac 地址有误", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBlePermission() {
        var result =
            checkSelfPermission(Manifest.permission.BLUETOOTH) + checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) + checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION + Manifest.permission.READ_EXTERNAL_STORAGE
                        + Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            result += checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) + checkSelfPermission(
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            result += checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN)
        }
        if (result != PackageManager.PERMISSION_GRANTED) {
            var permissionsList = mutableListOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            )
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                permissionsList.addAll(
                    listOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                )
            } else {
                permissionsList.add(Manifest.permission.BLUETOOTH_ADMIN)
            }
            requestPermissions(permissionsList.toTypedArray(), 10086)
        } else {
            scanProcess()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10086 && grantResults.sum() == PackageManager.PERMISSION_GRANTED) {
            scanProcess()
        }
    }

    private fun scanProcess() {
        adapter.deviceList.clear()
        showHUD()
        FBKApiHeartRate.startScan()
    }

    private fun disableFunctionBtn(s: Boolean, device: FBKBleDevice) {
        binding.btnScan.isEnabled = !s
        binding.btnDisconnect.isEnabled = s
        binding.goTestPage.isEnabled = s
        binding.btnConnect.isEnabled = !s
        if (s) {
            binding.textViewMac.text = device.macAddress
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initSDK() {

        FBKApiHeartRate.initConfig(application).setBleCallBack(object : FBKBleCallBack {
            override fun onFinishDiscovery() {
                progressHud?.hide()
            }

            override fun onDiscoveryDevice(result: FBKBleDevice) {
                adapter.deviceList.add(result)
                adapter.notifyDataSetChanged()
            }

            override fun bleConnectError(errorStr: String?, baseMethod: FBKApiBaseMethod?) {
                Log.d(TAG, errorStr ?: "")
            }

            @SuppressLint("MissingPermission")
            override fun bleConnectStatus(
                deviceStatus: FBKBleDeviceStatus?,
                baseMethod: FBKApiBaseMethod?
            ) {
                runOnUiThread {
                    if (selectedIndex != -1) {
                        if (deviceStatus == FBKBleDeviceStatus.BleConnected || deviceStatus == FBKBleDeviceStatus.BleDisconnected) {
                            val device = adapter.deviceList[selectedIndex]
                            device.connectStatus =
                                deviceStatus ?: FBKBleDeviceStatus.BleDisconnected
                            adapter.notifyDataSetChanged()
                            disableFunctionBtn(
                                device.connectStatus == FBKBleDeviceStatus.BleConnected,
                                device
                            )
                            progressHud?.hide()
                        } else if (deviceStatus == FBKBleDeviceStatus.BleMacInvalid) {
                            progressHud?.hide()
                            Toast.makeText(this@MainActivity, "Mac 地址有误", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        //direct connect
                        progressHud?.hide()
                        var device = baseMethod?.bluetoothDevice
                        if (device != null) {
                            adapter.deviceList.clear()
                            adapter.deviceList.add(
                                FBKBleDevice(
                                    device,
                                    device.name,
                                    device.address,
                                    connectStatus = FBKBleDeviceStatus.BleConnected
                                )
                            )
                            disableFunctionBtn(
                                true,
                                adapter.deviceList.first()
                            )
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun bleConnectStatusLog(logStr: String?, baseMethod: FBKApiBaseMethod?) {
                logStr?.let {
                    Log.d(TAG,it)
                }
            }
        }).setHeartRateCallBack(javaClass.name, object : FBKHearRateCallBack {
            override fun deviceHeartRate(rate: Int, baseMethod: FBKApiBaseMethod?) {
                val current = LocalDateTime.now()
                rateList.add(HearRateRecord(current.toString(), rate.toString()))
                runOnUiThread {
                    if (!binding.exportExl.isEnabled) binding.exportExl.isEnabled = true
                }
            }
        })
    }

    fun exportExcel() {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet("HeartRate")
        sheet.setColumnWidth(0, 3000)
        sheet.setColumnWidth(1, 1000)
        val tempList = rateList.toList()
        tempList.forEachIndexed { index, hearRateRecord ->
            val row = sheet.createRow(index)
            val cell1 = row.createCell(0)
            val cell2 = row.createCell(1)
            cell1.setCellValue(hearRateRecord.time)
            cell2.setCellValue(hearRateRecord.rate)
        }


        val r = storeExcelInStorage(this, "HeartRate.xls", workBook)
        if (r) {
            launchShareFileIntent(FileShareUtils.accessFile(this, "HeartRate.xls"))
        }

    }

    private fun storeExcelInStorage(
        context: Context,
        fileName: String,
        workbook: HSSFWorkbook
    ): Boolean {
        var isSuccess: Boolean
        val file = File(context.getExternalFilesDir(null), fileName)
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            workbook.write(fileOutputStream)
            Log.e(TAG, "Writing file$file")
            isSuccess = true
        } catch (e: IOException) {
            Log.e(TAG, "Error writing Exception: ", e)
            isSuccess = false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save file due to Exception: ", e)
            isSuccess = false
        } finally {
            try {
                fileOutputStream?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return isSuccess
    }

    private fun launchShareFileIntent(uri: Uri) {
        val intent = ShareCompat.IntentBuilder.from(this)
            .setType("application/vnd.ms-excel")
            .setStream(uri)
            .setChooserTitle("Select application to share file")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

}