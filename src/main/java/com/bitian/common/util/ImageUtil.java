package com.bitian.common.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * 图片处理工具类
 * <p>
 * 提供图片缩放、裁剪、旋转、翻转、水印、格式转换、灰度化、
 * Base64 编解码、缩略图生成、图片信息获取等常用功能。
 * </p>
 *
 * @author 何浩
 */
public class ImageUtil {

    /** 默认 JPEG 输出质量对应的格式名 */
    private static final String FORMAT_JPEG = "JPEG";
    private static final String FORMAT_PNG = "PNG";
    private static final String FORMAT_GIF = "GIF";
    private static final String FORMAT_BMP = "BMP";

    private ImageUtil() {
        // 工具类禁止实例化
    }

    // ==================== 缩放 ====================

    /**
     * 将图片进行缩放
     *
     * @param source  源图片
     * @param targetW 目标宽度
     * @param targetH 目标高度
     * @param keepRatio 是否等比缩放
     * @return 缩放后的图片
     */
    public static BufferedImage resize(BufferedImage source, int targetW,
                                       int targetH, boolean keepRatio) {
        int type = source.getType();
        double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();
        if (keepRatio) {
            if (sx > sy) {
                sx = sy;
                targetW = (int) (sx * source.getWidth());
            } else {
                sy = sx;
                targetH = (int) (sy * source.getHeight());
            }
        }

        BufferedImage target;
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage(targetW, targetH, type);
        }

        Graphics2D g = target.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        } finally {
            g.dispose();
        }
        return target;
    }

    /**
     * 按指定宽度等比缩放，高度自动计算
     *
     * @param source 源图片
     * @param width  目标宽度
     * @return 缩放后的图片
     */
    public static BufferedImage resizeByWidth(BufferedImage source, int width) {
        int height = (int) ((double) width / source.getWidth() * source.getHeight());
        return resize(source, width, height, false);
    }

    /**
     * 按指定高度等比缩放，宽度自动计算
     *
     * @param source 源图片
     * @param height 目标高度
     * @return 缩放后的图片
     */
    public static BufferedImage resizeByHeight(BufferedImage source, int height) {
        int width = (int) ((double) height / source.getHeight() * source.getWidth());
        return resize(source, width, height, false);
    }

    /**
     * 按比例缩放
     *
     * @param source 源图片
     * @param scale  缩放比例（0.5 表示缩小一半，2.0 表示放大一倍）
     * @return 缩放后的图片
     */
    public static BufferedImage resizeByScale(BufferedImage source, double scale) {
        int width = (int) (source.getWidth() * scale);
        int height = (int) (source.getHeight() * scale);
        return resize(source, width, height, false);
    }

    // ==================== 缩略图 ====================

    /**
     * 生成缩略图（等比缩放，不超过指定的最大宽高）
     *
     * @param source   源图片
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 缩略图
     */
    public static BufferedImage thumbnail(BufferedImage source, int maxWidth, int maxHeight) {
        int srcW = source.getWidth();
        int srcH = source.getHeight();
        if (srcW <= maxWidth && srcH <= maxHeight) {
            return source;
        }
        return resize(source, maxWidth, maxHeight, true);
    }

    /**
     * 生成缩略图并保存到文件
     *
     * @param srcFile   源文件
     * @param destFile  目标文件
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @throws IOException 读写异常
     */
    public static void thumbnail(File srcFile, File destFile, int maxWidth, int maxHeight) throws IOException {
        BufferedImage source = ImageIO.read(srcFile);
        BufferedImage thumb = thumbnail(source, maxWidth, maxHeight);
        String format = getFormatByFileName(destFile.getName());
        writeImage(thumb, format, destFile);
    }

    // ==================== 保存 ====================

    /**
     * 将图片缩放后保存（兼容旧方法签名）
     *
     * @param fromFileStr   源文件路径
     * @param saveToFileStr 目标文件路径
     * @param width         目标宽度（<=0 则不缩放）
     * @param height        目标高度（<=0 则不缩放）
     * @throws Exception 读写异常
     */
    public static void saveImageAsJpg(String fromFileStr, String saveToFileStr,
                                      int width, int height) throws Exception {
        File fromFile = new File(fromFileStr);
        File saveFile = new File(saveToFileStr);
        saveImageAsJpg(fromFile, saveFile, width, height);
    }

    /**
     * 将图片缩放后保存（兼容旧方法签名）
     *
     * @param fromFile   源文件
     * @param saveToFile 目标文件
     * @param width      目标宽度（<=0 则不缩放）
     * @param height     目标高度（<=0 则不缩放）
     * @throws Exception 读写异常
     */
    public static void saveImageAsJpg(File fromFile, File saveToFile,
                                      int width, int height) throws Exception {
        BufferedImage srcImage = ImageIO.read(fromFile);
        if (srcImage == null) {
            throw new IOException("无法读取图片文件: " + fromFile.getAbsolutePath());
        }
        String imgType = getFormatByFileName(fromFile.getName());
        if (width > 0 || height > 0) {
            srcImage = resize(srcImage, width, height, false);
        }
        ImageIO.write(srcImage, imgType, saveToFile);
    }

    /**
     * 将 BufferedImage 写入文件
     *
     * @param image  图片
     * @param format 格式名（如 "PNG", "JPEG"）
     * @param file   目标文件
     * @throws IOException 写入异常
     */
    public static void writeImage(BufferedImage image, String format, File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        ImageIO.write(image, format, file);
    }

    /**
     * 将 BufferedImage 写入输出流
     *
     * @param image  图片
     * @param format 格式名
     * @param out    输出流
     * @throws IOException 写入异常
     */
    public static void writeImage(BufferedImage image, String format, OutputStream out) throws IOException {
        ImageIO.write(image, format, out);
    }

    // ==================== 裁剪 ====================

    /**
     * 裁剪图片
     *
     * @param source 源图片
     * @param x      裁剪起始 x 坐标
     * @param y      裁剪起始 y 坐标
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @return 裁剪后的图片
     */
    public static BufferedImage crop(BufferedImage source, int x, int y, int width, int height) {
        int srcW = source.getWidth();
        int srcH = source.getHeight();
        // 边界修正
        x = Math.max(0, x);
        y = Math.max(0, y);
        width = Math.min(width, srcW - x);
        height = Math.min(height, srcH - y);
        return source.getSubimage(x, y, width, height);
    }

    /**
     * 居中裁剪为正方形
     *
     * @param source 源图片
     * @return 居中裁剪后的正方形图片
     */
    public static BufferedImage cropToSquare(BufferedImage source) {
        int srcW = source.getWidth();
        int srcH = source.getHeight();
        int size = Math.min(srcW, srcH);
        int x = (srcW - size) / 2;
        int y = (srcH - size) / 2;
        return crop(source, x, y, size, size);
    }

    /**
     * 居中裁剪为指定宽高比
     *
     * @param source      源图片
     * @param ratioWidth  宽度比例
     * @param ratioHeight 高度比例
     * @return 裁剪后的图片
     */
    public static BufferedImage cropToRatio(BufferedImage source, int ratioWidth, int ratioHeight) {
        int srcW = source.getWidth();
        int srcH = source.getHeight();
        double targetRatio = (double) ratioWidth / ratioHeight;
        double srcRatio = (double) srcW / srcH;

        int cropW, cropH;
        if (srcRatio > targetRatio) {
            // 源图更宽，裁剪宽度
            cropH = srcH;
            cropW = (int) (srcH * targetRatio);
        } else {
            // 源图更高，裁剪高度
            cropW = srcW;
            cropH = (int) (srcW / targetRatio);
        }
        int x = (srcW - cropW) / 2;
        int y = (srcH - cropH) / 2;
        return crop(source, x, y, cropW, cropH);
    }

    // ==================== 旋转 ====================

    /**
     * 旋转图片
     *
     * @param source  源图片
     * @param degrees 旋转角度（正值为顺时针）
     * @return 旋转后的图片
     */
    public static BufferedImage rotate(BufferedImage source, double degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int srcW = source.getWidth();
        int srcH = source.getHeight();
        int newW = (int) Math.floor(srcW * cos + srcH * sin);
        int newH = (int) Math.floor(srcH * cos + srcW * sin);

        int type = source.getType() == BufferedImage.TYPE_CUSTOM
                ? BufferedImage.TYPE_INT_ARGB : source.getType();
        BufferedImage target = new BufferedImage(newW, newH, type);
        Graphics2D g = target.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate((newW - srcW) / 2.0, (newH - srcH) / 2.0);
            g.rotate(radians, srcW / 2.0, srcH / 2.0);
            g.drawRenderedImage(source, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    /**
     * 顺时针旋转 90 度
     *
     * @param source 源图片
     * @return 旋转后的图片
     */
    public static BufferedImage rotate90(BufferedImage source) {
        return rotate(source, 90);
    }

    /**
     * 旋转 180 度
     *
     * @param source 源图片
     * @return 旋转后的图片
     */
    public static BufferedImage rotate180(BufferedImage source) {
        return rotate(source, 180);
    }

    /**
     * 逆时针旋转 90 度（即顺时针 270 度）
     *
     * @param source 源图片
     * @return 旋转后的图片
     */
    public static BufferedImage rotate270(BufferedImage source) {
        return rotate(source, 270);
    }

    // ==================== 翻转 ====================

    /**
     * 水平翻转
     *
     * @param source 源图片
     * @return 翻转后的图片
     */
    public static BufferedImage flipHorizontal(BufferedImage source) {
        int w = source.getWidth();
        int h = source.getHeight();
        int type = source.getType() == BufferedImage.TYPE_CUSTOM
                ? BufferedImage.TYPE_INT_ARGB : source.getType();
        BufferedImage target = new BufferedImage(w, h, type);
        Graphics2D g = target.createGraphics();
        try {
            g.drawImage(source, w, 0, -w, h, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    /**
     * 垂直翻转
     *
     * @param source 源图片
     * @return 翻转后的图片
     */
    public static BufferedImage flipVertical(BufferedImage source) {
        int w = source.getWidth();
        int h = source.getHeight();
        int type = source.getType() == BufferedImage.TYPE_CUSTOM
                ? BufferedImage.TYPE_INT_ARGB : source.getType();
        BufferedImage target = new BufferedImage(w, h, type);
        Graphics2D g = target.createGraphics();
        try {
            g.drawImage(source, 0, h, w, -h, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    // ==================== 水印 ====================

    /**
     * 添加文字水印
     *
     * @param source   源图片
     * @param text     水印文字
     * @param font     字体
     * @param color    颜色
     * @param alpha    透明度（0.0f ~ 1.0f）
     * @param x        水印 x 坐标
     * @param y        水印 y 坐标
     * @return 带水印的图片
     */
    public static BufferedImage addTextWatermark(BufferedImage source, String text,
                                                 Font font, Color color, float alpha,
                                                 int x, int y) {
        int type = source.getType() == BufferedImage.TYPE_CUSTOM
                ? BufferedImage.TYPE_INT_ARGB : source.getType();
        BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(), type);
        Graphics2D g = target.createGraphics();
        try {
            g.drawImage(source, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(color);
            g.setFont(font);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawString(text, x, y);
        } finally {
            g.dispose();
        }
        return target;
    }

    /**
     * 在右下角添加文字水印（自动计算位置，边距 10px）
     *
     * @param source 源图片
     * @param text   水印文字
     * @param font   字体
     * @param color  颜色
     * @param alpha  透明度（0.0f ~ 1.0f）
     * @return 带水印的图片
     */
    public static BufferedImage addTextWatermarkBottomRight(BufferedImage source, String text,
                                                            Font font, Color color, float alpha) {
        Graphics2D temp = source.createGraphics();
        FontMetrics metrics;
        try {
            temp.setFont(font);
            metrics = temp.getFontMetrics();
        } finally {
            temp.dispose();
        }
        int textW = metrics.stringWidth(text);
        int textH = metrics.getHeight();
        int margin = 10;
        int x = source.getWidth() - textW - margin;
        int y = source.getHeight() - textH + metrics.getAscent() - margin;
        return addTextWatermark(source, text, font, color, alpha, x, y);
    }

    /**
     * 添加图片水印
     *
     * @param source    源图片
     * @param watermark 水印图片
     * @param alpha     透明度（0.0f ~ 1.0f）
     * @param x         水印 x 坐标
     * @param y         水印 y 坐标
     * @return 带水印的图片
     */
    public static BufferedImage addImageWatermark(BufferedImage source, BufferedImage watermark,
                                                  float alpha, int x, int y) {
        int type = source.getType() == BufferedImage.TYPE_CUSTOM
                ? BufferedImage.TYPE_INT_ARGB : source.getType();
        BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(), type);
        Graphics2D g = target.createGraphics();
        try {
            g.drawImage(source, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(watermark, x, y, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    /**
     * 在右下角添加图片水印（边距 10px）
     *
     * @param source    源图片
     * @param watermark 水印图片
     * @param alpha     透明度
     * @return 带水印的图片
     */
    public static BufferedImage addImageWatermarkBottomRight(BufferedImage source,
                                                             BufferedImage watermark, float alpha) {
        int margin = 10;
        int x = source.getWidth() - watermark.getWidth() - margin;
        int y = source.getHeight() - watermark.getHeight() - margin;
        return addImageWatermark(source, watermark, alpha, x, y);
    }

    // ==================== 格式转换 ====================

    /**
     * 图片格式转换
     *
     * @param srcFile    源文件
     * @param destFile   目标文件
     * @param destFormat 目标格式（如 "PNG", "JPEG", "GIF", "BMP"）
     * @throws IOException 读写异常
     */
    public static void convertFormat(File srcFile, File destFile, String destFormat) throws IOException {
        BufferedImage image = ImageIO.read(srcFile);
        if (image == null) {
            throw new IOException("无法读取图片文件: " + srcFile.getAbsolutePath());
        }
        // 如果目标格式不支持透明通道（如 JPEG），需转换色彩模型
        if ("JPEG".equalsIgnoreCase(destFormat) || "JPG".equalsIgnoreCase(destFormat)) {
            image = removeAlphaChannel(image);
        }
        writeImage(image, destFormat, destFile);
    }

    /**
     * 图片格式转换（根据文件扩展名自动推断目标格式）
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @throws IOException 读写异常
     */
    public static void convertFormat(File srcFile, File destFile) throws IOException {
        String destFormat = getFormatByFileName(destFile.getName());
        convertFormat(srcFile, destFile, destFormat);
    }

    // ==================== 灰度化 ====================

    /**
     * 将图片转为灰度图
     *
     * @param source 源图片
     * @return 灰度图
     */
    public static BufferedImage toGrayscale(BufferedImage source) {
        BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = target.createGraphics();
        try {
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    // ==================== 透明度 ====================

    /**
     * 检查图片是否包含透明像素
     *
     * @param bufferedImage 待检查的图片
     * @return 如果包含透明像素返回 true
     */
    public static boolean isTransparent(BufferedImage bufferedImage) {
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = bufferedImage.getRGB(i, j);
                if ((pixel >> 24) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 移除 Alpha 通道（将透明区域替换为白色背景）
     * <p>适用于将 PNG 转为 JPEG 时去除透明背景</p>
     *
     * @param source 源图片
     * @return 无透明通道的图片
     */
    public static BufferedImage removeAlphaChannel(BufferedImage source) {
        if (!source.getColorModel().hasAlpha()) {
            return source;
        }
        BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = target.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, source.getWidth(), source.getHeight());
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    // ==================== Base64 编解码 ====================

    /**
     * 将图片编码为 Base64 字符串
     *
     * @param image  图片
     * @param format 格式名（如 "PNG", "JPEG"）
     * @return Base64 编码的字符串
     * @throws IOException 编码异常
     */
    public static String toBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 将图片编码为带 Data URI 前缀的 Base64 字符串
     * <p>例如: {@code data:image/png;base64,iVBOR...}</p>
     *
     * @param image  图片
     * @param format 格式名
     * @return Data URI 格式的 Base64 字符串
     * @throws IOException 编码异常
     */
    public static String toBase64DataUri(BufferedImage image, String format) throws IOException {
        String base64 = toBase64(image, format);
        String mimeType = "image/" + format.toLowerCase();
        return "data:" + mimeType + ";base64," + base64;
    }

    /**
     * 将 Base64 字符串解码为 BufferedImage
     * <p>支持带 Data URI 前缀和纯 Base64 字符串</p>
     *
     * @param base64Str Base64 字符串
     * @return 解码后的图片
     * @throws IOException 解码异常
     */
    public static BufferedImage fromBase64(String base64Str) throws IOException {
        // 去除 Data URI 前缀
        if (base64Str.contains(",")) {
            base64Str = base64Str.substring(base64Str.indexOf(",") + 1);
        }
        byte[] bytes = Base64.getDecoder().decode(base64Str);
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    // ==================== 字节数组 I/O ====================

    /**
     * 将图片转为字节数组
     *
     * @param image  图片
     * @param format 格式名
     * @return 字节数组
     * @throws IOException 编码异常
     */
    public static byte[] toByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    /**
     * 将字节数组转为 BufferedImage
     *
     * @param bytes 字节数组
     * @return 图片
     * @throws IOException 解码异常
     */
    public static BufferedImage fromByteArray(byte[] bytes) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    /**
     * 从输入流读取图片
     *
     * @param inputStream 输入流
     * @return 图片
     * @throws IOException 读取异常
     */
    public static BufferedImage fromInputStream(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    // ==================== 图片信息 ====================

    /**
     * 获取图片宽度
     *
     * @param file 图片文件
     * @return 宽度（像素）
     * @throws IOException 读取异常
     */
    public static int getWidth(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("无法读取图片文件: " + file.getAbsolutePath());
        }
        return image.getWidth();
    }

    /**
     * 获取图片高度
     *
     * @param file 图片文件
     * @return 高度（像素）
     * @throws IOException 读取异常
     */
    public static int getHeight(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("无法读取图片文件: " + file.getAbsolutePath());
        }
        return image.getHeight();
    }

    /**
     * 获取图片尺寸 [宽度, 高度]
     *
     * @param file 图片文件
     * @return 包含宽度和高度的数组 [width, height]
     * @throws IOException 读取异常
     */
    public static int[] getSize(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("无法读取图片文件: " + file.getAbsolutePath());
        }
        return new int[]{image.getWidth(), image.getHeight()};
    }

    /**
     * 获取图片格式（通过文件头检测，不依赖扩展名）
     *
     * @param file 图片文件
     * @return 格式名（如 "png", "jpeg"），未识别则返回 null
     * @throws IOException 读取异常
     */
    public static String getFormat(File file) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        if (iis == null) {
            return null;
        }
        try {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                return readers.next().getFormatName();
            }
        } finally {
            iis.close();
        }
        return null;
    }

    /**
     * 获取图片详细信息（宽度、高度、格式、文件大小）
     *
     * @param file 图片文件
     * @return 包含图片信息的 Map
     * @throws IOException 读取异常
     */
    public static Map<String, Object> getImageInfo(File file) throws IOException {
        Map<String, Object> info = new HashMap<String, Object>();
        BufferedImage image = ImageIO.read(file);
        if (image != null) {
            info.put("width", image.getWidth());
            info.put("height", image.getHeight());
        }
        String format = getFormat(file);
        info.put("format", format);
        info.put("fileSize", file.length());
        info.put("fileName", file.getName());
        return info;
    }

    /**
     * 计算宽高比字符串（如 "16:9", "4:3"）
     *
     * @param width  宽度
     * @param height 高度
     * @return 宽高比字符串
     */
    public static String getAspectRatio(int width, int height) {
        int gcd = gcd(width, height);
        return (width / gcd) + ":" + (height / gcd);
    }

    // ==================== 颜色处理 ====================

    /**
     * 获取图片的主色调（采样计算平均颜色）
     *
     * @param source 源图片
     * @return 主色调 Color 对象
     */
    public static Color getDominantColor(BufferedImage source) {
        // 先缩小到 50x50 再采样，提升性能
        BufferedImage small = resize(source, 50, 50, true);
        long totalR = 0, totalG = 0, totalB = 0;
        int count = 0;
        for (int x = 0; x < small.getWidth(); x++) {
            for (int y = 0; y < small.getHeight(); y++) {
                int rgb = small.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                if (alpha < 128) {
                    continue; // 跳过透明像素
                }
                totalR += (rgb >> 16) & 0xFF;
                totalG += (rgb >> 8) & 0xFF;
                totalB += rgb & 0xFF;
                count++;
            }
        }
        if (count == 0) {
            return Color.BLACK;
        }
        return new Color((int) (totalR / count), (int) (totalG / count), (int) (totalB / count));
    }

    /**
     * 将 Color 对象转为十六进制字符串（如 "#FF5733"）
     *
     * @param color 颜色
     * @return 十六进制颜色字符串
     */
    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    // ==================== 图片比较 ====================

    /**
     * 简单比较两张图片是否完全相同（逐像素比较）
     *
     * @param img1 图片 1
     * @param img2 图片 2
     * @return 完全相同返回 true
     */
    public static boolean equals(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    // ==================== 拼接 ====================

    /**
     * 水平拼接多张图片
     * <p>所有图片将被缩放到相同高度（取最大高度）</p>
     *
     * @param images 待拼接的图片数组
     * @return 拼接后的图片
     */
    public static BufferedImage mergeHorizontal(BufferedImage... images) {
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("至少需要一张图片");
        }
        int maxHeight = 0;
        int totalWidth = 0;
        for (BufferedImage img : images) {
            maxHeight = Math.max(maxHeight, img.getHeight());
        }
        // 统一高度
        BufferedImage[] resized = new BufferedImage[images.length];
        for (int i = 0; i < images.length; i++) {
            if (images[i].getHeight() == maxHeight) {
                resized[i] = images[i];
            } else {
                resized[i] = resizeByHeight(images[i], maxHeight);
            }
            totalWidth += resized[i].getWidth();
        }

        BufferedImage result = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try {
            int x = 0;
            for (BufferedImage img : resized) {
                g.drawImage(img, x, 0, null);
                x += img.getWidth();
            }
        } finally {
            g.dispose();
        }
        return result;
    }

    /**
     * 垂直拼接多张图片
     * <p>所有图片将被缩放到相同宽度（取最大宽度）</p>
     *
     * @param images 待拼接的图片数组
     * @return 拼接后的图片
     */
    public static BufferedImage mergeVertical(BufferedImage... images) {
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("至少需要一张图片");
        }
        int maxWidth = 0;
        int totalHeight = 0;
        for (BufferedImage img : images) {
            maxWidth = Math.max(maxWidth, img.getWidth());
        }
        // 统一宽度
        BufferedImage[] resized = new BufferedImage[images.length];
        for (int i = 0; i < images.length; i++) {
            if (images[i].getWidth() == maxWidth) {
                resized[i] = images[i];
            } else {
                resized[i] = resizeByWidth(images[i], maxWidth);
            }
            totalHeight += resized[i].getHeight();
        }

        BufferedImage result = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        try {
            int y = 0;
            for (BufferedImage img : resized) {
                g.drawImage(img, 0, y, null);
                y += img.getHeight();
            }
        } finally {
            g.dispose();
        }
        return result;
    }

    // ==================== 圆角 / 圆形 ====================

    /**
     * 生成圆角图片
     *
     * @param source       源图片
     * @param cornerRadius 圆角半径
     * @return 圆角图片
     */
    public static BufferedImage roundCorner(BufferedImage source, int cornerRadius) {
        int w = source.getWidth();
        int h = source.getHeight();
        BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
            g.setComposite(AlphaComposite.SrcIn);
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    /**
     * 生成圆形头像（居中裁剪为正方形后切圆）
     *
     * @param source 源图片
     * @return 圆形图片
     */
    public static BufferedImage circleAvatar(BufferedImage source) {
        BufferedImage square = cropToSquare(source);
        int size = square.getWidth();
        return roundCorner(square, size);
    }

    // ==================== 内部工具方法 ====================

    /**
     * 根据文件名获取图片格式
     *
     * @param fileName 文件名
     * @return 格式名
     */
    private static String getFormatByFileName(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) {
            return FORMAT_PNG;
        } else if (lower.endsWith(".gif")) {
            return FORMAT_GIF;
        } else if (lower.endsWith(".bmp")) {
            return FORMAT_BMP;
        }
        return FORMAT_JPEG;
    }

    /**
     * 计算最大公约数（辗转相除法）
     */
    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
