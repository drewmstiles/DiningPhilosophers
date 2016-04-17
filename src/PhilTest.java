
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class PhilTest {
	private static int WAITING = 0, EATING = 1, THINKING = 2;
	private static int NUM_PHILS = -1;
	private static Lock lock = new ReentrantLock();
	private static Condition[] phil;
	private static int[] states;
	private static int[] priority;
	public static AtomicInteger counter = new AtomicInteger(0);
	private static int[] appetites;
	public static double[] averageWait;
	public static int[] max;
	
	
	public static void init(int num) {
		
		NUM_PHILS = num;
		phil = new Condition[NUM_PHILS];
		states = new int[NUM_PHILS];
		priority = new int[NUM_PHILS];
		appetites = new int[NUM_PHILS];
		averageWait = new double[NUM_PHILS];
		 max = new int[NUM_PHILS];
		
		for (int k = 0; k < NUM_PHILS; k++) {
			phil[k] = lock.newCondition();
			states[k] = THINKING;
		}
	}
	

	public static void main(String a[]) {
		
		init(Integer.parseInt(a[0]));
		
		Philosopher p[] = new Philosopher[NUM_PHILS];
		
		long start = System.currentTimeMillis();
		for (int k = 0; k < p.length; k++) {
			p[k] = new Philosopher(lock, phil, states, priority, NUM_PHILS, k, counter, appetites, averageWait, max);
			p[k].start();
		}
		
		for (Thread t : p) {
			try {
				t.join();
			} 
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		
		long duration = (System.currentTimeMillis() - start);
		double avg = getAverageWaitTime();
		System.out.printf("%f\t%d", avg, duration );
	}

	
	private static double getAverageWaitTime() {
		double avgTotal = 0.0;
		for (int i = 0; i < phil.length; i++) {
			avgTotal += max[i];
		}
		return avgTotal / phil.length;
	}
	
	
} // end class PhilTest