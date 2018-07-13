package com.zpone.zxing.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zpone.R;
import com.zpone.bluetooth.MyBluetoothManager;
import com.zpone.ui.fragments.BaseFragment;
import com.zpone.ui.fragments.ConnectedDeviceInfoFragment;
import com.zpone.ui.fragments.ConnectedFragment;
import com.zpone.zxing.camera.CameraManager;
import com.zpone.zxing.decoding.CaptureActivityHandler;
import com.zpone.zxing.decoding.DecodeHandlerInterface;
import com.zpone.zxing.decoding.InactivityTimer;
import com.zpone.zxing.view.ViewfinderView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang & PengJian.Wu
 */
public class CaptureFragment extends BaseFragment implements Callback,
        DecodeHandlerInterface {
    public static CaptureFragment newInstance() {
        return new CaptureFragment();
    }

    public static final String SCAN_RESULT_ACTION = "com.zxing.fragment.ACTION_SCAN_RESULT";
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private ImageButton cancelScanButton;
    private View view;
    private ProgressDialog progressDialog = null;

    /**
     * Called when the fragment is first created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.camera_fragment, null);
        // ViewUtil.addTopView(getApplicationContext(), this,
        // R.string.scan_card);
        CameraManager.init(getActivity());
        viewfinderView = (ViewfinderView) view
                .findViewById(R.id.viewfinder_view);
        cancelScanButton = (ImageButton) view.findViewById(R.id.btn_cancel_scan);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) view
                .findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        // quit the scan view
        cancelScanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME
        if (resultString.equals("")) {

            Toast.makeText(getActivity(), "Scan failed!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getActivity(), "Scan Result:" + resultString, Toast.LENGTH_SHORT)
                    .show();
            final String bt_mac = resultString;
            BluetoothAdapter bluetoothAdapter = MyBluetoothManager.getInstance().getmBluetoothAdapter();
            if (bluetoothAdapter != null && BluetoothAdapter.checkBluetoothAddress(bt_mac)) {
                final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bt_mac);
                if (device != null) {
                    //在子线程中连接蓝牙设备
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.setMessage("Connecting:" + device.getName());
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //MainService.getService().connectBLEDevice(device);
                            Looper.prepare();
                            BleManager.getInstance().connect(bt_mac, new MyBleGattCallback());
                            Looper.loop();
                        }
                    }).start();
                }
            } else {
                Toast.makeText(getActivity(), R.string.invalid_mac_adress, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(
                    Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * you should get result like this.
     * <p>
     * String scanResult = data.getExtras().getString("result");
     */
    @Override
    public void resturnScanResult(int resultCode, Intent data) {

//		Toast.makeText(getActivity(), data.getExtras().getString("result"), 0)
//				.show();
    }

    @Override
    public void launchProductQuary(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(intent);
    }

    @Override
    public void onBluetoothConnect(BluetoothDevice device) {
//        super.onBluetoothConnect(device);
        Toast.makeText(getActivity(), "连接成功！", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().postSticky(device);
        start(ConnectedFragment.newInstance());
    }

    @Override
    public void onBluetoothDisconnect() {
        Toast.makeText(getActivity(), "连接失败请重试！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothConnecting() {
        Toast.makeText(getActivity(), "正在连接...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBluetoothDsiconnecting() {
//        super.onBluetoothDsiconnecting();
        Toast.makeText(getActivity(), "正在断开连接", Toast.LENGTH_LONG).show();

    }

    /**
     * 连接回调
     */
    class MyBleGattCallback extends BleGattCallback {

        @Override
        public void onStartConnect() {
            Toast.makeText(getActivity(), "正在连接！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException e) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "连接失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            start(ConnectedDeviceInfoFragment.newInstance());
            Toast.makeText(getActivity(), "连接成功：！" + bleDevice.getName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisConnected(boolean b, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "连接断开！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
