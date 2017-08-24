package com.zpower.model;

import java.util.UUID;

/**
 * 使用时替换下列为你对应的uuid
 * Created by qin on 2017/1/31.
 */

public interface BluetoothUUID {
    UUID SERVICE = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB");
    UUID SERVICE2 = UUID.fromString("00001801-0000-1000-8000-00805F9B34FB");
    UUID READ = UUID.fromString("0002A38-0000-1000-8000-00805F9B34FB");
    UUID WRITE_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    UUID WRITE = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    UUID DESCR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    //UUID NOTIFY_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    UUID NOTIFICATION1 = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    UUID NOTIFICATION2 = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    //电池电量
    UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    UUID BATTERY_NOTIFICATION = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");


    //Service
    UUID FITNESS_MACHINE_SERVICE=UUID.fromString("00001826-0000-1000-8000-00805F9B34FB");

    //characteristics
    //fitness_machine_feature
    UUID FINESS_MACHINE_FEATURE=UUID.fromString("00002acc-0000-1000-8000-00805F9B34FB");
    //indoor_bike_data
    UUID INDOOR_BIKE_DATA=UUID.fromString("00002ad2-0000-1000-8000-00805F9B34FB");
    //support_resistance_level_range
    UUID SUPPORT_RESISTANCE_LEVEL_RANGE=UUID.fromString("00002ad6-0000-1000-8000-00805F9B34FB");
    //fitness_machine_control_point
    UUID FITNESS_MACHINE_CONTROL_POINT=UUID.fromString("00002ad9-0000-1000-8000-00805F9B34FB");
    //fitness_machine_status
    UUID FITNESS_MACHINE_STATUS=UUID.fromString("00002ada-0000-1000-8000-00805F9B34FB");
}
