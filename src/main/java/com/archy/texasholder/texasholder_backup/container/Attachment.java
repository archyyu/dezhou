package com.archy.texasholder.container;

import java.nio.ByteBuffer;

public class Attachment
{

	public Attachment()
	{
		secretKey = null;
		readBuffer = ByteBuffer.allocateDirect(ConfigData.MAX_MSG_LEN);
		msg = new StringBuffer();
		blueBoxed = false;
		isNPC = false;
	}

	public String getSecretKey()
	{
		return secretKey;
	}

	public void setSecretKey(String secretKey)
	{
		this.secretKey = secretKey;
	}

	public ByteBuffer readBuffer;
	public StringBuffer msg;
	private String secretKey;
	public boolean blueBoxed;
	public String bbSessionId;
	public boolean isNPC;
}
