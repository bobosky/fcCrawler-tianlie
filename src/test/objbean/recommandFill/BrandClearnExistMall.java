package test.objbean.recommandFill;
/**
 * 数据异常导致品牌中存在mall的数据
 * @author Administrator
 *
 */
public class BrandClearnExistMall {

	
	/**
	 * 判断brand中是否存在mall信息
	 * @param brandName1
	 * @return
	 */
	public static boolean isExistMall(String brandName1)
	{
		String brandName=brandName1.trim();
		boolean flag=true;
		if(brandName.endsWith("购物中心"))
		{}else if(brandName.contains("商厦"))
		{}else if(brandName.endsWith("商场"))
		{}else if(brandName.endsWith("商城"))
		{}else if(brandName.endsWith("购物广场"))
		{}else if(brandName.endsWith("大厦"))
		{}else if(brandName.endsWith("嘉里中心"))
		{}else if(brandName.endsWith("广场"))
		{}else if(brandName.endsWith("百货"))
		{}else if(brandName.endsWith("奥特莱斯"))
		{}else if(brandName.endsWith("三里屯VILLAGE南区"))
		{}else if(brandName.endsWith("三里屯Village北区"))
		{}else if(brandName.contains("凯德MALL"))
		{}else if(brandName.endsWith("绿地缤纷城"))
		{}else if(brandName.endsWith("新一城"))
		{}else if(brandName.endsWith("搜秀城"))
		{}else if(brandName.endsWith("SOHO"))
		{}else if(brandName.endsWith("大悦城"))
		{}else if(brandName.endsWith("市场"))
		{}else if(brandName.endsWith("颐堤港"))
		{}else if(brandName.toUpperCase().endsWith("王府井IN88"))
		{}else if(brandName.endsWith("三里屯太古里"))
		{}else if(brandName.endsWith("天阶"))
		{}else if(brandName.endsWith("银泰中心"))
		{}else if(brandName.endsWith("五彩城"))
		{}else if(brandName.endsWith("世茂广场"))
		{}else if(brandName.endsWith("国际商区"))
		{}else{
			flag=false;
		}
		return flag;
	}
}
