package com.bitian.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
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

	/**
	 * 单例模式获取对象
	 * 
	 * @return
	 */
	public static SystemInfo getInstance() {
		if (currentSystem == null) {
			currentSystem = new SystemInfo();
		}
		return currentSystem;
	}

	/**
	 * 本地IP
	 * 
	 * @return IP地址
	 */
	public String getIP() {
		String ip = localHost.getHostAddress();
		return ip;
	}

	/**
	 * 获取用户机器名称
	 * 
	 * @return
	 */
	public String getHostName() {
		return localHost.getHostName();
	}

	/**
	 * 获取C盘卷 序列号
	 * 
	 * @return
	 */
	public String getDiskNumber() {
		String line = "";
		String HdSerial = "";// 记录硬盘序列号
		try {
			Process proces = Runtime.getRuntime().exec("cmd /c dir c:");// 获取命令行参数
			BufferedReader buffreader = new BufferedReader(
					new InputStreamReader(proces.getInputStream()));

			while ((line = buffreader.readLine()) != null) {

				if (line.indexOf("卷的序列号是 ") != -1) { // 读取参数并获取硬盘序列号

					HdSerial = line.substring(line.indexOf("卷的序列号是 ")
							+ "卷的序列号是 ".length(), line.length());
					break;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return HdSerial;
	}

	/**
	 * 获取当前系统名称
	 * 
	 * @return 当前系统名，例如： windows xp
	 */
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

	/**
	 * 获取到操作系统的网卡物理地址信息
	 * @return
	 */
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

	public static void main(String[] args) {
		SystemInfo info = SystemInfo.getInstance();
		System.out.println(info.getMacs());
//		Properties props = System.getProperties(); // 系统属性

		// Set<Object> keySet = sysProperty.keySet();
		// for (Object object : keySet) {
		// String property = sysProperty.getProperty(object.toString());
		// System.out.println(object.toString()+" : "+property);
		// }
		// System.out.println("Java的运行环境版本："+props.getProperty("java.version"));
		// System.out.println("Java的运行环境供应商："+props.getProperty("java.vendor"));
		// System.out.println("Java供应商的URL："+props.getProperty("java.vendor.url"));
		// System.out.println("Java的安装路径："+props.getProperty("java.home"));
		// System.out.println("Java的虚拟机规范版本："+props.getProperty("java.vm.specification.version"));
		// System.out.println("Java的虚拟机规范供应商："+props.getProperty("java.vm.specification.vendor"));
		// System.out.println("Java的虚拟机规范名称："+props.getProperty("java.vm.specification.name"));
		// System.out.println("Java的虚拟机实现版本："+props.getProperty("java.vm.version"));
		// System.out.println("Java的虚拟机实现供应商："+props.getProperty("java.vm.vendor"));
		// System.out.println("Java的虚拟机实现名称："+props.getProperty("java.vm.name"));
		// System.out.println("Java运行时环境规范版本："+props.getProperty("java.specification.version"));
		// System.out.println("Java运行时环境规范供应商："+props.getProperty("java.specification.vender"));
		// System.out.println("Java运行时环境规范名称："+props.getProperty("java.specification.name"));
		// System.out.println("Java的类格式版本号："+props.getProperty("java.class.version"));
		// System.out.println("Java的类路径："+props.getProperty("java.class.path"));
		// System.out.println("加载库时搜索的路径列表："+props.getProperty("java.library.path"));
		// System.out.println("默认的临时文件路径："+props.getProperty("java.io.tmpdir"));
		// System.out.println("一个或多个扩展目录的路径："+props.getProperty("java.ext.dirs"));
		// System.out.println("操作系统的名称："+props.getProperty("os.name"));
		// System.out.println("操作系统的构架："+props.getProperty("os.arch"));
		// System.out.println("操作系统的版本："+props.getProperty("os.version"));
		// System.out.println("文件分隔符："+props.getProperty("file.separator")); //在
		// unix 系统中是＂／＂
		// System.out.println("路径分隔符："+props.getProperty("path.separator")); //在
		// unix 系统中是＂:＂
		// System.out.println("行分隔符："+props.getProperty("line.separator")); //在
		// unix 系统中是＂/n＂
		// System.out.println("用户的账户名称："+props.getProperty("user.name"));
		// System.out.println("用户的主目录："+props.getProperty("user.home"));
		// System.out.println("用户的当前工作目录："+props.getProperty("user.dir"));
	}
}