
package com.automature.zug.engine;


public class HiPerfTimer
{

	private long startTime, stopTime;
	//private long freq;

	/**
	 * Non-parameterized constructor.
	 */
	public HiPerfTimer()
	{
		startTime = 0;
		stopTime = 0;
		Start();
	}

	/**
	 *  Start the timer
	 */
	public void Start()
	{
		// lets do the waiting threads there work
		try 
		{
			Thread.sleep(0);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		startTime= System.currentTimeMillis();
	}

	/**
	 *  Stop the timer
	 * @return time duration in int
	 */
	public int Stop()
	{
		// lets do the waiting threads there work
		try 
		{
			Thread.sleep(0);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		stopTime=System.currentTimeMillis();

		return Duration();
	}

	/**
	 * Returns the duration of the timer (in milliseconds)
	 */
	public int Duration()
	{
		return (int)(stopTime - startTime) ;
	}
}
