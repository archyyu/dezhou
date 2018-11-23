package com.yl.web.action;

import java.security.MessageDigest;
import com.yl.util.PayUtils;
import com.yl.util.PayFinalValue;
import com.yl.vo.Mol;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.yl.web.dao.PaymentDao;

public class MolOnLineMsglAction extends Action
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
		System.out.println("===MolOnLineMsglAction back url=======");
		String f = "mol_error";
		String ResCode = request.getParameter("ResCode") == null ? "" : request
				.getParameter("ResCode");
		String MerchantID = request.getParameter("MerchantID") == null ? ""
				: request.getParameter("MerchantID");
		String MRef_ID = request.getParameter("MRef_ID") == null ? "" : request
				.getParameter("MRef_ID");
		String Amount = request.getParameter("Amount") == null ? "" : request
				.getParameter("Amount");
		String Currency = request.getParameter("Currency") == null ? ""
				: request.getParameter("Currency");
		String MOLOrderID = request.getParameter("MOLOrderID") == null ? ""
				: request.getParameter("MOLOrderID");
		String MOLUsername = request.getParameter("MOLUsername") == null ? ""
				: request.getParameter("MOLUsername");
		String Signature = request.getParameter("Signature") == null ? ""
				: request.getParameter("Signature");

		// 校验参数
		if (ResCode.equals("") || MerchantID.equals("")
				|| MOLOrderID.equals("") || MRef_ID.equals(""))
		{
			request.setAttribute("flag", f);
			return mapping.findForward("payret");
		}
		Mol mol = new Mol();
		String SecretPIN = PayFinalValue.MOL_SecretPIN;
		mol.setResCode(ResCode);
		mol.setAmount(Amount);
		mol.setCurrency(Currency);
		mol.setMOLOrderID(MOLOrderID);
		mol.setMerchantID(MerchantID);
		mol.setName(MOLUsername);
		mol.setMRef_ID(MRef_ID);
		mol.setSignature(Signature);
		String hb = MRef_ID.indexOf("|") < 0 ? "" : MRef_ID.substring(MRef_ID
				.indexOf("|") + 1);
		mol.setHeartBeat(hb);
		System.out.println("==hb==" + mol.getHeartBeat());
		// ShA1 传递过来的参数
		String strToHash = (mol.getResCode() + mol.getMerchantID()
				+ mol.getMRef_ID() + mol.getAmount() + mol.getCurrency()
				+ mol.getMOLOrderID() + mol.getName() + SecretPIN)
				.toLowerCase();
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		String sig = (PayUtils.bytes2String(sha1.digest(strToHash.getBytes())))
				.toLowerCase();
		System.out.println(mol.getResCode() + "=getResCode=sig==" + sig);
		System.out.println("==mol.getSignature(Signature)=="
				+ mol.getSignature());
		// 传递过来的参数 是否有修改
		if (sig.equals(mol.getSignature()))
		{
			if (mol.getResCode().equals("100"))
			{
				String orderID = mol.getMRef_ID();
				orderID = orderID.substring(0, orderID.indexOf("|"));
				String amount = mol.getAmount();
				amount = amount.substring(0, amount.indexOf("."));
				mol.setAmount(amount);
				if (!pdao.isDealOrder(orderID))
				{
					f = "mol_ok";
					// 修改订单状态
					pdao.updateOrder(PayFinalValue.PV_PAY_MOL_Mol,
							Integer.parseInt(mol.getAmount()),
							mol.getMOLOrderID(), orderID, "");
					request.setAttribute("pmoney", mol.getAmount());
					request.setAttribute("extg",
							PayUtils.getGtypeByMyr(mol.getAmount()));
					System.out.println(mol.getAmount() + "=="
							+ PayUtils.getGtypeByMyr(mol.getAmount()));
				}
				else
				{
					System.out.println("===order is deal===");
					f = "mol_ok";
					request.setAttribute("pmoney", mol.getAmount());
					request.setAttribute("extg",
							PayUtils.getGtypeByMyr(mol.getAmount()));
				}
			}
			else
			{
				// 交易状态没成功，重新查询交易的状态
				strToHash = (mol.getMerchantID() + mol.getMRef_ID() + SecretPIN + mol
						.getHeartBeat()).toLowerCase();
				sig = (PayUtils.bytes2String(sha1.digest(strToHash.getBytes())))
						.toLowerCase();
				// 生成查询的sig ;
				mol.setSignature(sig);
				String res = PayUtils.molQueryTrxStatus(mol);
				if (!res.equals(""))
				{
					String r_ResCode = PayUtils.getValueByTag(res, "ResCode");
					String r_Status = PayUtils.getValueByTag(res, "Status");

					if (r_ResCode.equals("100") && r_Status.equals("1"))
					{
						String r_MRef_ID = PayUtils.getValueByTag(res,
								"MRef_ID");
						r_MRef_ID = r_MRef_ID.substring(0,
								r_MRef_ID.indexOf("|"));
						String r_Amount = PayUtils.getValueByTag(res, "Amount");
						r_Amount = r_Amount.substring(0, r_Amount.indexOf("."));
						String r_MOLOrderID = PayUtils.getValueByTag(res,
								"MOLOrderID");
						// 交易成功 查看该订单是否处理
						if (!pdao.isDealOrder(r_MRef_ID))
						{
							f = "mol_ok";
							// 修改订单状态
							pdao.updateOrder(PayFinalValue.PV_PAY_MOL_Mol,
									Integer.parseInt(r_Amount), r_MOLOrderID,
									r_MRef_ID, "");
							request.setAttribute("pmoney", r_Amount);
							request.setAttribute("extg",
									PayUtils.getGtypeByMyr(r_Amount));
							System.out.println(r_Amount + "=="
									+ PayUtils.getGtypeByMyr(r_Amount));
						}
						else
						{
							System.out.println("===order is deal===");
							f = "mol_ok";
							request.setAttribute("pmoney", r_Amount);
							request.setAttribute("extg",
									PayUtils.getGtypeByMyr(r_Amount));
						}

					}
				}
			}
		}
		else
		{
			System.out.println("==Parameter error==");
		}
		request.setAttribute("flag", f);
		return mapping.findForward("payret");
	}
}
