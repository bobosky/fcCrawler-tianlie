package test;

public class son extends father{

	public static String name="son";
	
	public static String get()
	{
		return name;
	}
	
	public static String getFather()
	{
		return "";
		//return super.name;
	}
	
	public static void pri()
	{
		print(get());
	}
	public static void main(String[] args) {
		son so=new son();
//		System.out.println(so.get());
//		so.print(so.get());
//		so.print(so.getFather());
//		so.pri();
		so.print();
		son so2=new son();
		so.setCount(2L);
		so.print();
		so2.print();
	}
}
