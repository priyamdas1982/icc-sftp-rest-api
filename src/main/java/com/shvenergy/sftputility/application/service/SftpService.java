package com.shvenergy.sftputility.application.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.Map;

import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.shvenergy.sftputility.application.configuration.SftpConfig;

@Service
public class SftpService {

	@Autowired
	private SftpConfig sftpConfig;


	

	public void downloadFiles(String host, String remotePath, String filepattern,
			String userName, String password, ByteArrayOutputStream outputStream, String boundary) throws Exception {
		JSch jsch = new JSch();
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Session session = jsch.getSession(userName, host, sftpConfig.getPort());
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();

		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		channelSftp.connect();

		/*
		 * InputStream inputStream = channelSftp.get(remotePath);
		 * System.out.print(inputStream.read());
		 */

		channelSftp.cd(remotePath);
		Vector<ChannelSftp.LsEntry> files = channelSftp.ls("*" + filepattern + "*"); // List all files with .txt
																						// extension

		for (ChannelSftp.LsEntry file : files) {
			String fileName = file.getFilename();
			System.out.println("Fetching: " + fileName);

			
		}
		Encoder base64Encoder = Base64.getEncoder();
		


        for (ChannelSftp.LsEntry file  : files) {
        	
        	
        	channelSftp.get(file.getFilename(), baos);

			byte[] fileContent = baos.toByteArray();
			
             outputStream.write(("--" + boundary + "\r\n").getBytes());
             outputStream.write(("Content-Type: " + getContentTypeFromFileName(file.getFilename()) + "\r\n").getBytes());
             outputStream.write(("Content-Disposition: attachment; filename=\"" + file.getFilename() + "\"\r\n").getBytes());
             outputStream.write(("\r\n").getBytes());
             outputStream.write(base64Encoder.encodeToString(fileContent).getBytes());
             outputStream.write(("\r\n").getBytes());
        }
        
        outputStream.write(("--" + boundary + "--").getBytes());
		channelSftp.disconnect();
		session.disconnect();

		

		// Similar to your download logic, using sftpConfig values
	}

	public void uploadFile(String remotePath, InputStream fileStream) throws Exception {
		// Upload logic using sftpConfig values
	}
	
	
    public static String detectContentType(String filePath) throws IOException {
    	String mimeType = "";
    	try {
        Path path = Paths.get(filePath);
        mimeType = Files.probeContentType(path);

        if (mimeType == null) {
            // If probeContentType doesn't return a value, use URLConnection
            File file = new File(filePath);
            URLConnection connection = file.toURI().toURL().openConnection();
            mimeType = connection.getContentType();
        }
    	}catch (IOException e) {
    		mimeType="text/plain";
        }
        return mimeType;
    }
    
    
    public static String getContentTypeFromFileName(String fileName) {
        String extension = getExtension(fileName);
        
        // Manual mapping of extensions to MIME types
        Map<String, String> mimeTypes = new HashMap();
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("xml", "application/xml");
        mimeTypes.put("txt", "text/plain");
        // Add more mappings as needed
        
        // Lookup the MIME type based on the extension
        return mimeTypes.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }

    public static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

}
