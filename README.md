# APIHeartRate
Android Bluetooth Low Energy


## Usage

- #### 初始化
    
        FBKApiHeartRate.initConfig(application)


- #### 传入callback

        FBKApiHeartRate.setBleCallBack(object : FBKBleCallBack {...})
                       .setBleCallBack(object : FBKBasicInfoCallBack{..})
                       .setHeartRateCallBack(javaClass.name,object : FBKHearRateCallBack{..})
        callback 可以单独设置
        FBKBleCallBack 与 FBKBasicInfoCallBack 只能设置一次，多次设置会覆盖
        FBKHearRateCallBack 可以设置多次，退出相关页面后，记得调用removeHeartRateCallBack

- #### 接口列表
   
#### 接口列表
| 功能名称        | 方法    |  支持状态  |固件支持版本|
| --------          | :-----:   | :----: | :----:|
| 心率值连接后自动返回        | 需要注册setHeartRateCallBack                    |      ✅    |>= v1.1|
| 扫描设备        | startScan(timeOut: Long? = 5000)                     |      ✅    |>= v1.1|
| 停止扫描         |   stopScan()                                       |     ✅  | >= v1.1|
| 获取已连接设备         |   getConnectedDevice(uuid:[UUID])                    |     ✅  | >= v1.1|
| 通过device对象连接设备 |   connectBluetooth(bluetoothDevice: BluetoothDevice?)        |     ✅  | >= v1.1|
| 通过macAdrres连接设备   |   connectBluetooth(macAddress: String?)                  |     ✅  | >= v1.1|
| 断开设备             |   disconnectBle()                                      |     ✅  | >= v1.1|
| 注册事件监听          |   registerBleListenerReceiver()                       |   ✅    |>= v1.1|
| 取消事件监听          |   unregisterBleListenerReceiver()                     |     ✅  | >= v1.1|
| 获取设备电量          |   readDeviceBatteryPower()                            |     ✅  | >= v1.1|
| 获取厂商信息          |  readManufacturerName()                               |     ✅  | >= v1.1|
| 获取ModelNum         |   readModelString()                                    |     ✅  | >= v1.1|
| 获取硬件版本          |  readHardwareVersion()                                   |     ✅  | >= v1.1|
| 获取软件版本          |   readSoftwareVersion()                                   |     ✅  | >= v1.1|
| 获取固件版本          |   readFirmwareVersion()                                    |     ✅  | >= v1.1|
| 获取系统ID           |   readSystemId()                                    |     ✅  | >= v1.1|
| 设置心率最大阈值       |   setDeviceThreshold(max: Int)                         |     ✅  | >= v1.1|
| 获取序列号            |   getDeviceSerial()                                      |     ❌  | ---|
| 获取步频              |   getDeviceStepFrequency()                              |     ❌  | ---|
| 获取实时血氧           |   getRealTimeOxygen()                                     |     ❌  |---|
| 同步时间              |   syncTime()                                              |     ✅  | >= v1.1|
| 主动读特征            |   readCharacteristicValue(characteristicUuid: String)                         |     ✅  | >= v1.1|
| 主动设置特征监听      |   setCharacteristicNotification(characteristicUuid: String, enabled: Boolean)           |     ✅  | >= v1.1|
| 主动写特征           |   writeToBle(characteristicUuid: String, cmd: ByteArray?)                  |     ✅  | >= v1.1|
| 获取历史数据           |   ------                                    |     ❌  | ---|
| <font color="red">新增部分 </font>   |       |      |  
| 设置心率区间         |  setDeviceThreshold(min: UInt8, max: UInt8) |     ✅ | >= v1.1|
| ota接口         |  startOTA(data: ByteArray?) 状态监听接口(原BleCallBack中)：bleOtaStauts(status: OtaStatus, progress: Float)；bleOtaError(error: OtaError)|     ✅ | >= v1.1|
| 接收手环按钮切换值         | armBandPlayStatusChange（被动） |     ✅ | >= v1.1|
| 长按5s接收解绑指令         | armBandUnbind（被动） |     ✅ | >= v1.2|
| 恢复出厂设置         | resetBand() |     ❌ | --- |
| 获取设备充电状态         | queryBatteryStatus() |     ❌ | --- |
| 设备充电状态回调         | batteryStatus() |     ❌ | --- |

- #### 基本接口

    ``` kotlin
    /**
     * 扫描设备
     * @param timeOut 超时时间 默认5秒
     */
    fun startScan(timeOut: Long? = 5000)

    /**
     * 停止扫描
     */
    fun stopScan()

    /**
     * 连接蓝牙设备
     * @param bluetoothDevice 原生蓝牙对象
     */
    fun connectBluetooth(bluetoothDevice: BluetoothDevice?)

    /**
     * 通过蓝牙地址连接
     * @param macAddress 蓝牙地址 xx:xx:xx:xx:xx:xx
     */
    fun connectBluetooth(macAddress: String?)

    /**
     * 断开连接
     */
    fun disconnectBle()

    /**
     * 注册蓝牙广播监听
     * STATE_CHANGED
       ACL_CONNECTED
       ACL_DISCONNECTED
     */
    fun registerBleListenerReceiver()

    /**
     * 注销广播监听
     */
    fun unregisterBleListenerReceiver()

    /**
     * 读特征值
     * @param characteristicUuid 特征值UUID
     */
    fun readCharacteristicValue(characteristicUuid: String)

    /**
     * 建立特征监听
     * @param characteristicUuid 特征值UUID 需要支持notify
     */
    fun setCharacteristicNotification(characteristicUuid: String, enabled: Boolean)

    /**
     * 写特征值
     * @param characteristicUuid 支持写属性的特征UUID
     * @param cmd 需要写的数据
     */
    fun writeToBle(characteristicUuid: String, cmd: ByteArray?)

    /**
     * 读取设备电量
     */
    fun readDeviceBatteryPower()

    /**
     * 读取设备固件版本
     */
    fun readFirmwareVersion()

    /**
     * 读取硬件版本
     */
    fun readHardwareVersion()

    /**
     * 读取软件版本
     */
    fun readSoftwareVersion()

    /**
     * 读取系统ID
     */
    fun readSystemId()

    /**
     * 读取Model
     */
    fun readModelString()

    /**
     * 读取厂商名称
     */
    fun readManufacturerName()

    /**
     * 读取设备序列号
     */
    fun getDeviceSerial()

    /**
     * 获取实时步频
     */
    fun getDeviceStepFrequency()

    /**
     * 获取实时血氧
     */
    fun getRealTimeOxygen()

    /**
     * 同步设备时间
     */
    fun syncTime()

    /**
     * 设置心跳阈值
     */
    fun setDeviceThreshold(max: Int)
    ```

- #### FBKBleCallBack 蓝牙连接相关回调
    ```kotlin
    /**
     * 设备搜索完成回调
     */
    fun onFinishDiscovery()

    /**
     * 搜索到设备的回调，会有多次
     * @param result 搜索到的设备
     */
    fun onDiscoveryDevice(result: FBKBleDevice)

    /**
     * 设备连接相关错误
     * @param errorStr 错误
     * @param baseMethod 接口实例对象， 可从中获取bluetooth对象
     */
    fun bleConnectError(errorStr: String?, baseMethod: FBKApiBaseMethod?)

    /**
     * 蓝牙设备连接状态
     * @param deviceStatus 设备当前状态
     * @param baseMethod 接口实例对象， 可从中获取bluetooth对象
     */
    fun bleConnectStatus(
        deviceStatus: FBKBleDeviceStatus?,
        baseMethod: FBKApiBaseMethod?
    )

    /**
     * 设备连接状态日志
     * @param logStr 日志
     * @param baseMethod 接口实例对象， 可从中获取bluetooth对象
     */
    fun bleConnectStatusLog(logStr: String?, baseMethod: FBKApiBaseMethod?)
    ```
- #### FBKBasicInfoCallBack 基本信息回调
    ```kotlin
    /**
     * 返回设备电量
     */
    fun batteryPower(value: Int, baseMethod: FBKApiBaseMethod?)


    /**
     * 返回固件版本
     */
    fun firmwareVersion(version: String?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回硬件版本
     */
    fun hardwareVersion(version: String?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回软件本
     */
    fun softwareVersion(version: String?, baseMethod: FBKApiBaseMethod?)


    /**
     * 返回系统ID
     * @param data 字节序列
     */
    fun deviceSystemID(data: ByteArray?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回Model名称
     */
    fun deviceModelString(version: String?, baseMethod: FBKApiBaseMethod?)


    /**
     * 返回厂商名称
     */
    fun deviceManufacturerName(version: String?, baseMethod: FBKApiBaseMethod?)


    /**
     * 同步设备时间成功回调
     */
    fun deviceTimeSynced()

    /**
     * 设置设备心跳阈值回调
     */
    fun deviceThresholdChanged()

    /**
     * 获取私有mac地址
     *  * 暂未实现
     */
    fun privateMacAddress(
        paramMap: Map<String?, String?>?,
        baseMethod: FBKApiBaseMethod?
    )

    /**
     * 设备基本信息
     *  * 暂未实现
     */
    fun deviceBaseInfo(
        baseInfo: FBKBleBaseInfo?,
        baseMethod: FBKApiBaseMethod?
    )

    /**
     * 返回连接信息
     * 暂未实现
     */
    fun bleConnectInfo(version: String?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回设备协议版本
     * 暂未实现
     */
    fun protocolVersion(version: String?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回实时步频
     * 暂未实现
     */
    fun deviceStepFrequency(id: Int?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回实时血氧
     * 暂未实现
     */
    fun deviceOxygen(id: Int?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回私有版本
     * 暂未实现
     */
    fun privateVersion(paramMap: Map<String?, String?>?, baseMethod: FBKApiBaseMethod?)

    /**
     * 返回设备序列号
     * 暂未实现
     */
    fun deviceSerialNumber(version: String?, baseMethod: FBKApiBaseMethod?)
    ```
- #### FBKHearRateCallBack 心率数据回调
    ```kotlin
    /**
     * 返回实时心率
     * 首次数据为连接设备10s后发出，后续心跳值一秒返回一次
     * @param rate 整型心跳数据
     */
    fun deviceHeartRate(rate: Int, baseMethod: FBKApiBaseMethod?)
    ```

## License

	   Copyright 2023 tek

	   Licensed under the Apache License, Version 2.0 (the "License");
	   you may not use this file except in compliance with the License.
	   You may obtain a copy of the License at

   		   http://www.apache.org/licenses/LICENSE-2.0

	   Unless required by applicable law or agreed to in writing, software
	   distributed under the License is distributed on an "AS IS" BASIS,
	   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	   See the License for the specific language governing permissions and
	   limitations under the License.




