package com.yl.web.dao.Imp;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yl.util.PayFinalValue;
import com.yl.vo.MyCard;
import com.yl.web.dao.PaymentDao;
import com.yl.web.po.Paypal;

public class PaymentDaoImp implements PaymentDao
{
	private JdbcTemplate jt;

	public void setJt(JdbcTemplate jt)
	{
		this.jt = jt;
	}

	public boolean isDealOrder(String orderID)
	{
		System.out.println("==checkTxnId===");
		String sql = "select * from paylog where state='Y' and orderID=?";
		Object[] params = new Object[]
		{ orderID };
		int[] types = new int[]
		{ Types.VARCHAR };
		List paypal = jt.queryForList(sql, params, types);
		System.out.println(paypal.size());
		return (paypal != null && paypal.size() == 1) ? true : false;
	}

	public HashMap<String, String> DealBillingOrder(String type, String AuthCode)
	{
		System.out.println("==isDealBilling===");
		String sql = "select uid,state,money,orderID,email from paylog where ";
		// email中保存的是授权码 唯一
		if (type.equals("ac"))
		{
			sql += "email=?";
		}
		else if (type.equals("ts"))
		{
			sql += "orderID=?";
		}
		Object[] params = new Object[]
		{ AuthCode };
		int[] types = new int[]
		{ Types.VARCHAR };
		List l = jt.queryForList(sql, params, types);
		System.out.println(l.size());
		HashMap<String, String> user = new HashMap<String, String>();
		if (l != null && l.size() == 1)
		{
			user.put("uid", ((Map) l.get(0)).get("uid").toString());
			user.put("state", ((Map) l.get(0)).get("state").toString());
			user.put("money", ((Map) l.get(0)).get("money").toString());
			user.put("orderID", ((Map) l.get(0)).get("orderID").toString());
			user.put("email", ((Map) l.get(0)).get("email").toString());
		}
		return user;
	}

	public void saveTranRecord(Paypal p)
	{
		System.out.println("====saveTranRecord======");
		String sql = "insert into paypal(uid,txnid,itemname,itemnumber,paymentstatus,paymentamount,paymentcurrency,receiveremail,payeremail,paymentdate) values(?,?,?,?,?,?,?,?,?,?)";
		Object[] params = new Object[]
		{ p.getUid(), p.getTxnid(), p.getItemname(), p.getItemnumber(),
				p.getPaymentsatus(), p.getPaymentamount(),
				p.getPaymentcurrency(), p.getReceiveremail(),
				p.getPayeremail(), p.getPaymentdate() };
		int[] types = new int[]
		{ Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.DATE };
		int i = jt.update(sql, params, types);
		System.out.println("i=" + i);
		params = null;
		types = null;
	}

	public void updateUserGold(String uid, int gold)
	{
		String sql = "update userinfo set gold=gold+'" + gold + "' where uid='"
				+ uid + "'";
		jt.update(sql);
	}

	public void updateMCAuthCode(String AuthCode, String mcTradeSeq,
			String orderID)
	{
		String sql = "update paylog set email='" + AuthCode + "',txnid='"
				+ mcTradeSeq + "' where orderID='" + orderID + "'";
		jt.update(sql);
	}

	// check橘子游戏支付平台orderID是否唯一
	public boolean checkOrderID(String orderID)
	{
		String sql = "select * from paylog where orderID=?";
		Object[] params = new Object[]
		{ orderID };
		int[] types = new int[]
		{ Types.VARCHAR };
		List order = jt.queryForList(sql, params, types);
		System.out.println(order.size());
		return (order != null && order.size() == 1) ? true : false;
	}

	// gamania 保存订单到数据库
	public void saveOrder(String orderID, String uid, String money,
			String pmethod, String clientIP)
	{
		String paytype = "";
		int smoney = 0;
		if (pmethod.equals(PayFinalValue.PV_PAY_GAMANIA_P)
				|| pmethod.equals(PayFinalValue.PV_PAY_GAMANIA_A))
		{
			paytype = PayFinalValue.PV_PAY_GAMANIA;
			smoney = Integer.parseInt(money);
		}
		else if (pmethod.equals(PayFinalValue.PV_PAY_PAYPAL_Paypal))
		{
			paytype = PayFinalValue.PV_PAY_PAYPAL;
			smoney = (int) (Double.parseDouble(money) * 100);
		}
		else if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_InGame))
		{
			paytype = PayFinalValue.PV_PAY_MYCARD;
		}
		else if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_Point))
		{
			paytype = PayFinalValue.PV_PAY_MYCARD;
			smoney = Integer.parseInt(money);
		}
		else if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_Billing))
		{
			paytype = PayFinalValue.PV_PAY_MYCARD;
			smoney = Integer.parseInt(money);
		}
		else if (pmethod.equals(PayFinalValue.PV_PAY_MOL_Mol))
		{
			paytype = PayFinalValue.PV_PAY_MOL;
			smoney = Integer.parseInt(money);
		}
		System.out.println("====saveTranRecord======");
		String sql = "insert into paylog(orderID,uid,paytype,paymethod,money,state,clientIP,time) values(?,?,?,?,?,?,?,now())";
		Object[] params = new Object[]
		{ orderID, uid, paytype, pmethod, smoney, "N", clientIP, };
		int[] types = new int[]
		{ Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.INTEGER, Types.VARCHAR, Types.VARCHAR };
		int i = jt.update(sql, params, types);
		params = null;
		types = null;
	}

	// 修改支付订单信息
	public void updateOrder(String payType, int gmoney, String gmethod,
			String orderID, String strPayStatus)
	{
		String sql = "";
		if (payType.equals(PayFinalValue.PV_PAY_GAMANIA))
		{
			if (strPayStatus.equals("s"))
			{
				sql = "update paylog set state = 'Y',gmoney=" + gmoney
						+ ",gmethod='" + gmethod
						+ "',exchange='N' where orderID='" + orderID + "'";
			}
			else
			{
				sql = "update paylog set state='" + strPayStatus + "',gmoney="
						+ gmoney + ",gmethod='" + gmethod + "' where orderID='"
						+ orderID + "'";
			}
		}
		else if (payType.equals(PayFinalValue.PV_PAY_PAYPAL_Paypal))
		{
			sql = "update paylog set email='" + strPayStatus + "',txnid='"
					+ gmethod + "',state = 'Y',gmoney=" + gmoney
					+ ",exchange='N' where orderID='" + orderID + "'";
		}
		else if (payType.equals(PayFinalValue.PV_PAY_MYCARD_Point))
		{
			sql = "update paylog set email='" + strPayStatus + "',txnid='"
					+ gmethod + "',state = 'Y',gmoney=" + gmoney
					+ ",exchange='N' where orderID='" + orderID + "'";
		}
		else if (payType.equals(PayFinalValue.PV_PAY_MYCARD_InGame))
		{
			sql = "update paylog set email='" + strPayStatus + "',txnid='"
					+ gmethod + "',state = 'Y',gmoney=" + gmoney
					+ ",exchange='N' where orderID='" + orderID + "'";
		}
		else if (payType.equals(PayFinalValue.PV_PAY_MYCARD_Billing))
		{
			sql = "update paylog set state = 'Y',gmoney=" + gmoney
					+ ",exchange='N' where orderID='" + orderID + "'";
		}
		else if (payType.equals(PayFinalValue.PV_PAY_MOL_Mol))
		{
			sql = "update paylog set state = 'Y',txnid='" + gmethod
					+ "',gmoney=" + gmoney + ",exchange='N' where orderID='"
					+ orderID + "'";
		}
		jt.update(sql);
	}

	// gamania 告知用户充值情况
	public String backPayMsg(String orderID)
	{
		String orderQuery = "select gmoney from paylog Where orderID ='"
				+ orderID + "'and state = 'Y'";
		List l = jt.queryForList(orderQuery);
		return (l != null && l.size() == 1) ? ((Map) l.get(0)).get("gmoney")
				.toString() : "";
	}

	public ArrayList<MyCard> QueryMCTradeByDate(String sdate, String edate)
	{
		String TradeQuery = "select txnid,uid,orderID,DATE_FORMAT(time,'%Y-%m-%d %H:%m:%s') as time from paylog where paymethod='mc_ingame' and state = 'Y' and (time between '"
				+ sdate + "' and '" + edate + "')";
		List l = jt.queryForList(TradeQuery);
		ArrayList<MyCard> mclist = new ArrayList<MyCard>();
		if (l != null && l.size() > 0)
		{
			for (int i = 0; i < l.size(); i++)
			{
				MyCard mc = new MyCard();
				mc.setCust_ID(((Map) l.get(i)).get("uid").toString());
				mc.setGAME_TNO(((Map) l.get(i)).get("orderID").toString());
				mc.setMyCard_ID(((Map) l.get(i)).get("txnid").toString());
				mc.setTradeok_time(((Map) l.get(i)).get("time").toString());
				mc.setTRADE_NO(((Map) l.get(i)).get("txnid").toString());
				mclist.add(mc);
			}
		}
		return mclist;
	}

	public MyCard QueryMCTradeByID(String myCardID)
	{
		String TradeQuery = "select txnid,uid,orderID,DATE_FORMAT(time,'%Y-%m-%d %H:%m:%s') as time from paylog where txnid ='"
				+ myCardID + "' and state = 'Y'";
		List l = jt.queryForList(TradeQuery);
		MyCard mc = null;
		if (l != null && l.size() == 1)
		{
			mc = new MyCard();
			mc.setCust_ID(((Map) l.get(0)).get("uid").toString());
			mc.setGAME_TNO(((Map) l.get(0)).get("orderID").toString());
			mc.setMyCard_ID(((Map) l.get(0)).get("txnid").toString());
			mc.setTradeok_time(((Map) l.get(0)).get("time").toString());
			mc.setTRADE_NO(((Map) l.get(0)).get("txnid").toString());
		}
		return mc;
	}
}