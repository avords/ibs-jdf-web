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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Export;
import org.extremecomponents.table.core.TableModel;

/**
 * For producing a delimited datafile view of the table. Default delimiter is
 * comma.
 * 
 * @author Brad Parks
 */
public class ExtendCsvView implements View {
    public final static String DELIMITER = "delimiter";
    final static String DEFAULT_DELIMITER = ",";
    private StringBuilder plainData = new StringBuilder();

    public void beforeBody(TableModel model) {
    	 Export export = model.getExportHandler().getCurrentExport();
         String delimiter = export.getAttributeAsString(DELIMITER);
         if (StringUtils.isBlank(delimiter)) {
             delimiter = DEFAULT_DELIMITER;
         }
         
    	List columns = model.getColumnHandler().getHeaderColumns();
        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            Column column = (Column) iter.next();
            String title = column.getCellDisplay();
            title = title.replaceAll("\t","").replaceAll("\n","").replaceAll("\r","");
            plainData.append("\"");
            plainData.append(title.trim());
            plainData.append("\"");
            plainData.append(delimiter);
            if (column.isLastColumn()) {
                plainData.append("\r\n");
            }
        }
    }

    public void body(TableModel model, Column column) {
        Export export = model.getExportHandler().getCurrentExport();
        String delimiter = export.getAttributeAsString(DELIMITER);
        if (StringUtils.isBlank(delimiter)) {
            delimiter = DEFAULT_DELIMITER;
        }
        String value = column.getCellDisplay();
        if(StringUtils.isNumeric(value) && (!column.getCell().equals("org.extremecomponents.table.cell.ExtendRowCountCell")
        		||!column.getCell().equals("org.extremecomponents.table.cell.RowCountCell"))){
        	value = column.getValue()==null?"":column.getValue().toString();
        }
        value = value.replaceAll("\t","").replaceAll("\n","").replaceAll("\r","");
        value = ExportViewUtils.parseCSV(value);
        plainData.append("\"");
        plainData.append(value.trim());
        plainData.append("\"");
        plainData.append(delimiter);
        if (column.isLastColumn()) {
            plainData.append("\r\n");
        }
    }

    public Object afterBody(TableModel model) {
        return plainData.toString();
    }
}
