
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
 * 文件下载
 * @author luoyong
 * @date 2017-12-24
 */
public class DownloadFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadFilter.class.getName());

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
		String jobNumber = StringUtils.substringAfter(req.getServletPath(), "/download/");
		if (!StringUtils.isNumeric(jobNumber)) {
			res.sendError(403);
			return;
		}
		LOG.info("download job number: " + jobNumber);
		try (OutputStream out = res.getOutputStream()) {
			res.setContentType("application/x-download");
			// 设置文件名
			res.addHeader("Content-Disposition", "attachment;filename=" + jobNumber + ".jpg");
			BufferedImage bi = createImage(jobNumber);
			ImageIO.write(bi, "JPG", out);
			// out.write((byte[]) b);
			out.flush();
		} catch (FileNotFoundException e) {
			LOG.error("生成编号图片出错：", e);
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
		if (StringUtils.isNumeric(content)) {
			if (content.length() > 2) {
				size = content.length() * 80;
			}
		}
		Integer width = size;
		Integer height = size;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 字体
		Font font = new Font("Calibri", Font.BOLD + Font.ITALIC, 140);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		g2.setFont(font);
		// 消除java.awt.Font字体的锯齿
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, width, height);
		g2.setPaint(new Color(27, 15, 99));
		FontRenderContext context = g2.getFontRenderContext();
		Rectangle2D bounds = font.getStringBounds(content, context);
		double x = (width - bounds.getWidth()) / 2;
		double y = (height - bounds.getHeight()) / 2;
		double ascent = -bounds.getY();
		double baseY = y + ascent;
		g2.drawString(content, (int) x, (height - 92) / 2 + 92);
		return bi;
	}
}
