package com.yl.web.action;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.yl.util.Utils;
import com.yl.vo.MyCard;
import com.yl.web.dao.PaymentDao;

public class MyCardDifferentAction extends Action
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
		System.out.println("==MyCardStoreInfoAction====");

		String sdate = request.getParameter("StartDate");
		String edate = request.getParameter("EndDate");
		String mcID = request.getParameter("MyCardID");
		sdate = sdate == null ? "" : sdate;
		edate = edate == null ? "" : edate;
		mcID = mcID == null ? "" : mcID;
		StringBuffer writeMsg = new StringBuffer();
		String s = "";
		System.out.println("sdate======" + sdate);
		System.out.println("edate======" + edate);
		System.out.println("mcID======" + mcID);
		if (Utils.volidateDate(sdate) && Utils.volidateDate(edate))
		{
			sdate += " 00:00:01";
			edate += " 23:59:59";
			// 差异报表
			ArrayList<MyCard> tlist = pdao.QueryMCTradeByDate(sdate, edate);
			System.out.println("tlist======" + tlist.size());
			for (int i = 0; i < tlist.size(); i++)
			{
				MyCard mc = tlist.get(i);
				s = mc.getMyCard_ID() + "," + mc.getCust_ID() + ","
						+ mc.getTRADE_NO() + "," + mc.getGAME_TNO() + ","
						+ mc.getTradeok_time() + "<BR>";
				writeMsg.append(s);
			}
		}
		else if (!mcID.equals(""))
		{
			// mycard 卡号查询
			MyCard mc = pdao.QueryMCTradeByID(mcID);
			if (mc != null)
			{
				s = mc.getMyCard_ID() + "," + mc.getCust_ID() + ","
						+ mc.getTRADE_NO() + "," + mc.getGAME_TNO() + ","
						+ mc.getTradeok_time() + "<BR>";
				writeMsg.append(s);
			}
		}
		else
		{
			writeMsg.append("Parameter error");
		}
		System.out.println("writeMsg======" + writeMsg.toString());
		response.getOutputStream().write(writeMsg.toString().getBytes());
		response.flushBuffer();
		return null;

	}
}
