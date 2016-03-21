package edu.tongji.proteingoggle.jobcontrol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class JobQueue {

	private static List<Job> queue = null;
	private static Object mutex = new Object();
//	private static int vipIndex = 0;
	private static Job lastVIPJob = null;
	private JobQueue(){
		
	}
	public static List<Job> getQueue(){
        if(queue==null){
            synchronized (mutex){
                if(queue==null) queue= Collections.synchronizedList(new ArrayList<Job>());
            }
        }
        return queue;
    }
	
	public static void printQueue()
	{
		if(queue == null)
		{
			getQueue();
		}
			System.out.println("----------------job queue--------------");

 		for(Job j:queue)
 		{
 			System.out.println(j.getSubmitUserEmail()+"   "+j.getFile().toString());
 		}
	}
	
	public static void appendNormalJob(Job j)
	{
		if(queue == null)
		{
			getQueue();
		}
		queue.add(j);
	}
	
	private static int getIndex()
	{
		if(lastVIPJob==null)
		{
			return 0;
		}
		else
		{
			return queue.indexOf(lastVIPJob)+1;
		}
	}
	
	public static void appendVIPJob(Job j)
	{
		if(queue == null)
		{
			getQueue();
		}
		int index = getIndex();
		queue.add(index,j);
		lastVIPJob = j;
	}
}
