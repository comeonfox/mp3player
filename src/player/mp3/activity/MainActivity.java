package player.mp3.activity;

import player.mp3.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * MP3��������������Activity��
 * @author Administrator
 */
public class MainActivity extends TabActivity
{
	private TabHost tabHost = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//�õ�TabHost�������TabActivity�Ĳ�������������������
		tabHost = getTabHost();
		Resources resource = getResources();//�����Դ����
		//�����ʾ���ط�������MP3�б����Activity
		addLocalListActivity(resource);
		//�����ʾԶ�̷�������MP3�б����Activity
		addRemoteServerAcitvity(resource);
	}
	
	/**
	 * ��ת������Զ�̷����������б�Activity��
	 */
	private void addRemoteServerAcitvity(Resources resource)
	{
		//����һ����ת��Զ�̷������˵�Intent����
		Intent remoteIntent = new Intent();
		remoteIntent.setClass(this, RemoteServerMp3ListActivity.class);

		addTabSpecOnTabHost(remoteIntent, "����", resource.getDrawable(R.drawable.heyzap));
	}
	
	/**
	 * �����ʾ����MP3 �б����Activity��
	 * @param resource
	 */
	private void addLocalListActivity(Resources resource)
	{
		//����һ����ת�����ص�Intent����
		Intent localIntent = new Intent(this, LocalMp3ListActivity.class);

		addTabSpecOnTabHost(localIntent, "�����б�", resource.getDrawable(R.drawable.bluetooth));
	}
	
	/**
	 * ���TabSpec
	 * @param targetIntent
	 * @param tabSpecName
	 * @param drawable
	 */
	private void addTabSpecOnTabHost(Intent targetIntent, String tabSpecName,  Drawable drawable)
	{
		//����һ��TabSpec������������Ǵ���һ��ҳ
		TabHost.TabSpec targetTabSpec = tabHost.newTabSpec(tabSpecName);
		//���ø�ҳ��ָʾ��
		targetTabSpec.setIndicator(tabSpecName, drawable);
		//���ø�ҳ������Intent
		targetTabSpec.setContent(targetIntent);
		//�����úõ�TabSpec������ӵ�TabHost��
		tabHost.addTab(targetTabSpec);
	}
}
