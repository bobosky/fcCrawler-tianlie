package com.zj.exec;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.etl.FileToMongo;
import com.etl.InitCompanyMapMongo;
import com.etl.MongoToMysql;
import com.intoMongo.IntoMongoDB;
import com.util.FileUtil;
import com.zj.bean.QueueBean;
import com.zj.config.FileConfig;
import com.zj.config.QueueConfigXml;
import com.zj.etlFilter.FangFilter;

public class Main {
	private static Logger log = Logger.getLogger(Main.class);

	public static ArrayList<QueueBean> taskQueueBeanList = null;

	/**
	 * 存储有效的Runnable
	 */
	private static ArrayList<Runnable> taskList = null;
	/**
	 * 存储有效的队列信息
	 */
	private static ArrayList<QueueBean> taskBean = null;

	/**
	 * 存储执行的线程
	 */
	private static ArrayList<Thread> taskListThread = null;

	/**
	 * 唯一标识
	 */
	public static long idIndex = 0;
	/**
	 * 存储变化前最大的那个index值
	 */
	public static long currentMaxIndex = 0;
	/**
	 * taskQueueBeanList是否发成变化
	 */
	public static boolean isChange = true;

	/**
	 * 监控周期
	 */
	public static int monitorCycle = 10000;

	/**
	 * 通过 queue的关联id获取和当前queue以来的上一个queue
	 * @param queue
	 * @param queueList
	 * @return
	 */
	public static QueueBean getTaskById(QueueBean queue,ArrayList<QueueBean> queueList)
	{
		if(queue.getFirstId()<0)
		{
			return null;
		}
		for(QueueBean bean:queueList)
		{

			if(bean.getId()==queue.getFirstId())
			{
				return bean;
			}
		}
		return null;
	}
	
	public static void run() {
		// 所有所有任务
		taskQueueBeanList = QueueConfigXml.taskQueueBeanList;
		taskList = new ArrayList<Runnable>();
		taskListThread = new ArrayList<Thread>();
		taskBean = new ArrayList<QueueBean>();
		for (int i = 0; i < taskQueueBeanList.size(); i++) {
			if (taskQueueBeanList.get(i).isRun()) {
				taskBean.add(taskQueueBeanList.get(i));
				if(taskQueueBeanList.get(i).isStart())
				{
					// 如果为执行
					Runnable run1 = run(taskQueueBeanList.get(i),getTaskById(taskQueueBeanList.get(i),taskQueueBeanList));
					taskList.add(run1);
					Thread tr = new Thread(run1, "main"
							+ taskQueueBeanList.get(i).getIdIndex());
					tr.start();
					log.info(taskQueueBeanList.get(i).getName() + ":启动");
					taskListThread.add(tr);
				}
			}
		}
		// 启动监控
		MonitorThread monitorThread = new MonitorThread(taskBean, taskList,
				taskListThread);
		Thread monitor = new Thread(monitorThread, "monitor");
		monitor.start();
		log.info("监控程序启动");
	}

	/**
	 * 获取唯一标识
	 * 
	 * @return
	 */
	public static synchronized long getIdIndex() {
		idIndex++;
		return idIndex;
	}

	public static void main22(String[] args) {
		PropertyConfigurator.configure("src/log4j.properties");
		// 执行停车场爬虫
		// Main.runParking(4);
		// 执行job爬虫
		// Main.runJobs(4);
		// 执行地铁口对应公交车站
		// Main.runSubwayAndBus(4);
		// 执行公交信息
		// Main.runBusAndStation(4);
		// 执行搜房信息
		// Main.runFang(10);

		// 将本地文件写入mongodb中
		// String ip="192.168.85.11";
		// int port=10001;
		// String database="demo";
		// 公交车信息
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.busAndStationFile),ip,
		// port, database, "busStationInfo",1000);
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.JobsFile),ip,
		// port, database, "subwayAndJob",1000);
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.parkingFile),ip,
		// port, database, "parkingStation",1000);
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.subwayAndBusFile),ip,
		// port, database, "subwayAndBus",1000);
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.fangFile),ip,
		// port, database, "fang",1000);

	}

	public static void stop() {
		// for(int i=0;i<taskListThread.size();i++)
		// {
		// try {
		// taskListThread.get(i).join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
	}

	/**
	 * 执行一个 任务
	 * 
	 * @param name
	 */
	public static Runnable run(QueueBean bean,QueueBean beanFirst) {
		bean.setMainRun(true);
		if (bean.getName().equals("parking")) {
			return new MainParkingThread(bean,beanFirst);
		} else if (bean.getName().equals("jobs")) {
			
			System.out.println("job:" + bean.isExecIsRun());
			return new MainJobsThread(bean,beanFirst);
		} else if (bean.getName().equals("subwayAndBus")) {
			return new MainSubwayAndBusThread(bean,beanFirst);
		} else if (bean.getName().equals("busAndStation")) {
			return new MainBusAndStationThread(bean,beanFirst);
		}else if(bean.getName().equals("fangList"))
		{
			return new MainFangListThread(bean,beanFirst);
		}else if (bean.getName().equals("fang")) {
			return new MainFangThread(bean,beanFirst);
		}else if(bean.getName().equals("fangNew"))
		{
			return new MainFangNewThread(bean,beanFirst);
		}else if (bean.getName().equals("company")) {
			return new MainCompany51JobThread(bean,beanFirst);
		} else if (bean.getName().equals("companyEnd")) {
			return new MainCompany51JobGetComThread(bean,beanFirst);
		} else if (bean.getName().equals("companySearch")) {
			return new MainCompanyZhaopinThread(bean,beanFirst);
		} else if (bean.getName().equals("companySearchEnd")) {
			return new MainCompanyZhaopinGetComThread(bean,beanFirst);
		} else if (bean.getName().equals("companyGetJobQueue")) {
			return new MainCompany51JobDescThread(bean,beanFirst);
		} else if (bean.getName().equals("companyGetJobDesc")) {
			return new MainCompany51JobDescJobThread(bean,beanFirst);
		} else if(bean.getName().equals("dianpingArea"))
		{
			return new MainBussinessAreaThread(bean,beanFirst);
		}
		return null;
	}

	public static void main(String[] args) {
		// 初始化log4j
		PropertyConfigurator.configure("./log4j.properties");
		// 执行爬虫
		if (args.length == 0 || args[0].equals("help")) {
			System.out.println("# 执行爬虫");
			System.out.println("java -jar  xxx.jar crawler");
			System.out.println("#执行 文件入mongo");
			System.out.println("java -jar  xxx.jar intoMongo busAndStationFile|jobsFile|parkingFile|busAndStationFile|subwayAndBusFile|(or file path) collectionName");
			System.out.println("java -jar  xxx.jar intoMongo busAndStationFile|jobsFile|parkingFile|busAndStationFile|subwayAndBusFile|(or file path) ip port database collection printcount regex");
			System.out.println("# regex 只在 fang 有效 并赋值为 fangCode");
			System.out.println("# 执行  mongo 入 mysql");
			System.out.println("java -jar xxx.jar mongoIntoMysql busAndStation|parking|subwayAndBus|subwayAndJob|buspoi|fang|51company");
			System.out.println(" 执行 搜房 mongo数据更新 搜房更新 source 为基础 update为增量");
			System.out.println("java -jar xxx.jar intoMongUpdate fangUpdate ip port database collectionSource collectionUpdate filePath");
			System.out.println("执行搜房 mongo数据的价格，租金校验程序 生成 priceEtlStatus，hireEtlStatus 字段");
			System.out.println("java -jar xxx.jar intoMongoUpdateFangEtl");
			System.out.println("执行 公司 mongo信息 到redis Map全部更新");
			System.out.println("java -jar xxx.jar companyMapUpdate mongoip mongoPort mongodatabase redisip redisport");
			System.out.println("执行 公司 mongo信息 全量插入");
			System.out.println("java -jar xxx.jar intoMongoCompany companFilePath jobFilePath outputFilePath ip port database collection printcount");
			System.out.println("执行 公司 mongo信息 增量更新");
			System.out.println("java -jar xxx.jar intoMongoCompanyUpdate companFilePath jobDescFilePath jobFilePath ip port database collection printcount limitcount");
			return;
		} else if (args[0].equals("crawler")) {
			// 默认执行爬虫
			Main.run();
		} else if (args[0].equals("intoMongo")) {
			// 如果为爬虫则
			String file = "";
			String regex = "test";
			if (args[1].equals("busAndStationFile")) {
				file = FileUtil.getNowDate(FileConfig.busAndStationFile);
			} else if (args[1].equals("jobsFile")) {
				file = FileUtil.getNowDate(FileConfig.jobsFile);
			} else if (args[1].equals("parkingFile")) {
				file = FileUtil.getNowDate(FileConfig.parkingFile);
			} else if (args[1].equals("busAndStationFile")) {
				file = FileUtil.getNowDate(FileConfig.busAndStationFile);
			} else if (args[1].equals("subwayAndBusFile")) {
				file = FileUtil.getNowDate(FileConfig.subwayAndBusFile);
			} else if (args[1].equals("fang")) {
				file = FileUtil.getNowDate(FileConfig.fangFile);
				regex = "fangCode";
			} else if (args[1].length() > 0) {
				file = args[1];
			} else {
				log.error("输入参数错误");
				System.exit(1);
			}

			String ip = "192.168.1.4";

			int port = 27017;

			String database = "demo";

			String collectionName = "";

			int printCount = 1000;

			if (args.length == 3) {
				collectionName = args[2];
			} else {
				try {
					ip = args[2];
					port = Integer.parseInt(args[3]);
					database = args[4];
					collectionName = args[5];
					printCount = Integer.parseInt(args[6]);
					regex = args[7];
				} catch (Exception e) {
					log.info("输入 参数错误");
					System.exit(1);
				}
			}

			// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.busAndStationFile),ip,
			// port, database, "busStationInfo",1000,"test");
			IntoMongoDB.readWriteToMongod(file, ip, port, database,
					collectionName, printCount, regex);
		} else if (args[0].equals("intoMongoUpdate")) {
			// 如果为爬虫则
			String file = "";
			if (args[1].equals("fangUpdate")) {
				file = FileUtil.getNowDate(FileConfig.fangFile);
			} else if (args[1].length() > 0) {
				file = args[1];
			} else {
				log.error("输入参数错误");
				System.exit(1);
			}

			String ip = "192.168.1.4";

			int port = 27017;

			String database = "demo";

			String collectionSource = "";

			String collectionUpdate="";
			
			int printCount = 1000;
			String filePath="";
			//String regex = "test";
				try {
					ip = args[2];
					port = Integer.parseInt(args[3]);
					database = args[4];
					collectionSource = args[5];
					collectionUpdate = args[6];
					filePath=args[7];
					// regex=args[7];
				} catch (Exception e) {
					e.printStackTrace();
					log.error("输入参数错误");
					System.exit(1);
				}
			// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.busAndStationFile),ip,
			// port, database, "busStationInfo",1000,"test");
//			IntoMongoDB.readWriteToMongodUpdateFang(file, ip, port, database,
//					collectionName, printCount, "");
			FileToMongo exec=new FileToMongo(ip,port,database,collectionSource,collectionUpdate,"fang",filePath);
			exec.run();
			log.info("执行成功");
		}else if (args[0].equals("intoMongoUpdateFangEtl")) {
			// 如果为爬虫则
			FangFilter filter=new FangFilter(1D);
			filter.runAll();
			log.info("执行成功");
		}
		else if (args[0].equals("mongoIntoMysql")) {
			if (args[1].equals("busAndStation")) {
				MongoToMysql exec = new MongoToMysql("busAndStation");
				exec.run();
			} else if (args[1].equals("parking")) {
				MongoToMysql exec2 = new MongoToMysql("parking");
				exec2.run();
			} else if (args[1].equals("buswayAndBus")) {
				MongoToMysql exec3 = new MongoToMysql("subwayAndBus");
				exec3.run();
			} else if (args[1].equals("subwayAndJob")) {
				MongoToMysql exec4 = new MongoToMysql("subwayAndJob");
				exec4.run();
			}
			// MongoToMysql exec5 = new MongoToMysql("subwayAndJob");
			// exec5.run();
			// MongoToMysql exec6 = new MongoToMysql("test");
			// exec6.run();
			else if (args[1].equals("buspoi")) {
				MongoToMysql exec7 = new MongoToMysql("poi", "地铁站");
				exec7.setMongoDatabase("demo");
				exec7.setMongoIp("192.168.1.4");
				exec7.setMongoPort(27017);
				exec7.run();
			}
			else if (args[1].equals("fang")) {
				MongoToMysql exec8 = new MongoToMysql("fang");
				exec8.run();
			} else if (args[1].equals("51company")) {
				MongoToMysql exec9 = new MongoToMysql("51company");
				exec9.run();
			}
			log.info("执行成功");
			// MongoToMysql exec7 = new MongoToMysql("busPoi","公交站");
			// exec7.setMongoDatabase("mydb");
			// exec7.setMongoIp("192.168.1.37");
			// exec7.setMongoPort(27017);
			// exec7.run();
		}else if(args[0].equals("companyMapUpdate"))
		{
			try {
				InitCompanyMapMongo.run(args[1],Integer.parseInt(args[2]),args[3],args[4],Integer.parseInt(args[5]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		else if (args[0].equals("intoMongoCompany")) {
			String ip = "192.168.1.4";

			int port = 27017;

			String database = "demo";

			String collectionName = "";

			int printCount = 1000;
			String fileName = args[1];
			String fileName2 = args[2];
			String outputFile = args[3];
			try {
				ip = args[4];
				port = Integer.parseInt(args[5]);
				database = args[6];
				collectionName = args[7];
				try {
					printCount = Integer.parseInt(args[8]);
				} catch (Exception e) {
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("输入参数错误");
				System.exit(1);
			}
			IntoMongoDB.readWriteToMongodJobCompany(fileName, fileName2,
					outputFile, ip, port, database, collectionName, printCount);
		} else if (args[0].equals("intoMongoCompanyUpdate")) {
			String ip = "192.168.1.4";

			int port = 27017;

			String database = "demo";

			String collectionName = "";

			int printCount = 1000;
			String fileName = args[1];
			String fileName2 = args[2];
			String outputFile = args[3];
			int limitCount = 1000;
			try {
				ip = args[4];
				port = Integer.parseInt(args[5]);
				database = args[6];
				collectionName = args[7];
				limitCount = Integer.parseInt(args[8]);
				try {
					printCount = Integer.parseInt(args[8]);
				} catch (Exception e) {
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("输入参数错误");
				System.exit(1);
			}
			IntoMongoDB.readWriteToMongodJobCompanyUpdate(fileName, fileName2,
					outputFile, ip, port, database, collectionName, printCount,
					limitCount);
		}
		// Main.run();
		// 执行入库
		// String ip="192.168.85.11";
		// int port=10001;
		// String database="demo";

		// String ip="192.168.1.4";
		// int port=27017;
		// String database="demo";
		//
		// String ip="192.168.1.37";
		// int port=27017;
		// String database="mydb";
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.busAndStationFile),ip,
		// port, database, "busStationInfo",1000,"test");
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.jobsFile),ip,
		// port, database, "subwayAndJob",1000,"test");
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.parkingFile),ip,
		// port, database, "parkingStation",1000,"test");
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.subwayAndBusFile),ip,
		// port, database, "subwayAndBus",1000,"test");
		//
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.fangFile),ip,
		// port, database, "fang",1000,"fangCode");
		// IntoMongoDB.readWriteToMongod(FileUtil.getNowDate(FileConfig.jobsFile),ip,
		// port, database, "subwayAndJobSimple",1000,"test");
		// IntoMongoDB.readWriteToMongod("f:\\zj\\jobsDesc-2014-12-04.txt",ip,
		// port, database, "subwayAndJobSimple",1000,"test");
		// IntoMongoDB.readWriteToMongod("f:\\zj\\jobsDesc-2014-12-05.txt",ip,
		// port, database, "subwayAndJobSimple",1000,"test");
		// IntoMongoDB.readWriteToMongod("f:\\zj\\jobsDesc-2014-12-06.txt",ip,
		// port, database, "subwayAndJobSimple",1000,"test");
		// IntoMongoDB.readWriteToMongod("f:\\zj\\jobsDesc-2014-12-07.txt",ip,
		// port, database, "subwayAndJobSimple",1000,"test");
		// IntoMongoDB.readWriteToMongod("f:\\zj\\jobsDesc-2014-12-08.txt",ip,
		// port, database, "subwayAndJobSimple",1000,"test");

	}

}
