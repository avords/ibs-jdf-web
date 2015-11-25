package com.handpay.ibenefit.framework.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil {

	protected static final int WIDTH_MULT = 300;
	protected static final int MIN_CHARS = 8;
	private static final int MIN_WIDTH = WIDTH_MULT * MIN_CHARS;
	protected static final double NON_NUMERIC = -.99999;
	protected static final int MAX_ROWS = 65536;

	protected HSSFWorkbook wb;
	protected HSSFSheet sheet;
	protected int rownum;
	protected int cellnum;
	protected HSSFRow hssfRow;

	public int createExcelFile(List<Object[]> datas, OutputStream outputStream, String[] titles) throws Exception {
		try {
			wb = new HSSFWorkbook();
			int sheetNum = wb.getNumberOfSheets();
			boolean getSheet = false;
			for (int i = 0; i < sheetNum; i++) {
				sheet = wb.getSheetAt(i);
				if (sheet.getLastRowNum() != MAX_ROWS) {
					rownum = sheet.getLastRowNum() + 1;
					getSheet = true;
					break;
				}
			}
			if (!getSheet) {
				sheet = wb.createSheet();
				rownum = 0;
				sheet.setAutobreaks(true);
			}

			createBody(datas, titles);
			wb.write(outputStream);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
				wb = null;
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public void createBody(List<Object[]> dataList, String[] titles) throws Exception {
		createTitle(titles);
		for (int i = 0, n = dataList.size(); i < n; i++) {
			if (MAX_ROWS-1 == rownum) {
				sheet = wb.createSheet();
				rownum = 0;
				sheet.setAutobreaks(true);
				createTitle(titles);
			}
			hssfRow = sheet.createRow(rownum + 1);
			cellnum = 0;
			Object[] objs = dataList.get(i);
			for (Object obj : objs) {
				if (obj == null || "null".equals(obj)) {
					obj = "";
				}
				createCell(obj.toString());
			}
			rownum++;
		}
	}

	private void createTitle(String[] titles) {
		cellnum = 0;
		hssfRow = sheet.createRow(rownum);
		for (int j = 0; j < titles.length; j++) {
			createCell(titles[j].toString());
		}
	}

	public void createCell(String value) {
		HSSFCell hssfCell = hssfRow.createCell(cellnum);
		fixWidthAndPopulate(hssfCell, value);
		cellnum++;
	}

	private void fixWidthAndPopulate(HSSFCell cell, String value) {
		cell.setCellValue(value);
		int valWidth = value.length() * WIDTH_MULT;
		if (valWidth < MIN_WIDTH) {
			valWidth = MIN_WIDTH;
		}
		if (valWidth > sheet.getColumnWidth(cell.getCellNum())) {
			sheet.setColumnWidth(cell.getCellNum(), (short) valWidth);
		}
	}

	public void exportExcel(HttpServletResponse response, List<Object[]> datas,
			String[] titles, String exportName) throws IOException, Exception {
		FileUpDownUtils.setDownloadResponseHeaders(response, exportName);
		OutputStream outputStream = response.getOutputStream();
		createExcelFile(datas, outputStream, titles);
	}

}
