package test;

public class father {

	public static String get()
	{
		return name;
	}
	public static void print(String name)
	{
		System.out.println("my is "+name);
	}
	public static String name="father";
	
	public static long count=0;
	public static void print()
	{
		System.out.println("count:"+count);
	}
	public static long getCount() {
		return count;
	}
	public static void setCount(long count2) {
		count = count2;
	}
	
}
