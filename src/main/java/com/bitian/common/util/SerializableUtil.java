package com.bitian.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableUtil {
	// 对象写入数组
	public static byte[] encoderObject(Object obj) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		byte[] b = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);

			oos.writeObject(obj);

			b = baos.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return b;
	}

	// 数组读出对象
	public static Object decoderObject(byte[] b) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		Object obj = null;

		try {

			bais = new ByteArrayInputStream(b);
			ois = new ObjectInputStream(bais);

			obj = ois.readObject();

		} catch (Exception e) {
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

}
