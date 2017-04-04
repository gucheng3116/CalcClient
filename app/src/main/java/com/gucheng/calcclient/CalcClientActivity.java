package com.gucheng.calcclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class CalcClientActivity extends AppCompatActivity {
    private static final String TAG = "IPC_Client";
    private CalcMethods mCalcMethods = null;
    private boolean mBound = false;
    long numOne , numTwo, numResult;
    EditText addOne;
    EditText addTwo;
    TextView resultView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_client);
        addOne = (EditText)findViewById(R.id.addOne);
        addTwo = (EditText)findViewById(R.id.addTwo);
        Button  calcStart = (Button)findViewById(R.id.start_calc);
        resultView = (TextView)findViewById(R.id.result);
        calcStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String one = addOne.getText().toString();
                numOne = Long.valueOf(one);
                String Two = addTwo.getText().toString();
                numTwo = Long.valueOf(Two);
                try {
                    Log.d(TAG, "mCalcMethods.addFunc");
                    mCalcMethods.addFunc(numOne, numTwo);
                    numResult = mCalcMethods.addGetResult();
                    resultView.setText("GetResult is " + numResult);
                } catch (RemoteException e) {
                    Log.d(TAG, "mCalcMethods.addFunc exception");
                    e.printStackTrace();
                }

            }
        });


    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "service Connected");
            mBound = true;
            mCalcMethods = CalcMethods.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            Intent intent = new Intent();
            intent.setAction("com.gucheng.aidl");
            intent.setPackage("com.gucheng.calcserver");
            boolean isSuccess = bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, " connection is " + isSuccess);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }
}
