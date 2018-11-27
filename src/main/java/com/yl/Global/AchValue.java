package com.yl.Global;

import java.util.HashMap;

public class AchValue
{

	/**
	 * 成就种类
	 */
	public static final int CHENJIU_WIN_ROCKETS = 1; // 红A 一对
	public static final int CHENJIU_WIN_COWBOYS = 2; // 红K 一对
	public static final int CHENJIU_WIN_LADIES = 3; // 红Q 一对
	public static final int CHENJIU_WIN_LOW_ROCKETS = 4; //
	public static final int CHENJIU_WIN_ROYAL_FLUSH = 5; // 皇家同花顺
	public static final int CHENJIU_WIN_STRAIGHT_FLUSH = 6; // 梅花 2到6同花顺
	public static final int CHENJIU_WIN_BROADWAY = 7; // 10~A
	public static final int CHENJIU_WIN_STR_TO_TEN = 8; // 6~10
	public static final int CHENJIU_WIN_MIKE_HAVEN = 9; // 方块A和K
	public static final int CHENJIU_WIN_HARRY_POTTER = 10; // 梅花J 方块k
	public static final int CHENJIU_WIN_ANY_QUADS = 11; //
	public static final int CHENJIU_WIN_FOUR_ACES = 12; // 4条A
	public static final int CHENJIU_WIN_FOUR_KINDS = 13; // 4条K
	public static final int CHENJIU_WIN_ALL_WHEEL_DRIVE = 14; // 任意4条
	public static final int CHENJIU_WIN_JACK_ASS = 15; // 红桃J黑桃A
	public static final int CHENJIU_WIN_ANNA_KOURNIKOVA = 16; // 梅花k和A
	public static final int CHENJIU_WIN_LADY_LOVE = 17; // 10和Q是红桃
	public static final int CHENJIU_WIN_ROYAL_COUPLE = 18; // K和Q 是梅花
	public static final int CHENJIU_WIN_OJ = 19; // 方块Q和红桃J
	public static final int CHENJIU_WIN_KOJACK = 20; // 黑k梅花J
	public static final int CHENJIU_WIN_BRUNSON = 21; // 10和2
	public static final int CHENJIU_WIN_HELLMUTH = 22; //
	public static final int CHENJIU_WIN_NEGREANU = 23; //
	public static final int CHENJIU_WIN_JEFF_TALLEY = 24; // 黑桃7方块4
	public static final int CHENJIU_WIN_HIGH_ROLLER = 25; //
	public static final int CHENJIU_WIN_JACKPOT = 26; //
	public static final int CHENJIU_WIN_GOLD_MINE = 27; //
	public static final int CHENJIU_WIN_BIG_DOG = 28; //
	public static final int CHENJIU_WIN_JERICHO = 29; // 黑桃J和2
	public static final int CHENJIU_WIN_SOPRANO = 30; // 黑桃4和方块k
	public static final int CHENJIU_WIN_THREE_CROWD = 31; //
	public static final int CHENJIU_WIN_FAMILY_POT = 32; //
	public static final int CHENJIU_WIN_FLOP_BLUFF = 33; //
	public static final int CHENJIU_WIN_RIVER_BLUFF = 34; //
	public static final int CHENJIU_WIN_AK_FULL_HOUSE = 35; // AAAKK
	public static final int CHENJIU_WIN_QS_FULL_JS = 36; // QQQJJ
	public static final int CHENJIU_WIN_FULL_HOUSE = 37; // 三条一对
	public static final int CHENJIU_WIN_KING_CRAB = 38; // 方块k和黑桃3
	public static final int CHENJIU_WIN_ASHTRAY = 39; // 方块A和黑桃3
	public static final int CHENJIU_WIN_MAVERICK = 40; // 5黑桃
	public static final int CHENJIU_WIN_DIAMONDS = 41; // 5方块
	public static final int CHENJIU_WIN_BIG_HEARTED = 42; // 5红桃
	public static final int CHENJIU_WIN_CLUBS = 43; // 5梅花
	public static final int CHENJIU_LOSE_BAD_BEAT = 44; //
	public static final int CHENJIU_LOSE_SUPER_BAD_BEAT = 45; // 顺子输
	public static final int CHENJIU_LOSE_BROKEN_HEART = 46; // 5红桃输
	public static final int CHENJIU_LOSE_BROKEN_HOME = 47; // 三带二输
	public static final int CHENJIU_WIN_CHEVY = 48; // 黑桃5 红桃7
	public static final int CHENJIU_WIN_GOLD_RUSH = 49; // 梅花4红桃9
	public static final int CHENJIU_WIN_TURN_BLUFF = 50; //
	public static final int CHENJIU_WIN_THREE_SONS = 51; // 三条J
	public static final int CHENJIU_WIN_BEAST = 52; // 三条6
	public static final int CHENJIU_WIN_CRABS = 53; //
	public static final int CHENJIU_WIN_RABBIT = 54; //
	public static final int CHENJIU_WIN_DONKEY = 55; // 7和2
	public static final int CHENJIU_WIN_HAPPY_MEAL = 56; // 对9对6
	public static final int CHENJIU_WIN_OLDSMOBILE = 57; // 9红桃和8梅花
	public static final int CHENJIU_WIN_ANY_TWO_PAIR = 58; // 250次两对
	public static final int CHENJIU_WIN_QUADS = 59; //
	public static final int CHENJIU_WIN_ANY_SETS10 = 60; // 10次三条A
	public static final int CHENJIU_WIN_FULL_HOUSE15 = 61; // 15次三带二
	public static final int CHENJIU_WIN_FLUSH15 = 62; // 15次同花
	public static final int CHENJIU_WIN_STRAIGHT25 = 63; // 25次顺子
	public static final int CHENJIU_WIN_ANY_SETS25 = 64; // 25次三条A
	public static final int CHENJIU_WIN_ACES25 = 65; // 25次红A
	public static final int CHENJIU_WIN_TWO_PAIRS50 = 66; // 50次两对
	public static final int CHENJIU_WIN_ANY_POKETS50 = 67; //
	public static final int CHENJIU_WIN_ANY_FLUSH50 = 68; // 50次同花
	public static final int CHENJIU_WIN_FULL_HOUSE50 = 69; // 50次三带二
	public static final int CHENJIU_WIN_ANY_QUADS50 = 70; //
	public static final int CHENJIU_WIN_ANY_POCKETS100 = 71; //
	public static final int CHENJIU_WIN_FULL_HOUSE100 = 72; // 100次三带二
	public static final int CHENJIU_WIN_ROCKETS150 = 73; //
	public static final int CHENJIU_WIN_ANY_STRAIGHT150 = 74; // 150 顺子
	public static final int CHENJIU_WIN_ANY_FLUSH = 75; // 50次同花
	public static final int CHENJIU_WIN_ANY_SETS200 = 76; // 200次三条A
	public static final int CHENJIU_WIN_KINGS25 = 77; // 25次红K

	public static final String[] CHENJIU_NAME_LIST =
	{ "红A一对", "红K一对", "红Q一对", "Win low rocket", "皇家同花顺", "梅花 2到6同花顺", "10~A顺子",
			"6~10顺子 ", "方块A和K", "梅花J 方块k", "Win any quads", "4条A", "4条K",
			"任意4条", "红桃J黑桃A", "梅花k和A", "10和Q是红桃", "K和Q 是梅花", "方块Q和红桃J",
			"黑k梅花J", "10和2", "Win hellmuth", "Win", "黑桃7方块4",
			"Win high Roller", "Win jackpot", "Win gold Mine", "Win big dog",
			"黑桃J和2", "黑桃4和方块k", "Win three crowd", "Win family pot",
			"Win flop bluff", "Win river bluff", "AAAKK", "QQQJJ", "三条一对",
			"方块k和黑桃3", "方块A和黑桃3", "5黑桃", "5方块", "5红桃", "5梅花", "Lose bad beat",
			"顺子输", "5红桃输", "三带二输", "黑桃5 红桃7", "梅花4红桃9", "Win turn bluff",
			"三条J", "三条6", "Win crabs", "Win rabbit", "7和2", "对9对6", "9红桃和8梅花",
			"250次两对", "Win quads", "10次三条A", "15次三带二", "15次同花", "25次顺子",
			"25次三条A", "25次红A", "50次两对", "50次Win any pokets", "50次同花", "50次三带二",
			"50次Win any quads", "50次Win any pockets", "100次三带二",
			"50次Win rockets", "150次顺子", "50次同花", "200次三条A", "25次红K", };

	public static HashMap<Integer, AchieveInfo> achieveInfo = new HashMap<Integer, AchieveInfo>();

	public void init()
	{
		achieveInfo.put(1, new AchieveInfo(0, 3000));
		achieveInfo.put(2, new AchieveInfo(0, 2000));
		achieveInfo.put(3, new AchieveInfo(0, 1000));
		achieveInfo.put(4, new AchieveInfo(0, 1000));
		achieveInfo.put(5, new AchieveInfo(500, 0));
		achieveInfo.put(6, new AchieveInfo(0, 90000));
		achieveInfo.put(7, new AchieveInfo(20, 0));
		achieveInfo.put(8, new AchieveInfo(0, 20000));
		achieveInfo.put(11, new AchieveInfo(0, 36000));
		achieveInfo.put(12, new AchieveInfo(60, 0));
		achieveInfo.put(13, new AchieveInfo(0, 60000));
		achieveInfo.put(35, new AchieveInfo(80, 0));
		achieveInfo.put(36, new AchieveInfo(0, 80000));
		achieveInfo.put(37, new AchieveInfo(15, 35000));
		achieveInfo.put(40, new AchieveInfo(0, 5000));
		achieveInfo.put(41, new AchieveInfo(0, 5000));
		achieveInfo.put(42, new AchieveInfo(5, 0));
		achieveInfo.put(43, new AchieveInfo(5, 0));
		achieveInfo.put(51, new AchieveInfo(0, 8000));
		achieveInfo.put(52, new AchieveInfo(10, 0));
		achieveInfo.put(59, new AchieveInfo(0, 300000));
		achieveInfo.put(60, new AchieveInfo(100, 0));

		achieveInfo.put(61, new AchieveInfo(0, 225000));
		achieveInfo.put(62, new AchieveInfo(150, 0));
		achieveInfo.put(63, new AchieveInfo(0, 125000));
		achieveInfo.put(64, new AchieveInfo(0, 250000));
		achieveInfo.put(65, new AchieveInfo(100, 0));
		achieveInfo.put(66, new AchieveInfo(0, 8000));
		achieveInfo.put(67, new AchieveInfo(50, 0));
		achieveInfo.put(68, new AchieveInfo(500, 0));
		achieveInfo.put(69, new AchieveInfo(0, 600000));
		achieveInfo.put(70, new AchieveInfo(0, 1800000));
		achieveInfo.put(71, new AchieveInfo(100, 0));
		achieveInfo.put(72, new AchieveInfo(0, 1200000));
		achieveInfo.put(67, new AchieveInfo(100, 0));
	}

	public class AchieveInfo
	{
		private int gold;
		private int point;

		AchieveInfo(int gold, int point)
		{
			this.gold = gold;
			this.point = point;

		}

		public int gold()
		{
			return gold;
		}

		public int point()
		{
			return point;
		}
	}
}
