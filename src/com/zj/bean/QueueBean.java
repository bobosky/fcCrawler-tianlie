package com.zj.bean;

import java.util.ArrayList;
import java.util.Date;

import com.util.DateFormat;
import com.zj.exec.Main;

/**
 * 队列的bean信息
 * @author Administrator
 *
 */
public class QueueBean {
	/**
	 * 唯一表示
	 */
	private long idIndex=Main.getIdIndex();
	/**
	 * 该任务是否有效
	 */
	private boolean isRun=false;
	
	/**
	 * 程序是否开始
	 */
	private boolean isStart=false;
	/**
	 *程序是否结束 
	 */
	private boolean isEnd=false;
	/**
	 * 主程序是否执行
	 */
	private boolean isMainRun=false;
	/**
	 * 线程数
	 */
	private int threadCount=0;
	/**
	 * 任务id
	 */
	private int id=0;
	/**
	 * 获取上一个id
	 */
	private int firstId=-1;
	/**
	 * 任务名
	 */
	private String name="";
	
	/**
	 * 队列名 如果 input方法为存在则表示用内存
	 * 否则 表示用redis
	 */
	private ArrayList<String> queueInputName=new ArrayList<String>();
	
	private ArrayList<String> queueInputDatabase=new ArrayList<String>();
	
	/**
	 * 队列名 如果 output方法为存在则表示用内存
	 * 否则 表示用redis
	 * 输出文件
	 */
	private ArrayList<String> queueOutputName=new ArrayList<String>();
	/**
	 * 使用的输出数据库
	 */
	private ArrayList<String> queueOutputDatabase=new ArrayList<String>();
	/**
	 * 开始时间
	 * 如果为null则表示立即开始 否则按照时间开始
	 */
	private Date startTime=null;
	/**
	 * 周期
	 */
	private int cycleTime=0;
	/**
	 * 周期的格式
	 */
	private int cycleTimeCate=-1;
	/**
	 * 下一次开始时间
	 */
	private Date nextTime=null;
	
	/**
	 * 每次个线程解析完一个线程等待时间
	 */
	private int urlParseCycleTime=0;
	/**
	 * 周期内未结束策略
	 * 0表示 kill否则表示放置不管
	 */
	private int cycleStrageory=1;
	/**
	 * input队列最大限制
	 */
	private int inputQueueNum=100000;
	/**
	 * input队列超时控制
	 */
	private int inputQueueGetTime=1000;
	/**
	 * out队列最大控制
	 */
	private int outQueueNum=100000;
	/**
	 * out队列超时控制
	 */
	private int outQueueGetTime=10000;
	/**
	 * 队列是否初始化
	 */
	private boolean inputQueueInit=true;
	/**
	 * 输入字符串的内容
	 */
	private String inputQueueInitString="";
	/**
	 * 对应 redis的 ip 及 端口 以:分隔
	 */
	private String inputQueueUrl="";
	/**
	 * 对应 redis的 ip 及 端口 以:分隔
	 */
	private String outQueueUrl="";
	/**
	 * 打印次数
	 */
	private int printCount=10;
	
	/**
	 * 执行线程的程序 是否还在执行 如果多线程都执行结束
	 * 则为false
	 */
	private boolean execIsRun=true;
	
	/**
	 * 文件list
	 */
	private ArrayList<String> inputFileList=null;
	
	/**
	 * 是否 写内存 写文件都执行
	 */
	private boolean isAllWriteRun=false;
	
	/**
	 * 程序结束时的shell执行程序
	 */
	private String shellContent=null;
	
	/**
	 * 初始化程序
	 * 主要是为了设置下一个时间点
	 */
	public void init()
	{
		if(cycleTime!=0)
		{
			nextTime=DateFormat.addSecond(startTime, cycleTime,cycleTimeCate);
		}
	}
	/**
	 * 如果时间到下一个周期
	 * 开始时间需要被切换
	 */
	public QueueBean changeTime()
	{
		QueueBean result=new QueueBean();
		result.setStartTime(nextTime);
		if(cycleTime!=0)
		{
			System.out.println("周期:"+cycleTime);
			result.setNextTime(DateFormat.addSecond(result.getStartTime(), cycleTime,cycleTimeCate));
		}
		result.setThreadCount(threadCount);
		result.setCycleTimeCate(cycleTimeCate);
		result.setRun(isRun);
		result.setId(id);
		result.setName(name);
		result.setCycleTime(cycleTime);
		result.setCycleStrageory(cycleStrageory);
		result.setInputQueueNum(inputQueueNum);
		result.setInputQueueGetTime(inputQueueGetTime);
		result.setOutQueueNum(outQueueNum);
		result.setOutQueueGetTime(outQueueGetTime);
		result.setUrlParseCycleTime(urlParseCycleTime);
		result.setQueueInputName(queueInputName);
		result.setQueueOutputName(queueOutputName);
		result.setInputQueueUrl(inputQueueUrl);
		result.setOutQueueUrl(outQueueUrl);
		result.setInputQueueInit(inputQueueInit);
		result.setStart(false);
		result.setEnd(false);
		result.setMainRun(false);
		result.setPrintCount(printCount);
		result.setQueueOutputDatabase(queueOutputDatabase);
		result.setQueueInputDatabase(queueInputDatabase);
		result.setAllWriteRun(isAllWriteRun);
		result.setShellContent(shellContent);
		result.setFirstId(firstId);
		result.setInputQueueInitString(inputQueueInitString);
		return result;
	}
	
	
	public String getInputQueueInitString() {
		return inputQueueInitString;
	}
	public void setInputQueueInitString(String inputQueueInitString) {
		this.inputQueueInitString = inputQueueInitString;
	}
	public boolean isMainRun() {
		return isMainRun;
	}
	public void setMainRun(boolean isMainRun) {
		this.isMainRun = isMainRun;
	}
	public int getFirstId() {
		return firstId;
	}
	public void setFirstId(int firstId) {
		this.firstId = firstId;
	}
	public String getShellContent() {
		return shellContent;
	}
	public void setShellContent(String shellContent) {
		this.shellContent = shellContent;
	}
	public ArrayList<String> getQueueInputDatabase() {
		return queueInputDatabase;
	}
	public void setQueueInputDatabase(ArrayList<String> queueInputDatabase) {
		this.queueInputDatabase = queueInputDatabase;
	}
	public void addQueueInputDatabase(String queueInputDatabase) {
		this.queueInputDatabase.add(queueInputDatabase);
	}
	
	public boolean isAllWriteRun() {
		return isAllWriteRun;
	}
	public void setAllWriteRun(boolean isAllWriteRun) {
		this.isAllWriteRun = isAllWriteRun;
	}
	public boolean isExecIsRun() {
		return execIsRun;
	}
	public void setExecIsRun(boolean execIsRun) {
		this.execIsRun = execIsRun;
	}
	public int getPrintCount() {
		return printCount;
	}
	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}
	public boolean isInputQueueInit() {
		return inputQueueInit;
	}
	public void setInputQueueInit(boolean inputQueueInit) {
		this.inputQueueInit = inputQueueInit;
	}
	public String getInputQueueUrl() {
		return inputQueueUrl;
	}
	public void setInputQueueUrl(String inputQueueUrl) {
		this.inputQueueUrl = inputQueueUrl;
	}
	public String getOutQueueUrl() {
		return outQueueUrl;
	}
	public void setOutQueueUrl(String outQueueUrl) {
		this.outQueueUrl = outQueueUrl;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public int getCycleTimeCate() {
		return cycleTimeCate;
	}


	public void setCycleTimeCate(int cycleTimeCate) {
		this.cycleTimeCate = cycleTimeCate;
	}


	public boolean isRun() {
		return isRun;
	}
	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	
	
	public ArrayList<String> getQueueInputName() {
		return queueInputName;
	}
	
	public String getQueueInputName(int index)
	{
		if(queueInputName.size()>=index+1)
		{
			return queueInputName.get(index);
		}
		return null;
	}
	public void setQueueInputName(ArrayList<String> queueInputName) {
		this.queueInputName = queueInputName;
	}
	public void addQueueInputName(String queueInputName)
	{
		this.queueInputName.add(queueInputName);
	}
	public ArrayList<String> getQueueOutputName() {
		return queueOutputName;
	}
	public String getQueueOutputName(int index)
	{
		if(queueOutputName.size()>=index+1)
		{
			return queueOutputName.get(index);
		}
		return null;
	}

	public ArrayList<String> getQueueOutputDatabase() {
		return queueOutputDatabase;
	}
	
	public String getQueueOutputDatabase(int index)
	{
		if(queueOutputDatabase.size()>=index+1)
		{
			return queueOutputDatabase.get(index);
		}
		return null;
	}
	public void setQueueOutputDatabase(ArrayList<String> queueOutputDatabase) {
		this.queueOutputDatabase = queueOutputDatabase;
	}
	public void addQueueOutputDatabase(String queueOutputDatabase) {
		this.queueOutputDatabase.add(queueOutputDatabase);
	}
	
	public void setQueueOutputName(ArrayList<String> queueOutputName) {
		this.queueOutputName = queueOutputName;
	}
	public void addQueueOutputName(String queueOutputName) {
		this.queueOutputName.add(queueOutputName);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public int getCycleTime() {
		return cycleTime;
	}
	public void setCycleTime(int cycleTime) {
		this.cycleTime = cycleTime;
	}
	public Date getNextTime() {
		return nextTime;
	}
	public void setNextTime(Date nextTime) {
		this.nextTime = nextTime;
	}
	public int getCycleStrageory() {
		return cycleStrageory;
	}
	public void setCycleStrageory(int cycleStrageory) {
		this.cycleStrageory = cycleStrageory;
	}
	public int getInputQueueNum() {
		return inputQueueNum;
	}
	public void setInputQueueNum(int inputQueueNum) {
		this.inputQueueNum = inputQueueNum;
	}
	public int getInputQueueGetTime() {
		return inputQueueGetTime;
	}
	public void setInputQueueGetTime(int inputQueueGetTime) {
		this.inputQueueGetTime = inputQueueGetTime;
	}
	public int getOutQueueNum() {
		return outQueueNum;
	}
	public void setOutQueueNum(int outQueueNum) {
		this.outQueueNum = outQueueNum;
	}
	public int getOutQueueGetTime() {
		return outQueueGetTime;
	}
	public void setOutQueueGetTime(int outQueueGetTime) {
		this.outQueueGetTime = outQueueGetTime;
	}
	public int getUrlParseCycleTime() {
		return urlParseCycleTime;
	}
	public void setUrlParseCycleTime(int urlParseCycleTime) {
		this.urlParseCycleTime = urlParseCycleTime;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public boolean isEnd() {
		return isEnd;
	}
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	public long getIdIndex() {
		return idIndex;
	}
	public void setIdIndex(long idIndex) {
		this.idIndex = idIndex;
	}
	public ArrayList<String> getInputFileList() {
		return inputFileList;
	}
	public void setInputFileList(ArrayList<String> inputFileList) {
		this.inputFileList = inputFileList;
	}
	
	
	
	
}
