package player.mp3.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import file.utils.FileUtils;
import player.constant.Constant;
import player.model.Mp3Info;
import player.mp3.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class LocalMp3ListActivity extends ListActivity
{
	private List<Mp3Info> localMp3List = null;//�洢����MP3�ļ��б�
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
		
	}
	
	protected void onResume() {
		super.onResume();
		updateLocalMp3List();
	}
	/**
	 * ��listView�������б仯ʱ�ŵ��ô˷���
	 */
	public void onContentChanged() {
		super.onContentChanged();
		//���±��������ص�MP3�ļ��б�
		updateLocalMp3List();
	}
	
	/**
	 * ���±��������ص�MP3�ļ��б�
	 */
	private void updateLocalMp3List(){
		localMp3List = new ArrayList<Mp3Info>();//����MP3Info�б����
		FileUtils fileUtils = new FileUtils();
		//��SD���ϻ�ú�׺ΪMP3���ļ����洢��һ���б���
		List<File> fileList = fileUtils.getFilesList(Constant.MP3.MP3_DIR_PATH, ".mp3");
		//�����ñ��ص�MP3�б�Ϊnull�򷵻�
		if(null == fileList){
			return;
		}
		//�����ݴ洢��localMp3List�б���
		for(Iterator<File> iterator = fileList.iterator() ; iterator.hasNext(); ){
			File file = (File)iterator.next();
			Mp3Info mp3Info = new Mp3Info();
			mp3Info.setMp3Name(file.getName());
			mp3Info.setMp3Size(Long.toString(file.length()));
			localMp3List.add(mp3Info);
		}
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		for (Iterator<Mp3Info> iterator = localMp3List.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put(Constant.MP3.MP3_NAME, mp3Info.getMp3Name());
			hashMap.put(Constant.MP3.MP3_SIZE, mp3Info.getMp3Size());
			list.add(hashMap);
		}
		SimpleAdapter simplerAdapter = new SimpleAdapter(this, list , R.layout.mp3info_item, new String[]{Constant.MP3.MP3_NAME,Constant.MP3.MP3_SIZE}, new int[]{R.id.mp3item_mp3_name,R.id.mp3item_mp3_size});
		setListAdapter(simplerAdapter);
	}
	
	/**
	 * ���û����һ��ѡ���ǵ��ô˺���
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Mp3Info mp3Info = localMp3List.get(position);
		//���SD���ϵĺ��û�����mp3�ļ�ͬ����lrc�ļ�
		String lrcString = getLrcFileFromSDCard(mp3Info.getMp3Name());
		//��ת��PlayerActivity
		JumpToPlayerActivity(l,v,position,id, lrcString );
	}
	/**
	 * ��SD���ϻ����Mp3�ļ�ͬ����lrc�ļ�
	 * @param fileName MP3�ļ��� ���磺"a.mp3"
	 * @return String ��MP3ͬ����lrc�ļ��������ַ���
	 */
	private String getLrcFileFromSDCard(String mp3fileName){
		int index = mp3fileName.indexOf(".");
		mp3fileName = mp3fileName.substring(0, index);
		//���SD���ϵ�����lrc�ļ��б�
		FileUtils fileUtils = new FileUtils();
		List<File> fileList = fileUtils.getFilesList(Constant.MP3.MP3_DIR_PATH, ".lrc");
		//ѡ����MP3�ļ�ͬ����lrc�ļ�����
		String lrcFileContent = new String();//�洢lrc�ļ�������
		for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			String lrcFileName = file.getName();
			int i = lrcFileName.indexOf(".");
			lrcFileName = lrcFileName.substring(0, i);
			if(lrcFileName.equals(mp3fileName)){
				//���SD���ϵ�ָ����lrc�ļ�����
				lrcFileContent = fileUtils.readTextFile(Constant.MP3.MP3_DIR_PATH, lrcFileName+".lrc");
			}
		}
		return lrcFileContent;
	}
	/**
	 * ��ת��PlayerActivity
	 * @param l
	 * @param v
	 * @param position
	 * @param id
	 * @param lrcStr lrc�ļ��������ַ���
	 */
	private void JumpToPlayerActivity(ListView l, View v, int position, long id, String lrcStr){
		//������ѡ�ĸ���,��ת��PlayerActivity
		if(null != localMp3List)
		{
			Mp3Info mp3Info = localMp3List.get(position);//��ȡ�û���ѡ��Mp3����
			Intent intent = new Intent();
			intent.setClass(this, PlayerActivity.class);
			intent.putExtra(Constant.MP3.MP3_INFO_KEY, mp3Info);//����û������MP3�ļ���Ϣ
			intent.putExtra("lrcStr", lrcStr);//�����MP3ͬ����lrc�ַ�������
			startActivity(intent);
		}
	}
}
