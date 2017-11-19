package com.zpower.model;

import com.zpower.utils.BaseUtils;

/**
 * Created by user on 2017/8/30.
 * Power by cly
 * buffer[0],beffer[1];//前两个字节是flag
 * buffer[2],beffer[3];//第三和第四个字节是功率
 * buffer[4],buffer[5];//第五和第六个字节是圈数
 * buffer[6],buffer[7];//第七和第八个字节是时间
 */

public class DataRecord {
    private byte[] flag=new byte[2];
    private byte[] power=new byte[2];
    private byte[] round=new byte[2];
    private byte[] time=new byte[2];

    public DataRecord(byte[] buffer) {
        if(buffer!=null&&buffer.length==8){
            System.arraycopy(buffer, 0, flag, 0, 2);
            System.arraycopy(buffer, 2, power, 0, 2);
            System.arraycopy(buffer, 4, round, 0, 2);
            System.arraycopy(buffer, 6, time, 0, 2);
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
     * @return 返回圈数
     */
    public int getRounds(){
        return BaseUtils.bytes2ToInt(round,0);
    }

    /**
     *
     * @return 返回耗时
     */
    public int getElapsedTime(){
        return BaseUtils.bytes2ToInt(time,0);
    }

    /**
     *
     * @return 返回瞬时功率
     */
    public int getInsPower(){
        return BaseUtils.bytes2ToInt(power,0);
    }

}
