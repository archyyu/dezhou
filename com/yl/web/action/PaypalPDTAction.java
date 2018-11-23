package com.yl.web.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.yl.util.PayFinalValue;
import com.yl.util.PayUtils;
import com.yl.web.dao.PaymentDao;

public class PaypalPDTAction extends Action
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
		System.out.println("=====PaypalPDTAction===========");
		Enumeration en = request.getParameterNames();
		while (en.hasMoreElements())
		{
			String paramName = (String) en.nextElement();
			String paramValue = request.getParameter(paramName);
			System.out.println(paramName + "========:" + paramValue);
		}
		// 获取 PayPal 交易流水号 tx
		String tx_token = request.getParameter("tx");
		String st = request.getParameter("st");
		String amt = request.getParameter("amt");
		String cc = request.getParameter("cc");
		String cm = request.getParameter("cm");
		String itemnumber = request.getParameter("item_number");
		String f = "error";
		int gold = 0;
		if (amt == null)
		{
			f = "error";
		}
		else
		{
			gold = PayUtils.getGtypeByAmt(amt);
		}
		if (tx_token == null || st == null || gold == 0 || cm == null
				|| !cc.equals("USD") || !"1".equals(itemnumber))
		{
			f = "error";
		}
		// 定义您的身份标记
		// String auth_token =
		// "oH4Bg759yD7jQFzDSs9IUnr-zCKOXB0hvNVsddq-2hG8GbFF5aFJLF-cgjW";
		String auth_token = "1M9fg0jV_dZ5Aqsasz3Muf_qfdPGVsmjEmJzBF7x8up9oRsAQikABzb4jlm";
		// 形成验证字符串
		String req = "cmd=_notify-synch&tx=" + tx_token + "&at=" + auth_token;
		// URL u= new URL("http://www.sandbox.paypal.com/cgi-bin/webscr");
		URL u = new URL("http://www.paypal.com/cgi-bin/webscr");
		URLConnection uc = u.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(req);
		pw.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream()));
		String res = in.readLine();
		System.out.println(res);
		in.close();
		if (res.equals("SUCCESS"))
		{
			if (st.equals("Completed"))
			{
				// 将美元转换成整数保存到数据库
				// 检查订单是否已经处理
				if (!pdao.isDealOrder(cm.substring(cm.indexOf("###") + 3)))
				{
					f = "ok";
					pdao.updateOrder(PayFinalValue.PV_PAY_PAYPAL_Paypal,
							(int) (Double.parseDouble(amt) * 100), tx_token,
							cm.substring(cm.indexOf("###") + 3), "");
					request.setAttribute("gold", String.valueOf(gold));
					request.setAttribute("amt", amt);
					request.setAttribute("cc", cc);
					request.setAttribute("cm",
							cm.substring(0, cm.indexOf("###")));
				}
				else
				{
					System.out.println("===order is deal===");
				}
			}
			else
			{
				System.out.println("===not completed===");
			}
		}
		else
		{
			System.out.println("==fail===");
		}
		request.setAttribute("flag", f);
		return mapping.findForward("payret");
	}
}
