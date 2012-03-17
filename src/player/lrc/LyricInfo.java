package player.lrc;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ��ʽ��������Ϣ�࣬��Ҫ�����Ǵ洢������ʺ�ĸ����Ϣ
 * ��������ʵ�ʱ��㣬ÿ��ʱ����Ӧ�ĸ�����ݣ���������ʱ��
 * @author Administrator
 */
public class LyricInfo implements Serializable
{
	private static final long serialVersionUID = -860365065197720702L;

	//���ʱ���Ķ���
	private Queue<Long> timeStampQueue = new LinkedList<Long>();
	//���ʱ����Ӧ�ĸ��
	private Queue<String> lyricQueue = new LinkedList<String>();
	

	public LyricInfo(Queue<Long> timeStampQueue, Queue<String> lyricQueue)
	{
		this.timeStampQueue = timeStampQueue;
		this.lyricQueue = lyricQueue;
	}
	
	public LyricInfo()
	{
	}

	public Queue<Long> getTimeStampQueue()
	{
		return timeStampQueue;
	}

	public void setTimeStampQueue(Queue<Long> timeStampQueue)
	{
		this.timeStampQueue = timeStampQueue;
	}

	public Queue<String> getLyricQueue()
	{
		return lyricQueue;
	}

	public void setLyricQueue(Queue<String> lyricQueue)
	{
		this.lyricQueue = lyricQueue;
	}
}
