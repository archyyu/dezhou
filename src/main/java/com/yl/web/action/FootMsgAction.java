package com.yl.web.action;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.web.dao.UserDao;

public class FootMsgAction extends Action
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
		System.out.println("=====footMsg===========");
		HttpSession session = request.getSession();
		HashMap<String, HashMap> grbmap = udao.guangRB();
		HashMap<String, HashMap> recmap = udao.recUlist();
		HashMap<String, HashMap> wwmap = udao.whoWin();
		System.out.println("grbmap.size()==" + grbmap.size());
		System.out.println("recmap.size()==" + recmap.size());
		System.out.println("wwmap.size()==" + wwmap.size());
		StringBuffer gsb = new StringBuffer();
		StringBuffer rsb = new StringBuffer();
		StringBuffer wsb = new StringBuffer();
		gsb.append("<users>");
		rsb.append("<users>");
		wsb.append("<users>");
		String ustr = "<user uid=\"{0}\" name=\"{1}\" pic=\"{2}\" bet=\"{3}\" pm=\"{4}\"/>";
		String user = "";
		for (String key : grbmap.keySet())
		{
			System.out.println(key);
			HashMap<String, String> hm = grbmap.get(key);
			user = ustr.replace("{0}", hm.get("uid"))
					.replace("{1}", hm.get("name"))
					.replace("{2}", hm.get("pic"))
					.replace("{3}", hm.get("allmoney"))
					.replace("{4}", hm.get("ph"));
			gsb.append(user);
		}
		gsb.append("</users>");

		for (String key : recmap.keySet())
		{
			System.out.println(key);
			HashMap<String, String> hm = recmap.get(key);
			user = ustr.replace("{0}", hm.get("uid"))
					.replace("{1}", hm.get("name"))
					.replace("{2}", hm.get("pic"))
					.replace("{3}", hm.get("allmoney"))
					.replace("{4}", hm.get("ph"));
			rsb.append(user);
		}
		rsb.append("</users>");

		for (String key : wwmap.keySet())
		{
			System.out.println(key);
			HashMap<String, String> hm = wwmap.get(key);
			user = ustr.replace("{0}", hm.get("uid"))
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
		System.out.println("rsb==" + rsb);
		System.out.println("wsb==" + wsb);
		System.out.println("gsb==" + gsb);

		return mapping.findForward("foot");
	}
}
