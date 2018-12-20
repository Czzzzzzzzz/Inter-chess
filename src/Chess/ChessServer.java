package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

// 服务器界面类
public class ChessServer extends Frame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ServerMsgPanel serverMsgPanel = new ServerMsgPanel();

	ServerSocket serverSocket;

	@SuppressWarnings("rawtypes")
	Hashtable clientDataHash = new Hashtable(50); // 将客户端套接口和输出流绑定

	@SuppressWarnings("rawtypes")
	Hashtable clientNameHash = new Hashtable(50); // 将客户端套接口和客户名绑定

	@SuppressWarnings("rawtypes")
	Hashtable chessPeerHash = new Hashtable(100); // 将游戏创建者和游戏加入者绑定

	// 将游戏创建者绑定
	@SuppressWarnings("rawtypes")
	Hashtable chessCreateGameHash = new Hashtable(50);

	// 将对战玩家与台号绑定
	@SuppressWarnings("rawtypes")
	Hashtable chessPlayGameHash = new Hashtable(50);

	// 将台号和观战状态绑定
	@SuppressWarnings("rawtypes")
	Hashtable shareHash = new Hashtable(50);

	// 处理回收台号
	public static int[] terracelist = new int[50];

	// 台号数组为空
	public static boolean isnull = true;

	public ChessServer() {
		super("Java 五子棋服务器");
		setBackground(Color.LIGHT_GRAY);
		add(serverMsgPanel, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		pack();
		setVisible(true);
		setSize(800, 600);
		setResizable(false);
		validate();

		try {
			createServer(10086, serverMsgPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 用指定端口和面板创建服务器
	@SuppressWarnings("unchecked")
	public void createServer(int port, ServerMsgPanel serverMsgPanel) throws IOException {
		Socket clientSocket; // 客户端套接口
		this.serverMsgPanel = serverMsgPanel; // 设定当前主机
		try {
			serverSocket = new ServerSocket(port);
			serverMsgPanel.msgTextArea.setText("服务器启动于:" + InetAddress.getLocalHost() + ":" // djr
					+ serverSocket.getLocalPort() + "\n");
			while (true) {
				// 监听客户端套接口的信息
				clientSocket = serverSocket.accept();
				serverMsgPanel.msgTextArea.append("已连接用户:" + clientSocket + "\n");
				// 建立客户端输出流
				ObjectOutputStream outputData = new ObjectOutputStream(clientSocket.getOutputStream());
				// 将客户端套接口和输出流绑定
				clientDataHash.put(clientSocket, outputData);
				// 将客户端套接口和客户名绑定
				// 创建并运行服务器端线程
				ChessServerThread thread = new ChessServerThread(clientSocket, clientDataHash, clientNameHash,
						chessCreateGameHash, chessPlayGameHash, chessPeerHash, shareHash, serverMsgPanel);
				thread.start();
			}
		} catch (IOException ex) {
		}
	}

	@SuppressWarnings("unused")
	public static void main(String args[]) {
		ChessServer chessServer = new ChessServer();
	}
}
