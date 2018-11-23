package com.yl.container;

public class UserVariable
{

	public static String TYPE_NULL = "x";
	public static String TYPE_BOOLEAN = "b";
	public static String TYPE_NUMBER = "n";
	public static String TYPE_STRING = "s";
	protected String value;
	protected String type;

	public UserVariable(String value, String type)
	{
		this.value = value;
		this.type = type;
	}

	public UserVariable(String value)
	{
		setValue(value);
	}

	public UserVariable(int value)
	{
		setValue(value);
	}

	public UserVariable(double value)
	{
		setValue(value);
	}

	public UserVariable(boolean value)
	{
		setValue(value);
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}

	public void setValue(String value, String type)
	{
		this.value = value;
		this.type = type;
	}

	public void setValue(String value)
	{
		this.value = value;
		if (value == null)
			type = TYPE_NULL;
		else
			type = TYPE_STRING;
	}

	public void setValue(int value)
	{
		this.value = String.valueOf(value);
		type = TYPE_NUMBER;
	}

	public void setValue(double value)
	{
		this.value = String.valueOf(value);
		type = TYPE_NUMBER;
	}

	public void setValue(boolean value)
	{
		this.value = String.valueOf(value);
		type = TYPE_BOOLEAN;
	}

	public String getValue()
	{
		return value;
	}

	public int getIntValue()
	{
		return Integer.parseInt(value);
	}

	public double getDoubleValue()
	{
		return Double.parseDouble(value);
	}

	public boolean getBooleanValue()
	{
		return Boolean.parseBoolean(value);
	}

}