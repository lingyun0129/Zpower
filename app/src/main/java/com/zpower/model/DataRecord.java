package com.zpower.model;

import com.zpower.utils.BaseUtils;

/**
 * Created by user on 2017/8/30.
 * Power by cly
 * buffer[0];//前两个字节是flag
 * buffer[1]--beffer[4];//第二至第五个字节是Cumulative Wheel Revolutions(累计轮盘圈数)
 * buffer[5],buffer[6];//第六和第七个字节是Last Wheel Event Time
 * buffer[7],buffer[8];//第八和第九个字节是Cumulative Crank Revolutions(曲柄累计圈数)
 * buffer[9],buffer[10];//第十和第十一个字节是Last Crank Event Time
 */

public class DataRecord {
    private byte flag;
    private byte[] wheel_round=new byte[4];
    private byte[] wheel_time=new byte[2];
    private byte[] crank_round=new byte[2];
    private byte[] crank_time=new byte[2];
    public DataRecord(byte[] buffer) {
        if(buffer!=null&&buffer.length==8){
            System.arraycopy(buffer, 0, flag, 0, 0);
            System.arraycopy(buffer, 1, wheel_round, 0, 3);
            System.arraycopy(buffer, 5, wheel_time, 0, 2);
            System.arraycopy(buffer, 7, crank_round, 0, 2);
            System.arraycopy(buffer, 9, crank_time, 0, 2);
        }
    }

    /**
     *
     * @return  返回flag
     */
    public byte getFlag(){
        return flag;
    }

    /**
     *
     * @return 返回圈数
     */
    public int getRounds(){
        return BaseUtils.bytes4ToInt(wheel_round,0);
    }

    /**
     *
     * @return 返回耗时
     */
    public int getElapsedTime(){
        return BaseUtils.bytes2ToInt(wheel_time,0);
    }


}
