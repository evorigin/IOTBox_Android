package com.iotbox;

import com.iotbox.SerialPortActivity.SerialPortType;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GpsActivity extends GpsSerialPortActivity {

	EditText mReception;
	EditText weidu;
	EditText jingdu;
	EditText weixingshu;
	EditText weixinguse;
	
	String gpss;
	int i=0;
	GpsData gpsdata=new GpsData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//�򿪴���
		openPort();
		
		setContentView(R.layout.console_gps);
		TextView titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("GPS�ռ�ʵ��");	
		
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				GpsActivity.this.finish();
			}
		});

		
		mReception = (EditText) findViewById(R.id.editText1);
		weidu = (EditText) findViewById(R.id.editText2);
		jingdu = (EditText) findViewById(R.id.editText3);
		weixingshu = (EditText) findViewById(R.id.editText4);
		weixinguse = (EditText) findViewById(R.id.editText5);	
 
	}	
  	@Override
	protected void onDataReceived(final String s) 
  	{
  		//Runnable����������
		runOnUiThread(new Runnable() {
			public void run()
			{
				if (mReception != null)
				{
					
					mReception.append(s+"\n");
					
					if(gpsdata.parseGPRMC(s)==3)
					{
						weidu.setText(""+gpsdata.lats+":"+gpsdata.lat+"");
						jingdu.setText(""+gpsdata.lngs+":"+gpsdata.lng);											
					} 
				}   				
				
			}
		});
	}
  	
	public void backToHome(View v) {
		this.finish();
	}
}

 
/*
 * �������ڽ��յ����ֽڴ���RMC���Ƽ���λ��Ϣ
 * $GPRMC,013946,A,3202.1855,N,11849.0769,E,0.05,218.30,111105,4.5,W,A*20..
 * $GPRMC,<1> ,2,<3> ,4,<5> ,6,<7> ,<8> ,<9> ,10,11,12*hh<CR><LF>
 * <1>UTCʱ�䣬hhmmss��ʱ���룩��ʽ <2> ��λ״̬��A=��Ч��λ��V=��Ч��λ
 * <3>γ��ddmm.mmmm���ȷ֣���ʽ��ǰ���0Ҳ�������䣩 <4> γ�Ȱ���N�������򣩻�S���ϰ���
 * <5>����dddmm.mmmm���ȷ֣���ʽ��ǰ���0Ҳ�������䣩 <6> ���Ȱ���E����������W��������
 * <7>�������ʣ�000.0~999.9�ڣ�ǰ���0Ҳ�������䣩 <8> ���溽��000.0~359.9�ȣ����汱Ϊ�ο���׼��ǰ���0Ҳ�������䣩
 * <9> UTC���ڣ�ddmmyy�������꣩��ʽ <10> ��ƫ�ǣ�000.0~180.0�ȣ�ǰ���0Ҳ�������䣩
 * <11>��ƫ�Ƿ���E��������W������ <12> ģʽָʾ����NMEA0183 3.00�汾�����A=������λ��D=��֣�E=���㣬N=������Ч��
 * 
 * 
 * ����ֵ 0 ��ȷ 1У��ʧ�� 2��GPRMC��Ϣ 3��Ч��λ 4��ʽ���� 5У�����
 */
class GpsData {	
	short hour,minute,second;
	double lat,lng,speed;
	char lats,lngs;
	boolean valid;
	
	public int parseGPRMC(String by) {
		if(by==null||by.isEmpty())//�жϷǿ�
			return 4;
		if(checksum(by.getBytes())==false)//����У��Ͳ�������е�У��ͱȽ�
			return 5;
		MyStringTokenizer str=new MyStringTokenizer(new String(by),',');
		String temp=null;
		temp=str.nextToken();//ȡ��һ���Ӵ������
		if(!temp.equals("$GPRMC"))// ȷ����$GPRMC
			return 2;
	
		temp = str.nextToken();// ʱ��
		hour=(short)(Integer.parseInt(temp.substring(0,2))+8);
		minute=(short)Integer.parseInt(temp.substring(2,4));
		second=(short)Float.parseFloat(temp.substring(4));
		temp = str.nextToken();// ��λ״̬
		if(temp.length()!=1)//��
			return 42;
		else if(temp.charAt(0)=='A')//ΪA����Ч ΪV����Ч
			return 3;
		temp = str.nextToken();// γ��
		lat = Double.parseDouble(temp.substring(0,2));// γ��-��
		lat += Double.parseDouble(temp.substring(2))/60;// γ��-��
		temp = str.nextToken();// γ�Ȱ���
		if(temp.length()!=1)
			return 44;
		else if(temp.charAt(0)=='N')
			lats='N';
		else if(temp.charAt(0)=='S')
			lats='S';
		else   //������Ϣ
			return 45;
		
		temp = str.nextToken();// ����
		lng = Double.parseDouble(temp.substring(0,3));// ����-��
		lng += Double.parseDouble(temp.substring(3))/60;// ����-��
		temp = str.nextToken();// ���Ȱ���
		if(temp.length()!=1)
			return 47;
		else if(temp.charAt(0)=='E')
			lngs='E';
		else if(temp.charAt(0)=='W')
			lngs='W';
		else
			return 48;
		
		temp = str.nextToken();// ��������
		if(!temp.isEmpty()) {
			speed = Double.parseDouble(temp)*1.852;//�ٶȵ�λת��Ϊǧ��ÿСʱ
		}
		
		this.valid=true;//ת���ɹ���������������Ч
		return 0;
	}

	private  boolean checksum(byte[] b) {
		byte chk = 0;// У���
		byte cb = b[1];// ��ǰ�ֽ�
		int i = 0;
		if(b[0] != '$')
			return false;
		for(i=2;i<b.length;i++){//����У��� 
			if(b[i] == '*')
				break;
			cb = (byte)(cb^b[i]);
		}
		
		if(i!=b.length-3)//У��λ������
			return false;
		
		i++;
		byte[] bb=new byte[2];//���ڴ��������λ
		bb[0]=b[i++];bb[1]=b[i];
		try {
			chk = (byte)Integer.parseInt(new String(bb),16);//����λת��Ϊһ���ֽ�
		}
		catch(Exception e){//����λ�޷�ת��Ϊһ���ֽڣ���ʽ���� 
			return false;
		}
	
		return chk==cb;//�������У��������������Ƿ�һ��
	}
}

class MyStringTokenizer {
	private String ss;
	String split;
	String[] msg;
	int i=-1;
	MyStringTokenizer(String s,char a) {
		ss=s;
		split=""+a;
		msg=ss.split(split);
	}
	
	public String nextToken() {
		i++;
		return msg[i];
		
	}

}
