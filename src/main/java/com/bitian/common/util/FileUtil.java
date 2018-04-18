package com.bitian.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;

public class FileUtil {
	
	public static long copyFile(File source, File target) throws Exception {
		long time = new Date().getTime();
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
				return new Date().getTime() - time;
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
}
