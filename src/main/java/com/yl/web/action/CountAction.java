package com.yl.web.action;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.web.dao.UserDao;

public class CountAction extends Action
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
		System.out.println("=====clist===========");
		HashMap chm = udao.countlist();
		ArrayList clist = (ArrayList) chm.get("clist");
		request.setAttribute("clist", clist);
		request.setAttribute("cregnum", chm.get("regnum").toString());
		request.setAttribute("conline", chm.get("onlinenum").toString());

		return mapping.findForward("count");
	}
}
