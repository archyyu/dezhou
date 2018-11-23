package com.yl.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yl.Global.ConstList;
import com.yl.vo.PukeType;

/**
 * 2010年4月29日0:03:16
 * 
 * @author chch
 * 
 */
public class Utils
{
	/**
	 * 从小到大排序返回一字符串
	 */
	public static String sortArrayToStr(int[] a)
	{
		String str = "";
		for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				int temp = -1;
				if (a[i] > a[j])
				{
					temp = a[i];
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
		for (int i = 0; i < a.length; i++)
		{
			str += a[i];
		}
		return str;
	}

	/**
	 * 从小到大排序返回一数组
	 */
	public static int[] sortArray(int[] a)
	{
		for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				int temp = -1;
				if (a[i] > a[j])
				{
					temp = a[i];
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
		return a;
	}

	/**
	 * 从大到小排序返回一数组
	 */
	public static int[] sortBigToSmall(int[] a)
	{
		for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				int temp = -1;
				if (a[i] < a[j])
				{
					temp = a[i];
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
		return a;
	}

	/**
	 * 由数组组成字符串 10 11 12 13 14分别用a,b,c,d,e替换
	 */

	public static String arrayToStr(int[] a)
	{
		String str = "";
		String s = "";
		for (int i = 0; i < a.length; i++)
		{
			if (a[i] == 10)
			{
				s = "a";
			}
			else if (a[i] == 11)
			{
				s = "b";
			}
			else if (a[i] == 12)
			{
				s = "c";
			}
			else if (a[i] == 13)
			{
				s = "d";
			}
			else if (a[i] == 14)
			{
				s = "e";
			}
			else
			{
				s = String.valueOf(a[i]);
			}
			str += s;
		}
		return str;
	}

	/**
	 * 取出数组中与最大数相等的数组下标
	 */
	public static HashMap getArrayAddr(int[] a)
	{
		HashMap map = new HashMap();
		int n = 0;
		int[] b = new int[a.length];
		int level = -1;
		for (int i = a.length - 1; i >= 0; i--)
		{
			if (a[a.length - 1] == a[i])
			{
				b[n] = a[i];
				n++;
			}
		}
		map.put("level", new Integer(a[a.length - 1]));
		map.put("barray", b);
		return map;
	}

	/**
	 * 取出数组中的最大值 以及与最大值相等的个数
	 */
	public static Map<String,Integer> getMaxAndNum(int[] a)
	{
		a = sortArray(a);
		Map<String,Integer> map = new HashMap<String,Integer>();
		int n = 0;
		for (int i = a.length - 1; i >= 0; i--)
		{
			if (a[a.length - 1] == a[i])
			{
				n++;
			}
		}
		map.put("level", a[a.length - 1]);
		map.put("num", n);
		return map;
	}

	/**
	 * 得到需要比较的第二大数
	 */
	public static int[] getSecondMax(HashMap<Integer, PukeType> cmap, int max,
			int[] second)
	{
		int k = 0;
		Iterator itor = cmap.keySet().iterator();
		while (itor.hasNext())
		{
			int key = Integer.parseInt(itor.next().toString());
			PukeType pt = (PukeType) cmap.get(key);
			if (pt.getMaxnum() == max)
			{
				second[k] = pt.getSecond();
				k++;
			}
		}
		return second;
	}

	/**
	 * 得到需要比较的第三大数
	 */
	public static int[] getThirdMax(HashMap<Integer, PukeType> cmap,
			int maxOne, int maxSecond, int[] third)
	{
		int k = 0;
		Iterator itor = cmap.keySet().iterator();
		while (itor.hasNext())
		{
			int key = Integer.parseInt(itor.next().toString());
			PukeType pt = (PukeType) cmap.get(key);
			if (pt.getMaxnum() == maxOne && pt.getSecond() == maxSecond)
			{
				third[k] = pt.getThird();
				k++;
			}
		}
		return third;
	}

	/**
	 * 选出数组某一个位置右侧的三位，如果越界，从a[0]开始
	 */
	public static int[] selectNum(int sid, String str)
	{
		int[] a = new int[3];
		int i = str.indexOf(String.valueOf(sid));
		if (str.length() - i >= 4)
		{
			for (int j = 0; j < 3; j++)
			{
				// a[j] = str.charAt(i+j+1);
				a[j] = Integer.parseInt(str.substring(i + j + 1, i + j + 2)
						.toString());
			}
		}
		else
		{
			for (int j = 0; j < 3 - (str.length() - i - 1); j++)
			{
				for (int k = 0; k < str.length() - i - 1; k++)
				{
					a[k] = Integer.parseInt(str.substring(i + k + 1, i + k + 2)
							.toString());
				}
				if (str.length() > 2)
				{
					a[str.length() - i - 1 + j] = Integer.parseInt(str
							.substring(j, j + 1).toString());
				}
				if (str.length() == 2)
				{
					a[0] = Integer.parseInt(str.substring(0, 1).toString());
					a[1] = Integer.parseInt(str.substring(1).toString());
					a[2] = Integer.parseInt(str.substring(0, 1).toString());
				}

			}
		}
		return a;
	}

	/**
	 * 字符串比较大小，返回最大的字符串
	 */
	public static String compareStrRetBigA(String[] s)
	{
		String str = "";
		List list = new ArrayList();
		for (int i = 0; i < s.length; i++)
		{
			list.add(s[i]);
		}
		Collections.sort(list);
		Collections.reverse(list);
		str = list.get(0).toString();
		str = str.substring(str.indexOf("@") + 1);
		return str;
	}

	/**
	 * 字符串比较大小，返回最大的字符串
	 */
	public static String[] compareStrRetBigB(String[] s)
	{
		String str = "";
		List list = new ArrayList();
		List Cmplist = new ArrayList();
		for (int i = 0; i < s.length; i++)
		{
			list.add(s[i]);
		}

		Collections.sort(list);
		Collections.reverse(list);
		str = list.get(0).toString();

		String CmpStr = ((String) list.get(0)).substring(0,
				str.indexOf("@") + 1);
		for (int i = 0; i < s.length; i++)
		{
			if (s[i].indexOf(CmpStr) > -1)
			{
				Cmplist.add(s[i].substring(s[i].indexOf("@") + 1,
						s[i].indexOf("@") + 2));
			}
		}
		String[] winArray = new String[Cmplist.size()];
		for (int i = 0; i < Cmplist.size(); i++)
		{
			winArray[i] = Cmplist.get(i).toString();
		}
		return winArray;
	}

	/**
	 * 返回数组中相等的值
	 * 
	 * @param a
	 * @return
	 */
	public static int retNum(int[] a)
	{
		int num = 0;
		for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				int temp = -1;
				if (a[i] == a[j])
				{
					num = a[i];
					break;
				}
			}
		}
		return num;
	}

	public static boolean isNumeric(String str)
	{
		for (int i = str.length(); --i >= 0;)
		{
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 返回葫芦和一对的num 返回四条和单牌的num
	 * 
	 * @param a
	 * @return
	 */
	public static int[] retNumForhulu(int[] a)
	{
		int[] fts = new int[2];
		HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
		HashMap t = new HashMap();
		a = sortBigToSmall(a);
		int f = 0, g = 0;

		A: for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				if (a[i] == a[j])
				{
					g++;
					if (g == 4)
					{
						fts[0] = a[2];
						break A;
					}
				}
			}
		}
		for (int i = 0; i < a.length; i++)
		{
			if (a[i] != fts[0])
			{
				fts[1] = a[i];
			}
		}
		return fts;

	}

	/**
	 * 返回三条和两个单牌的num
	 * 
	 * @param a
	 * @return
	 */
	public static int[] retNumForthree(int[] a)
	{
		int[] fts = new int[3];
		HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
		HashMap t = new HashMap();
		a = sortBigToSmall(a);
		int f = 0, g = 0;

		A: for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				if (a[i] == a[j])
				{
					g++;
					if (g == 3)
					{
						fts[0] = a[i];
						break A;
					}
				}
			}
		}
		f = 1;
		for (int i = 0; i < a.length; i++)
		{
			if (a[i] != fts[0])
			{
				fts[f] = a[i];
				f++;
			}
		}
		if (fts[1] < fts[2])
		{
			int tmp = fts[3];
			fts[3] = fts[2];
			fts[2] = tmp;
		}

		return fts;

	}

	/**
	 * 返回两对牌，以及一单张的num值
	 * 
	 * @param a
	 * @return
	 */
	public static int[] retNumForTwo(int[] a)
	{
		int[] fts = new int[3];
		HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
		HashMap t = new HashMap();
		a = sortBigToSmall(a);
		int f = 0;
		for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				if (a[i] == a[j])
				{
					m.put(i, a[i]);
					fts[f] = a[i];
					f++;
					t.put(i, i);
					t.put(j, j);
				}
			}
		}
		for (int i = 0; i < a.length; i++)
		{
			if (!t.containsKey(i))
			{
				fts[2] = a[i];
			}
		}
		return fts;

	}

	/**
	 * 将数组中相等的值以及“0”去掉，返回从小到大排序的数组
	 * 
	 * @param args
	 */
	public static List delEqualNum(int[] a)
	{

		Set s = new HashSet();
		List l = new ArrayList();
		for (int i = 0; i < a.length; i++)
		{
			if (a[i] == 0)
			{
				continue;
			}
			s.add(new Integer(a[i]));
		}
		Iterator it = s.iterator();
		while (it.hasNext())
		{
			int j = (Integer) it.next();
			l.add(new Integer(j));
		}

		Collections.sort(l);
		return l;

	}

	/**
	 * 将数组中a[n]-a[n-1]的值保存在数组中
	 * 
	 * @param list
	 */
	public static int[] sidePool(List list)
	{
		int[] b = new int[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			if (i == 0)
			{
				b[i] = (Integer) list.get(i);
				continue;
			}
			b[i] = (Integer) list.get(i) - (Integer) list.get(i - 1);

		}
		return b;
	}

	/**
	 * 获取系统的当前时间
	 * 
	 * @param args
	 */
	public static String getCurrentTime()
	{
		return String.valueOf(Utils.getDateToLong());
	}

	/**
	 * 切割字符串
	 * 
	 * @param args
	 */
	public static List splitStrToArray(String str, String c)
	{
		StringTokenizer st = new StringTokenizer(str, c);
		List t = new ArrayList();
		while (st.hasMoreElements())
		{
			t.add(st.nextElement().toString());
		}
		return t;
	}

	/**
	 * 获取三个随机数
	 * 
	 * @param amt
	 * @return
	 */
	public static String getThreeRadomNum()
	{
		Random ran = new Random();
		String str = "";
		for (int i = 0; i < 3; i++)
		{
			str += String.valueOf(ran.nextInt(10));
		}
		return str;

	}

	/**
	 * 根据给定的净胜场次，获得经验值
	 * 
	 * @param args
	 */
	public static int getExprienceNumByWintz(int wintz)
	{
		int beginNum = 0;
		int endNum = 0;
		if (wintz < 5)
		{
			beginNum = 50;
			endNum = 100;
		}
		else if (wintz >= 5 && wintz <= 10)
		{
			beginNum = 100;
			endNum = 200;
		}
		else if (wintz >= 11 && wintz <= 20)
		{
			beginNum = 200;
			endNum = 400;
		}
		else if (wintz >= 21 && wintz <= 50)
		{
			beginNum = 400;
			endNum = 1000;
		}
		else if (wintz >= 51 && wintz <= 100)
		{
			beginNum = 1000;
			endNum = 1500;
		}
		else if (wintz >= 100)
		{
			beginNum = 1500;
			endNum = 2500;
		}
		Random ran = new Random();
		return ran.nextInt(endNum - beginNum + 1) + beginNum;

	}

	/**
	 * 计算出2%的几率
	 * 
	 * @param args
	 */
	public static int isOneOrZero()
	{
		Random ran = new Random();
		int a = ran.nextInt(100);
		return a == 0 || a == 1 ? 1 : 0;

	}

	/**
	 * 返回用户的等级以及多余的经验 1~10 ： An = 10+5n(n-1) =5*n*n-5n+10 11~20 ：An =
	 * 460+10(n-10)*(n+9)=10*n*n-10n-440 21~40 ：An =
	 * 3360+15(n-20)*(n+19)=15*n*n-15n-2340 41~70 ：An =
	 * 21060+20(n-40)*(n+39)=20*n*n-20n-10140 71~100 ：An =
	 * 86460+25(n-70)*(n+69)=25*n*n-25n-34290
	 * 
	 * @param args
	 */
	public static int[] retLevelAndExp(int exp)
	{
		int level = 0;
		int overageExp = exp; // 多余的经验
		int levUp = 20;

		while (exp >= (20 + level * level + level * 10))
		{
			exp -= 20 + level * level + level * 10;
			level++;
			levUp = 20 + level * level + level * 10;
			overageExp = exp;

		}
		int[] rtn = new int[3];
		rtn[0] = level;
		rtn[1] = overageExp;
		rtn[2] = levUp;

		return rtn;
	}

	public static String retLevel(int exp)
	{
		String str = retLevelAndExp(exp)[0] + "";
		return str;
	}

	/**
	 * 判断挑战双方是否处于同一梯度中
	 * 
	 * @param args
	 */
	public static boolean IsInSameEchelon(int zlevel, int blevel)
	{

		return retEchelon(zlevel) == retEchelon(blevel) ? true : false;
	}

	/**
	 * 判断挑战玩家处于哪一梯度
	 * 
	 * @param args
	 */
	public static int retEchelon(int level)
	{

		if (level >= 0 && level <= 10)
		{
			level = 1;
		}
		else if (level >= 11 && level <= 20)
		{
			level = 2;
		}
		else if (level >= 21 && level <= 40)
		{
			level = 3;
		}
		else if (level >= 41 && level <= 70)
		{
			level = 4;
		}
		else if (level >= 71 && level <= 100)
		{
			level = 5;
		}
		return level;

	}

	/**
	 * 判断征服赛，两者的筹码是否符合规定
	 * 
	 * @param args
	 */
	public static int retBetByEchelon(int level)
	{
		int bet = 0;
		switch (retEchelon(level))
		{
		case 1:
			bet = 1000;
			break;
		case 2:
			bet = 2000;
			break;
		case 3:
			bet = 3000;
			break;
		case 4:
			bet = 4000;
			break;
		case 5:
			bet = 5000;
			break;
		}
		return bet;
	}

	/**
	 * 返回系统当前时间
	 * 
	 * @param args
	 */
	public static String retCurrentTiem()
	{
		return String.valueOf(Utils.getDateToLong());
	}

	/**
	 * 
	 * @param args
	 */
	public static long millsTosecond(long L)
	{
		L = Utils.getDateToLong() - L;
		L = L < 0 ? 0 : L / 1000;
		return L;
	}

	/**
	 * 获取日期
	 * 
	 * @param difnum
	 * @return
	 */
	public static String getDateToStr(int difnum)
	{
		java.util.Date myDate = new java.util.Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		// formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		long myTime = (myDate.getTime() / 1000) + (difnum * 60 * 60 * 24);
		myDate.setTime(myTime * 1000);
		return formatter.format(myDate);
	}

	public static long getDateToLong()
	{
		// long timeInMillis =
		// Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTimeInMillis();
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		return timeInMillis;
	}

	public static String encodeStr(String str)
	{
		String retStr = "";
		retStr = str.replaceAll("'", " ");
		return retStr;
	}

	// 对字符串数组排序 小-->大
	public static String[] sortArryStr(String[] str)
	{
		for (int i = 0; i < str.length; i++)
		{
			for (int j = i + 1; j < str.length; j++)
			{
				String temp = "";
				if (str[i].compareTo(str[j]) > 0)
				{
					temp = str[i];
					str[i] = str[j];
					str[j] = temp;
				}
			}
		}
		return str;
	}

	// 找出数组中大于”给定字符串“下一个位置元素
	public static String retItem(String f, String sid, String[] str)
	{
		String s = "";
		if (str.length > 0)
		{
			sortArryStr(str);
			// a 后一个元素 b,前一个元素 c代表第二轮以后玩家的起始位置
			for (String key : str)
			{
				if (f.equals("a"))
				{
					if (sid.compareTo(key) < 0)
					{
						s = key;
						break;
					}
				}
				else if (f.equals("b"))
				{
					if (sid.compareTo(key) > 0)
					{
						s = key;
					}
				}
				else if (f.equals("c"))
				{
					if (sid.compareTo(key) <= 0)
					{
						s = key;
						break;
					}
				}
			}
			s = s.equals("") ? (f.equals("a") ? str[0] : str[str.length - 1]) : s;
		}
		else
		{
			s = "";
		}
		return s;
	}

	public static boolean volidateDate(String dateStr)
	{
		String regexpForDate = "^\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}$";
		Pattern p = Pattern.compile(regexpForDate);
		Matcher m = p.matcher(dateStr);
		return m.find();
	}

	public static String getAuthrozedString(String uid)
	{
		String s = "";
		SimpleDateFormat sdf0 = new SimpleDateFormat("ddMMyyyyHH");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
		String s0 = sdf0.format(new Date());
		String s1 = sdf1.format(new Date());
		String s2 = sdf2.format(new Date());
		String s3 = sdf3.format(new Date());
		long[] timeToken = new long[4];
		timeToken[0] = Long.parseLong(s0);
		timeToken[1] = Long.parseLong(s1);
		timeToken[2] = Long.parseLong(s2);
		timeToken[3] = Long.parseLong(s3);

		long uidToken = Long.parseLong(uid) * Long.parseLong(s1)
				* Long.parseLong(s2) * Long.parseLong(s3) - Long.parseLong(s0);
		s = s0 + "" + uidToken;

		getAuthorInfo(s);
		return s;
	}

	private String dateToString(String date)
	{

		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		String str = formatDate.format(date);
		Date time = null;
		try
		{
			time = formatDate.parse(str);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return formatDate.format(time);
	}

	public static long[] getAuthorInfo(String AuthorToken)
	{
		long tokenInfo[] = new long[5];
		ConstList.config.logger.info("AuthorToken=" + AuthorToken);
		tokenInfo[0] = Long.parseLong(AuthorToken.substring(0, 2));
		tokenInfo[1] = Long.parseLong(AuthorToken.substring(2, 4));
		tokenInfo[2] = Long.parseLong(AuthorToken.substring(4, 8));
		long tmpL1 = Long.parseLong(AuthorToken.substring(0, 8));
		long tmpL2 = Long.parseLong(AuthorToken.substring(8));
		long tmpL3 = (tmpL2 - tmpL2)
				/ (tokenInfo[0] * tokenInfo[1] * tokenInfo[2]);

		ConstList.config.logger.info("tmpL1=" + tmpL1 + ",tmpL2=" + tmpL2
				+ ",tmpL3=" + tmpL3);
		ConstList.config.logger.info("tokenInfo[0]=" + tokenInfo[0] + ","
				+ tokenInfo[1] + "," + tokenInfo[2] + "," + tokenInfo[3]);
		return tokenInfo;

	}

	public static int debugid = 16;

	public static void main(String[] args)
	{
		if (debugid == 16)
		{

			// ConstList.config.logger.info(getAuthrozedString("10085"));
			ConstList.config.logger.info(retLevelAndExp(10)[0] + ","
					+ retLevelAndExp(10)[1] + "," + retLevelAndExp(10)[2] + ","
					+ retLevel(9017));
		}
		if (debugid == 0)
		{
			ConstList.config.logger.info(retLevel(7980));
			ConstList.config.logger.info(retLevelAndExp(7980));
		}
		else if (debugid == 1)
		{
			int[] a = selectNum(1, "12");
			for (int i = 0; i < a.length; i++)
			{
				ConstList.config.logger.info(a[i]);
			}
		}
		else if (debugid == 2)
		{
			String str = "2007-8-29";
			System.out.print(volidateDate(str));
		}
		else if (debugid == 3)
		{
			System.out.print(getDateToStr(0));
			String[] str =
			{ "sid1", "sid2", "sid3", "sid4", "sid5", "sid6", "sid7", "sid8",
					"sid9" };
			retItem("a", "sid9", str);
			retItem("c", "sid2", str);
			ConstList.config.logger.info(encodeStr("King's"));
			ConstList.config.logger.info(retLevelAndExp(80));
			String b = retLevelAndExp(8)[0] + "";
			Random ran = new Random();
			int a = ran.nextInt(2);
		}
		else if (debugid == 4)
		{
			int a = getExprienceNumByWintz(51);
		}
		else if (debugid == 5)
		{
			String a = getThreeRadomNum();
			ConstList.config.logger.info(IsInSameEchelon(0, 4));
			retLevel(481647);
			splitStrToArray("2@2@2@2@2@2@2@32@32@", "@");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String s = sdf.format(new Date());
		}
		else if (debugid == 4)
		{
			String s = "25";
			ConstList.config.logger.info(s.substring(1, 2));
		}
		else if (debugid == 6)
		{
			int[] a =
			{ 5, 6, 6, 4, 4 };
			a = retNumForTwo(a);
			for (int i = 0; i < a.length; i++)
			{
				ConstList.config.logger.info(a[i]);
			}
			getMaxAndNum(a);
			int t = retNum(a);
			ConstList.config.logger.info(t);
			int[] b = selectNum(5, sortArrayToStr(a));
			for (int i = 0; i < b.length; i++)
			{
				ConstList.config.logger.info(b[i]);
			}
		}
		else if (debugid == 7)
		{
			int[] a =
			{ 5, 7 };
			getMaxAndNum(a);
		}
		else if (debugid == 8)
		{
			int[] a =
			{ 5, 356, 4, 8, 4, 43, 5, 4, 0 };
			List aa = delEqualNum(a);
			int[] b = sidePool(delEqualNum(a));
			for (int i = 0; i < b.length; i++)
			{
				ConstList.config.logger.info(b[i]);
			}
		}
		else if (debugid == 9)
		{
			int[] a = selectNum(1, "12");
			for (int i = 0; i < a.length; i++)
			{
				ConstList.config.logger.info(a[i]);
			}
		}
		else if (debugid == 10)
		{
			int[] a =
			{ 5, 3, 4, 5, 4 };
			retNumForTwo(a);
			int num = retNum(a);
			System.out.print(num);
		}
		else if (debugid == 11)
		{
			int[] a =
			{ 1, 2, 3, 5, 6, 6, 7, 7 };

			a = sortBigToSmall(a);
			for (int i = 0; i < a.length; i++)
			{
				System.out.print(a[i]);
			}
		}
		else if (debugid == 12)
		{
			String[] a =
			{ "37721@133", "37728@333", "37729@233", "37724@533", "37726@633", };
			System.out.print(compareStrRetBigB(a));
		}
		else if (debugid == 13)
		{
			int[] a =
			{ 1, 2, 3, 5, 6, 6, 7, 7 };
			a = sortArray(a);
			for (int i = 0; i < a.length; i++)
			{
				ConstList.config.logger.info(a[i]);
			}

		}
		else if (debugid == 14)
		{
			int[] a =
			{ 1, 2, 3, 5, 6, 6, 7, 7 };
			Map<String,Integer> map = getMaxAndNum(a);
			Set<String> hm = map.keySet();
			ConstList.config.logger.info(hm);
			Iterator<String> it =  hm.iterator();
			while (it.hasNext())
			{
				String str =  it.next();
				ConstList.config.logger.info(str);
			}
		}
		else if (debugid == 15)
		{
			HashMap map = new HashMap();
			map.put(1, 3);
			map.put(3, 5);
			map.put("a", 343);
			ConstList.config.logger.info(map);
			int[] b = (int[]) map.get("barray");
			for (int i = 0; i < b.length; i++)
			{
				System.out.print(b[i]);
			}
			String str = 2 + "l" + 3;
			String s = str.substring(str.indexOf("l") + 1);
			System.out.print(s);
		}
	}
}