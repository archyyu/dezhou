package com.yl.mail;

public class Main
{

	public static void main(String[] args)
	{
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("wang735164515@163.com");
		mailInfo.setPassword("wang86821455");// 您的邮箱密码
		mailInfo.setFromAddress("wang735164515@163.com");
		mailInfo.setToAddress("shenrh@hbyongqin.com");
		mailInfo.setSubject("密码重置");
		mailInfo.setContent("你的密码被修改为3543254325432");
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo);// 发送文体格式
		sms.sendHtmlMail(mailInfo);// 发送html格式
	}
}
