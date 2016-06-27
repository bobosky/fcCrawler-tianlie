package com.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * 文件读写操作类
 * 
 * @author Administrator
 *
 */
public class FileUtil {
	Logger log = Logger.getLogger(FileUtil.class);
	/**
	 * 文件指针
	 */
	private File file = null;
	/**
	 * 文件类型
	 */
	private String code = "utf-8";
	private OutputStreamWriter write;
	private BufferedWriter writer;
	private Object object = new Object();

	/**
	 * 文件
	 * @param filename
	 * @param code
	 */
	public FileUtil(String filename,String code2)
	{
		//System.out.println(filename+"\t"+code);
		//文件名需要重制定
		String fileName2=getNowDate(filename);
		file=new File(fileName2);
		if(!file.exists())
		{
			log.info("文件不存在:"+fileName2);
			//创建文件
			try {
				log.info("创建文件:"+fileName2);
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
		
			int zn=0;
			while(true)
			{
				zn++;
				File fileTemp=new File(fileName2.substring(0,fileName2.lastIndexOf("."))+
						"_bak_"+zn+fileName2.substring(fileName2.lastIndexOf(".")));
				if(fileTemp.exists())
				{
					continue;
				}else{
					log.info("转移文件："+fileName2+"\t"+fileTemp.getAbsolutePath());
					file.renameTo(fileTemp);
					file=new File(fileName2);
					break;
				}
			}
			//file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.code=code2;
		try {
			write =new OutputStreamWriter(new FileOutputStream(file,true),this.code);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
	    writer=new BufferedWriter(write);    
	}

	/**
	 * 写入文件的内容
	 * 
	 * @param queue
	 *            队列
	 * @param 放入队列中的数量
	 */
	public void wirte(LinkedBlockingQueue<String> queue, int size) {
		synchronized (object) {
			for (int i = 0; i < size; i++) {
				// 如果异常则不管
				String str = queue.poll();
				if (str == null || str.length() < 1) {
					continue;
				}
				try {
					//log.info("写入文件内容：" + str);
					writer.write(str);
					writer.write("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				writer.flush();
				write.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭当前文件io
	 */
	public void close() {
		try {
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取当天的文件
	 * @param filename
	 * @return
	 */
	public static String getNowDate(String filename)
	{
		
		return filename.substring(0,filename.indexOf("."))+"-"+DateFormat.parse(new Date())+"."+
				filename.substring(filename.indexOf(".")+1);
	}
	
	public static void main(String[] args) {
		String filename="d:\\sdf\\sdf\\wrqwer.txt";
		System.out.println(filename.substring(0,filename.indexOf("."))+"-"+DateFormat.parse(new Date())+"."+
				filename.substring(filename.indexOf(".")+1));
	}

}
