package com.util;

public class test {
public static void main(String[] args) {
	String str="特2&nbsp;&nbsp;特11&nbsp;&nbsp;9&nbsp;&nbsp;17&nbsp;&nbsp;22&nbsp;&nbsp;44&nbsp;&nbsp;48&nbsp;&nbsp;59&nbsp;&nbsp;69&nbsp;&nbsp;71&nbsp;&nbsp;120&nbsp;&nbsp;301&nbsp;&nbsp;673&nbsp;&nbsp;826&nbsp;&nbsp;120&nbsp;&nbsp;夜201&nbsp;&nbsp;夜209";;
	String[] strL=str.split("(\\&nbsp;\\&nbsp;)");
	System.out.println(strL.length);
	for(String str2:strL)
	{
		System.out.println(str2);
	}
	
	String str2="http://www.fang.com/ask/Ask_StepTwo.aspx?asktitle=%BD%A8%CD%E2SOHO%D0%B4%D7%D6%C2%A5&newcode=1010087100";
	System.out.println(str2.indexOf("newcode=")+8);
	System.out.println(str2.indexOf("'"));
	System.out.println(str2.substring(str2.indexOf("newcode=")+8));
}
}
