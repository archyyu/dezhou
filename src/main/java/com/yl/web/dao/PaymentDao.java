package com.yl.web.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.yl.vo.MyCard;
import com.yl.web.po.Paypal;

public interface PaymentDao
{
	// 检查 paypal中txn_id 是否已经处理过
	public boolean isDealOrder(String txnId);

	// 将交易记录保存到数据库
	public void saveTranRecord(Paypal p);

	// 付款成功，更新付款用户的金币
	public void updateUserGold(String uid, int gold);

	// 检查 gamania中orderID 是否唯一
	public boolean checkOrderID(String orderID);

	// gamania 保存订单到数据库
	public void saveOrder(String orderID, String uid, String money,
			String pmethod, String clientIP);

	// gamania 更新订单到数据库
	public void updateOrder(String payType, int gmoney, String gmethod,
			String orderID, String strPayStatus);

	// gamania 根据充值金额，提供金币给用户
	public String backPayMsg(String orderID);

	public HashMap<String, String> DealBillingOrder(String type, String AuthCode);

	public void updateMCAuthCode(String AuthCode, String mcTradeSeq,
			String orderID);

	public ArrayList<MyCard> QueryMCTradeByDate(String sdate, String edate);

	public MyCard QueryMCTradeByID(String myCardID);
}
