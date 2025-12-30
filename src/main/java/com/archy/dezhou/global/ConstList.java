package com.archy.dezhou.global;


public class ConstList
{

	public static String gameVersion = null;

	public static final String GAME_TYPE_TEXAS_HOLDEM = "texas_holden";

	public static final String CMD_LOOK_CARD = "1"; // 看牌
	public static final String CMD_ADD_BET = "2"; // 加注
	public static final String CMD_FOLLOW_BET = "3"; // 跟注
	public static final String CMD_DROP_CARD = "4"; // 弃牌
	public static final String CMD_ALL_IN = "5"; // 全下
	public static final String CMD_SITDOWN = "6"; // 坐下
	public static final String CMD_STANDUP = "7"; // 站起
	public static final String CMD_CHECK = "8";
	public static final String CMD_LEAVE = "106"; // 离开大厅
	public static final String CMD_ROOMINFO = "9"; // 获取桌面信息
	public static final String CMD_ROUND_OVER = "rover"; // 结束
	public static final String CMD_WHO_TURN = "wt"; // 轮到谁！
	public static final String CMD_SBOT = "sbot"; // 游戏发底牌
	public static final String CMD_DBT = "dbt"; // 结束分配筹码
	public static final String CMD_FLUSHACH = "rach"; // 刷新成就列表

    public enum CMD
    {
        CMD_LOOK_CARD("1"),
        CMD_ADD_BET("2"),
        CMD_FOLLOW_BET("3"),
        CMD_DROP_CARD("4"),
        CMD_ALL_IN("5"),
        CMD_SITDOWN("6"),
        CMD_STANDUP("7"),
        CMD_LEAVE("8"),
        CMD_ROOMINFO("9"),
        CMD_ROUND_OVER("10"),
        CMD_WHO_TURN("wt"),
        CMD_SBOT("sbot"),
        CMD_DBT("dbt"),
        CMD_FLUSHACH("rach"),
        CMD_END("end");
        private String cmd;
        CMD(String c)
        {
            this.cmd = c;
        }
        public String value()
        {
            return this.cmd;
        }
    }



    public static final int DROPCARDTIMEEXPIRED = 5 * 1000;


	public enum CardState
	{

        GAME_STATE_CARD_LEVEL_9(19),
        GAME_STATE_CARD_LEVEL_8(18),
        GAME_STATE_CARD_LEVEL_7(17),
        GAME_STATE_CARD_LEVEL_6(16),
        GAME_STATE_CARD_LEVEL_5(15),
        GAME_STATE_CARD_LEVEL_4(14),
        GAME_STATE_CARD_LEVEL_3(13),
        GAME_STATE_CARD_LEVEL_2(12),
        GAME_STATE_CARD_LEVEL_1(11),
        GAME_STATE_CARD_LEVEL_0(10),
        GAME_STATE_CARD_LEVEL_END(-1);

		private int value = 0;
		CardState(int v)
        {
            this.value = v;
        }
        public int value()
        {
            return this.value;
        }
	}


	public enum PlayerGameState
	{
		GAME_STATE_LOOK_CARD(1),
		GAME_STATE_ADD_BET(2),
		GAME_STATE_FOLLOW_BET(3),
		GAME_STATE_DROP_CARD(4),
		GAME_STATE_ALL_END(5),
		GAME_STATE_SITDOWN(6),
		GAME_STATE_STANDUP(7),
		GAME_STATE_LEAVE(8),
		GAME_STATE_ROOMINFO(9),
		
		PLAYER_STATE_WAIT(100),
		PLAYER_STATE_PLAYER(102);
		
		private int value = 0;
		PlayerGameState(int v)
		{
			this.value = v;
		}
		
		public int value()
		{
			return this.value;
		}
	}


	/**
	 * 玩家状态 
	 **/
	public enum PlayerCareerState
	{
		PLAYER_STATE_WAIT(100),
		PLAYER_STATE_PLAYER(102),
		PLAYER_STATE_BANKED(103),
		PLAYER_STATE_SMALLBLIND(104),
		PLAYER_STATE_BIGBLIND(105),
		PLAYER_STATE_LEAVE(106);
		
		private int value;
		PlayerCareerState(int v)
		{
			this.value = v;
		}
		public int value()
		{
			return this.value;
		}
	}

	/**
	 * round state
	*/
	public enum RoundState 
	{	

		Pre_Flop(1),
		Flop(2),
		Turn(3),
		River(4);

		private int value;
		RoundState(int v) {
			this.value = v;
		}
		public int value()
		{
			return this.value;
		}

	}
	
	
	public static final int MAXNUMPLAYEROFROOM = 8;

	/**
	 * 黑桃 > 红桃 > 梅花 > 方块
	 */
	public static String bt = "D"; // 黑桃
	public static String ht = "C"; // 红桃
	public static String mh = "B"; // 梅花
	public static String fk = "A"; // 方块

	public static final int level_9_ExpireTime = 20000; // 第9级优先级消息队列的过期时间为20秒
	public static final int level_8_ExpireTime = 40000; // 第8级优先级消息队列的过期时间为40秒
	public static final int level_7_ExpireTime = 80000; // 第7级优先级消息队列的过期时间为80秒
	public static final int level_6_ExpireTime = 160000; // 第6级优先级消息队列的过期时间为160秒
	public static final int level_5_ExpireTime = 360000; // 第5级优先级消息队列的过期时间为360秒
	public static final int level_4_ExpireTime = 1440000; // 第4级优先级消息队列的过期时间为1440秒，24分
	public static final int level_3_ExpireTime = 3600000; // 第3级优先级消息队列的过期时间为3600秒，1小时
	public static final int level_2_ExpireTime = 7200000; // 第2级优先级消息队列的过期时间为7200秒，2小时
	public static final int level_1_ExpireTime = 14400000; // 第1级优先级消息队列的过期时间为2小时，4小时
	public static final int level_0_ExpireTime = 86400000 * 7; // 第0级优先级消息队列的过期时间为7*24小时，7天
	
	public enum MessageType
	{
		MESSAGE_ZERO(0,86400000 * 7),
		MESSAGE_ONE(1,14400000),
		MESSAGE_TWO(2,7200000),
		MESSAGE_THREE(3,3600000),
		MESSAGE_FORE(4,1440000),
		MESSAGE_FIVE(5,360000),
		MESSAGE_SIX(6,160000),
		MESSAGE_SEVEN(7,80000),
		MESSAGE_EIGHT(8,40000),
		MESSAGE_NINE(9,20000);
		
		private int level = 0;
		private int expireTime = 0;
		MessageType(int level, int expiredTime)
		{
			this.level = level;
			this.expireTime = expiredTime;
		}
		
		public int getMessageLevel()
		{
			return this.level;
		}
		
		public int getMessageExpireTime()
		{
			return this.expireTime;
		}
		
	}
	

	public static String[] DebugUidList = new String[]
	{ "10009", "10010", "10086", "10085", "10090" };

}
