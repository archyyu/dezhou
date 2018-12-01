
package com.archy.dezhou.backlet;

/**
 *@author archy_yu 
 **/

import java.util.Map;
import java.util.HashMap;

import com.archy.dezhou.backlet.base.IDataBacklet;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.User;

public class BackletKit
{
	private static Map<String,IDataBacklet> backlets = new HashMap<String,IDataBacklet>(); 
	
	public static void init()
	{
		backlets.put("userManage", new PlayerManageBacklet());
		backlets.put("roomlist", new RoomListBacklet());
		backlets.put("bet",new PukeLogicBacket());
		backlets.put("other", new OtherBacklet());
		backlets.put("scriptName", new ScriptNameBacklet());
		backlets.put("pubMsg", new PubMsgBacklet());
	}
	
	public static IDataBacklet getBacklet(String cmd)
	{
		return backlets.get(cmd);
	}
	
	public static byte[] SimpleObjectXml(byte[] xmlByteA)
	{
		String tmpString = new String(xmlByteA);
		tmpString = tmpString.replace("var", "v");
		tmpString = tmpString.replace("t='s'", "");
		tmpString = tmpString.replace("t='a'", "");
		tmpString = tmpString.replace("t='n'", "");
		tmpString = tmpString.replace("t='b'", "");
		tmpString = tmpString.replace("dataObj", "d");
		tmpString = tmpString.replace("obj", "o");
		xmlByteA =  tmpString.getBytes();
		return xmlByteA;
	}
	
	public static String errorXml(String errorinfo) 
	{
		return "<?xml version='1.0' encoding='UTF-8'?>\n" + "<d><error>"
		+ errorinfo + "</error></d>\n";
	}

	public static String infoXml(String info) 
	{
		return "<?xml version='1.0' encoding='UTF-8'?>\n" + "<d><info>" + info
		+ "</info></d>\n";
	}
	
	public static byte[] WorldListXml(User u)
	{
		byte[] xmlByteA = "N".getBytes();
		return xmlByteA;
	}
	
	public static String okXml(String Okinfo)
	{
		return "<?xml version='1.0' encoding='UTF-8'?>\n" + "<d><status>"
				+ Okinfo + "</status></d>\n";
	}
	
	public static String NullXml(String nullinfo)
	{
		return nullinfo;
	}
	
	public static ActionscriptObject parms2AsObj(Map<String,String> parms)
	{
		ActionscriptObject aObj = new ActionscriptObject();
		for (String key : parms.keySet())
		{
			if (key.equals("bet"))
			{
				aObj.putNumber("bet",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("id"))
			{
				aObj.putNumber("id",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("cb"))
			{
				aObj.putNumber("cb",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("dn"))
			{
				aObj.putNumber("dn",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("dt"))
			{
				aObj.putNumber("dt",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("et"))
			{
				aObj.putNumber("et",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("eg"))
			{
				aObj.putNumber("eg",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("eb"))
			{
				aObj.putNumber("eb",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("msid"))
			{
				aObj.putNumber("msid",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("num"))
			{
				aObj.putNumber("num",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("nsid"))
			{
				aObj.putNumber("nsid",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("rid"))
			{
				aObj.putNumber("rid",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("rstid"))
			{
				aObj.putNumber("rstid",
						Integer.parseInt(parms.get(key)));
			}

			else if (key.equals("sid"))
			{
				aObj.putNumber("sid",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("st"))
			{
				aObj.putNumber("st",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("stid"))
			{
				aObj.putNumber("stid",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("tm"))
			{
				aObj.putNumber("tm",
						Integer.parseInt(parms.get(key)));
			}

			else if (key.equals("rsid"))
			{
				aObj.putNumber("rsid",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("t"))
			{
				aObj.putNumber("t",
						Integer.parseInt(parms.get(key)));
			}
			else if (key.equals("compressFlag"))
			{
				aObj.put(key, parms.get(key));
			}
			else
			{
				aObj.put(key, parms.get(key));
			}
		}
		
		return aObj;
	}
	
}
