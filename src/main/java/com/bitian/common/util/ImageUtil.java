package com.bitian.common.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
/**
 * 图片处理类
 * @author 何浩
 *
 */
public class ImageUtil {

	
	/**
	 * 将图片进行缩放
	 * @param source 源图片
	 * @param targetW 目标长度
	 * @param targetH 目标宽度
	 * @param db 是否等比缩放
	 * @return 缩放后的图片
	 */
	public static BufferedImage resize(BufferedImage source, int targetW,
			int targetH,boolean db) {
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		if(db){
			if (sx > sy) {
				sx = sy;
				targetW = (int) (sx * source.getWidth());
			} else {
				sy = sx;
				targetH = (int) (sy * source.getHeight());
			}
		}
		
		if (type == BufferedImage.TYPE_CUSTOM) { // handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
					targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else
			target = new BufferedImage(targetW, targetH, type);
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}

	public static void saveImageAsJpg(String fromFileStr, String saveToFileStr,
			int width, int hight) throws Exception {
		BufferedImage srcImage;
		String imgType = "JPEG";
		if (fromFileStr.toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		File saveFile = new File(saveToFileStr);
		File fromFile = new File(fromFileStr);
		srcImage = ImageIO.read(fromFile);
		if (width > 0 || hight > 0) {
			srcImage = resize(srcImage, width, hight,false);
		}
		ImageIO.write(srcImage, imgType, saveFile);
	}
	
	public static void saveImageAsJpg(File fromFile, File saveToFile,
			int width, int hight) throws Exception {
		BufferedImage srcImage;
		String imgType = "JPEG";
		if (fromFile.getPath().toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		srcImage = ImageIO.read(fromFile);
		if (width > 0 || hight > 0) {
			srcImage = resize(srcImage, width, hight,false);
		}
		ImageIO.write(srcImage, imgType, saveToFile);
	}

	public static boolean isTransparent(BufferedImage bufferedImage) {
		boolean isTransparent = false;
		int height = bufferedImage.getHeight();
		int width = bufferedImage.getWidth();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int pixel = bufferedImage.getRGB(i, j);
				if(pixel >> 24 == 0) {
					isTransparent = true;
					break;
				}
			}
		}
		return isTransparent;
	}

}
