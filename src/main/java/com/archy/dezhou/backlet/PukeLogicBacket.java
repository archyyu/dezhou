package com.archy.dezhou.backlet;

import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.entity.room.Room;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.backlet.base.DataBacklet;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.SFSObjectSerializer;
import com.archy.dezhou.entity.Player;

public class PukeLogicBacket extends DataBacklet
{
	@Override
	public byte[] process( String subCmd, Map<String, String> parms, FullHttpResponse httpResponse )
	{
		
		String cmd = parms.get("cmd");
		String uid = parms.get("uid");
		
		byte[] xmlByteA = null;
		ActionscriptObject asObj = null;

		Player user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
		if(user == null)
		{
			return BackletKit.errorXml("UserNotLogined").getBytes();
		}

		
		Room room = UserModule.getInstance().getRoom( user.getRoomId() );
		if(room == null)
		{
			return BackletKit.errorXml("parmsInInvalid").getBytes();
		}
		
		PukerGame game = room.getPokerGame();
		Player player = game.findPlayerByUser(user);
		
		if(player != null)
		{
			player.clearDropCardNum();
		}
		
		if(subCmd.equals("common"))
		{
			try
			{
			
				if(cmd.equals(ConstList.CMD_ROOMINFO))
				{
					asObj = room.toAsObj();
				}
				else if(cmd.equals(ConstList.CMD_LOOK_CARD))
				{
					asObj = game.playerLookCard(player);
				}
				else if(cmd.equals(ConstList.CMD_ADD_BET))
				{
					int bet = Integer.parseInt(parms.get("cb"));
					asObj = game.playerAddBet(player,bet);
				}
				else if(cmd.equals(ConstList.CMD_FOLLOW_BET))
				{
					int bet = Integer.parseInt(parms.get("cb"));
					asObj = game.playerFollowBet(player,bet);
				}
				else if(cmd.equals(ConstList.CMD_DROP_CARD))
				{
					asObj = game.playerDropCard(player);
				}
				else if(cmd.equals(ConstList.CMD_ALL_IN))
				{
					int bet = Integer.parseInt(parms.get("cb"));
					asObj = game.playerAllIn(player,bet);
				}
				else if(cmd.equals(ConstList.CMD_SITDOWN))
				{
					int seatId = Integer.parseInt(parms.get("sid"));
					int cb = Integer.parseInt(parms.get("cb"));
					if(seatId < 0 || seatId > 8)
					{
						return BackletKit.infoXml("UserManagerParmsIsValid").getBytes();
					}
					asObj = room.playerSitDown(seatId,user,cb);
				}
				else if(cmd.equals(ConstList.CMD_STANDUP))
				{
					asObj = room.playerStandUp(user.getUid(),false);
					if(game != null)
					{
						if(room.isGame() && game.isGameOverWhenDropCard())
						{
							game.gameOverHandle();
						}
					}
				}
				else if(cmd.equals(ConstList.CMD_LEAVE))
				{
					room.playerLeave(user);
					
					log.info("room : " + room + "   leave user: " + user + "   player:" + player);
					asObj = game.playerLeave(player);
				}
				
				if(asObj == null)
				{
					return BackletKit.infoXml("UserManagerParmsIsValid").getBytes();
				}

				httpResponse.headers().set("num", "0");

			}
			catch (Exception e)
			{
				log.error("error",e);
			}
		}
		else if(subCmd.startsWith("other"))
		{
			if(cmd.equals(ConstList.CMD_FLUSHACH))
			{
				log.error("CMD_FLUSHACH=="+ cmd);
						
				asObj = null;
			}
			if(asObj == null)
			{
				return BackletKit.infoXml("UserManagerParmsIsValid").getBytes();
			}
		}
		else
		{
			return BackletKit.infoXml("UserManagerParmsIsValid").getBytes();
		}
		
		
//		log.error(" cmd=="+ cmd + "  subCmd===" + subCmd);
		httpResponse.headers().set("n", "0");
		httpResponse.headers().set("cmd",cmd);
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlByteA = SFSObjectSerializer.obj2xml(asObj, 0, "", sb);
		return BackletKit.SimpleObjectXml(xmlByteA);
	}
}
