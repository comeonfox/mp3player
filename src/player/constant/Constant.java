package player.constant;

/**
 * �����ӿڣ�����ӿ�����Ҫ��һЩӦ�ó����еĳ���
 * @author Administrator
 */
public interface Constant {
	/**
	 * ��Ϣ�����ڲ���
	 * @author Administrator
	 *
	 */
	public final class Message{
		public static final int MP3_PLAY_START = 1;//��ʼ����MP3����
		public static final int MP3_PLAY_PAUSE = 2;//��ͣ����MP3����
		public static final int MP3_PLAY_STOP = 3;//ֹͣ����MP3����
		public static final String PLAY_MP3_MESSAGE_KEY = "message";//����MP3��Ϣ�����ַ���
	}
	
	/**
	 * MP3��Ϣ��
	 * @author Administrator
	 */
	public final class MP3{
		public static final String MP3_NAME = "mp3_name";//mp3���Ƴ����ַ���
		public static final String MP3_SIZE = "mp3_size";//MP3��С�����ַ���
		public static final String LRC_NAME = "lrc_name";//lrc���Ƴ����ַ���
		public static final String LRC_SIZE = "lrc_size";//lrc��С�����ַ���
		public static final String MP3_INFO_KEY = "mp3Info";//Mp3Info����ļ����ַ���
		public static final String MP3_DIR_PATH = "mp3";//�洢Mp3����Ը�Ŀ¼��Ŀ¼·��
	}
	
	/**
	 * ��¼������ԴURL����Ϣ�ַ���������
	 * @author Administrator
	 *
	 */
	public final class URL{
		//��Դ�ļ���URL�ַ�������
		public static final String RESOURCE_URL_STR = "http://219.245.92.63:8090/mp3/resources.xml";
		/**
		 * ��������URL
		 */
		public static final String SERVER_ROOT_URL_STR = "http://219.245.92.63:8090/";
		public static final String MP3_URL_STR = "http://219.245.92.63:8090/mp3/";
		public static final String PROTOCOL = "http";
		public static final String HOST = "219.245.92.63";
		public static final String PORT = "8090";
	}
	
	public final class BROADCAST_ACTION
	{
		public static final String LRC_UPDATE_ACTION = "player_lrc_update_action";
	}
	
	/**
	 * ���ӷ������Ĳ�������
	 * @author Administrator
	 */
	public final class SERVER_CONNECTION_PARAMS
	{
		/**
		 * ��ʱ����3000����
		 */
		public static final long TIME_OUT = 3*1000L;
		public static final int SOCKET_BUFFER_SIZE = 8192;
	}
}
