package com.yl.web.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.util.PayFinalValue;
import com.yl.web.dao.PaymentDao;

public class PaypalINPAction extends Action
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
		System.out.println("=====PaypalINPAction===========");
		Enumeration en = request.getParameterNames();
		String str = "cmd=_notify-validate";
		while (en.hasMoreElements())
		{
			String paramName = (String) en.nextElement();
			String paramValue = request.getParameter(paramName);
			str = str + "&" + paramName + "="
					+ URLEncoder.encode(paramValue, "iso-8859-1");
		}
		// URL u= new URL("http://www.sandbox.paypal.com/cgi-bin/webscr");
		URL u = new URL("http://www.paypal.com/cgi-bin/webscr");
		URLConnection uc = u.openConnection();
		uc.setDoInput(true); // 设置可以输出
		uc.setDoOutput(true); // 设置可以输入
		uc.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(str);
		pw.close();
		String itemname = request.getParameter("item_name");
		String itemnumber = request.getParameter("item_number");
		String paymentstatus = request.getParameter("payment_status");
		String paymentamount = request.getParameter("mc_gross");
		String paymentcurrency = request.getParameter("mc_currency");
		String txnid = request.getParameter("txn_id");
		String receiveremail = request.getParameter("receiver_email");
		String payeremail = request.getParameter("payer_email");
		String paymentdate = request.getParameter("payment_date");
		String custom = request.getParameter("custom");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream()));
		String res = in.readLine();
		in.close();
		if (res != null && res.equals("VERIFIED"))
		{
			System.out.println("====VERIFIED====");
			// 检查付款状态
			System.out.println("====paymentstatus====" + paymentstatus);
			if (paymentstatus.equals("Completed"))
			{
				// 检查 txn_id 是否已经处理过
				boolean isExist = pdao.isDealOrder(custom.substring(custom
						.indexOf("###") + 3));
				System.out.println("====isExist====" + isExist);
				System.out.println("====receiveremail====" + receiveremail);
				if (!isExist)
				{
					if (receiveremail.equals("40143116@qq.com"))
					{
						System.out.println("====db====");
						pdao.updateOrder(PayFinalValue.PV_PAY_PAYPAL_Paypal,
								(int) Double.parseDouble(paymentamount) * 100,
								txnid,
								custom.substring(custom.indexOf("###") + 3),
								payeremail);
					}
					else
					{
						System.out.println("=receiverEmail is wrong===");
					}
				}
				else
				{
					System.out.println("====txn_id is exist===");
				}

				// 检查 receiver_email 是否是您的 PayPal 账户中的 EMAIL 地址
				// 检查付款金额和货币单位是否正确
			}
			else
			{
				System.out.println("====paymentStatus not completed====");
			}
		}
		else if (res.equals("INVALID"))
		{
			System.out.println("====INVALID====");
		}
		else
		{
			System.out.println("====else error===");
		}
		System.out.println("==over==");
		return null;
	}
}
