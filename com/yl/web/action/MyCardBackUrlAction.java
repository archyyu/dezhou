package com.yl.web.action;

import java.util.HashMap;
import com.yl.util.PayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.web.dao.PaymentDao;
import com.yl.util.PayFinalValue;

public class MyCardBackUrlAction extends Action
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
		System.out.println("===mycard back url=======");
		/**
		 * ReturnMsgNo：交易訊息代碼 ReturnMsg：交易訊息 AuthCode：授權碼
		 * */
		String rmn = request.getParameter("ReturnMsgNo") == null ? "" : request
				.getParameter("ReturnMsgNo");
		String rm = request.getParameter("ReturnMsg") == null ? "" : request
				.getParameter("ReturnMsg");
		String ac = request.getParameter("AuthCode") == null ? "" : request
				.getParameter("AuthCode");
		String f = "mc_error";
		// 金流授權成功
		if (rmn.equals("1"))
		{
			// 驗證MyCard交易
			String s = PayUtils.confirmBillingTrade(ac);
			if (!s.equals(""))
			{
				String queryNum = s.substring(s.indexOf(">") + 1,
						s.indexOf("|")).trim();
				String resultTrade = s.substring(s.lastIndexOf("|") + 1,
						s.lastIndexOf("<")).trim();
				System.out.println("MyCard==confirm" + queryNum);
				System.out.println("MyCard==confirm" + resultTrade);
				if (queryNum.equals("1") && resultTrade.equals("3"))
				{
					HashMap hm = pdao.DealBillingOrder("ac", ac);
					if (hm != null && hm.size() > 0)
					{
						String uid = hm.get("uid").toString();
						String state = hm.get("state").toString();
						String money = hm.get("money").toString();
						String orderID = hm.get("orderID").toString();
						if (state.equals("Y"))
						{
							System.out.println("order is deal===");
						}
						else
						{
							// 成功後呼叫MyCard 請款之Web Service
							String res = PayUtils
									.confirmBillingPayment(ac, uid);
							if (!res.equals(""))
							{
								String paymentNum = res.substring(
										res.indexOf(">") + 1, res.indexOf("|"))
										.trim();
								if (paymentNum.equals("1"))
								{
									// 更新数据库
									f = "mc_ok";
									pdao.updateOrder(
											PayFinalValue.PV_PAY_MYCARD_Billing,
											Integer.parseInt(money), "",
											orderID, "");
									request.setAttribute("pmoney", money);
									request.setAttribute("extg",
											PayUtils.getGoldByMoney(money));
								}
							}
						}
					}
				}
				else
				{
					System.out.println("MyCard==confirm fail===");
				}
			}
			else
			{
				System.out.println("confirmBillingTrade=error===");
			}

		}
		else
		{
			System.out.println("ReturnMsg====" + rm);
		}
		request.setAttribute("mc_flag", f);
		return mapping.findForward("payret");
	}
}
