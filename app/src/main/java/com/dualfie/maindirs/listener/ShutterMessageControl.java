package com.dualfie.maindirs.listener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.cameraview.view.camera.CameraView;
import com.dualfie.maindirs.R;
import com.dualfie.maindirs.dconstants.Commands;
import com.dualfie.maindirs.dconstants.Constants;
import com.dualfie.maindirs.helpers.JSONUtil;
import com.dualfie.maindirs.model.MessageFormat;
import com.dualfie.maindirs.network.BluetoothComm;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ShutterMessageControl {
    private static final String TAG = "ShutterMessageControl";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private Handler mHandler;
    private BluetoothComm bluetoothMessageController;
    private ArrayList<MessageFormat> mChatMessages;
    private String mUser;
    private Bitmap mImage;
    private BluetoothSocket mSocket = null;
    private static final String DESTINATION = "yyyy-MM-dd HH:mm:ss";
    private static final String PHOTO_EXT = ".jpg";
    private static final String VIDEO_EXT = ".mp4";

    protected void create(Context context ) {
        mChatMessages = new ArrayList<MessageFormat>();
        mHandler = new Handler(new Handler.Callback() {
            JSONObject messageJSON = null;
            MessageFormat message2 = null;
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case Constants.STATE_CONNECTED:
                                Toast.makeText(context, "Connected to " + mDevice.getName(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case Constants.STATE_CONNECTING:
                                Toast.makeText(context, "Connecting ",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case Constants.STATE_LISTEN:
                            case Constants.STATE_NONE:
                                Toast.makeText(context, "Waiting for connection from control ! ",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + msg.arg1);
                        }
                        break;
                    case Constants.MESSAGE_WRITE:
                        // Write acknowledge messages here
                        break;
                    case Constants.MESSAGE_READ:

                        String readMessage = new String(msg.obj.toString());
                        try {
                            messageJSON = new JSONObject(readMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            message2 = new MessageFormat(messageJSON.get("message").toString(),
                                                             messageJSON.get("from").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        switch (message2.getMessageText()) {
                            case "CAPTURE":
                                lcam.setImageConfirmationEnabled(false);
                                File fl = new File(context.getCacheDir(), DateFormat.format(DESTINATION, new Date()) + PHOTO_EXT);
                                lcam.takePicture(fl);
 //                               lcam.getHandler().obtainMessage().sendToTarget();
 //                               lcam.confirmPicture();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + msg.arg1);
                        }

                        break;
                    case Constants.MESSAGE_DEVICE_OBJECT:
                        mDevice = msg.getData().getParcelable(Constants.DEVICE_OBJECT);
                        Toast.makeText(context, "Connected to " + mDevice.getName(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_TOAST:
                        Toast.makeText(context, msg.getData().getString("toast"),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_LOST:
                        BluetoothComm.sleep(500);
                        Toast.makeText(context, "Reconnected", Toast.LENGTH_SHORT).show();
                        bluetoothMessageController.connect(mDevice);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
                return false;
            }
        });

    }

    private CameraView lcam;
    public void start(Context context, CameraView mCamera) {
        lcam = mCamera;
        create(context);
        bluetoothMessageController = new BluetoothComm(context,  mHandler);
        bluetoothMessageController.start();
    }

    public void Resume() {
        if (bluetoothMessageController != null) {
            if (bluetoothMessageController.getState() == Constants.STATE_NONE) bluetoothMessageController.start();
        }
    }
    public void Destroy() {
        if (bluetoothMessageController != null)
            bluetoothMessageController.stop();
    }

    public static final int SEND_IMAGE = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(int img, String image, Context context) throws IOException {
        if (bluetoothMessageController.getState() != Constants.STATE_CONNECTED) {
            Toast.makeText( context, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

       if (image.length() > 0) {
            if ( img == SEND_IMAGE) {
                new FiletoString() {
                    @Override
                    protected void onPostExecute(String buf) {
                        String send = JSONUtil.makeJSON("Image", "Image", buf);
                        bluetoothMessageController.write(send.getBytes());
                    }
                }.execute(image);
            }
            else {
                byte[] send = JSONUtil.makeJSON("message", "", image).getBytes();
                bluetoothMessageController.write(send);
            }
        }
    }

        private static class FiletoString extends AsyncTask<String, Integer, String> {
            @Override
            protected String doInBackground(String... params) {
                try {
                    return readFile(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return null;
            }

            private static String readFile(String filename) throws IOException {
                File f = new File(filename);
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                byte[] img = bytes.toByteArray();
                String encodedImage = Base64.encodeToString(img, Base64.URL_SAFE);
                bytes.flush();
                bytes.close();
                return encodedImage;
            }
    }



}
