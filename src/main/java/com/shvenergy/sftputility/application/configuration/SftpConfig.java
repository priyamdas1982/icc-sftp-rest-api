package com.shvenergy.sftputility.application.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sftp")
public class SftpConfig {

    private String host="storagestandalone.blob.core.windows.net";
    private int port=22;
    private String username="storagestandalone.iccappserviceuser";
    private String password="P5TN74XB2hxvWSAVb7FKj0mnNmdVFVq5";
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

    // Getters and setters

}
