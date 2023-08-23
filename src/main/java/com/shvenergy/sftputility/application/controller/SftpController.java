package com.shvenergy.sftputility.application.controller;

import java.io.ByteArrayOutputStream;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shvenergy.sftputility.application.service.SftpService;

@RestController
@RequestMapping("/sftp")
public class SftpController {

    @Autowired
    private SftpService sftpService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int KEY_LENGTH = 16;
	
	/*
	 * @GetMapping("/download") public ResponseEntity<InputStreamResource>
	 * downloadFileOld(@RequestParam String remotePath, @RequestParam String
	 * host,@RequestParam String userName, @RequestParam String password) { try {
	 * ArrayList<byte[]> fileStream = sftpService.downloadFile(remotePath,host,
	 * userName,password); InputStreamResource resource = new
	 * InputStreamResource(fileStream); return ResponseEntity.ok()
	 * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
	 * remotePath) .contentType(MediaType.APPLICATION_OCTET_STREAM) .body(resource);
	 * 
	 * 
	 * } catch (Exception e) { System.out.println(e); return
	 * ResponseEntity.badRequest().build(); } }
	 */
	 
    
    
    @GetMapping("/download-multiple")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String host,@RequestParam String remotePath, @RequestParam String filepattern,@RequestParam String userName, @RequestParam String password) {
        try {
        	
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        	String boundary = generateRandomKey(); // Choose a unique boundary string
            sftpService.downloadFiles(host,remotePath, filepattern, userName,password, outputStream, boundary);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_MIXED);
            
            
            
            headers.set(HttpHeaders.CONTENT_TYPE, "multipart/mixed; boundary=" + boundary);

            byte[] responseBytes = outputStream.toByteArray();
            outputStream.close();

            ByteArrayResource byteArrayResource = new ByteArrayResource(responseBytes);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("multipart/mixed"))
                    .body(byteArrayResource);
        } catch (Exception e) {
        	System.out.println(e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String remotePath, @RequestParam MultipartFile file) {
        try {
            sftpService.uploadFile(remotePath, file.getInputStream());
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading file.");
        }
    }
    public static String generateRandomKey() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(KEY_LENGTH);

        for (int i = 0; i < KEY_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

}
