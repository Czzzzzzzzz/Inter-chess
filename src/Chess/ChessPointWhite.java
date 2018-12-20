package Chess;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class ChessPointWhite extends Canvas {
	
	private static final long serialVersionUID = 1L;
	ChessPad padBelonged; // 白棋所属的棋盘

	public ChessPointWhite(ChessPad padBelonged) {
		setSize(40, 40);
		this.padBelonged = padBelonged;
	}

	public void paint(Graphics g) { // 画棋子
		g.setColor(Color.white);
		g.fillOval(0, 0, 14, 14);
	}
}
