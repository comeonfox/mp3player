package player.mp3.activity;

import player.constant.Constant;
import player.model.Mp3Info;
import player.mp3.R;
import player.mp3.service.PlayerService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlayerActivity extends Activity{
	
	private ImageButton imgBtn_start = null;
	private ImageButton imgBtn_pause = null;
	private ImageButton imgBtn_stop = null;
	
	private TextView lrcAreaTxtView = null;
	
	private Intent intent = null;//���͸�PlayerService��Intent����
	
	private String lrcString = null;//�洢����MP3�ļ���lrc����ļ�����

	private BroadcastReceiver broadcastReceiver = new LrcBroadcastReceiver();
	
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(broadcastReceiver);//ȡ��ע��broadcastReceiver
	}

	protected void onResume()
	{
		super.onResume();
		//ע��broadcastReceiver
		registerReceiver(broadcastReceiver, getIntentFilter());
	}
	/**
	 * ���IntentFilter����
	 * @return
	 */
	private IntentFilter getIntentFilter()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.BROADCAST_ACTION.LRC_UPDATE_ACTION);
		return filter;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.player);
		getTextViewResource();//�����ʾ�����Դ
		getImageButtonResource();//��ȡͼƬ��ť��Դ
		bindListenerToButton();//�󶨼���������ť��
		//���lrc�ļ����ַ�������
		lrcString = getIntent().getStringExtra("lrcStr");
		//������PlayerActivity��PlayerService��intent
		intent = new Intent(PlayerActivity.this, PlayerService.class);
	}
	/**
	 * ��ȡ��ʾ��ʵ�TextView��Դ
	 */
	private void getTextViewResource()
	{
		lrcAreaTxtView = (TextView)findViewById(R.id.PlayerActivity_lrc_showarea_textview);
	}
	/**
	 * ��ȡͼƬ��ť��Դ
	 */
	private void getImageButtonResource(){
		imgBtn_start = (ImageButton)findViewById(R.id.player_start);
		imgBtn_pause = (ImageButton)findViewById(R.id.player_pause);
		imgBtn_stop = (ImageButton)findViewById(R.id.player_stop);
	}
	
	/**
	 * �󶨼���������ť��
	 */
	private void bindListenerToButton(){
		imgBtn_start.setOnClickListener(new StartButtonListener());
		imgBtn_pause.setOnClickListener(new PauseButtonListener());
		imgBtn_stop.setOnClickListener(new StopButtonListener());
	}
	
	/**
	 * ��ʼ���Ű�ť�ڲ��࣬���û������ʼ����ʱ����������onClick����
	 * @author Administrator
	 *
	 */
	private class StartButtonListener implements OnClickListener{

		public void onClick(View view) {
			Mp3Info mp3Info = (Mp3Info)PlayerActivity.this.getIntent().getSerializableExtra(Constant.MP3.MP3_INFO_KEY);
			intent.putExtra(Constant.MP3.MP3_INFO_KEY, mp3Info);//��MP3Info������뵽Intent�д���PlayerService
			intent.putExtra("lrcString", lrcString);//��������ַ�����ӵ�Intent�д���PlayerService
			sendMp3PlayCommand(Constant.Message.MP3_PLAY_START);
			startService(intent);//��Service
		}
	}
	
	/**
	 * ��ͣ��ť�ڲ��࣬���û������ͣ��ťʱ����������onClick����
	 * @author Administrator
	 */
	private class PauseButtonListener implements OnClickListener{
		public void onClick(View view) {
			//������ͣ����Mp3����
			sendMp3PlayCommand(Constant.Message.MP3_PLAY_PAUSE);
			startService(intent);//��Service
		}
	}
	
	/**
	 * ֹͣ��ť�ڲ��࣬���û����ֹͣʱ����������onClick����
	 * @author Administrator
	 */
	private class StopButtonListener implements OnClickListener{

		public void onClick(View v) {
			//����ֹͣ����Mp3����
			sendMp3PlayCommand(Constant.Message.MP3_PLAY_STOP);
			startService(intent);//��Service
		}
	}
	
	/**
	 * ��ʹ㲥�������ڲ���,������㲥���������յ���Ӧ��ϵͳ��Ϣ��͵��ô˷��������¸��
	 * @author Administrator
	 */
	private class LrcBroadcastReceiver extends BroadcastReceiver
	{
		public void onReceive(Context context, Intent intent)
		{
			//��ô����µĸ��
			String updateLrc = intent.getStringExtra("update_lrc");
			lrcAreaTxtView.setText(updateLrc);//���¸��
		//*****************************************88���Դ���*****************
			System.out.println(updateLrc);
		//****************************************************************8***
		}
	}
	
	/**
	 * ���Ϳ���MP3���ŵ������PlayerService
	 * @param msg
	 */
	private void sendMp3PlayCommand(int msg){
		//���Ͳ��ŵ���Ϣ��PlayerService
		intent.putExtra(Constant.Message.PLAY_MP3_MESSAGE_KEY, msg);
	}
}
