package Game;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Button extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4931865580039435955L;
	int row;
	int col;

	public void setImageIcon(ImageIcon icon) {
		this.setIcon(icon);
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getRow() {
		return row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getCol() {
		return col;
	}
}
