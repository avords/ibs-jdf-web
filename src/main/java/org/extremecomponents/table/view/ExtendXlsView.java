/*
 * Copyright 2004 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.extremecomponents.table.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.core.PreferencesConstants;
import org.extremecomponents.table.core.TableModel;

/**
 * com.extremecomp.table.view.XlsView.java -
 *
 * @author paul horn
 */
public class ExtendXlsView implements View {
    private static Log logger = LogFactory.getLog(ExtendXlsView.class);
    public static final int WIDTH_MULT = 240; // width per char
    public static final int MIN_CHARS = 8; // minimum char width
    public static final short DEFAULT_FONT_HEIGHT = 8;
    public static final double NON_NUMERIC = -.99999;
    public static final String DEFAULT_MONEY_FORMAT = "$###,###,##0.00";
    public static final String DEFAULT_PERCENT_FORMAT = "##0.0%";
    public static final String NBSP = "&nbsp;";

    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private HSSFPrintSetup ps;
    private Map styles;
    private short rownum;
    private short cellnum;
    private HSSFRow hssfRow;
    private String moneyFormat;
    private String percentFormat;

    public ExtendXlsView() {
    }

    public void beforeBody(TableModel model) {
        logger.debug("XlsView.init()");

        moneyFormat = model.getPreferences().getPreference(PreferencesConstants.TABLE_EXPORTABLE + "format.money");
        if (StringUtils.isEmpty(moneyFormat)) {
            moneyFormat = DEFAULT_MONEY_FORMAT;
        }
        percentFormat = model.getPreferences().getPreference(PreferencesConstants.TABLE_EXPORTABLE + "format.percent");
        if (StringUtils.isEmpty(percentFormat)) {
            percentFormat = DEFAULT_PERCENT_FORMAT;
        }

        wb = new HSSFWorkbook();
        sheet = wb.createSheet();
        wb.setSheetName(0, "Export Workbook");

        styles = initStyles(wb);
        ps = sheet.getPrintSetup();

        sheet.setAutobreaks(true);
        ps.setFitHeight((short) 1);
        ps.setFitWidth((short) 1);

        createHeader(model);
    }

    public void body(TableModel model, Column column) {
        if (column.isFirstColumn()) {
            rownum++;
            cellnum = 0;
            hssfRow = sheet.createRow(rownum);
        }
        String value = column.getCellDisplay();
        if(StringUtils.isNumeric(value) && !column.getCell().equals("org.extremecomponents.table.cell.ExtendRowCountCell")){
        	value = column.getValue()==null?"":column.getValue().toString();
        }
        value = value.replaceAll("\t","").replaceAll("\n","");
        value =	ExportViewUtils.parseXLS(value);

        HSSFCell hssfCell = hssfRow.createCell(cellnum);

        //modify by springside
        //hssfCell.setEncoding(HSSFCell.ENCODING_UTF_16);
        //end of modify by springside

        if (column.isEscapeAutoFormat()){
            writeToCellAsText(hssfCell, value);
        }
        else{
            writeToCellFormatted(hssfCell, value);
        }
        cellnum++;
    }

    public Object afterBody(TableModel model) {
        return wb;
    }

    private void createHeader(TableModel model) {
        rownum = 0;
        cellnum = 0;
        HSSFRow row = sheet.createRow(rownum);

        List columns = model.getColumnHandler().getHeaderColumns();
        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            Column column = (Column) iter.next();
            String title = column.getCellDisplay();
            HSSFCell cell = row.createCell(cellnum);

            //modify by springside
            //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            //end of modify by springside

            cell.setCellStyle((HSSFCellStyle) styles.get("titleStyle"));
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(title);
            int valWidth = (title + "").length() * WIDTH_MULT;
            sheet.setColumnWidth(cell.getCellNum(), (short) valWidth);

            cellnum++;
        }
    }

    private void writeToCellAsText(HSSFCell cell, String value){
        // format text
        if (value.trim().equals(NBSP)) {
            value = "";
        }
        cell.setCellStyle((HSSFCellStyle) styles.get("textStyle"));
        fixWidthAndPopulate(cell, NON_NUMERIC, value);
    }


    private void writeToCellFormatted(HSSFCell cell, String value) {
        double numeric = NON_NUMERIC;

//        try {
//            numeric = Double.parseDouble(value);
//        } catch (Exception e) {
//            numeric = NON_NUMERIC;
//        }

        if (value.startsWith("$") || value.endsWith("%") || value.startsWith("($")) {
            boolean moneyFlag = (value.startsWith("$") || value.startsWith("($"));
            boolean percentFlag = value.endsWith("%");

            value = StringUtils.replace(value, "$", "");
            value = StringUtils.replace(value, "%", "");
            value = StringUtils.replace(value, ",", "");
            value = StringUtils.replace(value, "(", "-");
            value = StringUtils.replace(value, ")", "");

            try {
                numeric = Double.parseDouble(value);
            } catch (Exception e) {
                numeric = NON_NUMERIC;
            }

            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

            if (moneyFlag) {
                // format money
                cell.setCellStyle((HSSFCellStyle) styles.get("moneyStyle"));
            } else if (percentFlag) {
                // format percent
                numeric = numeric / 100;
                cell.setCellStyle((HSSFCellStyle) styles.get("percentStyle"));
            }
        } else if (numeric != NON_NUMERIC) {
            // format numeric
            cell.setCellStyle((HSSFCellStyle) styles.get("numericStyle"));
        } else {
            // format text
            if (value.trim().equals(NBSP)) {
                value = "";
            }
            cell.setCellStyle((HSSFCellStyle) styles.get("textStyle"));
        }

        fixWidthAndPopulate(cell, numeric, value);
    }

    private void fixWidthAndPopulate(HSSFCell cell, double numeric, String value) {
        int valWidth = 0;

        if (numeric != NON_NUMERIC) {
            cell.setCellValue(numeric);
            valWidth = (cell.getNumericCellValue() + "$,.").length() * WIDTH_MULT;
        } else {
            cell.setCellValue(value);
            valWidth = (cell.getStringCellValue() + "").length() * WIDTH_MULT;

            if (valWidth < (WIDTH_MULT * MIN_CHARS)) {
                valWidth = WIDTH_MULT * MIN_CHARS;
            }
        }

        if (valWidth > sheet.getColumnWidth(cell.getCellNum())) {
            sheet.setColumnWidth(cell.getCellNum(), (short) valWidth);
        }
    }

    private Map initStyles(HSSFWorkbook wb) {
        return initStyles(wb, DEFAULT_FONT_HEIGHT);
    }

    private Map initStyles(HSSFWorkbook wb, short fontHeight) {
        Map result = new HashMap();
        HSSFCellStyle titleStyle = wb.createCellStyle();
        HSSFCellStyle textStyle = wb.createCellStyle();
        HSSFCellStyle boldStyle = wb.createCellStyle();
        HSSFCellStyle numericStyle = wb.createCellStyle();
        HSSFCellStyle numericStyleBold = wb.createCellStyle();
        HSSFCellStyle moneyStyle = wb.createCellStyle();
        HSSFCellStyle moneyStyleBold = wb.createCellStyle();
        HSSFCellStyle percentStyle = wb.createCellStyle();
        HSSFCellStyle percentStyleBold = wb.createCellStyle();

        result.put("titleStyle", titleStyle);
        result.put("textStyle", textStyle);
        result.put("boldStyle", boldStyle);
        result.put("numericStyle", numericStyle);
        result.put("numericStyleBold", numericStyleBold);
        result.put("moneyStyle", moneyStyle);
        result.put("moneyStyleBold", moneyStyleBold);
        result.put("percentStyle", percentStyle);
        result.put("percentStyleBold", percentStyleBold);

        HSSFDataFormat format = wb.createDataFormat();

        // Global fonts
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setColor(HSSFColor.BLACK.index);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints(fontHeight);

        HSSFFont fontBold = wb.createFont();
        fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        fontBold.setColor(HSSFColor.BLACK.index);
        fontBold.setFontName(HSSFFont.FONT_ARIAL);
        fontBold.setFontHeightInPoints(fontHeight);

        // Money Style
        moneyStyle.setFont(font);
        moneyStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        moneyStyle.setDataFormat(format.getFormat(moneyFormat));

        // Money Style Bold
        moneyStyleBold.setFont(fontBold);
        moneyStyleBold.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        moneyStyleBold.setDataFormat(format.getFormat(moneyFormat));

        // Percent Style
        percentStyle.setFont(font);
        percentStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        percentStyle.setDataFormat(format.getFormat(percentFormat));

        // Percent Style Bold
        percentStyleBold.setFont(fontBold);
        percentStyleBold.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        percentStyleBold.setDataFormat(format.getFormat(percentFormat));

        // Standard Numeric Style
        numericStyle.setFont(font);
        numericStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

        // Standard Numeric Style Bold
        numericStyleBold.setFont(fontBold);
        numericStyleBold.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

        // Title Style
        titleStyle.setFont(font);
        titleStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        titleStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleStyle.setRightBorderColor(HSSFColor.BLACK.index);
        titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleStyle.setTopBorderColor(HSSFColor.BLACK.index);
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // Standard Text Style
        textStyle.setFont(font);
        textStyle.setWrapText(true);

        // Standard Text Style
        boldStyle.setFont(fontBold);
        boldStyle.setWrapText(true);

        return result;
    }



}
