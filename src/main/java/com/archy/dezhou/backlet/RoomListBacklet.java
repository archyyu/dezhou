package com.archy.dezhou.backlet;

/**
 *@author archy_yu 
 **/

import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.room.Room;
import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.backlet.base.DataBacklet;
import io.netty.handler.codec.http.FullHttpResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.List;
import java.util.Map;

public class RoomListBacklet extends DataBacklet
{
	
	private final static String LIST = "list";
	private final static String JOIN = "join";
	private final static String LEAVE = "leave";
	
	
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,FullHttpResponse httpResponse)
	{
		byte[] xmlByteA = null;
		
		if(subCmd.equals(LIST))
		{
			String rt = parms.get("rt") == null ? "rg" : parms
					.get("rt");
			String bb = parms.get("bb") == null ? "-1" : parms
					.get("bb");
			String sb = parms.get("sb") == null ? "-1" : parms
					.get("sb");
			
			httpResponse.headers().set("cmd", "roomlist");
			httpResponse.headers().set("ts", "-1");
			httpResponse.headers().set("num", "0");
			
			xmlByteA = this.getRoomListFromMemory(rt, bb, sb).getBytes();
		}
		else if(subCmd.equals(JOIN))
		{
			String uid = parms.get("uid") == null ? "" : parms.get("uid");
			String roomname = parms.get("name") == null ? "" : parms.get("name");
			
			httpResponse.headers().set("cmd", "roomjoin");
			httpResponse.headers().set("ts", "-1");
			
			Room room = UserModule.getInstance().getRoomByName(roomname);
			Player user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
			
			if(room == null || user == null)
			{
				return BackletKit.errorXml("UserNotLogined").getBytes();
			}
			
			Room oldRoom = UserModule.getInstance().getRoom(user.getRoomId());
			if(oldRoom != null)
			{
				
//				if(oldRoom.getName().equals(roomname))
//				{
//					return BackletKit.infoXml("haveBeenEntered").getBytes();
//				}
				oldRoom.userLeave(user);
				
//				ConstList.config.logger.info(" leave oldroom: "  + oldRoom.getRoomId() +  " user:" +user);
			}
			
			if(room.isUserInRoom(user))
			{
				//TODO error
			}
			
			int ret = room.userJoin(user);
			user.setRoomId(room.getRoomId());
			if(ret == 0)
			{
				xmlByteA = BackletKit.okXml("UserEnterRoomOk").getBytes();
			}
			else
			{
				xmlByteA = BackletKit.infoXml("haveBeenEntered").getBytes();
			}
			
		}
		else if(subCmd.equals(LEAVE))
		{
			String uid = parms.get("uid");
			String rName = parms.get("name");
			Player user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
			
			
			if(user == null)
			{
				return BackletKit.errorXml("UserNotLogined").getBytes();
			}

			Room room = UserModule.getInstance().getRoomByName(rName);
			
			if(room == null)
			{
				return BackletKit.errorXml("YourParmsIsInValid").getBytes();
			}
			
			
			int lea = room.userLeave(user);
			
			if(lea == 0)
			{
				xmlByteA = BackletKit.okXml("UserLeaveRoomOk").getBytes();
			}
			else
			{
				xmlByteA = BackletKit.infoXml("haveBeenLeaved").getBytes();
			}
			log.info("user:" + user.getUid() + " leave room: "  +  " roomName:" +rName);
		}
		else
		{
			xmlByteA = BackletKit.infoXml("RoomManagerParmsIsValid")
					.getBytes();
		}
		return xmlByteA;
	}

	public String getRoomListFromMemory(String roomtype, String bb,
											   String sb)
	{
		List<Room> roomlist = UserModule.getInstance().getRoomList();
		String xmlString = "";
		Element root = new Element("roomCollection");
		Element child = new Element("rooms");
		for (int i = 0; i < roomlist.size(); i++)
		{
			Room room = roomlist.get(i);
			Element subchild = new Element("room");
			subchild.setAttribute("id", "" + room.getRoomId());
			subchild.setAttribute("name", room.getName());
			subchild.setAttribute("mu", "" + room.getMaxUsers());
			int[] usercount = UserModule.getInstance().getplayerNum(room.getName());
			subchild.setAttribute("uc", "" + usercount[1]);
			subchild.setAttribute("sc", "" + usercount[2]);

			subchild.setAttribute("mix_buy","" + room.getMinbuy());
			subchild.setAttribute("big_bet","" + room.getBbet());
			subchild.setAttribute("small_bet","" + room.getSbet());
			subchild.setAttribute("max_buy","" + room.getMaxbuy());
			subchild.setAttribute("creater",room.getCreator());
			subchild.setAttribute("show_name",room.getShowname());

			if(  roomlist.get(i).getBbet() >= Integer.parseInt(sb)
					&& roomlist.get(i).getBbet() <= Integer.parseInt(bb))
			{
				child.addContent(subchild);
			}
		}

		root.addContent(child);
		Document doc = new Document(root);
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(format);
		xmlString = outputter.outputString(doc);
		return xmlString;
	}

}
