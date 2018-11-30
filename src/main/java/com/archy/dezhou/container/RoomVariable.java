package com.archy.dezhou.container;

public class RoomVariable extends UserVariable
{

	private User owner; // 所有者
	private boolean persistent;// 是否持续的。
	private boolean priv; // 优先级

	public RoomVariable(String value, String type, User owner,
			boolean persistent, boolean priv)
	{
		super(value, type);
		this.owner = owner;
		this.persistent = persistent;
		this.priv = priv;
	}

	public boolean isPersistent()
	{
		return persistent;
	}

	public User getOwner()
	{
		return owner;
	}

	public boolean isPrivate()
	{
		return priv;
	}

	public void setPersistent(boolean b)
	{
		persistent = b;
	}

	public void setPrivate(boolean b)
	{
		priv = b;
	}

	public void setOwner(User who)
	{
		owner = who;
	}

	public boolean equals(Object o)
	{
		boolean b = false;
		RoomVariable rv = (RoomVariable) o;
		if (rv.getValue().equals(value) && rv.getType().equals(type)
				&& rv.isPersistent() == isPersistent()
				&& rv.isPrivate() == isPrivate())
			b = true;
		return b;
	}

	public String toString()
	{
		StringBuffer sb = (new StringBuffer("Var value: ")).append(value);
		sb.append(", priv: ").append(isPrivate()).append(", pers: ")
				.append(isPersistent());
		sb.append(", owner: ").append(owner);
		return sb.toString();
	}
}
