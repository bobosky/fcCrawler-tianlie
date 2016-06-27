package com.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HttpDemo {

	public static String getString(String lp,String param,String code)
	{
		@SuppressWarnings("deprecation")
		String ll = URLEncoder.encode(param);
		URL u=null;
		try {
			u = new URL(lp+ll);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String result="";
		try {
	        byte[] b = new byte[256];   
	        InputStream in = null;   
	        ByteArrayOutputStream bo = new ByteArrayOutputStream();   
	            try {   
	                in = u.openStream();   
	                int i;
	                while ((i = in.read(b)) != -1) { 
	                    bo.write(b, 0, i);   
	                }
	                result =bo.toString(code);//
	                bo.reset();   
	            } catch (Exception e) {   
	           //     System.out.println(e.getMessage());   
	            } finally {   
	                if (in != null) {   
	                    in.close();   
	                }   
	            }  
			   } catch (Exception ex) {   
			        System.out.println(ex.getMessage());   
			 }
			   return result;
	}
	
	@SuppressWarnings("unused")
	private static void doCompressFile(String inFileName) {
		 
        try {
        
            System.out.println("Creating the GZIP output stream.");
            String outFileName = inFileName + ".gz";
            GZIPOutputStream out = null;
            try {
                out = new GZIPOutputStream(new FileOutputStream(outFileName));
            } catch(FileNotFoundException e) {
                System.err.println("Could not create file: " + outFileName);
                System.exit(1);
            }
                    
 
            System.out.println("Opening the input file.");
            FileInputStream in = null;
            try {
                in = new FileInputStream(inFileName);
            } catch (FileNotFoundException e) {
                System.err.println("File not found. " + inFileName);
                System.exit(1);
            }
 
            System.out.println("Transfering bytes from input file to GZIP Format.");
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
 
            System.out.println("Completing the GZIP file");
            out.finish();
            out.close();
        
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
    }
	
	public static String getString2(String lp,String param,String code)
	{
		@SuppressWarnings("deprecation")
		String ll = URLEncoder.encode(param);
		URL u=null;
		try {
			u = new URL(lp+ll);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String result="";
		try {
	        byte[] b = new byte[256];   
	        InputStream in = null;
	        ByteArrayOutputStream out=new ByteArrayOutputStream();
	        GZIPOutputStream bo = new GZIPOutputStream(out);   
	            try {   
	                in = u.openStream();   
	                int i;
	                while ((i = in.read(b)) != -1) { 
	                    bo.write(b, 0, i);   
	                }
	                bo.finish();
	                bo.close();
	                result =out.toString(code);;//
	                out.reset();   
	            } catch (Exception e) {   
	           //     System.out.println(e.getMessage());   
	            } finally {   
	                if (in != null) {   
	                    in.close();   
	                }   
	            }  
			   } catch (Exception ex) {   
			        System.out.println(ex.getMessage());   
			 }
		System.out.println("结束");
			   return result;
	}
	
	public static void main(String[] args) throws IOException {
		
		String str=HttpDemo.getString2("http://jianwaisoho010.fang.com/office/", "","gb2312");
		//String str=HttpDemo.getString("http://office.fang.com/loupan/house/", "","gb2312");
		for(int i=1;i<100;i++)
		{
			if(i*100>str.length())
			{
				break;
			}
			System.out.println(i+":"+str.substring((i-1)*100,i*100));
		}
		
		
		URL url = new URL("http://jianwaisoho010.fang.com/office/");
        InputStream is = url.openStream();
        GZIPInputStream in = new GZIPInputStream(is);
 
        InputStreamReader isr = new InputStreamReader(in,"gb2312");
        char[] buffer = new char[1024];
        int pos = 0;
        StringBuilder sb = new StringBuilder();
         
        while((pos = isr.read(buffer)) != -1){
            sb.append(new String(buffer,0,pos));
        }
        isr.close();
        is.close();
        System.out.println(sb);
	}
}
