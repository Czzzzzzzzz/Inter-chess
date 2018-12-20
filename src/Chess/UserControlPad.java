package Chess;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UserControlPad extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JLabel ipLabel = new JLabel("IP", JLabel.LEFT);

	public JTextField ipInputted = new JTextField("0.0.0.0", 8);

	public JButton connectButton = new JButton("连接");

	public JButton createButton = new JButton("创建");

	public JButton joinButton = new JButton("加入");

	public JButton chatButton = new JButton("聊天");

	public JButton top = new JButton("变强");

	public JButton save = new JButton("保存");

	public JButton instructions = new JButton("说明");// 游戏说明

	public JButton share = new JButton("观战");

	public JButton allow = new JButton("开放观战");

	public JButton resetButton = new JButton("退出");

	public UserControlPad() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		connectButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		createButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		joinButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		chatButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		top.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		save.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		instructions.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		resetButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		share.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		allow.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		add(ipLabel);
		add(ipInputted);
		add(connectButton);
		add(createButton);
		add(joinButton);
		add(chatButton);
		add(top);
		add(save);
		add(instructions);
		add(share);
		add(allow);
		add(resetButton);

	}
}