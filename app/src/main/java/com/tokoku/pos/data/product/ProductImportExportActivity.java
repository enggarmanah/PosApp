package com.tokoku.pos.data.product;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.android.pos.dao.Merchant;
import com.android.pos.dao.Product;
import com.android.pos.dao.ProductGroup;
import com.tokoku.pos.Constant;
import com.tokoku.pos.R;
import com.tokoku.pos.async.ProgressDlgFragment;
import com.tokoku.pos.base.activity.BaseActivity;
import com.tokoku.pos.common.ConfirmListener;
import com.tokoku.pos.dao.ProductDaoService;
import com.tokoku.pos.dao.ProductGroupDaoService;
import com.tokoku.pos.util.CodeUtil;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.ConfirmationUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.NotificationUtil;
import com.tokoku.pos.util.PoiUtil;
import com.tokoku.pos.util.UserUtil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ProductImportExportActivity extends BaseActivity implements ConfirmListener, ProgressDlgFragment.ProgressListener {

    private ProductGroupDaoService productGroupDaoService;
    private ProductDaoService productDaoService;

    private static final int PICKFILE_RESULT_CODE = 1;
    private static boolean mIsStopProcess;

	private String mFilePath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_product_import_export);

        productGroupDaoService = new ProductGroupDaoService();
        productDaoService = new ProductDaoService();

        mProgressDialog.setProgressListener(this);

        initDrawerMenu();

		Button importBtn = (Button)findViewById(R.id.importBtn);
        Button exportBtn = (Button)findViewById(R.id.exportBtn);

        importBtn.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("file/*.xls|file/*.xlsx");
				startActivityForResult(intent,PICKFILE_RESULT_CODE);
			}});

        exportBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                ExportProductTask exportProductTask = new ExportProductTask();
                exportProductTask.execute();
            }});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode){
			case PICKFILE_RESULT_CODE:
				if(resultCode==RESULT_OK){
					mFilePath = data.getData().getPath();
					File file = new File(mFilePath);
                    ConfirmationUtil.confirmTask(getFragmentManager(), this, ConfirmationUtil.IMPORT_PRODUCT, "Import Product : " + file.getName());
				}
				break;

		}
	}

    private static Float getFloatFromCell(Cell cell) {

	    Float value = null;

        try {
            value = (float) cell.getNumericCellValue();
        } catch (Exception e) {}

        return  value;
    }

    private static String getStringFromCell(Cell cell) {

	    String value = null;

        try {
            value = cell.getStringCellValue();
        } catch (Exception e) {
            try {
                Long l = (long) cell.getNumericCellValue();
                value = l.toString();
            } catch (Exception ex) {}
        }
        
        return  value;
    }

    private static Date getDateFromCell(Cell cell) {

        Date value = null;

        try {
            value = cell.getDateCellValue();
        } catch (Exception e) {
        }

        return  value;
    }

    private static int getRowCount(Iterator<Row> rowIter) {

	    // ignore header
        int count = -1;

        while(rowIter.hasNext()) {

            count++;

            Row row = rowIter.next();

            try {

                Cell cell = row.getCell(0);
                String code = getStringFromCell(cell);

                if (code == null) {
                    break;
                }
            } catch (Exception e) {
            }
        }

        return  count;
    }

    private class ImportProductTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                mProgressDialog.setCancellationMessage(false);
                mProgressDialog.show(getFragmentManager(), progressDialogTag);

                try {
                    mProgressDialog.setMessage(getString(R.string.import_product));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mProgressDialog.setProgress(0);

                Merchant merchant = MerchantUtil.getMerchant();

                // Creating Input Stream
                File file = new File(params[0]);
                FileInputStream myInput = new FileInputStream(file);

                InputStream inp = new FileInputStream(file);

                Workbook wb = WorkbookFactory.create(inp);

                // Get the first sheet from workbook
                Sheet mySheet = wb.getSheetAt(0);

                /** We now need something to iterate through the cells.**/
                Iterator<Row> rowIter = mySheet.rowIterator();

                List<ProductGroup> productGroups = productGroupDaoService.getProductGroups();

                HashMap<String, ProductGroup> productGroupMap = new HashMap<String, ProductGroup>();
                for (ProductGroup productGroup : productGroups) {
                    productGroupMap.put(productGroup.getName(), productGroup);
                }

                int count = 0;
                boolean isValidHeader = true;

                // skip first row
                if (rowIter.hasNext()) {

                    Row row = rowIter.next();

                    int columnIndex = 0;

                    Cell cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_code).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_name).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_type).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_group).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_price_type).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    String priceLabel1 = !CommonUtil.isEmpty(merchant.getPriceLabel1()) && merchant.getPriceTypeCount() > 1 ? getString(R.string.field_price) + Constant.SPACE_STRING + merchant.getPriceLabel1() : getString(R.string.field_price);
                    String priceLabel2 = !CommonUtil.isEmpty(merchant.getPriceLabel2()) ? getString(R.string.field_price) + Constant.SPACE_STRING + merchant.getPriceLabel2() : Constant.EMPTY_STRING;
                    String priceLabel3 = !CommonUtil.isEmpty(merchant.getPriceLabel3()) ? getString(R.string.field_price) + Constant.SPACE_STRING + merchant.getPriceLabel3() : Constant.EMPTY_STRING;

                    cell = row.getCell(columnIndex++);
                    if (!priceLabel1.equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    if (merchant.getPriceTypeCount() >= 2) {
                        cell = row.getCell(columnIndex++);
                        if (!priceLabel2.equals(getStringFromCell(cell))) {
                            isValidHeader = false;
                        }
                    }

                    if (merchant.getPriceTypeCount() >= 3) {
                        cell = row.getCell(columnIndex++);
                        if (!priceLabel3.equals(getStringFromCell(cell))) {
                            isValidHeader = false;
                        }
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_cost_price).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_pic_required).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_commission_type).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_commission).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_promo_price).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_promo_start).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_promo_end).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_quantity_type).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_min_stock).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }

                    cell = row.getCell(columnIndex++);
                    if (!getString(R.string.field_status).equals(getStringFromCell(cell))) {
                        isValidHeader = false;
                    }
                }

                if (!isValidHeader) {

                    mProgressDialog.dismissAllowingStateLoss();
                    NotificationUtil.setAlertMessage(getFragmentManager(), getString(R.string.alert_import_invalid_file));
                    return false;
                }

                int rowCount = getRowCount(mySheet.rowIterator());
                mIsStopProcess = false;

                while(rowIter.hasNext() && !mIsStopProcess){

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    count++;

                    try {
                        mProgressDialog.setProgress(count*100/rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Row row = rowIter.next();

                    try {

                        int columnIndex = 0;

                        Cell cell = row.getCell(columnIndex++);
                        String code = getStringFromCell(cell);

                        cell = row.getCell(columnIndex++);
                        String name = getStringFromCell(cell);

                        if (code == null) {
                            break;
                        }

                        Product product = productDaoService.getProduct(code, name);

                        if (product == null) {
                            product = new Product();
                        }

                        Long merchantId = MerchantUtil.getMerchantId();
                        product.setMerchantId(merchantId);

                        product.setCode(code);
                        product.setName(name);

                        cell = row.getCell(columnIndex++);
                        String type = getStringFromCell(cell);
                        type = CodeUtil.getProductType(type);
                        product.setType(type);

                        cell = row.getCell(columnIndex++);
                        String productGroup = getStringFromCell(cell);
                        ProductGroup prdGrp = productGroupMap.get(productGroup);

                        if (prdGrp != null) {
                            product.setProductGroupId(prdGrp.getId());
                            product.setProductGroup(prdGrp);

                        } else if (!CommonUtil.isEmpty(productGroup)) {

                            ProductGroup newPrdGrp = new ProductGroup();

                            newPrdGrp.setMerchantId(merchantId);
                            newPrdGrp.setName(productGroup);
                            newPrdGrp.setStatus(Constant.STATUS_ACTIVE);

                            newPrdGrp.setUploadStatus(Constant.STATUS_YES);

                            String userId = UserUtil.getUser().getUserId();

                            if (newPrdGrp.getCreateBy() == null) {
                                newPrdGrp.setCreateBy(userId);
                                newPrdGrp.setCreateDate(new Date());
                            }

                            newPrdGrp.setUpdateBy(userId);
                            newPrdGrp.setUpdateDate(new Date());

                            productGroupDaoService.addProductGroup(newPrdGrp);
                            product.setProductGroupId(newPrdGrp.getId());
                            product.setProductGroup(newPrdGrp);

                            productGroupMap.put(newPrdGrp.getName(), newPrdGrp);
                        }

                        cell = row.getCell(columnIndex++);
                        String priceType = getStringFromCell(cell);
                        priceType = CodeUtil.getPriceType(priceType);
                        product.setPriceType(priceType);

                        cell = row.getCell(columnIndex++);
                        Float price1 = getFloatFromCell(cell);
                        product.setPrice1(price1);

                        if (merchant.getPriceTypeCount() >= 2) {
                            cell = row.getCell(columnIndex++);
                            Float price2 = getFloatFromCell(cell);
                            product.setPrice2(price2);
                        }

                        if (merchant.getPriceTypeCount() >= 3) {
                            cell = row.getCell(columnIndex++);
                            Float price3 = getFloatFromCell(cell);
                            product.setPrice3(price3);
                        }

                        cell = row.getCell(columnIndex++);
                        Float costPrice = getFloatFromCell(cell);
                        product.setCostPrice(costPrice);

                        cell = row.getCell(columnIndex++);
                        String picRequired = getStringFromCell(cell);
                        picRequired = CodeUtil.getPicRequired(picRequired);
                        product.setPicRequired(picRequired);

                        cell = row.getCell(columnIndex++);
                        String commissionType = getStringFromCell(cell);
                        commissionType = CodeUtil.getCommissionType(commissionType);
                        product.setCommisionType(commissionType);

                        cell = row.getCell(columnIndex++);
                        Float commission = getFloatFromCell(cell);
                        product.setCommision(commission);

                        cell = row.getCell(columnIndex++);
                        Float promoPrice = getFloatFromCell(cell);
                        product.setPromoPrice(promoPrice);

                        cell = row.getCell(columnIndex++);
                        Date promoStart = getDateFromCell(cell);
                        product.setPromoStart(promoStart);

                        cell = row.getCell(columnIndex++);
                        Date promoEnd = getDateFromCell(cell);
                        product.setPromoEnd(promoEnd);

                        cell = row.getCell(columnIndex++);
                        String quantityType = getStringFromCell(cell);
                        quantityType = CodeUtil.getQuantityType(quantityType);
                        product.setQuantityType(quantityType);

                        cell = row.getCell(columnIndex++);
                        Float minStock = getFloatFromCell(cell);
                        product.setMinStock(minStock);

                        cell = row.getCell(columnIndex++);
                        String status = getStringFromCell(cell);
                        status = CodeUtil.getProductStatus(status);
                        product.setStatus(status);

                        product.setUploadStatus(Constant.STATUS_YES);

                        String userId = UserUtil.getUser().getUserId();

                        if (product.getCreateBy() == null) {
                            product.setCreateBy(userId);
                            product.setCreateDate(new Date());
                        }

                        product.setUpdateBy(userId);
                        product.setUpdateDate(new Date());

                        if (product.getId() == null) {
                            productDaoService.addProduct(product);
                            //mProgressDialog.setMessage(getString(R.string.import_add_product, product.getName()));
                            System.out.println(count + ". add product : " + product.getCode() + " " + product.getName());
                        } else {
                            productDaoService.updateProduct(product);
                            //mProgressDialog.setMessage(getString(R.string.import_update_product, product.getName()));
                            System.out.println(count + ". update product : " + product.getCode() + " " + product.getName());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mProgressDialog.dismissAllowingStateLoss();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {}

        @Override
        protected void onPostExecute(Boolean result) {}
    }

    private class ExportProductTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            mProgressDialog.setCancellationMessage(false);
            mProgressDialog.show(getFragmentManager(), progressDialogTag);
            mProgressDialog.setMessage(getString(R.string.export_product));
            mProgressDialog.setProgress(0);

            Merchant merchant = MerchantUtil.getMerchant();

            try {

                //New Workbook
                XSSFWorkbook wb = PoiUtil.getWorkbook();

                Cell c = null;

                CellStyle headerCs = PoiUtil.getHeaderCellStyle(wb);
                CellStyle contentCs = PoiUtil.getContentCellStyle(wb);
                CellStyle contentNumberCs = PoiUtil.getContentNumberCellStyle(wb);

                //New Sheet
                Sheet sheet = PoiUtil.getSheet(wb, getString(R.string.product));

                int index = 0;
                int columnIndex = 0;

                // Generate column headings
                Row row = sheet.createRow(index++);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_code));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_name));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_type));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_group));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_price_type));
                c.setCellStyle(headerCs);

                String priceLabel1 = !CommonUtil.isEmpty(merchant.getPriceLabel1()) && merchant.getPriceTypeCount() > 1 ? Constant.SPACE_STRING + merchant.getPriceLabel1() : Constant.EMPTY_STRING;
                String priceLabel2 = !CommonUtil.isEmpty(merchant.getPriceLabel2()) ? Constant.SPACE_STRING + merchant.getPriceLabel2() : Constant.EMPTY_STRING;
                String priceLabel3 = !CommonUtil.isEmpty(merchant.getPriceLabel3()) ? Constant.SPACE_STRING + merchant.getPriceLabel3() : Constant.EMPTY_STRING;

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_price) + priceLabel1);
                c.setCellStyle(headerCs);

                if (merchant.getPriceTypeCount() >= 2) {
                    c = row.createCell(columnIndex++);
                    c.setCellValue(getString(R.string.field_price) + priceLabel2);
                    c.setCellStyle(headerCs);
                }

                if (merchant.getPriceTypeCount() >= 3) {
                    c = row.createCell(columnIndex++);
                    c.setCellValue(getString(R.string.field_price) + priceLabel3);
                    c.setCellStyle(headerCs);
                }

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_cost_price));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_pic_required));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_commission_type));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_commission));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_promo_price));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_promo_start));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_promo_end));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_quantity_type));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_min_stock));
                c.setCellStyle(headerCs);

                c = row.createCell(columnIndex++);
                c.setCellValue(getString(R.string.field_status));
                c.setCellStyle(headerCs);

                sheet.setColumnWidth(0, (8 * 500));
                sheet.setColumnWidth(1, (24 * 500));
                sheet.setColumnWidth(2, (8 * 500));
                sheet.setColumnWidth(3, (8 * 500));
                sheet.setColumnWidth(4, (8 * 500));
                sheet.setColumnWidth(5, (12 * 500));
                sheet.setColumnWidth(6, (12 * 500));
                sheet.setColumnWidth(7, (12 * 500));
                sheet.setColumnWidth(8, (12 * 500));
                sheet.setColumnWidth(9, (8 * 500));
                sheet.setColumnWidth(10, (8 * 500));
                sheet.setColumnWidth(11, (8 * 500));
                sheet.setColumnWidth(12, (8 * 500));
                sheet.setColumnWidth(13, (8 * 500));
                sheet.setColumnWidth(14, (8 * 500));
                sheet.setColumnWidth(15, (8 * 500));

                List<ProductGroup> productGroups = productGroupDaoService.getProductGroups();

                HashMap<Long, ProductGroup> productGroupMap = new HashMap<Long, ProductGroup>();
                for (ProductGroup productGroup : productGroups) {
                    productGroupMap.put(productGroup.getId(), productGroup);
                }

                int count = 0;

                List<Product> products = productDaoService.getProducts();
                mIsStopProcess = false;

                for (Product product : products) {

                    System.out.println("Export " + count + ". " + product.getName());

                    count++;
                    columnIndex = 0;

                    if (mIsStopProcess) {
                        return false;
                    }

                    try {
                        mProgressDialog.setProgress(count*100/products.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    row = sheet.createRow(index++);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(product.getCode());
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(product.getName());
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CodeUtil.getProductTypeLabel(product.getType()));
                    c.setCellStyle(contentCs);

                    ProductGroup productGroup = productGroupMap.get(product.getProductGroupId());

                    if (productGroup != null) {
                        c = row.createCell(columnIndex++);
                        c.setCellValue(productGroup.getName());
                        c.setCellStyle(contentCs);
                    } else {
                        c = row.createCell(columnIndex++);
                        c.setCellValue(getString(R.string.empty));
                        c.setCellStyle(contentCs);
                    }

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CodeUtil.getPriceTypeLabel(product.getPriceType()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CommonUtil.getNvlFloat(product.getPrice1()));
                    c.setCellStyle(contentNumberCs);

                    if (merchant.getPriceTypeCount() >= 2) {
                        c = row.createCell(columnIndex++);
                        c.setCellValue(CommonUtil.getNvlFloat(product.getPrice2()));
                        c.setCellStyle(contentNumberCs);
                    }

                    if (merchant.getPriceTypeCount() >= 3) {
                        c = row.createCell(columnIndex++);
                        c.setCellValue(CommonUtil.getNvlFloat(product.getPrice3()));
                        c.setCellStyle(contentNumberCs);
                    }

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CommonUtil.getNvlFloat(product.getCostPrice()));
                    c.setCellStyle(contentNumberCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CodeUtil.getPicRequiredLabel(product.getPicRequired()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CodeUtil.getCommissionTypeLabel(product.getCommisionType()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CommonUtil.getNvlFloat(product.getCommision()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CommonUtil.getNvlFloat(product.getPromoPrice()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CommonUtil.getNvlDate(product.getPromoStart()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CommonUtil.getNvlDate(product.getPromoEnd()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CodeUtil.getQuantityTypeLabel(product.getQuantityType()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CommonUtil.getNvlFloat(product.getMinStock()));
                    c.setCellStyle(contentCs);

                    c = row.createCell(columnIndex++);
                    c.setCellValue(CodeUtil.getProductStatusLabel(product.getStatus()));
                    c.setCellStyle(contentCs);
                }

                String subject = getString(R.string.product);

                // Create a path where we will place our List of objects on external storage
                File file = CommonUtil.generateReportFile(subject, wb);

                //startActivityForResult(CommonUtil.getActionViewIntent(file), 1);
                startActivityForResult(CommonUtil.getActionSendIntent(file, subject), 1);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgressDialog.dismissAllowingStateLoss();

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {}

        @Override
        protected void onPostExecute(Boolean result) {}
    }

    public void onConfirm(String task) {

        if (ConfirmationUtil.IMPORT_PRODUCT.equals(task)) {

            if (isStoragePermissionGranted()) {
                ImportProductTask importProductTask = new ImportProductTask();
                importProductTask.execute(mFilePath);
            }
         }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            ImportProductTask importProductTask = new ImportProductTask();
            importProductTask.execute(mFilePath);
        }
    }

    @Override
    public void onCancel() {

	    mIsStopProcess = true;
    }
}