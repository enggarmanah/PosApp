package com.tokoku.pos.driver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.tokoku.pos.R;

public class BluetoothPrintDriver {
	
	//private static final String TAG = "BluetoothChatService";
	//private static final String NAME = "BluetoothPrintDriver";
	//private static final boolean D = true;
	
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private static ConnectedThread mConnectedThread;
	private static int mState;
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	public static final int UPCA = 0;
	public static final int UPCE = 1;
	public static final int EAN13 = 2;
	public static final int EAN8 = 3;
	public static final int CODE39 = 4;
	public static final int ITF = 5;
	public static final int CODEBAR = 6;
	public static final int CODE93 = 7;
	public static final int Code128_B = 8;
	public static final int CODE11 = 9;
	public static final int MSI = 10;
	
	private Context mContext;

	@SuppressLint({ "NewApi" })
	public BluetoothPrintDriver(Context context, Handler handler) {
		this.mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		this.mContext = context;
		this.mHandler = handler;
	}

	private synchronized void setState(int state) {
		Log.d("BluetoothChatService", "setState() " + mState + " -> " + state);
		mState = state;

		this.mHandler.obtainMessage(1, state, -1).sendToTarget();
	}

	public synchronized int getState() {
		return mState;
	}

	public synchronized void start() {
		Log.d("BluetoothChatService", "start");

		if (this.mConnectThread != null) {
			this.mConnectThread.cancel();
			this.mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (this.mAcceptThread == null) {
			this.mAcceptThread = new AcceptThread();
			this.mAcceptThread.start();
		}
		setState(STATE_LISTEN);
	}

	public synchronized void connect(BluetoothDevice device) {
		Log.d("BluetoothChatService", "connect to: " + device);

		if ((mState == STATE_CONNECTING) && (this.mConnectThread != null)) {
			this.mConnectThread.cancel();
			this.mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		this.mConnectThread = new ConnectThread(device);
		this.mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		Log.d("BluetoothChatService", "connected");

		if (this.mConnectThread != null) {
			this.mConnectThread.cancel();
			this.mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (this.mAcceptThread != null) {
			this.mAcceptThread.cancel();
			this.mAcceptThread = null;
		}

		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		Message msg = this.mHandler.obtainMessage(4);
		Bundle bundle = new Bundle();
		bundle.putString("device_name", device.getName());
		msg.setData(bundle);
		this.mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	public synchronized void stop() {
		Log.d("BluetoothChatService", "stop");
		if (this.mConnectThread != null) {
			this.mConnectThread.cancel();
			this.mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (this.mAcceptThread != null) {
			this.mAcceptThread.cancel();
			this.mAcceptThread = null;
		}
		setState(STATE_NONE);
	}

	public void write(byte[] out) {
		ConnectedThread r;
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		r.write(out);
	}

	public void write2(byte[] out) throws IOException {
		ConnectedThread r;
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		for (int i = 0; i < out.length; i++)
			r.mmOutStream.write(out[i]);
	}

	public static void BT_Write(String dataString) {
		byte[] data = null;

		if (mState != STATE_CONNECTED)
			return;
		ConnectedThread r = mConnectedThread;
		try {
			data = dataString.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		r.write(data);
	}

	public static void BT_Write(String dataString, boolean bGBK) {
		byte[] data = null;

		if (mState != STATE_CONNECTED)
			return;
		ConnectedThread r = mConnectedThread;

		if (bGBK)
			try {
				data = dataString.getBytes("GBK");
			} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			}
		else {
			data = dataString.getBytes();
		}

		r.write(data);
	}

	public static void BT_Write(byte[] out) {
		if (mState != STATE_CONNECTED)
			return;
		ConnectedThread r = mConnectedThread;

		r.write(out);
	}

	public static void BT_Write(byte[] out, int dataLen) {
		if (mState != STATE_CONNECTED)
			return;
		ConnectedThread r = mConnectedThread;

		r.write(out, dataLen);
	}

	private void connectionFailed() {
		setState(STATE_LISTEN);

		Message msg = this.mHandler.obtainMessage(5);
		Bundle bundle = new Bundle();
		bundle.putString("toast", mContext.getString(R.string.printer_cant_connect));
		msg.setData(bundle);
		this.mHandler.sendMessage(msg);
	}

	private void connectionLost() {
		setState(STATE_NONE);

		Message msg = this.mHandler.obtainMessage(5);
		Bundle bundle = new Bundle();
		bundle.putString("toast", mContext.getString(R.string.printer_connection_lost));
		msg.setData(bundle);
		this.mHandler.sendMessage(msg);
	}

	public static boolean IsNoConnection() {
		if (mState != STATE_CONNECTED) {
			return true;
		}
		return false;
	}

	public static boolean InitPrinter() {
		byte[] combyte = { 27, 64 };
		if (mState != STATE_CONNECTED) {
			return false;
		}
		BT_Write(combyte);
		return true;
	}

	public static void WakeUpPritner() {
		byte[] b = new byte[3];
		try {
			BT_Write(b);
			Thread.sleep(100L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void Begin() {
		WakeUpPritner();
		InitPrinter();
	}

	public static void LF() {
		byte[] cmd = { 13 };
		BT_Write(cmd);
	}

	public static void CR() {
		byte[] cmd = { 10 };
		BT_Write(cmd);
	}

	public static void SelftestPrint() {
		byte[] cmd = { 18, 84 };
		BT_Write(cmd, 2);
	}

	public static void StatusInquiry() {
		byte[] cmd = { 16, 4, -2, 16, 4, -1 };
		BT_Write(cmd, 6);
	}

	public static void SetRightSpacing(byte Distance) {
		byte[] cmd = { 27, 32, Distance };
		BT_Write(cmd);
	}

	public static void SetAbsolutePrintPosition(byte nL, byte nH) {
		byte[] cmd = { 27, 36, nL, nH };
		BT_Write(cmd);
	}

	public static void SetRelativePrintPosition(byte nL, byte nH) {
		byte[] cmd = { 27, 92, nL, nH };
		BT_Write(cmd);
	}

	public static void SetDefaultLineSpacing() {
		byte[] cmd = { 27, 50 };
		BT_Write(cmd);
	}

	public static void SetLineSpacing(byte LineSpacing) {
		byte[] cmd = { 27, 50, LineSpacing };
		BT_Write(cmd);
	}

	public static void SetLeftStartSpacing(byte nL, byte nH) {
		byte[] cmd = { 29, 76, nL, nH };
		BT_Write(cmd);
	}

	public static void SetAreaWidth(byte nL, byte nH) {
		byte[] cmd = { 29, 87, nL, nH };
		BT_Write(cmd);
	}

	public static void SetCharacterPrintMode(byte CharacterPrintMode) {
		byte[] cmd = { 27, 33, CharacterPrintMode };
		BT_Write(cmd);
	}

	public static void SetUnderline(byte UnderlineEn) {
		byte[] cmd = { 27, 45, UnderlineEn };
		BT_Write(cmd);
	}

	public static void SetBold(byte BoldEn) {
		byte[] cmd = { 27, 69, BoldEn };
		BT_Write(cmd);
	}

	public static void SetCharacterFont(byte Font) {
		byte[] cmd = { 27, 77, Font };
		BT_Write(cmd);
	}

	public static void SetRotate(byte RotateEn) {
		byte[] cmd = { 27, 86, RotateEn };
		BT_Write(cmd);
	}

	public static void SetAlignMode(byte AlignMode) {
		byte[] cmd = { 27, 97, AlignMode };
		BT_Write(cmd);
	}

	public static void SetInvertPrint(byte InvertModeEn) {
		byte[] cmd = { 27, 123, InvertModeEn };
		BT_Write(cmd);
	}

	public static void SetFontEnlarge(byte FontEnlarge) {
		byte[] cmd = { 29, 33, FontEnlarge };
		BT_Write(cmd);
	}

	public static void SetBlackReversePrint(byte BlackReverseEn) {
		byte[] cmd = { 29, 66, BlackReverseEn };
		BT_Write(cmd);
	}

	public static void SetChineseCharacterMode(byte ChineseCharacterMode) {
		byte[] cmd = { 28, 33, ChineseCharacterMode };
		BT_Write(cmd);
	}

	public static void SelChineseCodepage() {
		byte[] cmd = { 28, 38 };
		BT_Write(cmd);
	}

	public static void CancelChineseCodepage() {
		byte[] cmd = { 28, 46 };
		BT_Write(cmd);
	}

	public static void SetChineseUnderline(byte ChineseUnderlineEn) {
		byte[] cmd = { 28, 45, ChineseUnderlineEn };
		BT_Write(cmd);
	}

	public static void OpenDrawer(byte DrawerNumber, byte PulseStartTime, byte PulseEndTime) {
		byte[] cmd = { 27, 112, DrawerNumber, PulseStartTime, PulseEndTime };
		BT_Write(cmd);
	}

	public static void CutPaper() {
		byte[] cmd = { 27, 105 };
		BT_Write(cmd);
	}

	public static void PartialCutPaper() {
		byte[] cmd = { 27, 109 };
		BT_Write(cmd);
	}

	public static void FeedAndCutPaper(byte CutMode) {
		byte[] cmd = { 29, 86, CutMode };
		BT_Write(cmd);
	}

	public static void FeedAndCutPaper(byte CutMode, byte FeedDistance) {
		byte[] cmd = { 29, 86, CutMode, FeedDistance };
		BT_Write(cmd);
	}

	public static void AddCodePrint(int CodeType, String data) {
		switch (CodeType) {
		case 0:
			UPCA(data);
			break;
		case 1:
			UPCE(data);
			break;
		case 2:
			EAN13(data);
			break;
		case 3:
			EAN8(data);
			break;
		case 4:
			CODE39(data);
			break;
		case 5:
			ITF(data);
			break;
		case 6:
			CODEBAR(data);
			break;
		case 7:
			CODE93(data);
			break;
		case 8:
			Code128_B(data);
			break;
		case 9:
			break;
		case 10:
			break;
		}
	}

	public static void UPCA(String data) {
		int m = 0;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '9') || (data.charAt(i) < '0'))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void UPCE(String data) {
		int m = 1;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '9') || (data.charAt(i) < '0'))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void EAN13(String data) {
		int m = 2;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '9') || (data.charAt(i) < '0'))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void EAN8(String data) {
		int m = 3;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '9') || (data.charAt(i) < '0'))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void CODE39(String data) {
		int m = 4;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '') || (data.charAt(i) < ' '))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void ITF(String data) {
		int m = 5;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '9') || (data.charAt(i) < '0'))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void CODEBAR(String data) {
		int m = 6;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '') || (data.charAt(i) < ' '))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void CODE93(String data) {
		int m = 7;
		int num = data.length();
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '') || (data.charAt(i) < ' '))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
		}

		BT_Write(cmd);
	}

	public static void Code128_B(String data) {
		int m = 73;
		int num = data.length();
		int transNum = 0;
		int mIndex = 0;
		byte[] cmd = new byte[1024];

		cmd[(mIndex++)] = 29;
		cmd[(mIndex++)] = 107;
		cmd[(mIndex++)] = ((byte) m);
		int Code128C = mIndex;
		mIndex++;
		cmd[(mIndex++)] = 123;
		cmd[(mIndex++)] = 66;
		for (int i = 0; i < num; i++) {
			if ((data.charAt(i) > '') || (data.charAt(i) < ' '))
				return;
		}
		if (num > 30)
			return;
		for (int i = 0; i < num; i++) {
			cmd[(mIndex++)] = ((byte) data.charAt(i));
			if (data.charAt(i) == '{') {
				cmd[(mIndex++)] = ((byte) data.charAt(i));
				transNum++;
			}
		}

		int checkcodeID = 104;
		int n = 1;

		for (int i = 0; i < num; i++) {
			checkcodeID += n++ * (data.charAt(i) - ' ');
		}
		checkcodeID %= 103;

		if ((checkcodeID >= 0) && (checkcodeID <= 95)) {
			cmd[(mIndex++)] = ((byte) (checkcodeID + 32));
			cmd[Code128C] = ((byte) (num + 3 + transNum));
		} else if (checkcodeID == 96) {
			cmd[(mIndex++)] = 123;
			cmd[(mIndex++)] = 51;
			cmd[Code128C] = ((byte) (num + 4 + transNum));
		} else if (checkcodeID == 97) {
			cmd[(mIndex++)] = 123;
			cmd[(mIndex++)] = 50;
			cmd[Code128C] = ((byte) (num + 4 + transNum));
		} else if (checkcodeID == 98) {
			cmd[(mIndex++)] = 123;
			cmd[(mIndex++)] = 83;
			cmd[Code128C] = ((byte) (num + 4 + transNum));
		} else if (checkcodeID == 99) {
			cmd[(mIndex++)] = 123;
			cmd[(mIndex++)] = 67;
			cmd[Code128C] = ((byte) (num + 4 + transNum));
		} else if (checkcodeID == 100) {
			cmd[(mIndex++)] = 123;
			cmd[(mIndex++)] = 52;
			cmd[Code128C] = ((byte) (num + 4 + transNum));
		} else if (checkcodeID == 101) {
			cmd[(mIndex++)] = 123;
			cmd[(mIndex++)] = 65;
			cmd[Code128C] = ((byte) (num + 4 + transNum));
		} else if (checkcodeID == 102) {
			cmd[(mIndex++)] = 123;
			cmd[(mIndex++)] = 49;
			cmd[Code128C] = ((byte) (num + 4 + transNum));
		}

		BT_Write(cmd);
	}

	public static void printString(String str) {
		try {
			BT_Write(str.getBytes("GBK"));
			BT_Write(new byte[] { 10 });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printParameterSet(byte[] buf) {
		BT_Write(buf);
	}

	public static void printByteData(byte[] buf) {
		BT_Write(buf);
		BT_Write(new byte[] { 10 });
	}

	public static void printImage() {
		printParameterSet(new byte[] { 27, 64 });
		printParameterSet(new byte[] { 27, 33 });

		byte[] bufTemp2 = { 27, 64, 27, 74, 24, 29, 118, 48, 0, 16, 0, -128, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -16, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 63, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 15, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 3, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 63, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 15, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 3, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 8, 0, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 12, 0, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 14, 0, 63, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 15, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -16, 15, -128, 15, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -31, -1, -16, 15, -64, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, -1, -16, 15, -32, 3,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 127, -16, 15, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 63, -16, 15, -8,
				0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 31, -16, 15, -8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 15, -16, 15,
				-16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 7, -16, 15, -32, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -32, 3, -16,
				15, -64, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 1, -16, 15, -128, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -8, 0,
				-16, 15, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 112, 14, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0,
				48, 12, 0, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 16, 8, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -128,
				0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 0, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -32, 0,
				0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 0, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -8, 0, 0,
				31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, 127,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 3, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 0, 63, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -8, 0, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 0, 15, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -32, 0, 0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 0, 3, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 0, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 16, 8, 0, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -2, 0, 48, 12, 0, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, 0, 112, 14, 0, 63, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -8, 0, -16, 15, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 1, -16, 15, -128, 15, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -32, 3, -16, 15, -64, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 7, -16, 15, -32, 3, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 15, -16, 15, -16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 31, -16, 15, -8, 0,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 63, -16, 15, -4, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 127, -16, 15, -8,
				1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, -1, -16, 15, -16, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -31, -1, -16, 15,
				-32, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -16, 15, -64, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16,
				15, -128, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 15, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 14, 0, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 12, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 0, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 0, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 0, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-16, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -16, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10 };
		printByteData(bufTemp2);

		printString("");

		printParameterSet(new byte[] { 27, 64 });
		printParameterSet(new byte[] { 27, 97 });
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			try {
				tmp = BluetoothPrintDriver.this.mAdapter.listenUsingRfcommWithServiceRecord("BluetoothPrintDriver", BluetoothPrintDriver.MY_UUID);
			} catch (IOException e) {
				Log.e("BluetoothChatService", "listen() failed", e);
			}
			this.mmServerSocket = tmp;
		}

		public void run() {
			Log.d("BluetoothChatService", "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;

			while (BluetoothPrintDriver.mState != 3) {
				try {
					socket = this.mmServerSocket.accept();
				} catch (Exception e) {
					mState = STATE_NONE;
					Log.e("BluetoothChatService", "accept() failed", e);
					break;
				}

				if (socket != null) {
					synchronized (BluetoothPrintDriver.this) {
						switch (BluetoothPrintDriver.mState) {
						case 1:
						case 2:
							BluetoothPrintDriver.this.connected(socket, socket.getRemoteDevice());
							break;
						case 0:
						case 3:
							try {
								socket.close();
							} catch (IOException e) {
								Log.e("BluetoothChatService", "Could not close unwanted socket", e);
							}
						}
					}
				}
			}

			Log.i("BluetoothChatService", "END mAcceptThread");
		}

		public void cancel() {
			Log.d("BluetoothChatService", "cancel " + this);
			try {
				this.mmServerSocket.close();
			} catch (IOException e) {
				Log.e("BluetoothChatService", "close() of server failed", e);
			}
		}
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			this.mmDevice = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(BluetoothPrintDriver.MY_UUID);
			} catch (IOException e) {
				Log.e("BluetoothChatService", "create() failed", e);
			}
			this.mmSocket = tmp;
		}

		public void run() {
			Log.i("BluetoothChatService", "BEGIN mConnectThread");
			setName("ConnectThread");

			BluetoothPrintDriver.this.mAdapter.cancelDiscovery();
			try {
				this.mmSocket.connect();
			} catch (IOException e) {
				BluetoothPrintDriver.this.connectionFailed();
				try {
					this.mmSocket.close();
				} catch (IOException e2) {
					Log.e("BluetoothChatService", "unable to close() socket during connection failure", e2);
				}

				BluetoothPrintDriver.this.start();
				return;
			}

			synchronized (BluetoothPrintDriver.this) {
				BluetoothPrintDriver.this.mConnectThread = null;
			}

			BluetoothPrintDriver.this.connected(this.mmSocket, this.mmDevice);
		}

		public void cancel() {
			try {
				this.mmSocket.close();
			} catch (IOException e) {
				Log.e("BluetoothChatService", "close() of connect socket failed", e);
			}
		}
	}

	private class ConnectedThread extends Thread {
		
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d("BluetoothChatService", "create ConnectedThread");
			this.mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e("BluetoothChatService", "temp sockets not created", e);
			}

			this.mmInStream = tmpIn;
			this.mmOutStream = tmpOut;
		}

		public void run() {
			Log.i("BluetoothChatService", "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			while (true) {
				try {
					
					int i = 0;
					
					buffer[i] = ((byte) this.mmInStream.read());

					i++;
					if (i >= 3) {
						Log.i("BluetoothChatService", "revBuffer[0]:" + buffer[0] + "  revBuffer[1]:" + buffer[1] + "  revBuffer[2]:"
								+ buffer[2]);
						
						int unknownValue = -1;
						
						BluetoothPrintDriver.this.mHandler.obtainMessage(2, unknownValue, -1, buffer).sendToTarget();

						if (this.mmInStream.available() != 0)
							;
					}
				} catch (IOException e) {
					mState = STATE_NONE;
					Log.e("BluetoothChatService", "disconnected", e);
					BluetoothPrintDriver.this.connectionLost();
					break;
				}
			}
		}

		public void write(byte[] buffer) {
			try {
				this.mmOutStream.write(buffer);

				BluetoothPrintDriver.this.mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e("BluetoothChatService", "Exception during write", e);
			}
		}

		public void write(byte[] buffer, int dataLen) {
			try {
				for (int i = 0; i < dataLen; i++) {
					this.mmOutStream.write(buffer[i]);
				}

				BluetoothPrintDriver.this.mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e("BluetoothChatService", "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				this.mmSocket.close();
			} catch (IOException e) {
				Log.e("BluetoothChatService", "close() of connect socket failed", e);
			}
		}
	}
}