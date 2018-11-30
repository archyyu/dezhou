package com.archy.dezhou.container;

/**
 * 
 * @author Lapo
 */
public class SFSVariable
{
	public static java.lang.String TYPE_NULL = "null";
	public static java.lang.String TYPE_BOOLEAN = "boolean";
	public static java.lang.String TYPE_NUMBER = "number";
	public static java.lang.String TYPE_STRING = "string";

	protected String value;
	protected String type;

	/**
	 * Default constructor
	 * 
	 * @param value
	 *            var value
	 * @param type
	 *            var type
	 */
	public SFSVariable(String value, String type)
	{
		this.value = value;
		this.type = type;
	}

	/**
	 * Set the variable type
	 * 
	 * @param type
	 *            the type
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Returns the variable type.
	 * 
	 * @return the variable type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * Set the variable value
	 * 
	 * @param value
	 *            the value
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Returns the variable value.
	 * 
	 * @return the variable value
	 */
	public String getValue()
	{
		return this.value;
	}
}
