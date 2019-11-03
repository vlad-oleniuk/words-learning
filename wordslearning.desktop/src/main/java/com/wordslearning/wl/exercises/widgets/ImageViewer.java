package com.wordslearning.wl.exercises.widgets;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class ImageViewer extends JPanel {

	private Image img;
	private double imageRatio;
	private int imgOriginalHeight;
	private int imgOriginalWidth;

	public void setImage(Image image) {
		imgOriginalHeight = image.getHeight(this);
		imgOriginalWidth = image.getWidth(this);
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
			int h) {
		this.img = img;
		imgOriginalHeight = h;
		imgOriginalWidth = w;
		imageRatio = new Double(imgOriginalHeight) / imgOriginalWidth;
		return super.imageUpdate(img, infoflags, x, y, w, h);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		int imgWidth, imgHeight;
		boolean needScale = imgOriginalHeight > getSize().height
				|| imgOriginalWidth > getSize().width;
		if (needScale) {
			double frameRatio = new Double(getSize().height) / getSize().width;
			/*
			 * scale keeping image ratio, so that it could be paint in the
			 * current frame
			 */
			if (imageRatio > frameRatio) {
				// if picture is proportionally higher - limit vertically
				imgHeight = getSize().height;
				imgWidth = (int) (imgHeight / imageRatio);
			} else {
				imgWidth = getSize().width;
				imgHeight = (int) (imageRatio * imgWidth);
			}
		} else {
			imgWidth = imgOriginalWidth;
			imgHeight = imgOriginalHeight;
		}
		g2.drawImage(img, (getSize().width - imgWidth) / 2,
				(getSize().height - imgHeight) / 2, imgWidth, imgHeight, this);
	}
}
