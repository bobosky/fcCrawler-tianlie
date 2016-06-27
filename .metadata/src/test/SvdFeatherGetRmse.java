package test;

import java.util.ArrayList;

import com.zj.exec.ShellExec;

/**
 * 对最终的执行程序获取Emse值
 * @author Administrator
 *
 */
public class SvdFeatherGetRmse {

	
	/**
	 * 
	 * @param dirPath 执行的目录地址
	 * end.txt 文件存储了当前值对应的所有 从1开始到最后的所有模型的rmse值
	 * 获取其中最小的值作为实际值
	 * @return
	 */
	public static ModelValue get(String dirPath)
	{
		//从end.txt文件种获取文件文件信息
		//其中为最小误差所以为获取最小值
		ArrayList<String> get=ShellExec.execShellAndGet("cat "+dirPath+"/end.txt");
		int index=0;
		double min=Double.MAX_VALUE;
		ModelValue result=new ModelValue();
		for(String str:get)
		{
			String[] strList=str.split("\t");
			if(strList.length<=1)
			{
				return null;
			}
			//第一个为index 第二个为值
			Double val=Double.parseDouble(strList[1]);
			if(result.getRmse()>val)
			{
				result.setIndex(Integer.parseInt(strList[0]));
				result.setRmse(val);
			}
		}
		System.out.println("modelval:"+result.getIndex()+"\t"+result.getRmse());
		return result;
	}
	/**
	 * 将 预测值 于ua.test文件种的第一个数值 计算误差值 rmse
	 * @param filePath1 为预测值
	 * @param filePath2 为实际值
	 * 打印最终的rmse值
	 */
	public static void exec(int index,String filePath1,String filePath2)
	{
		ArrayList<String> predict=ShellExec.execShellAndGet("cat "+filePath1);
	//	System.out.println("cat "+filePath2+"|cut -d ' ' -f 1");
		ArrayList<String> real=ShellExec.execShellAndGet("cat "+filePath2+" |cut -d ' ' -f 1");//获取实际值
		double val=0;
		for(int i=0;i<predict.size();i++)
		{
			val+=Math.abs(Double.parseDouble(predict.get(i))-Double.parseDouble(real.get(i)));
		}
		val/=predict.size();
		System.out.println(index+"\t"+val);
	}
	/**
	 * 处理成rmse程序
	 * @param args
	 */
	public static void main(String[] args) {
//		int index=Integer.parseInt(args[0]);
		//输入为 第一个为 对应的model序号
		//第二个为预测文件
		//第三个为实际的ua.test文件
	//	SvdFeatherGetRmse.exec(index, args[1],args[2]);
		SvdFeatherGetRmse.exec(0, "/home/apps/tianlie_exec/svdfeather/svd/demo/basicMF/svddatabase/pred5.txt",
				"/home/apps/tianlie_exec/svdfeather/svd/demo/basicMF/svddatabase/ua.test");
	}
}
