
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class PhilTest {
	
	public static int WAITING = 0, EATING = 1, THINKING = 2;
	public static final int NUM_PHILS = 5;
	
	public static void init() {
		
		// construct private data structures
		phil = new Condition[NUM_PHILS];
		states = new int[NUM_PHILS];
		appetites = new int[NUM_PHILS];
		max = new double[NUM_PHILS];
		
		// initialize environment threads
		for (int k = 0; k < NUM_PHILS; k++) {
			phil[k] = lock.newCondition();
			states[k] = THINKING;
		}
	}
	

	public static void main(String a[]) {
		
		init();
		
		long start = System.currentTimeMillis();
		
		execute();
		
		long end = System.currentTimeMillis();
		
		System.out.printf("\nMax Wait (Avg): %.2f turns\nRuntime: %d ms", getAverage(max), (end - start));
	}

	
	private static void execute() {
		
		Philosopher p[] = new Philosopher[NUM_PHILS];
		
		// begin
		for (int k = 0; k < p.length; k++) {
			p[k] = new Philosopher(lock, phil, states, NUM_PHILS, k, counter, appetites, max);
			p[k].start();
		}
		
		// synchronize
		for (Thread t : p) {
			try {
				t.join();
			} 
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	private static double getAverage(double[] arr) {
		double avg = 0.0;
		for (int i = 0; i < arr.length; i++) {
			avg += arr[i];
		}
		return avg / arr.length;
	}
	
	private static Lock lock = new ReentrantLock();
	private static Condition[] phil;
	private static int[] states;
	private static AtomicInteger counter = new AtomicInteger(0);
	private static int[] appetites;
	private static double[] max;
	
} // end class PhilTest