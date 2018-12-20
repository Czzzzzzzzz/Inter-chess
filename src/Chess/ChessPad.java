package Chess;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import Login.Jdbc;

public class ChessPad extends Panel implements MouseListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 是否首次创建擂台
	boolean isFirstCreate = true;

	// 鼠标是否能使用
	public boolean isMouseEnabled = false;

	// 是否胜利
	public boolean isWinned = false;

	// 是否在下棋中
	public boolean isGaming = false;

	// 棋子的x轴坐标位
	public int chessX_POS = -1;

	// 棋子的y轴坐标位
	public int chessY_POS = -1;

	// 棋子的颜色
	public int chessColor = 1;

	// 黑棋x轴坐标位数组
	public int chessBlack_XPOS[] = new int[200];

	// 黑棋y轴坐标位数组
	public int chessBlack_YPOS[] = new int[200];

	// 白棋x轴坐标位数组
	public int chessWhite_XPOS[] = new int[200];

	// 白棋y轴坐标位数组
	public int chessWhite_YPOS[] = new int[200];

	// 黑棋数量
	public int chessBlackCount = 0;

	// 白棋数量
	public int chessWhiteCount = 0;

	// 黑棋获胜次数
	public int chessBlackVicTimes = 0;

	// 白棋获胜次数
	public int chessWhiteVicTimes = 0;

	Jdbc b = new Jdbc();
	
	// 套接口
	public Socket chessSocket;

	public ObjectInputStream inputData;

	public ObjectOutputStream outputData;

	public String chessSelfName = null;

	public String chessPeerName = null;

	public String host = null;

	public int port = 10086;

	public TextField statusText = new TextField("请连接服务器！");

	public ChessThread chessThread = new ChessThread(this);

	public ChessPad() {
		setSize(440, 440);
		setLayout(null);
		addMouseListener(this);
		add(statusText);
		statusText.setBounds(new Rectangle(40, 5, 360, 24));
		statusText.setEditable(false);
	}

	// 连接到主机
	public boolean connectServer(String ServerIP, int ServerPort) throws Exception {
		try {
			// 取得主机端口
			chessSocket = new Socket(ServerIP, ServerPort);
			// 取得输入流
			inputData = new ObjectInputStream(chessSocket.getInputStream());
			// 取得输出流
			outputData = new ObjectOutputStream(chessSocket.getOutputStream());
			if (isFirstCreate) {
				chessThread.start();
				isFirstCreate = false;
			}

			return true;
		} catch (IOException ex) {
			statusText.setText("连接失败! \n");
		}
		return false;
	}

	// 断开擂台连接
	public void disconnectServer(Socket chessSocket, ObjectInputStream inputData, ObjectOutputStream outputData)
			throws Exception {
		try {
			this.chessSocket = chessSocket;
			this.inputData = inputData;
			this.outputData = outputData;
			if (inputData != null) {
				inputData.close();
			}
			if (outputData != null) {
				outputData.close();
			}
			if (chessSocket != null) {
				chessSocket.close();
			}
			statusText.setText("你已取消创建对战，可重新发起对战！");
		} catch (IOException e) {
			statusText.setText("客户端异常，请重新登录！");
		}
	}

	// 设定胜利时的棋盘状态
	public void setVicStatus(int vicChessColor) {
		// 清空棋盘
		this.removeAll();
		// 将黑棋的位置设置到零点
		for (int i = 0; i <= chessBlackCount; i++) {
			chessBlack_XPOS[i] = 0;
			chessBlack_YPOS[i] = 0;
		}
		// 将白棋的位置设置到零点
		for (int i = 0; i <= chessWhiteCount; i++) {
			chessWhite_XPOS[i] = 0;
			chessWhite_YPOS[i] = 0;
		}
		// 清空棋盘上的黑棋数
		chessBlackCount = 0;
		// 清空棋盘上的白棋数
		chessWhiteCount = 0;
		add(statusText);
		statusText.setBounds(40, 5, 360, 24);
		if (vicChessColor == 1) { // 黑棋胜
			chessBlackVicTimes++;
			JOptionPane.showMessageDialog(null, "黑方胜");
		} else if (vicChessColor == -1) { // 白棋胜
			chessWhiteVicTimes++;
			JOptionPane.showMessageDialog(null, "白方胜 ");
		}
	}

	// 取得指定棋子的位置
	public void setLocation(int xPos, int yPos, int chessColor) {
		if (chessColor == 1) { // 棋子为黑棋时
			chessBlack_XPOS[chessBlackCount] = xPos * 20;
			chessBlack_YPOS[chessBlackCount] = yPos * 20;
			chessBlackCount++;
		} else if (chessColor == -1) { // 棋子为白棋时
			chessWhite_XPOS[chessWhiteCount] = xPos * 20;
			chessWhite_YPOS[chessWhiteCount] = yPos * 20;
			chessWhiteCount++;
		}
	}

	// 判断当前状态是否为胜利状态
	public boolean checkVicStatus(int xPos, int yPos, int chessColor) {
		int chessLinkedCount = 1; // 连接棋子数
		int chessLinkedCompare = 1; // 用于比较是否要继续遍历一个棋子的相邻网格
		int chessToCompareIndex = 0; // 要比较的棋子在数组中的索引位置
		int closeGrid = 1; // 相邻网格的位置
		if (chessColor == 1) { // 黑棋时
			chessLinkedCount = 1; // 将该棋子自身算入的话，初始连接数为1
			// 以下每对for循环语句为一组，因为下期的位置能位于中间而非两端
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) { // 遍历相邻4个网格
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) { // 遍历棋盘上所有黑棋子
					if (((xPos + closeGrid) * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& ((yPos * 20) == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的右边4个棋子是否都为黑棋
						chessLinkedCount = chessLinkedCount + 1; // 连接数加1
						if (chessLinkedCount == 5) { // 五子相连时，胜利
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {// 若中间有一个棋子非黑棋，则会进入此分支，此时无需再遍历
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
					if (((xPos - closeGrid) * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& (yPos * 20 == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的左边4个棋子是否都为黑棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			// 进入新的一组for循环时要将连接数等重置
			chessLinkedCount = 1;
			chessLinkedCompare = 1;
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
					if ((xPos * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& ((yPos + closeGrid) * 20 == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的上边4个棋子是否都为黑棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
					if ((xPos * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& ((yPos - closeGrid) * 20 == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的下边4个棋子是否都为黑棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			chessLinkedCount = 1;
			chessLinkedCompare = 1;
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
					if (((xPos - closeGrid) * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& ((yPos + closeGrid) * 20 == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的左上方向4个棋子是否都为黑棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
					if (((xPos + closeGrid) * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& ((yPos - closeGrid) * 20 == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的右下方向4个棋子是否都为黑棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			chessLinkedCount = 1;
			chessLinkedCompare = 1;
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
					if (((xPos + closeGrid) * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& ((yPos + closeGrid) * 20 == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的右上方向4个棋子是否都为黑棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
					if (((xPos - closeGrid) * 20 == chessBlack_XPOS[chessToCompareIndex])
							&& ((yPos - closeGrid) * 20 == chessBlack_YPOS[chessToCompareIndex])) { // 判断当前下的棋子的左下方向4个棋子是否都为黑棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
		} else if (chessColor == -1) { // 白棋时
			chessLinkedCount = 1;
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if (((xPos + closeGrid) * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& (yPos * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的右边4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if (((xPos - closeGrid) * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& (yPos * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的左边4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			chessLinkedCount = 1;
			chessLinkedCompare = 1;
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if ((xPos * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& ((yPos + closeGrid) * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的上边4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if ((xPos * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& ((yPos - closeGrid) * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的下边4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			chessLinkedCount = 1;
			chessLinkedCompare = 1;
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if (((xPos - closeGrid) * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& ((yPos + closeGrid) * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的左上方向4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if (((xPos + closeGrid) * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& ((yPos - closeGrid) * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的右下方向4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			chessLinkedCount = 1;
			chessLinkedCompare = 1;
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if (((xPos + closeGrid) * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& ((yPos + closeGrid) * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的右上方向4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return true;
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
			for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
				for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
					if (((xPos - closeGrid) * 20 == chessWhite_XPOS[chessToCompareIndex])
							&& ((yPos - closeGrid) * 20 == chessWhite_YPOS[chessToCompareIndex])) {// 判断当前下的棋子的左下方向4个棋子是否都为白棋
						chessLinkedCount++;
						if (chessLinkedCount == 5) {
							return (true);
						}
					}
				}
				if (chessLinkedCount == (chessLinkedCompare + 1)) {
					chessLinkedCompare++;
				} else {
					break;
				}
			}
		}
		return false;
	}

	// 画棋盘
	public void paint(Graphics g) {
		for (int i = 40; i <= 380; i = i + 40) {
			g.drawLine(40, i, 400, i);
		}
		g.drawLine(40, 400, 400, 400);
		for (int j = 40; j <= 380; j = j + 40) {
			g.drawLine(j, 40, j, 400);
		}
		g.drawLine(400, 40, 400, 400);
		g.fillOval(97, 97, 6, 6);
		g.fillOval(337, 97, 6, 6);
		g.fillOval(97, 337, 6, 6);
		g.fillOval(337, 337, 6, 6);
		g.fillOval(217, 217, 6, 6);
	}

	// 画棋子
	public void paintFirPoint(int xPos, int yPos, int chessColor) {
		ChessPointBlack chessPBlack = new ChessPointBlack(this);
		ChessPointWhite chessPWhite = new ChessPointWhite(this);
		if (chessColor == 1 && isMouseEnabled) { // 黑棋
													// 设置棋子的位置
			setLocation(xPos, yPos, chessColor);
			// 取得当前局面状态
			// To-do： 改写，发送成绩
			// 发送信息
			chessThread.sendMessage("/" + chessPeerName + " /chess " + xPos + " " + yPos + " " + chessColor);
			// 添加到棋盘
			this.add(chessPBlack);
			// 设置边界
			chessPBlack.setBounds(xPos * 40 - 7, yPos * 40 - 7, 16, 16);
			// 判断是否胜利
			if (checkVicStatus(xPos, yPos, chessColor)) {
				// 设置胜利状态
				setVicStatus(1);
				String username = super.getName();
				chessThread.sendMessage("/win " + username);
			}
			else {
				statusText.setText("黑(第" + chessBlackCount + "步)" + xPos + " " + yPos + ",轮到白方.");
			}
			isMouseEnabled = false;
		} else if (chessColor == -1 && isMouseEnabled) { // 白棋
			setLocation(xPos, yPos, chessColor);
			chessThread.sendMessage("/" + chessPeerName + " /chess " + xPos + " " + yPos + " " + chessColor);
			this.add(chessPWhite);
			chessPWhite.setBounds(xPos * 40 - 7, yPos * 40 - 7, 16, 16);
			if (checkVicStatus(xPos, yPos, chessColor)) {
				setVicStatus(-1);
				String username = super.getName();
				chessThread.sendMessage("/win " + username);
			}
			else {
				statusText.setText("白(第" + chessWhiteCount + "步)" + xPos + " " + yPos + ",轮到黑方.");
			}
			isMouseEnabled = false;
		}
	}

	// 画网络棋盘
	public void paintNetFirPoint(int xPos, int yPos, int chessColor) {
		ChessPointBlack firPBlack = new ChessPointBlack(this);
		ChessPointWhite firPWhite = new ChessPointWhite(this);
		setLocation(xPos, yPos, chessColor);
		if (chessColor == 1) {
			isWinned = checkVicStatus(xPos, yPos, chessColor);
			this.add(firPBlack);
			firPBlack.setBounds(xPos * 40 - 7, yPos * 40 - 7, 16, 16);
			if (isWinned == false) {
				statusText.setText("黑(第" + chessBlackCount + "步)" + xPos + " " + yPos + ",轮到白方.");
			} else {
				setVicStatus(1);
			}
		} else if (chessColor == -1) {
			isWinned = checkVicStatus(xPos, yPos, chessColor);
			this.add(firPWhite);
			firPWhite.setBounds(xPos * 40 - 7, yPos * 40 - 7, 16, 16);
			if (isWinned == false) {
				statusText.setText("白(第" + chessWhiteCount + "步)" + xPos + " " + yPos + ",轮到黑方.");
			} else {
				setVicStatus(-1);
			}
		}
		isMouseEnabled = true;
	}

	// 捕获下棋事件
	@SuppressWarnings("deprecation")
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
			chessX_POS = (int) e.getX();
			chessY_POS = (int) e.getY();
			int a = (chessX_POS + 10) / 40, b = (chessY_POS + 10) / 40;
			if (chessX_POS / 40 < 2 || chessY_POS / 40 < 2 || chessX_POS / 40 > 39 || chessY_POS / 40 > 39) {
				// 下棋位置不正确时，不执行任何操作
			} else {
				paintFirPoint(a, b, chessColor); // 画棋子
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
	}
}