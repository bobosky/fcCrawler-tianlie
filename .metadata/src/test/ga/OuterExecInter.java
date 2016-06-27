package test.ga;

import java.util.ArrayList;
import java.util.Collection;

import test.ModelValue;

/**
 * 外部程序执行接口
 * @author Administrator
 *
 */
public interface OuterExecInter {
//	/**
//	 * 获取外部执行程序值
//	 * @return
//	 */
//	public T get();
//	/**
//	 * 获取对应的list
//	 * @return
//	 */
//	public ArrayList<T> getList();
//	/**
//	 * 有输入list
//	 * @param inputList
//	 * @return
//	 */
//	public T get(T[] inputList);
//	public T get(double[] inputList);
	public ModelValue get(double[] inputList,ArrayList<String> inputStrList);
}
