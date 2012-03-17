package player.mp3.service;

import java.util.Queue;

import file.utils.FileUtils;
import player.constant.Constant;
import player.lrc.LyricInfo;
import player.lrc.LyricProcessor;
import player.model.Mp3Info;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

public class PlayerService extends Service{

	private int message = 0;//mp3���ŵ���Ϣ���п�ʼ����ͣ��ֹͣ��Ϣ
	private Mp3Info mp3Info = null;//�洢�û������MP3�ļ�

	private boolean isPlaying = false;//�ж��Ƿ����ѽ����_����״̬
	private boolean isPause = false;//�ж��Ƿ�����ͣ״̬
	private boolean isReleased = false;//�ж��Ƿ����ͷ�״̬
	
	private MediaPlayer mediaPlayer = null;
	
	private String lrcString = null;//�洢����MP3�ļ���lrc����ļ�����
	private LyricInfo lrcInfo = new LyricInfo();//������ĸ�ʶ���
	
	private Looper looper = null;
//	private Handler handler = new Handler();
	private LrcHandler handler = null;
	
	private UpdateTimeCallback updateTimeCallback = null;
	
	private long begin = 0L;//��¼MP3��ʼ����ʱ�̣���λ�Ǻ���
	private long pauseTime = 0L;//��¼��ͣ���ŵ�ʱ�䣬��λ�Ǻ���
	private String lrcLine = null;//��¼��ǰҪ���ŵ�һ�и��
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		HandlerThread handlerThread = new HandlerThread("handler_thread");
		handlerThread.start();//��һ�����߳�
		looper = handlerThread.getLooper();
		handler = new LrcHandler(looper); 
	}

	/**
	 * ���û�����һ��Service�͵����������
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		getExtraInfoFromIntent(intent);//��Intent��ȡ���͹�������Ϣ
		messageHandler(message);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * MP3������Ϣ��������������Ϣ�Ĳ�ͬ���в�ͬ�Ĵ���
	 * @param msg
	 */
	private void messageHandler(int msg){
		if(Constant.Message.MP3_PLAY_START == msg){
			//��ʼ����MP3
			play();
		}
		else if(Constant.Message.MP3_PLAY_PAUSE == msg){
			//��ͣ����Mp3
			pause();
		}
		else if(Constant.Message.MP3_PLAY_STOP == msg){
			//ֹͣ����Mp3
			stop();
		}
	}
	
	/**
	 * ���MP3���ŵ����������MP3_PLAY_START��MP3_PLAY_PAUSE��MP3_PLAY_STOP
	 * @param intent
	 * @return int
	 */
	private int getMp3PlayCommand(Intent intent){
		return intent.getIntExtra(Constant.Message.PLAY_MP3_MESSAGE_KEY, -1);
	}
	
	/**
	 * ���Mp3Info ���������û�ѡ����
	 * @param intent
	 * @return
	 */
	private Mp3Info getMp3FromIntent(Intent intent){
		return (Mp3Info)intent.getSerializableExtra(Constant.MP3.MP3_INFO_KEY);
	}
	
	/**
	 * ��Intent��ȡ���͹�������Ϣ
	 * @param intent
	 */
	private void getExtraInfoFromIntent(Intent intent){
		mp3Info = getMp3FromIntent(intent);
		message = getMp3PlayCommand(intent);
		lrcString = intent.getStringExtra("lrcString");
	}
	
	/**
	 * ����MP3�ļ�
	 */
	private void play(){
		Uri uri = getMp3UriFromSDCard(mp3Info);//���MP3Uri
		mediaPlayer = MediaPlayer.create(PlayerService.this, uri);
		if(mediaPlayer!=null){
			
			lrcInfo = parseLrc(lrcString);//������ʣ���ȡlrc�ַ�����������IrcInfo����
			//����һ������ʱ����̶߳���
			updateTimeCallback = new UpdateTimeCallback(lrcInfo);
			
			mediaPlayer.setLooping(false);//ѭ��������Ϊfalse���Ͳ�ѭ��������
			mediaPlayer.start();//��ʼ����
			begin = System.currentTimeMillis();//��ʼ���ź��ȡ��ʼʱ�̣���λ�Ǻ���
			//ִ�и��¸���߳�
			handler.post(updateTimeCallback);
			//���Ĳ���״̬
			isPlaying = mediaPlayer.isPlaying();
			isPause = false;
			isReleased = false;
		}
	}
	
	/**
	 * ��ͣ����MP3�ļ�
	 */
	private void pause(){
		if(null != mediaPlayer){
			if(!isPause && !isReleased){
				mediaPlayer.pause();//��ͣ����
				handler.removeCallbacks(updateTimeCallback);//�Ѹ����߳��Ƴ��̶߳��У������и��¸��
				pauseTime = System.currentTimeMillis();//��ȡ��ͣ���ŵ�ǰʱ��
				isPause = true;//�ı䲥��״̬Ϊ��ͣ
			}
			else if(isPause && !isReleased){
				mediaPlayer.start();//���¿�ʼ��������
				handler.post(updateTimeCallback);//�Ѹ����߳����������̶߳��У����и��¸��
				begin = begin + System.currentTimeMillis() - pauseTime;
				isPause = false;//���Ĳ���״̬
			}
			isPlaying = true;
			isReleased = false;
		}
	}
	
	/**
	 * ֹͣ����Mp3�ļ�
	 */
	private void stop(){
		if(null != mediaPlayer){
			if(isPlaying && !isReleased){
				mediaPlayer.stop();//ֹͣ����
				mediaPlayer.release();//�ͷŲ�������Դ
				handler.removeCallbacks(updateTimeCallback);//�Ѹ����߳��Ƴ��̶߳��У������и��¸��
				isPlaying = false;
				isPause = false;
				isReleased = true;
			}
		}
	}
	
	/**
	 * ���¸�ʻص������ڲ���
	 * @author Administrator
	 */
	private class UpdateTimeCallback implements Runnable
	{
		Queue<String> lrcQueue = null;//��ʶ���
		Queue<Long> timeQueue = null;//ʱ������
		Long currentTime = 0L;//��ǰ���Ÿ������Ӧ�Ŀ�ʼʱ��
		Long nextTime = 0L;//��ǰ���Ÿ�ʶ�Ӧ�Ľ���ʱ�̣�Ҳ������һ���ʵĿ�ʼʱ��
		public UpdateTimeCallback(LyricInfo lrcInfo)
		{
			lrcQueue = lrcInfo.getLyricQueue();
			timeQueue = lrcInfo.getTimeStampQueue();
		}
		public void run()
		{
			//����ӿ�ʼ���ŵ����ڹ������˶���ʱ�䣬�Ժ���Ϊ��λ
			long offset = System.currentTimeMillis() - begin;
			if(!timeQueue.isEmpty())
			{
				currentTime = timeQueue.peek();//ȡ����ǰ��ʶ�Ӧ�Ŀ�ʼʱ��
				Long temp = timeQueue.poll();//��ʱ�����������׵�ʱ��
				nextTime = timeQueue.peek();//ȡ����ǰ��ʶ�Ӧ�Ľ���ʱ�̣�����һ���ʵĿ�ʼʱ��
				timeQueue.add(temp);//���°Ѷ���ʱ�̷Żض�����
				if(null != currentTime && 0 == currentTime)
				{
					lrcLine = lrcQueue.poll();
					timeQueue.poll();
				}
				if(null != nextTime && offset >= nextTime)//�����ǰ��ʱ��ƫ�������ڵ��ڵ�ǰ��ʵĽ���ʱ�̣��͸�����һ����
				{
					Intent intent = new Intent();//����һ��Intent����
					intent.setAction(Constant.BROADCAST_ACTION.LRC_UPDATE_ACTION);//����Intent�Ķ���Ϊ���¸��Action
					intent.putExtra("update_lrc", lrcLine);//��Ҫ���µĸ�ʷ���Intent������׼������PlayerActivity���и���
					sendBroadcast(intent);//���͹㲥�����и���
					lrcLine = lrcQueue.poll();//ȡ�����׸�ʣ�׼�����¸��
					timeQueue.poll();
				}
			}
			handler.post(updateTimeCallback);//��������
		}
	}
	
	/**
	 * ��SD���ϻ��ָ����MP3URI·��
	 * @param mp3Info
	 * @return Uri
	 */
	private Uri getMp3UriFromSDCard(Mp3Info mp3Info){
		FileUtils fileUtils = new FileUtils();
		return fileUtils.getFileUriFromSDCard(Constant.MP3.MP3_DIR_PATH, mp3Info.getMp3Name());
	}
	
	/**
	 * ��������ļ�����ý�����ĸ����Ϣ����
	 * @param lrcStr ����ļ�������
	 * @return LyricInfo �����Ϣ����
	 */
	private LyricInfo parseLrc(String lrcStr)
	{
		LyricProcessor lrcProcessor = new LyricProcessor();
		return lrcProcessor.parseLrc(lrcStr);//�������;
	}
}



class LrcHandler extends Handler
{
	public LrcHandler()
	{
	}
	public LrcHandler(Looper looper)
	{
		super(looper);
	}
}
