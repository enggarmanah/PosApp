package com.tokoku.pos.util;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by Radix on 6/8/2017.
 */

public class PoiUtil {

    public static XSSFWorkbook getWorkbook() {

        return new XSSFWorkbook();
    }

    public static XSSFCellStyle getHeaderCellStyle(XSSFWorkbook wb) {

        //Cell style for header row
        XSSFCellStyle cs = wb.createCellStyle();

        byte[] rgb = new byte[3];
        rgb[0] = (byte) 52; // red
        rgb[1] = (byte) 84; // green
        rgb[2] = (byte) 148; // blue

        XSSFColor myColor = new XSSFColor();
        myColor.setRGB(rgb);

        cs.setFillForegroundColor(myColor);

        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);

        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.WHITE.index);
        cs.setFont(font);

        return cs;
    }

    public static XSSFCellStyle getContentCellStyle(XSSFWorkbook wb) {

        //Cell style for header row
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);

        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);

        cs.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);

        return cs;
    }

    public static XSSFCellStyle getContentNumberCellStyle(XSSFWorkbook wb) {

        //Cell style for header row
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        cs.setWrapText(true);

        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);

        cs.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);

        CreationHelper ch = wb.getCreationHelper();
        cs.setDataFormat(ch.createDataFormat().getFormat("#,##0.00;\\-#,##0.00"));

        return cs;
    }

    public static XSSFCellStyle getBottomCellStyle(XSSFWorkbook wb) {

        //Cell style for header row
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setWrapText(true);

        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);

        cs.setBottomBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setLeftBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setRightBorderColor(HSSFColor.GREY_50_PERCENT.index);
        cs.setTopBorderColor(HSSFColor.GREY_50_PERCENT.index);

        return cs;
    }

    public static XSSFCellStyle getBottomNumberCellStyle(XSSFWorkbook wb) {

        //Cell style for header row
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setAlignment(HorizontalAlignment.RIGHT);
        cs.setWrapText(true);

        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);

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
