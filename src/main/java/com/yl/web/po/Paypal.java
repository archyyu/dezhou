package com.yl.web.po;

public class Paypal
{
	private String uid;
	private String txnid;
	private String itemname;
	private int itemnumber;
	private String paymentsatus;

	private String paymentamount;
	private String paymentcurrency;
	private String receiveremail;
	private String paymentdate;
	private String payeremail;

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getTxnid()
	{
		return txnid;
	}

	public void setTxnid(String txnid)
	{
		this.txnid = txnid;
	}

	public String getItemname()
	{
		return itemname;
	}

	public void setItemname(String itemname)
	{
		this.itemname = itemname;
	}

	public int getItemnumber()
	{
		return itemnumber;
	}

	public void setItemnumber(int itemnumber)
	{
		this.itemnumber = itemnumber;
	}

	public String getPaymentsatus()
	{
		return paymentsatus;
	}

	public void setPaymentsatus(String paymentsatus)
	{
		this.paymentsatus = paymentsatus;
	}

	public String getPaymentamount()
	{
		return paymentamount;
	}

	public void setPaymentamount(String paymentamount)
	{
		this.paymentamount = paymentamount;
	}

	public String getPaymentcurrency()
	{
		return paymentcurrency;
	}

	public void setPaymentcurrency(String paymentcurrency)
	{
		this.paymentcurrency = paymentcurrency;
	}

	public String getReceiveremail()
	{
		return receiveremail;
	}

	public void setReceiveremail(String receiveremail)
	{
		this.receiveremail = receiveremail;
	}

	public String getPaymentdate()
	{
		return paymentdate;
	}

	public void setPaymentdate(String paymentdate)
	{
		this.paymentdate = paymentdate;
	}

	public String getPayeremail()
	{
		return payeremail;
	}

	public void setPayeremail(String payeremail)
	{
		this.payeremail = payeremail;
	}

}
