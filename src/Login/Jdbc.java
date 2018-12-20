package Login;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.jasypt.util.text.BasicTextEncryptor;

public class Jdbc {
	Connection con = null;// 创建连接（Connection）对象
	Statement statement = null;
	ResultSet res = null;// resultset表示数据库结果集的数据表
	ResultSet res1 = null;// resultset表示数据库结果集的数据表
	String driver;// 驱动的名称
	String url;// mysql服务器的地址
	String name;// mysql的用户名
	String passwd;// 密码
	String path = "dbinfo.properties";
	String code;// 兑换码

	@SuppressWarnings("deprecation")
	public Jdbc() {
		try {
			// 加载配置文件dbinfo.properties
			InputStream in = Jdbc.class.getClassLoader().getResourceAsStream(path);
			Properties properties = new Properties();
			properties.load(in);
			// 获取drivername，URL password username
			driver = properties.getProperty("driverName");
			url = properties.getProperty("url");
			name = properties.getProperty("user");
			passwd = properties.getProperty("password");
			// code = properties.getProperty("code");
			// 解密
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword("password");
			Class.forName(textEncryptor.decrypt(driver)).newInstance();// classLoader
																		// 加载相应的驱动
			// 使用单例节省不必要的浪费
			// Class.forName(driver);
			con = DriverManager.getConnection(textEncryptor.decrypt(url), textEncryptor.decrypt(name),
					textEncryptor.decrypt(passwd));// 创建连接
			statement = con.createStatement();// 获取statement对象

		} catch (ClassNotFoundException e) {
			System.out.println("对不起，找不到这个Driver");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 对用户信息的修改实际上就是对密码的修改
	public boolean update(String username1, String password1, String newpassword) {
		boolean judge = false;
		boolean s = compare(username1, password1);
		if (s) {
			String sql = "update user set password=\"" + newpassword + "\"where username=\"" + username1 + "\"";
			try {
				int a = statement.executeUpdate(sql);
				if (a == 1) {
					JOptionPane.showMessageDialog(null, "密码修改成功！");
					judge = true;
				}
				con.close();
				statement.close();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "用户不存在！");
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "修改失败");
		}
		return judge;
	}

	// 删除用户信息
	@SuppressWarnings("unused")
	public void delete(String username, String password) {
		if (compare(username, password)) {
			JOptionPane.showMessageDialog(null, "已经完成删除");
		} else {
			return;
		}
		String sql = "delete from user where username=\"" + username + "\"";
		try {
			int a = statement.executeUpdate(sql);
			con.close();
			statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "不存在该用户！");
			e.printStackTrace();
		}

	}

	// 用户注册功能的实现，添加数据
	public void insert(String username, String password) {
		String sql = "insert into user(username,password) values(\"" + username + "\",\"" + password + "\")";
		try {
			if (username == null || password == null) {
				JOptionPane.showMessageDialog(null, "注册信息不全");
			} else {
				int a = statement.executeUpdate(sql);
				if (a == 1) {
					JOptionPane.showMessageDialog(null, "注册成功！");
				}
			}
			con.close();
			statement.close();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "对不起该用户名已经有了！");
			e.printStackTrace();
		}
	}

	// 对比用户名和密码是不是匹配且状态是不是允许登录
	public boolean compare(String username, String password) {
		boolean m = false;
		String sql = "select password, status ,vip,level,score from user where username=\"" + username + "\"";
		String resql = "update user set status =\"" + 1 + "\"where username=\"" + username + "\"";
		try {
			res = statement.executeQuery(sql);// 执行SQL查询并返回该查询所生成的resultset对象
			if (res.next()) {
				String pa = res.getString(1);
				int st = res.getInt(2);
				int vip = res.getInt(3);
				System.out.println(pa + " " + password);
				if (pa.equals(password)) {
					if (st == 0 && vip == 1) {
						m = true;
						statement.executeUpdate(resql);
						JOptionPane.showMessageDialog(null, "尊重的vip用户，欢迎回来");
					} else if (st == 0 && vip != 1) {
						m = true;
						statement.executeUpdate(resql);
						JOptionPane.showMessageDialog(null, "登录成功");
					} else {
						JOptionPane.showMessageDialog(null, "账号已经登录了");
					}

				} else {
					JOptionPane.showMessageDialog(null, "密码错误！");
				}
			} else {
				JOptionPane.showMessageDialog(null, "用户名不存在！");
			}
			res.close();
			con.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return m;
	}

	// 更新用户状态
	public void reset(String username) {
		String sql = "update user set status =\"" + 0 + "\"where username=\"" + username + "\"";
		try {
			statement.executeUpdate(sql);
			con.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// 查询积分
	public int integral(String username) {
		String sql = "select score from user where username=\"" + username + "\"";
		int score = 0;
		try {
			res1 = statement.executeQuery(sql);// 执行SQL查询并返回该查询所生成的resultset对象
			if (res1.next()) {
				score = res1.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return score;
	}

	// 查询等级
	public int level(String username) {
		String sql = "select level from user where username=\"" + username + "\"";
		int level = 0;
		try {
			res1 = statement.executeQuery(sql);// 执行SQL查询并返回该查询所生成的resultset对象
			if (res1.next()) {
				level = res1.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return level;
	}

	// 更新用户分数
	public void updatevictory(String username) {
		String sql1 = "select level,score from user where username=\"" + username + "\"";

		try {
			res1 = statement.executeQuery(sql1);// 执行SQL查询并返回该查询所生成的resultset对象
			if (res1.next()) {
				int score = res1.getInt(2);
				int i = 5;
				int z = score + i;

				String str1 = "update user set level =\"";
				String str2 = "\"where username=\"" + username + "\"";
				String sql = "update user set score =\"" + z + "\"where username=\"" + username + "\"";
				statement.executeUpdate(sql);
				if (score == 45) {
					String str3 = str1 + 2 + str2;
					statement.executeUpdate(str3);
				} else if (score == 95) {
					String str3 = str1 + 3 + str2;
					statement.executeUpdate(str3);
				} else if (score == 145) {
					String str3 = str1 + 4 + str2;
					statement.executeUpdate(str3);
				} else if (score == 195) {
					String str3 = str1 + 5 + str2;
					statement.executeUpdate(str3);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// 打钱充值
	public void Somefarm(String username) {
		String sql1 = "select level,score from user where username=\"" + username + "\"";
		try {
			res1 = statement.executeQuery(sql1);// 执行SQL查询并返回该查询所生成的resultset对象
			if (res1.next()) {
				int score = res1.getInt(2);
				int i = 20;
				int z = score + i;
				String str1 = "update user set level =\"";
				String str2 = "\"where username=\"" + username + "\"";
				String sql = "update user set score =\"" + z + "\"where username=\"" + username + "\"";
				String sql2 = "update user set vip =\"" + 1 + "\"where username=\"" + username + "\"";
				statement.executeUpdate(sql);
				statement.executeUpdate(sql2);
				if (score >= 30 && score <= 75) {
					String str3 = str1 + 2 + str2;
					statement.executeUpdate(str3);
				} else if (score >= 75 && score <= 125) {
					String str3 = str1 + 3 + str2;
					statement.executeUpdate(str3);
				} else if (score >= 125 && score <= 175) {
					String str3 = str1 + 4 + str2;
					statement.executeUpdate(str3);
				} else if (score >= 175 && score <= 180) {
					String str3 = str1 + 5 + str2;
					statement.executeUpdate(str3);
				} else {
					String str3 = str1 + 5 + str2;
					statement.executeUpdate(str3);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
