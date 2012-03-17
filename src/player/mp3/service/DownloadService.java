package player.mp3.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import file.utils.FileUtils;

import player.constant.Constant;
import player.download.HttpDownloader;
import player.model.Mp3Info;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * ʵ�����ع��ܵ�Service�࣬�����Ҫ���F���dMP3��lrc�ļ�
 * @author Administrator
 */
public class DownloadService extends Service
{
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	/**
	 * ÿ���û����Mp3ListActivity�б��ϵ�һ����Ŀ�Ǿ�ִ���������
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		//��Intent�����л�ȡ�û���ȡ��mp3Info������Ϣ��
		Mp3Info mp3Info = (Mp3Info)intent.getExtras().get(Constant.MP3.MP3_INFO_KEY);
		//����һ�������̣߳�����Mp3Info������Ϊ�������ݵ��̶߳�����
		DownloadThread downloadThread = new DownloadThread(mp3Info);
		//��������MP3�߳�
		Thread mp3downloadThread = new Thread(downloadThread);
		mp3downloadThread.start();//��������MP3�߳�
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * MP3�����߳��ڲ��࣬���dMP3�ļ���lrc�ļ�
	 * @author Administrator
	 */
	private class DownloadThread implements Runnable
	{
		private Mp3Info mp3Info = null;//�洢Ҫ���ص�MP3��Ϣ
		
		public DownloadThread(Mp3Info mp3Info)
		{
			this.mp3Info = mp3Info;
		}
		
		public void run()
		{
			downloadMp3File();//����MP3�ļ�
			downloadLrcFile();//���dlrc�ļ�
		}
		
		/**
		 * ����MP3�ļ�
		 */
		private void downloadMp3File(){
			//���������ļ�����Ķ���
			HttpDownloader httpDownloader = new HttpDownloader();
			//����ָ����MP3�ļ���ָ����SD��Ŀ¼��
			//����Զ�̷������洢MP3�ļ���Ŀ¼��url�ַ���
			String urlStr = Constant.URL.SERVER_ROOT_URL_STR+Constant.MP3.MP3_DIR_PATH+File.separator+mp3Info.getMp3Name();
			//���ض������ļ������洢��ָ���ı���SD��Ŀ¼�£�����ָ�����ļ����洢
			int result = httpDownloader.downloadBinaryFileToSDCard(urlStr, Constant.MP3.MP3_DIR_PATH, mp3Info.getMp3Name());
		    String resultMessage = null;
			if(result == HttpDownloader.FILE_DOWNLOAD_SUCCESS){
				resultMessage = "���سɹ�";
			}else if(result == HttpDownloader.FILE_DOWNLOAD_EXIST){
				resultMessage = "�ļ��Ѿ����ڣ�����Ҫ�ظ�����";
			}else {
				resultMessage = "�ļ�����ʧ��";
			}
			
			//���ļ����صĽ����Ϣ��ʾ���û�
//			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE );
//			Notification notification = new Notification();
//			notification.tickerText = resultMessage;
//			notification.when = System.currentTimeMillis();
//			Context context = getApplicationContext();
//			String contentTitle = "download";
//			String contentText = resultMessage;
//			Intent notificationIntent = new Intent(DownloadService.this, Mp3ListActivity.class);
//			PendingIntent contentIntent = PendingIntent.getService(this, 0, null, PendingIntent.FLAG_ONE_SHOT);
//			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//			
//			notificationManager.notify(DOWNLOAD_ID, notification);
//			System.out.println("context------->"+context.toString());
		}
		/**
		 * ���dlrc���~�ļ�
		 */
		private void downloadLrcFile(){
			HttpDownloader httpDownloader = new HttpDownloader();
			//����Զ�̷������洢lrc�ļ���url
			String urlstr = Constant.URL.MP3_URL_STR + mp3Info.getLrcName();
			//����ָ����lrc�ļ�
			String lrcStr = httpDownloader.downloadTxtFile(urlstr);
			if(null == lrcStr){
				return;
			}
			//�������µ�lrc�ļ��浽SD����
			saveLrcFileToSDCard(lrcStr, Constant.MP3.MP3_DIR_PATH, mp3Info.getLrcName());
		}
		/**
		 * �������µ�lrc�ļ��浽SD����
		 * @param lrcStr lrc�ı��ļ�����
		 * @param dir SD���ϴ洢lrc�ļ���Ŀ¼
		 * @param fileName �洢��SD���ϵ�lrc�ļ���
		 */
		private void saveLrcFileToSDCard(String lrcStr, String dir, String fileName){
			FileUtils fileUtil = new FileUtils();//����һ��FileUtils����
			fileUtil.createPathInSDCard(dir);//��SD���ϴ����洢lrc�ļ���Ŀ¼
			fileUtil.createFileInSDCard(dir, fileName);//��SD���ϴ���lrc�ļ�
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(lrcStr.getBytes());
			InputStream input = byteArrayInputStream;
			fileUtil.writeToSDCardFromInputStream(dir, fileName, input);//������д��SD���ϵ�lrc�ļ���
		}
	}
}
