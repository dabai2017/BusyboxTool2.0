package com.install.busybox;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import android.view.*;

public class MainActivity extends Activity 
{
	//控件声明
	Switch sw1;
	LinearLayout li1;
	ProgressDialog pd1;
	ProgressDialog pd2;
	ProgressDialog pd3;
	//变量声明
	String dabai[]={"echo 大白2017@酷安","echo 本软件编译时间为20180304_2312"};
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	
		//UI实例化
		sw1 = (Switch)findViewById(R.id.mainSwitch1);
		li1 = (LinearLayout)findViewById(R.id.mainLinearLayout1);
		
		//init
		
		//检测su
			new Thread() {
			@Override
			public void run() {
				super.run();
				//新线程操作
				try
				{
				Runtime.getRuntime().exec("su");
				}
				catch (IOException e)
				{}
			}
		}.start();
		
		//打开后检测busy
		ifbusy();
		
		li1.setOnLongClickListener(new View.OnLongClickListener() {	
		  	@Override  
	            public boolean onLongClick(View line){
				new shell().execCommand(dabai,true);
				Toast toast = Toast.makeText(getApplicationContext(),"大白2017@酷安 版权所有", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();
	                return true;  
	            }  
	        });  
		
		
		
		sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked){
						Install();
					}else {
						Uninstall();
					}
				}
			});
		}
		
		//安装
		public void Install(){
			pd1 = ProgressDialog.show(this, "提示", "正在安装Busybox");
			
			new Thread() {
				@Override
				public void run() {
					super.run();
					//新线程操作
					try
					{
						c("busybox", "/sdcard/busybox");
					}
					catch (IOException e)
					{}
					runOnUiThread(new Runnable(){
							@Override
							public void run() {
								//更新UI操作
								String cmd[]={"mount -o rw,remount /system",
									"cp /sdcard/busybox /system/xbin/busybox"};
								new shell().execCommand(cmd, true);
								pd1.dismiss();
								ifbusy();			
							}

						});
				}
			}.start();
			
		}
		//卸载
		public void Uninstall(){
			pd2 = ProgressDialog.show(this, "提示", "正在卸载Busybox");
			
			new Thread() {
				@Override
				public void run() {
					super.run();
					//新线程操作
					String cmd[]={"mount -o rw,remount /system",
						"rm -f /system/xbin/busybox"};
					new shell().execCommand(cmd, true);
					
					runOnUiThread(new Runnable(){
							@Override
							public void run() {
								//更新UI操作
								pd2.dismiss();
								ifbusy();
							}

						});


				}
			}.start();
			
			
		}
		
	//文件IO
	public void c(String assetsFileName, String OutFileName) throws IOException 
	{
        File f = new File(OutFileName);
        if (f.exists())
            f.delete();
        f = new File(OutFileName);
        f.createNewFile();
        InputStream I = getAssets().open(assetsFileName);
        OutputStream O = new FileOutputStream(OutFileName);
        byte[] b = new byte[1024];
        int l = I.read(b);
        while (l > 0) 
		{
            O.write(b, 0, l);
            l = I.read(b);
        }
        O.flush();
        I.close();
        O.close();
    }
	
		
	//判断文件存在
	public boolean fileIsExists(String fi)
	{
		try
		{
			File f=new File(fi);
			if (!f.exists())
			{
				return false;
			}
		}
		catch (Exception e)
		{
// TODO: handle exception
			return false;
		}
		return true;
	}
	
	
	
	public void ifbusy(){	
		deleteFile();
		if(fileIsExists("/system/xbin/busybox")){
			sw1.setChecked(true);
			//toast("Busybox:已安装");
		}
		else{
			sw1.setChecked(false);
			//toast("Busybox:未安装");
		}
	}
	
	public void deleteFile(){
		File file = new File("/sdcard/busybox");
		if(file.exists()){
			file.delete();
		}
	}
	
	public void toast(String a){
		Toast.makeText(getApplicationContext(),a,Toast.LENGTH_SHORT).show();
	}
	
		
		
}
