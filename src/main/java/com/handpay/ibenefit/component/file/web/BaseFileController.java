package com.handpay.ibenefit.component.file.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.handpay.ibenefit.base.file.service.IFileManager;
import com.handpay.ibenefit.framework.util.FileUpDownUtils;
import com.handpay.ibenefit.framework.util.UploadFile;

@Controller
@RequestMapping("/baseFile")
public class BaseFileController {
	private static final String BASE_DIR = "framework/";
	@Reference(version="1.0")
	private IFileManager fileManager;

	@RequestMapping("/uploadAndDownload")
	@ResponseBody
	public String uploadAndDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UploadFile uploadFile = FileUpDownUtils.getUploadFile(request, "uploadFile");
		byte[] fileData = FileUpDownUtils.getFileContent(uploadFile.getFile());
		String filePath = fileManager.saveContentFile(fileData,uploadFile.getFileName());
		return filePath;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public String uploadAndDownload(HttpServletRequest request, String relativePath) throws Exception {
		boolean result = fileManager.deleteFile(relativePath);
		return String.valueOf(result);
	}

}
