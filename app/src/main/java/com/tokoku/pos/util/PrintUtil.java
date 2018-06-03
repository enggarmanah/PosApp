package com.tokoku.pos.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.tokoku.pos.R;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.OrderItem;
import com.android.pos.dao.Orders;
import com.android.pos.dao.Product;
import com.android.pos.dao.TransactionItem;
import com.android.pos.dao.Transactions;
import com.tokoku.pos.Constant;
import com.tokoku.pos.cashier.CashierActivity;
import com.tokoku.pos.cashier.CashierPrinterListActivity;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.driver.BluetoothPrintDriver;

import org.apache.commons.lang3.StringUtils;

public class PrintUtil {

	// Printer Configuration

	// Debugging
	private static final String TAG = "BloothPrinterActivity";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	
	public static int revBytes = 0;
	public static boolean isHex = false;
	
	public static Integer mPrinterLineSize = 32;
	
	public static final int REFRESH = 8;

	// Name of the connected device
	private static String mConnectedDeviceName = null;
	// Local Bluetooth adapter
	private static BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private static BluetoothPrintDriver mChatService = null;
	
	private static Activity mActivity;
	private static ProductDaoService mProductDaoService;

    private static String spaces = "                                                                                                    ";
    private static String divider = "---------------------------------------------------------------------------------------------------";
	
	public static void reset() {
		
		if (mChatService != null) {
			mChatService.stop();
		}
		
		mConnectedDeviceName = null;
		mBluetoothAdapter = null;
		mChatService = null;
	}
	
	public static void setPrinterLineSize(Integer printerLineSize) {
		
		if (printerLineSize != null) {
			mPrinterLineSize = printerLineSize;
		}
	}
	
	public static boolean initBluetooth(CashierActivity activity) {
		
		boolean isActivated = false;
		
		if (activity != null) {
			mActivity = activity;
		}
		
		if (mActivity == null) {
			return false;
		}
		
		if (!isPrinterActive()) {
			return false;
		}
		
		// Get local Bluetooth adapter
		
		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult

		if (mBluetoothAdapter == null) {
            ((CashierActivity)mActivity).setMessage(mActivity.getString(R.string.alert_bluetooth_not_available));

		} else {

			if (!mBluetoothAdapter.isEnabled()) {

				((CashierActivity)mActivity).setMessage(mActivity.getString(R.string.alert_bluetooth_not_active));
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				
				mActivity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

			} else {
				
				isActivated = true;
				
				if (mChatService == null) {
					
					setupChat();
				} 
			}
		}
		
		return isActivated;
	}
	
	public static void setupChat() {

		if (mChatService == null) {
			
			mChatService = new BluetoothPrintDriver(mActivity, mHandler);
		}
	}
	
	public static void connectToBluetoothPrinter(String address) {
		
		// Get Bluetooth driver
		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		
		// Get the BLuetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

		// Attempt to connect to the device
		mChatService.connect(device);
	}
	
	public static void selectBluetoothPrinter() {
		
		if (mActivity == null) {
			return;
		}
		
		setupChat();
		
		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		
		if (mBluetoothAdapter != null && BluetoothPrintDriver.IsNoConnection()) {
		
			mChatService.stop();
			mChatService.start();
				
			Intent serverIntent = new Intent(mActivity, CashierPrinterListActivity.class);
			mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		}
	}

	public static void disablePrinterOptions() {
		
		if (mActivity != null) {
			((CashierActivity)mActivity).disablePrinterOption();
		}
	}
	
	// The Handler that gets information back from the BluetoothChatService
	private static final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			if (mActivity == null) {
				return;
			}

			switch (msg.what) {

			case MESSAGE_STATE_CHANGE:

				switch (msg.arg1) {
				
				case BluetoothPrintDriver.STATE_CONNECTED:

					((CashierActivity)mActivity).showMessage(mActivity.getString(R.string.printer_connected_to, mConnectedDeviceName));
					((CashierActivity)mActivity).clearMessage();
					
					((CashierActivity)mActivity).setSelectPrinterVisible(false);
					
					break;

				case BluetoothPrintDriver.STATE_CONNECTING:

					((CashierActivity)mActivity).showMessage(mActivity.getString(R.string.printer_connecting));
					
					break;

				case BluetoothPrintDriver.STATE_LISTEN:

				case BluetoothPrintDriver.STATE_NONE:
					
					((CashierActivity)mActivity).setMessage(mActivity.getString(R.string.printer_please_check_printer));
					((CashierActivity)mActivity).setSelectPrinterVisible(true);

					break;
				}
				break;

			case MESSAGE_WRITE:
				break;

			case MESSAGE_READ:

				String ErrorMsg = null;
				byte[] readBuf = (byte[]) msg.obj;
				float Voltage = 0;

				if (D) {
					Log.i(TAG, "readBuf[0]:" + readBuf[0] + "  readBuf[1]:" + readBuf[1] + "  readBuf[2]:" + readBuf[2]);
				}

				if (readBuf[2] == 0) {
					ErrorMsg = "NO ERROR!";
				}

				else {

					if ((readBuf[2] & 0x02) != 0)
						ErrorMsg = "ERROR: No printer connected!";

					if ((readBuf[2] & 0x04) != 0)
						ErrorMsg = "ERROR: No paper!  ";

					if ((readBuf[2] & 0x08) != 0)
						ErrorMsg = "ERROR: Voltage is too low!  ";

					if ((readBuf[2] & 0x40) != 0)
						ErrorMsg = "ERROR: Printer Over Heat!  ";
				}

				Voltage = (float) ((readBuf[0] * 256 + readBuf[1]) / 10.0);

				((CashierActivity)mActivity).showMessage(ErrorMsg + "Battery voltage : " + Voltage + " V");
				break;

			case MESSAGE_DEVICE_NAME:

				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				((CashierActivity)mActivity).showMessage(mActivity.getString(R.string.printer_connected_to, mConnectedDeviceName));
				break;

			case MESSAGE_TOAST:

				((CashierActivity)mActivity).showMessage(msg.getData().getString(TOAST));
				break;
			}
		}
	};
	
	public static boolean isBluetoothEnabled() {
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			return false;
		} else {
		    if (mBluetoothAdapter.isEnabled()) {
		        // Bluetooth is enable :)
		    	return true;
		    } else {
		    	return false;
		    }
		}
	}
	
	public static boolean isPrinterConnected() {
		
		return !BluetoothPrintDriver.IsNoConnection();
	}
	
	public static boolean isPrinterActive() {
		
		return Constant.STATUS_ACTIVE.equals(MerchantUtil.getMerchant().getPrinterRequired());
	}
	
	private static boolean isMiniFont() {
		
		return !Constant.FONT_SIZE_REGULAR.equals(MerchantUtil.getMerchant().getPrinterMiniFont());
	}

	public static void printTransaction(Transactions transaction, String printingOption) throws Exception {

		if (Constant.PRINTING_OPTION_YES_ONCE.equals(printingOption)) {
			printTransaction(transaction);
			printFooter();
			printFeed();

		} else if (Constant.PRINTING_OPTION_YES_TWICE.equals(printingOption)) {

            printTransaction(transaction);
            printFooter();
            BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
            BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
            printTransaction(transaction);
            printFooter();
            printFeed();
		}
	}

	public static void printTransaction(Transactions transaction) throws Exception {
		
		mProductDaoService = new ProductDaoService();

		BluetoothPrintDriver.WakeUpPritner();
		
		BluetoothPrintDriver.Begin();

		// width
		// BluetoothPrintDriver.SetFontEnlarge((byte)0x10);

		// height
		// BluetoothPrintDriver.SetFontEnlarge((byte)0x01);

		// underline
		// BluetoothPrintDriver.SetUnderline((byte)0x02);

		// bold
		// BluetoothPrintDriver.SetBold((byte)0x01);

		// mini font
		
		if (isMiniFont()) {
			BluetoothPrintDriver.SetCharacterFont((byte) 0x01);
		}

		// highlight
		// BluetoothPrintDriver.SetBlackReversePrint((byte)0x01);

		StringBuffer line = new StringBuffer();
		
		Merchant merchant = MerchantUtil.getMerchant();
		
		line.append(merchant.getName().toUpperCase(CommonUtil.getLocale()));
		
		line.append(spaces.substring(0, mPrinterLineSize - merchant.getName().length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		String addrAndTelp = merchant.getAddress();
		
		if (!CommonUtil.isEmpty(merchant.getTelephone())) {
			 addrAndTelp += mActivity.getString(R.string.print_telephone) + merchant.getTelephone();
		}
		
		String[] addrAndTelps = addrAndTelp.split(Constant.SPACE_STRING);
		
		String addrAndTlpLine = Constant.EMPTY_STRING;
		
		for (String str : addrAndTelps) {
			
			if (addrAndTlpLine.length() + str.length() < mPrinterLineSize) {
				
				addrAndTlpLine = addrAndTlpLine + str + Constant.SPACE_STRING; 
			
			} else {
				
				line.setLength(0);
				line.append(addrAndTlpLine.toUpperCase(CommonUtil.getLocale()));
				line.append(spaces.substring(0, mPrinterLineSize - addrAndTlpLine.length()));
				
				BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
				
				addrAndTlpLine = str + Constant.SPACE_STRING;
			}
		}
		
		line.setLength(0);
		line.append(addrAndTlpLine.toUpperCase(CommonUtil.getLocale()));
		line.append(spaces.substring(0, mPrinterLineSize - addrAndTlpLine.length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		String transDateText = CommonUtil.formatDateTime(transaction.getTransactionDate()); 
		
		line.setLength(0);
		line.append(transDateText);
		line.append(spaces.substring(0, mPrinterLineSize - transDateText.length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		String cashierText = mActivity.getString(R.string.print_cashier) + transaction.getCashierName();
		
		line.setLength(0);
		line.append(cashierText);
		line.append(spaces.substring(0, mPrinterLineSize - cashierText.length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		if (!CommonUtil.isEmpty(transaction.getOrderType())) {
		
			String orderType = mActivity.getString(R.string.print_order_type) + CodeUtil.getOrderTypeLabel(transaction.getOrderType());
			
			if (!Constant.TXN_ORDER_TYPE_SERVICE.equals(transaction.getOrderType())) {
				
				line.setLength(0);
				line.append(orderType);
				line.append(spaces.substring(0, mPrinterLineSize - orderType.length()));
				BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
			}
			
			String orderReference = Constant.EMPTY_STRING;
			
			if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(transaction.getOrderType())) {
				orderReference = mActivity.getString(R.string.print_table_no) + transaction.getOrderReference();
			} else {
				orderReference = mActivity.getString(R.string.print_customer) + transaction.getOrderReference();
			}
			
			line.setLength(0);
			line.append(orderReference);
			line.append(spaces.substring(0, mPrinterLineSize - orderReference.length()));
			BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
			
			BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		} else if (!CommonUtil.isEmpty(transaction.getCustomerName())) {
			
			String customer = mActivity.getString(R.string.print_customer) + transaction.getCustomerName();
			
			line.setLength(0);
			line.append(customer);
			line.append(spaces.substring(0, mPrinterLineSize - customer.length()));
			BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
			
			BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		}
		
		List<TransactionItem> transactionItems = transaction.getTransactionItemList();
		
		float totalQuantity = 0;
		
		for (TransactionItem item : transactionItems) {
			
			if (item.getQuantity() != 0) {
			
				String productName = item.getProductName();
				
				if (productName.length() > mPrinterLineSize) {
					productName = productName.substring(0, mPrinterLineSize);
				}
				
				float quantity = item.getQuantity();
				float price = item.getPrice();
				float total = quantity * price;
				
				totalQuantity += quantity;
				
				line.setLength(0);
				
				line.append(productName);
				line.append(spaces.substring(0, mPrinterLineSize - productName.length()));
				
				BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
				
				String quantityStr = CommonUtil.formatNumber(quantity);
				String priceStr = CommonUtil.formatNumber(price);
				String totalStr = CommonUtil.formatCurrency(total);
				
				line.setLength(0);
				
				line.append(quantityStr);
				
				int size = quantityStr.length() <= 2 ? 2 : quantityStr.length();
				line.append(spaces.substring(0, size - quantityStr.length()));
				
				Product product = mProductDaoService.getProduct(item.getProductId());
				
				line.append(" " + CommonUtil.getShortUnitName(product.getQuantityType()) + " x @ ");
				line.append(priceStr);
				line.append(spaces.substring(0, mPrinterLineSize - line.toString().length() - totalStr.length()));
				line.append(totalStr);
				
				BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
			}
		}
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		String billLabel = CommonUtil.formatNumber(transactionItems.size()) + Constant.SPACE_STRING + mActivity.getString(R.string.product_short) + Constant.SPACE_STRING +
 						   CommonUtil.formatNumber(totalQuantity) + Constant.SPACE_STRING + mActivity.getString(R.string.item);
		String billAmount = CommonUtil.formatCurrency(transaction.getBillAmount()); 
		
		line.setLength(0);
		line.append(billLabel);
		line.append(spaces.substring(0, mPrinterLineSize - billLabel.length() - billAmount.length()));
		line.append(billAmount);
		
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		if (transaction.getDiscountName() != null) {
			
			String discountLabel = transaction.getDiscountName();
			
			if (transaction.getDiscountPercentage() != null && transaction.getDiscountPercentage() != 0) {
				discountLabel +=  Constant.SPACE_STRING + CommonUtil.formatPercentage(transaction.getDiscountPercentage());
			}
			
			String discountText = mActivity.getString(R.string.print_negative) + CommonUtil.formatCurrency(transaction.getDiscountAmount());

			int minSpaces = 2;

			if (discountLabel.length() + minSpaces + discountText.length() < mPrinterLineSize) {

                line.setLength(0);
                line.append(discountLabel);
                line.append(spaces.substring(0, mPrinterLineSize - discountLabel.length() - discountText.length()));
                line.append(discountText);

                BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);

            } else {

                line.setLength(0);
                line.append(discountLabel);

                BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);

                line.setLength(0);
                line.append(spaces.substring(0, mPrinterLineSize - discountText.length()));
                line.append(discountText);

                BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
            }
		}
		
		if (transaction.getTaxAmount() != 0) {
		
			String taxLabel = mActivity.getString(R.string.print_tax) + CommonUtil.formatPercentage(transaction.getTaxPercentage());
			String taxText = CommonUtil.formatCurrency(transaction.getTaxAmount());
			
			line.setLength(0);
			line.append(taxLabel);
			line.append(spaces.substring(0, mPrinterLineSize - taxLabel.length() - taxText.length()));
			line.append(taxText);
			
			BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		}
		
		if (transaction.getServiceChargeAmount() != 0) {
			
			String serviceChargeLabel = mActivity.getString(R.string.print_service_charge) + CommonUtil.formatPercentage(transaction.getServiceChargePercentage());
			String serviceChargeText = CommonUtil.formatCurrency(transaction.getServiceChargeAmount());
			
			line.setLength(0);
			line.append(serviceChargeLabel);
			line.append(spaces.substring(0, mPrinterLineSize - serviceChargeLabel.length() - serviceChargeText.length()));
			line.append(serviceChargeText);
			
			BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		}
		
		String totalLabel = mActivity.getString(R.string.print_total);
		
		String paymentLabel =  Constant.EMPTY_STRING;
		String returnLabel = Constant.EMPTY_STRING;
		
		if (Constant.PAYMENT_TYPE_CREDIT.equals(transaction.getPaymentType())) {
			paymentLabel = mActivity.getString(R.string.payment);
			returnLabel = CodeUtil.getPaymentTypeLabel(transaction.getPaymentType());
		} else {
			paymentLabel = CodeUtil.getPaymentTypeLabel(transaction.getPaymentType());
			returnLabel = mActivity.getString(R.string.change);
		}
		
		String totalText = CommonUtil.formatCurrency(transaction.getTotalAmount());
		String paymentText = CommonUtil.formatCurrency(transaction.getPaymentAmount());
		String returnText = CommonUtil.formatCurrency(Math.abs(transaction.getReturnAmount()));
		
		line.setLength(0);
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		line.setLength(0);
		
		line.append(totalLabel);
		line.append(spaces.substring(0, mPrinterLineSize - totalLabel.length() - totalText.length()));
		line.append(totalText);

		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);

		line.setLength(0);
		line.append(paymentLabel);
		line.append(spaces.substring(0, mPrinterLineSize - paymentLabel.length() - paymentText.length()));
		line.append(paymentText);

		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);

		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);

		line.setLength(0);
		line.append(returnLabel);
		line.append(spaces.substring(0, mPrinterLineSize - returnLabel.length() - returnText.length()));
		line.append(returnText);

		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);

		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
	}

	public static void initPdf(Activity activity) {

	    mActivity = activity;
    }

	public static Uri printPdf(Transactions transaction) throws Exception {

        File file = new File(Environment.getExternalStorageDirectory() + "/Receipt " + transaction.getTransactionNo() + ".pdf");

        OutputStream output = new FileOutputStream(file);

        Document document = new Document(PageSize.A4, 25f, 25f, 15f, 20f);

        PdfWriter pdfWriter = PdfWriter.getInstance(document, output);

        document.open();

        mPrinterLineSize = 90;

        float fntSize = 10f;
        float lineSpacing = 12f;

        Paragraph p = new Paragraph(new Phrase(lineSpacing,"",
                FontFactory.getFont(FontFactory.COURIER, fntSize)));

        mProductDaoService = new ProductDaoService();

		StringBuffer line = new StringBuffer();

		Merchant merchant = MerchantUtil.getMerchant();

		p.add(Constant.NEWLINE_STRING);

		line.append(merchant.getName());
		line.append(spaces.substring(0, mPrinterLineSize - merchant.getName().length()));
		p.add(line.toString() + Constant.NEWLINE_STRING);

		p.add(Constant.NEWLINE_STRING);

		String addrAndTelp = merchant.getAddress();

		if (!CommonUtil.isEmpty(merchant.getTelephone())) {
			addrAndTelp += mActivity.getString(R.string.print_telephone) + merchant.getTelephone();
		}

		String[] addrAndTelps = addrAndTelp.split(Constant.SPACE_STRING);

		String addrAndTlpLine = Constant.EMPTY_STRING;

		for (String str : addrAndTelps) {

			if (addrAndTlpLine.length() + str.length() < mPrinterLineSize) {

				addrAndTlpLine = addrAndTlpLine + str + Constant.SPACE_STRING;

			} else {

				line.setLength(0);
				line.append(addrAndTlpLine);
				line.append(spaces.substring(0, mPrinterLineSize - addrAndTlpLine.length()));

				p.add(line.toString() + Constant.NEWLINE_STRING);

				addrAndTlpLine = str + Constant.SPACE_STRING;
			}
		}

		line.setLength(0);
		line.append(addrAndTlpLine);
		line.append(spaces.substring(0, mPrinterLineSize - addrAndTlpLine.length()));
		p.add(line.toString() + Constant.NEWLINE_STRING);

		p.add(Constant.NEWLINE_STRING);

		String transDateText = CommonUtil.formatDateTime(transaction.getTransactionDate());

		line.setLength(0);
		line.append(transDateText);
		line.append(spaces.substring(0, mPrinterLineSize - transDateText.length() - transaction.getTransactionNo().length()));
		line.append(transaction.getTransactionNo());
		p.add(line.toString() + Constant.NEWLINE_STRING);

		String cashierText = mActivity.getString(R.string.print_cashier) + transaction.getCashierName();

		line.setLength(0);
		line.append(cashierText);
		line.append(spaces.substring(0, mPrinterLineSize - cashierText.length()));
		p.add(line.toString() + Constant.NEWLINE_STRING);

		p.add(Constant.NEWLINE_STRING);

		List<TransactionItem> transactionItems = transaction.getTransactionItemList();

		float totalQuantity = 0;

		for (TransactionItem item : transactionItems) {

			if (item.getQuantity() != 0) {

			    int pricingInfoLineSize = 37;
			    int productLineSize = mPrinterLineSize - pricingInfoLineSize;

				String productName = item.getProductName();

				if (productName.length() > productLineSize - 2) {
					productName = productName.substring(0, productLineSize - 2);
				}

				float quantity = item.getQuantity();
				float price = item.getPrice();
				float total = quantity * price;

				totalQuantity += quantity;

				line.setLength(0);

				line.append(productName);
				line.append(spaces.substring(0, productLineSize - productName.length()));

				p.add(line.toString());

				String quantityStr = CommonUtil.formatNumber(quantity);
				String priceStr = CommonUtil.formatNumber(price);
				String totalStr = CommonUtil.formatCurrency(total);

				line.setLength(0);

				line.append(quantityStr);

				int size = quantityStr.length() <= 2 ? 2 : quantityStr.length();
				line.append(spaces.substring(0, size - quantityStr.length()));

				Product product = mProductDaoService.getProduct(item.getProductId());

				line.append(" " + CommonUtil.getShortUnitName(product.getQuantityType()) + " x @ ");
				line.append(priceStr);
				line.append(spaces.substring(0, pricingInfoLineSize - line.toString().length() - totalStr.length()));
				line.append(totalStr);

				p.add(line.toString() + Constant.NEWLINE_STRING);
			}
		}

		p.add(Constant.NEWLINE_STRING);

		String billLabel = CommonUtil.formatNumber(transactionItems.size()) + Constant.SPACE_STRING + mActivity.getString(R.string.product_short) + Constant.SPACE_STRING +
				CommonUtil.formatNumber(totalQuantity) + Constant.SPACE_STRING + mActivity.getString(R.string.item);
		String billAmount = CommonUtil.formatCurrency(transaction.getBillAmount());

		line.setLength(0);
		line.append(billLabel);
		line.append(spaces.substring(0, mPrinterLineSize - billLabel.length() - billAmount.length()));
		line.append(billAmount);

		p.add(line.toString() + Constant.NEWLINE_STRING);

		p.add(spaces.substring(0, mPrinterLineSize) + Constant.NEWLINE_STRING);

		if (transaction.getDiscountName() != null) {

			String discountLabel = transaction.getDiscountName();

			if (transaction.getDiscountPercentage() != null && transaction.getDiscountPercentage() != 0) {
				discountLabel +=  Constant.SPACE_STRING + CommonUtil.formatPercentage(transaction.getDiscountPercentage());
			}

			String discountText = mActivity.getString(R.string.print_negative) + CommonUtil.formatCurrency(transaction.getDiscountAmount());

			int minSpaces = 2;

			if (discountLabel.length() + minSpaces + discountText.length() < mPrinterLineSize) {

				line.setLength(0);
				line.append(discountLabel);
				line.append(spaces.substring(0, mPrinterLineSize - discountLabel.length() - discountText.length()));
				line.append(discountText);

				p.add(line.toString() + Constant.NEWLINE_STRING);

			} else {

				line.setLength(0);
				line.append(discountLabel);

				p.add(line.toString() + Constant.NEWLINE_STRING);

				line.setLength(0);
				line.append(spaces.substring(0, mPrinterLineSize - discountText.length()));
				line.append(discountText);

				p.add(line.toString() + Constant.NEWLINE_STRING);
			}
		}

		if (transaction.getTaxAmount() != 0) {

			String taxLabel = mActivity.getString(R.string.print_tax) + CommonUtil.formatPercentage(transaction.getTaxPercentage());
			String taxText = CommonUtil.formatCurrency(transaction.getTaxAmount());

			line.setLength(0);
			line.append(taxLabel);
			line.append(spaces.substring(0, mPrinterLineSize - taxLabel.length() - taxText.length()));
			line.append(taxText);

			p.add(line.toString() + Constant.NEWLINE_STRING);
		}

		if (transaction.getServiceChargeAmount() != 0) {

			String serviceChargeLabel = mActivity.getString(R.string.print_service_charge) + CommonUtil.formatPercentage(transaction.getServiceChargePercentage());
			String serviceChargeText = CommonUtil.formatCurrency(transaction.getServiceChargeAmount());

			line.setLength(0);
			line.append(serviceChargeLabel);
			line.append(spaces.substring(0, mPrinterLineSize - serviceChargeLabel.length() - serviceChargeText.length()));
			line.append(serviceChargeText);

			p.add(line.toString() + Constant.NEWLINE_STRING);
		}

		String totalLabel = mActivity.getString(R.string.print_total);

		String paymentLabel =  Constant.EMPTY_STRING;
		String returnLabel = Constant.EMPTY_STRING;

		if (Constant.PAYMENT_TYPE_CREDIT.equals(transaction.getPaymentType())) {
			paymentLabel = mActivity.getString(R.string.payment);
			returnLabel = CodeUtil.getPaymentTypeLabel(transaction.getPaymentType());
		} else {
			paymentLabel = CodeUtil.getPaymentTypeLabel(transaction.getPaymentType());
			returnLabel = mActivity.getString(R.string.change);
		}

		String totalText = CommonUtil.formatCurrency(transaction.getTotalAmount());
		String paymentText = CommonUtil.formatCurrency(transaction.getPaymentAmount());
		String returnText = CommonUtil.formatCurrency(Math.abs(transaction.getReturnAmount()));

		line.setLength(0);

		p.add(Constant.NEWLINE_STRING);

		line.setLength(0);

		line.append(totalLabel.toUpperCase());
		line.append(spaces.substring(0, mPrinterLineSize - totalLabel.length() - totalText.length()));
		line.append(totalText);

		p.add(line.toString() + Constant.NEWLINE_STRING);

		line.setLength(0);
		line.append(paymentLabel.toUpperCase());
		line.append(spaces.substring(0, mPrinterLineSize - paymentLabel.length() - paymentText.length()));
		line.append(paymentText);

		p.add(line.toString() + Constant.NEWLINE_STRING);

		p.add(Constant.NEWLINE_STRING);

		line.setLength(0);
		line.append(returnLabel.toUpperCase());
		line.append(spaces.substring(0, mPrinterLineSize - returnLabel.length() - returnText.length()));
		line.append(returnText);

		p.add(line.toString() + Constant.NEWLINE_STRING);

        // print footer

        p.add(Constant.NEWLINE_STRING);

        String footer = merchant.getPrinterFooter();

        if (!CommonUtil.isEmpty(footer)) {
            String[] footers = footer.split(Constant.SPACE_STRING);
            String footerLine = Constant.EMPTY_STRING;

            for (String str : footers) {

                if (footerLine.length() + str.length() < mPrinterLineSize) {

                    footerLine = footerLine + str + Constant.SPACE_STRING;

                } else {

                    line.setLength(0);
                    line.append(footerLine.toUpperCase(CommonUtil.getLocale()));
                    line.append(spaces.substring(0, mPrinterLineSize - footerLine.length()));

                    p.add(line.toString() + Constant.NEWLINE_STRING);

                    footerLine = str + Constant.SPACE_STRING;
                }
            }

            line.setLength(0);
            line.append(footerLine);
            line.append(spaces.substring(0, mPrinterLineSize - footerLine.length()));

            p.add(line.toString() + Constant.NEWLINE_STRING);

        } else {
            footer = mActivity.getString(R.string.print_thank_you);

            line.setLength(0);
            line.append(footer);
            line.append(spaces.substring(0, mPrinterLineSize - footer.length()));

            p.add(line.toString() + Constant.NEWLINE_STRING);
        }

        p.add(Constant.NEWLINE_STRING);

        // creating line divider

        int addrAndTelpLineCount = StringUtils.countMatches(addrAndTlpLine, "\n") + 1;
        int printerFooterCount = StringUtils.countMatches(footer, "\n") + 1;

        int lineHeight = 12;

        float top = 818f;
        float line1Y = top;
        float line2Y = line1Y - 2 * lineHeight;
        float line3Y = line2Y - lineHeight - addrAndTelpLineCount * lineHeight;
        float line4Y = line3Y - 3 * lineHeight;
        float line5Y = line4Y - lineHeight - (transactionItems.size() * lineHeight);
        float line6Y = line5Y - 3 * lineHeight;
        float line7Y = line6Y - 3 * lineHeight;
        float line8Y = line7Y - 2 * lineHeight;
        float line9Y = line8Y - lineHeight - printerFooterCount * lineHeight;

        float leftX = 20;
        float rightX = 570;
        float midLeftX = 335;
        float midRightX = 465;

        PdfContentByte canvas = pdfWriter.getDirectContent();
        CMYKColor magentaColor = new CMYKColor(0.f, 0.f, 0.f, 1.f);

        canvas.setColorStroke(magentaColor);
        canvas.moveTo(leftX, line1Y);
        canvas.lineTo(rightX, line1Y);
        canvas.moveTo(leftX, line2Y);
        canvas.lineTo(rightX, line2Y);
        canvas.moveTo(leftX, line3Y);
        canvas.lineTo(rightX, line3Y);
        canvas.moveTo(leftX, line4Y);
        canvas.lineTo(rightX, line4Y);
        canvas.moveTo(leftX, line5Y);
        canvas.lineTo(rightX, line5Y);
        canvas.moveTo(leftX, line6Y);
        canvas.lineTo(rightX, line6Y);
        canvas.moveTo(leftX, line7Y);
        canvas.lineTo(rightX, line7Y);
        canvas.moveTo(leftX, line8Y);
        canvas.lineTo(rightX, line8Y);
        canvas.moveTo(leftX, line9Y);
        canvas.lineTo(rightX, line9Y);

        canvas.moveTo(leftX, line1Y);
        canvas.lineTo(leftX, line9Y);
        canvas.moveTo(midLeftX, line4Y);
        canvas.lineTo(midLeftX, line5Y);
        canvas.moveTo(midRightX, line4Y);
        canvas.lineTo(midRightX, line8Y);
        canvas.moveTo(rightX, top);
        canvas.lineTo(rightX, line9Y);

        canvas.closePathStroke();

        document.add(p);
        document.close();

        return Uri.fromFile(file);
	}

    public static void printFooter() throws Exception {

        StringBuffer line = new StringBuffer();

	    Merchant merchant = MerchantUtil.getMerchant();
        String footer = merchant.getPrinterFooter();

        if (!CommonUtil.isEmpty(footer)) {
            String[] footers = footer.split(Constant.SPACE_STRING);
            String footerLine = Constant.EMPTY_STRING;

            for (String str : footers) {

                if (footerLine.length() + str.length() < mPrinterLineSize) {

                    footerLine = footerLine + str + Constant.SPACE_STRING;

                } else {

                    line.setLength(0);
                    line.append(footerLine.toUpperCase(CommonUtil.getLocale()));
                    line.append(spaces.substring(0, mPrinterLineSize - footerLine.length()));

                    BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);

                    footerLine = str + Constant.SPACE_STRING;
                }
            }

            line.setLength(0);
            line.append(footerLine.toUpperCase(CommonUtil.getLocale()));
            line.append(spaces.substring(0, mPrinterLineSize - footerLine.length()));
            BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);

        } else {
            String thankYou = mActivity.getString(R.string.print_thank_you);

            line.setLength(0);
            line.append(thankYou);
            line.append(spaces.substring(0, mPrinterLineSize - thankYou.length()));
        }

        BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
	}

    public static void printFeed() throws Exception {

        //BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
        BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
        BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
	}

    public static void printOrder(Orders order) throws Exception {

		BluetoothPrintDriver.WakeUpPritner();
		
		BluetoothPrintDriver.Begin();

		if (isMiniFont()) {
			BluetoothPrintDriver.SetCharacterFont((byte) 0x01);
		}

		String spaces = "                                        ";
		String divider = "----------------------------------------";
		
		StringBuffer line = new StringBuffer();
		
		Merchant merchant = MerchantUtil.getMerchant();
		
		line.append(merchant.getName().toUpperCase(CommonUtil.getLocale()));
		
		line.append(spaces.substring(0, mPrinterLineSize - merchant.getName().length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		String addrAndTelp = merchant.getAddress();
		
		if (!CommonUtil.isEmpty(merchant.getTelephone())) {
			 addrAndTelp += mActivity.getString(R.string.print_telephone) + merchant.getTelephone();
		}
		
		String[] addrAndTelps = addrAndTelp.split(Constant.SPACE_STRING);
		
		String addrAndTlpLine = Constant.EMPTY_STRING;
		
		for (String str : addrAndTelps) {
			
			if (addrAndTlpLine.length() + str.length() < mPrinterLineSize) {
				
				addrAndTlpLine = addrAndTlpLine + str + Constant.SPACE_STRING; 
			
			} else {
				
				line.setLength(0);
				line.append(addrAndTlpLine.toUpperCase(CommonUtil.getLocale()));
				line.append(spaces.substring(0, mPrinterLineSize - addrAndTlpLine.length()));
				
				BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
				
				addrAndTlpLine = str + Constant.SPACE_STRING;
			}
		}
		
		line.setLength(0);
		line.append(addrAndTlpLine.toUpperCase(CommonUtil.getLocale()));
		line.append(spaces.substring(0, mPrinterLineSize - addrAndTlpLine.length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		String transDateText = CommonUtil.formatDateTime(order.getOrderDate()); 
		
		line.setLength(0);
		line.append(transDateText);
		line.append(spaces.substring(0, mPrinterLineSize - transDateText.length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		if (order.getEmployee() != null) {
			
			String waitressText = mActivity.getString(R.string.print_waitress) + order.getEmployee().getName();
			
			line.setLength(0);
			line.append(waitressText);
			line.append(spaces.substring(0, mPrinterLineSize - waitressText.length()));
			BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
			
		} else {
			
			String cashierText = mActivity.getString(R.string.print_cashier) + UserUtil.getUser().getName();
			
			line.setLength(0);
			line.append(cashierText);
			line.append(spaces.substring(0, mPrinterLineSize - cashierText.length()));
			BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		}
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		String orderType = mActivity.getString(R.string.print_order_type) + CodeUtil.getOrderTypeLabel(order.getOrderType());
		
		if (!Constant.TXN_ORDER_TYPE_SERVICE.equals(order.getOrderType())) {
			
			line.setLength(0);
			line.append(orderType);
			line.append(spaces.substring(0, mPrinterLineSize - orderType.length()));
			BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		}
		
		String orderReference = Constant.EMPTY_STRING;
		
		if (Constant.TXN_ORDER_TYPE_DINE_IN.equals(order.getOrderType())) {
			orderReference = mActivity.getString(R.string.print_table_no) + order.getOrderReference();
		} else {
			orderReference = mActivity.getString(R.string.print_customer) + order.getOrderReference();
		}
		
		line.setLength(0);
		line.append(orderReference);
		line.append(spaces.substring(0, mPrinterLineSize - orderReference.length()));
		BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
		
		BluetoothPrintDriver.BT_Write(divider.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		
		List<OrderItem> orderItems = order.getOrderItemList();
		
		for (OrderItem item : orderItems) {
			
			if (item.getQuantity() != 0) {
			
				String productName = item.getProductName();
				
				if (productName.length() > mPrinterLineSize) {
					productName = productName.substring(0, mPrinterLineSize);
				}
				
				float quantity = item.getQuantity();
				
				line.setLength(0);
				
				line.append(productName);
				line.append(spaces.substring(0, mPrinterLineSize - productName.length()));
				
				String quantityStr = Constant.SPACE_STRING + CommonUtil.formatNumber(quantity);
				
				line.setLength(mPrinterLineSize - quantityStr.length());
				line.append(quantityStr);
				
				BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
				
				if (!CommonUtil.isEmpty(item.getRemarks())) {
					
					String remarks = mActivity.getString(R.string.print_remarks) + item.getRemarks();
					
					line.setLength(0);
					line.append(remarks);
					line.append(spaces.substring(0, mPrinterLineSize - remarks.length()));
					
					BluetoothPrintDriver.BT_Write(line.toString() + Constant.CR_STRING);
				}
			}
		}
		
		BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
		BluetoothPrintDriver.BT_Write(spaces.substring(0, mPrinterLineSize) + Constant.CR_STRING);
	}
}
