package com.archy.dezhou.util;

public class PayFinalValue
{

	/**
	 * 固定参数常量
	 */
	public static final String PV_PAY_MYCARD_Billing = "mc_billing";
	public static final String PV_PAY_MYCARD_InGame = "mc_ingame";
	public static final String PV_PAY_MYCARD_Point = "mc_point";
	public static final String PV_PAY_PAYPAL_Paypal = "paypal";
	public static final String PV_PAY_GAMANIA_P = "p";
	public static final String PV_PAY_GAMANIA_A = "a";
	public static final String PV_PAY_MOL_Mol = "mol";

	public static final String PV_PAY_MOL = "mol";
	public static final String PV_PAY_GAMANIA = "gamania";
	public static final String PV_PAY_PAYPAL = "paypal";
	public static final String PV_PAY_MYCARD = "mycard";

	/**
	 * MyCard金流代号
	 */
	public static final String MYCARD_YTDX = "sps1";
	public static final String MYCARD_YCDX = "sps2";
	public static final String MYCARD_ZHDX = "sps3";
	public static final String MYCARD_TGD = "sps4";
	public static final String MYCARD_ZHSH = "sps5";
	public static final String MYCARD_XYK_SD = "sps6";
	public static final String MYCARD_XYK_HLDH = "sps7";
	public static final String MYCARD_HINET = "sps8";
	public static final String MYCARD_SEEDNET = "sps9";
	public static final String MYCARD_SONET = "sps10";
	public static final String MYCARD_ZHXT = "sps11";
	public static final String MYCARD_HNYH = "sps12";
	public static final String MYCARD_ZHYZ = "sps13";
	public static final String MYCARD_GTSH = "sps14";
	public static final String MYCARD_TXYH = "sps15";
	public static final String MYCARD_XGYH = "sps16";
	public static final String MYCARD_TBFB = "sps17";
	public static final String MYCARD_TWYH = "sps18";
	public static final String MYCARD_SHYH = "sps19";
	public static final String MYCARD_TDYH = "sps20";
	public static final String MYCARD_DYYH = "sps21";
	public static final String MYCARD_YSYH = "sps22";
	public static final String MYCARD_ZFYH = "sps23";
	public static final String MYCARD_ZHYH = "sps24";
	public static final String MYCARD_ZFB = "sps25";

	/**
	 * 固定数值常量
	 */
	public static final int GIVEN_BET_EveryDay = 5000; // 每日赠送
	public static final int GIVEN_BET_NewPlayer = 50000; // 新用户赠送
	public static final int TIME_COUNT_PT = 30; // 普通区定时：15秒
	public static final int TIME_COUNT_KS = 30; // 快速场：7秒 由于网络延时服务器多出了 3～4秒

	public static final int Point_TMP = 50;
	public static final int Point_TZF = 1;
	public static final int Point_TTZ = 1;

	public static final int GIVEN_Point_CM = 10; // 新用户赠送 10个普通赛点
	public static final int GIVEN_Point_ZF = 1; // 新用户赠送 1个征服赛点
	public static final int GIVEN_Point_TZ = 1; // 新用户赠送 1个挑战赛点

	/**
	 * merchant Info about MOL
	 */
	public static final String MOL_API_URL = "https://global.mol.com/api/login";
	public static final String MOL_MerchantID = "201011266124";
	public static final String MOL_SecretPIN = "pvqf6124pvqf";
	// Currency Allowed: USD;SGD;MYR;INR;PHP;IDR
	public static final String MOL_Merchant_Admin_Module = "https://global.mol.com/api/login_report/login.aspx";
	public static final String MOL_UserName = "PUTZONEHKLimited";
	public static final String MOL_Password = "pvqf6124";

	public static final String MOL_AcountEmail = "PUTZONEHKLimited_demo_01@gmail.com";
	public static final String MOL_AcountPsd = "pvqf6124";

	public static final String MOL_URL_GetHeartBeat = "/s_module/heartbeat.asmx/GetHeartBeat?";
	public static final String MOL_URL_Purchase = "/u_module/purchase.aspx";
	public static final String MOL_URL_Querytrxstatus = "/s_module/querytrxstatus.asmx/queryTrxStatus?";

	/**
	 * merchant Info about MyCard
	 * 后端查询：http://bargain.mycard520.com.tw/SwWeb/RootDefault.aspx think think
	 */
	public static final String MyCard_URL = "https://b2b.mycard520.com.tw";
	public static final String MyCard_Billing_GameFacID = "PZ";

	public static final String MyCard_URL_getBillingAuth = "/MyCardBillingRESTSrv/MyCardBillingRESTSrv.svc/Auth/";
	public static final String MyCard_FactoryId = "MFD0000065";
	public static final String MyCard_ServiceId = "MFSD000398";
	public static final String MyCard_GameFacID = "GFD00363";
	public static final String MyCard_URL_Point = "https://member.mycard520.com.tw/MemberLoginService/";
	public static final String MyCard_URL_ServiceAuth = "/MyCardPointPaymentServices/MyCardPpServices.asmx/MyCardMemberServiceAuth?";
	public static final String MyCard_URL_PaymentConfirm = "/MyCardBillingRESTSrv/MyCardBillingRESTSrv.svc/PaymentConfirm?";
	public static final String MyCard_URL_MemberCLRender = "/MyCardPointPaymentServices/MyCardPpServices.asmx/MemberCostListRender?";
	public static final String MyCard_URL_InGameRender = "https://b2b.mycard520.com.tw/MyCardService/MyCardService.asmx/MyCardRender";
	public static final String MyCard_URL_TradeQuery = "/MyCardPointPaymentServices/MyCardPpServices.asmx/MyCardMemberServiceTradeQuery?";
	public static final String MyCard_URL_Billing = "https://www.mycard520.com.tw/MyCardBilling/";

}
