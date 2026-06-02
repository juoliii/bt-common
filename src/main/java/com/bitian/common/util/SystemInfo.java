package com.bitian.common.util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 系统信息工具类（单例）。
 * <p>
 * 提供主机名、IP、MAC 地址、磁盘序列号、操作系统属性、
 * CPU / 内存 / JVM 运行时等常用系统信息的获取方法。
 * </p>
 *
 * <h3>用法示例</h3>
 * <pre>{@code
 * SystemInfo info = SystemInfo.getInstance();
 * String ip   = info.getIP();
 * String host = info.getHostName();
 * List<String> macs = info.getMacs();
 * }</pre>
 *
 * @author gxsn
 */
public class SystemInfo {

    private static final Logger LOG = Logger.getLogger(SystemInfo.class.getName());

    /** 线程安全的懒加载单例（静态内部类方式） */
    private static class Holder {
        private static final SystemInfo INSTANCE = new SystemInfo();
    }

    private final InetAddress localHost;

    private SystemInfo() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.log(Level.WARNING, "无法获取本机 InetAddress", e);
        }
        this.localHost = addr;
    }

    /**
     * 获取 {@code SystemInfo} 单例实例。
     *
     * @return 单例实例，永不为 {@code null}
     */
    public static SystemInfo getInstance() {
        return Holder.INSTANCE;
    }

    // ==================== 网络信息 ====================

    /**
     * 获取所有活跃（UP）的非回环网卡信息列表。
     * <p>每个 {@link NetworkInterfaceInfo} 包含网卡名称、显示名称、MAC、
     * IPv4/IPv6 地址、子网前缀长度、广播地址、MTU 等完整信息。</p>
     *
     * <h4>用法示例</h4>
     * <pre>{@code
     * for (SystemInfo.NetworkInterfaceInfo nic : SystemInfo.getInstance().getNetworkInterfaces()) {
     *     System.out.println(nic.getName() + " -> " + nic.getIpv4Addresses());
     * }
     * }</pre>
     *
     * @return 网卡信息列表（不可变）；获取失败时返回空列表
     */
    public List<NetworkInterfaceInfo> getNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            if (nics == null) {
                return Collections.emptyList();
            }
            List<NetworkInterfaceInfo> result = new ArrayList<>();
            while (nics.hasMoreElements()) {
                NetworkInterface ni = nics.nextElement();
                // 跳过回环和未启用的网卡
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                result.add(buildInterfaceInfo(ni));
            }
            return Collections.unmodifiableList(result);
        } catch (SocketException e) {
            LOG.log(Level.WARNING, "获取网卡信息列表失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据网卡名称获取指定网卡信息。
     *
     * @param name 网卡名称，例如 {@code "eth0"}、{@code "wlan0"}
     * @return 网卡信息；未找到时返回 {@code null}
     */
    public NetworkInterfaceInfo getNetworkInterface(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (NetworkInterfaceInfo info : getNetworkInterfaces()) {
            if (name.equals(info.getName())) {
                return info;
            }
        }
        return null;
    }

    // -------------------- 向后兼容的便捷方法 --------------------

    /**
     * 获取本机 IP（基于 {@link InetAddress#getLocalHost()}）。
     * <p>注意：在多网卡环境下可能返回 {@code 127.0.0.1}，
     * 如需获取实际局域网 IP 请使用 {@link #getLocalIPv4()}，
     * 如需获取所有网卡 IP 请使用 {@link #getNetworkInterfaces()}。</p>
     *
     * @return 本机 IP 地址字符串；获取失败时返回空字符串
     */
    public String getIP() {
        return localHost != null ? localHost.getHostAddress() : "";
    }

    /**
     * 获取本机主机名。
     *
     * @return 主机名字符串；获取失败时返回空字符串
     */
    public String getHostName() {
        return localHost != null ? localHost.getHostName() : "";
    }

    /**
     * 获取本机第一个非回环 IPv4 地址。
     * <p>相比 {@link #getIP()} 在多网卡场景下更可靠。</p>
     *
     * @return IPv4 地址字符串；无法获取时返回空字符串
     */
    public String getLocalIPv4() {
        List<NetworkInterfaceInfo> nics = getNetworkInterfaces();
        for (NetworkInterfaceInfo nic : nics) {
            List<String> ipv4s = nic.getIpv4Addresses();
            if (!ipv4s.isEmpty()) {
                return ipv4s.get(0);
            }
        }
        return "";
    }

    /**
     * 获取本机所有网卡的 MAC 地址列表。
     * <p>仅包含硬件地址长度为 6 字节的物理网卡。</p>
     *
     * @return MAC 地址列表（格式：{@code AA-BB-CC-DD-EE-FF}）；获取失败时返回空列表
     */
    public List<String> getMacs() {
        List<NetworkInterfaceInfo> nics = getNetworkInterfaces();
        List<String> result = new ArrayList<>();
        for (NetworkInterfaceInfo nic : nics) {
            String mac = nic.getMac();
            if (!mac.isEmpty()) {
                result.add(mac);
            }
        }
        return result;
    }

    /**
     * 获取第一个物理网卡的 MAC 地址。
     *
     * @return MAC 地址字符串；获取失败时返回空字符串
     */
    public String getFirstMac() {
        List<String> macs = getMacs();
        return macs.isEmpty() ? "" : macs.get(0);
    }

    // ==================== 磁盘信息 ====================

    /**
     * 获取系统默认临时目录所在卷的磁盘序列号。
     *
     * @return 磁盘序列号字符串；获取失败时返回空字符串
     */
    public String getDiskNumber() {
        File file = null;
        try {
            file = File.createTempFile("sysinfo_", ".tmp");
            FileStore fileStore = Files.getFileStore(file.toPath());
            Object vsn = fileStore.getAttribute("volume:vsn");
            return vsn != null ? vsn.toString() : "";
        } catch (IOException e) {
            LOG.log(Level.WARNING, "获取磁盘序列号失败", e);
            return "";
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    /**
     * 获取所有磁盘根目录的磁盘空间信息。
     *
     * @return 不可变 Map，key 为根路径，value 为 {@link DiskSpace}
     */
    public Map<String, DiskSpace> getDiskSpaces() {
        File[] roots = File.listRoots();
        if (roots == null || roots.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, DiskSpace> result = new LinkedHashMap<>();
        for (File root : roots) {
            result.put(root.getAbsolutePath(), new DiskSpace(
                    root.getTotalSpace(),
                    root.getFreeSpace(),
                    root.getUsableSpace()
            ));
        }
        return Collections.unmodifiableMap(result);
    }

    // ==================== 操作系统信息 ====================

    /**
     * 获取操作系统名称。
     *
     * @return 操作系统名称，例如 {@code "Windows 10"}
     */
    public String getSystemName() {
        return System.getProperty("os.name", "");
    }

    /**
     * 获取操作系统版本。
     *
     * @return 操作系统版本字符串
     */
    public String getSystemVersion() {
        return System.getProperty("os.version", "");
    }

    /**
     * 获取操作系统架构。
     *
     * @return 架构字符串，例如 {@code "amd64"}
     */
    public String getSystemArch() {
        return System.getProperty("os.arch", "");
    }

    /**
     * 获取可用处理器（逻辑核心）数量。
     *
     * @return 可用处理器数
     */
    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取系统平均负载（最近 1 分钟）。
     * <p>在 Windows 平台上通常返回 {@code -1.0}。</p>
     *
     * @return 系统平均负载
     */
    public double getSystemLoadAverage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        return osBean.getSystemLoadAverage();
    }

    // ==================== JVM / 内存信息 ====================

    /**
     * 获取 Java 版本。
     *
     * @return Java 版本字符串，例如 {@code "17.0.2"}
     */
    public String getJavaVersion() {
        return System.getProperty("java.version", "");
    }

    /**
     * 获取 JVM 运行时间（毫秒）。
     *
     * @return JVM 自启动以来的毫秒数
     */
    public long getJvmUptime() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        return runtimeBean.getUptime();
    }

    /**
     * 获取 JVM 堆内存使用信息。
     *
     * @return 堆内存 {@link MemoryUsage}
     */
    public MemoryUsage getHeapMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getHeapMemoryUsage();
    }

    /**
     * 获取 JVM 非堆内存使用信息。
     *
     * @return 非堆内存 {@link MemoryUsage}
     */
    public MemoryUsage getNonHeapMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getNonHeapMemoryUsage();
    }

    /**
     * 获取 JVM 最大可用内存（字节）。
     *
     * @return 最大内存字节数
     */
    public long getJvmMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    /**
     * 获取 JVM 已分配内存（字节）。
     *
     * @return 已分配内存字节数
     */
    public long getJvmTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * 获取 JVM 空闲内存（字节）。
     *
     * @return 空闲内存字节数
     */
    public long getJvmFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * 获取当前用户名。
     *
     * @return 当前操作系统用户名
     */
    public String getUserName() {
        return System.getProperty("user.name", "");
    }

    /**
     * 获取当前用户主目录。
     *
     * @return 用户主目录路径
     */
    public String getUserHome() {
        return System.getProperty("user.home", "");
    }

    /**
     * 获取系统临时目录。
     *
     * @return 临时目录路径
     */
    public String getTempDir() {
        return System.getProperty("java.io.tmpdir", "");
    }

    /**
     * 汇总输出所有系统信息（用于调试 / 日志）。
     *
     * @return 格式化的系统信息字符串
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("===== System Info =====\n");
        sb.append("Host Name       : ").append(getHostName()).append('\n');
        sb.append("IP (localhost)  : ").append(getIP()).append('\n');
        sb.append("OS              : ").append(getSystemName()).append(' ')
                .append(getSystemVersion()).append(" (").append(getSystemArch()).append(")\n");
        sb.append("Processors      : ").append(getAvailableProcessors()).append('\n');
        sb.append("System Load     : ").append(getSystemLoadAverage()).append('\n');
        sb.append("Java Version    : ").append(getJavaVersion()).append('\n');
        sb.append("JVM Uptime (ms) : ").append(getJvmUptime()).append('\n');
        sb.append("JVM Max Memory  : ").append(formatBytes(getJvmMaxMemory())).append('\n');
        sb.append("JVM Total Memory: ").append(formatBytes(getJvmTotalMemory())).append('\n');
        sb.append("JVM Free Memory : ").append(formatBytes(getJvmFreeMemory())).append('\n');
        sb.append("User            : ").append(getUserName()).append('\n');
        sb.append("User Home       : ").append(getUserHome()).append('\n');
        sb.append("Temp Dir        : ").append(getTempDir()).append('\n');
        sb.append("----- Network Interfaces -----\n");
        List<NetworkInterfaceInfo> nics = getNetworkInterfaces();
        if (nics.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            for (NetworkInterfaceInfo nic : nics) {
                sb.append("  ").append(nic.getName());
                if (!nic.getDisplayName().equals(nic.getName())) {
                    sb.append(" (").append(nic.getDisplayName()).append(')');
                }
                sb.append('\n');
                sb.append("    MAC       : ").append(nic.getMac().isEmpty() ? "N/A" : nic.getMac()).append('\n');
                sb.append("    IPv4      : ").append(nic.getIpv4Addresses().isEmpty() ? "N/A" : nic.getIpv4Addresses()).append('\n');
                sb.append("    IPv6      : ").append(nic.getIpv6Addresses().isEmpty() ? "N/A" : nic.getIpv6Addresses()).append('\n');
                sb.append("    Broadcast : ").append(nic.getBroadcastAddresses().isEmpty() ? "N/A" : nic.getBroadcastAddresses()).append('\n');
                sb.append("    Prefix    : ").append(nic.getSubnetPrefixLengths().isEmpty() ? "N/A" : nic.getSubnetPrefixLengths()).append('\n');
                sb.append("    MTU       : ").append(nic.getMtu()).append('\n');
                sb.append("    Virtual   : ").append(nic.isVirtual()).append('\n');
            }
        }
        sb.append("===========================");
        return sb.toString();
    }

    // ==================== 内部工具方法 ====================

    /**
     * 从 {@link NetworkInterface} 构建 {@link NetworkInterfaceInfo}。
     */
    private NetworkInterfaceInfo buildInterfaceInfo(NetworkInterface ni) throws SocketException {
        // MAC
        String mac = "";
        byte[] macBytes = ni.getHardwareAddress();
        if (macBytes != null && macBytes.length == 6) {
            mac = formatMac(macBytes);
        }

        // IP 地址、广播地址、子网前缀
        List<String> ipv4List = new ArrayList<>();
        List<String> ipv6List = new ArrayList<>();
        List<String> broadcastList = new ArrayList<>();
        List<Short> prefixList = new ArrayList<>();

        for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
            InetAddress addr = ia.getAddress();
            if (addr instanceof Inet4Address) {
                ipv4List.add(addr.getHostAddress());
            } else if (addr instanceof Inet6Address) {
                ipv6List.add(addr.getHostAddress());
            }
            InetAddress broadcast = ia.getBroadcast();
            if (broadcast != null) {
                broadcastList.add(broadcast.getHostAddress());
            }
            prefixList.add(ia.getNetworkPrefixLength());
        }

        // 网关（尝试通过子网推断默认网关 —— x.x.x.1）
        String gateway = "";
        if (!ipv4List.isEmpty()) {
            gateway = guessGateway(ipv4List.get(0), prefixList.isEmpty() ? (short) 24 : prefixList.get(0));
        }

        return new NetworkInterfaceInfo(
                ni.getName(),
                ni.getDisplayName(),
                mac,
                Collections.unmodifiableList(ipv4List),
                Collections.unmodifiableList(ipv6List),
                Collections.unmodifiableList(broadcastList),
                Collections.unmodifiableList(prefixList),
                gateway,
                ni.getMTU(),
                ni.isVirtual(),
                ni.supportsMulticast(),
                ni.isPointToPoint()
        );
    }

    /**
     * 根据 IPv4 地址和子网前缀长度推断默认网关（通常为子网内的 .1 地址）。
     * <p>这是一种启发式推断，并非所有网络环境都准确。</p>
     */
    private static String guessGateway(String ipv4, short prefixLength) {
        try {
            byte[] addrBytes = InetAddress.getByName(ipv4).getAddress();
            // 计算子网掩码
            int mask = prefixLength == 0 ? 0 : (0xFFFFFFFF << (32 - prefixLength));
            int ip = ((addrBytes[0] & 0xFF) << 24) | ((addrBytes[1] & 0xFF) << 16)
                    | ((addrBytes[2] & 0xFF) << 8) | (addrBytes[3] & 0xFF);
            // 子网地址 + 1 = 常见网关地址
            int gatewayIp = (ip & mask) | 1;
            return String.format("%d.%d.%d.%d",
                    (gatewayIp >> 24) & 0xFF, (gatewayIp >> 16) & 0xFF,
                    (gatewayIp >> 8) & 0xFF, gatewayIp & 0xFF);
        } catch (UnknownHostException e) {
            return "";
        }
    }

    /**
     * 将 MAC 地址字节数组格式化为 {@code AA-BB-CC-DD-EE-FF} 形式。
     */
    private static String formatMac(byte[] bytes) {
        StringBuilder mac = new StringBuilder(17);
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                mac.append('-');
            }
            mac.append(String.format("%02X", bytes[i]));
        }
        return mac.toString();
    }

    /**
     * 将字节数格式化为人类可读的字符串（KB / MB / GB）。
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        } else if (bytes < 1024L * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024L * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    // ==================== 内部数据类 ====================

    /**
     * 网卡信息数据类。
     * <p>封装单个网络接口的完整信息，包括名称、MAC、IP 地址（v4/v6）、
     * 广播地址、子网前缀长度、默认网关（推断）、MTU 等。</p>
     *
     * <h4>字段说明</h4>
     * <ul>
     *   <li>{@code name} — 系统网卡名，如 {@code eth0}、{@code wlan0}、{@code 本地连接}</li>
     *   <li>{@code displayName} — 网卡显示名称（Windows 下通常为硬件描述）</li>
     *   <li>{@code mac} — MAC 地址，格式 {@code AA-BB-CC-DD-EE-FF}；无物理地址时为空字符串</li>
     *   <li>{@code ipv4Addresses} — 该网卡绑定的所有 IPv4 地址</li>
     *   <li>{@code ipv6Addresses} — 该网卡绑定的所有 IPv6 地址</li>
     *   <li>{@code broadcastAddresses} — 广播地址列表</li>
     *   <li>{@code subnetPrefixLengths} — 子网前缀长度列表（与 IP 地址一一对应）</li>
     *   <li>{@code gateway} — 推断的默认网关地址（启发式：子网 + 1）</li>
     *   <li>{@code mtu} — 最大传输单元</li>
     *   <li>{@code virtual} — 是否为虚拟网卡</li>
     *   <li>{@code multicast} — 是否支持组播</li>
     *   <li>{@code pointToPoint} — 是否为点对点接口（如 PPP）</li>
     * </ul>
     */
    public static class NetworkInterfaceInfo {
        private final String name;
        private final String displayName;
        private final String mac;
        private final List<String> ipv4Addresses;
        private final List<String> ipv6Addresses;
        private final List<String> broadcastAddresses;
        private final List<Short> subnetPrefixLengths;
        private final String gateway;
        private final int mtu;
        private final boolean virtual;
        private final boolean multicast;
        private final boolean pointToPoint;

        public NetworkInterfaceInfo(String name, String displayName, String mac,
                                    List<String> ipv4Addresses, List<String> ipv6Addresses,
                                    List<String> broadcastAddresses, List<Short> subnetPrefixLengths,
                                    String gateway, int mtu,
                                    boolean virtual, boolean multicast, boolean pointToPoint) {
            this.name = name;
            this.displayName = displayName;
            this.mac = mac;
            this.ipv4Addresses = ipv4Addresses;
            this.ipv6Addresses = ipv6Addresses;
            this.broadcastAddresses = broadcastAddresses;
            this.subnetPrefixLengths = subnetPrefixLengths;
            this.gateway = gateway;
            this.mtu = mtu;
            this.virtual = virtual;
            this.multicast = multicast;
            this.pointToPoint = pointToPoint;
        }

        /** 网卡系统名称，如 {@code eth0}、{@code wlan0} */
        public String getName() { return name; }

        /** 网卡显示名称（Windows 下通常为硬件描述） */
        public String getDisplayName() { return displayName; }

        /** MAC 地址，格式 {@code AA-BB-CC-DD-EE-FF}；无物理地址时为空字符串 */
        public String getMac() { return mac; }

        /** 该网卡绑定的所有 IPv4 地址 */
        public List<String> getIpv4Addresses() { return ipv4Addresses; }

        /** 该网卡绑定的所有 IPv6 地址 */
        public List<String> getIpv6Addresses() { return ipv6Addresses; }

        /** 广播地址列表 */
        public List<String> getBroadcastAddresses() { return broadcastAddresses; }

        /** 子网前缀长度列表（与 IP 地址一一对应） */
        public List<Short> getSubnetPrefixLengths() { return subnetPrefixLengths; }

        /** 推断的默认网关地址（启发式：子网 + 1），可能不准确 */
        public String getGateway() { return gateway; }

        /** 最大传输单元（MTU） */
        public int getMtu() { return mtu; }

        /** 是否为虚拟网卡 */
        public boolean isVirtual() { return virtual; }

        /** 是否支持组播 */
        public boolean isMulticast() { return multicast; }

        /** 是否为点对点接口（如 PPP） */
        public boolean isPointToPoint() { return pointToPoint; }

        /**
         * 获取子网掩码字符串（基于第一个子网前缀长度）。
         * <p>例如前缀长度 24 对应 {@code "255.255.255.0"}。</p>
         *
         * @return 子网掩码字符串；无前缀信息时返回空字符串
         */
        public String getSubnetMask() {
            if (subnetPrefixLengths.isEmpty()) {
                return "";
            }
            short prefix = subnetPrefixLengths.get(0);
            if (prefix <= 0 || prefix > 32) {
                return "";
            }
            int mask = prefix == 32 ? 0xFFFFFFFF : (0xFFFFFFFF << (32 - prefix));
            return String.format("%d.%d.%d.%d",
                    (mask >> 24) & 0xFF, (mask >> 16) & 0xFF,
                    (mask >> 8) & 0xFF, mask & 0xFF);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("NetworkInterface{");
            sb.append("name='").append(name).append('\'');
            if (!displayName.equals(name)) {
                sb.append(", display='").append(displayName).append('\'');
            }
            if (!mac.isEmpty()) {
                sb.append(", mac='").append(mac).append('\'');
            }
            if (!ipv4Addresses.isEmpty()) {
                sb.append(", ipv4=").append(ipv4Addresses);
            }
            if (!ipv6Addresses.isEmpty()) {
                sb.append(", ipv6=").append(ipv6Addresses);
            }
            if (!gateway.isEmpty()) {
                sb.append(", gateway='").append(gateway).append('\'');
            }
            String mask = getSubnetMask();
            if (!mask.isEmpty()) {
                sb.append(", mask='").append(mask).append('\'');
            }
            sb.append(", mtu=").append(mtu);
            if (virtual) {
                sb.append(", virtual");
            }
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * 磁盘空间信息。
     */
    public static class DiskSpace {
        private final long totalSpace;
        private final long freeSpace;
        private final long usableSpace;

        public DiskSpace(long totalSpace, long freeSpace, long usableSpace) {
            this.totalSpace = totalSpace;
            this.freeSpace = freeSpace;
            this.usableSpace = usableSpace;
        }

        /** 总空间（字节） */
        public long getTotalSpace() { return totalSpace; }

        /** 空闲空间（字节） */
        public long getFreeSpace() { return freeSpace; }

        /** 可用空间（字节） */
        public long getUsableSpace() { return usableSpace; }

        /** 已使用空间（字节） */
        public long getUsedSpace() { return totalSpace - freeSpace; }

        /** 使用率（0.0 ~ 1.0） */
        public double getUsageRate() {
            return totalSpace > 0 ? (double) (totalSpace - freeSpace) / totalSpace : 0.0;
        }

        @Override
        public String toString() {
            return String.format("DiskSpace{total=%s, free=%s, usable=%s, usage=%.1f%%}",
                    formatBytes(totalSpace), formatBytes(freeSpace),
                    formatBytes(usableSpace), getUsageRate() * 100);
        }
    }
}