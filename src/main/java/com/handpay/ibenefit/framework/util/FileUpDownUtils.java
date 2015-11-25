package com.handpay.ibenefit.framework.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.handpay.ibenefit.framework.config.GlobalConfig;

public class FileUpDownUtils {

	private static final Logger LOGGER = Logger.getLogger(FileUpDownUtils.class);

	protected static final String TMP_DIR = GlobalConfig.getTempDir();
	protected static final String DEFAULT_FILE_PARAM = "uploadFile";

	
	public static String encodeDownloadFileName(HttpServletRequest request,String fileName){
        String agent = request.getHeader("USER-AGENT");    
        if (null != agent){
        	try{
        		if (-1 != agent.indexOf("Firefox")) {//Firefox    
        			fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8"))))+ "?=";    
                }else if (-1 != agent.indexOf("Chrome")) {//Chrome   
                	fileName = new String(fileName.getBytes(), "ISO8859-1");    
                } else {//IE7+    
                	fileName = java.net.URLEncoder.encode(fileName, "UTF-8");    
                	fileName = StringUtils.replace(fileName, "+", "%20");//替换空格    
                }    
        	}catch (Exception e){
        	}
        }
    	return fileName;
	}

	
    public static void setDownloadResponseHeaders(HttpServletResponse response, String exportFileName) {
        String mimeType = MimeUtils.getFileMimeType(exportFileName);
        if (StringUtils.isNotBlank(mimeType)) {
            response.setContentType(mimeType);
        }else{
        	LOGGER.info("Undefined file type : " + exportFileName , null);
        	mimeType = MimeUtils.getFileMimeType("txt.txt");
        	response.setContentType(mimeType);
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + exportFileName + "\"");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
    }

    public static String getTempFileName(String original){
    	DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return dateFormat.format(new Date()) + "_" + original;
    }
    
    public static UploadFile getUploadFile(HttpServletRequest request) throws IOException {
    	return getUploadFile(request, DEFAULT_FILE_PARAM);
    }

	public static UploadFile getUploadFile(HttpServletRequest request,String parameterName) throws IOException {
		MultipartHttpServletRequest multipartRequest = null;
		UploadFile uploadFile = null;
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
		} catch (ClassCastException e) {
			LOGGER.error("getUploadFile()", e);
			return null;
		}
		MultipartFile multipartFile = multipartRequest.getFile(parameterName);
		if (multipartFile != null && StringUtils.isNotBlank(multipartFile.getOriginalFilename())) {
			String fileName = multipartFile.getOriginalFilename();
			String fileRealPath = TMP_DIR + File.separator + getTempFileName(fileName);
			File file = new File(fileRealPath);
			file.delete();
			multipartFile.transferTo(file);
			uploadFile = new UploadFile(fileName,file);
		}
		return uploadFile;
	}

	public static byte[] getFileContent(File file) throws Exception {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] temp = new byte[1024];
		int size = 0;
		while ((size = in.read(temp)) != -1) {
			out.write(temp, 0, size);
		}
		in.close();
		return  out.toByteArray();
	}


	public static List<UploadFile> getUploadFiles(HttpServletRequest request) throws IOException {
		MultipartHttpServletRequest multipartRequest = null;
		List<UploadFile> result = new ArrayList<UploadFile>();
		try {
			multipartRequest = (MultipartHttpServletRequest) request;
		    for (Iterator it = multipartRequest.getFileNames(); it.hasNext();) {
		        String key = (String)it.next();
		        MultipartFile multipartFile = multipartRequest.getFile(key);
		        if (multipartFile != null && StringUtils.isNotBlank(multipartFile.getOriginalFilename())) {
					String fileName = multipartFile.getOriginalFilename();
					String fileRealPath = TMP_DIR + File.separator + getTempFileName(fileName);;
					File file = new File(fileRealPath);
					file.delete();
					multipartFile.transferTo(file);
					UploadFile uploadFile = new UploadFile(fileName,file);
					result.add(uploadFile);
				}
		    }
		} catch (ClassCastException e) {
			LOGGER.error("getUploadFiles()", e);
		}
		return result;
	}

	
	public static void downloadFile(File file,HttpServletResponse response) throws Exception {
		if(!file.exists()){
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		InputStream inputStream =new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int n;
		while( -1!=(n = inputStream.read(buffer))) {
			response.getOutputStream().write(buffer,0,n);
		}
		response.getOutputStream().flush();
		inputStream.close();
	}

	
	public static void uploadFileToDisk(HttpServletRequest request,String uploadFileSaveDir) throws IOException{
		File toPath = new File(uploadFileSaveDir);
		uploadFileToDisk(request, toPath);
	}

	
	public static void uploadFileToDisk(HttpServletRequest request,File uploadFileSaveDir) throws IOException{
		uploadFileToDisk(request, uploadFileSaveDir, DEFAULT_FILE_PARAM);
	}

	public static void uploadFileToDisk(HttpServletRequest request,File uploadFileSaveDir,String parameterName) throws IOException{
		UploadFile file = getUploadFile(request,parameterName);
		File toPath = uploadFileSaveDir;
		if (!toPath.isDirectory()) {
			if (!toPath.mkdirs()) {
				throw new IOException("Failed create directory " + toPath );
			}
		}
		FileUtils.copyFile(file.getFile(), new File(toPath + "/" + file.getFileName()));
	}

}
