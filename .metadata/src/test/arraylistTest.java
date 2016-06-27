package test;
  
import java.util.ArrayList;  
import java.util.Iterator;  
import java.util.LinkedList;  
  
public class arraylistTest {  
  
    public static void main(String[] args) {  
        ArrayList<String> alist = new ArrayList<String>();  
        LinkedList<String> llist = new LinkedList<String>();  
          int len=1000000;
        /* 
         * 普通的add方法比较 
         * */  
        long abegin = System.currentTimeMillis();  
        for (int i = 0; i < len; i++) {  
            alist.add(String.valueOf(i));  
        }  
        long aend = System.currentTimeMillis();  
        System.out.println("arraylist time:" + (aend - abegin));  
  
        long lbegin = System.currentTimeMillis();  
        for (int i = 0; i < len; i++) {  
            llist.addLast(String.valueOf(i));  
        }  
        long lend = System.currentTimeMillis();  
        System.out.println("linkedlist time:" + (lend - lbegin));  
  
          
        /* 
         * 普通的get方法遍历比较 
         * */  
        long agbegin = System.currentTimeMillis();  
        for (int i = 0; i < len; i++) {  
            alist.get(i);  
        }  
        long agend = System.currentTimeMillis();  
        System.out.println("arraylist time get:" + (agend - agbegin));  
  
//        long lgbegin = System.currentTimeMillis();  
//        for (int i = 0; i < len; i++) {  
//            llist.get(i);  
//        }  
//        long lgend = System.currentTimeMillis();  
//        System.out.println("linkedlist time get:" + (lgend - lgbegin));  
  
          
        /* 
         *iterator方法遍历比较 
         * */  
        long lgibegin = System.currentTimeMillis();  
        for (Iterator iterator = llist.iterator(); iterator.hasNext();) {  
             iterator.next();  
        }  
        long lgiend = System.currentTimeMillis();  
        System.out.println("arraylist time get iterator:" + (lgiend - lgibegin));  
          
          
        long agibegin = System.currentTimeMillis();  
        for (Iterator iterator = alist.iterator(); iterator.hasNext();) {  
             iterator.next();  
        }  
        long agiend = System.currentTimeMillis();  
        System.out.println("linkedlist time get iterator:" + (agiend - agibegin));  
    }  
}  