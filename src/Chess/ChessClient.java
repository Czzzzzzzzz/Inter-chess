package Chess;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jasypt.util.text.BasicTextEncryptor;

import Chat.ChatLogin;
import Login.Jdbc;

// 五子棋客户端
public class ChessClient extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 客户端套接口
	Socket clientSocket;

	// 数据输入流
	ObjectInputStream inputStream;

	// 数据输出流
	ObjectOutputStream outputStream;

	// 用户名
	String chessClientName = null;

	// 主机地址
	String host = null;

	// 主机端口
	int port = 10086;

	// 是否在聊天
	boolean isOnChat = false;

	// 是否在下棋
	boolean isOnChess = false;

	// 游戏是否进行中
	boolean isGameConnected = false;

	// 是否为游戏创建者
	boolean isCreator = false;

	// 是否为游戏加入者
	boolean isParticipant = false;

	// 是否可挑战
	boolean isChallenge = false;

	// chessClientThread
	ChessClientThread clientthread;

	// 是否启动观战线程
	boolean isStartShareThread = false;

	// 用户名
	static String username;

	// 用户列表区
	userJTable userJTablePad;

	// 用户操作区
	UserControlPad userControlPad = new UserControlPad();

	// 下棋区
	ChessPad chessPad = new ChessPad();

	// 面板区
	Panel southPanel = new Panel();

	Panel northPanel = new Panel();

	Panel centerPanel = new Panel();

	Panel eastPanel = new Panel();

	Jdbc b = new Jdbc();
	// 配置文件的位置
	String path = "dbinfo.properties";
	String code;// 兑换码

	// 构造方法，创建界面
	@SuppressWarnings("static-access")
	public ChessClient(String username) {

		super("Java 五子棋客户端");
		this.username = username;
		setLayout(new BorderLayout());
		host = userControlPad.ipInputted.getText();

		// 添加jtable
		userJTablePad = new userJTable();
		eastPanel.setLayout(new BorderLayout());
		eastPanel.add(userJTablePad, BorderLayout.NORTH);

		// 添加画板
		chessPad.host = userControlPad.ipInputted.getText();
		centerPanel.add(chessPad, BorderLayout.CENTER);

		// 添加button事件
		userControlPad.connectButton.addActionListener(this);
		userControlPad.createButton.addActionListener(this);
		userControlPad.joinButton.addActionListener(this);
		userControlPad.chatButton.addActionListener(this);
		userControlPad.resetButton.addActionListener(this);
		userControlPad.save.addActionListener(this);
		userControlPad.top.addActionListener(this);
		userControlPad.instructions.addActionListener(this);
		userControlPad.allow.addActionListener(this);
		userControlPad.share.addActionListener(this);
		userControlPad.createButton.setEnabled(false);
		userControlPad.joinButton.setEnabled(false);
		userControlPad.top.setVisible(false);
		userControlPad.save.setVisible(false);
		userControlPad.chatButton.setVisible(false);
		userControlPad.allow.setVisible(false);
		userControlPad.share.setVisible(false);
		southPanel.add(userControlPad, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (isOnChat) { // 聊天中
					try { // 关闭客户端套接口
						clientSocket.close();
					} catch (Exception ed) {
					}
				}
				if (isOnChess || isGameConnected) { // 下棋中
					try { // 关闭下棋端口
						chessPad.chessSocket.close();
					} catch (Exception ee) {
					}
				}
				System.exit(0);
			}
		});

		add(eastPanel, BorderLayout.EAST);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		pack();
		setSize(670, 500);
		setVisible(true);
		setResizable(false);
		this.validate();
	}

	// 按指定的IP地址和端口连接到服务器
	public boolean connectToServer(String serverIP, int serverPort) throws Exception {
		try {
			// 创建客户端套接口
			clientSocket = new Socket(serverIP, serverPort);
			// 创建输入流
			inputStream = new ObjectInputStream(clientSocket.getInputStream());
			// 创建输出流
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			// 创建客户端线程
			clientthread = new ChessClientThread(this, username);
			System.out.println(username);
			// 启动线程，等待信息
			clientthread.start();
			isOnChat = true;
			return true;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "不能连接");
		}
		return false;
	}

	// 断开与服务器连接
	public boolean disconnectToServer(Socket clientSocket, ObjectInputStream inputStream,
			ObjectOutputStream outputStream) throws Exception {
		try {
			this.clientSocket = clientSocket;
			this.inputStream = inputStream;
			this.outputStream = outputStream;
			if (this.inputStream != null) {
				this.inputStream.close();
			}
			if (this.outputStream != null) {
				this.outputStream.close();
			}
			if (this.clientSocket != null) {
				this.clientSocket.close();
			}
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "客户端异常，正在退出，请重新登录！");
			System.exit(0);
		}
		return false;
	}

	// 客户端事件处理
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
		// 加载配置文件dbinfo.properties
		InputStream in = Jdbc.class.getClassLoader().getResourceAsStream(path);
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (e.getSource() == userControlPad.connectButton) { // 连接到主机按钮单击事件
			host = chessPad.host = userControlPad.ipInputted.getText(); // 取得主机地址
			String connectText = userControlPad.connectButton.getText();// 获取button文字
			try {
				if (connectText.equals("连接")) {
					if (connectToServer(host, port)) { // 成功连接到主机时，设置客户端相应的界面状态
						userControlPad.connectButton.setEnabled(false);
						userControlPad.connectButton.setVisible(false);
						userControlPad.createButton.setEnabled(true);
						userControlPad.createButton.setEnabled(true);
						userControlPad.joinButton.setEnabled(true);
						userControlPad.chatButton.setEnabled(true);
						userControlPad.chatButton.setVisible(true);
						userControlPad.top.setEnabled(true);
						userControlPad.top.setVisible(true);
						userControlPad.save.setEnabled(true);
						userControlPad.save.setVisible(false);
						userControlPad.ipInputted.setVisible(false);
						userControlPad.ipLabel.setVisible(false);
						userControlPad.share.setVisible(true);
						chessPad.setName(username);
						int level = b.level(username);
						super.setTitle(String.format("欢迎  %s 进入游戏大厅！", username) + "当前等级lv" + level);
						chessPad.statusText.setText(String.format("连接成功，欢迎  %s 进入游戏大厅！", username));
					}
				} else if (connectText.equals("断开连接")) {
					if (disconnectToServer(clientSocket, inputStream, outputStream)) {
						chessPad.disconnectServer(chessPad.chessSocket, chessPad.inputData, chessPad.outputData);
						JOptionPane.showInternalMessageDialog(null, "你已断开与服务器连接，请重新启动客户端！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
				}
			} catch (Exception ei) {
				JOptionPane.showMessageDialog(null, "服务器出了点小差...");
			}
		}
		// 改写
		if (e.getSource() == userControlPad.joinButton) { // 加入游戏按钮单击事件
			// 取得要加入的游戏用户名和状态
			String oppoName = userJTablePad.username;
			System.out.println(oppoName);
			String statu = userJTablePad.statu;
			if (oppoName == null || oppoName.equals(username)) {
				JOptionPane.showMessageDialog(null, "必须选择一个用户");
			} else {
				// 判断加入的对局是否创建游戏状态
				if (statu.equals("0")) {
					try {
						if (!isGameConnected) {
							if (!chessPad.connectServer(chessPad.host, chessPad.port)) {
								JOptionPane.showInternalMessageDialog(null, "无法加入游戏，请重新登录！", "提示",
										JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}
							isGameConnected = true;
						}
						isOnChess = true;
						isParticipant = true;
						userControlPad.createButton.setEnabled(false);
						userControlPad.createButton.setVisible(false);
						userControlPad.joinButton.setEnabled(false);
						userControlPad.top.setVisible(false);
						// 发送加入游戏请求
						chessPad.chessThread.sendMessage("/joingame " + oppoName + " " + username);
					} catch (Exception ee) {
						isGameConnected = false;
						isOnChess = false;
						isParticipant = false;
						userControlPad.createButton.setEnabled(true);
						userControlPad.joinButton.setEnabled(true);
						JOptionPane.showMessageDialog(null, "不能连接，请重启客户端");
					}
				} else {
					JOptionPane.showInternalMessageDialog(null, "对方正在游戏，你可以进行观战", "提示",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		// 根据button的text来调用对应方法
		if (e.getSource() == userControlPad.createButton) { // 创建游戏按钮单击事件
			try {
				String createButtonText = userControlPad.createButton.getText();
				if (createButtonText.equals("创建")) {
					if (!isGameConnected) {
						if (!chessPad.connectServer(chessPad.host, chessPad.port)) {
							JOptionPane.showInternalMessageDialog(null, "无法创建游戏，请重新登录！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						}
						isGameConnected = true;
					}
					isOnChess = true;
					isCreator = true;
					userControlPad.connectButton.setEnabled(false);
					userControlPad.createButton.setText("取消创建");
					userControlPad.joinButton.setEnabled(false);
					userControlPad.joinButton.setVisible(false);
					userControlPad.share.setVisible(false);
					userControlPad.top.setVisible(false);
					chessPad.chessThread.sendMessage("/creategame " + username);
				} else if (createButtonText.equals("取消创建")) {
					chessPad.chessThread.sendMessage("/cancel " + username);
					isOnChess = false;
					isCreator = false;
					userControlPad.connectButton.setEnabled(true);
					userControlPad.createButton.setText("创建");
					userControlPad.joinButton.setEnabled(true);
					userControlPad.joinButton.setVisible(true);
					userControlPad.top.setVisible(true);
					userControlPad.share.setVisible(true);
					userControlPad.allow.setVisible(false);
				}
			} catch (Exception ec) {
				isGameConnected = false;
				isOnChess = false;
				isCreator = false;
				userControlPad.createButton.setEnabled(true);
				userControlPad.joinButton.setEnabled(true);
				ec.printStackTrace();
				JOptionPane.showMessageDialog(null, "不能连接");
			}
		}
		if (e.getSource() == userControlPad.chatButton) {
			ChatLogin lg = new ChatLogin(username);
		}
		if (e.getSource() == userControlPad.resetButton) {
			b.reset(username);
			System.exit(0);
		}
		if (e.getSource() == userControlPad.top) {
			code = properties.getProperty("code");
			// 解密 符合RSA标准的基于密码的加密
			BasicTextEncryptor textEncryptor2 = new BasicTextEncryptor();
			textEncryptor2.setPassword("password");
			int charge = JOptionPane.showConfirmDialog(null, "骚年渴望力量吗？", "变强之路", JOptionPane.YES_NO_CANCEL_OPTION);
			// 如果这个整数等于JOptionPane.YES_OPTION，则说明你点击的是“确定”按钮，则允许继续操作，否则结束
			if (charge == JOptionPane.YES_OPTION) {
				int ConversionCode = Integer
						.parseInt(JOptionPane.showInputDialog(null, "请输入兑换码", "兑换码", JOptionPane.QUESTION_MESSAGE));
				if (ConversionCode == Integer.valueOf(textEncryptor2.decrypt(code))) {// 兑换码是不是一致
					clientthread.sendMessage("/updateScore" + username);
				} else {
					JOptionPane.showMessageDialog(null, "我觉得你还会回来的");
				}
			} else {
				JOptionPane.showMessageDialog(null, "我觉得你还会回来的");
			}
		}
		if (e.getSource() == userControlPad.save) {
			String fileName = "save";
			String folder = "save.png";
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle screenRectangle = new Rectangle(screenSize);
			Robot robot = null;
			try {
				robot = new Robot();
				BufferedImage image = robot.createScreenCapture(screenRectangle);
				// 保存路径
				File screenFile = new File(fileName);
				if (!screenFile.exists()) {
					screenFile.mkdir();
				}
				File f = new File(screenFile, folder);
				ImageIO.write(image, "png", f);
				// 自动打开
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
					Desktop.getDesktop().open(f);
			} catch (AWTException | IOException e1) {
			}

		}
		if (e.getSource() == userControlPad.instructions) {
			JOptionPane.showMessageDialog(null, "先手为黑子，游戏胜利加5分。" + "\n" + "适度游戏益智，沉迷游戏伤身！");
		}
		if (e.getSource() == userControlPad.allow) {// 允许观战
			int charge = JOptionPane.showConfirmDialog(null, "是否选择开放观战？", "观战", JOptionPane.YES_NO_CANCEL_OPTION);
			if (charge == JOptionPane.YES_OPTION) {
				if (!isStartShareThread) {
					new SendScreenImg(8000).start();
					isStartShareThread = true;
				}
				chessPad.chessThread.sendMessage("/share" + username);
				userControlPad.allow.setEnabled(false);
				JOptionPane.showMessageDialog(null, "已开放观战");
			} else {
				JOptionPane.showMessageDialog(null, "取消开放观战");
			}
		}
		if (e.getSource() == userControlPad.share) {// 观战
			String oppoName = userJTablePad.username;
			String share = userJTablePad.share;
			if (oppoName == null || oppoName.equals(username)) {
				JOptionPane.showMessageDialog(null, "必须选择一个用户");
			} else {
				if (share.equals("yes")) {
					new ReceiveImages(new BorderInit(), "127.0.0.1").start();
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
							// this.setLocationRelativeTo(null);
							this.setLocation(900, 200);// 用尺子慢慢量的
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
				} else {
					JOptionPane.showMessageDialog(null, "该对战没有开启观战");
				}
			}
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

}
