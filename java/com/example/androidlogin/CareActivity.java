package com.example.androidlogin;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class CareActivity extends Activity {
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    Button btn_save;
    static String phone_number;
    static String limit_time;
    EditText edit_number,edit_time;

    @Override
    public void onBackPressed() {
        // 버튼을 누르면 메인화면으로 이동
        Intent intent = new Intent();
        intent.putExtra("phone_number", edit_number.getText().toString()) ;
        intent.putExtra("limit_time", edit_time.getText().toString()) ;
        setResult(Activity.RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.care);

        Intent intent = getIntent();


        edit_number = (EditText) findViewById (R.id.edit_number);
        edit_time = (EditText) findViewById (R.id.edit_time);

        //메인액티비티에서 받아옴
        phone_number= intent.getStringExtra("phone_number") ;
        limit_time= intent.getStringExtra("limit_time") ;


        if(phone_number=="-") edit_number.setText("-");
        else edit_number.setText(phone_number);


        if(limit_time==null) edit_time.setText("");
        else edit_time.setText(limit_time);

        btn_save = (Button) findViewById(R.id.btn_save);

        //저장 버튼 누르면 edittext창에서 입력한 값들 BluetoothChat으로 전달
        btn_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent2 = new Intent(CareActivity.this, BluetoothChat.class);
//                intent2.putExtra("phone_number", edit_number.getText().toString()) ;
//                intent2.putExtra("limit_time", edit_time.getText().toString()) ;
//                startActivity(intent2);
                Intent intent = new Intent();
                intent.putExtra("phone_number", edit_number.getText().toString()) ;
                intent.putExtra("limit_time", edit_time.getText().toString()) ;
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });


    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);

    }

    String ble_str = "";
    // 블루투스 상태
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;

                // 데이터를 받았을때
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    ble_str += readMessage;
                    String[] data = ble_str.split(",");

                    Log.e(TAG, ble_str);
            }
        }
    };
}

