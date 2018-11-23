package com.yl.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yl.Global.ConstList;

public class FileProperty
{

	public static File chooseFile(String filepath)
	{
		File file = null;
		try
		{
			file = new File(filepath);
		}
		catch (Exception ex)
		{
			ConstList.config.logger.error(ex.getMessage());
		}
		return file;
	}

	public static void showFileProperties(File file)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 H:mm:ss");
		ConstList.config.logger.debug("文件是否存在："
				+ (file.exists() ? "存在" : "不存在"));
		ConstList.config.logger.debug(file.getAbsolutePath());
		ConstList.config.logger.debug("是否可读："
				+ (file.setWritable(true) ? "可读写" : "可读写"));
		ConstList.config.logger
				.debug("是否隐藏：" + (file.isHidden() ? "隐藏" : "显示"));
		ConstList.config.logger.debug("是否文件：？" + (file.isFile() ? "是" : "否"));
		ConstList.config.logger.debug("最后修改时间："
				+ sdf.format(new Date(file.lastModified())) + "("
				+ file.lastModified() + "毫秒)");
		ConstList.config.logger.debug("文件大小：" + file.length() + "字节");
	}

	public static void main(String[] args)
	{

		showFileProperties(chooseFile("res/log4j2.properties"));
	}

}
