package player.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.UnknownServiceException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import player.constant.Constant;

import file.utils.FileUtils;

/**
 * ����ʵ�ִ�ָ����URL�����ļ������������ı��ļ�Ҳ�������ض������ļ�
 * �����ж��Ƿ����ӵ�������
 * @author Administrator
 */
public class HttpDownloader 
{
	private URL url = null;//�洢�ļ���URL����
	private String urlStr = null;//�洢�ļ���url�ַ���
	
	public static final int FILE_DOWNLOAD_ERROR = -1;
	public static final int FILE_DOWNLOAD_SUCCESS = 0;
	public static final int FILE_DOWNLOAD_EXIST = 1;
	
	public HttpDownloader()
	{
		
	}
	
	/**
	 * ���� String ��ʾ��ʽ���� URL ����
	 * @param urlstr ����Ϊ URL ������ String
	 */
	public HttpDownloader(String urlstr)
	{
		this.urlStr = urlstr;
		try 
		{
			url = new URL(urlStr);
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public HttpDownloader(URL url)
	{
		this.url = url;
	}
	
	/**
	 * @param protocol Ҫʹ�õ�Э�����ơ�
	 * @param host �������ơ�
	 * @param port �����˿ںš�
	 * @param file �����ϵ��ļ�
	 * @param handler URL �����������
	 */
	public HttpDownloader(String protocol,
	           		      String host,
	                      int port,
	                      String file,
	                      URLStreamHandler handler)
	{
		try
		{
			url = new URL(protocol, host, port, file, handler);
			urlStr = url.toString();
		} 
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	public HttpDownloader(String protocol,
					      String host,
					      int port,
					      String file)
	{
		this(protocol, host, port, file, null);
	}

	/**
	 * �ж�Ҫ���ص��ļ���URL��ַ�Ƿ���ڸ��ļ�
	 * @return true �����ļ�
	 *         false �������ļ�
	 */
	private boolean isFileUrlAvailable(String urlStr){
		this.urlStr = urlStr;
		if(urlStr.endsWith("null")){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * �����ļ���URL�������ı��ļ����ļ�����ֵ���������ı��ļ�������
	 * @param url �ļ���Զ�̷�������URL ���磺http://219.245.92.63:8090/mp3/a.mp3
	 * @return  String �����ı��ļ�������,���ļ��������򷵻�null
	 */
	public String downloadTxtFile(String urlstr)
	{
		urlStr = urlstr;
		//���ļ��������򷵻�null
		if(!isFileUrlAvailable(urlstr)){
			return null;
		}
		try
		{
			url = new URL(urlStr);
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace();
		}
		return download_txt();
	}
	/**
	 * �����ı��ļ����ļ�����ֵ���������ı��ļ�������
	 * @param protocol String Э��
	 * @param host String ��������IP��ַ
	 * @param port �˿ں�
	 * @param dir Ŀ¼
	 * @param fileName �ļ���
	 * @return String �����سɹ������ı��ļ����ݣ����򷵻�null
	 */
	public String downloadTxtFile(String protocol, String host, String port, String dir, String fileName){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol).append(":").append(File.separator).append(File.separator).append(host).append(":").append(port).append(File.separator).append(dir).append(File.separator).append(fileName);
		
		return downloadTxtFile(sb.toString());
	}
	
	/**
	 * �����ı��ļ����ļ�����ֵ���������ı��ļ�������
	 * @return String �����سɹ������ı��ļ����ݣ����򷵻�null
	 */
	public String downloadTxtFile()
	{
		if(null == url || null == urlStr)
		{
			return null;
		}
		else
		{
			return download_txt();
		}
	}
	
	/**
	 * ����һ���ı��ļ��ľ���ʵ��
	 * 1 ����һ��URL����
	 * 2 ͨ��URL���󣬴���һ��HttpURLConnection����
	 * 3 �õ�InputStream
	 * 4 ��InputStream���ж�ȡ���� 
	 * @return String �����Ѿ����ص��ı��ļ�������
	 */
	private String download_txt()
	{
		String line = null;
		StringBuffer strbuf = new StringBuffer();
		BufferedReader bufReader = null;
		InputStreamReader reader=null;
		InputStream inputStream=null;
		try 
		{
			inputStream = getInputStreamFromURL(urlStr);
			reader = new InputStreamReader(inputStream, "GB2312");
			bufReader = new BufferedReader(reader);
			while(true)
			{
				line = bufReader.readLine();
				if(null != line)
				{
					strbuf.append(line);
				}
				else
				{
					break;
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				inputStream.close();//���������û�����Ͳ�������Ӧ�ó���
				reader.close();
				bufReader.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return strbuf.toString();
	}
	
	/**
	 * ���ض������ļ������洢��ָ���ı���SD��Ŀ¼�£�����ָ�����ļ����洢
	 * @param urlstr �ļ���Զ�̷�������URL�����磺http://219.245.92.63:8090/mp3/a.mp3
	 * @param dir Ҫ�洢�ڱ��ص�SD�������Ŀ¼��
	 * @param fileName Ҫ�洢�ڱ��ص�SD�����ļ���
	 * @return -1���ļ����س���
	 *          0���ļ����سɹ�
	 *          1���ļ��Ѿ�����
	 */
	public int downloadBinaryFileToSDCard(String urlstr, String dir, String fileName)
	{
		if(!isFileUrlAvailable(urlstr)){
			return FILE_DOWNLOAD_ERROR;
		}
		urlStr = urlstr;
		InputStream input = null;
		FileUtils fileUtils = new FileUtils();
		if(fileUtils.isFileExist(dir, fileName)){
			return FILE_DOWNLOAD_EXIST;
		}
		else{
			try
			{
				input = getInputStreamFromURL(urlStr);
				File resultFile = fileUtils.writeToSDCardFromInputStream(dir, fileName, input);
				if(null == resultFile){
					return FILE_DOWNLOAD_ERROR;
				}
				else{
					return FILE_DOWNLOAD_SUCCESS;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return FILE_DOWNLOAD_SUCCESS;
	}
	
	/**
	 * ��Զ�̷��������ض������ļ�
	 * @param urlstr �ļ���Զ�̷�������URL�����磺http://219.245.92.63:8090/mp3/a.mp3
	 * @return InputStream
	 */
	public InputStream downloadBinaryFile(String urlstr){
		urlStr = urlstr;
		InputStream inputStream = getInputStreamFromURL(urlstr);
		return inputStream;
	}
	/**
	 * ��Զ�̷��������ض������ļ�
	 * @param protocol String Э��
	 * @param host String ��������IP��ַ
	 * @param port �˿ں�
	 * @param dir Ŀ¼
	 * @param fileName �ļ���
	 * @return InputStream �����سɹ�����InputStream���󣬷��򷵻�null
	 */
	public InputStream downloadBinaryFile(String protocol, String host, String port, String dir, String fileName){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol).append(":").append(File.separator).append(File.separator).append(host).append(":").append(port).append(File.separator).append(dir).append(File.separator).append(fileName); 
		return downloadBinaryFile(sb.toString());
	}
	
	/**
	 * ��url�������������
	 * @param urlStr Զ�̷�������洢���ļ���URL
	 * @return InputStream
	 */
	private InputStream getInputStreamFromURL(String urlstr) {
		InputStream input = null;
		this.urlStr = urlstr;
		try {
			url = new URL(urlStr);//����һ��URL����
			//URL���������
			HttpURLConnection httpUrlConn = (HttpURLConnection)url.openConnection();
			//���InputStream����
			input = httpUrlConn.getInputStream();
		}
		catch(SocketException e){
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
        catch(UnknownServiceException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
		}
		return input;
	}
	
	/**
     * �ж��Ƿ��Ѿ����ӵ�������
     * @param uristr String  ��������URI ���磺tocant��������"http://219.245.92.63:8090/"
     * @return true �Ѿ������Ϸ�����
     *         false δ���ӵ�������
     */
    public boolean isServerConnected(String uristr){
    	long timeOut = Constant.SERVER_CONNECTION_PARAMS.TIME_OUT;
    	URI uri = null;//��������URI
		try{
			uri = new URI(uristr);
		} 
		catch (URISyntaxException e){
			e.printStackTrace();
		}
		if(null==uri){
			return false;
		}
		else{
			final HttpGet httpGet = new HttpGet(uri);
			HttpParams params = new BasicHttpParams();
			//�������ӳ�ʱֵΪtimeOut
			HttpConnectionParams.setConnectionTimeout(params, (int)timeOut);
			//����socket���ӳ�ʱֵΪtimeOut
			HttpConnectionParams.setSoTimeout(params, (int)timeOut);
			//����socket�����С
			int socketBufferSize = Constant.SERVER_CONNECTION_PARAMS.SOCKET_BUFFER_SIZE;
			HttpConnectionParams.setSocketBufferSize(params, socketBufferSize);
			final HttpClient httpClient = new DefaultHttpClient(params);
			//��ȡ����ǰ��ʱ��
			long beforConnectionTime = System.currentTimeMillis();
			long currentTime = 0L;//���浱ǰʱ��
			while(true){
				currentTime = System.currentTimeMillis();
				if((currentTime - beforConnectionTime) > (timeOut * 2/3)){
					//�������ʱ����ڳ�ʱֵ��2/3���ʾ�Ѿ���ʱ���ӣ��򷵻�false
					return false;
				}
				try{
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(200 == httpResponse.getStatusLine().getStatusCode()){
						//���http���ӷ���200�ڱ�ʾ���ӳɹ�������true
						return true;
					}
				}
				catch (ClientProtocolException e){
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
    }
}
