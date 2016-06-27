package test.objbean.recommandFill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import test.objbean.CategoryMallBean;
import test.objbean.ExplainBean;
import test.objbean.ItemRecommandBean;
import test.objbean.SonBean;
import test.objbean.SonSimBean;

import com.util.FileUtil2;
import com.util.JsonUtil;

/**
 * 并且过滤掉背景品牌中存在的mall地异常数据
 * @author Administrator
 *
 */
public class BrandClearnBrand {

	
	/**
	 * 读取推荐数据
	 *  覆盖 文件
	 * @param path brand推荐的文件或者brandOther推荐的文件
	 */
	public static void readRecommandData(String path) {

		FileUtil2 fileExplain = new FileUtil2(path, "utf-8", false);
		LinkedList<String> strListExplain = fileExplain.readAndClose();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("explain", ExplainBean.class);
		map.put("similaryBrand", SonSimBean.class);
		map.put("categoryMall", CategoryMallBean.class);
		map.put("recommandMall", SonBean.class);
		//将brand中存在的brand过滤掉

		LinkedList<String> list=new LinkedList<String>();
		if (strListExplain == null)
			return;
		while (strListExplain.size() > 0) {
//			 if (strListExplain.size() < 7800) {
//			 break;
//			 }
			String str = strListExplain.pollFirst();
			System.out.println(strListExplain.size());
			// ItemRecommandBean
			// test=(ItemRecommandBean)JsonUtil.getDtoFromJsonObjStr(str,ItemRecommandBean.class,map);
			ItemRecommandBean item=(ItemRecommandBean) JsonUtil
					.getDtoFromJsonObjStr(str, ItemRecommandBean.class, map);
			//过滤其中的mall数据
			Iterator<SonSimBean> sim=null;
			try{
				sim=item.getSimilaryBrand().iterator();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(str);
				continue;
				//System.exit(1);
			}
			while(sim.hasNext())
			{
				SonSimBean be=sim.next();
				if(BrandClearnExistMall.isExistMall(be.getName()))
				{
					System.out.println("清洗brand存在mall：sim:"+be.getName());
					sim.remove();
				}
			}
			//最终写入文件中
			list.add(JsonUtil.getJsonStr(item));
		}
		FileUtil2 file22=new FileUtil2(path, "utf-8");
		file22.write(list);
	}
	
	public static void main(String[] args) {
		String file=System.getProperty("user.dir")+"/data/brandAbdcontentRecommandExplain.txt";
		file=System.getProperty("user.dir")+"/data/brandCom.txt";
		BrandClearnBrand.readRecommandData(file);
	}
}
