package com.anemortalkid.imshow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Imshow is a java based implementation of the OpenCV imshow() function, since
 * OpenCV for java did not include this function under {@link Highgui}, or
 * {@link Imgproc}, I decided to write my own. It basically just renders a
 * BufferedImage on a JFrame
 * 
 * @author jan_monterrubio
 * 
 */
public class Imshow {

	private static final int DEF_HEIGHT = 800;
	private static final int DEF_WIDTH = 600;

	/**
	 * Displays the given {@link Mat} in a frame of the default size
	 * 
	 * @param matrix
	 *            the {@link Mat} to display
	 */
	public static void imshow(Mat matrix) {
		BufferedImage image = toBufferedImage(matrix);
		imshow(image);
	}

	/**
	 * Displays the given {@link BufferedImage} in a frame of the default size
	 * 
	 * @param image
	 *            the {@link BufferedImage} to display
	 */
	public static void imshow(BufferedImage image) {
		JFrame imshowFrame = getFrameWithImage(image);
		imshowFrame.setVisible(true);
	}

	/**
	 * Displays the given {@link Mat} in a frame of the default size with the
	 * given title
	 * 
	 * @param title
	 *            the title of the frame
	 * @param matrix
	 *            the {@link Mat} to display
	 */
	public static void imshow(String title, Mat matrix) {
		BufferedImage image = toBufferedImage(matrix);
		imshow(title, image);
	}

	/**
	 * Displays the given {@link Mat} in a frame of the specified
	 * windowDimension size with the given title
	 * 
	 * @param title
	 *            the title of the frame
	 * @param matrix
	 *            the {@link Mat} to display
	 * @param windowDimension
	 *            the {@link Dimension} to set the image frame to
	 */
	public static void imshow(String title, Mat matrix,
			Dimension windowDimension) {
		BufferedImage image = toBufferedImage(matrix);
		imshow(title, image, windowDimension);
	}

	/**
	 * Displays the given {@link BufferedImage} in a frame of the default size
	 * with the given title
	 * 
	 * @param title
	 *            the title of the frame
	 * @param image
	 *            the {@link BufferedImage} to display
	 */
	public static void imshow(String title, BufferedImage image) {
		JFrame imshowFrame = getFrameWithImage(image);
		imshowFrame.setTitle(title);
		imshowFrame.setVisible(true);
	}

	/**
	 * Displays the given {@link BufferedImage} in a frame of the specified
	 * windowDimension with the given title
	 * 
	 * @param title
	 *            the title of the frame
	 * @param image
	 *            the {@link BufferedImage} to display
	 * @param windowDimension
	 *            the {@link Dimension} to set the image frame to
	 */
	public static void imshow(String title, BufferedImage image,
			Dimension windowDimension) {
		JFrame imshowFrame = getFrameWithImage(image);
		imshowFrame.setTitle(title);
		imshowFrame.setPreferredSize(windowDimension);
		imshowFrame.setVisible(true);
	}

	/*
	 * Constructs a JFrame with an ImShowPanel and returns it so we can
	 * customize it further
	 */
	private static JFrame getFrameWithImage(BufferedImage image) {
		ImgShowPanel panel = new ImgShowPanel(image);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		frame.setSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
		return frame;
	}

	/**
	 * Converts the desired inputMat to a {@link BufferedImage} so we can see it
	 * visually
	 * 
	 * @param inputMat
	 *            the Matrix to convert to a {@link BufferedImage}
	 * @return a {@link BufferedImage} with the contents of the matrix
	 */
	private static BufferedImage toBufferedImage(Mat inputMat) {
		BufferedImage bi;
		int height = inputMat.rows();
		int width = inputMat.cols();
		byte[] imgData = new byte[(int) (inputMat.cols() * inputMat.rows() * inputMat
				.elemSize())];
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (inputMat.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}

		// Copy the data into the matrix
		inputMat.get(0, 0, imgData);

		bi = new BufferedImage(width, height, type);

		bi.getRaster().setDataElements(0, 0, width, height, imgData);
		return bi;
	}

	/**
	 * Creates a {@link Mat} which represents the given BufferedImage
	 * 
	 * @param bufferedImage
	 *            the buffered image to convert to a matrix
	 * @return a Matrix representing the given buffered image
	 */
	private static Mat toMatrix(BufferedImage bufferedImage) {
		int imageType = bufferedImage.getType();
		int matrixType = CvType.CV_8UC3;
		if (imageType == BufferedImage.TYPE_BYTE_GRAY)
			matrixType = CvType.CV_8UC1;

		byte[] pixels = ((DataBufferByte) bufferedImage.getRaster()
				.getDataBuffer()).getData();

		Mat matrix = new Mat(new Size(bufferedImage.getWidth(),
				bufferedImage.getHeight()), matrixType);
		matrix.put(0, 0, pixels);
		return fromBGR2RGB(matrix);
	}

	/**
	 * Converts a BGR Mat to RGB.
	 * 
	 * @param mat
	 *            the matrix from GRB to convert to RGB
	 * @return a matrix that is in RGB form
	 */
	public static Mat fromBGR2RGB(Mat mat) {
		Mat fixed = new Mat();
		Imgproc.cvtColor(mat, fixed, Imgproc.COLOR_BGR2RGB);
		return fixed;
	}

	/**
	 * An {@link ImgShowPanel} is a {@link JPanel} which renders the given
	 * {@link BufferedImage} to the size of its container
	 * 
	 * @author jan_monterrubio
	 * 
	 */
	private static class ImgShowPanel extends JPanel {

		/**
		 * Generated Serial Version
		 */
		private static final long serialVersionUID = -9051069073482301836L;

		/**
		 * Image to reference
		 */
		private BufferedImage image;

		/**
		 * Constructs an {@link ImgShowPanel} with the given
		 * {@link BufferedImage}
		 * 
		 * @param image
		 *            the {@link BufferedImage} to display within this
		 *            {@link ImgShowPanel}
		 */
		private ImgShowPanel(BufferedImage image) {
			this.image = image;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		}
	}
}
