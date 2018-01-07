package com.tokoku.pos.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by Radix on 6/8/2017.
 */

public class PoiUtil {

    public static HSSFWorkbook getWorkbook() {

        return new HSSFWorkbook();
    }

    public static CellStyle getHeaderCellStyle(Workbook wb) {

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.BLACK.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cs.setWrapText(true);

        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.WHITE.index);
        cs.setFont(font);

        return cs;
    }

    public static CellStyle getContentCellStyle(Workbook wb) {

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cs.setWrapText(true);

        cs.setBorderBottom((short) 1);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);

        cs.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);

        return cs;
    }

    public static CellStyle getContentNumberCellStyle(Workbook wb) {

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cs.setAlignment(CellStyle.ALIGN_RIGHT);
        cs.setWrapText(true);

        cs.setBorderBottom((short) 1);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);

        cs.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);

        CreationHelper ch = wb.getCreationHelper();
        cs.setDataFormat(ch.createDataFormat().getFormat("#,##0.00;\\-#,##0.00"));

        return cs;
    }

    public static CellStyle getBottomCellStyle(Workbook wb) {

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cs.setWrapText(true);

        cs.setBorderBottom((short) 1);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);

        cs.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);

        return cs;
    }

    public static CellStyle getBottomNumberCellStyle(Workbook wb) {

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cs.setAlignment(CellStyle.ALIGN_RIGHT);
        cs.setWrapText(true);

        cs.setBorderBottom((short) 1);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);

        cs.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);

        CreationHelper ch = wb.getCreationHelper();
        cs.setDataFormat(ch.createDataFormat().getFormat("#,##0.00;\\-#,##0.00"));

        return cs;
    }

    public static Sheet getSheet(Workbook wb, String title) {

        Sheet sheet = null;
        sheet = wb.createSheet(title);
        sheet.setDefaultRowHeight((short) 300);

        return sheet;
    }
}
