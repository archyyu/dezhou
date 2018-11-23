package com.yl.web.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.yl.web.po.TinyUser;

public interface UserDao
{
	public void saveUser(TinyUser u);

	public Map checkUser(String uid, String name, String pic);

	public HashMap<String, HashMap> userList();

	// 近日玩家
	public HashMap<String, HashMap> recUlist();

	// 谁赢过
	public HashMap<String, HashMap> whoWin();

	// 光荣榜
	public HashMap<String, HashMap> guangRB();

	// 数据统计
	public HashMap countlist();
}
