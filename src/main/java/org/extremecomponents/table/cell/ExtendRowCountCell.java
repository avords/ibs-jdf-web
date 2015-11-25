package org.extremecomponents.table.cell;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.core.TableModel;

public class ExtendRowCountCell extends AbstractCell {
	
    protected String getCellValue(TableModel model, Column column) {
    	//每页从新开始计数
        int rowcount = model.getRowHandler().getRow().getRowCount();
        return String.valueOf(rowcount);
    }
}
