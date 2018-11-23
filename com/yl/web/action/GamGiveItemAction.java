package com.yl.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.yl.util.PayUtils;
import com.yl.web.dao.PaymentDao;

public class GamGiveItemAction extends Action
{
	private PaymentDao pdao;

	public void setPdao(PaymentDao pdao)
	{
		this.pdao = pdao;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		System.out.println("GamGiveItemAction==");
		// 获取从橘子游戏支付平台post过来的参数
		String strCPOrderID = request.getParameter("CPOrderID") == null ? ""
				: request.getParameter("CPOrderID");
		// String strVCode = request.getParameter("Vcode") == null ? "" :
		// request.getParameter("Vcode");
		// String strClientIP = request.getRemoteAddr();
		// 根据充值金额，提供金币给用户
		String dealFlag = "gam_ok";
		String pmoney = pdao.backPayMsg(strCPOrderID);

		if (pmoney.equals(""))
		{
			dealFlag = "gam_error";
		}
		else
		{
			request.setAttribute("pmoney", pmoney);
			System.out.println("pmoney==" + pmoney);
			request.setAttribute("extg", PayUtils.getGoldByMoney(pmoney));
			System.out.println("extg==" + PayUtils.getGoldByMoney(pmoney));
		}
		request.setAttribute("dflag", dealFlag);
		System.out.println("dealFlag==" + dealFlag);
		return mapping.findForward("payret");

	}
}
