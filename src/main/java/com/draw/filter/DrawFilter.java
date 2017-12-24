package com.draw.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 绘制编号图片（未启用，使用DownloadFilter）
 * @author luoyong
 * @date 2017-12-24
 */
public class DrawFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(DrawFilter.class.getName());

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		// 路径名称
		String jobNumber = StringUtils.substringAfter(req.getServletPath(), "/draw/");
		if (!StringUtils.isNumeric(jobNumber)) {
			res.sendError(403);
			return;
		}
		LOG.info("draw job number: ", jobNumber);
		try (OutputStream out = res.getOutputStream()) {
			res.setContentType("application/x-download");
			// 设置文件名
			res.addHeader("Content-Disposition", "attachment;filename=" + jobNumber + ".jpg");
			BufferedImage bi = createImage(jobNumber);
			ImageIO.write(bi, "JPG", out);
		} catch (FileNotFoundException e) {
			LOG.error("draw job number error：", e);
			res.sendError(404);
		}
	}

	/**
	 * 创建图片
	 * @param content 内容
	 * @return
	 */
	private static BufferedImage createImage(String content) {
		int size = 240;
		if (StringUtils.isNumeric(content) && content.length() > 2) {
			size = content.length() * 80;
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
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, width, height);
		g2.setPaint(new Color(27, 15, 99));
		FontRenderContext context = g2.getFontRenderContext();
		Rectangle2D bounds = font.getStringBounds(content, context);
		float x = (float) ((width - bounds.getWidth()) / 2);
		float y = (float) (height / 2 + 45.5);
		g2.drawString(content, x, y);
		return bi;
	}
}
