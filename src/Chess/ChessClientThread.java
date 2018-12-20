package Chess;

import java.io.IOException;

import javax.swing.JOptionPane;

public class ChessClientThread extends Thread {
	public ChessClient chessClient;
	public String username;

	public ChessClientThread(ChessClient chessClient, String username) {
		this.chessClient = chessClient;
		this.username = username;
	}

	// 信息接收函数
	@SuppressWarnings("deprecation")
	public void dealWithMsg(String msgReceived) {
		if (msgReceived.startsWith("/peer ")) { // 收到信息为游戏中的等待时
			chessClient.chessPad.chessPeerName = msgReceived.substring(6);
			chessClient.userControlPad.top.setVisible(false);
			chessClient.userControlPad.save.setVisible(true);
			chessClient.chessPad.removeAll();
			if (chessClient.isCreator) { // 若用户为游戏建立者
				chessClient.chessPad.chessColor = 1; // 设定其为黑棋先行
				chessClient.userControlPad.share.setVisible(false);
				chessClient.userControlPad.allow.setVisible(true);
				chessClient.chessPad.isMouseEnabled = true;
				chessClient.userControlPad.createButton.setVisible(false);
				chessClient.chessPad.statusText.setText("你为黑方，你先下..");
				JOptionPane.showMessageDialog(null, "你为黑方，你先下..");
			} else if (chessClient.isParticipant) { // 若用户为游戏加入者
				chessClient.chessPad.chessColor = -1; // 设定其为白棋后性
				chessClient.userControlPad.share.setVisible(false);
				chessClient.chessPad.statusText.setText("你为白方，等待对方先下..");
				chessClient.userControlPad.joinButton.setVisible(false);
				JOptionPane.showMessageDialog(null, "你为白方，等待对方先下..");
			}
			
		} else if (msgReceived.equals("/OK")) { // 收到信息为成功创建游戏
			chessClient.chessPad.statusText.setText("游戏创建等待对手");
		}
		else if (msgReceived.equals("/error")) { // 收到信息错误
			JOptionPane.showMessageDialog(null, "错误");
		}
		// 向服务器发送用户名
		else if (msgReceived.equals("/getname")) {
			sendMessage("/sendname " + username);
		}
		else if (msgReceived.equals("/updateSuccess")) {
			JOptionPane.showMessageDialog(null, "期待你变得更强！");
		}
		else if (msgReceived.equals("/oppogiveup")) {
			// 调用胜利方法
			chessClient.chessPad.setVicStatus(chessClient.chessPad.chessColor);
			chessClient.isOnChess = false;
			chessClient.isCreator = false;
			chessClient.isParticipant = false;
			chessClient.userControlPad.createButton.setVisible(true);
			chessClient.userControlPad.createButton.setText("创建");
			chessClient.userControlPad.createButton.setEnabled(true);
			chessClient.userControlPad.joinButton.setEnabled(true);
			chessClient.userControlPad.joinButton.setVisible(true);
			chessClient.userControlPad.top.setVisible(true);
			chessClient.userControlPad.top.setEnabled(true);
		}
		else if (msgReceived.equals("/reset")) {
			chessClient.isOnChess = false;
			chessClient.isCreator = false;
			chessClient.isParticipant = false;
			chessClient.userControlPad.createButton.setVisible(true);
			chessClient.userControlPad.createButton.setText("创建");
			chessClient.userControlPad.createButton.setEnabled(true);
			chessClient.userControlPad.joinButton.setEnabled(true);
			chessClient.userControlPad.joinButton.setVisible(true);
			chessClient.userControlPad.save.setVisible(false);
			chessClient.userControlPad.top.setVisible(true);
			chessClient.userControlPad.top.setEnabled(true);
			chessClient.userControlPad.allow.setEnabled(true);
			chessClient.userControlPad.allow.setVisible(false);
			chessClient.userControlPad.share.setVisible(true);
			chessClient.chessPad.chessPeerName = null;
			chessClient.chessPad.isMouseEnabled = false;
		}
	}

	// 发送信息
	public void sendMessage(String sndMessage) {
		try {
			chessClient.outputStream.writeObject(sndMessage);
		} catch (Exception ea) {
		}
	}

	public void run() {
		try {
			Object message;
			while (true) {
				// 等待聊天信息，进入wait状态
				message = chessClient.inputStream.readObject();
				// 对传过来的object对象进行类型判断，如果未String[][],则刷新列表，如果为String，则执行对应命令
				if (message instanceof String[][]) {
					// 创建个人列表
					String[][] ownList = (String[][]) message;
					// 给table刷新数据
					chessClient.userJTablePad.tableModel.setDataVector(ownList, chessClient.userJTablePad.columnNames);
				} else if (message instanceof String) {
					dealWithMsg((String) message);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
		}
	}
	
}
