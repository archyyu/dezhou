package com.yl.entity;

import java.util.HashMap;

public class BaseUinfo
{
	private boolean isOline = false;
	private boolean isPlaying = false;
	private int payGold = 0;
	private int rmoney = 0;
	private int tmoney = 0;
	private int exprience = 0;
	private int gold = 0;
	private int winTzCount = 0;
	private int lostTzCount = 0;
	private int tributeBet = 0;
	private int achievement = 0;
	private String uid;
	private String roomType = "pt";
	private String name = "";
	private String pic = "";
	private String roomKey = "";
	private int roomid = -1;
	private long baceMPoint = 0;
	private long coolDownTime = 0;
	private int tzMatchPoint = 0;
	private int zfMatchPoint = 0;
	private int cMatchPoint = 0;
	private String cdtype = ""; // zf tz
	private String ltStatus = "";
	private String dateStr = "";
	private HashMap<String, HashMap<String, Integer>> propmap = new HashMap<String, HashMap<String, Integer>>();
	private int firstRoundBet = 0; // 第一轮押注筹码
	private int secondRoundBet = 0; // 第二轮押注筹码
	private int thirdRoundBet = 0; // 第三轮押注筹码
	private int fourthRoundBet = 0; // 第四轮押注筹码
	private int pkleave = 0;
}
