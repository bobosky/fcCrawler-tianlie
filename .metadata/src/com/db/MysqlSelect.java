package com.db;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 
 * Title.mysql 连接类 <br>
 * Description.
 * <p>
 * Copyright: Copyright (c) 2014-6-26 上午11:51:04
 * <p>
 * Company: 联嘉云贸易有限公司
 * <p>
 * Author: tianlie@uni2uni-js.com
 * <p>
 * Version: 1.0
 * <p>
 */
 public class MysqlSelect {
	public boolean flag = false;
	public Statement statement = null;
	public ResultSet resultSet = null;

	public MysqlSelect(){
		
	}
	public MysqlSelect(boolean flag, Statement statement, ResultSet resultSet) {
		this.flag = flag;
		this.statement = statement;
		this.resultSet = resultSet;
	}
 }