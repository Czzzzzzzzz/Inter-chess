package Chess;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class SendScreenImg extends Thread {
	public static int SERVERPORT = 8000;
	private ServerSocket serverSocket;
	private Robot robot;
	public Dimension screen;
	public Rectangle rect;
	private Socket socket;

	public static void main(String args[]) {
		new SendScreenImg(SERVERPORT).start();
	}

	// 构造方法 开启套接字连接 机器人robot 获取屏幕大小
	public SendScreenImg(int SERVERPORT) {
		try {
			serverSocket = new ServerSocket(SERVERPORT);
			serverSocket.setSoTimeout(864000000);
			robot = new Robot();
		} catch (Exception e) {
			e.printStackTrace();
		}
		screen = Toolkit.getDefaultToolkit().getScreenSize(); // 获取主屏幕的大小
		rect = new Rectangle(screen); // 构造屏幕大小的矩形
	}

	@Override
	public void run() {
		// 实时等待接收截屏消息
		while (true) {
			try {
				socket = serverSocket.accept();
				// System.out.println("有人进入观战");
				ZipOutputStream zip = new ZipOutputStream(new DataOutputStream(socket.getOutputStream()));
				zip.setLevel(9); // 设置压缩级别

				BufferedImage img = robot.createScreenCapture(rect);
				zip.putNextEntry(new ZipEntry("test.jpg"));
				ImageIO.write(img, "jpg", zip);
				if (zip != null)
					zip.close();
				// System.out.println("有人正在观战");

			} catch (IOException ioe) {
				System.out.println("连接断开");
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}