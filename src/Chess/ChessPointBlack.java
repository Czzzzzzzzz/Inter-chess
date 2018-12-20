package Chess;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class ChessPointBlack extends Canvas {
	
	private static final long serialVersionUID = 1L;
	ChessPad padBelonged; // 黑棋所属的棋盘

	public ChessPointBlack(ChessPad padBelonged) {
		setSize(40, 40); // 设置棋子大小
		this.padBelonged = padBelonged;
	}

	public void paint(Graphics g) { // 画棋子
		g.setColor(Color.black);
		g.fillOval(0, 0, 14, 14);
	}
}
