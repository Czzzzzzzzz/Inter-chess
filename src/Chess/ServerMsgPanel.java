package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;

import javax.swing.JLabel;

//主机面板类
public class ServerMsgPanel extends Panel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TextArea msgTextArea = new TextArea("", 30, 100,
			TextArea.SCROLLBARS_VERTICAL_ONLY);
	public JLabel statusLabel = new JLabel("当前连接数:", Label.LEFT);
	public Panel msgPanel = new Panel();
	public Panel statusPanel = new Panel();
	public ServerMsgPanel()
	{
		setSize(600, 400);
		setBackground(Color.LIGHT_GRAY);
		setLayout(new BorderLayout());
		msgPanel.setLayout(new FlowLayout());
		msgPanel.setSize(210, 210);
		statusPanel.setLayout(new BorderLayout());
		statusPanel.setSize(210, 50);
		msgPanel.add(msgTextArea);
		statusPanel.add(statusLabel, BorderLayout.WEST);
		add(msgPanel, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.NORTH);
	}
}
