

package com.example.androidlogin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BluetoothChat extends Activity {

    // 초기화 및 객체 선언 부분
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CARE_INFO = 3;

    private String mConnectedDeviceName = null;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;


    TextView tv_1, tv_2;
    int set_hour_1 = 25, set_min_1 = 0, set_hour_2 = 25, set_min_2 = 0;
    int set_hour_3 = 25, set_min_3 = 0, set_hour_4 = 25, set_min_4 = 0;

    private ImageButton btn_1, btn_2;
    private ImageButton btn_image;
    private ImageButton btn_recom;
    private ImageButton blue_scan;

    MediaPlayer mediaPlayer;

    private ImageButton btn_care;

    private String phone_number;
    private String limit_time;
    private String limittime;

    TextView text_time;

    CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_main);

//        Intent intent = getIntent();


        mediaPlayer = MediaPlayer.create(BluetoothChat.this, R.raw.dingdong);

        tv_1 = (TextView) findViewById(R.id.tv3);
        tv_2 = (TextView) findViewById(R.id.tv4);
        btn_1 = (ImageButton) findViewById(R.id.button);
        btn_2 = (ImageButton) findViewById(R.id.button2);

        btn_image = (ImageButton) findViewById(R.id.ImageButton);
        btn_recom = (ImageButton) findViewById(R.id.btn_recom);
        blue_scan = (ImageButton) findViewById(R.id.bluetooth_search);

        btn_care = (ImageButton) findViewById(R.id.btn_care);
        text_time = (TextView) findViewById(R.id.text_time);



        //설정한 시간
        limittime = limit_time;

        //예외처리
        if (phone_number == null || phone_number == "-") phone_number = "-";
        if (limit_time == null) text_time.setText("설정 필요");

        //타이머 기능
        countDownTimer = new CountDownTimer(200000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int a;
                //시간이 설정된 상태면
                if (text_time.getText() != "설정 필요") {
                    a = Integer.parseInt(limit_time);
                    Log.d("TESTTESt", String.valueOf(a));
                    text_time.setText(limit_time);

                    //블투 연결 안되있으면 타이머 작동 안함
                    if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                        Log.d("TESTTESt", "DIS");
                        a++;
                    } else {
                        Log.d("TESTTESt", "Conntetd");

                    }
                    //약통이 모두 비워져있으면 타이머 작동 안함
                    if (tv_1.getText() == "X" && tv_2.getText() == "X") a++;

                    if (a == 0) {
                        //전화번호 설정되어있는 상태면 문자 발송
                        if (phone_number != "-") sendSMS(phone_number, "사용자가 약 할당치를 복용하지 않았습니다.");
                        //타이머 끝나면 시간 초기화
                        a = Integer.parseInt(limittime) + 1;
                    }
                    a--;
                    limit_time = String.valueOf(a);
                }
            }

            @Override
            public void onFinish() {

            }
        };

        countDownTimer.start();

        //알람 기능
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();

                final int hour = cal.get(Calendar.HOUR_OF_DAY);
                final int min = cal.get(Calendar.MINUTE);
                final int sec = cal.get(Calendar.SECOND);
                BluetoothChat.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (set_hour_1 != 25) {
                            if ((hour == set_hour_1) && (min == set_min_1) && (sec == 0)) {
                                mediaPlayer.start();
                                pop("첫번째 약 먹을시간입니다.");
                            }
                        }

                        if (set_hour_2 != 25) {
                            if ((hour == set_hour_2) && (min == set_min_2) && (sec == 0)) {
                                mediaPlayer.start();
                                pop("두번째 약 먹을시간입니다.");
                            }
                        }

                        if (set_hour_3 != 25) {
                            if ((hour == set_hour_3) && (min == set_min_3) && (sec == 0)) {
                                mediaPlayer.start();
                                pop("세번째 약 먹을시간입니다.");
                            }
                        }

                        if (set_hour_4 != 25) {
                            if ((hour == set_hour_4) && (min == set_min_4) && (sec == 0)) {
                                mediaPlayer.start();
                                pop("네번째 약 먹을시간입니다.");
                            }
                        }
                    }
                });

            }
        };


        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog_1 = new TimePickerDialog(BluetoothChat.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        set_hour_1 = i;
                        set_min_1 = i1;
                        Log.e(TAG, "SET_TIME_1 = " + String.valueOf(set_hour_1) + "::" + String.valueOf(set_min_1));
                        Toast.makeText(BluetoothChat.this, "첫번째 약은  " + String.valueOf(set_hour_1) + "시" + String.valueOf(set_min_1) + "분으로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }, set_hour_1, set_min_1, true);
                timePickerDialog_1.show();
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog_1 = new TimePickerDialog(BluetoothChat.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        set_hour_2 = i;
                        set_min_2 = i1;
                        Log.e(TAG, "SET_TIME_2 = " + String.valueOf(set_hour_2) + "::" + String.valueOf(set_min_2));
                        Toast.makeText(BluetoothChat.this, "두번째 약은  " + String.valueOf(set_hour_2) + "시" + String.valueOf(set_min_2) + "분으로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }, set_hour_2, set_min_2, true);
                timePickerDialog_1.show();
            }
        });


        //보호자 설정 버튼 클릭시
        btn_care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CareActivity로 화면 전환
                Intent intent2 = new Intent(BluetoothChat.this, CareActivity.class);
                intent2.putExtra("phone_number", phone_number);
                intent2.putExtra("limit_time", limittime);
                startActivityForResult(intent2, REQUEST_CARE_INFO);
            }
        });

        // 이름으로 검색하기 위한 이미지 버튼 클릭시
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NameMainActivity로 화면 전환
                Intent intent1 = new Intent(BluetoothChat.this, NameMainActivity.class);
                intent1.putExtra("phone_number", phone_number);
                intent1.putExtra("limit_time", limittime);
                startActivity(intent1);
            }
        });


        btn_recom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FormMainActivity로 화면 전환
                Intent intent3 = new Intent(BluetoothChat.this, RecomMainActivity.class);
                startActivity(intent3);
            }
        });

        blue_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(BluetoothChat.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        timer.schedule(timerTask, 0, 1000);



    }

    //문자 발송 함수
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "문자메시지 전송 완료", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "전송실패", Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter(SENT));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

    }

    boolean flag = false;

    public void pop(String str) {

        AlertDialog.Builder alert = new AlertDialog.Builder(BluetoothChat.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flag = false;
                dialogInterface.dismiss();
            }
        });
        if (!flag) {
            alert.setMessage(str);
            AlertDialog dialog = alert.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            flag = true;
        }

    }


    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");


        // 블루투스가 활성화 되어있지않으면 활성화키 팝업
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) setupChat();
        }
    }

    // 블루투스 서비스 객체 생성
    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);

    }

    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    // 앱종료시 블루투스서비스종료
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
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
                // 전송할 데이터가 있을때
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;

                // 데이터를 받았을때
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    ble_str += readMessage;
                    String[] data = ble_str.split(",");

                    Log.e(TAG, ble_str);

                    try {
                        if (data[0].equals("y") && data[5].equals("z")) {
                            if (data[1].equals("a") || data[2].equals("c")) tv_1.setText("O");
                            if (tv_1.getText() == "O") {
                                if (data[1].equals("b") && data[2].equals("d")) {
                                    tv_1.setText("X");
                                    limit_time = String.valueOf(limittime);
                                    pop("약을 다시 채워넣어주세요.");
                                }
                            }
                            if (data[1].equals("b") && data[2].equals("d")) tv_1.setText("X");


                            if (data[3].equals("e") || data[4].equals("g")) tv_2.setText("O");
                            if (tv_2.getText() == "O") {
                                if (data[3].equals("f") && data[4].equals("h")) {
                                    tv_2.setText("X");
                                    limit_time = String.valueOf(limittime);
                                    pop("약을 다시 채워넣어주세요.");
                                }
                            }
                            if (data[3].equals("f") && data[4].equals("h")) tv_2.setText("X");


                            ble_str = "";
                        } else {
                            for (int i = 0; i < 200; i++) if (data[i].equals("z")) ble_str = "";
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            // 블루투스 디바이스 연결 요청을 받았을때 연결부분
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    mChatService.connect(device);

                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
            case REQUEST_CARE_INFO:
                if(resultCode == Activity.RESULT_OK) {
                    //CareActivity에서 저장한 값 불러옴
                    phone_number = data.getStringExtra("phone_number");
                    limit_time = data.getStringExtra("limit_time");
                    limittime = limit_time;
                    //예외처리
                    if (phone_number == null || phone_number == "-") phone_number = "-";
                    if (limit_time == null) {
                        text_time.setText("설정 필요");
                    } else {
                        text_time.setText(limit_time);
                    };

                    countDownTimer.cancel();

                    //타이머 기능
                    countDownTimer = new CountDownTimer(200000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            int a;
                            //시간이 설정된 상태면
                            if (text_time.getText() != "설정 필요") {
                                a = Integer.parseInt(limit_time);
                                text_time.setText(limit_time);

                                //블투 연결 안되있으면 타이머 작동 안함
                                if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                                    a++;
                                }                                 //약통이 모두 비워져있으면 타이머 작동 안함
                                if (tv_1.getText() == "X" && tv_2.getText() == "X") a++;

                                if (a == 0) {
                                    //전화번호 설정되어있는 상태면 문자 발송
                                    if (phone_number != "-") sendSMS(phone_number, "사용자가 약 할당치를 복용하지 않았습니다.");
                                    //타이머 끝나면 시간 초기화
                                    a = Integer.parseInt(limittime) + 1;
                                }
                                a--;
                                limit_time = String.valueOf(a);
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    };

                    countDownTimer.start();

                }
        }
    }

    // 상단 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    // 상단 메뉴 클릭시 액티비티 이동 팝업
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
        }
        return false;
    }
}

