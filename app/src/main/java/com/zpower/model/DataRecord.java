package com.zpower.model;

import com.zpower.utils.BaseUtils;

/**
 * Created by user on 2017/8/30.
 * Power by cly
 * buffer[0],beffer[1];//前两个字节是flag
 * buffer[2],beffer[3];//第三和第四个字节是Instantaneous Cadence，瞬时踏频
 * buffer[4],buffer[5];//第五和第六个字节是Average Cadence,平均踏频
 * buffer[6],buffer[7];//第七和第八个字节是Instantaneous Power,瞬时功率
 * buffer[8],buffer[9];//第九和第十个字节是Average Power，平均功率
 */

public class DataRecord {
    private byte[] flag=new byte[2];
    private byte[] ins_cadence=new byte[2];
    private byte[] avg_cadence=new byte[2];
    private byte[] ins_power=new byte[2];
    private byte[] avg_power=new byte[2];

    public DataRecord(byte[] buffer) {
        if(buffer!=null&&buffer.length==10){
            System.arraycopy(buffer, 0, flag, 0, 2);
            System.arraycopy(buffer, 2, ins_cadence, 0, 2);
            System.arraycopy(buffer, 4, avg_cadence, 0, 2);
            System.arraycopy(buffer, 6, ins_power, 0, 2);
            System.arraycopy(buffer, 8, avg_power, 0, 2);
        }
    }

    /**
     *
     * @return  返回flag
     */
    public byte[] getFlag(){
        return flag;
    }

    /**
     *
     * @return 返回瞬时踏频
     */
    public int getInsCadence(){
        return BaseUtils.bytes2ToInt(ins_cadence,0);
    }

    /**
     *
     * @return 返回平均踏频
     */
    public int getAvgCadence(){
        return BaseUtils.bytes2ToInt(avg_cadence,0);
    }

    /**
     *
     * @return 返回瞬时功率
     */
    public int getInsPower(){
        return BaseUtils.bytes2ToInt(ins_power,0);
    }

    /**
     *
     * @return 返回平均功率
     */
    public int getAvgPower(){
        return BaseUtils.bytes2ToInt(avg_power,0);
    }
}
