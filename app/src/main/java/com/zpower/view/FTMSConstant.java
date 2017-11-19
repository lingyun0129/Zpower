package com.zpower.view;

/**
 * Created by user on 2017/9/5.
 * Power by cly
 */

public class FTMSConstant {
    //follow defines the Result Code for the Fitness Machine Control Point.
    public final static int RESULT_SUCCESS=0x01;
    public final static int RESULT_OPCODE_NOT_SUPPORT=0x02;
    public final static int RESULT_INVALID_PARAMETER=0x03;
    public final static int RESULT_OPERATION_FAILED=0x04;
    public final static int RESULT_CONTROL_NOT_PERMITTED=0x05;


    //op code
    public final static int OP_REQUEST_CONTROL=0x00;
    public final static int OP_RESET=0x01;
    public final static int OP_START_RESUME=0x07;
    public final static int OP_STOP_PAUSE=0x08;
    public final static int OP_CALIBRATION=0x0c;
    public final static int OP_NEWTON=0x0a;
    public final static int RESPONSE_CODE=0x80;

    //Control Information Parameter Value for Stop or Pause Procedure
    public final static int PARAM_STOP=0x01;
    public final static int PARAM_PAUSE=0x02;
    /**
     * 功能阈值功率：代表的是你在一段时间内可以稳定输出的最高的平均功率
     */
    public final static float FTP=290.0f;//function threshold power
}
