package com.zhl.gradedemo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileHelper 
{
	private static int BUFFER_SIZE = 4096;
	
	public static File extractResourceOnce(Context context, String name) 
	{
		try 
		{
			String pureName = name.replaceAll("\\.[^.]*$", "");
			Log.i("Pure", pureName);
			
			File filesDir = getFilesDir(context);
			File targetDir = new File(filesDir, pureName);

			String md5sum = md5sum(context.getAssets().open(name));

			File md5sumFile = new File(targetDir, ".md5sum");
			if (targetDir.isDirectory()) {
				if (md5sumFile.isFile()) {
					String md5sum2 = readFileAsString(md5sumFile);
					if (md5sum2.equals(md5sum)) {
						return targetDir; /* already extracted */
					}
				}
				removeDirectory(targetDir); /* remove old dirty resource */
			}

			unzip(context.getAssets().open(name), targetDir);
			writeFileAsString(md5sumFile, md5sum);

			return targetDir;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static File extractProvisionOnce(Context context, String name) {

		try 
		{
			File targetFile = new File(getFilesDir(context), name);
			copyInputStreamToFile(context.getAssets().open(name), targetFile);
			return targetFile;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static File getFilesDir(Context context) 
	{

		File targetDir = null;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
		{
			// targetDir = context.getExternalFilesDir(null); // not support
			// android 2.1
			targetDir = new File(Environment.getExternalStorageDirectory(),
					"Android/data/" + context.getApplicationInfo().packageName
							+ "/files");
			if (!targetDir.exists()) 
			{
				targetDir.mkdirs();
			}
		}

		if (targetDir == null || !targetDir.exists()) {
			targetDir = context.getFilesDir();
		}
		return targetDir;
	}
	
	public static String md5sum(InputStream is)
	{
		int bytes;
		byte buf[] = new byte[BUFFER_SIZE];
		try 
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			while ((bytes = is.read(buf, 0, BUFFER_SIZE)) > 0) 
			{
				md.update(buf, 0, bytes);
			}
			is.close();
			return bytes2hex(md.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String bytes2hex(byte[] bytes) 
	{
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) 
		{
			int v = bytes[i] & 0xff;
			if (v < 16) 
			{
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}
	
	public static String readFileAsString(File file) throws IOException 
	{
		String line;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}
	
	public static void writeFileAsString(File file, String str) throws IOException 
	{
		FileWriter fw = new FileWriter(file);
		fw.write(str);
		fw.close();
	}
	
	public static void removeDirectory(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					removeDirectory(files[i]);
				}
				files[i].delete();
			}
			directory.delete();
		}
	}
	
	public static void unzip(InputStream is, File targetDir)
			throws IOException 
	{
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is,
				BUFFER_SIZE));
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				new File(targetDir, ze.getName()).mkdirs();
			} else {

				File file = new File(targetDir, ze.getName());
				File parentdir = file.getParentFile();
				if (parentdir != null && (!parentdir.exists())) {
					parentdir.mkdirs();
				}

				int pos;
				byte[] buf = new byte[BUFFER_SIZE];
				OutputStream bos = new FileOutputStream(file);
				while ((pos = zis.read(buf, 0, BUFFER_SIZE)) > 0) {
					bos.write(buf, 0, pos);
				}
				bos.flush();
				bos.close();

			}
		}
		zis.close();
		is.close();
	}
	
	public static void copyInputStreamToFile(InputStream is, File file)
			throws Exception {
		int bytes;
		byte[] buf = new byte[BUFFER_SIZE];

		FileOutputStream fos = new FileOutputStream(file);
		while ((bytes = is.read(buf, 0, BUFFER_SIZE)) > 0) {
			fos.write(buf, 0, bytes);
		}

		is.close();
		fos.close();
	};
	
	public static String sha1(String message) 
	{
		try 
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(message.getBytes(), 0, message.length());
			return bytes2hex(md.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
