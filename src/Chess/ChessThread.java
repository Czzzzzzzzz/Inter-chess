package Chess;

import java.io.IOException;
import java.util.StringTokenizer;

public class ChessThread extends Thread {
	ChessPad chessPad; // 当前线程的棋盘

	public ChessThread(ChessPad chessPad) {
		this.chessPad = chessPad;
	}

	// 处理取得的信息
	public void dealWithMsg(String msgReceived) {
		if (msgReceived.startsWith("/chess ")) { // 收到的信息为下棋
			StringTokenizer userMsgToken = new StringTokenizer(msgReceived, " ");
			// 表示棋子信息的数组、0索引为：x坐标；1索引位：y坐标；2索引位：棋子颜色
			String[] chessInfo = { "-1", "-1", "0" };
			int i = 0; // 标志位
			String chessInfoToken;
			while (userMsgToken.hasMoreTokens()) {
				chessInfoToken = (String) userMsgToken.nextToken(" ");
				if (i >= 1 && i <= 3) {
					chessInfo[i - 1] = chessInfoToken;
				}
				i++;
			}
			chessPad.paintNetFirPoint(Integer.parseInt(chessInfo[0]), Integer.parseInt(chessInfo[1]),
					Integer.parseInt(chessInfo[2]));
		} else if (msgReceived.equals("/error")) { // 收到的为错误信息
			chessPad.statusText.setText("用户不存在，请重新加入!");
		}
	}

	// 发送信息
	public void sendMessage(String sndMessage) {
		try {
			chessPad.outputData.writeObject(sndMessage);
		} catch (Exception ea) {
			ea.printStackTrace();
		}
	}

	public void run() {
		Object msgReceived;
		try {
			while (true) { // 等待信息输入
				msgReceived = chessPad.inputData.readObject();
				if (msgReceived instanceof String) {
					dealWithMsg((String) msgReceived);
				}
			}
		} catch (IOException | ClassNotFoundException es) {
		}
	}
}
