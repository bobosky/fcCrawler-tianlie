package com.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import test.objbean.CategoryItemBean;
import test.objbean.ExplainBean;
import test.objbean.MallRecommandBean;
import test.objbean.SonBean;
import test.objbean.SonSimBean;

import com.util.FileUtil2;
import com.util.JsonUtil;

public class HotClearn {

	
	public static void main(String[] args) {
		FileUtil2 fileExplain=new FileUtil2(System.getProperty("user.dir")+"/data/temp/mallAbdcontentRecommandExplainOut.txt","utf-8",false);
		LinkedList<String> strListExplain=fileExplain.readAndClose();
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("explain",ExplainBean.class);
		ArrayList<MallRecommandBean> recommandListMall=null;
		if(true)
		{
			map.put("similaryMall",SonSimBean.class);
			map.put("categoryItem",CategoryItemBean.class);
			map.put("recommandShop",SonBean.class);
			recommandListMall=new ArrayList<MallRecommandBean>();
			HashSet<String> uniBrandName=new HashSet<String>();
			if(strListExplain!=null)
			while(strListExplain.size()>0)
			{
				String str=strListExplain.pollFirst();
				//recommandListMall.add((MallRecommandBean)JsonUtil.getDtoFromJsonObjStr(str,MallRecommandBean.class,map));
				MallRecommandBean bean=(MallRecommandBean)JsonUtil.getDtoFromJsonObjStr(str,MallRecommandBean.class,map);
//				System.out.println(str);
				for(CategoryItemBean  be:bean.getCategoryItem())
				{
					String category=be.getCategoryName();
					for(SonBean sonBean:be.getRecommandShop())
					{
						String name=sonBean.getName();
						for(ExplainBean exBean:sonBean.getExplain())
						{
							if(exBean.getType()==1)
							{
								String temp=exBean.getText().split(":")[0]+name;
								if(uniBrandName.contains(temp))
								{
									
								}else{
									uniBrandName.add(temp);
										System.out.println(exBean.getText().split(":")[0]+"\t"+category+"\t"+name);
								}	
							}
						}
					}
				}
			}
		}
	}
}
