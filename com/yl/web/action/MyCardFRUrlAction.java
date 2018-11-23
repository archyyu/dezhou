package com.yl.web.action;

import com.yl.util.PayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.util.PayFinalValue;
import com.yl.web.dao.PaymentDao;

public class MyCardFRUrlAction extends Action
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
		System.out.println("====MycardfrurlAction===========");
		String rmn = request.getParameter("ReturnMsgNo");
		String f = "mc_error";
		if (rmn != null && rmn.equals("1"))
		{
			String fs = request.getParameter("FactorySeq");
			String otp = request.getParameter("OTP");
			String ac = request.getParameter("AuthCode");
			System.out.println("otp==" + otp);
			System.out.println("ac==" + ac);
			// 扣點確認交易
			String res = PayUtils.confirmTrade(ac, otp);
			if (!res.equals(""))
			{
				String c_rmn = "ReturnMsgNo";
				c_rmn = PayUtils.getValueByTag(res, c_rmn);
				if (c_rmn.equals("1"))
				{
					// 查询订单的交易相信信息
					String queryRes = PayUtils.TradeQuery(ac, otp);
					if (!queryRes.equals(""))
					{
						if (PayUtils.getValueByTag(queryRes, "ReturnMsgNo")
								.equals("1"))
						{
							String mcci = PayUtils.getValueByTag(queryRes,
									"MyCardCustId");
							String mcp = PayUtils.getValueByTag(queryRes,
									"MyCardPoint");
							String mcb = PayUtils.getValueByTag(queryRes,
									"MyCardBonus");
							String fts = PayUtils.getValueByTag(queryRes,
									"FacTradeSeq");
							// 判断订单是否处理
							if (!pdao.isDealOrder(fts))
							{
								f = "mc_ok";
								// 修改订单状态
								pdao.updateOrder(
										PayFinalValue.PV_PAY_MYCARD_Point,
										Integer.parseInt(mcp), ac, fts, mcci);
								request.setAttribute("pmoney", mcp);
								request.setAttribute("extg",
										PayUtils.getGoldByMoney(mcp));
							}
							else
							{
								System.out.println("===order is deal===");
							}
						}
					}
				}
			}
		}
		request.setAttribute("mc_flag", f);
		return mapping.findForward("payret");
	}
}
