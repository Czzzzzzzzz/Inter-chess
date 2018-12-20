package Game;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -901759229149914660L;
	Button[] button = new Button[25];// 按钮数组
	ImageIcon[] icon = new ImageIcon[25];// 图片数组，默认值8位空白照片
	int state[] = new int[25];// 照片存放顺序
	int NullButton;// 空白照片按钮
	int pattern;
	int total;
	int count;

	public MainPanel(String path, int pattern) {
		this.pattern = pattern;
		total = pattern * pattern;
		this.setSize(300, 300);
		breakRandom(path, pattern);

	}

	// 重新设置Buttont图片
	public void breakRandom(String path, int pattern) {
		count = 0;
		this.pattern = pattern;
		total = pattern * pattern;
		ImageCutUtil.cutImage(new File(path + "\\" + "index.jpg"), pattern, path);

		System.out.println("");
		for (int i = 0; i < total - 1; i++) {
			ImageIcon image = new ImageIcon(path + "\\" + i + ".jpg");
			System.out.println("height: " + image.getIconHeight() + " width: " + image.getIconWidth());
		}
		this.removeAll();
		updateUI();
		this.setLayout(new GridLayout(pattern, pattern));
		NullButton = total - 1;
		random(state);
		for (int i = 0; i < total; i++) {
			// 初始化按钮
			button[i] = new Button();
			button[i].setRow(i / pattern);// 初始化每一个按钮所在的行
			button[i].setCol(i % pattern);// 初始化每一个按钮所在的列
			this.add(button[i]);
		}

		for (int i = 0; i < total - 1; i++)
			System.out.print(state[i] + " ");
		for (int i = 0; i < total - 1; i++) {
			icon[i] = new ImageIcon(path + "\\" + state[i] + ".jpg");
			// System.out.println("height: "+icon[i].getIconHeight()+" width:
			// "+icon[i].getIconWidth());
			button[i].setImageIcon(icon[i]);
		}
		button[total - 1].setImageIcon(null);

		for (int i = 0; i < total; i++) {

			// 给每一个按钮添加监听事件
			button[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					Button button = (Button) e.getSource();
					System.out.println("点击按钮的横纵坐标为");
					System.out.println("x=" + button.getRow() + "|" + "y=" + button.getCol());
					remove(button);
					count++;
					System.out.println("pattern: " + pattern + "total: " + total);
					System.out.println("当前空白按钮是第几个");
					System.out.println("nullButton=" + NullButton);// 打印现在的空白按钮是哪个
					// 打印此时的图片顺序
					System.out.println("当前图片顺序状态为");
					for (int i = 0; i < total; i++) {
						System.out.print(state[i] + "  ");
					}
					System.out.println();
				}
			});
		}

	}

	public boolean isOdd(int a[]) {
		int sum = 0;
		for (int i = 0; i < total; i++)
			for (int j = i + 1; j < total; j++) {
				if (a[i] > a[j])
					sum++;
			}
		if (sum % 2 == 0)
			return true;
		else
			return false;

	}

	// 产生随机数组，打乱图片位置
	public void random(int a[]) {
		while (true) {
			Random cd = new Random();
			int i = 0;
			a[0] = cd.nextInt(total - 1);
			for (i = 1; i < total - 1; i++) {
				int temp = cd.nextInt(total - 1);
				for (int j = 0; j < i; j++) {
					if (a[j] != temp) {
						a[i] = temp;
					} else {
						i--;
						break;
					}
				}
			}
			a[i] = total - 1;
			if (isOdd(a)) {
				System.out.println("图片的初始顺序状态为");
				for (i = 0; i < total; i++)
					System.out.print(a[i] + "  ");
				System.out.println("isOdd" + isOdd(a));
				return;
			}
		}
	}

	public void remove(Button clicked) {
		System.out.println("当前  pattern: " + pattern + " total " + total + " nullButton " + NullButton);
		int rowN = button[NullButton].getRow();// 得到空白按钮横坐标
		int colN = button[NullButton].getCol();// 得到空白按钮纵坐标
		int rowC = clicked.getRow();// 得到点击按钮横坐标
		int colC = clicked.getCol();// 得到点击按钮纵坐标
		if (((rowN - rowC) == 1 && (colN - colC) == 0) || ((rowN - rowC) == -1 && (colN - colC) == 0)
				|| ((rowN - rowC) == 0 && (colN - colC) == 1) || ((rowN - rowC) == 0 && (colN - colC) == -1)) {
			ImageIcon icon = (ImageIcon) clicked.getIcon();// 得到点击按钮的图片
			button[NullButton].setImageIcon(icon);// 设置空白按钮的图片，即交换空白按钮与点击按钮的图片
			clicked.setImageIcon(null);// 设置点击按钮图片为空白
			int clickState = rowC * pattern + colC;// 获得点击按钮是第几个按钮(0-8)
			NullButton = rowN * pattern + colN;// 获得空白按钮是几个(0-8)
			state[NullButton] = state[clickState];// 交换图片数组的顺序状态
			state[clickState] = total - 1;
			NullButton = clickState;// 设置空白按钮是第几个
			check();
		} else {
			return;
		}
	}

	// 判断拼图是否完成
	public void check() {
		for (int i = 0; i < total; i++)
			if (state[i] != i) {
				return;
			}
		JOptionPane.showMessageDialog(this, "拼图完成");
	}

	public int getCount() {
		return count;
	}

}
