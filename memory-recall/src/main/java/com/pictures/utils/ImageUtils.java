package com.pictures.utils;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtils {

	public String path = "";

	public ImageUtils(String path) {
		this.path = path;
	}

	/**
	 * @param srcFile
	 * @param outFile
	 * @param x
	 *            point
	 * @param y
	 *            point
	 * @param width
	 * @param height
	 * @return
	 * @author Liang Echo
	 */
	public static boolean cutPic(String srcFile, String outFile, int x, int y, int width, int height) {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			// 如果源图片不存在
			if (!new File(srcFile).exists()) {
				return false;
			}

			// 读取图片文件
			is = new FileInputStream(srcFile);

			// 获取文件格式
			String ext = srcFile.substring(srcFile.lastIndexOf(".") + 1);

			// ImageReader声称能够解码指定格式
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(ext);
			ImageReader reader = it.next();

			// 获取图片流
			iis = ImageIO.createImageInputStream(is);

			// 输入源中的图像将只按顺序读取
			reader.setInput(iis, true);

			// 描述如何对流进行解码
			ImageReadParam param = reader.getDefaultReadParam();

			// 图片裁剪区域
			Rectangle rect = new Rectangle(x, y, width, height);

			// 提供一个 BufferedImage，将其用作解码像素数据的目标
			param.setSourceRegion(rect);

			// 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象
			BufferedImage bi = reader.read(0, param);

			// 保存新图片
			File tempOutFile = new File(outFile);
			if (!tempOutFile.exists()) {
				tempOutFile.mkdirs();
			}
			ImageIO.write(bi, ext, new File(outFile));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (iis != null) {
					iis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	@SuppressWarnings("resource")
	public static byte[] loadImage(String path) {
		try {
			File file = new File(path);
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        int bytesRead;
	        byte[] bytes = new byte[1024];
	        while ((bytesRead = bufferedInputStream.read(bytes)) > 0) {
	            byteArrayOutputStream.write(bytes, 0, bytesRead);
	        }
	        byte[] data = byteArrayOutputStream.toByteArray();
	        return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveImage(String path, byte[] image) {
		Path savePath = Paths.get(path);
		try {
			Files.write(savePath, image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
