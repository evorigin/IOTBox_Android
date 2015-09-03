package com.iotbox;

public class Constant {
	
	/*Ƶ��
		��Ƶ57600
		��Ƶ9600 
		����Ƶ57600 
		3g 115200 
		zigbee 115200 
		���룬gps9600
	*/ 
    public static int baudrate_barcode 	= 	9600; 
    public static int baudrate_lrf 		= 	9600; 
    public static int baudrate_hrf 		= 	115200; 
    public static int baudrate_urf		= 	57600; 
    public static int baudrate_gps 		= 	9600; 
    public static int baudrate_gsm 		= 	115200; 
    public static int baudrate_zigbee	= 	115200; 
	
	// ָ��
    public static String barcodeSendCmd ="ff5555af1111111111";
    
	public static CharSequence rfid14443a_setPro = "010C00030410002101090000";
	public static CharSequence rfid14443a_readPro = "0109000304A0010000"; 

	public static CharSequence rfid14443b_setPro = "010C000304100021010C0000";
	public static CharSequence rfid14443b_readPro = "0109000304B0040000"; 

	public static CharSequence rfid15693_setPro = "010C00030410002101000000";
	public static CharSequence rfid15693_readPro = "010B000304142401000000";
	 
	
	public static String RFIDCommand_RMU_GetStatus = "aa020055";
	public static String RFIDCommand_RMU_GetVersion = "aa020755";
	public static String RFIDCommand_RMU_InventoryAnti9 = "aa03110955";
	public static String RFIDCommand_RMU_Inventory = "aa 02 10 55";
	public static String RFIDCommand_RMU_InventorySingle = "aa 02 18 55";
	public static String RFIDCommand_RMU_StopGet = "aa 02 12 55";
	public static String RFIDCommand_RMU_GetPower = "aa 02 01 55";
     
}
