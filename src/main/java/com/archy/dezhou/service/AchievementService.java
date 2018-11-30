package com.archy.dezhou.service;

import java.util.HashMap;
import java.util.List;

import com.archy.dezhou.entity.Puke;

public interface AchievementService
{
	List getAchievement(HashMap<Integer, Puke> pkmap);
}
