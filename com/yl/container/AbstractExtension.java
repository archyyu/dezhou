package com.yl.container;

import java.util.LinkedList;
import net.n3.nanoxml.IXMLElement;
import org.json.JSONException;
import org.json.JSONObject;
import com.yl.Global.ConstList;

public abstract class AbstractExtension
{

	protected String __zoneName;
	protected String __roomName;
	protected boolean __isActive;

	public AbstractExtension()
	{
		__isActive = true;
	}

	public void setOwner(String zoneName, String roomName)
	{
		__roomName = roomName;
		__zoneName = zoneName;
	}

	public String getOwnerZone()
	{
		return __zoneName;
	}

	public String getOwnerRoom()
	{
		return __roomName;
	}

	public void init()
	{
	}

	public void destroy()
	{
	}

	public boolean isActive()
	{
		return __isActive;
	}

	public void setActive(boolean b)
	{
		__isActive = b;
	}

	public void registerForEvents(String zoneName, String roomName)
	{
		setOwner(zoneName, roomName);
	}

	public void unRegister()
	{
	}

	public void trace(String msg)
	{
		StringBuffer sb = new StringBuffer("[ ");
		sb.append(getClass().getName()).append(" ]: ").append(msg);
		ConstList.config.logger.info(sb);
	}

	public IXMLElement loadConfig(String xmlFile)
	{
		return null;
	}

	public void handleRequest(String s, JSONObject jsonobject, User user, int i)
	{
	}

	public Object handleInternalRequest(Object params)
	{
		return null;
	}

	// sendResponse(response, -1, null, recipientList);
	public void sendResponse(ActionscriptObject ao, int fromRoom, User sender,
			LinkedList<?> recipients)
	{
		StringBuffer xmlMsg = (new StringBuffer(
				"<msg t='xt'><body action='xtRes' r='")).append(fromRoom)
				.append("'>");
		xmlMsg.append("<![CDATA[")
				.append(SFSObjectSerializer.serialize((SFSObject) ao))
				.append("]]>");
		xmlMsg.append("</body></msg>");

	}

	public void sendResponse(String params[], int fromRoom, User sender,
			LinkedList<?> recipients)
	{
		StringBuffer response = (new StringBuffer("|")).append("xt")
				.append("|");
		response.append(params[0]).append("|");
		response.append(fromRoom).append("|");
		for (int i = 1; i < params.length; i++)
			response.append(params[i]).append("|");
	}

	public void sendResponse(JSONObject jso, int fromRoom, User sender,
			LinkedList<?> recipients)
	{
		try
		{
			JSONObject jsoBody = new JSONObject();
			jsoBody.put("r", fromRoom);
			jsoBody.put("o", jso);
			JSONObject jsoMsg = new JSONObject();
			jsoMsg.put("t", "xt");
			jsoMsg.put("b", jsoBody);
		}
		catch (JSONException jsoEx)
		{
			ConstList.config.logger
					.error((new StringBuilder("Error creating JSON response: "))
							.append(jsoEx).toString());
		}
	}

	public void sendResponse(net.sf.json.JSONObject jso, int fromRoom,
			User sender, LinkedList<?> recipients)
	{
		try
		{
			net.sf.json.JSONObject jsoBody = new net.sf.json.JSONObject();
			jsoBody.put("r", Integer.valueOf(fromRoom));
			jsoBody.put("o", jso);
			net.sf.json.JSONObject jsoMsg = new net.sf.json.JSONObject();
			jsoMsg.put("t", "xt");
			jsoMsg.put("b", jsoBody);
		}
		catch (net.sf.json.JSONException jsoEx)
		{
			ConstList.config.logger
					.error((new StringBuilder("Error creating JSON response: "))
							.append(jsoEx).toString());
		}
	}
}
