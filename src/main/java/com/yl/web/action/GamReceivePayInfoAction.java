package com.yl.web.action;

import java.security.MessageDigest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.util.PayUtils;
import com.yl.util.PayFinalValue;
import com.yl.web.dao.PaymentDao;

public class GamReceivePayInfoAction extends Action
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
		// 接收橘子游戏支付平台post过来的参数
		System.out.println("GamReceivePayInfoAction==");
		String strCPOrderID = request.getParameter("CPOrderID") == null ? ""
				: request.getParameter("CPOrderID");
		String strTransactionID = request.getParameter("TransactionID") == null ? ""
				: request.getParameter("TransactionID");
		String strPayMethod = request.getParameter("PayMethod") == null ? ""
				: request.getParameter("PayMethod");
		String strPayAgentID = request.getParameter("PayAgentID") == null ? ""
				: request.getParameter("PayAgentID");
		String strPayStatus = request.getParameter("PayStatus") == null ? ""
				: request.getParameter("PayStatus");
		String strPayMoney = request.getParameter("PayMoney") == null ? ""
				: request.getParameter("PayMoney");
		String strPayMemo = request.getParameter("PayMemo") == null ? ""
				: request.getParameter("PayMemo");
		String strMemo1 = request.getParameter("Memo1") == null ? "" : request
				.getParameter("Memo1");
		String strMemo2 = request.getParameter("Memo2") == null ? "" : request
				.getParameter("Memo2");
		String strVCode = request.getParameter("Vcode") == null ? "" : request
				.getParameter("Vcode");
		String strClientIP = request.getRemoteAddr();
		String strKey = "1KKUP9FB75XV65YG0HNIUDKD9QI0WGGQGE214OQX93";
		// 交易驗證碼
		String strToHash = (strCPOrderID + "&" + strTransactionID + "&"
				+ strPayMethod + "&" + strPayAgentID + "&" + strPayStatus + "&"
				+ strPayMoney + "&" + strKey + "&" + strClientIP).toLowerCase();
		System.out.println("strToHash==" + strToHash);
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		String strHashKey = (PayUtils.bytes2String(sha1.digest(strToHash
				.getBytes()))).toLowerCase();
		System.out.println("strHashKey==" + strHashKey);
		System.out.println("strVCode==" + strVCode);
		if (!strHashKey.toLowerCase().equals(strVCode))
		{
			// 资料错误
			response.getOutputStream().write("0".getBytes());
		}
		else
		{
			try
			{
				// 商家確認接收該訂單資訊並儲存 修改订单状态
				// 检查订单是否处理
				if (!pdao.isDealOrder(strCPOrderID))
				{
					pdao.updateOrder(PayFinalValue.PV_PAY_GAMANIA,
							Integer.parseInt(strPayMoney), strPayMethod,
							strCPOrderID, strPayStatus);
				}
			}
			catch (Exception e)
			{
				response.getOutputStream().write("0".getBytes());
			}
			response.getOutputStream().write("1".getBytes());

		}
		return null;
	}
}