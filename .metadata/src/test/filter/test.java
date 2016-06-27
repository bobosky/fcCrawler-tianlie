package test.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class test {
public static void main(String[] args) {
	String str="asdf^@@^asdf";
	System.out.println(str.split("\\^@@\\^")[0]);
	int len=10000000;
	
	int[] ll=new int[len];
	Date st1=new Date();
	for(int i=0;i<len;i++)
	{
		ll[i]=i;
	}
	Date ent1=new Date();
	System.out.println((ent1.getTime()-st1.getTime())+"ms||");
	Date start1=new Date();
	int j=0;
	for(int i=0;i<len;i++)
	{
		j=ll[i]+1;
	}
	Date end1=new Date();
	System.out.println((end1.getTime()-start1.getTime())+"ms");

	LinkedList<Integer> list2=new LinkedList<Integer>();
	Date st3=new Date();
	for(int i=0;i<len;i++)
	{
		list2.addFirst(i);
	}
	Date start3=new Date();
	Date ent3=new Date();
	System.out.println((ent3.getTime()-st3.getTime())+"ms||");
	Iterator<Integer> inter=list2.listIterator();
	while(inter.hasNext())
	{
		j=inter.next()+1;
	}
	Date end3=new Date();
	System.out.println((end3.getTime()-start3.getTime())+"ms");
	
	ArrayList<Integer> list=new ArrayList<Integer>();
	Date st2=new Date();
	for(int i=0;i<len;i++)
	{
		list.add(i);
	}
	Date start2=new Date();
	Date ent2=new Date();
	System.out.println((ent2.getTime()-st2.getTime())+"ms||");
//	for(int i=0;i<len;i++)
//	{
//		j=list.get(i)+1;
//	}
	for(Integer iter:list)
	{
		j=iter+1;
	}
	Date end2=new Date();
	System.out.println((end2.getTime()-start2.getTime())+"ms");

}
}
