package com.archy.dezhou.util;

import com.archy.dezhou.jzlib.JZlib;
import com.archy.dezhou.jzlib.ZInputStream;
import com.archy.dezhou.jzlib.ZOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ComPressUtil
{

	public static String getXmlString()
	{
		System.out.println("getXmlString is called!");
		InputStream stmp = "".getClass().getResourceAsStream("/puker.xml");

		int i = -1;
		try
		{
			byte[] b = new byte[stmp.available()];
			StringBuffer sb = new StringBuffer();
			DataInputStream dis = new DataInputStream(stmp);
			dis.read(b, 0, dis.available());
			String content = new String(b);
			return content;
		}
		catch (Exception ex)
		{
			System.out.println("发生了异常！");
			ex.printStackTrace();
			return null;
		}

	}

	public static byte[] Zip(byte[] object) {
		byte[] data = null;
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// 创建字节数组输出流
			ZOutputStream zOut = new ZOutputStream(out,
					JZlib.Z_BEST_COMPRESSION);
			// 创建压缩输出流，后面参数表示压缩率
			DataOutputStream objOut = new DataOutputStream(zOut);
			// 创建数据输出流，数据压缩入口
			objOut.write(object);
			objOut.flush();
			zOut.close();
			data = out.toByteArray();
			// 返回压缩数据
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return data;
	}

	public static byte[] UnZip(byte[] object) {
		int MAXLENGTH = 1024 * 8;
		int BUFFER = 1024;
		byte[] data = new byte[MAXLENGTH];
		byte[] truedata = null;
		// MAXLENGTH表示输出数据的最大长度
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(object);
			// 创建字节数组输入流，数据解压入口
			ZInputStream zIn = new ZInputStream(in);
			// 创建解压工作流
			DataInputStream objIn = new DataInputStream(zIn);
			// 创建数据输入流，用于解压数据输出
			int len = 0;
			int count;
			while ((count = objIn.read(data, len, len + BUFFER)) != -1)
			{
				len = len + count;
			}
			// 将原有数据以BUFFER长度分段解压
			// BUFFER表示流缓冲长度，每次解压长度
			truedata = new byte[len];
			System.arraycopy(data, 0, truedata, 0, len);
			// 去除空值，得到原始数据
			objIn.close();
			zIn.close();
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return truedata;
	}
}
