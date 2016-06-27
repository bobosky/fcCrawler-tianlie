package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.util.JsonUtil;
import com.zj.bean.Company51JobBean;
import com.zj.bean.Company51JobCompanyBean;
import com.zj.bean.Company51JobPositionBean;
import com.zj.crawler.AntGetUrl;
import com.zj.exec.MainStatic;
import com.zj.intoDb.IntoDb;
import com.zj.queue.Company51JobDescAntQueue;

public class Company51JobDescAntParse {
	private static Logger log = Logger
			.getLogger(Company51JobDescAntParse.class);

	/**
	 * 返回是否 正确 如果为 false 则表示 到页面结束了
	 * 
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 * @return
	 */
	public static boolean runUrl(String url, Company51JobBean inputBean,Company51JobDescAntQueue queue,
			IntoDb intoDb, IntoDb intoDb2) {

		boolean flag = false;

		// 判断当前页是否有效
		// System.out.println(url);
		Pattern p_1 = Pattern.compile("orange1\"[^>]*?>([\\d]*?)</a");
		Matcher m_1 = p_1.matcher(url);
		int max = -1;
		while (m_1.find()) {
			int val = Integer.parseInt(m_1.group(1));
			if (max < val) {
				max = val;
			}
		}
		p_1 = Pattern.compile("currPage\"[^>]*?>([\\d]*?)<");
		m_1 = p_1.matcher(url);
		while (m_1.find()) {
			int val = Integer.parseInt(m_1.group(1));
			if (max < val) {
				max = val;
			}
		}

		if (max < inputBean.getCurrentPage()) {
			return false;
		}
		Company51JobCompanyBean company = new Company51JobCompanyBean();
		company.setCompanyCode(inputBean.getCompanyCode());
		company.setCompanyName(inputBean.getCompanyName());
		company.setCompanyUrl(inputBean.getCompanyUrl());
		company.setLocation(inputBean.getLocation());
		// 获取coid

		p_1 = Pattern.compile("search/codetail.php\\?coid=([\\d]*?)&");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			inputBean.setCompanyCode(Long.parseLong(m_1.group(1)));
		}
		// 解析页面获取 公司信息
		p_1 = Pattern.compile("公司行业：</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		int end = 0;
		if (m_1.find()) {
			end = m_1.end();
			String str = m_1.group(1).replaceAll("(&nbsp;&nbsp;)", ",");
			if (str.startsWith(",")) {
				str = str.substring(1);
			}
			company.setIndustryCategory(str);
			url = url.substring(end < 0 ? 0 : end);
		}
		end = 0;
		p_1 = Pattern.compile("公司性质：</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			end = m_1.end();
			company.setCompanyCategory(m_1.group(1)
					.replaceAll("(&nbsp;&nbsp;)", " ").trim());
			url = url.substring(end < 0 ? 0 : end);
		}
		end = 0;
		p_1 = Pattern.compile("公司规模：</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			end = m_1.end();
			company.setCompanyMemberNum(m_1.group(1)
					.replaceAll("(&nbsp;&nbsp;)", " ").trim());
			url = url.substring(end < 0 ? 0 : end);
		}
		end = 0;
		p_1 = Pattern
				.compile("class=\"txt_font\"[^>]*?>([\\s\\S]*?)</p>[\\s]*?<p[^>]*?class=\"pot");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			end = m_1.end();
			company.setDesc(m_1.group(1));
			url = url.substring(end < 0 ? 0 : end);
		}
		end = 0;
		p_1 = Pattern.compile(">地[^：]*?址：([^<]*?)<");
		m_1 = p_1.matcher(url);
		boolean fl1 = false;
		if (m_1.find()) {
			fl1 = true;
			end = m_1.end();
			company.setAddress(m_1.group(1));
			url = url.substring(end < 0 ? 0 : end);
		}
		if (!fl1) {
			end = 0;
			p_1 = Pattern.compile("地址:([^<]*?)<br");
			m_1 = p_1.matcher(url);
			if (m_1.find()) {
				end = m_1.end();
				company.setAddress(m_1.group(1));
				url = url.substring(end < 0 ? 0 : end);
			}
		}
		end = 0;
		p_1 = Pattern.compile("邮政编码：([\\d]*?)<");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			end = m_1.end();
			company.setPostcode(m_1.group(1));
			url = url.substring(end < 0 ? 0 : end);
		}
		end = 0;

		p_1 = Pattern
				.compile("href=\"([^\"]*?)\"[^>]*?class=\"blue\"[^>]*?>([^<]*?)</a></td><td[^>]*?>([^<]*?)</td><td[^>]*?>([^<]*?)</td><td[^>]*?>([^<]*?)</td><td[^>]*?>([^<]*?)</td>");
		m_1 = p_1.matcher(url);
		while (m_1.find()) {
			flag = true;
			Company51JobPositionBean job = new Company51JobPositionBean();
			job.setCompanyCode(inputBean.getCompanyCode());
			job.setPositonName(m_1.group(2));
			job.setPositionUrl(m_1.group(1));
			try {
				String jobcode = m_1.group(1).substring(
						m_1.group(1).indexOf("job/") + 4,
						m_1.group(1).indexOf(",c.html"));
				job.setJobCode(Long.parseLong(jobcode));
				if (MainStatic.jobCode(job.getJobCode())) {
					// 如果存在job
					continue;
				}
				MainStatic.addJobCode(job.getJobCode());
				job.setWorkAddress(m_1.group(3));
				job.setPublishDate(m_1.group(4));
				job.setTrunkDate(m_1.group(5));
				String str = m_1.group(6).replaceAll("(&nbsp;)", "").trim();
				job.setMemberCount(str);
				String val = queue.redis.hget(
						MainStatic.jobCodeMap, Long.toString(job.getJobCode()));
				if (val == null) {
					// 如果为 空则表示为新的职位

				} else if (val.equals(job.getPublishDate())) {
					// 如果时间相同则不添加
					log.info("职位:" + job.getJobCode() + "\t未发生变化");
					continue;
				}
				queue.redis.hset(MainStatic.jobCodeMap,
						Long.toString(job.getJobCode()), job.getPublishDate());
				String a = JsonUtil.getJsonStr(job);
				// 需要判断jobcode的更新时间是否发生变化
				if(inputBean.getLocation() != null)
				{//如果为新的公司则添加如redis中
					intoDb.add(a,true);
				}else{
					//否则为已经有了但是职位出现更新
					//则只写入职位的简述信息表中companyDescJob中
					intoDb.add(a,false);
				}
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				log.error("job url异常:" + m_1.group(1));
			}
		}

		// 如果为新的公司信息则 获取 否则不获取粉丝数
		if (inputBean.getLocation() != null) {
			// String
			// code2="http://fans.51job.com/payservice/fans/ajax/fans_exteral_ajax.php?jsoncallback=jsonp1417080952633&_=1417080954099&type=0&coid="+inputBean.getCompanyCode()+"&step=0";
			String code2 = "http://fans.51job.com/payservice/fans/ajax/fans_exteral_ajax.php?jsoncallback?&type=0&coid="
					+ inputBean.getCompanyCode() + "&step=0";
			// System.out.println(code2);
			while (true) {
				try {
					url = AntGetUrl.doGet(code2, "gbk");
					// System.out.println("url:"+url);
					break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			p_1 = Pattern.compile("粉丝团\\(([\\d]*?)\\)");
			m_1 = p_1.matcher(url);
			if (m_1.find()) {
				if (m_1.group(1).length() > 0)
					company.setFansCount(Integer.parseInt(m_1.group(1)));
			}
			intoDb2.add(JsonUtil.getJsonStr(company));
		} else {
			log.info("公司:" + company.getCompanyCode() + "\t未发生变化");
		}
		// System.out.println("result:"+result);

		// 获取职位信息

		return flag;

	}
}
