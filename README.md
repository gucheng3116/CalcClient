# CalcClient
    
&emsp;&emsp;Android中通过Binder机制实现跨进程通信，Binder机制比较典型的例子就是AIDL Service。AIDL跨进程通信的实现在Android中是比较重要的知识点，同时也有一定的难度。本文通过一个具体简单的栗子来讲述如何实现跨进程通信。

&emsp;&emsp;Android跨进程通信，由四大部分组成，分别是Client、Server、Service Manager和Binder驱动程序。其中Service Manager和Binder驱动程序系统已经为我们实现好，用户只需要按照规范实现Client和Server就可以了。

&emsp;&emsp;Client和Server在进程间通讯时分别做哪些事情？

&emsp;&emsp;Client:调用Server提供的接口函数，传递参数给Server，接收Server返回的结果。

&emsp;&emsp; Server：对接收到的Client的参数进行处理，为Client实现接口函数。

**下面我们来举一个例子：**  
&emsp;&emsp;该例子中我们一共编写两个应用程序，一个是Client端，一个是Server端。这两个应用程序实现一个加法运算，在Client端传递两个数给Server端，Server端对这两个数进行相加，并将结果返回给Client端。
  <center>
<img src="http://img.blog.csdn.net/20170404221143707?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ3VjaGVuZzMxMTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast" height="40%" width="40%" alt=""/></center>

   Server端界面如下：    
&emsp;&emsp;![这里写图片描述](http://img.blog.csdn.net/20170404221408757?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ3VjaGVuZzMxMTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

&emsp;&emsp;Server端实现了计算Client端传递参数的接口，以及Client端获取计算结果的接口。同时，Server端也将结果显示到界面上，这样就可以直观地看到两个应用程序有相互影响，而不是说Client端自己计算了结果。  

**代码实现步骤如下：**

**1.编写.aidl文件CalcMethods.aidl：**
这里设计了以下两个接口函数：
    &emsp;&emsp;addFunc：传递两个参数给Server端；
   &emsp;&emsp; addGetResult：获取Server端计算结果。
```
  // CalcMethods.aidl
package com.gucheng.calcclient;

// Declare any non-default types here with import statements

interface CalcMethods {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

            long addFunc(long addOne, long addTwo);
            long addGetResult();
}
  
```


 
 &emsp;&emsp;该文件在两个应用程序中都要包含。编写了该文件后编译就会生成对应的.java文件。
    
**2.新建一个服务类AIDLService.java，实现aidl文件中定义的接口。**
主要实现如下：

```
private static CalcMethods.Stub mCalcMethod = new         CalcMethods.Stub() {


        @Override
        public long addFunc(long addOne, long addTwo) throws RemoteException {
            result = addOne + addTwo;
            Log.d(TAG, "addFunc result is " + result);

            CalcServerActivity.mResult = result;
            return result;
        }

        @Override
        public long addGetResult() throws RemoteException {
            return result;
        }
    };
```

**3.在Server端的AndroidManifest.xml文件中注册该AIDLService。**

```
<service
            android:name=".service.AIDLService"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
            <action android:name="com.gucheng.aidl"/>
            <category android:name="android.intent.category.DEFAULT"/>
        </intent-filter>
</service>
```

**4.编写Client端的代码：**

  &emsp;&emsp;实例化ServiceConnection对象mServiceConnection。该对象在调用bindService绑定服务的时候会用到。链接成功返回一个类型为CalcMethods的对象mCalcMethods，该类型就是我们定义的AIDL类。通过该变量我们可以访问在Server端实现的方法。
    
```
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
```

&emsp;&emsp;这里我们在点击了求和按钮的响应事件里调用Server端实现的方法 。点击了求和按钮后，获取EditText中的数值，调用addFunc函数来传递参数给Server端，调用addGetResult获取计算结果，并将结果显示到TextView中。
```
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
```
	  
完整代码已经上传到github:  

Server端：https://github.com/gucheng3116/CalcServer  

Client端：https://github.com/gucheng3116/CalcClient

&emsp;&emsp;有问题可以给我留言，或者发送邮件到459843397@qq.com
