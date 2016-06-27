package com.zj.etlFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.logging.log4j.core.config.Order;

import com.db.MongoDb;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;


/**
 * 搜房 对一个做 价格和租金 价格做判断 是否有效
 * @author Administrator
 *
 */
public class FangFilter {

	MongoDb mongo = new MongoDb("192.168.1.4", 27017, "demo");

	public HashMap<String, HashSet<Building>> maplist = new HashMap<String, HashSet<Building>>();
	public double raide = 0d;

	/**
	 * 
	 * @param raide
	 *            半径
	 */
	public FangFilter(double raide) {
		this.raide = raide;
	}

	/**
	 * 读取数据
	 */
	public void readData() {
		// 获取fang的所有历史数据
		BasicDBObject objTemp = new BasicDBObject();
		BasicDBObject ref = new BasicDBObject();
		ref.put("fangCode", true);
		ref.put("year", true);
		ref.put("month", true);
		ref.put("priceValue", true);
		ref.put("location", true);
		ref.put("hireValue", true);
		ref.put("fangListc.city", true);
		ref.put("currentMonthSalePrice", true);
		ref.put("priceTrendValue", true);
		ref.put("currentMonthHirePriceEtl", true);
		// ref.put("hireTrendValueEtl",true);
		// 获取搜房的数据
		DBCursor cursor = mongo.find("fang", objTemp, ref);
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			// System.out.println(obj.toString());
			Location location = new Location(
					(BasicDBObject) obj.get("location"));
			String city = ((BasicDBObject) obj.get("fangListc"))
					.getString("city");
			Long fangCode = obj.getLong("fangCode");
			int year = obj.getInt("year");
			int month = obj.getInt("month");
			// KeyValue value=new KeyValue();
			// value.year=obj.getInt("year");
			// value.month=obj.getInt("month");
			// value.value=obj.getDouble("priceValue");
			// 获取搜房的历史数据
			BasicDBObject objTemp2 = new BasicDBObject();
			objTemp2.put("fangCode", fangCode);
			DBCursor cursor2 = mongo.find("fangUpdate", objTemp2, ref);
			HashSet<Building> cityMap = maplist.get(city);
			if (cityMap == null) {
				cityMap = new HashSet<Building>();
				maplist.put(city, cityMap);
			}
			Building build = new Building(fangCode, location, null, null);
			 ArrayList<BasicDBObject> array = (ArrayList<BasicDBObject>) obj
			 .get("priceTrendValue");
			 if (array != null) {
			 for (BasicDBObject ar : array) {
			 //
			 System.out.println(ar.getString("month")+"\t"+ar.getInt("money"));
			 String[] strList = ar.getString("month").split("-");
			 KeyValue value2 = new KeyValue();
			 value2.year = Integer.parseInt(strList[0]);
			 value2.month = Integer
			 .parseInt(strList[1].startsWith("0") ? strList[1]
			 .replace("0", "") : strList[1]);
			 value2.value = ar.getInt("money");
			 if (Double.compare(value2.value, 0D) == 0) {
			 // System.out.println("为空");
			 continue;
			 }
			 build.value.add(value2);
			 }
			 }

//			String sale = obj.getString("currentMonthSalePrice");
//			if (sale != null && !sale.equals("") && !sale.contains("暂无资料")) {
//				System.out.println(sale);
//				KeyValue value2 = new KeyValue();
//				value2.value = Double.parseDouble(sale);
//				value2.year = year;
//				value2.month = month;
//				build.value.add(value2);
//			} else {
//				KeyValue value2 = new KeyValue();
//				value2.value = 0;
//				value2.year = year;
//				value2.month = month;
//				build.value.add(value2);
//			}
			// System.out.println( obj.get("hireTrendValue"));
			// array = (ArrayList<BasicDBObject>) obj.get("hireTrendValueEtl");
			// if (array != null) {
			// for (BasicDBObject ar : array) {
			// //
			// System.out.println(ar.getString("month")+"\t"+ar.getInt("money"));
			// String[] strList = ar.getString("month").split("-");
			// KeyValue value2 = new KeyValue();
			// value2.year = Integer.parseInt(strList[0]);
			// value2.month = Integer
			// .parseInt(strList[1].startsWith("0") ? strList[1]
			// .replace("0", "") : strList[1]);
			// value2.value = ar.getInt("money");
			// if (Double.compare(value2.value, 0D) == 0) {
			// // System.out.println("为空");
			// continue;
			// }
			// build.hire.add(value2);
			// }
			// }
			String hire = obj.getString("currentMonthHirePriceEtl");
			if (hire != null && !hire.equals("")) {
				System.out.println(hire);
				KeyValue value2 = new KeyValue();
				value2.year = year;
				value2.month = month;
				value2.value = Double.parseDouble(hire);
				build.hire.add(value2);
			} else {
				System.out.println(hire);
				KeyValue value2 = new KeyValue();
				value2.year = year;
				value2.month = month;
				value2.value = 0;
				build.hire.add(value2);
			}
			// while (cursor2.hasNext()) {
			// BasicDBObject obj2 = (BasicDBObject) cursor2.next();
			// // System.out.println(obj2.toString());
			// KeyValue value2 = new KeyValue();
			// value2.year = obj2.getInt("year");
			// value2.month = obj2.getInt("month");
			// value2.value = obj2.getDouble("priceValue");
			// if (Double.compare(value2.value, 0D) == 0) {
			// // System.out.println("为空");
			// continue;
			// }
			// build.value.add(value2);
			// value2 = new KeyValue();
			// value2.year = obj2.getInt("year");
			// value2.month = obj2.getInt("month");
			// value2.value = obj2.getDouble("hireValue");
			// if (Double.compare(value2.value, 0D) == 0) {
			// // System.out.println("为空");
			// continue;
			// }
			// build.hire.add(value2);
			// }
			if (build.value.size() <= 0) {
				continue;
			}
			build.initSequence();
			cityMap.add(build);

			// if(Double.compare(value.value,0D)==0)
			// {
			// System.out.println("为空");
			// continue;
			// }
			// Building build=new Building(fangCode,location,value);
			// HashSet<Building> cityMap=maplist.get(city);
			// if(cityMap==null)
			// {
			// cityMap=new HashSet<Building>();
			// maplist.put(city, cityMap);
			// }
			// cityMap.add(build);
		}
	}

	public void println() {

	}

	public class Building implements Comparable<Building> {
		public Location location = null;
		public long fangCode = 0L;
		public HashSet<KeyValue> value = new HashSet<KeyValue>();

		public ArrayList<KeyValue> list = null;

		public HashSet<KeyValue> hire = new HashSet<KeyValue>();

		public ArrayList<KeyValue> listHire = null;

		public Building(long fangCode, Location location, KeyValue value,
				KeyValue valueHire) {
			this.fangCode = fangCode;
			this.location = location;
			if (value == null) {
			} else {
				this.value.add(value);
			}
			if (valueHire == null) {
			} else {
				this.hire.add(valueHire);
			}
		}

		/**
		 * 初始化序列
		 */
		public void initSequence() {
			list = new ArrayList<KeyValue>();
			list.addAll(value);
			value = null;
			Collections.sort(list);
			listHire = new ArrayList<KeyValue>();
			listHire.addAll(hire);

		}

		@Override
		public int compareTo(Building o) {
			// TODO Auto-generated method stub
			int val = Long.compare(fangCode, o.fangCode);
			if (val == 0) {
				// 将数据添加到内部

			}
			return val;
		}

		@Override
		public boolean equals(Object obj) {
			Building bulid = (Building) obj;
			if (fangCode == bulid.fangCode) {
				return true;
			}
			return false;
		};

		@Override
		public int hashCode() {
			return (int) fangCode % Integer.MAX_VALUE;
		};

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("[fangCode:").append(fangCode).append(",")
					.append(location.toString()).append("\n")
					.append(",valueLog:").append(list.toString()).append("\n")
					.append(",hireLog:").append(listHire.toString())
					.append("]");
			return sb.toString();
		}

		public String toStringOne() {
			StringBuffer sb = new StringBuffer();
			sb.append("[fangCode:").append(fangCode).append(",")
					.append(location.toString()).append(",valueLog:")
					.append(list.get(0).toString()).append(",hireLog:");
			if (listHire.size() == 0) {
				sb.append("null");
			} else {
				sb.append(listHire.get(0).toString());
			}
			sb.append("]");
			return sb.toString();
		}
	}

	public class KeyValue implements Comparable<KeyValue> {
		public int year = 0;
		public int month = 0;
		public double value = 0d;

		@Override
		public int compareTo(KeyValue o) {
			// TODO Auto-generated method stub
			if (this.year < o.year) {
				return -1;
			} else if (this.year > o.year) {
				return 1;
			} else {
				if (this.month < o.month) {
					return -1;
				} else if (this.month > o.month) {
					return 1;
				} else {
					// 如果相同则覆盖
					return 0;
				}
			}
		}

		@Override
		public boolean equals(Object obj) {
			KeyValue bulid = (KeyValue) obj;
			if (year == bulid.year && month == bulid.month) {
				return true;
			}
			return false;
		};

		@Override
		public int hashCode() {
			// System.out.println(year*100+month);
			return year * 100 + month;
		};

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("[year:").append(year).append(",month:").append(month)
					.append(",value:").append(value).append("]");
			return sb.toString();
		}

	}

	public class Location {
		public double lng = 0d;
		public double lat = 0d;

		public Location(BasicDBObject obj) {
			lat = obj.getDouble("lat");
			lng = obj.getDouble("lng");
		}

		@Override
		public String toString() {
			return "location:{lng:" + lng + ",lat:" + lat + "}";
		}
	}

	/**
	 * 打印方法
	 */
	public void print() {
		for (Entry<String, HashSet<Building>> map : maplist.entrySet()) {
			System.out.println("city:" + map.getKey());
			for (Building b : map.getValue()) {
				System.out.println("\t" + b.toString());
			}
		}
	}

	public void compute() {
		for (Entry<String, HashSet<Building>> map : maplist.entrySet()) {
			System.out.println("city:" + map.getKey());
		}
	}

	public class ValueMap implements Comparable<ValueMap> {
		public double value = 0d;
		public double source = 0d;
		public String tag = null;

		public ValueMap(double source, double value, String tag) {
			this.value = value;
			this.source = source;
			this.tag = tag;
		}

		@Override
		public int compareTo(ValueMap o) {
			// TODO Auto-generated method stub
			return -Double.compare(value, o.value);
		}

		@Override
		public String toString() {
			return "[source:" + source + ",value:" + value + ",tag:" + tag
					+ "]";
		}
	}

	public class Statistic {
		public ArrayList<ValueMap> data = new ArrayList<ValueMap>(100000);

		public Statistic(HashSet<Building> fang) {
			for (Building build : fang) {
				for (KeyValue key : build.list) {
					ValueMap temp = new FangFilter.ValueMap(key.value,
							key.value, build.fangCode + "_" + key.year + "_"
									+ key.month);
					add(temp);
				}
			}
		}

		public Statistic(ArrayList<ValueMap> fang) {
			data = fang;
		}

		public void add(ValueMap value) {
			data.add(value);
		}

		public double getMean() {
			double va = 0d;
			for (ValueMap v : data) {
				va += v.value;
			}
			return va / data.size();
		}

		public double getMeanSource() {
			double va = 0d;
			for (ValueMap v : data) {
				va += v.source;
			}
			return va / data.size();
		}

		public double getStd(double mean) {
			double va = 0d;
			for (ValueMap v : data) {
				va += (v.value - mean) * (v.value - mean);
			}
			if (data.size() <= 1) {
				return Math.sqrt(va);
			} else {
				return Math.sqrt(va / (data.size() - 1));
			}
		}

		public double getStdSource(double mean) {
			double va = 0d;
			for (ValueMap v : data) {
				va += (v.source - mean) * (v.source - mean);
			}
			if (data.size() <= 1) {
				return Math.sqrt(va);
			} else {
				return Math.sqrt(va / (data.size() - 1));
			}
		}

		public ArrayList<ValueMap> getStdListValue(double mean, double std) {
			ArrayList<ValueMap> stdList = new ArrayList<ValueMap>(10000);
			for (ValueMap v : data) {
				double value = (v.value - mean);
				ValueMap vm = new ValueMap(v.source, value, v.tag);
				stdList.add(vm);
			}
			Collections.sort(stdList);
			return stdList;
		}

		public ArrayList<ValueMap> getSingleValue(ArrayList<ValueMap> stdList,
				double rate, double std, double stdRate) {
			ArrayList<ValueMap> list = new ArrayList<ValueMap>(10000);
			double stdTrans = std * stdRate;
			int index = (int) (stdList.size() * rate);
			for (int i = 0; i < index; i++) {
				ValueMap van = stdList.get(i);
				double value = Math.abs(van.value);
				if (value > stdTrans) {
					list.add(van);
				}
			}
			for (int i = 0; i < index; i++) {
				ValueMap van = stdList.get(stdList.size() - 1 - i);
				double value = Math.abs(van.value);
				if (value > stdTrans) {
					list.add(van);
				}
			}
			return list;
		}
	}

	public class Density {
		public ArrayList<Building> set = null;
		public double rate = 0d;
		public double stdRate = 0d;
		public boolean isPrice = true;

		public Density(HashSet<Building> fang, double rate, double stdRate,
				boolean isPrice) {
			set = new ArrayList<Building>();
			set.addAll(fang);
			this.rate = rate;
			this.stdRate = stdRate;
			this.isPrice = isPrice;
		}

		/**
		 * 具体的执行
		 */
		public void run() {
			int count = 0;
			for (int i = 0; i < set.size(); i++) {
				ArrayList<ValueMap> nearSet = getNearlySet(i);
				 //System.out.println("数量:"+nearSet.size());
				boolean flag = true;
				if (isPrice) {
					//System.out.println(set.get(i).list.get(0).value);
					if (set.get(i).list.size() == 0) {
						flag = false;
						System.out.println("null:price:"
								+ set.get(i).toString());
					} else if (set.get(i).list.get(0).value > 200000
							|| set.get(i).list.get(0).value < 1000) {
						count++;
						flag = false;
						System.out.println("max:" + set.get(i).toString());
					} else if (isSingleValue(i, nearSet)) {
						count++;
						flag = false;
						System.out.println("sim:" + set.get(i).toStringOne()
								+ "\t" + getNearSetValue(nearSet));
					}
					BasicDBObject doc = new BasicDBObject();
					doc.put("fangCode", set.get(i).fangCode);
					BasicDBObject doc2 = new BasicDBObject();
					BasicDBObject status = new BasicDBObject();
					status.put("priceEtlStatus", flag);
					doc2.put("$set", status);
					mongo.update("fang", doc, doc2);
				} else {
					//System.out.println(set.get(i).listHire.get(0).value);
					if (set.get(i).listHire.size() == 0) {
						flag = false;
						System.out.println("null:hire:" + set.get(i).fangCode);
					} else if (set.get(i).listHire.get(0).value > 200
							|| set.get(i).listHire.get(0).value < 1) {
						count++;
						flag = false;
						System.out.println("max:" + set.get(i).toStringOne());
					} else if (isSingleValue(i, nearSet)) {
						count++;
						flag = false;
						System.out.println("sim:" + set.get(i).toStringOne()
								+ "\n" + getNearSetValue(nearSet));
					}
					BasicDBObject doc = new BasicDBObject();
					doc.put("fangCode", set.get(i).fangCode);
					BasicDBObject doc2 = new BasicDBObject();
					BasicDBObject status = new BasicDBObject();
					status.put("hireEtlStatus", flag);
					doc2.put("$set", status);
					mongo.update("fang", doc, doc2);
				}
			}
			System.out.println("size:" + set.size() + "\tsingelSize:" + count);
		}

		public String getNearSetValue(ArrayList<ValueMap> nearSet) {
			StringBuffer sb = new StringBuffer();
			for (ValueMap val : nearSet) {
				sb.append(val.source).append(":,");
			}
			return sb.toString();
		}

		/**
		 * 获取周边的building
		 * 
		 * @param index
		 */
		public ArrayList<ValueMap> getNearlySet(int index) {
			Building build = set.get(index);
			ArrayList<ValueMap> result = new ArrayList<ValueMap>();
			for (int i = 0; i < set.size(); i++) {
				if (i == index) {
					continue;
				}
				Building build2 = set.get(i);
				double val = getSimi(build, set.get(i));
				if (Double.compare(val, 0D) == 0) {
					continue;
				}
				if (isPrice) {
					if (build2.list.size() == 0) {
					} else {
						if (build2.list.get(0).value >= 1E-10) {
							result.add(new ValueMap(build2.list.get(0).value, val,
									Long.toString(set.get(i).fangCode)));
						}
					}
				} else {
					if (build2.listHire.size() == 0) {
					} else {
						if (build2.listHire.get(0).value >= 1E-10) {
							result.add(new ValueMap(
									build2.listHire.get(0).value, val, Long
											.toString(set.get(i).fangCode)));
						}
					}
				}
			}
			return result;
		}

		/**
		 * 判断是否独立
		 * 
		 * @param index
		 * @param nearly
		 * @return
		 */
		public boolean isSingleValue(int index, ArrayList<ValueMap> nearly) {
			if (nearly.size() < 10) {
				return false;
			}
			Statistic statistic = new Statistic(nearly);
			Building build = set.get(index);
			double val = 0d;
			if (isPrice) {
				val = build.list.get(0).value;
			} else {
				val = build.listHire.get(0).value;
			}
			double mean = statistic.getMeanSource();
			double std = statistic.getStdSource(mean);
			double ms = Math.abs(val - mean);
			if (ms > std * stdRate) {
				return true;
			}
			return false;
		}

		/**
		 * 获取比率
		 * 
		 * @param build
		 * @param build2
		 * @return
		 */
		public double getSimi(Building build, Building build2) {
			// System.out.println((build.location.lng-build2.location.lng)+"\t"+(build.location.lat-build2.location.lat));
			double dist = (build.location.lng - build2.location.lng)
					* (build.location.lng - build2.location.lng);
			dist += (build.location.lat - build2.location.lat)
					* (build.location.lat - build2.location.lat);
			dist = Math.sqrt(dist);
			// System.out.println(dist);
			if (dist > rate) {
				return 0;
			} else {
				return (rate - dist) / rate;
			}

		}
	}
	public void runAll()
	{
		readData();
		// filter.print();
		// 通过 std获取奇异值
		for (Entry<String, HashSet<Building>> map : maplist.entrySet()) {
			System.out.println("城市:" + map.getKey());
			Statistic statistic =new Statistic(map.getValue());
			double mean = statistic.getMean();
			double std = statistic.getStd(mean);
			ArrayList<ValueMap> stdList = statistic.getStdListValue(mean, std);
			ArrayList<ValueMap> notValue = statistic.getSingleValue(stdList,
					0.1, std, 2);
			System.out.println("size:" + stdList.size() + "\tmean:" + mean
					+ "\tstd:" + std);
			System.out.println(notValue.toString());
		}
		// 计算的 价格
		// 通过 密度获取奇异值
		double rate = 0.03;
		double stdRate = 4;
		for (Entry<String, HashSet<Building>> map : maplist.entrySet()) {
			System.out.println("房价:城市:" + map.getKey());
			Density density = new Density(map.getValue(), rate, stdRate,
					true);
			density.run();
		}

		// 计算的租金
		// 计算的 价格
		// 通过 密度获取奇异值
		rate = 0.03;
		stdRate = 4;
		for (Entry<String, HashSet<Building>> map :maplist.entrySet()) {
			System.out.println("租金:城市:" + map.getKey());
			Density density =new Density(map.getValue(), rate, stdRate,
					false);
			density.run();
		}
	}

	public static void main(String[] args) {
		FangFilter filter=new FangFilter(1D);
		filter.runAll();
	}
}
