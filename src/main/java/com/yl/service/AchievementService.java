package com.yl.service;

import java.util.HashMap;
import java.util.List;

import com.yl.entity.Puke;

public interface AchievementService
{
	public List getAchievement(HashMap<Integer, Puke> pkmap);
}
