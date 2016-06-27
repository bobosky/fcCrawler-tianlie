package test;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.etl.FileUtil2;
import com.zj.exec.ShellExec;

import test.bean.ItemNode;
import test.bean.UserInfo;
import test.bean.UserNode;
import test.clearn.DataFillUtil;
import test.clearn.FillMethod1;
import test.clearn.ValueAddChange;
import test.ga.GA_OuterExec;

/**
 * svdfeather 程序
 * @author Administrator
 *
 */
public class SvdFeather<T> {

	private  String pathDir=System.getProperty("user.dir")+"/";
	private  String pathDir2="/home/apps/tianlie_exec/svdfeather/svd/demo/basicMF/";
	/**
	 * svd执行次数
	 */
	private int svdRunNum_round=20;
	/**
	 * 配置文件信息
	 */
	private FileUtil2 svdConfig=null;//new FileUtil2(pathDir+"svd.conf","utf-8");
	/**
	 * 执行文件;
	 */
	private FileUtil2 svdRunFile=null;
	/**
	 * 配置文件config
	 */
	private Hashtable<String,Object> config=new Hashtable<String,Object>();
	
	
	/**
	 * 最终输出的文件
	 */
	private String svdFile=null;
	/**
	 * 测试输出文件
	 */
	private String svdFileTest=null;
	
	
	private static String regex="\t";
	/**
	 * 最终生成的feather format结构数据
	 */
	private FileUtil2 writeFile = null;
	/**
	 * 测试集合 文件
	 */
	private FileUtil2 writeFileTest=null;
	/**
	 * 用于存储不同feather的使用id长度
	 * 0对应globle,1对应user
	 */
	private int[] grouplength=null;
	/**
	 * 用于存储content的信息
	 */
	private HashMap<Long,int[]> contentMap=new HashMap<Long,int[]>();
	
	HashMap<Long, String> mall =null;
	HashMap<Long, String> brand =null;
	HashMap<Long, String> category =null;
	/**
	 * id映射 只针对mall
	 */
	HashMap<Long,Long> idToMap=new HashMap<Long,Long>();
	
	long idMapIndex=-1;
	
	HashMap<Long,Long> idToMap2=new HashMap<Long,Long>();
	long idMapIndex2=-1;
	/**
	 * userFeather数量
	 */
	private int userFeatherCount=15;
	/**
	 * itemFeather数量
	 */
	private int itemFeatherCount=15;
	
	/**
	 * 存储组 对应的用户
	 */
	private ArrayList<UserInfo> userGroup =null;
	/**
	 * 存储测试结合数据
	 */
	private ArrayList<UserInfo> userGroupTest=null;
	private 		LinkedList<String> sort=new LinkedList<String>();
	
	private int[] groupLength=new int[]{100,10000};
	/**
	 * svd初始化
	 * @param groupLength 为分类长度
	 * @param pathDir 目录路径
	 */
	public SvdFeather(int[] groupLength,String pathDir)
	{
		if(groupLength==null)
		{
			this.groupLength=new int[]{100,10000};
			System.out.println("初始化feather长度为默认 globle 0-100,user 101-100000,item 1000001-max");
		}
		if(pathDir==null)
		{
			System.exit(1);
		}
		this.pathDir=pathDir;
		//判断是否存在目录如果不存在则创建目录
		//并且判断是否存在中文 不允许存在中文
		if(!pathDir.contains(GA_OuterExec.dir))
		{
			System.out.println("目录异常 防止错误操作目录 请认真检查目录地址是否正确 并以:"+GA_OuterExec.dir+":开头");
			System.exit(0);
		}
		File dir=new File(pathDir);
		System.out.println("path:"+pathDir);
		if(dir.exists())
		{
			if(!dir.isDirectory())
			{
				//如果不是目录
				System.out.println(pathDir+" :是文件不是目录请修改");
				System.exit(1);
			}else{
				System.out.println("清除目录");
				//防止错误操作
		
				ShellExec.execShell("rm -fr "+pathDir);
				System.out.println("创建目录");
				ShellExec.execShell("mkdir -p "+pathDir);
			}
		}else{
			//创建目录
			System.out.println("创建目录");
			ShellExec.execShell("mkdir -p "+pathDir);
		}
		dir=null;
		svdConfig=new FileUtil2(pathDir+"svd.conf","utf-8");
		svdRunFile=new FileUtil2(pathDir+"run.sh","utf-8");
		svdFile=pathDir+"ua.base";
		svdFileTest=pathDir+"ua.test";
	}
	/**
	 * 初始化配置文件
	 */
	public void initConfigParam()
	{
		setConfigParam("base_score ",allScoreVal/allScoreCount/5);
		setConfigParam("learning_rate ",0.005);
		setConfigParam("wd_item       ",0.004);
		setConfigParam("wd_user       ",0.004);
//		System.out.println("itemCount:"+itemMax);
//		System.out.println("userCount:"+userMax);
		setConfigParam("num_item   ",itemMax);//brand.size()*itemFeatherCount);
		setConfigParam("num_user   ",userMax);//mall.size()*userFeatherCount);
		setConfigParam("num_global ",globleMax);
		setConfigParam("num_factor ",5);
		setConfigParam("active_type ",2);
		setConfigParam("test:buffer_feature","\"ua.test.buffer\"");
		setConfigParam("buffer_feature ","\"ua.base.buffer\"");
		setConfigParam("model_out_folder","\"./\"");
	}
	/**
	 * 设置配置文件参数
	 * @param str
	 * @param obj
	 */
	public void setConfigParam(String str,Object obj)
	{
		config.put(str,obj);
		sort.add(str);
	}
	/**
	 * 获取其中一个
	 * @param str
	 * @return
	 */
	public Object getConfigParamOne(String str)
	{
		return config.get(str);
	}
	
	/**
	 * 生成contentFile文件
	 */
	public void writeContent()
	{
		System.out.println("生成content文件当前不可用");
		//System.exit(1);
		// 初始化数据
		DataFillUtil util = new FillMethod1();
		//添加获取的feather
		//使用的变量
		HashSet<Integer> feather=new HashSet<Integer>();
		feather.add(3);//prevWeeklyHits
		feather.add(4);//weeklyHits
		feather.add(5);//monthlyHits
		feather.add(6);//hits
		//feather.add(7);//avgPrice
		feather.add(8);//shoppower
		feather.add(9);//popularity
		//feather.add(10);//power
		feather.add(12);//score
		//使用离散化的变量
		HashMap<Integer,Integer> cluster=new HashMap<Integer,Integer>();
		cluster.put(3,3);//prevWeeklyHits
		cluster.put(4,3);//weeklyHits
		cluster.put(5,3);//monthlyHits
		cluster.put(6,3);//hitsS
//		cluster.put(7,3);//avgPrice
//		cluster.put(8,3);//shoppower
//		cluster.put(9,3);//popularity
//		cluster.put(10,3);//power
//		cluster.put(12,5);//score
		util.run(feather,cluster);
		util.setChange(new ValueAddChange(){
			@Override
			public int[] change(float[] fval,int[] fint) {
				// TODO Auto-generated method stub
				int i=fint[6]/20+1;
				fint[6]=i;
				return fint;//(int)(fval[3]*3);
			}
		});
		//修改
		util.addOne();//.updateOne();
		//打印离散化后数据岛 centoentoFile中
		util.printS(System.getProperty("user.dir") + "/data/contentFile.txt");
	}
	/**
	 * 执行程序
	 */
	public void run()
	{
		System.out.println("重新生成content文件");
		writeContent();
		System.out.println("读取基础文件");
		try {
			readContentFile();
			readFile();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("生成feather文件");
		writeSvdFeatherFormat();
		System.out.println("初始化配置文件");
		initConfigParam();
		System.out.println("重写配置文件内容");

		svdConfig.write(config,sort);
		svdConfig.close();
		System.out.println("重写执行文件");
		LinkedList<String> write=new LinkedList<String>();
		write.add("#!/bin/bash");
		write.add("cd "+pathDir);
		write.add("../../../tools/make_feature_buffer ua.base ua.base.buffer");
		write.add("../../../tools/make_feature_buffer ua.test ua.test.buffer");
		write.add("../../../svd_feature svd.conf num_round="+svdRunNum_round);
		write.add("for((i=1;i<="+svdRunNum_round+";i++));");//从1开始
		write.add(" do");
		write.add(" ../../../svd_feature_infer svd.conf pred=${i} name_pred=pred${i}.txt");
		write.add("java -jar ../SvdFeatherGetRmse.jar ${i} pred${i}.txt ua.test >>end.txt");//执行java程序获取emse值
		write.add("done");
		//执行完全部并获取到对应的值
		svdRunFile.write(write);
		svdRunFile.close();
		//可以运行
		ShellExec.execShell("chmod +x "+pathDir+"run.sh");
	}
	/**
	 * 读取contentbase文件
	 */
	public void readContentFile()
	{
		FileUtil2 file=new FileUtil2(System.getProperty("user.dir") + "/data/contentFile.txt","utf-8",false);
		LinkedList<String> list=file.readAndClose();
		while(list.size()>0)
		{
			String[] strList=list.pollFirst().split(regex);
			int[] value=new int[strList.length-1];
			for(int i=1;i<strList.length;i++)
			{
				value[i-1]=Integer.parseInt(strList[i]);
			}
			contentMap.put(Long.parseLong(strList[0]),value);
		}
	}
	/**
	 * 读取基础数据表
	 * @throws SQLException 
	 */
	public void readFile() throws SQLException
	{
		MysqlConnection mysql = new MysqlConnection(
				"jdbc:mysql://192.168.1.4:3306/zjMysql", "root", "root");

		// String
		// sql="select a.*,b.CommentCount,b.CommentStart,c.CategoryName from MallAndShop as a "+
		// "left join MallAndCommentCount as b "+
		// "on a.MallName=b.MallName left join MallCategory as c "+
		// "on a.CategoryCode=c.CategoryCode order by MallID,ShopID";
		String sql = "select ShopGroupID,ShopGroupName from ShopGroupIdAndName2";
		// 存储mall信息
		HashMap<Long, String> mall = new HashMap<Long, String>();
		HashMap<Long, String> brand = new HashMap<Long, String>();
		HashMap<Long, String> category = new HashMap<Long, String>();
		MysqlSelect myse = mysql.sqlSelect(sql);
		ResultSet result = myse.resultSet;
		while (result.next()) {
			Long groupId = result.getLong(1);
			String groupName = result.getString(2);
			brand.put(groupId, groupName);
		}
		result.close();
		sql = "select shopId,case when branchName is null or branchName =\"\" then shopName else CONCAT(shopName,\"(\",branchName,\")\") end from MallInfoDianping ";
		myse = mysql.sqlSelect(sql);
		result = myse.resultSet;
		while (result.next()) {
			Long groupId = result.getLong(1);
			String groupName = result.getString(2);
			mall.put(groupId, groupName);
		}
		sql = "select CategoryCode,Categoryname from MallCategory";
		myse = mysql.sqlSelect(sql);
		result = myse.resultSet;
		while (result.next()) {
			Long groupId = result.getLong(1);
			String groupName = result.getString(2);
			category.put(groupId, groupName);
		}
		CF cf=new CF(50,0.3f,0.9f,mall,brand,category,"余弦10",true,false);//计算全局
		cf.readFile(System.getProperty("user.dir")+"/data/mallAndShopAndCategory2.txt", "utf-8", "\t");
		this.mall=mall;
		this.brand=brand;
		this.category=category;
		userGroup=cf.getUserGroup();
		userGroupTest=cf.getUserGroupTest();
	}
	/**
	 * 将数据写成svdfeather结构
	 * 包括 训练和测试集合
	 */
	public void writeSvdFeatherFormat()
	{
		writeFile=new FileUtil2(this.svdFile,"utf-8");
		writeSvdFeather(writeFile,userGroup);
		writeFileTest=new FileUtil2(this.svdFileTest,"utf-8");
		writeSvdFeather(writeFileTest,userGroupTest);
	}
	
	/**
	 * 写入svd格式方法
	 * @param write
	 * @param users
	 */
	public void writeSvdFeather(FileUtil2 write,ArrayList<UserInfo> users)
	{

		LinkedList<String> list=new LinkedList<String>();
		for(int i=0;i<users.size();i++)
		{
			//获取用户
			for(UserNode user:users.get(i).getUsers())
			{
				Long userID=idToMap.get(user.getUserId());
				if(userID==null)
				{
					idMapIndex++;
					userID=idMapIndex;
					idToMap.put(user.getUserId(),idMapIndex);
				}
				//遍历物品
				for(ItemNode item:user.getItemsLinked())
				{
					Long itemId=idToMap2.get(item.getItemId());
					if(itemId==null)
					{
						idMapIndex2++;
						itemId=idMapIndex2;
						idToMap2.put(user.getUserId(),idMapIndex2);
					}
					LinkedList<String> match=match(userID,contentMap.get(user.getUserId()),itemId,contentMap.get(item.getItemId()));
					if(match==null)
					{
						continue;
					}
					list.addAll(match);
				}
				//执行写入程序
				write.write(list);
			}
		}
		System.out.println("itemCount:"+itemMax);
		System.out.println("userCount:"+userMax);
//		globleMax=itemMax+1;
//		itemMax=-1;
//		userMax=-1;
		write.close();
	}
	public int max=0;
	public int itemMax=0;
	public int userMax=0;
	public int globleMax=0;
	public double allScoreVal=0d;
	public int allScoreCount=0;
	/**
	 * 
	 * @param userNode
	 * @return
	 */
	public LinkedList<String> match(Long userID,int[] userFeather,Long itemId, int[] itemFeather)
	{
		allScoreCount++;
		if(userFeather==null || itemFeather==null)
		{
			return null;
		}
		String str=" ";
		if(userFeather.length>globleMax)
		{
			globleMax=userFeather.length-1;
		}
		if(itemFeather.length>globleMax)
		{
			globleMax=itemFeather.length-1;
		}
		globleMax=0;
		LinkedList<String> list=new LinkedList<String>();
		LinkedList<Integer> index=new LinkedList<Integer>();
		int count=0;
		for(int i=0;i<itemFeather.length;i++)
		{
			if(userFeather[i]==itemFeather[i])
			{
				count++;
				index.add(i);
			}
		}
		if(true)
		{
			if(count>max)
			{
				max=count;
				System.out.println(max);
			}
			//获取最后一个值score为target分
			String str2="";//str+(count)+str+(userFeather.length-1)+str+(itemFeather.length-1);
			//String str2=str+(0)+str+(userFeather.length-1)+str+(itemFeather.length-1);
			//+1+":"+1+
//			for(Integer ii:index)
//			{
//				str2+=str+ii+":"+1;
//			}
			int userCount=0;
			for(int i=0;i<userFeather.length-1;i++)
			{
				if(userFeather[i]==0)
				{
					continue;
				}
				userCount++;
				str2+=str+(i)+":"+userFeather[i];
				if(userMax<(1+i))
				{
					userMax=(int) (1+i);
				//	System.out.println(itemMax+"\t"+i);
				}
			}
			int itemCount=0;
			for(int i=0;i<itemFeather.length-1;i++)
			{
				if(itemFeather[i]==0)
				{
					continue;
				}
				itemCount++;
				str2+=str+(i)+":"+itemFeather[i];
				if(itemMax<(i+1))
				{
					itemMax=(int) (i+1);
				//	System.out.println(itemMax+"\t"+i);
				}
			}
			if(itemFeather.length>0)
			{
				allScoreVal+=itemFeather[itemFeather.length-1];
//				str2=itemFeather[itemFeather.length-1]
//						+str+(count)+str+userCount+str+itemCount+
//						str2;
				str2=(itemFeather[itemFeather.length-1]*1.0/5)
						+str+0+str+userCount+str+itemCount+
						str2;
//				str2=(itemFeather[itemFeather.length-1]*1.0/5)
//						+str+(count+userCount+itemCount)+str+0+str+0+
//						str2;
			}
			//System.out.println(str2);
			list.add(str2);
		}
//		for(int i=0;i<itemFeather.length;i++)
//		{
//			String str=match(userNode,userFeather[i],itemNode,itemFeather[i],i);
//			if(str!=null)
//			{
//				list.add(str);
//			}
//		}
		return list;
	}
	/**
	 * 普通方法
	 * @param userNode
	 * @param userFeather
	 * @param itemNode
	 * @param itemFeather
	 * @return
	 */
	public String match(UserNode userNode,int userFeather,ItemNode itemNode,int itemFeather,int index)
	{
		String str=" ";
		if(userFeather==itemFeather)
		{
			return 1+str+1+str+1+str+1+str+index+":"+1+str+userNode.getUserId()+":"+userFeather+str+itemNode.getItemId()+":"+itemFeather;
		}else{
			return null;
		}
	}
	public static void main(String[] args) {
		SvdFeather svdFeather=new SvdFeather(null,"/home/apps/tianlie_exec/svdfeather/svd/demo/basicMF/svddatabase/");
		svdFeather.run();
	}
}
