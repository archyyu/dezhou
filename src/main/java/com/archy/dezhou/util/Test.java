package com.archy.dezhou.util;

import java.util.Calendar;

public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		System.out.println(getValiddate("20120101", 0));
		System.out.println(getValiddate("20120228", 1));
	}

	public static String getValiddate(String dateStr, int type)
	{
		System.out.println("getValiddate(String " + dateStr + ",int " + type
				+ ") called!");

		String TodayString = "";
		Calendar calendar = Calendar.getInstance();
		int TodateInt = 0;
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int days = calendar.get(Calendar.DAY_OF_MONTH);

		TodayString += year + "";
		if (month < 10)
			TodayString += "0" + month;
		if (days < 10)
			TodayString += "0" + days;

		System.out
				.println("days=" + days + ",trace:TodayString=" + TodayString);
		TodateInt = Integer.parseInt(TodayString);

		boolean isValid = false;

		if (type == 0)// 验证开始时间
		{
			if (Utils.isNumeric(dateStr) && dateStr.length() == 8)
			{
				int dateint = Integer.parseInt(dateStr);
				System.out
						.println("dateint=" + dateint + ",dateStr=" + dateStr);
				int Instartyear = dateint / 10000;
				int Instartmonth = dateint % 10000 / 100;
				int Instartdays = dateint % 100;
				if (Instartyear <= year
						&& Instartyear >= (year - 1)
						&& Instartmonth < 13
						&& Instartdays <= endOftheDay(Instartyear, Instartmonth)
						&& TodateInt >= dateint)
				{
					isValid = true;
				}

			}

			if (!isValid)
				return ((TodateInt / 100) * 100) + "01";
			else
				return dateStr;

		}
		else if (type == 1)// 验证结束时间
		{
			if (Utils.isNumeric(dateStr) && dateStr.length() == 8)
			{
				int dateint = Integer.parseInt(dateStr);
				System.out
						.println("dateint=" + dateint + ",dateStr=" + dateStr);
				int Instartyear = dateint / 10000;
				int Instartmonth = dateint % 10000 / 100;
				int Instartdays = dateint % 100;
				System.out.println("dateint=" + dateint);
				if (Instartyear <= year
						&& Instartmonth < 13
						&& Instartdays <= endOftheDay(Instartyear, Instartmonth))
				{
					isValid = true;
				}
			}

			if (!isValid)
				return year + "" + month + "" + endOftheDay(year, month);
			else
				return dateStr;
		}
		else
			return dateStr;
	}

	public static int endOftheDay(int year, int month)
	{
		if (year % 4 == 0 && month == 2)
		{
			if (year % 400 != 0 && year % 100 == 0)
				return 28;
			else
				return 29;
		}
		else if (month == 2)
			return 28;

		else if (month == 1 || month == 3 || month == 5 || month == 7
				|| month == 8 || month == 10 || month == 12)
			return 31;
		else
			return 30;
	}

}
