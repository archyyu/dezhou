package com.yl.Global;

import java.io.StringReader;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.yl.util.Config;
import com.yl.xload.XLoad;

public class ConstList
{

	public static Config config = new Config();

	private static Logger log = Logger.getLogger(ConstList.class);
	
	/**
	 *初始化db配置 
	 **/
	public static void initDbConfig()
	{
		try
		{
			byte[] bytes = XLoad.getResource(ConstList.dbConfigFileName);
			SAXReader reader = new SAXReader(); 
	    	StringReader read = new StringReader(new String(bytes));
	    	Document document = reader.read(read);
	    	Element root = document.getRootElement();
	    	
    		ConstList.LinkPoolSize = Integer.parseInt( root.element("dbpoolsize").getText() );
    		ConstList.gameVersion = root.element("gameVersion").getText();
    		ConstList.gameInstallurl = root.element("gameInstallurl").getText();
	    	
		}
		catch (Exception e)
		{
			log.error("decode db config error", e);
		}
	}
	
	public static String gameVersion = null;
	public static String gameInstallurl = null;
	
//	public static String HttpIp = null;
//	public static String HttpPort = null;
	
	public static int LinkPoolSize = 20;
	
	
	public static String debugUserKey = "094ca7ceff3ab0c6af27b98420493fb9";
	
	public static HashMap<Integer, String> blackWordList; // 黑话列表

	public static String httpConfigFileName = "session/httpserver/httpserver.xml";
	
	public static String dbConfigFileName = "session/version/version.xml";
	
	/**
	 * 
	 * 指向文件：session/config/common.xml
	 */
	public static String roomConfigFileName = "session/config/common.xml";

	/**
	 * 
	 * 黑名单文件：session/config/common.xml
	 */
	public static String BWConfigFileName = "session/global/blackWords.xml";

	/**
	 * 指向文件：session/roomlist/roomlist.xml
	 */
	public static String roomOutFileName = "session/roomlist/roomlist.xml";
	/**
	 * 指向文件：session/config/prop.xml
	 */
	public static String PropertyFileName = "session/config/prop.xml";
	/**
	 * 指向文件：session/global/userInfo.xml
	 */

	/**
	 * 指向文件：session/player/playerTemplate.xml
	 */
	public static String OneUserInfoFileTemplateName = "session/player/playerTemplate.xml";

	public static final String companName = "南昌鼎安科技有限公司";
	public static final String GameName = "梦幻德州扑克OL";
	public static final String GameType = "手机客户端网游";
	public static final String cpid = "710525";
	public static final String gameId = "652510052578";
	public static final String TechName = "辛元";
	public static final String TechPhone = "13407154199";
	public static final String TechEmail = "bond@ok123.mobi";
	public static final String IP = "112.25.14.117";
	public static final String SenderId = "760";
	public static final String DefaultSenderId = "202";
	public static final String ChannelId = "10002000";
	public static final String DefaultChannelId = "15077000";

	public static final String CMD_LOOK_CARD = "1"; // 看牌
	public static final String CMD_ADD_BET = "2"; // 加注
	public static final String CMD_FOLLOW_BET = "3"; // 跟注
	public static final String CMD_DROP_CARD = "4"; // 弃牌
	public static final String CMD_ALL_IN = "5"; // 全下
	public static final String CMD_SITDOWN = "6"; // 坐下
	public static final String CMD_STANDUP = "7"; // 站起
	public static final String CMD_LEAVE = "106"; // 离开大厅
	public static final String CMD_ROOMINFO = "9"; // 获取桌面信息
	
	public static final int DROPCARDTIMEEXPIRED = 16 * 1000;

	public static final String CMD_ROUND_OVER = "rover"; // 结束
	public static final String CMD_WHO_TURN = "wt"; // 轮到谁！
	public static final String CMD_SBOT = "sbot"; // 游戏发底牌
	public static final String CMD_DBT = "dbt"; // 结束分配筹码
	public static final String CMD_CREATROOM = "creatroom"; // 创建临时房间
	public static final String CMD_ADDROOM = "addroom"; // 添加房间
	public static final String CMD_DELROOM = "delroom"; // 创删除房间
	public static final String CMD_VIEWROOM = "viewroom"; // 预览房间
	public static final String CMD_TIMEPLAN = "tp"; // 30分钟计划
	public static final String CMD_ADDPKFRIEND = "apkf"; // 添加牌友
	public static final String CMD_FLUSHPKFRIEND = "fpkf"; // 刷新牌友
	public static final String CMD_RECPKFRIEND = "rpkf"; // 接受邀请
	public static final String CMD_REFUSEFRIEND = "ref"; // 拒绝牌友
	public static final String CMD_BUYDJ = "bdj"; // 购买道具
	public static final String CMD_SENDDJ = "sdj"; // 赠送礼品
	public static final String CMD_USEDJ = "udj"; // 使用道具
	public static final String CMD_EXGOLDANDBET = "edj"; // 兑换金币，筹码
	public static final String CMD_MYDJ = "mdg"; // 道具和礼品列表
	public static final String CMD_TOPMSG = "tmsg"; // 消息置顶
	public static final String CMD_DELFRIEND = "dlf"; // 删除牌友
	public static final String CMD_FQTZ = "fqtz"; // 发起挑战
	public static final String CMD_FQZF = "fqzf"; // 发起征服
	public static final String CMD_RECTZ = "rectz"; // 接受挑战并进入挑战房间
	public static final String CMD_REFTZ = "reftz"; // 拒绝挑战
	public static final String CMD_JOINGAME = "join"; // 加入游戏
	public static final String CMD_C_ROOMINFO = "rinfo"; // 获取桌面信息
	public static final String CMD_STARTGAME = "start"; // 开始游戏

	public static final String CMD_LOOK = "look"; // 看牌
	public static final String CMD_FOLLOW = "follow"; // 跟注
	public static final String CMD_DROP = "drop"; // 弃牌
	public static final String CMD_ALLIN = "allin"; // 全下
	public static final String CMD_ADD = "add"; // 加注

	public static final String CMD_C_ROUNDOVER = "crover"; // 每一轮结束
	public static final String CMD_C_WHOTURN = "cwt"; // 轮到谁！
	public static final String CMD_C_TZOVER = "tzover"; // 挑战结束
	public static final String CMD_BACK = "back"; // 返回大厅
	public static final String CMD_C_TZLIST = "tzl"; // 列举出可以挑战的对象
	public static final String CMD_C_ZFLIST = "zfl"; // 列举擂台场可以征服的对象
	public static final String CMD_C_ZFMSG = "zfm"; // 列举出自己征服的信息
	public static final String CMD_C_PTQ = "ptq"; // 监听用户进入普通区
	public static final String CMD_C_ZFOVER = "zfover"; // 征服比赛结束
	public static final String CMD_UINFO = "uinfo"; // 单个的用户信息
	public static final String CMD_MPS = "mps"; // 冷却加速
	public static final String CMD_LEVELUP = "lvlup"; // 升级
	public static final String CMD_CANCEL = "cancel"; // 取消
	public static final String CMD_GETFRL = "gfrl"; // 获取第四轮 扑克等级
	public static final String CMD_SENDBET = "sbet"; // 每天赠送4000筹码
	public static final String CMD_C_DBT = "cdbt"; // 结束分配筹码
	public static final String CMD_FLUSHACH = "rach"; // 刷新成就列表
	public static final String CMD_TZERROR = "tze"; // 挑战 错误信息
	public static final String CMD_CHECKSBOT = "csg"; // 是否满足游戏开始条件
	public static final String CMD_STORE = "store"; // 商店
	public static final String CMD_STOREMSG = "spg"; // 商店信息

	// 金币场命令集合
	public static final String CMD_GD_ROOMINFO = "gd_ri";
	public static final String CMD_GD_LOOK_CARD = "gd_lc";
	public static final String CMD_GD_FOLLOW_BET = "gd_fb";
	public static final String CMD_GD_DROP_CARD = "gd_dc";
	public static final String CMD_GD_ALL_IN = "gd_ai";
	public static final String CMD_GD_ADD_BET = "gd_ab";
	public static final String CMD_GD_SITDOWN = "gd_sd";
	public static final String CMD_GD_STANDUP = "gd_su";
	public static final String CMD_GD_LEAVE = "gd_lev";
	public static final String CMD_GD_LOST = "gd_lost";
	public static final String CMD_GD_VIEWROOM = "gd_vr";
	public static final String CMD_GD_ROUNDOVER = "gd_ro";
	public static final String CMD_GD_WHOTURN = "gd_wt";
	public static final String CMD_GD_DBT = "gd_dbt";
	public static final String CMD_GD_SBOT = "gd_sbot";
	/**
	 * 牌组状态
	 */
	public static final int GAME_STATE_CARD_LEVEL_9 = 19; // 皇家同花顺
	public static final int GAME_STATE_CARD_LEVEL_8 = 18; // 同花顺
	public static final int GAME_STATE_CARD_LEVEL_7 = 17; // 四条
	public static final int GAME_STATE_CARD_LEVEL_6 = 16; // 葫芦
	public static final int GAME_STATE_CARD_LEVEL_5 = 15; // 同花
	public static final int GAME_STATE_CARD_LEVEL_4 = 14; // 顺子
	public static final int GAME_STATE_CARD_LEVEL_3 = 13; // 三条
	public static final int GAME_STATE_CARD_LEVEL_2 = 12; // 两对
	public static final int GAME_STATE_CARD_LEVEL_1 = 11; // 一对
	public static final int GAME_STATE_CARD_LEVEL_0 = 10; // 高牌

	
	/**
	 *玩家游戏状态 
	 **/
	public static final int GAME_STATE_LOOK_CARD = 1; // 看牌
	public static final int GAME_STATE_ADD_BET = 2; // 加注
	public static final int GAME_STATE_FOLLOW_BET = 3; // 跟注
	public static final int GAME_STATE_DROP_CARD = 4; // 弃牌
	public static final int GAME_STATE_ALL_END = 5; // 全下
	public static final int GAME_STATE_SITDOWN = 6; // 坐下
	public static final int GAME_STATE_STANDUP = 7; // 站起
	public static final int GAME_STATE_LEAVE = 8; // 离开大厅
	public static final int GAME_STATE_ROOMINFO = 9; // 获取桌面信息

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
		private PlayerGameState(int v)
		{
			this.value = v;
		}
		
		public int value()
		{
			return this.value;
		}
	}
	
	/**
	 * 用户状态
	 */
	public static final int PLAYER_STATE_WAIT = 100; // 等待状态
	public static final int PLAYER_STATE_LEAVE = 106; // 离开游戏桌面
	public static final int PLAYER_STATE_LEAVEL = 106; // 离开游戏桌面
	public static final int PLAYER_STATE_PLAYER = 102; // 玩家状态
	public static final int PLAYER_STATE_BANKER = 103; // 游戏庄家
	public static final int PLAYER_STATE_SMALLBLIND = 104; // 游戏小盲注
	public static final int PLAYER_STATE_BIGBLIND = 105; // 游戏大盲注
	

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
		private PlayerCareerState(int v)
		{
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

	/**
	 * 房间类型
	 */
	public static final String RoomType_RG = "rg"; // 普通场
	public static final String RoomType_KS = "ks"; // 快速场
	public static final String RoomType_GD = "gd"; // 金币场

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
		private MessageType(int level,int expiredTime)
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
