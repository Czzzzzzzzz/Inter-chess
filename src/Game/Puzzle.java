package Game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class Puzzle extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5338915123518103076L;
	JMenuBar jmBar;// 主菜单控件
	JMenu menu, menuHelp, menuSelect, menuChange, menuRank;// 小菜单的控件
	JMenuItem itemStart, itemExit, itemSelect, itemView;//
	JRadioButtonMenuItem jrbm_change[] = new JRadioButtonMenuItem[4];// 图片更换的复选框
	JRadioButtonMenuItem jrbm_rank[] = new JRadioButtonMenuItem[3];// 难度调整的复选框
	MainPanel panel;
	String path;
	int pattern = 3;
	JLabel jl_time;
	JLabel jl_count;
	long startTime;
	long endTime;

	public Puzzle() {

		// 初始化菜单项
		jmBar = new JMenuBar();
		menu = new JMenu("菜单(M)");
		menuHelp = new JMenu("帮助(H)");
		menuSelect = new JMenu("选择(S)");
		itemStart = new JMenuItem("开始(S)");
		itemExit = new JMenuItem("退出(Z)");
		menuChange = new JMenu("图片更换");
		menuRank = new JMenu("等级(R)");
		itemView = new JMenuItem("查看背景(V)");
		jl_time = new JLabel("时间:");
		jl_count = new JLabel("步数:");

		jl_time.setForeground(Color.RED);
		jl_count.setForeground(Color.RED);

		// 添加图片选择按钮组
		ButtonGroup groupChange = new ButtonGroup();
		for (int i = 0; i < jrbm_change.length; i++) {
			jrbm_change[i] = new JRadioButtonMenuItem("0" + (i + 1) + ".jpg");
			groupChange.add(jrbm_change[i]);
			menuChange.add(jrbm_change[i]);
		}
		jrbm_change[0].setSelected(true);
		setPath();

		// 添加等级选择按钮组
		ButtonGroup groupRank = new ButtonGroup();
		String content;
		for (int i = 0; i < jrbm_rank.length; i++) {
			if (i == 0)
				content = new String("简单");
			else if (i == 1)
				content = new String("普通");
			else
				content = new String("复杂");
			jrbm_rank[i] = new JRadioButtonMenuItem(content);
			groupRank.add(jrbm_rank[i]);
			menuRank.add(jrbm_rank[i]);
		}
		jrbm_rank[0].setSelected(true);
		setPattern();

		menu.add(itemStart);
		menu.add(itemView);
		menu.add(itemExit);
		menuSelect.add(menuChange);
		menuSelect.add(menuRank);
		jmBar.add(menu);
		jmBar.add(menuSelect);
		jmBar.add(menuHelp);
		jmBar.add(new JLabel("                          "));
		jmBar.add(jl_time);
		jmBar.add(new JLabel("         "));
		jmBar.add(jl_count);
		this.setJMenuBar(jmBar);

		itemStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				breakState();
			}
		});
		itemView.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton index = new JButton(new ImageIcon(path + "\\index.jpg"));
				JFrame model = new JFrame("拼图模板");
				model.setSize(370, 370);
				model.setResizable(false);
				model.add(index);
				model.setVisible(true);

			}

		});
		itemExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		this.setTitle("拼图");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setSize(380, 420);
		this.setResizable(false);
		panel = new MainPanel(path, pattern);
		startTime = System.currentTimeMillis();
		this.add(panel);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	@Override
	public void run() {
		while (true) {
			endTime = System.currentTimeMillis();
			int time = (int) ((endTime - startTime) / 1000);
			jl_time.setText("时间: " + time);
			jl_count.setText("步数: " + panel.getCount());
		}
	}

	public void breakState() {
		startTime = System.currentTimeMillis();
		setPattern();
		setPath();
		System.out.println("pattern" + pattern);
		panel.breakRandom(path, pattern);
	}

	public void setPath() {
		for (int i = 0; i < jrbm_change.length; i++) {
			if (jrbm_change[i].isSelected()) {
				path = "Img\\type" + (i + 1) + "\\" + pattern;
			}
		}
	}

	public void setPattern() {
		for (int i = 0; i < jrbm_rank.length; i++) {
			if (jrbm_rank[i].isSelected()) {
				if (i == 0)
					pattern = 3;
				else if (i == 1)
					pattern = 4;
				else if (i == 2)
					pattern = 5;
			}
		}
	}
}
