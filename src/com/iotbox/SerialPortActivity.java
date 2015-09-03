/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.iotbox;
 
import java.io.IOException;
import java.io.InputStream; 
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidParameterException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;
import android_serialport_api.SerialPort;


public abstract class SerialPortActivity extends Activity {
	public enum SerialPortType{  
		port_barcode, port_lrf, port_hrf, port_urf, port_gsm, port_gps, port_zigbee
	}   
	
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread; 
	
	private DatagramPacket dataPacket;
	private DatagramSocket udpSocket;
	private String serverIP;
	private int serverPort;
	private boolean enableUpload; 
	
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null)
						return;
					/** 
                     * �����readҪ����ע�⣬����һֱ�ȴ����ݡ����Ҫ�ж��Ƿ������ɣ�ֻ�����ý�����ʶ��������������Ĵ��� 
                     */  
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size); 

						//����ȡ��Data���͵������ 
						if(enableUpload) {  
							sendToUDP(buffer,size);
						}
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	//����ȡ��Data���͵������
	private void sendToUDP(byte[] buffer,int size) { 
		try {
			InetAddress serverAddress = InetAddress.getByName(serverIP); 
			dataPacket = new DatagramPacket (buffer, size, serverAddress, serverPort);
			udpSocket.send(dataPacket);
		}
		catch (Exception e) { 
//			e.printStackTrace();
		}
	}
	
	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("��ʾ");
		b.setMessage(resourceId);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
//				SerialPortActivity.this.finish();
			}
		});
		b.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		SharedPreferences shared = getSharedPreferences(
				"com.iotbox_preferences", MODE_PRIVATE); 
		serverIP = shared.getString("SERVER_IP", "127.0.0.1");
		serverPort =  Integer.parseInt(shared.getString("SERVER_PORT", "8080"));
		enableUpload = shared.getBoolean("ENABLE_UPLOAD", false); 
  
		try {
			udpSocket = new  DatagramSocket (); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void openPort(SerialPortType type) {
		mApplication = (Application) getApplication();
		int baudrate = 0;
		switch(type) {
			case port_barcode:{
				baudrate = Constant.baudrate_barcode;
				break;
			}
			case port_lrf:{
				baudrate = Constant.baudrate_lrf;
				break;
			}
			case port_hrf:{
				baudrate = Constant.baudrate_hrf;
				break;
			}
			case port_urf:{
				baudrate = Constant.baudrate_urf;
				break;
			}
			case port_gsm:{
				baudrate = Constant.baudrate_gsm;
				break;
			}
			case port_zigbee:{
				baudrate = Constant.baudrate_zigbee;
				break;
			} 
			default:break;
		}
		
		try {
			mSerialPort = mApplication.getSerialPort(baudrate);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream(); 
			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}
	}

	protected abstract void onDataReceived(final byte[] buffer, final int size);

	@Override
	protected void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mApplication.closeSerialPort();
		mSerialPort = null;
		udpSocket = null;
		mOutputStream = null;
		mInputStream = null;
		super.onDestroy();
	}
}