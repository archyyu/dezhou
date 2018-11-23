package com.yl.testcase;

import java.util.HashMap;

import com.yl.Global.ConstList;
import com.yl.httpLogic.PlayerManager;

import sun.misc.BASE64Encoder;

public class testData
{

	public static BASE64Encoder encode = new BASE64Encoder();

	public static String PostHtmlContent(int CommandType, int CommandIndex,
			int otherType, String cmd)
	{

		HashMap<Integer, Object> commandList = new HashMap<Integer, Object>();

		HashMap<Integer, String> userManage = new HashMap<Integer, String>();
		userManage.put(0, "register");
		userManage.put(1, "registerupdate");
		userManage.put(2, "userlogin");
		userManage.put(3, "passwordupdate");
		userManage.put(4, "uinfo");
		userManage.put(5, "picUpdate");
		userManage.put(6, "logout");

		HashMap<Integer, String> roomlist = new HashMap<Integer, String>();
		roomlist.put(0, "list");
		roomlist.put(1, "detail");
		roomlist.put(2, "join");
		roomlist.put(3, "add");
		roomlist.put(4, "delete");

		HashMap<Integer, HashMap<String, String>> bet = new HashMap<Integer, HashMap<String, String>>();
		HashMap<String, String> CommonCmd = new HashMap<String, String>();
		HashMap<String, String> OtherCmd = new HashMap<String, String>();
		HashMap<String, String> ChallengeCmd = new HashMap<String, String>();
		HashMap<String, String> GoldCmd = new HashMap<String, String>();

		// 普通场指令--12个指令
		CommonCmd.put("roominfo", ConstList.CMD_ROOMINFO); // 00--查看桌面信息
		CommonCmd.put("look", ConstList.CMD_LOOK_CARD); // 01--看牌
		CommonCmd.put("follow", ConstList.CMD_FOLLOW_BET); // 02--跟注
		CommonCmd.put("fold", ConstList.CMD_DROP_CARD); // 03--弃牌
		CommonCmd.put("allin", ConstList.CMD_ALL_IN); // 04--全下
		CommonCmd.put("add", ConstList.CMD_ADD_BET); // 05--加注
		CommonCmd.put("sit", ConstList.CMD_SITDOWN); // 06--坐下
		CommonCmd.put("up", ConstList.CMD_STANDUP); // 07--站起
		CommonCmd.put("leave", ConstList.CMD_LEAVE); // 08--离开
		CommonCmd.put("create", ConstList.CMD_CREATROOM); // 09--创建房间
		CommonCmd.put("viewroom", ConstList.CMD_VIEWROOM); // 10--查看房间
		CommonCmd.put("pklevel", ConstList.CMD_GETFRL); // 11--获取第四轮 扑克等级
		bet.put(0, CommonCmd);

		// 交流指令--20个指令
		OtherCmd.put("tp", ConstList.CMD_TIMEPLAN); // 00--30分钟计划
		OtherCmd.put("addFriend", ConstList.CMD_ADDPKFRIEND); // 01--添加牌友
		OtherCmd.put("freshFriend", ConstList.CMD_FLUSHPKFRIEND);// 02--刷新牌友
		OtherCmd.put("acceptF", ConstList.CMD_RECPKFRIEND); // 03--接受牌友
		OtherCmd.put("refuseF", ConstList.CMD_REFUSEFRIEND); // 04--拒绝牌友
		OtherCmd.put("freshAchi", ConstList.CMD_FLUSHACH); // 05--刷新成就
		OtherCmd.put("buyDaoju", ConstList.CMD_BUYDJ); // 06--买道具
		OtherCmd.put("sendDaoju", ConstList.CMD_SENDDJ); // 07--赠送道具
		OtherCmd.put("exchange", ConstList.CMD_EXGOLDANDBET); // 08--兑换金币和筹码
		OtherCmd.put("useDaoju", ConstList.CMD_USEDJ); // 09--使用道具
		OtherCmd.put("daojuList", ConstList.CMD_MYDJ); // 10--道具列表
		OtherCmd.put("topMsg", ConstList.CMD_TOPMSG); // 11--消息置顶
		OtherCmd.put("delFriend", ConstList.CMD_DELFRIEND); // 12--删除牌友
		OtherCmd.put("monitor2CommonArea", ConstList.CMD_C_PTQ); // 13--监听用户到达普通区
		OtherCmd.put("aUserInfo", ConstList.CMD_UINFO); // 14--单个用户信息
		OtherCmd.put("coldSpeedUp", ConstList.CMD_MPS); // 15--冷却加速
		OtherCmd.put("tmoneySendPlan", ConstList.CMD_SENDBET); // 16--每天赠送筹码的计划
		OtherCmd.put("checkIfCanStartGame", ConstList.CMD_CHECKSBOT); // 17--监听游戏满足开始条件
		OtherCmd.put("daojuStore", ConstList.CMD_STORE); // 18--道具商店
		OtherCmd.put("levelUp", ConstList.CMD_LEVELUP); // 19--等级提升
		bet.put(1, OtherCmd);

		// 挑战场--22个指令
		ChallengeCmd.put("startTZ", ConstList.CMD_FQTZ); // 00--发起挑战
		ChallengeCmd.put("startZf", ConstList.CMD_FQZF); // 01--发起征服
		ChallengeCmd.put("acceptTz", ConstList.CMD_RECTZ); // 02--接受挑战并进入挑战房间
		ChallengeCmd.put("refuseTz", ConstList.CMD_REFTZ); // 03--拒绝挑战
		ChallengeCmd.put("joinGame", ConstList.CMD_JOINGAME); // 04--加入游戏
		ChallengeCmd.put("roominfo", ConstList.CMD_C_ROOMINFO); // 05--获取桌面信息
		ChallengeCmd.put("startGame", ConstList.CMD_STARTGAME); // 06--开始游戏
		ChallengeCmd.put("look", ConstList.CMD_LOOK); // 07--看牌
		ChallengeCmd.put("follow", ConstList.CMD_FOLLOW); // 08--跟注
		ChallengeCmd.put("fold", ConstList.CMD_DROP); // 09--弃牌
		ChallengeCmd.put("allIn", ConstList.CMD_ALLIN); // 10--全下
		ChallengeCmd.put("add", ConstList.CMD_ADD); // 11--加注
		ChallengeCmd.put("tzOver", ConstList.CMD_C_TZOVER); // 12--挑战结束
		ChallengeCmd.put("back", ConstList.CMD_BACK); // 13--返回大厅
		ChallengeCmd.put("tzzList", ConstList.CMD_C_TZLIST); // 14--列举出可以挑战的对象
		ChallengeCmd.put("zfzList", ConstList.CMD_C_ZFLIST); // 15--列举擂台场可以征服的对象
		ChallengeCmd.put("zfMsgList", ConstList.CMD_C_ZFMSG); // 16--列举出自己征服的信息
		ChallengeCmd.put("zfOver", ConstList.CMD_C_ZFOVER); // 17--征服比赛结束
		ChallengeCmd.put("cancel", ConstList.CMD_CANCEL); // 18--取消
		ChallengeCmd.put("roundOver", ConstList.CMD_C_ROUNDOVER); // 19--每一轮结束
		ChallengeCmd.put("whoTurn", ConstList.CMD_C_WHOTURN); // 20--轮到谁！
		ChallengeCmd.put("overAssignTmoney", ConstList.CMD_C_DBT); // 21--结束分配筹码
		bet.put(2, ChallengeCmd);

		// 金币场--9个指令
		GoldCmd.put("roominfo", ConstList.CMD_GD_ROOMINFO); // 00--查看桌面信息
		GoldCmd.put("look", ConstList.CMD_GD_LOOK_CARD); // 01--看牌
		GoldCmd.put("follow", ConstList.CMD_GD_FOLLOW_BET); // 02--跟注
		GoldCmd.put("fold", ConstList.CMD_GD_DROP_CARD); // 03--弃牌
		GoldCmd.put("allIn", ConstList.CMD_GD_ALL_IN); // 04--全下
		GoldCmd.put("add", ConstList.CMD_GD_ADD_BET); // 05--加注
		GoldCmd.put("sit", ConstList.CMD_GD_SITDOWN); // 06--坐下
		GoldCmd.put("up", ConstList.CMD_GD_STANDUP); // 07--站起
		GoldCmd.put("leave", ConstList.CMD_GD_LEAVE); // 08--离开
		bet.put(3, GoldCmd);

		String Title[][] = new String[][]
		{
				{ "用户管理---注册", "用户管理---更新资料", "用户管理---登陆", "用户管理---密码重置",
						"用户管理---信息查看", "用户管理---头像修改", "用户管理---用户登出", },
				{ "房间管理---列表", "房间管理---房间详情", "房间管理---进入", "房间管理---创建房间",
						"房间管理---删除房间" },
				{ "牌局管理---普通", "牌局管理---用户交流", "房间管理---挑战", "牌局管理---金币指令" },
				{ "管理列表---总入口", "百宝箱---注册通知接口", "百宝箱---充值接口" },

		};

		commandList.put(0, userManage);
		commandList.put(1, roomlist);
		commandList.put(2, bet);
		String[] commandTypeList = new String[]
		{ "userManage", "roomlist", "bet", "entry" };

		String header = "<?xml version='1.0' encoding='utf-8'?>\n"
				+ "<!DOCTYPE html PUBLIC '-//WAPFORUM//DTD XHTML Mobile 1.0//EN' 'http://www.wapforum.org/DTD/xhtml-mobile10.dtd'>\n"
				+ "<html xmlns='http://www.w3.org/1999/xhtml'>\n" + "<head>\n"
				+ "<title>"
				+ Title[CommandType][CommandIndex]
				+ "</title>\n"
				+ "<meta http-equiv='Content-Type' content='application/xhtml+xml; charset=UTF-8'/>\n"
				+ "<meta http-equiv='Cache-Control' content='no-cache'/>\n"
				+ "<style type='text/css'>\n"
				+ "*{font-family:Arial,Helvetica,sans-serif,宋体;}\n"
				+ "body{margin:0;padding:0;}.b_wrap{font-size:medium;text-align:left;line-height:1.5em;}.sel{margin-left:5px}\n"
				+ "a {color: #003ca9;text-decoration:none;}\n"
				+ ".logo{padding:0; text-align:left}\n"
				+ ".logo img{ vertical-align:middle}\n"
				+ ".logo a{padding: 0 0 0 5px;}\n"
				+ ".so{font-size:medium}\n"
				+ ".search_key{vertical-align:middle;font-size:medium;background-color:white;border:1px solid #acc1d8;height:26px;margin-right:0px;}\n"
				+ ".search_button_m{font-size:small;vertical-align:middle;text-align: center;height:26px;margin-left:0px;border:1px #acc1d8 solid;border-left:0px;}\n"
				+ ".search_button{font-size:medium;margin-right:6px;padding:0px 1px;text-decoration:none;text-align: center;cursor:auto;color: #003ca9;border: 0px;background-color: white;}\n"
				+ "#search form input{"
				+ "-webkit-appearance:none;-webkit-border-radius:0px;-webkit-box-align: center;-webkit-box-sizing: border-box;}\n"
				+ ".st_title{background-color: #f0efef;border-top: 1px solid #dfdfdf;border-bottom:1px solid #b8b8b8;height:30px;line-height:30px;color: #003ca9;}\n"
				+ ".st_title div:first-child{border-top:1px solid #fff;}\n"
				+ ".st_title a {color: #003ca9;padding-left:5px;}\n"
				+ ".st_title span{color: #003ca9;padding-left:5px;}\n"
				+ ".st{margin-top:8px;margin-bottom:8px;line-height:1.5em;text-align:justify}\n"
				+ ".st div span{font-size:medium;color:#676767;margin:0 0 0 5px}\n"
				+ ".top{text-align:left;font-size:medium;}.top a{color:#4ca520;}\n"
				+ ".ft{text-align: left;line-height:1.4em;margin-top:15px}\n"
				+ ".ad{background: none repeat scroll 0 0 #f4f4f4;margin: 1px;padding: 2px 0;}\n"
				+ ".ft span {color:#5e5f5f}\n" + "</style>\n" + "</head>\n";

		String SubCommandName = "";
		if (CommandType == 0 || CommandType == 1)
			SubCommandName = ((HashMap<Integer, String>) commandList
					.get(CommandType)).get(CommandIndex);
		else if (CommandType == 2)
			SubCommandName = CommandIndex + "";

		String Body = "";
		if (CommandType == 0 && otherType == 0)// 用户管理
		{
			Body += "<body>\n"
					+ "<div class='sel' id='search'>\n"
					+ "<form action='?"
					+ encode.encode((commandTypeList[CommandType] + "|" + SubCommandName)
							.getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";

			if (SubCommandName.equals("register"))

				Body += "昵称：<input type='text'   name='name' class='search_key'/>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "密码：<input type='text' name='password'  class='search_key' value='xy200505'/><br/>\n"
						+ "邮箱：<input type='text' name='email' class='search_key' value='bond@ok123.mobi'/><br/>\n"
						+ "性别：<input type='text' name='gendar'  class='search_key' value='男'/><br/>\n"
						+ "ＩＤ：<input type='text' name='uid'  class='search_key' value='"
						+ (PlayerManager.CreateUserId() - 1)
						+ "'/><br/>\n"
						+ "生日：<input type='text' name='birthday'  class='search_key' value='1976-05-14'/><br/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "</form>\n"
						+ "*************************************<br/>\n"
						+ "*************自动注册，快速进入*******<br/>\n"
						+ "<form action='?"
						+ encode.encode((commandTypeList[CommandType] + "|" + SubCommandName)
								.getBytes())
						+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "<input type='hidden' name='auto' size='1' value='yes'/>\n"
						+ "</form>\n"
						+ "*************************************<br/>\n"
						+ "*************用户充值*****************<br/>\n"
						+ "<form action='/bizcontrol/ChargeRequest' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "用户ｉｄ：<input type='text' name='uid'  lass='search_key' value=''/><br/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "手机号码：<input type='text' name='mobile'  lass='search_key' value='13667202367'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "充值数目：<input type='text' name='cn'  lass='search_key' value='2000'/><br/>\n"
						+ "</form>\n"
						+ "*************************************<br/>\n"
						+ "*************购买道具*****************<br/>\n"
						+ "<form action='/bizcontrol/BuyToolRequest' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "用户ｉｄ：<input type='text' name='uid'  lass='search_key' value=''/><br/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "手机号码：<input type='text' name='mobile'  lass='search_key' value='13667202367'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "道具ｉｄ：<input type='text' name='djid'  lass='search_key' value='10'/><br/>\n"
						+ "注：目前开放有效的道具id为：108,112,113,57,4,其他id购买如果提示失败，可能是联调时没有开放，测试时需要注意。<br/>\n"
						+ "</form>\n<br />"

						+ "*************查询余额*****************<br/>\n"
						+ "<form action='/bizcontrol/QueryBalRequest' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "用户ｉｄ：<input type='text' name='uid'  lass='search_key' value=''/><br/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "手机号码：<input type='text' name='mobile'  lass='search_key' value='13667202367'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "</form>\n"
						+ "*************充值明细*****************<br/>\n"
						+ "<form action='/bizcontrol/QueryChargeRecRequest' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "用户ｉｄ：<input type='text' name='uid'  lass='search_key' value=''/><br/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "手机号码：<input type='text' name='mobile'  lass='search_key' value='13667202367'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "开始时间：<input type='text' name='Startdate'  lass='search_key' value='20120101'/><br/>\n"
						+ "截止时间：<input type='text' name='Enddate'  lass='search_key' value='20120228'/><br/>\n"
						+ "</form>\n"

						+ "*************消费明细*****************<br/>\n"
						+ "<form action='bizcontrol/QueryConsumeRecRequest' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "用户ｉｄ：<input type='text' name='uid'  lass='search_key' value=''/><br/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "手机号码：<input type='text' name='mobile'  lass='search_key' value='13667202367'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "指定某月：<input type='text' name='OneMonth'  lass='search_key' value='201202'/><br/>\n"
						+ "查询范围：<input type='text' name='queryrange'  lass='search_key' value='1'/>（值为1/2/3中之一）<br/>\n"
						+ "查询类型：<input type='text' name='Type'  lass='search_key' value='3'/>（值为3/5/13中之一）<br/>\n"
						+ "</form>\n"

						+ "*************消费明细*****************<br/>\n"
						+ "<form action='/PublicTestCase' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='submit' name='ch_i' value='submit' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "处理用户数：<input type='text' name='dnum'  lass='search_key' value='100'/><br/>\n"
						+ "用户数为需要批量处理的用户人数。<br/>\n"
						+ "处理的类别：<input type='text' name='dtype'  lass='search_key' value='0'/><br/>\n"
						+ "处理的类别：0：充值；1：查询余额；2：购买道具；3：查询消费记录。<br/>\n"
						+ "用户创建时：<input type='text' name='ctime'  lass='search_key' value='2012-04-23'/><br/>\n"
						+ "用户创建时用于决定处理哪一天注册的用户。格式为YYYY-MM-DD<br/>\n"
						+ "道具的类别：<input type='text' name='djid'  lass='search_key' value='1'/><br/>\n"
						+ "随机道具吗：<input type='text' name='ifRandDj'  lass='search_key' value='yes'/><br/>\n"
						+ "道具的类别仅在消费道具时有效。范围为1-130<br/>\n"
						+ "充值的金额：<input type='text' name='money'  lass='search_key' value='100'/><br/>\n"
						+ "随机金额吗：<input type='text' name='ifRandmoney'  lass='search_key' value='yes'/><br/>\n"
						+ "老用户级别：<input type='text' name='userGrade'  lass='search_key' value='2'/><br/>\n"
						+ "老用户级别：0：外部公测用户，不做处理；1：一般内部公测用户，一次性定制35-40%；2：二次性订购用户,一次定制用户的65-70%；"
						+ "3：多次定制用户,二次定制用户的65-70%。<br/>\n" + "</form>\n"

						+ "</div>\n";

			else if (SubCommandName.equals("registerupdate"))
			{
				Body += "昵称：<input type='text'   name='name' class='search_key'/>\n"
						+ "邮箱：<input type='text' name='email'  class='search_key' value='bond@ok123.mobi'/><br/>\n"
						+ "性别：<input type='text' name='gendar'  class='search_key' value='男'/><br/>\n"
						+ "ＩＤ：<input type='text' name='uid'  class='search_key' value='"
						+ (PlayerManager.CreateUserId() - 1)
						+ "'/><br/>\n"
						+ "生日：<input type='text' name='birthday'  class='search_key' value='1976-05-14'/><br/>\n"
						+ "住址：<input type='text' name='address'  class='search_key' value=''/><br/>\n"
						+ "手机：<input type='text' name='mobile'   class='search_key' value=''/><br/>\n"
						+ "会话Key：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "旧密码：<input type='text' name='op'  lass='search_key' value=''/><br/>\n"
						+ "新密码：<input type='text' name='np'  lass='search_key' value=''/><br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n"
						+ "</form>\n" + "</div>\n";
			}
			else if (SubCommandName.equals("userlogin"))
			{
				Body += "昵称：<input type='text'   name='name' class='search_key'/>\n"
						+ "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n"
						+ "密码：<input type='text' name='password'  class='search_key' value=''/><br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "</form>\n"
						+ "***************************<br />"
						+ "<form action='/scriptName' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n"
						+ "<input type='hidden' name='ismobile'  value='yes'/><br/>\n"
						+ "用户伪码：<input type='text' name='userid'  lass='search_key' value='42226138'/><br/>\n"
						+ "手机号码：<input type='text' name='mobile'  lass='search_key' value='13667202367'/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "是否重置密码：<input type='text' name='resetPwd'  lass='search_key' value='yes'/><br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n"
						+ "</form>\n"

						+ "</div>\n";
			}
			else if (SubCommandName.equals("passwordupdate"))
			{
				Body += "邮箱号：<input type='text'   name='email' class='search_key'/>\n"
						+ "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n"
						+ "新密码：<input type='text' name='password'  class='search_key' value=''/><br/>\n"
						+ "伪ＫＥＹ：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "</form>\n" + "</div>\n";
			}
			else if (SubCommandName.equals("uinfo"))
			{
				Body += "自己的uid：<input type='text'   name='uid' class='search_key'/>\n"
						+ "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n"
						+ "目标uid：<input type='text' name='cuid'  class='search_key' value=''/><br/>\n"
						+ "会话Key：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "</form>\n" + "</div>\n";
			}
			else if (SubCommandName.equals("picUpdate"))
			{
				Body += "uid：<input type='text'   name='uid' class='search_key'/>\n"
						+ "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n"
						+ "picID：<input type='text' name='pic'  class='search_key' value=''/><br/>\n"
						+ "会话Key：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "</form>\n" + "</div>\n";
			}
			else if (SubCommandName.equals("logout"))
			{
				Body += "uid：<input type='text'   name='uid' class='search_key'/>\n"
						+ "<input type='submit' name='cmd'  value='logout' class='search_button_m'/>\n<br/>\n"
						+ "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n"
						+ "会话Key：<input type='text' name='key'  lass='search_key' value='"
						+ ConstList.debugUserKey
						+ "'/><br/>\n"
						+ "</form>\n"
						+ "</div>\n";
			}

			Body += "<div>\n<a href='?"
					+ encode.encode(("testcase|0|0").getBytes())
					+ "'>用户注册</a>&nbsp;&nbsp;<a href='?"
					+ encode.encode(("testcase|0|1").getBytes())
					+ "'>资料修改</a>\n" + "    <a href='?"
					+ encode.encode(("testcase|0|2").getBytes())
					+ "'>用户登陆</a>&nbsp;&nbsp;<a href='?"
					+ encode.encode(("testcase|0|3").getBytes())
					+ "'>密码重置</a>\n" + "    <a href='?"
					+ encode.encode(("testcase|0|4").getBytes())
					+ "'>用户信息</a>&nbsp;&nbsp;<a href='?"
					+ encode.encode(("testcase|0|5").getBytes())
					+ "'>修改头像</a>\n" + "    <a href='?"
					+ encode.encode(("testcase|0|6").getBytes())
					+ "'>用户登出</a>\n</div>\n";
			Body += "</body>\n";
		}
		else if (CommandType == 1 && otherType == 0)
		{
			String[] Command = new String[]
			{
					(commandTypeList[CommandType] + "|" + ((HashMap<Integer, String>) commandList
							.get(CommandType)).get(0)),
					(commandTypeList[CommandType] + "|" + ((HashMap<Integer, String>) commandList
							.get(CommandType)).get(1)),
					(commandTypeList[CommandType] + "|" + ((HashMap<Integer, String>) commandList
							.get(CommandType)).get(2)),
					(commandTypeList[CommandType] + "|" + ((HashMap<Integer, String>) commandList
							.get(CommandType)).get(3)),
					(commandTypeList[CommandType] + "|" + ((HashMap<Integer, String>) commandList
							.get(CommandType)).get(4))

			};
			ConstList.config.logger.info(Command[0]);
			ConstList.config.logger.info(Command[1]);
			ConstList.config.logger.info(Command[2]);
			ConstList.config.logger.info(Command[3]);
			ConstList.config.logger.info(Command[4]);
			Body += "<body>";
			Body += "<h>#############################房间列表#############################</h>\n";
			Body += "<form action='?"
					+ encode.encode((Command[0]).getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户Id：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间类型：<input type='text' name='rt'   class='search_key' value='rg'/><br/>\n";
			Body += "大盲注上限：<input type='text' name='bb'   class='search_key' value='50'/><br/>\n";
			Body += "大盲注下限：<input type='text' name='sb'   class='search_key' value='1'/><br/>\n";
			Body += "是否只显示在线人数：<input type='text' name='s'   class='search_key' value='1'/><br/>\n";
			Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";

			Body += "<h>#############################房间详情#############################</h>\n";
			Body += "<form action='?"
					+ encode.encode((Command[1]).getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户Id：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间Id：<input type='text' name='roomid'   class='search_key' value='2'/><br/>\n";
			Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";

			Body += "<h>#############################进入房间#############################</h>\n";

			Body += "<form action='?"
					+ encode.encode((Command[2]).getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间ｉｄ：<input type='text' name='roomid'  class='search_key' value='2'/><br/>\n";
			Body += "房间名称：<input type='text' name='name'   class='search_key' value='r12'/><br/>\n";
			Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
			Body += "<input type='hidden' name='log' size='20' value='1'/>\n<br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";
			Body += "<h>#############################创建房间#############################</h>";

			Body += "<form action='?"
					+ encode.encode((Command[3]).getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='showname'   class='search_key' value='输入名字'/><br/>\n";
			Body += "最大赌注：<input type='text' name='bbet'   class='search_key' value='2'/><br/>\n";
			Body += "最小赌注：<input type='text' name='sbet'   class='search_key' value='1'/><br/>\n";
			Body += "最大买入：<input type='text' name='maxbuy'   class='search_key' value='200'/><br/>\n";
			Body += "最小买入：<input type='text' name='mixbuy'   class='search_key' value='50'/><br/>\n";
			Body += "房间类型：<input type='text' name='roomtype'   class='search_key' value='pri'/>rg、pre之一<br/>\n";
			Body += "<input type='submit' name='cmd'  value='create' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "</form>\n";

			Body += "<h>#############################删除房间#############################</h>";

			Body += "<form action='?"
					+ encode.encode((Command[4]).getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='name'   class='search_key' value='r12'/><br/>\n";
			Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";

			Body += "</body>\n";

		}
		else if (CommandType == 2 && otherType == 0)
		{
			if (CommandIndex == 0) // 普通场指令
			{
				for (String key : CommonCmd.keySet())
				{
					if (CommonCmd.get(key).equals(cmd))
					{
						Body += "<body>\n";
						Body += "<form action='?"
								+ encode.encode(("bet|" + cmd).getBytes())
								+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
						Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
						Body += "房间ｉｄ：<input type='text' name='rid'   class='search_key' value='2'/><br/>\n";
						Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
						Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
								+ ConstList.debugUserKey + "'/><br/>\n";
						Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
						Body += "</form>\n";
						Body += "</body>";
						break;
					}
				}
			}
			else if (CommandIndex == 1) // 其他指令
			{
				for (String key : CommonCmd.keySet())
				{
					if (CommonCmd.get(key).equals(cmd))
					{
						Body += "<body>\n";
						Body += "<form action='?"
								+ encode.encode(("bet|" + cmd).getBytes())
								+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
						Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
						Body += "房间ｉｄ：<input type='text' name='rid'   class='search_key' value='2'/><br/>\n";
						Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
						Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
								+ ConstList.debugUserKey + "'/><br/>\n";
						Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
						Body += "</form>\n";
						Body += "</body>";
						break;
					}
				}

			}
			else if (CommandIndex == 2) // 挑战场指令
			{
				for (String key : CommonCmd.keySet())
				{
					if (CommonCmd.get(key).equals(cmd))
					{
						Body += "<body>\n";
						Body += "<form action='?"
								+ encode.encode(("bet|" + cmd).getBytes())
								+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
						Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
						Body += "房间ｉｄ：<input type='text' name='rid'   class='search_key' value='2'/><br/>\n";
						Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
						Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
								+ ConstList.debugUserKey + "'/><br/>\n";
						Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
						Body += "</form>\n";
						Body += "</body>";
						break;
					}
				}

			}
			else if (CommandIndex == 3) // 金币场指令
			{
				for (String key : CommonCmd.keySet())
				{
					if (CommonCmd.get(key).equals(cmd))
					{
						Body += "<body>";
						Body += "<form action='?"
								+ encode.encode(("bet|" + cmd).getBytes())
								+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
						Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
						Body += "房间ｉｄ：<input type='text' name='rid'   class='search_key' value='2'/><br/>\n";
						Body += "<input type='submit' name='ch_i'  value='submit' class='search_button_m'/>\n<br/>\n";
						Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
								+ ConstList.debugUserKey + "'/><br/>\n";
						Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
						Body += "</form>\n";
						Body += "</body>";
						break;
					}
				}

			}

		}
		else if (otherType == 1)
		{
			Body += "<body>\n";
			Body += "<a href='?" + encode.encode(("testcase|0|0").getBytes())
					+ "'>用户管理</a><br />\n";
			Body += "<a href='?" + encode.encode(("testcase|1|0").getBytes())
					+ "'>房间管理</a><br />\n";

			Body += "<a href='?" + encode.encode(("page|1").getBytes())
					+ "'>普通场</a><br />\n";
			Body += "<a href='?" + encode.encode(("page|2").getBytes())
					+ "'>用户交流指令</a><br />\n";
			Body += "<a href='?" + encode.encode(("page|3").getBytes())
					+ "'>挑战场指令</a><br />\n";
			Body += "<a href='?" + encode.encode(("page|4").getBytes())
					+ "'>金币场指令</a><br />\n";
			Body += "<a href='?" + encode.encode(("page|5").getBytes())
					+ "'>消息队列指令</a><br />\n";
			Body += "<a href='?" + encode.encode(("page|6").getBytes())
					+ "'>其他指令（统计、版本更新）</a><br />\n";

			Body += "<h>#############################牌局管理--普通场指令#############################</h>\n";
			Body += "<form action='?"
					+ encode.encode("bet|common".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='r1'/><br/>\n";
			Body += "座位ID：<input type='text' name='sid'    class='search_key' value='1'/><br/>\n";
			Body += "筹码数：<input type='text' name='cb'    class='search_key' value='500'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";

			Body += "房间信息CMD_ROOMINFO<input type='submit' name='cmd'  value='"
					+ cmdList[0] + "' class='search_button_m'/>\n<br/>\n";
			Body += "快速进入<input type='submit' name='cmd'  value='quick' class='search_button_m'/>\n<br/>\n";
			Body += "看牌CMD_LOOK_CARD<input type='submit' name='cmd'  value='"
					+ cmdList[1] + "' class='search_button_m'/>\n<br/>\n";
			Body += "跟牌CMD_FOLLOW_BET<input type='submit' name='cmd'  value='"
					+ cmdList[2] + "' class='search_button_m'/>\n<br/>\n";
			Body += "弃牌CMD_DROP_CARD<input type='submit' name='cmd'  value='"
					+ cmdList[3] + "' class='search_button_m'/>\n<br/>\n";
			Body += "全下CMD_ALL_IN<input type='submit' name='cmd'  value='"
					+ cmdList[4] + "' class='search_button_m'/>\n<br/>\n";
			Body += "加注CMD_ADD_BET<input type='submit' name='cmd'  value='"
					+ cmdList[5] + "' class='search_button_m'/>\n<br/>\n";
			Body += "坐下CMD_SITDOWN<input type='submit' name='cmd'  value='"
					+ cmdList[6] + "' class='search_button_m'/>\n<br/>\n";
			Body += "站起CMD_STANDUP<input type='submit' name='cmd'  value='"
					+ cmdList[7] + "' class='search_button_m'/>\n<br/>\n";
			Body += "离开CMD_LEAVE<input type='submit' name='cmd'  value='"
					+ cmdList[8] + "' class='search_button_m'/>\n<br/>\n";
			Body += "创建房间CMD_CREATROOM<input type='submit' name='cmd'  value='"
					+ cmdList[9] + "' class='search_button_m'/>\n<br/>\n";
			Body += "查看房间CMD_VIEWROOM<input type='submit' name='cmd'  value='"
					+ cmdList[10] + "' class='search_button_m'/>\n<br/>\n";
			Body += "牌面等级CMD_GETFRL<input type='submit' name='cmd'  value='"
					+ cmdList[11] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";
			Body += "</body>";
		}
		else if (otherType == 2)
		{
			Body += "<body>\n";
			Body += "<h>#############################牌局管理--玩家交流指令#############################</h>\n";
			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "时间分钟数：<input type='text' name='t'    class='search_key' value=''/><br/>\n";
			Body += "30分钟计划CMD_TIMEPLAN<input type='submit' name='cmd'  value='"
					+ cmdList[12] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10001'/><br/>\n";
			Body += "我的ｉｄ：<input type='text' name='mid'   class='search_key' value='10001'/><br/>\n";
			Body += "朋友ｉｄ：<input type='text' name='fid'   class='search_key' value='10002'/><br/>\n";
			Body += "附加信息：<input type='text' name='cn'    class='search_key' value='加你为好友，你同意吗？'/><br/>\n";
			Body += "添加牌友CMD_ADDPKFRIEND<input type='submit' name='cmd'  value='"
					+ cmdList[13] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "牌友列表CMD_FLUSHPKFRIEND<input type='submit' name='cmd'  value='"
					+ cmdList[14] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10001'/><br/>\n";
			Body += "我的ｉｄ：<input type='text' name='mid'   class='search_key' value='10001'/><br/>\n";
			Body += "朋友ｉｄ：<input type='text' name='fid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "接受牌友CMD_RECPKFRIEND<input type='submit' name='cmd'  value='"
					+ cmdList[15] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10001'/><br/>\n";
			Body += "我的ｉｄ：<input type='text' name='mid'   class='search_key' value='10001'/><br/>\n";
			Body += "朋友ｉｄ：<input type='text' name='fid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "拒绝牌友CMD_REFUSEFRIEND<input type='submit' name='cmd'  value='"
					+ cmdList[16] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "刷新成就CMD_FLUSHACH<input type='submit' name='cmd'  value='"
					+ cmdList[17] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "目标ｉｄ：<input type='text' name='fuid'   class='search_key' value='10001'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "道具类型：<input type='text' name='dt'    class='search_key' value='1'/><br/>\n";
			Body += "道具数量：<input type='text' name='num'   class='search_key' value='10'/><br/>\n";
			Body += "买道具CMD_BUYDJ<input type='submit' name='cmd'  value='"
					+ cmdList[18] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "赠送UID： <input type='text' name='sid'    class='search_key' value='10002'/><br/>\n";
			Body += "接收UID： <input type='text' name='rid'    class='search_key' value='10001'/><br/>\n";
			Body += "赠送类型： <input type='text' name='st'    class='search_key' value='1'/>(1为送道具，2为送筹码)<br/>\n";
			Body += "我的座位号： <input type='text' name='msid'    class='search_key' value='1'/><br/>\n";
			Body += "接收座位号： <input type='text' name='rsid'    class='search_key' value='2'/><br/>\n";
			Body += "送的筹码数： <input type='text' name='bet'    class='search_key' value='1'/>赠送类型为2时有效<br/>\n";
			Body += "道具类型： <input type='text' name='dt'    class='search_key' value='1'/>赠送类型为1时有效<br/>\n";
			Body += "道具数目： <input type='text' name='dn'    class='search_key' value='1'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "赠送道具CMD_SENDDJ<input type='submit' name='cmd'  value='"
					+ cmdList[19] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "兑换类型：<input type='text' name='et'    class='search_key' value='1'/>1 ：金币换筹码 2：筹码换金币<br/>\n";
			Body += "换金币数：<input type='text' name='eg'    class='search_key' value='10'/><br/>\n";
			Body += "换筹码数：<input type='text' name='eb'    class='search_key' value='1000'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "兑换金币筹码CMD_EXGOLDANDBET<input type='submit' name='cmd'  value='"
					+ cmdList[20] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "赠送UID： <input type='text' name='sid'    class='search_key' value='10002'/><br/>\n";
			Body += "接收UID： <input type='text' name='rid'    class='search_key' value='10001'/><br/>\n";
			Body += "我的座位号： <input type='text' name='stid'    class='search_key' value='1'/><br/>\n";
			Body += "接收座位号： <input type='text' name='rstid'    class='search_key' value='2'/><br/>\n";
			Body += "道具类型： <input type='text' name='dt'    class='search_key' value='1'/><br/>\n";
			Body += "礼物类型： <input type='text' name='pg'    class='search_key' value='p'/>值为“p”、“g”之一<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "使用道具CMD_USEDJ<input type='submit' name='cmd'  value='"
					+ cmdList[21] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "查询类型：<input type='text' name='type'   class='search_key' value='all'/>值为“inUse”或“all”之一<br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "道具列表CMD_MYDJ<input type='submit' name='cmd'  value='"
					+ cmdList[22] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "全局ｉｄ：<input type='text' name='id'   class='search_key' value=''/><br/>\n";
			Body += "道具ｉｄ：<input type='text' name='djid'   class='search_key' value=''/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "道具列表CMD_INUSE<input type='submit' name='cmd'  value='equipdj' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "置顶类型：<input type='text' name='top'   class='search_key' value='c'/>c、w之一<br/>\n";
			Body += "礼物类型：<input type='text' name='pg'   class='search_key' value='p'/>p、g之一<br/>\n";
			Body += "消息内容：<input type='text' name='msg'    class='search_key' value='请输入......'/><br/>\n";
			Body += "用  户   名：<input type='text' name='un'    class='search_key' value=''/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "消息置顶CMD_TOPMSG<input type='submit' name='cmd'  value='"
					+ cmdList[23] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "自己ｉｄ：<input type='text' name='muid'    class='search_key' value='10002'/><br/>\n";
			Body += "朋友ｉｄ：<input type='text' name='fuid'    class='search_key' value='10001'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "删除牌友CMD_DELFRIEND<input type='submit' name='cmd'  value='"
					+ cmdList[24] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "房间类型：<input type='text' name='rt'    class='search_key' value='pt'/><br/>\n";
			Body += "监听玩家到普通场CMD_C_PTQ<input type='submit' name='cmd'  value='"
					+ cmdList[25] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "用户昵称：<input type='text' name='nm'    class='search_key' value=''/><br/>\n";
			Body += "用户肖像：<input type='text' name='pic'    class='search_key' value=''/><br/>\n";
			Body += "总筹码数：<input type='text' name='tm'    class='search_key' value=''/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "查看单个用户信息CMD_UINFO<input type='submit' name='cmd'  value='"
					+ cmdList[26] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "加速类型：<input type='text' name='ty'    class='search_key' value='tz'/>tz/zf/mp之一<br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "冷却加速CMD_MPS<input type='submit' name='cmd'  value='"
					+ cmdList[27] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "每天赠送筹码的计划CMD_SENDBET<input type='submit' name='cmd'  value='"
					+ cmdList[28] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "是否满足开始游戏条件CMD_CHECKSBOT<input type='submit' name='cmd'  value='"
					+ cmdList[29] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "道具商店CMD_STORE<input type='submit' name='cmd'  value='"
					+ cmdList[30] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "<form action='?"
					+ encode.encode("bet|other".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "座位ｉｄ：<input type='text' name='sid'    class='search_key' value='1'/><br/>\n";
			Body += "等级提升CMD_LEVELUP<input type='submit' name='cmd'  value='"
					+ cmdList[31] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n-----------------------------------------------------\n\n";

			Body += "</body>";
		}
		else if (otherType == 3)
		{
			Body += "<body>\n";
			Body += "<h>#############################牌局管理--挑战场指令#############################</h>\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "挑战ｉｄ：<input type='text' name='zuid'    class='search_key' value='10002'/><br/>\n";
			Body += "被挑ｉｄ：<input type='text' name='buid'    class='search_key' value='10001'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "发起挑战CMD_FQTZ<input type='submit' name='cmd'  value='"
					+ cmdList[32] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='admin'/><br/>\n";
			Body += "征服ｉｄ：<input type='text' name='zuid'    class='search_key' value='10002'/><br/>\n";
			Body += "被征ｉｄ：<input type='text' name='buid'    class='search_key' value='10001'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "发起征服CMD_FQZF<input type='submit' name='cmd'  value='"
					+ cmdList[33] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "挑战ｉｄ：<input type='text' name='zuid'    class='search_key' value='10002'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "被挑ｉｄ：<input type='text' name='buid'    class='search_key' value='10001'/><br/>\n";
			Body += "房间类型：<input type='text' name='rt'    class='search_key' value='tz'/>tz（挑战）/zf（征服）值之一<br/>\n";
			Body += "被挑ｉｄ：<input type='text' name='rid'    class='search_key' value='10001'/><br/>\n";

			Body += "接受挑战并进入挑战房间CMD_RECTZ<input type='submit' name='cmd'  value='"
					+ cmdList[34] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "拒绝挑战CMD_REFTZ<input type='submit' name='cmd'  value='"
					+ cmdList[35] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "加入游戏CMD_JOINGAME<input type='submit' name='cmd'  value='"
					+ cmdList[36] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "获取桌面信息CMD_C_ROOMINFO<input type='submit' name='cmd'  value='"
					+ cmdList[37] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "开始游戏CMD_STARTGAME<input type='submit' name='cmd'  value='"
					+ cmdList[38] + "' class='search_button_m'/>\n<br/>\n";

			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "看牌CMD_LOOK<input type='submit' name='cmd'  value='"
					+ cmdList[39] + "' class='search_button_m'/>\n<br/>\n";

			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "跟注CMD_FOLLOW<input type='submit' name='cmd'  value='"
					+ cmdList[40] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "弃牌CMD_DROP<input type='submit' name='cmd'  value='"
					+ cmdList[41] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "全下CMD_ALLIN<input type='submit' name='cmd'  value='"
					+ cmdList[42] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "加注CMD_ADD<input type='submit' name='cmd'  value='"
					+ cmdList[43] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "挑战结束CMD_C_TZOVER<input type='submit' name='cmd'  value='"
					+ cmdList[44] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "返回大厅CMD_BACK<input type='submit' name='cmd'  value='"
					+ cmdList[45] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "列举出可以挑战的对象CMD_C_TZLIST<input type='submit' name='cmd'  value='"
					+ cmdList[46] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "列举擂台场可以征服的对象CMD_C_ZFLIST<input type='submit' name='cmd'  value='"
					+ cmdList[47] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "列举出自己征服的信息CMD_C_ZFMSG<input type='submit' name='cmd'  value='"
					+ cmdList[48] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "征服比赛结束CMD_C_ZFOVER<input type='submit' name='cmd'  value='"
					+ cmdList[49] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "取消CMD_CANCEL<input type='submit' name='cmd'  value='"
					+ cmdList[50] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "每一轮结束CMD_C_ROUNDOVER<input type='submit' name='cmd'  value='"
					+ cmdList[51] + "' class='search_button_m'/>\n<br/>\n";

			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "轮到谁！CMD_C_WHOTURN<input type='submit' name='cmd'  value='"
					+ cmdList[52] + "' class='search_button_m'/>\n<br/>\n";

			Body += "</form>\n------------------------------------------------\n";
			Body += "<form action='?"
					+ encode.encode("bet|challenge".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "结束分配筹码CMD_C_DBT<input type='submit' name='cmd'  value='"
					+ cmdList[53] + "' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n";
			Body += "</body>";

		}
		else if (otherType == 4)
		{
			Body += "<body>\n";
			Body += "<h>------------------------------牌局管理--金币场指令------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("bet|gold".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='r12'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "查看桌面信息CMD_GD_ROOMINFO<input type='submit' name='cmd'  value='"
					+ cmdList[54] + "' class='search_button_m'/>\n<br/>\n";
			Body += "看牌CMD_GD_LOOK_CARD<input type='submit' name='cmd'  value='"
					+ cmdList[55] + "' class='search_button_m'/>\n<br/>\n";
			Body += "跟注CMD_GD_FOLLOW_BET<input type='submit' name='cmd'  value='"
					+ cmdList[56] + "' class='search_button_m'/>\n<br/>\n";
			Body += "弃牌CMD_GD_DROP_CARD<input type='submit' name='cmd'  value='"
					+ cmdList[57] + "' class='search_button_m'/>\n<br/>\n";
			Body += "全下CMD_GD_ALL_IN<input type='submit' name='cmd'  value='"
					+ cmdList[58] + "' class='search_button_m'/>\n<br/>\n";
			Body += "加注CMD_GD_ADD_BET<input type='submit' name='cmd'  value='"
					+ cmdList[59] + "' class='search_button_m'/>\n<br/>\n";
			Body += "坐下CMD_GD_SITDOWN<input type='submit' name='cmd'  value='"
					+ cmdList[60] + "' class='search_button_m'/>\n<br/>\n";
			Body += "站起CMD_GD_STANDUP<input type='submit' name='cmd'  value='"
					+ cmdList[61] + "' class='search_button_m'/>\n<br/>\n";
			Body += "离开CMD_GD_LEAVE<input type='submit' name='cmd'  value='"
					+ cmdList[62] + "' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";
			Body += "</body>";
		}
		else if (otherType == 5)
		{
			Body += "<body>\n";
			Body += "<h>------------------------------牌局定时器------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("TimerBet|rand".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='r12'/><br/>\n";
			Body += "LINKID：<input type='text' name='ld'    class='search_key' value='-1'/><br/>\n";
			Body += "时间戳：：<input type='text' name='ts'    class='search_key' value='"
					+ (System.currentTimeMillis() - 5000) + "'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "重发<input type='submit' name='cmd'  value='reSend' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "下一个<input type='submit' name='cmd'  value='sendNext' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n";

			Body += "<h>------------------------------房间或世界聊天信息提交------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("pubMsg|rand".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='r12'/><br/>\n";
			Body += "消息类型：<input type='text' name='mt'    class='search_key' value='0'/><br/>\n";
			Body += "消息内容：<input type='text' name='msg'    class='search_key' value='请输入...'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "提交<input type='submit' name='cmd'  value='pubMsg' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n";

			Body += "<h>------------------------------私人聊天信息提交------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("pubMsg|rand".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "对方ｉｄ：<input type='text' name='fuid'   class='search_key' value='10001'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='r12'/><br/>\n";
			Body += "消息类型：<input type='text' name='mt'    class='search_key' value='0'/><br/>\n";
			Body += "消息内容：<input type='text' name='msg'    class='search_key' value='请输入...'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "提交<input type='submit' name='cmd'  value='priMsg' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n";

			Body += "<h>------------------------------世界信息列表------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("pubMsg|rand".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='r12'/><br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "提交<input type='submit' name='cmd'  value='world' class='search_button_m'/>\n<br/>\n";
			Body += "</form>\n";
			Body += "</body>";
		}
		else if (otherType == 6)
		{
			Body += "<body>\n";
			Body += "<h>------------------------------排行榜------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("other|rank".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "房间名称：<input type='text' name='rn'    class='search_key' value='r12'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "rank<input type='submit' name='cmd'  value='rank' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";

			Body += "<h>------------------------------ 版本更新------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("other|version".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "新版本号：<input type='text' name='gameVer'    class='search_key' value='2.0.0'/><br/>\n";
			Body += "系统密码:<input type='text' name='passwd'    class='search_key' value='defenceIllegalAccess'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "获取版本<input type='submit' name='cmd'  value='getVer' class='search_button_m'/>\n<br/>\n";
			Body += "设置版本<input type='submit' name='cmd'  value='setVer' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";

			Body += "<h>------------------------------ 教程完送金钱------------------------------</h>\n";
			Body += "<form action='?"
					+ encode.encode("other|sendMoney".getBytes())
					+ "' method='post' name='search' id='search-form' style='margin:0px;padding:0px;'>\n";
			Body += "用户ｉｄ：<input type='text' name='uid'   class='search_key' value='10002'/><br/>\n";
			Body += "<input type='hidden' name='compressFlag' size='1' value='youAreBeAuthrizedToBeUnCompressed'/>\n";
			Body += "赠送类型：<input type='text' name='type'  value='teach' class='search_button_m'/>\n<br/>\n";
			Body += "设置版本<input type='submit' name='cmd'  value='sendMoney' class='search_button_m'/>\n<br/>\n";
			Body += "会话Key：<input type='text' name='key'  lass='search_key' value='"
					+ ConstList.debugUserKey + "'/><br/>\n";
			Body += "</form>\n";

			Body += "</body>";
		}
		String tail = "</html>\n";
		return header + Body + tail;
	}

	public static String[] cmdList = new String[]
	{ ConstList.CMD_ROOMINFO, ConstList.CMD_LOOK_CARD,
			ConstList.CMD_FOLLOW_BET, ConstList.CMD_DROP_CARD,
			ConstList.CMD_ALL_IN, ConstList.CMD_ADD_BET, ConstList.CMD_SITDOWN,
			ConstList.CMD_STANDUP, ConstList.CMD_LEAVE,
			ConstList.CMD_CREATROOM, ConstList.CMD_VIEWROOM,
			ConstList.CMD_GETFRL,

			ConstList.CMD_TIMEPLAN, ConstList.CMD_ADDPKFRIEND,
			ConstList.CMD_FLUSHPKFRIEND, ConstList.CMD_RECPKFRIEND,
			ConstList.CMD_REFUSEFRIEND, ConstList.CMD_FLUSHACH,
			ConstList.CMD_BUYDJ, ConstList.CMD_SENDDJ,
			ConstList.CMD_EXGOLDANDBET, ConstList.CMD_USEDJ,
			ConstList.CMD_MYDJ, ConstList.CMD_TOPMSG, ConstList.CMD_DELFRIEND,
			ConstList.CMD_C_PTQ, ConstList.CMD_UINFO, ConstList.CMD_MPS,
			ConstList.CMD_SENDBET, ConstList.CMD_CHECKSBOT,
			ConstList.CMD_STORE, ConstList.CMD_LEVELUP,

			ConstList.CMD_FQTZ, ConstList.CMD_FQZF, ConstList.CMD_RECTZ,
			ConstList.CMD_REFTZ, ConstList.CMD_JOINGAME,
			ConstList.CMD_C_ROOMINFO, ConstList.CMD_STARTGAME,
			ConstList.CMD_LOOK, ConstList.CMD_FOLLOW, ConstList.CMD_DROP,
			ConstList.CMD_ALLIN, ConstList.CMD_ADD, ConstList.CMD_C_TZOVER,
			ConstList.CMD_BACK, ConstList.CMD_C_TZLIST, ConstList.CMD_C_ZFLIST,
			ConstList.CMD_C_ZFMSG, ConstList.CMD_C_ZFOVER,
			ConstList.CMD_CANCEL, ConstList.CMD_C_ROUNDOVER,
			ConstList.CMD_C_WHOTURN, ConstList.CMD_C_DBT,

			ConstList.CMD_GD_ROOMINFO, ConstList.CMD_GD_LOOK_CARD,
			ConstList.CMD_GD_FOLLOW_BET, ConstList.CMD_GD_DROP_CARD,
			ConstList.CMD_GD_ALL_IN, ConstList.CMD_GD_ADD_BET,
			ConstList.CMD_GD_SITDOWN, ConstList.CMD_GD_STANDUP,
			ConstList.CMD_GD_LEAVE };

}
