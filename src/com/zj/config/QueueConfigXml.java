package com.zj.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.util.DateFormat;
import com.util.ParamStatic;
import com.zj.bean.QueueBean;


/**
 * sqlserver配置信息
 * @author Administrator
 *
 */
public class QueueConfigXml {
	private static Logger log = Logger.getLogger(QueueConfigXml.class);
	
	public static ArrayList<QueueBean> taskQueueBeanList=new ArrayList<QueueBean>();
	
	
	public static int getSize()
	{
		return taskQueueBeanList.size();
	}
	
	static {
		taskQueueBeanList=new  ArrayList<QueueBean>();
		readFileXml();
	}
/**
 * 从xml中读取配置文件
 */
	public static void readFileXml()
	{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db=null;
		try {
			db = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Document doc=null;
		try {
			doc = db.parse(new File("./queueInfo.xml"));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
        Element elmtInfo = doc.getDocumentElement();
        NodeList nodes = elmtInfo.getChildNodes();
        log.info("读取队列配置文件");
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node result = nodes.item(i);
          //  System.out.println(i+":"+nodes.getLength()+":current:"+result.getNodeName());
            //多个任务的遍历
            if(result.getNodeName().equals("#text"))
            {
            	continue;
            }
            if (result.getNodeType() == Node.ELEMENT_NODE && result.getNodeName().equals("job"))
            {
            	NamedNodeMap  attributes=result.getAttributes();
            	QueueBean queueBean=new QueueBean();
            	for(int k=0;k<attributes.getLength();k++)
            	{
            		String name=attributes.item(k).getNodeName();
            		String value=attributes.item(k).getNodeValue();
            		//添加 url的string处理方法
            		if(name.equals("id"))
            		{
            			queueBean.setId(Integer.parseInt(value));
            		}else if(name.equals("isRun"))
            		{
            			if(value.equals("true"))
            			{
            				queueBean.setRun(true);
            			}
            		} else if(name.equals("name"))
            		{
            			queueBean.setName(value);
            		}else if(name.equals("threadCount"))
            		{
            			queueBean.setThreadCount(Integer.parseInt(value));
            		}else if(name.equals("fristTaskId"))
            		{
            			queueBean.setFirstId(Integer.parseInt(value));
            		}
            	}
        		NodeList beanList = result.getChildNodes();
            	for(int p=0;p<beanList.getLength();p++)
            	{
            		//对于每一个analysis都要建立对应的Bean
            		Node bean=beanList.item(p);
            		String value2=bean.getTextContent();
            		if (bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("StartTime"))
            		{
            			if(value2.equals("now"))
            			{
            				queueBean.setStartTime(new Date());
            				queueBean.setStart(true);
            			}else{
            				//将时间格式处理了
            				queueBean.setStartTime(DateFormat.strToDate(value2));
            			}
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("CycleTime"))
            		{
            			String[] str=value2.split(ParamStatic.interverSplit);
            			if(str.length==2)
            			{
            				if(str[1].equals("year"))
            				{
            					queueBean.setCycleTime(Integer.parseInt(str[0]));
            					queueBean.setCycleTimeCate(Calendar.YEAR);
            				}
            				else if(str[1].equals("month"))
            				{
            					queueBean.setCycleTime(Integer.parseInt(str[0]));
            					queueBean.setCycleTimeCate(Calendar.MONTH);
            				}
            				else if(str[1].equals("day"))
            				{
            					queueBean.setCycleTime(Integer.parseInt(str[0]));
            					queueBean.setCycleTimeCate(Calendar.DATE);
            				}else if(str[1].equals("hour"))
            				{
            					queueBean.setCycleTime(Integer.parseInt(str[0])*3600);
            					queueBean.setCycleTimeCate(Calendar.HOUR);
            				}else if(str[1].equals("minute"))
            				{
            					queueBean.setCycleTime(Integer.parseInt(str[0])*60);
            					queueBean.setCycleTimeCate(Calendar.MINUTE);
            				}else if(str[1].equals("second"))
            				{
            					queueBean.setCycleTime(Integer.parseInt(str[0]));
            					queueBean.setCycleTimeCate(Calendar.SECOND);
            				}else{
            					log.error("开始时间输入异常");
            				}
            			}
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("CycleStrategy"))
            		{
            			if(value2.equals("kill"))
            			{
            				queueBean.setCycleStrageory(ParamStatic.kill);
            			}else{
            				//默认全部为kill
            				queueBean.setCycleStrageory(ParamStatic.kill);
            			}
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("InputQueueNum"))
            		{
            			queueBean.setInputQueueNum(Integer.parseInt(value2));
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("InputQueueGetTime"))
            		{
            			queueBean.setInputQueueGetTime(Integer.parseInt(value2));
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("InputQueueDb"))
            		{
            			if(value2==null||value2.equals(""))
            			{
            				
            			}else{
            				queueBean.addQueueInputDatabase(value2.substring(0,value2.indexOf(":")));
            				queueBean.addQueueInputName(value2.substring(value2.indexOf(":")+1));
            			}
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("InputQueueUrl"))
            		{
            			queueBean.setInputQueueUrl(value2);
            		}
            		else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("InputQueueInit"))
            		{
            			queueBean.setInputQueueInit(Boolean.parseBoolean(value2));
            		}
            		else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("InputQueueInitString"))
            		{
            			queueBean.setInputQueueInitString(value2);
            		}
            		else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("OutQueueNum"))
            		{
            			queueBean.setOutQueueNum(Integer.parseInt(value2));
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("OutQueueGetTime"))
            		{
            			queueBean.setOutQueueGetTime(Integer.parseInt(value2));
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("urlParseCycleTime"))
            		{
            			queueBean.setUrlParseCycleTime(Integer.parseInt(value2));
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("OutQueueDb"))
            		{
            			if(value2==null||value2.equals(""))
            			{
            				
            			}else{
            				queueBean.addQueueOutputDatabase(value2.substring(0,value2.indexOf(":")));
            				queueBean.addQueueOutputName(value2.substring(value2.indexOf(":")+1));
            			}
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("OutQueueUrl"))
            		{
            			queueBean.setOutQueueUrl(value2);
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("PrintCount"))
            		{
            			queueBean.setPrintCount(Integer.parseInt(value2));
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("InputFile"))
            		{
            			if(queueBean.getInputFileList()==null)
            			{
            				queueBean.setInputFileList(new ArrayList<String>());
            			}
            			queueBean.getInputFileList().add(value2);
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("IsAllWriteRun"))
            		{
            			queueBean.setAllWriteRun(Boolean.parseBoolean(value2));
            		}else if(bean.getNodeType() == Node.ELEMENT_NODE && bean.getNodeName().equals("ShellExecContent"))
            		{
            			queueBean.setShellContent(value2);
            		}
            		
            		
            		
                	
            	}
            	//获取子内容
            	//初始化下一个时间
            	queueBean.init();
            	taskQueueBeanList.add(queueBean);
            }
        }
		 log.info("配置文件读取完成：任务总数:"+taskQueueBeanList.size());
	}
	public static void main(String[] args) {
		System.out.println(QueueConfigXml.getSize());
	}
}