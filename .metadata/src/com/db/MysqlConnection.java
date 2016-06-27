package com.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class MysqlConnection {
	protected Logger log = Logger.getLogger(MysqlConnection.class);
	private Connection conn = null;
	private String url = "";
	private String user = "";
	private String password = "";

	public MysqlConnection(String url, String user, String password) {
		String driver = "com.mysql.jdbc.Driver";
		this.url=url;
		this.user=user;
		this.password=password;
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (true) {
			try {
				// 加载驱动程序
				// 连续数据库
				conn = DriverManager.getConnection(this.url,this.user,this.password);
				if (!conn.isClosed()) {
					log.info("Succeeded connecting to the Database!");
				} else {
					log.error("数据库连接失败");
				}
				conn.setAutoCommit(true);
				break;
			} catch (Exception e1) {
				log.error(e1.getMessage());
			}
		}
	}

	public MysqlSelect sqlSelect(String sqlString) {
		ResultSet result = null;
		boolean flag = false;
		Statement statement = null;
		while (true) {
			try {
				// statement用来执行SQL语句

				statement = conn.createStatement();

				// 要执行的SQL语句
				result = statement.executeQuery(sqlString);
				flag = true;
				break;
			} catch (Exception e) {
				try {
					if (conn.isClosed()) {
						// 需要重连
						return null;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				log.error(e.getMessage());
			}
		}
		return new MysqlSelect(flag, statement, result);
	}

	/**
	 * 插入
	 * 
	 * @param sqlString
	 * @return
	 * @throws SQLException
	 */
	public boolean sqlInsert(String sqlString) throws SQLException {
		boolean flag = false;
		Statement statement = null;
		// statement用来执行SQL语句
		statement = conn.createStatement();
		statement.executeUpdate(sqlString);
		flag = true;

		return flag;
	}

	/**
	 * 批量插入
	 * 
	 * @param sqlString
	 * @return
	 */
	public boolean sqlInsertBatch(LinkedList<String> sqlString) {
		boolean flag = false;
		Statement statement = null;
		try {
			// statement用来执行SQL语句
			statement = conn.createStatement();
			for (String str : sqlString) {
				statement.addBatch(str);
			}
			statement.executeBatch();
			statement.close();
			flag = true;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return flag;
	}

	/**
	 * 更新
	 * 
	 * @param sqlString
	 * @return
	 */
	public boolean sqlUpdate(String sqlString) {
		boolean flag = false;
		Statement statement = null;
		try {
			// statement用来执行SQL语句

			statement = conn.createStatement();
			statement.executeUpdate(sqlString);
			statement.close();
			flag = true;
		} catch (Exception e) {
			// log.error(e.getMessage());
		}

		// log.info("执行完成");
		return flag;
	}

	/**
	 * 批量更新
	 * 
	 * @param sqlString
	 * @return
	 */
	public boolean sqlUpdateBatch(LinkedList<String> sqlString) {
		boolean flag = false;
		Statement statement = null;
		try {
			// statement用来执行SQL语句
			statement = conn.createStatement();
			for (String str : sqlString) {
				if ( str == null||str.equals("") ) {
				} else {
					statement.addBatch(str);
				}
			}
			statement.executeBatch();
			statement.close();
			flag = true;
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
				log.info("roolback执行错误");
			}
			log.error("已执行roolback");
			log.error("", e);
		}
		// log.info("执行完成");
		return flag;
	}

	/**
	 * 关闭操作
	 * 
	 * @return
	 */
	public boolean close() {
		if (conn == null) {

		} else {
			try {
				conn.close();
			} catch (Exception e) {
				log.error(e.getMessage());
				return false;
			}
		}
		log.info("数据库连接关闭");
		return true;

	}

	/**
	 * 提交操作
	 * 
	 * @return
	 */
	public boolean commit() {

		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			return false;
		}
		return true;

	}

	public boolean CallPurduce(String sql) {
		log.info(sql);
		CallableStatement st = null;
		boolean flag = false;
		try {
			// java中利用循环，即可获取结果际数据
			st = conn.prepareCall(sql);
			st.execute();
			flag = true;
		} catch (SQLException e) {

			// TODO Auto-generated catch block

			log.error(e.getMessage());

		} finally {
			try {
				if(st!=null)
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	public boolean CallPurduce(String sql, String date) {
		log.info(sql + "\tvalue:" + date);
		CallableStatement st = null;
		boolean flag = false;
		try {
			// java中利用循环，即可获取结果际数据
			st = conn.prepareCall(sql);
			st.setString(1, date);
			st.execute();
			flag = true;
		} catch (SQLException e) {

			// TODO Auto-generated catch block

			log.error(e.getMessage());

		} finally {
			try {
				if(st==null)
				st.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static void main(String[] args) throws SQLException {
	//	PropertyConfigurator.configure("./log4j.properties");
//		MysqlConnection mysql = new MysqlConnection(
//				"jdbc:mysql://192.168.2.243:3306/Uni2uni_Purchase", "root",
//				"root");
//		boolean flag = mysql
//				.sqlInsert("insert into UserPurchaseOrder(userid,ordername,purchasePrice,saveTime) values(10,'t4oit',33.46,now())");
//		if (flag) {
//			System.out.println("查询");
//			mysql.sqlSelect("select * from UserPurchaseOrder");
//		} else {
//			System.out.println("插入错误");
//		}
		
//		 MysqlConnection mysql = new MysqlConnection(
//	 				"jdbc:mysql://192.168.85.11:3306/zjMysql", "root",
//	 				"root");
	}

}