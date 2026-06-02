package com.bitian.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 序列化/反序列化工具类。
 * <p>
 * 提供对象与字节数组之间的相互转换能力，以及基于序列化的深拷贝功能。
 * </p>
 */
public class SerializableUtil {

	private static final Logger LOGGER = Logger.getLogger(SerializableUtil.class.getName());

	private SerializableUtil() {
		// 工具类禁止实例化
	}

	// ==================== 核心方法 ====================

	/**
	 * 将对象序列化为字节数组。
	 *
	 * @param obj 要序列化的对象，必须实现 {@link Serializable} 接口
	 * @return 序列化后的字节数组，序列化失败时返回 null
	 */
	public static byte[] serialize(Object obj) {
		if (obj == null) {
			return null;
		}
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
			 ObjectOutputStream oos = new ObjectOutputStream(baos)) {

			oos.writeObject(obj);
			oos.flush();
			return baos.toByteArray();

		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "对象序列化失败: " + obj.getClass().getName(), e);
			return null;
		}
	}

	/**
	 * 将字节数组反序列化为对象。
	 *
	 * @param data 字节数组
	 * @param <T>  目标类型
	 * @return 反序列化后的对象，失败时返回 null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
			 ObjectInputStream ois = new ObjectInputStream(bais)) {

			return (T) ois.readObject();

		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(Level.WARNING, "对象反序列化失败", e);
			return null;
		}
	}

	/**
	 * 基于序列化实现对象深拷贝。
	 * <p>
	 * 通过序列化再反序列化的方式，创建对象的完全独立副本（包括所有嵌套引用）。
	 * 对象及其引用链上的所有对象都必须实现 {@link Serializable} 接口。
	 * </p>
	 *
	 * @param obj 要深拷贝的对象
	 * @param <T> 对象类型
	 * @return 深拷贝后的新对象，失败时返回 null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deepCopy(T obj) {
		if (obj == null) {
			return null;
		}
		byte[] data = serialize(obj);
		if (data == null) {
			return null;
		}
		return (T) deserialize(data);
	}

	// ==================== 向后兼容方法 ====================

	/**
	 * 将对象序列化为字节数组。
	 *
	 * @param obj 要序列化的对象
	 * @return 序列化后的字节数组
	 * @deprecated 使用 {@link #serialize(Object)} 代替
	 */
	@Deprecated
	public static byte[] encoderObject(Object obj) {
		return serialize(obj);
	}

	/**
	 * 将字节数组反序列化为对象。
	 *
	 * @param b 字节数组
	 * @return 反序列化后的对象
	 * @deprecated 使用 {@link #deserialize(byte[])} 代替
	 */
	@Deprecated
	public static Object decoderObject(byte[] b) {
		return deserialize(b);
	}

}
