package com.yl.web.action;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;
import com.yl.web.dao.UserDao;
import com.yl.web.po.TinyUser;

public class TestAction extends Action
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
		DynaValidatorForm af = (DynaValidatorForm) form;
		HttpSession session = request.getSession();
		String uid = af.get("uid").toString();
		String name = af.get("name").toString();

		String pic = "";
		TinyUser u = new TinyUser();
		u.setUid(uid);
		u.setName(name);
		u.setPic(pic);
		u.setOnline("1");
		u.setMoney(50000);

		HashMap retmap = (HashMap) udao.checkUser(uid, name, pic);
		String flag = retmap.get("flag").toString();
		String tmoney = "30000";
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

		StringBuffer fxml = new StringBuffer();
		fxml.append("$start");
		fxml.append("<friends>");
		fxml.append(ustr);
		fxml.append("</friends>");
		fxml.append("$end");

		session.setAttribute("fxml", fxml);
		session.setAttribute("uinfo", uid + "*@*" + name);

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

		for (String key : recmap.keySet())
		{
			HashMap<String, String> hm = recmap.get(key);
			user = ustr1.replace("{0}", hm.get("uid"))
					.replace("{1}", hm.get("name"))
					.replace("{2}", hm.get("pic"))
					.replace("{3}", hm.get("allmoney"))
					.replace("{4}", hm.get("ph"));
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

		return mapping.findForward("putzone");
	}
}
