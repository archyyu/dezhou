package com.archy.dezhou.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Buddy class represents the user buddy.
 * <p>
 * This class is used internally by the
 * {@link it.gotoandplay.smartfoxclient.SmartFoxClient} class; also, Buddy
 * objects are returned by various methods and events of the SmartFoxServer API.
 * </p>
 * 
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#buddyList
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#addBuddy
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#getBuddyByName
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#getBuddyById
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#getBuddyRoom
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#loadBuddyList
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#removeBuddy
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#sendBuddyPermissionResponse
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#setBuddyVariables
 * 
 * @version 1.0.0
 * 
 * @author The gotoAndPlay() Team<br>
 *         <a
 *         href="http://www.smartfoxserver.com">http://www.smartfoxserver.com<
 *         /a><br>
 *         <a href="http://www.gotoandplay.it">http://www.gotoandplay.it</a><br>
 */
public class Buddy
{
	private String name;
	private int id;
	private boolean online;
	private boolean blocked;
	private Map<String, String> variables;

	public Buddy()
	{
		variables = new ConcurrentHashMap<String, String>();
	}

	/**
	 * Returns the buddy name.
	 * 
	 * @return the buddy name.
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * Sets the buddy name.
	 * 
	 * @param name the buddy name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the buddy id.
	 * 
	 * @return the buddy id.
	 */
	public int getId()
	{
		return id;
	}

	/*
	 * Sets the buddy id.
	 * 
	 * @param id the buddy id.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Returns if the user is online or not.
	 * 
	 * @return if the user is online or not.
	 */
	public boolean isOnline()
	{
		return online;
	}

	/*
	 * Sets if the buddy is online.
	 * 
	 * @param online if the buddy is online.
	 */
	public void setOnline(boolean online)
	{
		this.online = online;
	}

	/**
	 * Returns if the user is blocked.
	 * 
	 * @return if the user is blocked.
	 */
	public boolean isBlocked()
	{
		return blocked;
	}

	/*
	 * Sets if the buddy is blocked.
	 * 
	 * @param blocked if the buddy is blocked.
	 */
	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
	}

	/**
	 * Returns the buddy variables.
	 * 
	 * <p>
	 * The buddy variables are represented but {@code Map} where the variable
	 * name is key and the variable value is value.
	 * </p>
	 * 
	 * @return buddy variables.
	 */
	public Map<String, String> getVariables()
	{
		return variables;
	}

	/*
	 * Sets the buddy variables.
	 * 
	 * <p> The buddy variables are represented but {@code Map} where the
	 * variable name is key and the variable value is value. </p>
	 * 
	 * @param variables the {@code Map} that represents the buddy variables.
	 */
	public void setVariables(Map<String, String> variables)
	{
		this.variables = new ConcurrentHashMap<String, String>(variables);
	}
}
