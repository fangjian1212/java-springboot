/**
 * Created on 2006-09-08
 */
package com.fangjian.framework.utils.self;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class FileUtil {
	/**
	 * Default constructor.
	 *
	 */
	private FileUtil() {
	}

	/**
	 * 从指定的文件路径中读取文件.
	 *
	 * @param filePath
	 *            文件的全路径名,包括文件名
	 * @return String 文件的内容
	 * @throws IOException
	 */
	public static String read(final String filePath) throws IOException {
		File file = new File(filePath);
		InputStream is = new FileInputStream(file);
		String result = read(is);
		is.close();
		return result;
	}

	/**
	 * 从一个输入流中读取文件内容.
	 *
	 * @param is
	 *            InputStream object
	 * @return String The content in the InputSream.
	 * @throws IOException
	 */
	public static String read(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		StringBuffer buf = new StringBuffer();
		int size = 0;
		while ((size = bis.available()) > 0) {
			byte[] temp = new byte[size];
			bis.read(temp);
			buf.append(new String(temp));
		}
		return buf.toString();
	}

	/**
	 * 保存字符串到指定的文件.
	 *
	 * @param filePath
	 *            文件路径,包括文件名
	 * @param content
	 *            文件内容
	 * @throws IOException
	 */
	public static void save(final String filePath, final String content)
			throws IOException {
		File file = new File(filePath);
		OutputStream os = new FileOutputStream(file);
		save(os, content);
		os.close();
	}

	/**
	 * 保存字符串到输出流中.
	 *
	 * @param os
	 *            输出流
	 * @param content
	 *            内容
	 * @throws IOException
	 */
	public static void save(final OutputStream os, final String content)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(os);
		bos.write(content.getBytes());
		bos.flush();
	}

	/**
	 * 得到java class存放的目录
	 *
	 * @return String
	 */
	public static String getRealClassRootPath() {
		URI uri = null;
		try {
			uri = FileUtil.class.getResource("FileUtil.class").toURI();
		} catch (URISyntaxException e1) {
			// TODO
		}
		// String scheme = uri.getScheme();
		String path = uri.getPath();
		int index = path.indexOf("/com/");
		if (index == -1) {
			index = path.indexOf("classes") + "classes".length();
		}
		path = path.substring(0, index);
		return path;
	}

	private static Properties prop = System.getProperties();
	private static String os = prop.getProperty("os.name");
	private static boolean isWin = os.toUpperCase().startsWith("WIN");


	/**
	 * 将本地文件写到统一文件系统
	 *
	 * @param localFileName
	 *            诚寻运行本地系统文件路径
	 * @return 统一文件系统分配的文件名， 失败返回null
	 */
	public static String writeFile(String localFileName) {
		String rFileName = System.currentTimeMillis() + "";
		String dirName;
		if(isWin) {
			dirName = "c:\\temp\\";
		}
		else {
			dirName = "/tmp/";
		}

		try {
			File localFile = new File(localFileName);
			InputStream is = new FileInputStream(localFile);
			BufferedInputStream bis = new BufferedInputStream(is);
			File dirFile = new File(dirName);
			if(dirFile.isFile()) {
				dirFile.delete();
			}
			if(!dirFile.exists()) {
				dirFile.mkdir();
			}
			rFileName = dirName + rFileName;
			File rFile = new File(rFileName);
			OutputStream os = new FileOutputStream(rFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int size = 0;
			while ((size = bis.available()) > 0) {
				byte[] temp = new byte[size];
				bis.read(temp);
				bos.write(temp);
			}
			bos.flush();
			bos.close();
			os.close();
			bis.close();
			is.close();
			System.out.println("rFileName is " + rFileName);
			return rFileName;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	/**
	 * 将二进制流写到统一文件系统
	 *
	 * @param fileContent
	 * @return 统一文件系统分配的文件名， 失败返回null
	 */
	public static String writeFile(byte[] fileContent) {
		String rFileName = System.currentTimeMillis() + "";
		String dirName;
		if(isWin) {
			dirName = "c:\\temp\\";
		}
		else {
			dirName = "/tmp/";
		}

		try {
			File dirFile = new File(dirName);
			if(dirFile.isFile()) {
				dirFile.delete();
			}
			if(!dirFile.exists()) {
				dirFile.mkdir();
			}
			rFileName = dirName + rFileName;
			File file = new File(rFileName);
			OutputStream os = new FileOutputStream(file);
			os.write(fileContent);
			os.flush();
			os.close();
			System.out.println("rFileName is " + rFileName);
			return rFileName;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 将统一文件系统读到本地文件中, 如果本地文件已经存在， 会先被删除
	 *
	 * @param rfileName
	 *            写文件时返回的文件名
	 * @param localFileName
	 *            本地文件路径
	 * @return 成功 true, 失败 false
	 */
	public static boolean readFile(String rfileName, String localFileName) {
		try {
			File file = new File(localFileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			File rFile = new File(rfileName);
			InputStream is = new FileInputStream(rFile);
			BufferedInputStream bis = new BufferedInputStream(is);
			File localFile = new File(localFileName);
			OutputStream os = new FileOutputStream(localFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int size = 0;
			while ((size = bis.available()) > 0) {
				byte[] temp = new byte[size];
				bis.read(temp);
				bos.write(temp);
			}
			bos.flush();
			bos.close();
			os.close();
			bis.close();
			is.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 将统一文件系统读到输出流中
	 *
	 * @param rfileName
	 *            写文件时返回的文件名
	 * @param output
	 *            输出流
	 * @return 成功 true, 失败 false
	 */
	public static boolean readFile(String rfileName, OutputStream output) {
		try {
			File rFile = new File(rfileName);
			InputStream is = new FileInputStream(rFile);
			BufferedInputStream bis = new BufferedInputStream(is);
			int size = 0;
			while ((size = bis.available()) > 0) {
				byte[] temp = new byte[size];
				bis.read(temp);
				output.write(temp);
			}
			output.flush();
			output.close();
			bis.close();
			is.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean readFile(String rfileName, List<String> fileContents, String charsetName) {
		try {
			File rFile = new File(rfileName);
			InputStream is = new FileInputStream(rFile);
			BufferedInputStream bis = new BufferedInputStream(is);
			InputStreamReader inReader = new InputStreamReader(bis, charsetName);
			BufferedReader bufferReader = new BufferedReader(inReader);
			if(fileContents == null) {
				fileContents = new ArrayList<String>();
			}
			while(bufferReader.ready()) {
				fileContents.add(bufferReader.readLine());
			}
			is.close();
			bis.close();
			inReader.close();
			bufferReader.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 在统一文件系统中删除文件
	 *
	 * @param rfileName
	 *            写文件时返回的文件名
	 * @return 成功 true, 失败 false
	 */
	public static boolean deleteFile(String rfileName) {
		File file = new File(rfileName);
		return file.delete();
	}

}
