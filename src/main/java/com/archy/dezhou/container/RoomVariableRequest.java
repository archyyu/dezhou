package com.archy.dezhou.container;

/**
 * The RoomVariableRequest represent class represents a room variable in
 * requests to the server.
 * 
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#createRoom
 * @see it.gotoandplay.smartfoxclient.SmartFoxClient#setRoomVariables
 * 
 * @version 1.0.0
 * 
 * @author The gotoAndPlay() Team<br>
 *         <a
 *         href="http://www.smartfoxserver.com">http://www.smartfoxserver.com<
 *         /a><br>
 *         <a href="http://www.gotoandplay.it">http://www.gotoandplay.it</a><br>
 */
public class RoomVariableRequest extends SFSVariable
{
	private boolean isPrivate;
	private boolean persistent;

	/**
	 * 
	 * @param value
	 *            the variable value.
	 * @param type
	 *            the variable type.
	 */
	public RoomVariableRequest(String value, String type)
	{
		super(value, type);
		this.isPrivate = false;
		this.persistent = false;
	}

	/**
	 * 
	 * @param value
	 *            the variable value.
	 * @param type
	 *            the variable type.
	 * @param isPrivate
	 *            if the variable is private.
	 */
	public RoomVariableRequest(String value, String type, boolean isPrivate)
	{
		super(value, type);
		this.isPrivate = isPrivate;
		this.persistent = false;
	}

	/**
	 * 
	 * @param value
	 *            the variable value.
	 * @param type
	 *            the variable type.
	 * @param isPrivate
	 *            if the variable is private.
	 * @param persistent
	 *            if the variable is persistent.
	 */
	public RoomVariableRequest(String value, String type, boolean isPrivate,
			boolean persistent)
	{
		super(value, type);
		this.isPrivate = isPrivate;
		this.persistent = persistent;
	}

	/**
	 * Returns if the variable is private.
	 * 
	 * @return if the variable is private.
	 */
	public boolean isPrivate()
	{
		return isPrivate;
	}

	/**
	 * Sets if the variable is private.
	 * 
	 * @param isPrivate
	 *            if the variable is private.
	 */
	public void setPrivate(boolean isPrivate)
	{
		this.isPrivate = isPrivate;
	}

	/**
	 * Returns if the variable is persistent.
	 * 
	 * @return if the variable is persistent.
	 */
	public boolean isPersistent()
	{
		return this.persistent;
	}

	/**
	 * Sets if the variable is persistent.
	 * 
	 * @param persistent
	 *            if the variable is persistent.
	 */
	public void setPersistent(boolean persistent)
	{
		this.persistent = persistent;
	}
}
