package Chess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import Login.Jdbc;

public class ChessServerThread extends Thread {
	Socket clientSocket; // 保存客户端套接口信息

	@SuppressWarnings("rawtypes")
	Hashtable clientDataHash; // 保存客户端端口与输出流对应的Hash

	@SuppressWarnings("rawtypes")
	Hashtable clientNameHash; // 保存客户端套接口和客户名对应的Hash

	@SuppressWarnings("rawtypes")
	Hashtable chessPeerHash; // 保存游戏创建者和游戏加入者对应的Hash
	
	@SuppressWarnings("rawtypes")
	Hashtable shareHash;

	// 保存创建游戏用户Hash
	@SuppressWarnings("rawtypes")
	Hashtable chessCreateGameHash;

	// 保存正在游戏用户
	@SuppressWarnings("rawtypes")
	Hashtable chessPlayGameHash;

	ServerMsgPanel serverMsgPanel;

	boolean isClientClosed = false;

	Jdbc b = new Jdbc();

	// 合并二维数组方法
	public static String[][] addElement(String[][] tda1, String[][] tda2) {
		int length = tda1.length + tda2.length;
		String[][] tda = new String[length][5];
		int i;
		// tda1 -> tda
		for (i = 0; i < tda1.length; i++) {
			for (int j = 0; j < 5; j++) {
				tda[i][j] = tda1[i][j];
			}
		}
		// tda2 -> tda
		for (; i - tda1.length < tda2.length; i++) {
			for (int j = 0; j < 5; j++) {
				tda[i][j] = tda2[i - tda1.length][j];
			}
		}
		return tda;
	}

	@SuppressWarnings("rawtypes")
	public ChessServerThread(Socket clientSocket, Hashtable clientDataHash, Hashtable clientNameHash,
			Hashtable chessCreateGameHash, Hashtable chessPlayGameHash, Hashtable chessPeerHash, Hashtable shareHash, ServerMsgPanel server) {
		this.clientSocket = clientSocket;
		this.clientDataHash = clientDataHash;
		this.clientNameHash = clientNameHash;
		this.chessPeerHash = chessPeerHash;
		this.chessCreateGameHash = chessCreateGameHash;
		this.chessPlayGameHash = chessPlayGameHash;
		this.shareHash = shareHash;
		this.serverMsgPanel = server;
	}

	// 接收客户端信息
	@SuppressWarnings("unchecked")
	public void dealWithMsg(String msgReceived) {
		if (msgReceived.startsWith("/")) {
			if (msgReceived.equals("/list")) { // 收到的信息为更新用户列表
				Feedback(getUserList());
			} else if (msgReceived.startsWith("/creategame ")) { // 收到的信息为创建游戏
				String gameCreaterName = msgReceived.substring(12);
				synchronized (clientNameHash) { // 将用户端口放到用户列表中
					clientNameHash.put(gameCreaterName + "[inchess]", clientSocket);
				}
				synchronized (chessCreateGameHash) {
					// 添加用户到创建用户列表中
					chessCreateGameHash.put(gameCreaterName + "[inchess]", clientSocket);
				}
				sendGameStatusPeerMsg(gameCreaterName, "/OK");
				sendPublicMsg(getUserList());
			} else if (msgReceived.startsWith("/joingame ")) { // 收到的信息为加入游戏时
				String info = msgReceived.substring(10);
				// 获取创建游戏用户名
				String opponame = info.split(" ")[0];
				// 获取加入对战用户名
				String username = info.split(" ")[1];
				// 获取台号
				String tarrace = null;
				int hashSize = chessPlayGameHash.size();
				if (!ChessServer.isnull) {
					tarrace = String
							.valueOf(ChessServer.terracelist[(int) (Math.random() * ChessServer.terracelist.length)]);
				} else {
					tarrace = String
							.valueOf(hashSize != 0 ? (hashSize % 2 == 0 ? hashSize / 2 + 1 : (hashSize + 1) / 2 + 1) : 1);
				}
				// 将创建游戏者移出创建游戏状态
				synchronized (chessCreateGameHash) {
					chessCreateGameHash.remove(opponame + "[inchess]");
				}
				// 添加加入游戏者
				synchronized (clientNameHash) {
					clientNameHash.put(username + "[inchess]", clientSocket);
				}
				// 将创建者和加入者台号进行绑定
				synchronized (chessPlayGameHash) {
					chessPlayGameHash.put(opponame + "[inchess]", tarrace);
					chessPlayGameHash.put(username + "[inchess]", tarrace);
				}
				// 将创建者和加入者
				synchronized (chessPeerHash) {
					chessPeerHash.put(opponame, username);
					chessPeerHash.put(username, opponame);
				}
				
				sendPublicMsg(getUserList());
				sendGameStatusPeerMsg(username, ("/peer " + "[inchess]" + opponame));
				sendGameStatusPeerMsg(opponame, ("/peer " + "[inchess]" + username));
			} else if (msgReceived.startsWith("/[inchess]")) { // 传送信息
				String opponame = msgReceived.split(" ")[0].substring(10);
				int opponameLength = opponame.length();
				String gameMessage = msgReceived.substring((opponameLength + 11));
				if (!sendGameStatusPeerMsg(opponame + "[inchess]", gameMessage)) {
					Feedback("/error");
				}
			}
			// 让客户端发送username，使clientSocket与username进行绑定
			else if (msgReceived.startsWith("/sendname ")) {
				// 获取用户名
				String username = msgReceived.substring(10);
				// 将用户名和socket绑定
				clientNameHash.put(username, clientSocket);
				// 更新用户列表
				sendPublicMsg(getUserList());
			} else if (msgReceived.startsWith("/updateScore")) {
				String username = msgReceived.substring(12);
				b.Somefarm(username);
				sendPublicMsg(getUserList());
				Feedback("/updateSuccess");
			} else if (msgReceived.startsWith("/cancel")) {
				// 获取用户名
				String username = msgReceived.substring(8);
				// 在chessCreateGameHash中移除用户
				synchronized (chessCreateGameHash) {
					chessCreateGameHash.remove(username + "[inchess]");
				}
				// 更新用户列表
				sendPublicMsg(getUserList());
			} else if (msgReceived.startsWith("/win")) {
				// 用户名:此处用户名为usernmae
				String username = msgReceived.substring(5);
				String opponame = (String) chessPeerHash.get(username);
				String statu = (String) chessPlayGameHash.get(username + "[inchess]");
				// 从台号和玩家绑定hash中删除
				synchronized (chessPlayGameHash) {
					chessPlayGameHash.remove(username + "[inchess]");
					chessPlayGameHash.remove(opponame + "[inchess]");
				}
				// 判断用户是否开启了屏幕共享，若开启则进行删除
				if (shareHash.containsKey(statu)) {
					synchronized (shareHash) {
						shareHash.remove(statu);
					}
				}
				// 从对战玩家绑定hash中删除
				synchronized (chessPeerHash) {
					chessPeerHash.remove(username);
					chessPeerHash.remove(opponame);
					sendGameStatusPeerMsg(username, "/reset");
					sendGameStatusPeerMsg(opponame, "/reset");
				}
				// 更新成绩信息
				b.updatevictory(username);
				sendPublicMsg(getUserList());
			}else if (msgReceived.startsWith("/share")) {
				String statu = (String) chessPlayGameHash.get(msgReceived.substring(6) + "[inchess]");
				synchronized (shareHash) {
					shareHash.put(statu, "yes");
				}
				sendPublicMsg(getUserList());
			}
			else { // 收到的信息为其它信息时
				int lastLocation = msgReceived.indexOf(" ", 0);
				if (lastLocation == -1) {
					Feedback("/error");
					return;
				}
			}
		} else {
			msgReceived = clientNameHash.get(clientSocket) + ">" + msgReceived;
			serverMsgPanel.msgTextArea.append(msgReceived + "\n");
			sendPublicMsg(msgReceived);
			serverMsgPanel.msgTextArea.setCaretPosition(serverMsgPanel.msgTextArea.getText().length());
		}
	}

	// 发送公开信息
	@SuppressWarnings("rawtypes")
	public void sendPublicMsg(Object publicMsg) {
		synchronized (clientDataHash) {
			for (Enumeration enu = clientDataHash.elements(); enu.hasMoreElements();) {
				ObjectOutputStream outputData = (ObjectOutputStream) enu.nextElement();
				try {
					outputData.writeObject(publicMsg);
				} catch (IOException es) {
				}
			}
		}
	}

	// 发送游戏状态信息给指定的用户,gamePeerTarget:游戏用户 gamePeerMsg：游戏信息
	public boolean sendGameStatusPeerMsg(String gamePeerTarget, String gamePeerMsg) {
		// 通过用户名获取chessclient的socket
		Object gamePeerSocket = clientNameHash.get(gamePeerTarget);
		synchronized (clientDataHash) {
			// 通过socket查找输出流
			ObjectOutputStream gamePeerOutData = (ObjectOutputStream) clientDataHash.get(gamePeerSocket);
			try {
				// 发送信息
				gamePeerOutData.writeObject(gamePeerMsg);
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	// 发送反馈信息给连接到主机的人
	public void Feedback(Object feedBackMsg) {
		synchronized (clientDataHash) {
			ObjectOutputStream outputData = (ObjectOutputStream) clientDataHash.get(clientSocket);
			try {
				outputData.writeObject(feedBackMsg);
			} catch (Exception eb) {
			}
		}
	}

	// 取得用户列表
	@SuppressWarnings("rawtypes")
	public String[][] getUserList() {
		// 创建基础二维String数组
		String[][] userlist = {};
		for (Enumeration key = clientNameHash.keys(); key.hasMoreElements();) {
			String playUsername = key.nextElement().toString();
			String playstatus;
			String playInfo;
			String score = String.valueOf(b.integral(playUsername));
			String share = null;
			if (playUsername.indexOf("[inchess]") == -1) {
				if (chessCreateGameHash.containsKey(playUsername + "[inchess]")) {
					playstatus = "0";
					playInfo = "创建游戏";
					share = "";
				} else if (chessPlayGameHash.containsKey(playUsername + "[inchess]")) {
					playstatus = (String) chessPlayGameHash.get(playUsername + "[inchess]");
					playInfo = String.format("正在%s台游戏中", playstatus);
					if (shareHash.containsKey(playstatus)) {
						share = "yes";
					}
				} else {
					playstatus = "";
					playInfo = "在线";
					share = "";
				}
				String[][] otherPlay = { { playUsername, playInfo, playstatus, score ,share} };
				userlist = addElement(userlist, otherPlay);
			}
		}
		return userlist;
	}


	// 刚连接到主机时执行的方法
	public void sendInitMsg() {
		// 获取客户端用户名
		Feedback("/getname");
	}

	@SuppressWarnings("rawtypes")
	public void closeClient() {
		serverMsgPanel.msgTextArea.append("用户断开连接:" + clientSocket + "\n");
		Enumeration Key = clientNameHash.keys();
		while (Key.hasMoreElements()) {
			String username = (String) Key.nextElement();
			// 只对username == 用户名进行操作，不对username == 用户名[inchess]进行操作
			if (username.indexOf("[inchess]") == -1) {
				// 获取用户名
				if (clientSocket == clientNameHash.get(username)) {
					// 判断该用户是否在下棋
					if (chessPlayGameHash.containsKey(username + "[inchess]")) {
						// 获取台号
						@SuppressWarnings("unused")
						String number = (String) chessPlayGameHash.get(username + "[inchess]");
						// 获取对手用户名
						String opponame = (String) chessPeerHash.get(username);
						// 从playgamehash获取
						synchronized (chessPlayGameHash) {
							chessPlayGameHash.remove(username + "[inchess]");
							chessPlayGameHash.remove(opponame + "[inchess]");
						}
						// 如果开启屏幕共享，则进行删除
						if (shareHash.containsKey(number)) {
							synchronized (shareHash) {
								shareHash.remove(number);
							}
						}
						// 删除对战双方用户名绑定
						synchronized (chessPeerHash) {
							chessPeerHash.remove(username);
							chessPeerHash.remove(opponame);
						}
						// 给对手发送胜利消息
						//出bug，等下更改
						sendGameStatusPeerMsg(opponame, "/oppogiveup");
						// to-do：台号回收
						//胜利后的积分追加
						b.updatevictory(opponame);
					}
					// 判断用户是否在创建游戏
					else if (chessCreateGameHash.containsKey(username + "[inchess]")) {
						synchronized (chessCreateGameHash) {
							chessCreateGameHash.remove(username + "[inchess]");
						}
					}
					// 判断username[inchess]是否有连接
					if (clientNameHash.containsKey(username + "[inchess]")) {
						Socket inchessClientSocket = (Socket) clientNameHash.get(username + "[inchess]");
						synchronized (clientDataHash) {
							clientDataHash.remove(inchessClientSocket);
						}
						synchronized (clientNameHash) {
							clientNameHash.remove(username + "[inchess]");
						}
					}
					synchronized (clientDataHash) { //删除用户发送端口
						clientDataHash.remove(clientSocket);
					}
					synchronized (clientNameHash) { // 删除客户数据
						clientNameHash.remove(username);
						sendPublicMsg(getUserList());
					}
				}
			}
		}
		serverMsgPanel.statusLabel.setText("当前连接数:" + clientDataHash.size());
		try {
			clientSocket.close();
		} catch (IOException exx) {
		}
		isClientClosed = true;
	}

	public void run() {
		ObjectInputStream inputData;
		synchronized (clientDataHash) {
			serverMsgPanel.statusLabel.setText("当前连接数:" + clientDataHash.size());
		}
		try { // 等待连接到主机的信息
			inputData = new ObjectInputStream(clientSocket.getInputStream());
			// 初始化信息
			sendInitMsg();
			while (true) {
				Object message = inputData.readObject();
				dealWithMsg((String) message);
			}
		} catch (IOException | ClassNotFoundException esx) {
		} finally {
			if (!isClientClosed) {
				closeClient();
			}
		}
	}
}
