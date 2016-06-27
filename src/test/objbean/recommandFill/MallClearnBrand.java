package test.objbean.recommandFill;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import test.objbean.CategoryItemBean;
import test.objbean.CategoryMallBean;
import test.objbean.ExplainBean;
import test.objbean.ItemRecommandBean;
import test.objbean.MallRecommandBean;
import test.objbean.SonBean;
import test.objbean.SonSimBean;

import com.util.FileUtil2;
import com.util.JsonUtil;

/**
 * mall 推荐品牌的时候需要将mall中已经存在的品牌删除 地图中
 * 
 * @author Administrator
 *
 */
public class MallClearnBrand {

	public ArrayList<MallRecommandBean> mallRecommand = new ArrayList<MallRecommandBean>();
	/**
	 * 推荐文件
	 */
	public String recommandFile = "";
	/**
	 * 地图中 mall对应 品牌
	 */
	public HashMap<Long, HashSet<String>> mapMallAndShop = new HashMap<Long, HashSet<String>>();
	/**
	 * 剩下的map
	 */
	public HashSet<String> mapBrandExist = new HashSet<String>();
	/**
	 * 已经存在的brandName
	 */
	public HashSet<String> brandExist = new HashSet<String>();
	/**
	 * hot中无效的
	 */
	public HashSet<String> clearnHotBrand=new HashSet<String>();
	/**
	 * 最终的文件
	 */
	public String recommandFileOut = "";
	/**
	 * 空的brand的文件
	 */
	public String brandNullOut = "";

	public FileUtil2 writeRecommand = null;

	public FileUtil2 writeBrandNot = null;

	
	/**
	 * 推荐文件
	 * 
	 * @param recommandFile
	 */
	public MallClearnBrand(String recommandFile, String recommandFileOut,
			String brandNullOut) {
		this.recommandFile = recommandFile;
		this.recommandFileOut = recommandFileOut;
		this.brandNullOut = brandNullOut;
	}

	public void run() {
		writeRecommand = new FileUtil2(recommandFileOut, "utf-8");
		writeBrandNot = new FileUtil2(brandNullOut, "utf-8");
		System.out.println("读取brand");
		 readBrandName();
		// 读取地图数据
		 System.out.println("读取地图");
		readMap();
		// 读取
		System.out.println("执行推荐清洗");
		
		readClearnHot();
		readRecommand();
		// 读取

	}
	public void readClearnHot()
	{
		FileUtil2 file = new FileUtil2(System.getProperty("user.dir")
				+ "/data/brandClearnHot.txt", "utf-8", false);
		LinkedList<String> list = file.readAndClose();
		if (list == null) {
			return;
		}
		while (list.size() > 0) {
			String[] strList = list.pollFirst().split("\t");
			if(strList.length==4)
			{
				if(strList[3].equals("1"))
				{
					clearnHotBrand.add(strList[2].trim());
				}
			}
		}
		
	}

	public void readBrandName() {
		FileUtil2 file = new FileUtil2(System.getProperty("user.dir")
				+ "/data/brandName.txt", "utf-8", false);
		LinkedList<String> list = file.readAndClose();
		if (list == null) {
			return;
		}
		while (list.size() > 0) {
			// System.out.println(list.pollFirst());
			String[] strList = list.pollFirst().split("\t");
			if (strList.length == 2) {
				brandExist.add(strList[1].trim());
				mapBrandExist.remove(strList[1].trim());
			}
		}
		String[] brandOthers = new String[] {
				System.getProperty("user.dir") + "/data/brandOther10.txt",
				System.getProperty("user.dir") + "/data/brandOther20.txt",
				System.getProperty("user.dir") + "/data/brandOther30.txt",
				System.getProperty("user.dir") + "/data/brandOther50.txt",
				System.getProperty("user.dir") + "/data/brandOther55.txt",
				// System.getProperty("user.dir") + "/data/brandOther60.txt",
				System.getProperty("user.dir") + "/data/brandOther70.txt",
				System.getProperty("user.dir") + "/data/brandOther80.txt",
				System.getProperty("user.dir") + "/data/brandOther90.txt" };
		for (String str : brandOthers) {
			file = new FileUtil2(str, "utf-8", false);
			list = file.readAndClose();
			if (list == null) {
				System.out.println("空");
				return;
			}
			while (list.size() > 0) {
				// System.out.println(list.pollFirst());
				String[] strList = list.pollFirst().split("\t");
				if (strList.length == 9) {
					brandExist.add(strList[1].trim());
					mapBrandExist.remove(strList[1].trim());
				}
			}
		}
		// 没有命中的brand写入文件中
		for (String brandNa : mapBrandExist) {
			ItemRecommandBean temp = new ItemRecommandBean();
			temp.setBrandName(brandNa);
			writeBrandNot.write(JsonUtil.getJsonStr(temp));
		}
	}

	public void readRecommand() {
		//获取点评map数据

		
		FileUtil2 file = new FileUtil2(recommandFile, "utf-8", false);
		LinkedList<String> list = file.readAndClose();
		if (list == null) {
			return;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("explain", ExplainBean.class);
		map.put("similaryMall", SonSimBean.class);
		map.put("categoryItem", CategoryItemBean.class);
		map.put("recommandShop", SonBean.class);
//		for(Entry<Long,HashSet<String>> ln:mapMallAndShop.entrySet())
//		{
//			System.out.println("原始数据:"+ln.getKey());
//		}
		for (String str : list) {
			MallRecommandBean mall = (MallRecommandBean) JsonUtil
					.getDtoFromJsonObjStr(str, MallRecommandBean.class, map);
			Long mallId = mall.getMallId();
			HashSet<String> mapBrand = mapMallAndShop.get(mallId);
			if (mapBrand == null) {
				writeRecommand.write(str);
				System.out.println("不存在:" + mallId);
				// System.exit(1);
			} else {
				for (CategoryItemBean re : mall.getCategoryItem()) {
					Iterator<SonBean> iter = re.getRecommandShop().iterator();
					while (iter.hasNext()) {
						SonBean be = iter.next();
						if (mapBrand.contains(be.getName())) {
							System.out.println(mallId + ":存在:" + be.getName());
							iter.remove();
							continue;
						}
						else if(clearnHotBrand.contains(be.getName()))
						{
							System.out.println(mallId + ":清洗:" + be.getName());
							iter.remove();
						}
						else if(BrandClearnExistMall.isExistMall(be.getName()))
						{
							System.out.println(mallId + ":brand为mall:" + be.getName());
							iter.remove();
						}
					}
				}
				writeRecommand.write(JsonUtil.getJsonStr(mall));
			}
		}
	}

	public void readMap() {
		FileUtil2 file2=new FileUtil2(System.getProperty("user.dir")+"/data/dianping_map.txt","utf-8",false);
		LinkedList<String> list2=file2.readAndClose();
		HashMap<String,Long> mapRelP=new HashMap<String,Long>();
		if(list2!=null)
		{
			while(list2.size()>0)
			{
				JSONObject json = JSONObject.fromObject(list2.pollFirst());
				String name=json.getString("name");
				Long id=json.getLong("dianpingid");
				mapRelP.put(name,id);
			}
		}
		
		
		File dir = new File(System.getProperty("user.dir") + "/data/mapData");
		if (!dir.isDirectory()) {
			System.out.println("地图数据目录错误");
			System.exit(0);
		}
		File[] files = dir.listFiles();
		for (File fileTemp : files) {
			// 读取地图数据
			FileUtil2 file = new FileUtil2(fileTemp.getAbsolutePath(), "utf-8",
					false);
			String mallName=file.file.getName().split("\\.")[0];
			if(mallName.equals("北京来福士中心"))
			{
				mallName="来福士购物中心";
			}
			Long mallId=mapRelP.get(mallName);
			if(mallId==null)
			{
				System.out.println("--------------------------:空:"+mallName);
				continue;
			}
			LinkedList<String> list = file.readAndClose();
			HashSet<String> brand = new HashSet<String>();
			if (list == null) {
				return;
			}
			// int i=0;
			StringBuffer strB = new StringBuffer();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("data", DataBean.class);
			// map.put("Floors", FloorBean.class);
			// map.put("building", MallSonBean.class);
			// map.put("FuncAreas", FuncAreasBean.class);
			while (list.size() > 0) {
				// i++;
				// System.out.println(i+"+"+list.pollFirst());
				strB.append(list.pollFirst());

			}
			// MallMapBean
			// bean=(MallMapBean)JsonUtil.getDtoFromJsonObjStr(strB.toString(),MallMapBean.class,
			// map);
			// System.out.println(strB.toString());
			// System.out.println(JsonUtil.getJsonStr(bean));
			JSONObject json = JSONObject.fromObject(strB.toString());
			// System.out.println(json.get("data"));
			JSONObject json2 = JSONObject.fromObject(json.get("data"));

			JSONObject js = JSONObject.fromObject(json2.get("building"));
			//String mallName = js.getString("Name");
			
			System.out.println("添加mall:"+mallName);
			// System.out.println(json2.get("Floors"));
			JSONArray json3 = JSONArray.fromObject(json2.get("Floors"));
			for (int i = 0; i < json3.size(); i++) {
				// System.out.println(json3.get(i));
				JSONArray json4 = JSONArray.fromObject(JSONObject.fromObject(
						json3.get(i)).get("FuncAreas"));
				for (int j = 0; j < json4.size(); j++) {
					JSONObject json5 = JSONObject.fromObject(json4.get(j));
					String brandNa = json5.getString("Name");
					brand.add(brandNa);
					mapBrandExist.add(brandNa);
					// System.out.println(mallName+"\t"+json5.get("Name"));
				}
				// System.out.println();
			}
			mapMallAndShop.put(mallId, brand);
		}
		for (Entry<Long, HashSet<String>> m : mapMallAndShop.entrySet()) {
			System.out.println(m.getKey());
		}
	}

	public static void main(String[] args) {
		String dir = System.getProperty("user.dir") + "/data/";
		MallClearnBrand main = new MallClearnBrand(dir
				+ "mallAbdcontentRecommandExplainOut.txt", dir
				+ "mallAbdcontentRecommandExplainOut2.txt", dir
				+ "mallAbdcontentRecommandExplainBrand.txt");
		main.run();
	}
}
