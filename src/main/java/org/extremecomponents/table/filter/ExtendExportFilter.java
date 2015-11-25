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
package org.extremecomponents.table.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.handpay.ibenefit.framework.util.FileUpDownUtils;

/**
 * Export extend:fix file name encoding problem
 */
public class ExtendExportFilter extends ExportFilter {
	 protected void doFilterInternal(ServletRequest servletRequest, ServletResponse response, FilterChain chain, String exportFileName) throws IOException, ServletException {
		 if ((servletRequest instanceof HttpServletRequest)) {
				HttpServletRequest request = (HttpServletRequest) servletRequest;
				//encode
				exportFileName = FileUpDownUtils.encodeDownloadFileName(request, exportFileName);
		 }
		 super.doFilterInternal(servletRequest, response, chain,exportFileName);
	 }
}
