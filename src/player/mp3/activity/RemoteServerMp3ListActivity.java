package player.mp3.activity;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import player.constant.Constant;
import player.download.HttpDownloader;
import player.model.Mp3Info;
import player.mp3.R;
import player.mp3.service.DownloadService;
import player.xml.Mp3ListContentHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Զ�����ط������Ľ���Acitivy��,��ʾ�����б�
 * @author Administrator
 */
public class RemoteServerMp3ListActivity extends Activity 
{
	
	private static final int GROUP_ID = 0;
	private static final int UPDATE = 1;
	private static final int ABOUT = 2;
	private List<Mp3Info> mp3lists = null;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_server_mp3_list);
    }
    
    protected void onResume() {
    	super.onResume();
    	HttpDownloader httpDownloader = new HttpDownloader();
    	if(!httpDownloader.isServerConnected(Constant.URL.SERVER_ROOT_URL_STR)){
    		//���û�����ӵ�����������û���ʾδ���ӷ�������Ϣ
    		showConnectionErrorMessageToClient();
    		mp3lists = new ArrayList<Mp3Info>();
    	}
    	else{//����������·�����mp3�б�
    		//���·������б�
    		getServerUpdatedMp3List();
    	}
    	//������ʾMP3�б�
		updateMp3ListShow();
    }
    /**
     * ��ͻ���ʾ���Ӵ�����Ϣ
     */
    private void showConnectionErrorMessageToClient(){
    	CharSequence text = "������δ���ӣ������ع�����ʱ����ʵ�֣���ʹ�ñ�Ĺ���!";
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.show();
		Button btn = new Button(RemoteServerMp3ListActivity.this);
		btn.setText("�ر�");
		final Dialog dlg = new Dialog(RemoteServerMp3ListActivity.this);
		TextView txt = new TextView(dlg.getContext());
		txt.setText(text);
		
		dlg.setContentView(txt);
		dlg.show();
		btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				dlg.dismiss();
			}
		});
    }
    
    /**
     * ���MENU��ť֮�󣬻���ø÷��������ǿ���������������м����Լ��İ�ť�ؼ�
     */
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	int groupId = GROUP_ID;

    	int itemId = UPDATE;
    	int order = 1;
    	int title = R.string.mp3list_update;
    	menu.add(groupId, itemId, order, title);
    	
    	itemId = ABOUT;
    	order = 2;
    	title = R.string.mp3list_about;
    	menu.add(groupId, itemId, order, title);
    	
		return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * ���û����Menu��ĳһ��͵��ô˷���
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if(item.getItemId() == UPDATE)
    	{   //�û�����˸����б�ť
    		//��ȡ���·�������MP3
    		getServerUpdatedMp3List();
    		//������ʾMP3�б�
    		updateMp3ListShow();
    	}
    	else if(item.getItemId() == ABOUT)
    	{//�û�������ڰ�ť
    		
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    /**
     * ��ȡ���·�������MP3
     */
    private void getServerUpdatedMp3List()
    {
    	//����XML�ļ�
		String xml = downloadXML();
		//�������صļ�¼MP3��Ϣ��XML�ļ���mp3lists�б��У��б��д洢���ǽ�������Mp3Info����
	    mp3lists = parse(xml);
    }
    /**
     * ������ʾMP3�б�
     */
    private void updateMp3ListShow(){
    	//���listView
    	ListView listView = (ListView)findViewById(R.id.listview01);
    	if(null == mp3lists){
    		return;
    	}
    	//����һ��SimpleAdapter����
    	SimpleAdapter simpleAdapter = createSimpleAdapter(mp3lists);
    	//��simpleAdapter������ӵ�listView��
    	listView.setAdapter(simpleAdapter);
    	//����listView��һ��ѡ��
    	clickListItem(listView);
    }
    
    /**
     * �����б��һ��ѡ������¼�,̎�����dMP3��lrc�¼�
     * @param listView
     */
    private void clickListItem(ListView listView)
    {
    	listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//�����û�����б��е�λ�ã�ȡ����positionλ��ȷ����mp3Info����
				Mp3Info mp3Info = mp3lists.get(position);
				//����һ��intent����
				Intent intent = new Intent();
				//��mp3INFo��������intent��
				intent.putExtra(Constant.MP3.MP3_INFO_KEY, mp3Info);
				intent.setClass(RemoteServerMp3ListActivity.this, DownloadService.class);
				RemoteServerMp3ListActivity.this.startService(intent);//����Service
			}
		});
    }
    
    /**
     * ����һ��SimplerAdapter����
     * @param mp3lists
     * @return simpleAdapter
     */
    private SimpleAdapter createSimpleAdapter(List<Mp3Info> mp3lists)
    {
    	// ����һ��List���󣬲�����SimpleAdapter�ı�׼����mp3Infos���е�������ӵ�List����ȥ
    	List<HashMap<String, String>> list = insertToList(mp3lists);
    	
		// ����һ��SimpleAdapter����
		int resource = R.layout.mp3info_item;
		String[] from = new String[] { Constant.MP3.MP3_NAME, Constant.MP3.MP3_SIZE};
		int[] to = new int[] { R.id.mp3item_mp3_name, R.id.mp3item_mp3_size };
		SimpleAdapter simpleAdapter = new SimpleAdapter(RemoteServerMp3ListActivity.this, list, resource, from, to);
		return simpleAdapter;
    }
    
    /**
     * ����һ��List���󣬲�����SimpleAdapter�ı�׼����mp3Infos���е�������ӵ�List����ȥ
     * @param mp3lists
     * @return
     */
    private List<HashMap<String,String>> insertToList(List<Mp3Info> mp3lists)
    {
    	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (Iterator<Mp3Info> iterator = mp3lists.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Constant.MP3.MP3_NAME, mp3Info.getMp3Name());
			map.put(Constant.MP3.MP3_SIZE, mp3Info.getMp3Size());
			list.add(map);
		}
		return list;
    }
   
    /**
     * ����XML�����ļ�
     * @return
     */
    private String downloadXML()
    {
    	HttpDownloader httpDownloader = new HttpDownloader();
    	return httpDownloader.downloadTxtFile(Constant.URL.RESOURCE_URL_STR);
    }
    
    /**
     * ����MP3��Ϣ�ļ�������һ��xml�ļ�
     * @param xmlStr ������xml�ļ��ă��ݣ���һ���ַ���
     * @return ���ؽ������Ķ����б�
     */
    private List<Mp3Info> parse(String xmlStr)
    {
    	//���һ��SAXParserFactory����
    	SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<Mp3Info> mp3List = new ArrayList<Mp3Info>();
		try 
		{
			//��SAXParserFactory������XMLReader����
			XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(mp3List);
			xmlReader.setContentHandler(mp3ListContentHandler);
			xmlReader.parse(new InputSource(new StringReader(xmlStr)));
		} 
		catch (SAXException e)
		{
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			saxParserFactory = null;
		}
    	return mp3List;
    }
}

