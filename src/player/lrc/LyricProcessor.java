package player.lrc;

import java.util.LinkedList;
import java.util.Queue;

/**
 * ��ʴ����࣬��Ҫ�����ǽ���lrc����ļ�����������ļ��е�ʱ�����Ϣ�Լ���Ӧ
 * �ĸ����Ϣ��
 * @author Administrator
 */
public class LyricProcessor 
{
	//���������ĸ����Ϣ
	private LyricInfo lyricResult = new LyricInfo();
	
	public LyricProcessor()
	{
	}

	public LyricInfo getLyricResult()
	{
		return lyricResult;
	}
	
	/**
	 * ����lrc����ļ�
	 * @param lrcStr ����ļ����ı�����
	 */
	public LyricInfo parseLrc(String lrcStr)
	{
		//����ʱ������
		Queue<Long> timeQueue = new LinkedList<Long>();
		//����ʱ����Ӧ�ĸ�ʶ���
		Queue<String> lrcQueue = new LinkedList<String>();
		
		//�洢��ʵ�ʱ����ַ����͸�������ַ�������������У����������ݣ�˫����ʱ���
		String[] lrcArray = new String[]{};
		lrcArray = lrcStr.split("[\\[\\]]");
		
		for(int i = 0 ; i < lrcArray.length ; i++)
		{
			if(0 != i%2)//����������洢��ʱʱ����ַ���
			{//��ȡʱ�������
				Long time = timeToLong(lrcArray[i].trim());
				timeQueue.add(time);
			}
			else if(0 == i%2)//�����ż���洢��ʱ��������ַ���
			{//��������������
				if(null != lrcArray[i] && !"".equals(lrcArray[i]))
				{//�����ǰ���������ݲ��ǿ�
					String lrcLine = lrcArray[i];
					lrcQueue.add(lrcLine.trim());
				}
			}
		}
		lyricResult.setTimeStampQueue(timeQueue);
		lyricResult.setLyricQueue(lrcQueue);
		
		return lyricResult;
	}
	
	/**
	 * �Ѹ��ʱ����ַ���ת����Long�ͺ��롣���磺00:00.0ת��Ϊ0
	 * @param timeStr ����ַ��� ���磺21:02.2
	 * @return Long ����
	 */
	private Long timeToLong(String timeStr)
	{
		String s[] = timeStr.split(":");
		Long min = Long.parseLong(s[0]);
		String s2[] = s[1].split("\\.");
		Long sec = Long.parseLong(s2[0]);
		Long mill = Long.parseLong(s2[1]);
		Long time = min * 60 * 1000 + sec * 1000 + mill * 10;
		return time;
	}
}
