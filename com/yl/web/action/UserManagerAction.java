package com.yl.web.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookXmlRestClient;
import com.google.code.facebookapi.ProfileField;
import com.yl.util.PayFinalValue;
import com.yl.util.Utils;
import com.yl.web.dao.UserDao;
import com.yl.web.po.TinyUser;

public class UserManagerAction extends Action
{
	private UserDao udao;

	public void setUdao(UserDao udao)
	{
		this.udao = udao;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		HttpSession session = request.getSession();
		String sessionKey = (String) session.getAttribute("facebookUserId");
		String requestURL = "";

		String apiKey = "";
		String secretKey = "";
		StringBuffer fxml = new StringBuffer();

		if (null != sessionKey && sessionKey.length() > 0)
		{
			apiKey = (String) session.getAttribute("apiKey");
			secretKey = (String) session.getAttribute("secretKey");
			FacebookXmlRestClient frc = new FacebookXmlRestClient(apiKey,
					secretKey, sessionKey);
			Long myid = null;
			try
			{
				myid = frc.users_getLoggedInUser();
			}
			catch (FacebookException e)
			{
				System.out.println("==getFacebookUid Error===");
			}
			EnumSet<ProfileField> fields = EnumSet.of(ProfileField.FIRST_NAME,
					ProfileField.LAST_NAME, ProfileField.NAME,
					ProfileField.UID, ProfileField.PIC, ProfileField.BIRTHDAY,
					ProfileField.CURRENT_LOCATION, ProfileField.SEX,
					ProfileField.ONLINE_PRESENCE, ProfileField.STATUS);
			Collection<Long> users = new ArrayList();
			users.add(myid);
			Document d = null;
			try
			{
				d = frc.users_getInfo(users, fields);
			}
			catch (FacebookException e)
			{
				System.out.println("==getFacebookUser Error===");
			}
			String pic = d.getElementsByTagName("pic").item(0) == null ? "" : d
					.getElementsByTagName("pic").item(0).getTextContent();
			String uid = d.getElementsByTagName("uid").item(0) == null ? "" : d
					.getElementsByTagName("uid").item(0).getTextContent();
			String sex = d.getElementsByTagName("sex").item(0) == null ? "" : d
					.getElementsByTagName("sex").item(0).getTextContent();
			String bir = d.getElementsByTagName("birthday").item(0) == null ? ""
					: d.getElementsByTagName("birthday").item(0)
							.getTextContent();
			String address = d.getElementsByTagName("current_location").item(0) == null ? ""
					: d.getElementsByTagName("current_location").item(0)
							.getTextContent();
			String status = d.getElementsByTagName("status").item(0) == null ? ""
					: d.getElementsByTagName("status").item(0).getTextContent();
			String online = d.getElementsByTagName("online_presence").item(0) == null ? ""
					: d.getElementsByTagName("online_presence").item(0)
							.getTextContent();
			String name = d.getElementsByTagName("first_name").item(0) == null ? ""
					: d.getElementsByTagName("first_name").item(0)
							.getTextContent();

			name = Utils.encodeStr(name);
			TinyUser u = new TinyUser();
			if (pic.equals("") || pic.contains("static"))
			{
				pic = "http://www.putzone.com/putexas/putzoon/user.gif";
			}
			u.setUid(uid);
			u.setName(name);
			u.setPic(pic);
			u.setSex(sex);
			u.setBir(bir);
			u.setAddress(address);
			u.setStatus(status);
			u.setOnline("1");
			u.setMoney(PayFinalValue.GIVEN_BET_NewPlayer);
			// 查询数据库是否存在该用户
			HashMap retmap = (HashMap) udao.checkUser(uid, name, pic);
			String flag = retmap.get("flag").toString();
			String tmoney = String.valueOf(PayFinalValue.GIVEN_BET_NewPlayer);
			String level = "0";
			String gold = "0";
			String ctime = "0";
			String flogin = "1";
			int tm = -1;
			if (flag.equals("yes"))
			{
				tm = Integer.parseInt(retmap.get("tmoney").toString());
				flogin = retmap.get("loginnum").toString();
				tmoney = String.valueOf(tm);
				tm = Integer.parseInt(retmap.get("gold").toString());
				gold = String.valueOf(tm);
				ctime = retmap.get("ctime").toString();
				level = retmap.get("level").toString();
			}
			else if (flag.equals("no"))
			{
				String myads = request.getQueryString();
				myads = myads == null ? "" : myads;
				u.setMyads(myads);
				udao.saveUser(u);
			}
			String ustr = "<user uid=\"{0}\" name=\"{1}\" pic=\"{2}\" tmoney=\"{3}\" gold=\"{4}\" ctime=\"{5}\" level=\"{6}\" flogin=\"{7}\" />";
			ustr = ustr.replace("{0}", uid).replace("{1}", name)
					.replace("{2}", pic).replace("{3}", tmoney)
					.replace("{4}", gold).replace("{5}", ctime)
					.replace("{6}", level).replace("{7}", flogin);
			Document d2 = null;
			try
			{
				d2 = frc.friends_get();
			}
			catch (FacebookException e)
			{
				System.out.println("==getFacebookFriends Error===");
			}
			NodeList userIDNodes = d2.getElementsByTagName("uid");
			int fcount = userIDNodes.getLength();
			Collection<Long> friends = new ArrayList<Long>();
			for (int i = 0; i < fcount; i++)
			{
				Node node = userIDNodes.item(i);
				String idText = node.getTextContent();
				Long id = Long.parseLong(idText);
				friends.add(id);
			}
			fxml.append("$start");
			fxml.append("<friends>");
			fxml.append(ustr);
			fxml.append("</friends>");
			fxml.append("$end");
			session.setAttribute("fxml", fxml);
			session.setAttribute("uinfo", uid + "*@*" + name);
			session.setAttribute("uid", uid);
			session.setAttribute("uname", name);
			HashMap<String, HashMap> grbmap = udao.guangRB();
			HashMap<String, HashMap> recmap = udao.recUlist();
			HashMap<String, HashMap> wwmap = udao.whoWin();
			StringBuffer gsb = new StringBuffer();
			StringBuffer rsb = new StringBuffer();
			StringBuffer wsb = new StringBuffer();
			gsb.append("<users>");
			rsb.append("<users>");
			wsb.append("<users>");
			String ustr1 = "<user uid=\"{0}\" name=\"{1}\" pic=\"{2}\" bet=\"{3}\" pm=\"{4}\"/>";
			String user = "";
			for (String key : grbmap.keySet())
			{
				HashMap<String, String> hm = grbmap.get(key);
				user = ustr1.replace("{0}", hm.get("uid"))
						.replace("{1}", hm.get("name"))
						.replace("{2}", hm.get("pic"))
						.replace("{3}", hm.get("allmoney"))
						.replace("{4}", hm.get("ph"));
				gsb.append(user);
			}
			gsb.append("</users>");
			// facebook上的好友
			Document d3 = null;
			try
			{
				d3 = frc.users_getInfo(friends, fields);
			}
			catch (FacebookException e)
			{
				System.out.println("==getFacebookFriend Error===");
			}
			String name2 = "";
			String pic2 = "";
			String uid2 = "";
			for (int j = 0; j < fcount; j++)
			{
				pic2 = d3.getElementsByTagName("pic").item(j) == null ? "" : d3
						.getElementsByTagName("pic").item(j).getTextContent();
				uid2 = d3.getElementsByTagName("uid").item(j) == null ? "" : d3
						.getElementsByTagName("uid").item(j).getTextContent();
				name2 = d3.getElementsByTagName("first_name").item(j) == null ? ""
						: d3.getElementsByTagName("first_name").item(j)
								.getTextContent();
				if (pic2.equals("") || pic2.contains("static"))
				{
					pic2 = "http://www.putzone.com/putexas/putzoon/user.gif";
				}
				name2 = Utils.encodeStr(name2);
				user = ustr1.replace("{0}", uid2).replace("{1}", name2)
						.replace("{2}", pic2).replace("{3}", "")
						.replace("{4}", "");
				rsb.append(user);

			}
			rsb.append("</users>");

			for (String key : wwmap.keySet())
			{
				HashMap<String, String> hm = wwmap.get(key);
				user = ustr1.replace("{0}", hm.get("uid"))
						.replace("{1}", hm.get("name"))
						.replace("{2}", hm.get("pic"))
						.replace("{3}", hm.get("allmoney"))
						.replace("{4}", hm.get("ph"));
				wsb.append(user);
			}
			wsb.append("</users>");
			session.setAttribute("rsb", rsb);
			session.setAttribute("wsb", wsb);
			session.setAttribute("gsb", gsb);
		}
		return mapping.findForward("putzoon");
	}
}