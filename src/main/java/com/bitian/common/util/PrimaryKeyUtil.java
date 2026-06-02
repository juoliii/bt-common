package com.bitian.common.util;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 主键生成辅助工具类
 * <p>
 * 提供多种主键生成策略：
 * <ul>
 *   <li>{@link #getUUID()} - 32位无横线UUID（原始方法，保持不变）</li>
 *   <li>{@link #getUUIDWithHyphen()} - 标准36位UUID（含横线）</li>
 *   <li>{@link #getSequentialUUID()} - 时间有序UUID，适合数据库索引</li>
 *   <li>{@link #snowflakeId()} - 雪花算法，生成趋势递增的long型ID</li>
 *   <li>{@link #snowflakeIdStr()} - 雪花算法，返回字符串</li>
 *   <li>{@link #getNanoId()} - 21位NanoId，URL友好</li>
 *   <li>{@link #getNanoId(int)} - 自定义长度的NanoId</li>
 *   <li>{@link #getNanoId(char[], int)} - 自定义字母表和长度的NanoId</li>
 *   <li>{@link #getTimestampId()} - 时间戳+随机数组合ID</li>
 *   <li>{@link #getShortId()} - 8位短ID，适合非严格唯一场景</li>
 *   <li>{@link #getObjectId()} - 类MongoDB ObjectId，24位十六进制</li>
 * </ul>
 *
 * @author Administrator
 */
public class PrimaryKeyUtil {

    // ==================== UUID 相关 ====================

    /**
     * 返回一个32个字母或者数组组成的字符串，此字符串唯一不会重复
     *
     * @return 32位无横线UUID字符串
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /**
     * 返回标准格式的UUID（含横线），例如：550e8400-e29b-41d4-a716-446655440000
     *
     * @return 36位标准UUID字符串
     */
    public static String getUUIDWithHyphen() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成时间有序的UUID（Version 1 风格）。
     * <p>
     * 将当前时间戳嵌入UUID的高位部分，使生成的ID在时间维度上趋势递增，
     * 有利于数据库B+Tree索引的顺序写入性能。
     *
     * @return 32位无横线、时间有序的UUID字符串
     */
    public static String getSequentialUUID() {
        long timestamp = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
        // 将时间戳编码到UUID高位，保留随机部分保证唯一性
        String timeHex = String.format("%012x", timestamp);
        String randomPart = uuid.toString().replace("-", "").substring(12);
        return timeHex + randomPart;
    }

    // ==================== 雪花算法 ====================

    /**
     * 内部雪花算法实现。
     * <p>
     * ID 结构（64位）：
     * <pre>
     * 0 - 41位时间戳 - 5位数据中心ID - 5位工作机器ID - 12位序列号
     * </pre>
     * <ul>
     *   <li>1位符号位，始终为0（正数）</li>
     *   <li>41位时间戳（毫秒），可用约69年</li>
     *   <li>5位数据中心ID（0~31）</li>
     *   <li>5位工作机器ID（0~31）</li>
     *   <li>12位序列号，每毫秒可生成4096个ID</li>
     * </ul>
     */
    private static final class Snowflake {

        /** 起始时间戳 (2020-01-01 00:00:00 UTC) */
        private static final long EPOCH = 1577836800000L;

        /** 数据中心ID位数 */
        private static final long DATA_CENTER_ID_BITS = 5L;

        /** 工作机器ID位数 */
        private static final long WORKER_ID_BITS = 5L;

        /** 序列号位数 */
        private static final long SEQUENCE_BITS = 12L;

        /** 数据中心ID最大值 (31) */
        private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

        /** 工作机器ID最大值 (31) */
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

        /** 序列号掩码 (4095) */
        private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

        /** 工作机器ID左移位数 */
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

        /** 数据中心ID左移位数 */
        private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

        /** 时间戳左移位数 */
        private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

        private final long dataCenterId;
        private final long workerId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        Snowflake(long dataCenterId, long workerId) {
            if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
                throw new IllegalArgumentException(
                        "dataCenterId must be between 0 and " + MAX_DATA_CENTER_ID);
            }
            if (workerId > MAX_WORKER_ID || workerId < 0) {
                throw new IllegalArgumentException(
                        "workerId must be between 0 and " + MAX_WORKER_ID);
            }
            this.dataCenterId = dataCenterId;
            this.workerId = workerId;
        }

        synchronized long nextId() {
            long timestamp = System.currentTimeMillis();

            if (timestamp < lastTimestamp) {
                // 时钟回拨处理：等待至上次时间戳
                long offset = lastTimestamp - timestamp;
                if (offset <= 5) {
                    try {
                        Thread.sleep(offset << 1);
                        timestamp = System.currentTimeMillis();
                        if (timestamp < lastTimestamp) {
                            throw new RuntimeException(
                                    "Clock moved backwards. Refusing to generate id for "
                                            + (lastTimestamp - timestamp) + " milliseconds");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for clock correction", e);
                    }
                } else {
                    throw new RuntimeException(
                            "Clock moved backwards. Refusing to generate id for "
                                    + offset + " milliseconds");
                }
            }

            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    // 当前毫秒序列号用尽，等待下一毫秒
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                // 不同毫秒，序列号从随机值开始，避免低位全0导致分库分表不均匀
                sequence = ThreadLocalRandom.current().nextLong(2);
            }

            lastTimestamp = timestamp;

            return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                    | (dataCenterId << DATA_CENTER_ID_SHIFT)
                    | (workerId << WORKER_ID_SHIFT)
                    | sequence;
        }

        private long tilNextMillis(long lastTs) {
            long ts = System.currentTimeMillis();
            while (ts <= lastTs) {
                ts = System.currentTimeMillis();
            }
            return ts;
        }
    }

    /** 默认雪花算法实例（自动推断 workerId 和 dataCenterId） */
    private static final Snowflake DEFAULT_SNOWFLAKE;

    static {
        long autoWorkerId = generateWorkerId();
        long autoDataCenterId = generateDataCenterId();
        DEFAULT_SNOWFLAKE = new Snowflake(autoDataCenterId, autoWorkerId);
    }

    /**
     * 使用雪花算法生成唯一ID。
     * <p>
     * 生成的ID是趋势递增的long型整数，适合作为数据库主键。
     * 默认实例会自动根据网卡MAC地址推断 workerId 和 dataCenterId。
     *
     * @return 雪花算法生成的唯一ID
     */
    public static long snowflakeId() {
        return DEFAULT_SNOWFLAKE.nextId();
    }

    /**
     * 使用雪花算法生成唯一ID，返回字符串形式。
     *
     * @return 雪花算法生成的唯一ID字符串
     */
    public static String snowflakeIdStr() {
        return String.valueOf(DEFAULT_SNOWFLAKE.nextId());
    }

    /**
     * 使用指定的 dataCenterId 和 workerId 创建一个雪花算法ID生成器。
     * <p>
     * 适用于多数据中心/多节点部署场景，需要手动指定节点标识。
     *
     * @param dataCenterId 数据中心ID（0~31）
     * @param workerId     工作机器ID（0~31）
     * @return 雪花算法生成的唯一ID
     */
    public static long snowflakeId(long dataCenterId, long workerId) {
        return new Snowflake(dataCenterId, workerId).nextId();
    }

    /**
     * 从雪花ID中提取生成时间。
     *
     * @param snowflakeId 雪花算法生成的ID
     * @return 生成时间的 LocalDateTime
     */
    public static LocalDateTime snowflakeTime(long snowflakeId) {
        long timestamp = (snowflakeId >> Snowflake.TIMESTAMP_SHIFT) + Snowflake.EPOCH;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    // ==================== NanoId ====================

    /** NanoId 默认字母表（URL安全字符） */
    private static final char[] NANO_ID_ALPHABET =
            "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /** NanoId 默认长度 */
    private static final int NANO_ID_DEFAULT_SIZE = 21;

    /** 线程安全的安全随机数生成器 */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成21位NanoId。
     * <p>
     * NanoId使用URL安全的字符集（A-Za-z0-9_-），比UUID更短，
     * 在相同的碰撞概率下占用更少的存储空间。
     * <p>
     * 21位NanoId的碰撞概率与UUID v4相当（约需生成10亿个ID才有50%概率出现一次碰撞）。
     *
     * @return 21位NanoId字符串
     */
    public static String getNanoId() {
        return getNanoId(NANO_ID_ALPHABET, NANO_ID_DEFAULT_SIZE);
    }

    /**
     * 生成指定长度的NanoId（使用默认字母表）。
     *
     * @param size ID长度，必须大于0
     * @return 指定长度的NanoId字符串
     * @throws IllegalArgumentException 如果 size 小于等于0
     */
    public static String getNanoId(int size) {
        return getNanoId(NANO_ID_ALPHABET, size);
    }

    /**
     * 使用自定义字母表和长度生成NanoId。
     *
     * @param alphabet 字符集数组，长度必须在1~255之间
     * @param size     ID长度，必须大于0
     * @return 自定义NanoId字符串
     * @throws IllegalArgumentException 如果参数不合法
     */
    public static String getNanoId(char[] alphabet, int size) {
        if (alphabet == null || alphabet.length == 0 || alphabet.length > 255) {
            throw new IllegalArgumentException("Alphabet length must be between 1 and 255");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        // 使用位掩码优化取模运算
        int mask = (2 << (int) Math.floor(Math.log(alphabet.length - 1) / Math.log(2))) - 1;
        int step = (int) Math.ceil(1.6 * mask * size / alphabet.length);

        StringBuilder id = new StringBuilder(size);
        byte[] bytes = new byte[step];
        while (true) {
            SECURE_RANDOM.nextBytes(bytes);
            for (int i = 0; i < step; i++) {
                int index = bytes[i] & mask;
                if (index < alphabet.length) {
                    id.append(alphabet[index]);
                    if (id.length() == size) {
                        return id.toString();
                    }
                }
            }
        }
    }

    // ==================== 时间戳ID ====================

    /** 时间戳ID格式化器 */
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /** 时间戳ID的序列计数器 */
    private static final AtomicInteger TIMESTAMP_SEQ = new AtomicInteger(0);

    /**
     * 生成基于时间戳的ID。
     * <p>
     * 格式：yyyyMMddHHmmssSSS + 4位序列号 + 4位随机数，共25位。
     * 适用于对可读性要求较高的业务场景（如订单号、流水号）。
     *
     * @return 25位时间戳ID字符串
     */
    public static String getTimestampId() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        int seq = TIMESTAMP_SEQ.getAndIncrement() & 0x0FFF; // 取低12位，0~4095循环
        int random = ThreadLocalRandom.current().nextInt(10000);
        return String.format("%s%04d%04d", timestamp, seq, random);
    }

    // ==================== 短ID ====================

    /** Base62字符表 */
    private static final char[] BASE62_CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * 生成8位短ID（Base62编码）。
     * <p>
     * 基于UUID的高64位进行Base62编码截取，适合需要短标识符的场景（如短链接、邀请码）。
     * <p>
     * <b>注意：</b>短ID的碰撞概率高于UUID，不建议在需要严格唯一性的场景使用。
     * 在大量生成时（百万级以上），应考虑配合数据库唯一索引使用。
     *
     * @return 8位短ID字符串
     */
    public static String getShortId() {
        return getShortId(8);
    }

    /**
     * 生成指定长度的短ID（Base62编码）。
     *
     * @param length ID长度（1~22），长度越大碰撞概率越低
     * @return 指定长度的短ID字符串
     * @throws IllegalArgumentException 如果 length 不在合理范围内
     */
    public static String getShortId(int length) {
        if (length < 1 || length > 22) {
            throw new IllegalArgumentException("Length must be between 1 and 22");
        }

        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();

        StringBuilder sb = new StringBuilder(length);
        // 先用高64位
        long value = msb < 0 ? msb & Long.MAX_VALUE : msb;
        while (sb.length() < Math.min(length, 11) && value > 0) {
            sb.append(BASE62_CHARS[(int) (value % 62)]);
            value /= 62;
        }
        // 不够再用低64位
        value = lsb < 0 ? lsb & Long.MAX_VALUE : lsb;
        while (sb.length() < length && value > 0) {
            sb.append(BASE62_CHARS[(int) (value % 62)]);
            value /= 62;
        }
        // 如果还不够（极端情况），用随机字符补齐
        while (sb.length() < length) {
            sb.append(BASE62_CHARS[ThreadLocalRandom.current().nextInt(62)]);
        }

        return sb.toString();
    }

    // ==================== ObjectId ====================

    /** ObjectId 的机器标识（基于MAC地址哈希） */
    private static final int MACHINE_IDENTIFIER;

    /** ObjectId 的进程标识 */
    private static final short PROCESS_IDENTIFIER;

    /** ObjectId 的自增计数器 */
    private static final AtomicInteger OBJECT_ID_COUNTER = new AtomicInteger(
            new SecureRandom().nextInt());

    static {
        MACHINE_IDENTIFIER = createMachineIdentifier();
        PROCESS_IDENTIFIER = createProcessIdentifier();
    }

    /**
     * 生成类MongoDB的ObjectId。
     * <p>
     * 格式：24位十六进制字符串（12字节），结构如下：
     * <ul>
     *   <li>4字节：Unix时间戳（秒级）</li>
     *   <li>3字节：机器标识符</li>
     *   <li>2字节：进程标识符</li>
     *   <li>3字节：自增计数器</li>
     * </ul>
     * 生成的ID天然趋势递增，适合作为分布式系统的主键。
     *
     * @return 24位十六进制ObjectId字符串
     */
    public static String getObjectId() {
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        int counter = OBJECT_ID_COUNTER.getAndIncrement() & 0x00FFFFFF;

        // 拼装12字节
        byte[] bytes = new byte[12];
        bytes[0] = (byte) (timestamp >> 24);
        bytes[1] = (byte) (timestamp >> 16);
        bytes[2] = (byte) (timestamp >> 8);
        bytes[3] = (byte) timestamp;
        bytes[4] = (byte) (MACHINE_IDENTIFIER >> 16);
        bytes[5] = (byte) (MACHINE_IDENTIFIER >> 8);
        bytes[6] = (byte) MACHINE_IDENTIFIER;
        bytes[7] = (byte) (PROCESS_IDENTIFIER >> 8);
        bytes[8] = (byte) PROCESS_IDENTIFIER;
        bytes[9] = (byte) (counter >> 16);
        bytes[10] = (byte) (counter >> 8);
        bytes[11] = (byte) counter;

        StringBuilder hex = new StringBuilder(24);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b & 0xFF));
        }
        return hex.toString();
    }

    // ==================== 辅助方法 ====================

    /**
     * 根据MAC地址自动生成workerId（0~31）。
     */
    private static long generateWorkerId() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            if (sb.length() > 0) {
                return (sb.toString().hashCode() & 0xFFFF) % 32;
            }
        } catch (Exception ignored) {
            // 无法获取MAC地址时使用随机值
        }
        return ThreadLocalRandom.current().nextLong(32);
    }

    /**
     * 根据MAC地址自动生成dataCenterId（0~31）。
     */
    private static long generateDataCenterId() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            if (sb.length() > 0) {
                return (sb.toString().hashCode() >>> 16 & 0xFFFF) % 32;
            }
        } catch (Exception ignored) {
            // 无法获取MAC地址时使用随机值
        }
        return ThreadLocalRandom.current().nextLong(32);
    }

    /**
     * 生成机器标识（用于ObjectId）。
     */
    private static int createMachineIdentifier() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            return sb.toString().hashCode();
        } catch (Exception e) {
            return new SecureRandom().nextInt();
        }
    }

    /**
     * 生成进程标识（用于ObjectId）。
     */
    private static short createProcessIdentifier() {
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            if (processName != null && processName.contains("@")) {
                return (short) Integer.parseInt(processName.split("@")[0]);
            }
            return (short) processName.hashCode();
        } catch (Exception e) {
            return (short) new SecureRandom().nextInt();
        }
    }

    /** 私有构造函数，工具类不应被实例化 */
    private PrimaryKeyUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
