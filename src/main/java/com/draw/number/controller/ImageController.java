package com.draw.number.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 绘制并下载编号图片
 * @author luoyong
 * @date 2021/11/23 22:26
 */
@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {

	@GetMapping(path = "/draw/{numberLetters:^[A-Za-z0-9]+$}")
	public void drawImage(@PathVariable String numberLetters, HttpServletResponse response) throws IOException {
		log.info("draw numberLetters: ", numberLetters);
		try (OutputStream out = response.getOutputStream()) {
			response.setContentType("application/x-download");
			// 设置文件名
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + numberLetters + ".jpg");
			BufferedImage bi = createImage(numberLetters);
			ImageIO.write(bi, "JPG", out);
		} catch (FileNotFoundException e) {
			log.error("draw number error：", e);
			response.sendError(404);
		}
	}


	/**
	 * 创建图片
	 * @param numberLetters 只能包含数字、字母
	 * @return
	 */
	private static BufferedImage createImage(String numberLetters) {
		int size = 240;
		if (numberLetters.length() > 2) {
			size = numberLetters.length() * 80;
		}
		Integer width = size;
		Integer height = size;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		// 字体
		Font font = new Font("Calibri", Font.BOLD + Font.ITALIC, 140);
		g2.setFont(font);
		// 消除java.awt.Font字体的锯齿
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, width, height);
		g2.setPaint(new Color(27, 15, 99));
		FontRenderContext context = g2.getFontRenderContext();
		Rectangle2D bounds = font.getStringBounds(numberLetters, context);

		//		float x = (float) ((width - bounds.getWidth()) / 2);
		//		float y = (float) (height / 2 + 45.5);
		//		g2.drawString(number, x, y);
		//		return bi;

		double x = (width - bounds.getWidth()) / 2;
		double y = (height - bounds.getHeight()) / 2;
		double ascent = -bounds.getY();
		double baseY = y + ascent;
		g2.drawString(numberLetters, (int) x, (height - 92) / 2 + 92);
		return bi;
	}
}

