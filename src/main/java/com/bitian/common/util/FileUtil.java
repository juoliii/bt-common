package com.bitian.common.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
	
	public static long copyFile(File source, File target) throws Exception {
		long time = System.currentTimeMillis();
		int length = 2097152;
		FileInputStream in = new FileInputStream(source);
		FileOutputStream out = new FileOutputStream(target);
		FileChannel inC = in.getChannel();
		FileChannel outC = out.getChannel();
		while (true) {
			if (inC.position() == inC.size()) {
				in.close();
				out.close();
				inC.close();
				outC.close();
				return System.currentTimeMillis() - time;
			}
			if ((inC.size() - inC.position()) < 20971520){
				length = (int) (inC.size() - inC.position());
			}else{
				length = 20971520;
			}
			inC.transferTo(inC.position(), length, outC);
			inC.position(inC.position() + length);
		}
	}

	/**
	 * 文件压缩
	 * @param filePaths 压缩文件路径
	 * @param zipFile zip文件路径
	 */
	public static void zip(List<File> filePaths, File zipFile){
		ZipOutputStream zipOutput=null;
		try{
			// 创建一个zip文件输出流
			zipOutput = new ZipOutputStream(new FileOutputStream(zipFile));

			for (File file : filePaths){
				// 判断文件是否存在，如不存在直接跳过
				if (!file.exists()){
					continue;
				}
				BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file));
				// 设置压缩条目名称
				zipOutput.putNextEntry(new ZipEntry(file.getName()));
				byte[] bytes = new byte[1024];
				int len = -1;
				while ((len = bufferedInput.read(bytes)) != -1){
					zipOutput.write(bytes,0,len);
				}
				bufferedInput.close();
				zipOutput.closeEntry();
			}
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(zipOutput);
		}
	}
}
