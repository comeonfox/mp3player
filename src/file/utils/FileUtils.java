package file.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Environment;

/**
 * �ļ��๤����
 * ���������SD���ϴ���Ŀ¼�������ļ����ж��ļ��Ƿ���ڣ���InputStream��
 * ��д���ļ����ݣ���ȡSD����ĳ��Ŀ¼���ļ���Ϣ�б�
 * @author Administrator
 *
 */
public class FileUtils {
	private String SDCardRoot;// SD���豸��Ŀ¼

	public FileUtils() {
		File externalStorageDirectory = Environment.getExternalStorageDirectory();
		SDCardRoot = externalStorageDirectory.getAbsolutePath()+ File.separator;
	}

	/**
	 * ��SD���ﴴ��һ��Ŀ¼�����Ŀ¼�Ѵ����򷵻�null
	 * @param dir
	 * @return
	 */
	public File createPathInSDCard(String dir) {
		//ָ��һ���ļ������Ŀ¼������mp3���ش�SDCard�ĸ�Ŀ¼�����Ŀ¼��·��
		String path = absoluteDir(dir);
		File fold = new File(path);//�����ļ��ж���
		if(!fold.exists()){
			fold.mkdir();//����һ���ļ���
		}
		return fold;
	}

	/**
	 * ��SD���ﴴ��һ�����ļ����ļ���Ŀ¼��Ϊdir���ļ���ΪfileName ����ļ��Ѵ����򷵻�null
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public File createFileInSDCard(String dir, String fileName) {
		String path = absoluteDir(dir)+fileName;
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile(); 
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * �ж�SD�����ļ��Ƿ����
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String dir, String fileName) {
		String path = absoluteDir(dir)+fileName;
		File file = new File(path);
		return file.exists();
	}

	/**
	 * �����ݴ�InputStream��д��SD����
	 * @param dir Ŀ¼��
	 * @param fileName �ļ���
	 * @param input ������
	 * @return д��ʧ�ܷ���null�����򷵻�д����ļ�
	 */
	public File writeToSDCardFromInputStream(String dir, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		createPathInSDCard(dir);//����һ��Ŀ¼
		file = createFileInSDCard(dir, fileName);//����������������һ���ļ�
		byte[] buffer = new byte[1024 * 10];
		try {
			output = new BufferedOutputStream(new FileOutputStream(file));
			int tempSize = 0;
			while ((tempSize = input.read(buffer)) != -1) {
				output.write(buffer, 0, tempSize);
			}
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				output.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return file;
	}

	/**
	 * ��ȡSD���ϵ��ı��ļ�������
	 * @param dir �ı��ļ���SD�������Ŀ¼
	 * @param fileName �ı��ļ����ļ���
	 * @return String �ı��ļ��������ַ���
	 */
	public String readTextFile(String dir, String fileName){
		String path = absoluteDir(dir) + fileName;//��ȡ�ı��ļ���SD���ϵ�·��
		File file = new File(path);
		char[] buf = new char[1024 * 5];//�洢�����lrc�ļ�
		String lrc = null;//�洢�ı��ļ�����
		FileReader fileReader = null;
		try 
		{
			fileReader = new FileReader(file);
			while(-1 != (fileReader.read(buf))){
				
			}
			lrc = new String(buf);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lrc;
	}
	/**
	 * ���SD����ĳ��Ŀ¼�µ��ļ��б��ļ��������û�ָ��
	 * @param dir SD���ϵ�ĳ��Ŀ¼�����磺"mp3"
	 * @param fileType �ļ���׺���ͣ����磺".mp3"��".lrc"
	 * @return
	 */
	public List<File> getFilesList(String dir, String fileType){
		List<File> fileList = new ArrayList<File>();//����һ���洢�û���Ҫ���ļ����͵��б�
		File sdCardDir = new File(SDCardRoot + dir);//����Ŀ¼����
		if(!fileType.startsWith(".")){
			fileType = "."+fileType;
		}
		FileNameFilter fileNameFilter = new FileNameFilter(fileType);
		File[] files = sdCardDir.listFiles(fileNameFilter);
		//���û�ҵ�����MP3�ļ��򷵻�null
		if(null == files){
			return null;
		}
		for(int i = 0 ; i < files.length ; i++){
			fileList.add(files[i]);
		}
		return fileList;
	}
	
	/**
	 * �ļ����������ڲ��࣬������Թ����û�ָ�����ļ�����
	 * @author Administrator
	 */
	private class FileNameFilter implements FilenameFilter 
	{
		private String type = null;// �û���Ҫ���ļ���׺����

		public FileNameFilter(String type) 
		{
			this.type = type;
		}

		/**
		 * ����ļ��ĺ�׺��
		 * @param filename �ļ���
		 * @return String ��׺��
		 */
		private String getFileSuffix(String filename)
		{
			int index = filename.indexOf(".");
			return filename.substring(index, filename.length());
		}
		
		public boolean accept(File dir, String filename)
		{
			if(type.equalsIgnoreCase(getFileSuffix(filename)))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	/**
	 * ��SDCard�ϻ��һ���ļ���URI�ַ���
	 * @param dir �ļ���SD����Ŀ¼·��
	 * @param fileName �ļ���
	 * @return String ���ļ���Uri�ַ�������
	 */
	public String getFileUriStringFromSDCard(String dir, String fileName){
		String path = null;
		path = absoluteDir(dir)+fileName;//����ļ��ľ���·��
		return "file://"+path;
	}
	
	/**
	 * ��SDCard�ϻ��ָ���ļ���Uri����
	 * @param dir ָ���ļ���Ŀ¼��
	 * @param fileName �ļ���
	 * @return Uri���� 
	 */
	public Uri getFileUriFromSDCard(String dir, String fileName){
		String path = null;
		path = absoluteDir(dir)+fileName;//����ļ��ľ���·��
		return Uri.parse("file:/"+path);
	}
	
	/**
	 * ָ��һ���ļ������Ŀ¼������mp3���ش�SDCard�ĸ�Ŀ¼�����Ŀ¼��·��
	 * @param dir "mp3/" ����"mp3"
	 * @return "sdcard/mp3/"
	 */
	private String absoluteDir(String dir){
		String path = null;
		if(".".equals(dir)){
			path = SDCardRoot;
		}
		else
		{
			if(!dir.endsWith(File.separator)){
				dir = dir + File.separator;
			}
			path = SDCardRoot + dir;
		}
		return path;
	}

	public String getSDCardRoot() {
		return SDCardRoot;
	}

	public void setSDCardRoot(String sDCardRoot) {
		SDCardRoot = sDCardRoot;
	}
}
