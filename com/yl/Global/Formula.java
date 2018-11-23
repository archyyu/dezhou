package com.yl.Global;

import java.util.Hashtable;
import com.yl.vo.Puke;

public class Formula
{
	/**
	 * 牌型的定义 PukeType=0：皇家同花顺：同花色的A, K, Q, J和10. 例如F10，F11,F12，F13，F14
	 * PukeType=1：同花顺：五张同花色的连续牌。 F2，F3,F4，F5，F6
	 * PukeType=2：四条：其中四张是相同点数但不同花的扑克牌，第五张是随意的一张牌
	 * 。Fangkuai2，HeiTao2，Caohua2，Heitao2/HeiTao7
	 * PukeType=3：葫芦：由三张相同点数及任何两张其他相同点数的扑克牌组成Fangkuai2
	 * ，HeiTao2，Caohua2/Caohua7,HeiTao7 PukeType=4：同花：此牌由五张不按顺序但相同花的扑克牌组成
	 * F2，F3,F4，F5，F6 PukeType=5：顺子：此牌由五张连续扑克牌组成。F2，H3,F4，C5，F6
	 * PukeType=6：三条：由三张相同点数和两张不同点数的扑克组成
	 * 。Fangkuai2，HeiTao2，Caohua2/Caohua7,HeiTao4
	 * PukeType=7：两对：两对点数相同但两两不同的扑克和随意的一张牌组成 F2，H2/F4，C4/F6
	 * PukeType=8：一对：由两张相同点数的扑克牌和另三张随意的牌组成。F2，H2/F4，C6，C7
	 * PukeType=9：高牌：既不是同一花色也不是同一点数也不连续的五张牌组成。 F2，H5，F4，C6，C7
	 */
	public static byte PukeType = 9;
	public static int[] pukeCodeBorderList =
	{ 0, 1, 2, 11, 167, 323, 1610, 1620, 2478, 3336, 6196, 7483 };
	// public static int[] pukeCodeBorderList = {0, 1, 2, 11, 24, 37, 1324,
	// 1334, 1347, 2205, 5065, 6352};

	// 前5个元素表示片面的大小;
	// -1表示任意不相同的牌，0表示任意相同的牌
	// 最后一个元素表示牌面的整体大小顺序ID值
	public static short[][][] JokerComPareList =
	{
	{ // 0皇家同花顺，讲求花色1种
			{ 14, 1, }, },
			{// 1同花顺，讲求花色9种
			{ 13, 2, },
			{ 12, 3, },
			{ 11, 4, },
			{ 10, 5, },
			{ 9, 6, },
			{ 8, 7, },
			{ 7, 8, },
			{ 6, 9, },
			{ 5, 10, }, },
			// {//2四条，不讲求花色13种
			// {14, 11},
			// {13, 12},
			// {12, 13},
			// {11, 14},
			// {10, 15},
			// { 9, 16},
			// { 8, 17},
			// { 7, 18},
			// { 6, 19},
			// { 5, 20},
			// { 4, 21},
			// { 3, 22},
			// { 2, 23},
			// },
			// 2四条，不讲求花色156种
			new short[0][],
			// {//3葫芦，不讲求花色13种
			// {14, 24},
			// {13, 25},
			// {12, 26},
			// {11, 27},
			// {10, 28},
			// { 9, 29},
			// { 8, 30},
			// { 7, 31},
			// { 6, 32},
			// { 5, 33},
			// { 4, 34},
			// { 3, 35},
			// { 2, 36},
			// },
			// 3葫芦，不讲求花色156种
			new short[0][],
			// 4同花，讲求花色，通花的可能性为1287种，对于顺子的情况忽略掉
			new short[0][],
			// {//5顺子，不讲求花色 10种
			// {14, 1324},
			// {13, 1325},
			// {12, 1326},
			// {11, 1327},
			// {10, 1328},
			// { 9, 1329},
			// { 8, 1330},
			// { 7, 1331},
			// { 6, 1332},
			// { 5, 1333},
			// },
			{// 5顺子，不讲求花色 10种
			{ 14, 1610 },
			{ 13, 1611 },
			{ 12, 1612 },
			{ 11, 1613 },
			{ 10, 1614 },
			{ 9, 1615 },
			{ 8, 1616 },
			{ 7, 1617 },
			{ 6, 1618 },
			{ 5, 1619 }, },
			// {//6三条，不讲求花色 13种
			// {14, 1334},
			// {13, 1335},
			// {12, 1336},
			// {11, 1337},
			// {10, 1338},
			// { 9, 1339},
			// { 8, 1340},
			// { 7, 1341},
			// { 6, 1342},
			// { 5, 1343},
			// { 4, 1344},
			// { 3, 1345},
			// { 2, 1346},
			// },
			// 6三条，不讲求花色 858种
			new short[0][],
			// 7两对，不讲求花色，两对可能性有13!/2!*11! * 11 = 858种
			new short[0][],
			// 8一对，不讲求花色 可能性： 13 * 12!/9!*3! = 2860 种可能
			new short[0][],
			// 9高牌，不讲求花色， 可能性： 13!/5!8! = 1287种可能，对于顺子的情况忽略掉
			new short[0][], };
	// 概率
	public static int[] ProbabilityFactorList =
	{ 4, 36, 624, 3744, 5108, 10200, 54912, 123552, 1098240, 1302540 };
	// 总概率
	public static int TotalProbability = 2598960;
	// 大的牌型一共分为10类
	public static byte[] TheBiggestPukeType = new byte[10];

	/**
	 * pukeList定义：长度为10，前5个元素为扑克的牌面值，后5个值为扑克的花色值。
	 */
	public static int GetPukerCode(byte[] pukeList)
	{
		boolean ifPukeSameShape = false;
		if (pukeList.length != 10)
			return -1;

		byte[] SortList = new byte[5];
		;
		for (int i = 0; i < SortList.length; i++)
			SortList[i] = pukeList[i];

		// System.out.println("SortList="+SortList[0]+","+SortList[1]+","+SortList[2]+","+SortList[3]+","+SortList[4]);
		// 第一步，先判断花色
		if (pukeList[5] == pukeList[6] && pukeList[5] == pukeList[7]
				&& pukeList[7] == pukeList[8] && pukeList[8] == pukeList[9])
			ifPukeSameShape = true;
		if (ifPukeSameShape)
		{// 此时牌型可能是皇家同花顺或同花顺或同花 {0,1,4}
			// 处理同一花色的情况
			if (SortList[0] != 14)// 没有A的情况，普通的同花或同花顺{1,4}
			{
				if (SortList[0] - SortList[1] == 1
						&& SortList[1] - SortList[2] == 1
						&& SortList[2] - SortList[3] == 1
						&& SortList[3] - SortList[4] == 1)
				{
					PukeType = 1;// 普通同花顺
					System.out.println("牌型为" + PukeType + "--同花顺");
					return getSpecialPukeTypeCode(PukeType, (byte) SortList[0]);
				}
				else
				{
					PukeType = 4;// 同花
					System.out.println("牌型为" + PukeType + "--同花");
					return getNoRepeatPukerCode(SortList, (byte) 5);
				}
			}
			else
			// 有A的情况
			{
				int[] tmpMem = new int[]
				{ 0, 0, 0 };
				if (SortList[0] == 14 && SortList[1] == 13 && SortList[2] == 12
						&& SortList[3] == 11 && SortList[4] == 10)// 同花顺，且是皇家同花顺
					tmpMem[0] = 1;
				else if (SortList[0] == 14 && SortList[1] == 5
						&& SortList[2] == 4 && SortList[3] == 3
						&& SortList[4] == 2)// 同花顺，且是5,4,3,2,1的最小顺子
					tmpMem[2] = 10;
				else
					tmpMem[1] = getNoRepeatPukerCode(SortList, (byte) 5);// 普通的同花
				if (tmpMem[0] == 1)
				{
					PukeType = 0;// 皇家同花顺
					System.out.println("牌型为" + PukeType + "--皇家同花顺");
					return tmpMem[0];
				}
				else if (tmpMem[2] == 10)
				{
					PukeType = 1;// 同花顺
					System.out.println("牌型为" + PukeType + "--同花顺");
					return tmpMem[2];
				}
				else
				{
					PukeType = 4;// 同花
					System.out.println("牌型为" + PukeType + "--同花");
					return tmpMem[1];
				}
			}
		}
		else
		{ // 此时牌型可能是{2,3,5,6,7,8,9}
			// 处理不同花色的情况
			// 第1步，判断是否有四条的情况
			if (SortList[0] == SortList[1] && SortList[1] == SortList[2]
					&& SortList[2] == SortList[3]) // 四条，单牌在后
			{
				PukeType = 2;
				return get4PukeTypeCode(PukeType, SortList, 4, 1);
			}
			else if (SortList[1] == SortList[2] && SortList[2] == SortList[3]
					&& SortList[3] == SortList[4]) // 四条，单牌在前
			{
				PukeType = 2;
				return get4PukeTypeCode(PukeType, SortList, 4, 2);
			}
			// 第2步，判断是否葫芦
			else if ((SortList[0] == SortList[1] && SortList[1] == SortList[2] && SortList[2] != SortList[3])
					&& (SortList[3] == SortList[4]))// 葫芦，三条在前
			{
				PukeType = 3;
				return get4PukeTypeCode(PukeType, SortList, 5, 1);
			}
			else if ((SortList[0] == SortList[1] && SortList[1] != SortList[2])
					&& (SortList[2] == SortList[3] && SortList[3] == SortList[4]))// 葫芦，三条在后
			{
				PukeType = 3;
				return get4PukeTypeCode(PukeType, SortList, 5, 2);
			}
			// 第3步，判断是否三条
			else if (SortList[0] == SortList[1] && SortList[1] == SortList[2]
					&& SortList[2] != SortList[3] && SortList[3] != SortList[4])// 三条，且三条在前
			{
				PukeType = 6;
				return get3PukeTypeCode(PukeType, SortList);
			}
			else if (SortList[1] == SortList[2] && SortList[2] == SortList[3]
					&& SortList[3] != SortList[4] && SortList[0] != SortList[1])// 三条，且三条在中间
			{
				PukeType = 6;
				SortList = sort3PukeList(SortList, (byte) 1);
				return get3PukeTypeCode(PukeType, SortList);
			}
			else if (SortList[2] == SortList[3] && SortList[3] == SortList[4]
					&& SortList[0] != SortList[1] && SortList[1] != SortList[2])// 三条，且三条在最后
			{
				PukeType = 6;
				SortList = sort3PukeList(SortList, (byte) 2);
				return get3PukeTypeCode(PukeType, SortList);
			}
			// 第4步，判断是否为两对
			else if (SortList[0] == SortList[1] && SortList[2] == SortList[3]
					&& SortList[3] != SortList[4] && SortList[1] != SortList[2])// 两对，单牌在后
			{
				PukeType = 7;
				return get2PairsPukeCode(SortList);
			}
			else if (SortList[0] == SortList[1] && SortList[3] == SortList[4]
					&& SortList[1] != SortList[2] && SortList[2] != SortList[3])// 两对，单牌在中间
			{
				PukeType = 7;
				SortList = sort2PairsPukeList(SortList, (byte) 2);
				return get2PairsPukeCode(SortList);
			}
			else if (SortList[1] == SortList[2] && SortList[3] == SortList[4]
					&& SortList[0] != SortList[1] && SortList[2] != SortList[3])// 两对，单牌在前
			{
				PukeType = 7;
				SortList = sort2PairsPukeList(SortList, (byte) 0);
				return get2PairsPukeCode(SortList);
			}
			// 第5步，判断是否为一对
			else if (SortList[0] == SortList[1] && SortList[1] != SortList[2]
					&& SortList[2] != SortList[3] && SortList[3] != SortList[4])// 一对，且一对在前
			{
				PukeType = 8;
				return get1PairsPukeCode(SortList);
			}
			else if (SortList[1] == SortList[2] && SortList[0] != SortList[1]
					&& SortList[2] != SortList[3] && SortList[3] != SortList[4])// 一对，且一对在第二位
			{
				PukeType = 8;
				SortList = sort1PairsPukeList(SortList, (byte) 1);
				return get1PairsPukeCode(SortList);
			}
			else if (SortList[2] == SortList[3] && SortList[0] != SortList[1]
					&& SortList[1] != SortList[2] && SortList[3] != SortList[4])// 一对，且一对在第三位
			{
				PukeType = 8;
				SortList = sort1PairsPukeList(SortList, (byte) 2);
				return get1PairsPukeCode(SortList);
			}
			else if (SortList[3] == SortList[4] && SortList[0] != SortList[1]
					&& SortList[1] != SortList[2] && SortList[2] != SortList[3])// 一对，且一对在第四位，末尾
			{
				PukeType = 8;
				SortList = sort1PairsPukeList(SortList, (byte) 3);
				return get1PairsPukeCode(SortList);
			}
			// 第6步，牌不重复的情况，顺子或者高牌
			else if (SortList[0] != SortList[1] && SortList[1] != SortList[2]
					&& SortList[2] != SortList[3] && SortList[3] != SortList[4])
			{
				// 判断是否有顺子的情况
				if (SortList[0] - SortList[1] == 1
						&& SortList[1] - SortList[2] == 1
						&& SortList[2] - SortList[3] == 1
						&& SortList[3] - SortList[4] == 1)
				{
					PukeType = 5;
					System.out.println("牌型为" + PukeType + "--杂顺子");
					return getSpecialPukeTypeCode(PukeType, (byte) SortList[0]);
				}
				else if (SortList[0] == 14 && SortList[1] == 5
						&& SortList[2] == 4 && SortList[3] == 3
						&& SortList[4] == 2)
				{
					PukeType = 5;
					System.out.println("牌型为" + PukeType + "--最小的杂顺子");
					return getSpecialPukeTypeCode(PukeType, (byte) SortList[1]);
				}
				else
				// 此种情况下为高牌
				{
					PukeType = 9;
					System.out.println("牌型为" + PukeType + "--普通高牌");
					return getNoRepeatPukerCode(SortList, (byte) 10);
				}
			}
		}

		return -1;
	}

	public static int get2PairsPukeCode(byte[] SortPukeList)
	{
		int[] circleNum = new int[2];
		circleNum[0] = (13 - (SortPukeList[0] - 1));
		circleNum[1] = (SortPukeList[0] - (SortPukeList[2] + 1));
		int PukerTotalCode = pukeCodeBorderList[8];
		int[] pukerEveryBitList = new int[3];
		for (int i = 0; i < 3; i++)
		{
			if (i == 0)
				for (int j = 1; j <= circleNum[0]; j++)
					pukerEveryBitList[0] += (13 - j) * 11;
			else if (i == 1)
				pukerEveryBitList[1] += circleNum[1] * 11;
			else if (i == 2)
			{
				if (SortPukeList[0] > SortPukeList[4]
						&& SortPukeList[2] > SortPukeList[4])
					pukerEveryBitList[2] = 14 - SortPukeList[4] - 2;
				else if (SortPukeList[0] > SortPukeList[4]
						&& SortPukeList[2] < SortPukeList[4])
					pukerEveryBitList[2] = 14 - SortPukeList[4] - 1;
				else
					pukerEveryBitList[2] = 14 - SortPukeList[4];
			}
		}
		PukerTotalCode += pukerEveryBitList[0] + pukerEveryBitList[1]
				+ pukerEveryBitList[2];
		System.out.println("pukerEveryBitList=" + pukerEveryBitList[0] + ","
				+ pukerEveryBitList[1] + "," + pukerEveryBitList[2]);
		System.out.println("牌型为" + PukeType + "--两对：" + PukerTotalCode);
		return PukerTotalCode;
	}

	public static int get1PairsPukeCode(byte[] SortPukeList)
	{
		int[] circleNum = new int[3];
		int PukerTotalCode = pukeCodeBorderList[9];
		circleNum[0] = 13 - (SortPukeList[0] - 1);
		circleNum[1] = 13 - (SortPukeList[2] - 1);
		circleNum[2] = SortPukeList[2] - (SortPukeList[3] + 1);
		int[] pukerEveryBitList = new int[4];
		for (int i = 0; i < 4; i++)
		{
			if (i == 0)
			{
				for (int j = 1; j <= circleNum[0]; j++)
					pukerEveryBitList[0] += 220;
			}
			else if (i == 1)
			{
				for (int j = 1; j <= circleNum[1]; j++)
				{
					pukerEveryBitList[1] += (12 - j) * (11 - j) / 2;
				}
				if (SortPukeList[0] > SortPukeList[2])
					pukerEveryBitList[1] -= (SortPukeList[0] - 3)
							* (SortPukeList[0] - 4) / 2;
			}
			else if (i == 2)
			{
				for (int j = 1; j <= circleNum[2]; j++)
				{
					pukerEveryBitList[2] += (SortPukeList[2] - 2 - j);
				}
				if (SortPukeList[0] > SortPukeList[3]
						&& SortPukeList[0] < SortPukeList[2])
					pukerEveryBitList[2] -= SortPukeList[0] - 2;
			}
			else if (i == 3)
			{
				if (SortPukeList[0] > SortPukeList[4]
						&& SortPukeList[0] < SortPukeList[3])
					pukerEveryBitList[3] = SortPukeList[3]
							- (SortPukeList[4] + 1) - 1;
				else
					pukerEveryBitList[3] = SortPukeList[3]
							- (SortPukeList[4] + 1);
			}
		}
		PukerTotalCode += pukerEveryBitList[0] + pukerEveryBitList[1]
				+ pukerEveryBitList[2] + pukerEveryBitList[3];
		return PukerTotalCode;
	}

	// offset 为单牌的偏移量,只处理单牌在0和2的位置
	public static byte[] sort2PairsPukeList(byte[] preSortPukeList, byte offset)
	{
		byte[] tmpList = new byte[5];

		if (offset == 0)
		{
			tmpList[0] = preSortPukeList[1];
			tmpList[1] = preSortPukeList[2];
			tmpList[2] = preSortPukeList[3];
			tmpList[3] = preSortPukeList[4];
			tmpList[4] = preSortPukeList[0];
		}
		else if (offset == 2)
		{
			tmpList[0] = preSortPukeList[0];
			tmpList[1] = preSortPukeList[1];
			tmpList[2] = preSortPukeList[3];
			tmpList[3] = preSortPukeList[4];
			tmpList[4] = preSortPukeList[2];
		}
		return tmpList;
	}

	// offset 为对子的偏移量,处理单牌在1,2,3的位置
	public static byte[] sort1PairsPukeList(byte[] preSortPukeList, byte offset)
	{
		byte[] tmpList = new byte[5];

		if (offset == 1)
		{
			tmpList[0] = preSortPukeList[1];
			tmpList[1] = preSortPukeList[2];
			tmpList[2] = preSortPukeList[0];
			tmpList[3] = preSortPukeList[3];
			tmpList[4] = preSortPukeList[4];
		}
		else if (offset == 2)
		{
			tmpList[0] = preSortPukeList[2];
			tmpList[1] = preSortPukeList[3];
			tmpList[2] = preSortPukeList[0];
			tmpList[3] = preSortPukeList[1];
			tmpList[4] = preSortPukeList[4];
		}
		else if (offset == 3)
		{
			tmpList[0] = preSortPukeList[3];
			tmpList[1] = preSortPukeList[4];
			tmpList[2] = preSortPukeList[0];
			tmpList[3] = preSortPukeList[1];
			tmpList[4] = preSortPukeList[2];
		}
		return tmpList;

	}

	// offset 为三条的偏移量,处理三条在1,2,3的位置
	public static byte[] sort3PukeList(byte[] preSortPukeList, byte offset)
	{

		byte[] tmpList = new byte[5];

		if (offset == 1)
		{
			tmpList[0] = preSortPukeList[1];
			tmpList[1] = preSortPukeList[2];
			tmpList[2] = preSortPukeList[3];
			if (preSortPukeList[0] > preSortPukeList[4])
			{
				tmpList[3] = preSortPukeList[0];
				tmpList[4] = preSortPukeList[4];
			}
			else
			{
				tmpList[3] = preSortPukeList[4];
				tmpList[4] = preSortPukeList[0];
			}

		}
		else if (offset == 2)
		{
			tmpList[0] = preSortPukeList[2];
			tmpList[1] = preSortPukeList[3];
			tmpList[2] = preSortPukeList[4];
			if (preSortPukeList[0] > preSortPukeList[1])
			{
				tmpList[3] = preSortPukeList[0];
				tmpList[4] = preSortPukeList[1];
			}
			else
			{
				tmpList[3] = preSortPukeList[1];
				tmpList[4] = preSortPukeList[0];
			}
		}
		return tmpList;
	}

	public static int get4PukeTypeCode(byte puketype, byte[] SortPukeList,
			int code, int i)
	{
		int PukerTotalCode = pukeCodeBorderList[code];
		int pukerEveryBitList = 0;
		if (i == 1)
		{
			pukerEveryBitList = (14 - (SortPukeList[4] + 1))
					+ (14 - SortPukeList[0]) * 12;
		}
		else
		{
			pukerEveryBitList = (14 - (SortPukeList[0]))
					+ (14 - SortPukeList[2]) * 12;
		}

		PukerTotalCode += pukerEveryBitList;
		System.out.println("PukerTotalCode=" + PukerTotalCode);
		System.out.println("牌型为" + PukeType + "--四条：" + PukerTotalCode);
		return PukerTotalCode;
	}

	public static int get3PukeTypeCode(byte puketype, byte[] SortPukeList)
	{
		int[] circleNum = new int[2];
		if (SortPukeList[0] > SortPukeList[3])
		{
			circleNum[0] = (13 - (SortPukeList[0] - 1));
			circleNum[1] = (SortPukeList[0] - (SortPukeList[3] + 1));
		}
		else
		{
			circleNum[0] = (13 - (SortPukeList[0] - 1));
			circleNum[1] = (SortPukeList[0] - (SortPukeList[4] + 1));
		}

		int PukerTotalCode = pukeCodeBorderList[7];
		int[] pukerEveryBitList = new int[3];
		for (int i = 0; i < 3; i++)
		{
			if (i == 0)
				for (int j = 1; j <= circleNum[0]; j++)
					pukerEveryBitList[0] += (13 - j) * 11;
			else if (i == 1)
				pukerEveryBitList[1] += circleNum[1] * 11;
			else if (i == 2)
			{
				if (SortPukeList[0] > SortPukeList[3])
				{
					if (SortPukeList[0] > SortPukeList[4]
							&& SortPukeList[3] > SortPukeList[4])
						pukerEveryBitList[2] = 14 - SortPukeList[4] - 2;
					else if (SortPukeList[0] > SortPukeList[4]
							&& SortPukeList[3] < SortPukeList[4])
						pukerEveryBitList[2] = 14 - SortPukeList[4] - 1;
					else
						pukerEveryBitList[2] = 14 - SortPukeList[4];
				}
				else
				{
					if (SortPukeList[0] > SortPukeList[3]
							&& SortPukeList[4] > SortPukeList[3])
						pukerEveryBitList[2] = 14 - SortPukeList[3] - 2;
					else if (SortPukeList[0] > SortPukeList[3]
							&& SortPukeList[4] < SortPukeList[3])
						pukerEveryBitList[2] = 14 - SortPukeList[3] - 1;
					else
						pukerEveryBitList[2] = 14 - SortPukeList[3];
				}
			}
		}
		PukerTotalCode += pukerEveryBitList[0] + pukerEveryBitList[1]
				+ pukerEveryBitList[2];
		System.out.println("pukerEveryBitList=" + pukerEveryBitList[0] + ","
				+ pukerEveryBitList[1] + "," + pukerEveryBitList[2]);
		System.out.println("牌型为" + PukeType + "--三条：" + PukerTotalCode);
		return PukerTotalCode;
	}

	public static short getSpecialPukeTypeCode(byte puketype, byte bigOne)
	{
		short rtn = -1;
		for (int i = 0; i < JokerComPareList[puketype].length; i++)
		{
			if (JokerComPareList[puketype][i][0] == bigOne)
				rtn = JokerComPareList[puketype][i][1];
		}
		if (puketype == 5)
			System.out.println("此为特殊牌型杂顺子，牌面ID为" + rtn);
		else if (puketype == 1)
			System.out.println("此为特殊牌型同花顺，牌面ID为" + rtn);
		return rtn;

	}

	public static boolean ifserial(byte[] pukeList)
	{
		boolean ifserial = false;
		if (pukeList[0] - pukeList[1] == 1 && pukeList[1] - pukeList[2] == 1
				&& pukeList[2] - pukeList[3] == 1
				&& pukeList[3] - pukeList[4] == 1)
			ifserial = true;
		return ifserial;
	}

	public static int getNoRepeatPukerCode(byte[] pukeList, byte offsetId)
	{
		if (pukeList[0] > 14 || pukeList[1] > 13 || pukeList[2] > 12
				|| pukeList[3] > 11 || pukeList[4] > 10)
		{
			System.out.println("该数据违反德州扑克牌最小原则，请检查排序后重新提交。");
			return -1;
		}
		else if (!(pukeList[0] > pukeList[1] && pukeList[1] > pukeList[2]
				&& pukeList[2] > pukeList[3] && pukeList[3] > pukeList[4]))
		{
			System.out.println("该数据没有排序，不适用于本方法！请重新排序");
			return -1;
		}
		else if (pukeList[0] < 6 || pukeList[1] < 5 || pukeList[2] < 4
				|| pukeList[3] < 3 || pukeList[4] < 2)
		{
			System.out.println("该数据违反德州扑克牌最小原则，请检查排序后重新提交。");
			return -1;
		}
		else if (pukeList[0] == pukeList[1] && pukeList[1] == pukeList[2]
				&& pukeList[2] == pukeList[3] && pukeList[3] == pukeList[4])
		{
			System.out.println("该数据违反德州扑克牌存在原则，请检查数据。");
			return -1;
		}
		int beginBorder = pukeCodeBorderList[offsetId];
		int totalPossibilityNum = pukeCodeBorderList[offsetId + 1]
				- pukeCodeBorderList[offsetId];
		int PukerTotalCode = 0;
		int[] pukerEveryBitList = new int[pukeList.length];
		int[] circleNum = new int[4];
		for (int i = 0; i < pukeList.length; i++)
		{
			circleNum[0] = 13 - (pukeList[i] - 1);
			circleNum[1] = pukeList[0] - (pukeList[1] + 1);
			circleNum[2] = pukeList[1] - (pukeList[2] + 1);
			circleNum[3] = pukeList[2] - (pukeList[3] + 1);
			if (i == 0)
			{
				pukerEveryBitList[0] = 0;
				System.out.println("circleNum[0] = " + circleNum[0]);
				for (int j = 1; j <= circleNum[0]; j++)
					pukerEveryBitList[0] += (13 - j) * (12 - j) * (11 - j)
							* (10 - j) / 24;
			}
			else if (i == 1)
			{
				pukerEveryBitList[1] = 0;
				for (int j = 1; j <= circleNum[1]; j++)
					pukerEveryBitList[1] += (pukeList[0] - 2 - j)
							* (pukeList[0] - 3 - j) * (pukeList[0] - 4 - j) / 6;
			}
			else if (i == 2)
			{
				pukerEveryBitList[2] = 0;
				for (int j = 1; j <= circleNum[2]; j++)
					pukerEveryBitList[2] += (pukeList[1] - 2 - j)
							* (pukeList[1] - 3 - j) / 2;
			}
			else if (i == 3)
			{
				pukerEveryBitList[3] = 0;
				for (int j = 1; j <= circleNum[3]; j++)
					pukerEveryBitList[3] += (pukeList[2] - 2 - j);
			}
			else if (i == 4)
			{
				pukerEveryBitList[4] += (pukeList[i - 1] - 1) - pukeList[i];
			}
		}
		PukerTotalCode = pukerEveryBitList[0] + pukerEveryBitList[1]
				+ pukerEveryBitList[2] + pukerEveryBitList[3]
				+ pukerEveryBitList[4];
		if (PukerTotalCode > totalPossibilityNum)
		{
			System.out.println("计算的数据出错，请检查！" + PukerTotalCode);
			return -2;
		}
		else
		{
			PukerTotalCode += beginBorder;
			System.out.println("计算出来的数据落在正常范围之内，值为 " + PukerTotalCode);
		}
		return PukerTotalCode;
	}

	/**
	 * 从7,6,5张牌中抽出5张牌分别组成21，6, 1种组合
	 * 
	 * @param args
	 */
	public static Hashtable SevenToFiveGroups(Hashtable table, int pukeNum)
	{
		Hashtable group = new Hashtable();
		int num = 0;

		for (int a = 0; a < pukeNum - 4; a++)
		{
			for (int b = a + 1; b < pukeNum - 3; b++)
			{
				for (int c = b + 1; c < pukeNum - 2; c++)
				{
					for (int d = c + 1; d < pukeNum - 1; d++)
					{
						for (int e = d + 1; e < pukeNum; e++)
						{
							Hashtable Puke = new Hashtable();
							Puke.put(new Integer(0), table.get(new Integer(a)));
							Puke.put(new Integer(1), table.get(new Integer(b)));
							Puke.put(new Integer(2), table.get(new Integer(c)));
							Puke.put(new Integer(3), table.get(new Integer(d)));
							Puke.put(new Integer(4), table.get(new Integer(e)));
							group.put(new Integer(num++), Puke);
						}
					}
				}
			}
		}
		num = 0;
		return group;
	}

	/**
	 * 根据HashMap中 Puke的num属性从大到小排序
	 * 
	 * @param args
	 */
	public static Hashtable orderPuke(Hashtable table)
	{
		Puke temp;
		for (int i = 0; i < table.size(); i++)
		{
			for (int j = 0; j < table.size() - i - 1; j++)
			{
				if (((Puke) table.get(new Integer(j))).getNum() < ((Puke) table
						.get(new Integer(j + 1))).getNum())
				{
					temp = (Puke) table.get(new Integer(j));
					table.put(new Integer(j), table.get(new Integer(j + 1)));
					table.put(new Integer(j + 1), temp);
				}
			}
		}
		return table;
	}

	// 得到byte类型的tag
	public static byte getNumberTag(String s)
	{
		byte number = -1;
		if (s.equals("D"))
		{
			number = 0;
		}
		else if (s.equals("C"))
		{
			number = 1;
		}
		else if (s.equals("B"))
		{
			number = 2;
		}
		else if (s.equals("A"))
		{
			number = 3;
		}
		return number;
	}

	// 得到String类型的tag
	public static String getStringTag(byte number)
	{
		String s = null;
		if (number == 0)
		{
			s = "D";
		}
		else if (number == 1)
		{
			s = "C";
		}
		else if (number == 2)
		{
			s = "B";
		}
		else if (number == 3)
		{
			s = "A";
		}
		return s;
	}

	public static void getTheBiggestPukeType(Hashtable pukeListMap, int pukeNum)
	{
		Hashtable FivePukeListMap = new Hashtable();
		Hashtable GroupList = SevenToFiveGroups(pukeListMap, pukeNum);
		Puke pukeobj = null;
		byte tag, num;
		byte[][] sortedPukeList = new byte[GroupList.size()][10];
		int[] TheBigOne = new int[2]; // 预存最大的值，第一个元素为牌面值，第二个元素为第几种组合;
		int TmpCode = 0;
		for (int i = 0; i < GroupList.size(); i++)
		{
			FivePukeListMap = (Hashtable) GroupList.get(new Integer(i));// 得到第一种组合的五张牌
			FivePukeListMap = orderPuke(FivePukeListMap);
			for (int j = 0; j < FivePukeListMap.size(); j++)
			{
				pukeobj = (Puke) FivePukeListMap.get(new Integer(j));
				tag = getNumberTag(pukeobj.getTag());
				num = (byte) pukeobj.getNum();
				sortedPukeList[i][j] = num;
				sortedPukeList[i][j + 5] = tag;
			}
			TmpCode = GetPukerCode(sortedPukeList[i]);

			if (i == 0 || TmpCode < TheBigOne[0])
			{
				TheBigOne[0] = TmpCode;
				TheBigOne[1] = i;
			}
		}
		TheBiggestPukeType = sortedPukeList[TheBigOne[1]];
	}

	// 尝试用数组取代hashmap排序的方法。
	public static byte[] UtilsOfSortPukeList(byte[] pukeList)
	{
		int dealLen = pukeList.length / 2;
		byte[][] dealArray = new byte[2][dealLen];
		byte[] dealedArray = new byte[pukeList.length];
		for (int i = 0; i < dealLen; i++)
		{
			dealArray[0][i] = pukeList[i];
			dealArray[1][i] = pukeList[i + dealLen];
		}

		dealArray = insertSort(dealArray);
		for (int i = 0; i < dealLen; i++)
		{
			dealedArray[i] = dealArray[0][i];
			dealedArray[dealLen + i] = dealArray[1][i];
		}
		return dealedArray;

	}

	// 插入排序
	public static byte[][] insertSort(byte[][] result)
	{
		int ARRAYSIZE = result[0].length;
		for (int i = 1; i < ARRAYSIZE; i++)
		{
			for (int j = i; j > 0 && result[0][j] > result[0][j - 1]; j--)
			{
				swap(result[0], j, j - 1);
				swap(result[1], j, j - 1);
			}
		}
		return result;
	}

	private static void swap(byte[] a, int i, int j)
	{
		byte temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	public static Hashtable GetPukeHashTable(byte TotalPukeNum)
	{
		byte[] pukeNum2Tag = null;
		if (TotalPukeNum == -1)
		{
			pukeNum2Tag = combineUserAnd2DeskPuke(userPukeList, commonPukeList);
			TotalPukeNum = (byte) (pukeNum2Tag.length / 2);
		}
		else if (TotalPukeNum == 7)
			pukeNum2Tag = new byte[]
			{ 10, 11, 12, 13, 2, 12, 11, 1, 1, 1, 1, 1, 2, 2 };
		else if (TotalPukeNum == 6)
			pukeNum2Tag = new byte[]
			{ 2, 2, 4, 5, 7, 6, 1, 2, 1, 1, 1, 1 };
		else if (TotalPukeNum == 5)
			pukeNum2Tag = new byte[]
			{ 2, 2, 4, 5, 7, 1, 2, 1, 1, 1 };
		Puke[] pukeList = new Puke[TotalPukeNum];
		Hashtable pukeListTable = new Hashtable();
		for (int i = 0; i < TotalPukeNum; i++)
		{
			pukeList[i] = new Puke(getStringTag(pukeNum2Tag[TotalPukeNum + i]),
					pukeNum2Tag[i]);
			pukeListTable.put(new Integer(i), pukeList[i]);
		}
		return pukeListTable;
	}

	// 定义用户手中的两张牌，前两个元素为点数，中间两个元素为花色，后两个元素为归属，这里为玩家所有
	public static byte[][] userPukeList = new byte[][]
	{
	{ 13, 12 },
	{ 1, 1 },
	{ 0, 0 } };
	// 定义公共牌，五张，因时动态增加，用二位数组表示。
	// 花色的定义，0为黑心，1为红桃，2为梅花，3为菱形
	// 后五个元素为牌归属，这里指公共牌
	public static byte[][] commonPukeList = new byte[][]
	{
	{ 11, 3, 4, 12, 9, 10, 11, 4, 14, },
	{ 1, 0, 3, 0, 1, 1, 2, 2, 0, },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, }, };

	// 合并桌面上的牌和用户手中的牌。
	public static byte[] combineUserAnd2DeskPuke(byte[][] userPukeList,
			byte[][] commonPukeList)
	{
		if (userPukeList == null || commonPukeList == null)
			return null;
		int comLen = (userPukeList[0].length + commonPukeList[0].length) * 2;

		byte[] rtn = new byte[comLen];

		System.out.println("comLen=" + comLen);

		for (int i = 0; i < comLen / 2; i++)
		{
			if (i < userPukeList[0].length)
			{
				rtn[i] = userPukeList[0][i];
				rtn[i + comLen / 2] = userPukeList[1][i];
			}
			else
			{
				rtn[i] = commonPukeList[0][i - userPukeList[0].length];
				rtn[i + comLen / 2] = commonPukeList[1][i
						- userPukeList[0].length];
			}
		}
		return rtn;
	}

	public static void main(String[] args)
	{
		byte[] pukeList =
		{ 10, 14, 10, 14, 10, 2, 3, 2, 1, 3 };
		// 排序 先三条 单牌从小到大
		int rtn = -1;
		pukeList = UtilsOfSortPukeList(pukeList);
		rtn = GetPukerCode(pukeList);
		System.out.println("牌面的返回值为" + rtn + ",牌面的类型为" + PukeType);
		Hashtable pukeListTable = GetPukeHashTable((byte) 7);
		getTheBiggestPukeType(pukeListTable, pukeListTable.size());

	}
}
