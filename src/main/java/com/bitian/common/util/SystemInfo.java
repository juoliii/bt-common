package com.bitian.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * 获取当前系统信息
 * 
 * @author gxsn
 * 
 */
public class SystemInfo {
	// 当前实例
	private static SystemInfo currentSystem = null;
	private InetAddress localHost = null;

	private SystemInfo() {
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static SystemInfo getInstance() {
		if (currentSystem == null) {
			currentSystem = new SystemInfo();
		}
		return currentSystem;
	}

	public String getIP() {
		String ip = localHost.getHostAddress();
		return ip;
	}

	public String getHostName() {
		return localHost.getHostName();
	}

	public String getDiskNumber() {
		String diskSerialNumber = "";
		try {
			File file = File.createTempFile("temp", ".tmp");
			FileStore fileStore = Files.getFileStore(file.toPath());
			diskSerialNumber = fileStore.getAttribute("volume:vsn").toString();
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return diskSerialNumber;
	}

	public String getSystemName() {
		Properties sysProperty = System.getProperties();
		// 系统名称
		String systemName = sysProperty.getProperty("os.name");
		return systemName;
	}

	private String getMacFromBytes(byte[] bytes) {
		StringBuffer mac = new StringBuffer();
		byte currentByte;
		boolean first = false;
		for (byte b : bytes) {
			if (first) {
				mac.append("-");
			}
			currentByte = (byte) ((b & 240) >> 4);
			mac.append(Integer.toHexString(currentByte));
			currentByte = (byte) (b & 15);
			mac.append(Integer.toHexString(currentByte));
			first = true;
		}
		return mac.toString().toUpperCase();
	}

	public List<String> getMacs() {
		List<String> ret=new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
			while (el.hasMoreElements()) {
				NetworkInterface nf = el.nextElement();
				byte[] macBytes = nf.getHardwareAddress();
				if (macBytes != null && macBytes.length == 6) {
					ret.add(getMacFromBytes(macBytes));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}