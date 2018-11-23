package com.yl.web.action;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

import com.yl.util.PayUtils;
import com.yl.util.PayFinalValue;
import com.yl.vo.Mol;
import com.yl.web.dao.PaymentDao;

public class GamGoToPayAction extends Action
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
		DynaValidatorForm af = (DynaValidatorForm) form;
		String pmethod = af.getString("ptype") == null ? "" : af
				.getString("ptype");
		String pmoney = af.getString("pmoney") == null ? "" : af
				.getString("pmoney");
		String orderID = af.getString("orderID") == null ? "" : af
				.getString("orderID");
		String uinfo = af.getString("uinfo") == null ? "" : af
				.getString("uinfo");
		System.out.println("=uinfo=" + uinfo);
		System.out.println("=orderID=" + orderID);
		System.out.println("=pmethod=" + pmethod);
		System.out.println("===pmoney==" + pmoney);
		// 判断参数是否合法
		if (PayUtils.checkPayValues(orderID, pmethod, pmoney))
		{
			request.setAttribute("mc_flag", "mc_error");
			return mapping.findForward("payret");
		}
		// 不同支付平台orderID 字符串长度有限制
		if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_Billing))
		{
			orderID = orderID.substring(3);
		}
		else if (pmethod.equals(PayFinalValue.PV_PAY_MOL_Mol))
		{
			orderID = orderID.substring(3, 16);
		}

		String postMsg = "";
		// 检查订单是否唯一
		if (pdao.checkOrderID(orderID))
		{
			System.out.println("=====orderID not only====");
			request.setAttribute("flag", "error");
			return mapping.findForward("payret");
		}
		else
		{
			// 订单保存到数据库
			pdao.saveOrder(orderID, uinfo.substring(0, uinfo.indexOf("*@*"))
					.trim(), pmoney, pmethod, request.getRemoteAddr());
			if (pmethod.equals(PayFinalValue.PV_PAY_GAMANIA_P)
					|| pmethod.equals(PayFinalValue.PV_PAY_GAMANIA_A))
			{
				// 橘子游戏支付平台
				postMsg = PayUtils.postMsgToGamania(pmethod, pmoney,
						request.getRemoteAddr(), orderID);
			}
			else if (pmethod.equals(PayFinalValue.PV_PAY_PAYPAL_Paypal))
			{
				// paypal支付平台
				postMsg = PayUtils.postMsgToPaypal(uinfo + "###" + orderID,
						pmoney);
			}
			else if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_Point))
			{
				// mycard支付平台之会员扣点
				String res = PayUtils.getMcPointAuthCode(orderID, pmoney);
				if (!res.equals(""))
				{
					postMsg = PayUtils.postMsgToMycardPoint(res);
				}
			}
			else if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_InGame))
			{
				// mycard支付平台之储值
				String card_ID = af.getString("cid") == null ? "" : af
						.getString("cid");
				String card_psd = af.getString("cpsd") == null ? "" : af
						.getString("cpsd");
				String res = PayUtils.postMsgToMycardInGame(card_ID, card_psd,
						orderID, uinfo.substring(0, uinfo.indexOf("*@*"))
								.trim());
				if (!res.equals(""))
				{
					String f = "mc_error";
					String ReturnNo = PayUtils.getValueByTag(res, "ReturnNo");
					if (ReturnNo.equals("1"))
					{
						String mcp = PayUtils.getValueByTag(res, "MyCardPoint");
						String mcpjn = PayUtils.getValueByTag(res,
								"MyCardProjectNo");
						String mct = PayUtils.getValueByTag(res, "MyCardType");
						// 判断订单是否处理
						if (!pdao.isDealOrder(orderID))
						{
							// 修改订单状态
							f = "mc_ok";
							pdao.updateOrder(pmethod, Integer.parseInt(mcp),
									card_ID, orderID, mct);
							request.setAttribute("pmoney", mcp);
							request.setAttribute("extg",
									PayUtils.getGoldByMoney(mcp));
						}
					}
					request.setAttribute("mc_flag", f);
					return mapping.findForward("payret");
				}
			}
			else if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_Billing))
			{
				String serviceId = af.getString("serviceid") == null ? "" : af
						.getString("serviceid");
				String sidKey = pmoney + serviceId;
				// 查询物品所对应的serviceId；
				serviceId = PayUtils.getServiceId(sidKey);
				if (serviceId.equals(""))
				{
					request.setAttribute("flag", "error");
					return mapping.findForward("payret");
				}
				// mycard支付平台之会员billing
				String res = PayUtils.getMcBillingAuthCode(serviceId, orderID,
						pmoney);
				if (!res.equals(""))
				{
					// 更新数据库，将交易授权码，mycard交易序号保存在数据库中能够
					// 特殊说明： 交易授权码保存在email自动中
					// 交易代碼|交易訊息|MyCard交易序號|交易授權碼
					// 3.1.5 回傳格式範例 <string
					// xmlns="http://schemas.microsoft.com/2003/10/Serialization/">1|授權成功|MHA090401007654|hRcDXBa69bPYmdJKzMAaRzeBy8Xo63pKh2zZLNSJxkI=</string>
					String[] valueStr = PayUtils.getValueFromRes(res);
					String rmn = valueStr[0];
					if (rmn != null && rmn.equals("1"))
					{
						String rts = valueStr[2];
						String rac = valueStr[3];
						pdao.updateMCAuthCode(rac, rts, orderID);
						postMsg = PayUtils.postMsgToMycardBilling(rac);
					}
				}
			}
			else if (pmethod.equals(PayFinalValue.PV_PAY_MOL_Mol))
			{
				// Obtaining a HeartBeat
				String res = PayUtils.getMolHeartBeat();
				if (!res.equals(""))
				{
					String hb = PayUtils.getValueByTag(res, "HB");
					if (hb != null
							|| !hb.equals("00000000-0000-0000-0000-000000000000"))
					{
						Mol mol = new Mol();
						mol.setAmount(pmoney + ".00");
						mol.setCurrency("MYR");
						mol.setDescription("Putzone HappyGold");
						mol.setHeartBeat(hb);
						mol.setMerchantID(PayFinalValue.MOL_MerchantID);
						mol.setMRef_ID(orderID + "|" + mol.getHeartBeat());
						mol.setName("");
						mol.setEmail("");
						mol.setCountry("MY");
						String SecretPIN = PayFinalValue.MOL_SecretPIN;
						String strToHash = (mol.getMerchantID()
								+ mol.getMRef_ID() + mol.getAmount()
								+ mol.getCurrency() + SecretPIN + mol
								.getHeartBeat()).toLowerCase();
						System.out.println("==strToHash==" + strToHash);
						MessageDigest sha1 = MessageDigest.getInstance("SHA1");
						String signature = (PayUtils.bytes2String(sha1
								.digest(strToHash.getBytes()))).toLowerCase();
						mol.setSignature(signature);
						postMsg = PayUtils.postMsgToMol(mol);
					}
					else
					{
						System.out.println("==get hb error=");
					}
				}
				else
				{
					System.out.println("==get res error=");
				}
			}
		}
		System.out.println("==postMsg =" + postMsg);
		response.getOutputStream().write(postMsg.getBytes());
		response.flushBuffer();
		return null;
	}
}
