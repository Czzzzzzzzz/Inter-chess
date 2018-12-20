package Chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import Game.Puzzle;
import lombok.Data;

/**
 * 用户类的构建 以login类中的用户名为user
 * 
 * 
 * @author czx
 *
 */

@Data
public class User {

	JFrame frame;
	private JTextField textField;
	String username;
	TextArea textArea;
	private PrintWriter writer;
	private BufferedReader reader;
	String text;
	String next;
	static int port;
	static String host;
	Socket socket;
	static String strMsg;
	@SuppressWarnings("rawtypes")
	ArrayList userList = new ArrayList();

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Create the application.
	 * 
	 * @param host
	 * @param port
	 */

	@SuppressWarnings("static-access")
	public User(int port, String host) {
		this.port = port;
		this.host = host;
		initialize();
		System.out.println(port + host);

	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 516, 274);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		username = frame.getTitle();
		TextArea textArea = new TextArea();
		textArea.setBackground(Color.WHITE);
		textArea.setEditable(false);
		frame.getContentPane().add(textArea, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("文本输入"));
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		try {
			// 创建一个套接字，host port是从login中获取的
			socket = new Socket(host, port);
			// 创建一个往套接字中写数据的管道，即输出流，给服务器发送信息
			writer = new PrintWriter(socket.getOutputStream());
			// 创建一个聪套接字读数据的管道，即输入流，读服务器读返回信息
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnNewButton = new JButton("发送");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 获取用户输入读文本
				String str = textField.getText();
				text = str;
				username = frame.getTitle();

				if (!text.equals("")) {
					next = textArea.getText();
					// 通过输出流将数据发送给服务器
					// text = "From " + username + "说: " + (str + "\r\n") +
					// next;
					writer.println(username + "说：" + text);
					// writer.println(text);
					writer.flush();
					// 清空文本框
					textField.setText("");
				}
				JOptionPane.showMessageDialog(null, "请输入内容");// 先这样吧，能提示就好
			}

		});

		textField = new JTextField();
		textField.setToolTipText("");
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 获取用户输入读文本
				String str = textField.getText();
				text = str;
				username = frame.getTitle();

				if (!text.equals("")) {
					next = textArea.getText();
					writer.println(username + "说：" + text);
					writer.flush();

					// 清空文本框
					textField.setText("");
				}
				JOptionPane.showMessageDialog(null, "请输入内容");
			}
		});
		panel.add(textField);
		textField.setColumns(15);
		panel.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("断开");
		btnNewButton_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				frame.dispose();// 销毁页面
			}

		});
		panel.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("小游戏");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				Puzzle pz = new Puzzle();
			}
		});
		panel.add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("分享图片");
		btnNewButton_3.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				Shared shared = new Shared();
			}
		});
		panel.add(btnNewButton_3);
		// 启动线程

		new GetMsgFromServer().start();

		Thread t1 = new Thread() {
			@Override
			public void run() {
				while (this.isAlive()) {
					try {
						strMsg = reader.readLine();
						if (strMsg != null) {
							// 在文本域中显示聊天信息
							textArea.append(strMsg + "\n");
						}
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		};
		t1.start();
	}

	// 接受服务器读返回信息读线程
	class GetMsgFromServer extends Thread {

		@Override
		public void run() {

			while (this.isAlive()) {
				try {
					strMsg = reader.readLine();
					if (strMsg != null) {
					}
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	public void appendText(String in) {
		textArea.append("\n" + in);
	}

}