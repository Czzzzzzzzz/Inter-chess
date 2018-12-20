package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class userJTable extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// table
	JTable table;
	// 获取选中的用户名
	String username = null;
	// 获取选中的信息
	String info = null;
	// 获取选中的状态值
	String statu = null;
	// 获取观战状态
	String share = null;
	// tableModel
	DefaultTableModel tableModel;
	// 表头
	String[] columnNames = { "用户", "状态", "台号","积分","可观战" };

	public userJTable() {
		// 创建数据
		String[][] rowData = { { "", "未连接", "" ,"",""}, };
		// 创建tablemodel
		tableModel = new DefaultTableModel(rowData, columnNames);
		// 创建tablecell
		DefaultTableCellRenderer tableCell = new DefaultTableCellRenderer();
		// 创建table
		table = new JTable(tableModel) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			// 禁用JTable双击编辑方法
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		// 设置table仅允许单选
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// 设置表头
		table.getTableHeader().setFont(new Font("微软雅黑", Font.PLAIN, 12));
		// TO-do：到时候考虑下需要给个什么颜色背景比较醒目
		// 不允许改变列宽和拖动重新排序各列
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);;
		// 设置数据居中
		tableCell.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, tableCell);
		// 设置表格内容
		table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		table.setSelectionForeground(Color.DARK_GRAY);
		table.setSelectionBackground(Color.LIGHT_GRAY);
		table.setGridColor(Color.GRAY);
		// 设置行高
		table.setRowHeight(20);
		// 设置列宽
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		// 设置滚动面板大小
		table.setPreferredScrollableViewportSize(new Dimension(200, 1000));
		// 至于滚动面板中
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		// 添加鼠标事件
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// 获取选中行号
				int selectRow;
				// 如果行号为-1，则结束
				if ((selectRow = table.getSelectedRow()) == -1) {
					return;
				}
				// 鼠标左键单击table事件
				if (e.getButton() == MouseEvent.BUTTON1 && (e.getClickCount() == 1)) {
					// 获取选中行号
					selectRow = table.getSelectedRow();
					// 获取选中用户昵称
					username = table.getValueAt(selectRow, 0).toString();
					// 获取选中信息
					info = table.getValueAt(selectRow, 1).toString();
					// 获取台号
					if (table.getValueAt(selectRow, 2).toString() == "") {
						statu = "0";
					} else {
						statu = table.getValueAt(selectRow, 2).toString();
					}
					// 获取观战信息
					share = table.getValueAt(selectRow, 4).toString();
				}
			}
		});
	}

	// 合并二维数组方法
	public static String[][] addElement(String[][] tda1, String[][] tda2) {
		int length = tda1.length + tda2.length;
		String[][] tda = new String[length][5];
		int i;
		// tda1 -> tda
		for (i = 0; i < tda1.length; i++) {
			for (int j = 0; j < 5; j++) {
				tda[i][j] = tda1[i][j];
			}
		}
		// tda2 -> tda
		for (; i - tda1.length < tda2.length; i++) {
			for (int j = 0; j < 5; j++) {
				tda[i][j] = tda2[i - tda1.length][j];
			}
		}
		return tda;
	}

}
