package com.yl.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.util.PayUtils;
import com.yl.util.PayFinalValue;
import com.yl.util.XmlStrReaderUtils;
import com.yl.vo.MyCard;
import com.yl.web.dao.PaymentDao;

public class MyCardStoreInfoAction extends Action
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

		String data = request.getParameter("data");
		data = data == null ? "" : data;
		String flag = "data:";
		if (!data.equals(""))
		{
			ArrayList<MyCard> mclist = XmlStrReaderUtils.ReadStrXml(data);
			String tradeSea = "";
			for (MyCard mc : mclist)
			{
				if (mc.getReturnMsgNo().endsWith("1"))
				{
					tradeSea = mc.getTradeSeq();
					// 判断订单是否处理过,按照订单id查询
					HashMap<String, String> hm = pdao.DealBillingOrder("ts",
							tradeSea);
					if (hm != null && hm.size() > 0)
					{
						String uid = hm.get("uid").toString();
						String state = hm.get("state").toString();
						String money = hm.get("money").toString();
						String orderID = hm.get("orderID").toString();
						String email = hm.get("email").toString();
						if (state.equals("Y"))
						{
							System.out.println("order is deal===");
						}
						else
						{
							// 成功後呼叫MyCard 請款之Web Service
							String res = PayUtils.confirmBillingPayment(email,
									uid);
							if (!res.equals(""))
							{
								String paymentNum = res.substring(
										res.indexOf(">") + 1, res.indexOf("|"))
										.trim();
								if (paymentNum.equals("1"))
								{
									// 更新数据库
									pdao.updateOrder(
											PayFinalValue.PV_PAY_MYCARD_Billing,
											Integer.parseInt(money), "",
											orderID, "");
								}
							}

						}
					}
				}
			}
		}
		else
		{
			flag += data;
		}
		response.getOutputStream().write(flag.getBytes());
		response.flushBuffer();
		return null;
	}
}
