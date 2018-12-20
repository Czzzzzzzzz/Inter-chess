package Chat;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 此类主要用于验证登录信息和获取用户名
 * 
 * 
 * @author czx
 *
 */
public class ChatLogin {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	String host;// 服务器地址 默认使用127.0.0.1
	int port;// 端口 默认使用10086
	String loginName;// 用户名
	private String cm;// 类型转换用的
	static String name;
	String ip = "172.16.120.45";
	int cport = 10010;

	/**
	 * Create the application.
	 * 
	 * @param name
	 */
	@SuppressWarnings("static-access")
	public ChatLogin(String name) {
		initialize();
		this.name = name;
		textField.setText(name);
		textField.setEditable(false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setTitle("聊天登录");
		frame.setBounds(100, 100, 435, 235);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblNewLabel = new JLabel("用户名");
		panel.add(lblNewLabel);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);

		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);

		JLabel lblNewLabel_1 = new JLabel("请输入服务器的信息");
		panel_1.add(lblNewLabel_1);

		textField_1 = new JTextField();
		textField_1.setText(ip);
		panel_1.add(textField_1);
		textField_1.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("：");
		panel_1.add(lblNewLabel_2);

		textField_2 = new JTextField();
		textField_2.setText(String.valueOf(cport));
		textField_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				Judge judge = new Judge();
			}
		});
		panel_1.add(textField_2);
		textField_2.setColumns(5);

		JButton btnNewButton = new JButton("连接");
		panel_1.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				Judge judge = new Judge();
			}
		});
	}

	// 用于判断配置是不是符合要求
	class Judge {
		{
			cm = textField_2.getText();
			port = Integer.valueOf(cm);
			host = textField_1.getText();
			loginName = textField.getText();
			String name = textField.getText();
			if (cm.equals("10010") && host.equals("172.16.120.45") && name != null) {

				// 将连接的配置信息传输到User类中
				User u = new User(port, host);
				// 将用户名赋值给User窗口 当做用户名
				u.getFrame().setTitle(loginName);
				System.out.println(host);
				System.out.println(port);
				frame.dispose();// 销毁页面
			} else if (!cm.equals("10010") || !host.equals("172.16.120.45")) {
				JOptionPane.showMessageDialog(null, "请输入正确的网络地址和端口号");
			} else if (loginName == null) {
				JOptionPane.showMessageDialog(null, "用户名");
			} else {
				JOptionPane.showMessageDialog(null, "请输入正确的配置");
			}
		}

	}

}
