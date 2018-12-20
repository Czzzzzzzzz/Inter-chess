package Chess;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ReceiveImages extends Thread {
	public BorderInit frame;
	public Socket socket;
	public String IP;

	public static void main(String[] args) {
		new ReceiveImages(new BorderInit(), "127.0.0.1").start();
	}

	public ReceiveImages(BorderInit frame, String IP) {
		this.frame = frame;
		this.IP = IP;
	}

	public void run() {
		while (frame.getFlag()) {
			try {
				socket = new Socket(IP, 8000);
				DataInputStream ImgInput = new DataInputStream(socket.getInputStream());
				ZipInputStream imgZip = new ZipInputStream(ImgInput);

				imgZip.getNextEntry(); // 到Zip文件流的开始处
				Image img = ImageIO.read(imgZip); // 按照字节读取Zip图片流里面的图片
				frame.jlbImg.setIcon(new ImageIcon(img));
				frame.validate();
				TimeUnit.MILLISECONDS.sleep(50);// 接收图片间隔时间
				imgZip.close();

			} catch (IOException | InterruptedException e) {
				System.out.println("连接断开");
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}

// 来显示收到的屏幕信息
class BorderInit extends JFrame {
	private static final long serialVersionUID = 1L;
	public JLabel jlbImg;
	private boolean flag;

	public boolean getFlag() {
		return this.flag;
	}

	public BorderInit() {
		this.flag = true;
		this.jlbImg = new JLabel();
		this.setTitle("观战");
		this.setSize(880, 760);// 通过截图比对的最佳大小
		// this.setAlwaysOnTop(true); // 显示窗口始终在最前面
		this.add(jlbImg);
		//this.setLocationRelativeTo(null);
		this.setLocation(900,200);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.validate();

		// 窗口关闭事件
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				flag = false;
				BorderInit.this.dispose();
				System.out.println("窗体关闭");
				System.gc(); // 垃圾回收，节省资源
			}
		});
	}
}