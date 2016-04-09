
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class PhilTest {
	private static int WAITING = 0, EATING = 1, THINKING = 2;
	private static final int NUM_PHILS = 100;
	private static Lock lock = new ReentrantLock();
	private static Condition phil[] = new Condition[NUM_PHILS];
	private static int states[] = new int[NUM_PHILS];
	private static int priority[] = new int[NUM_PHILS];
	public static AtomicInteger counter = new AtomicInteger(0);
	private static int[] appetites = new int[NUM_PHILS];
	public static double[] averageWait = new double[NUM_PHILS];
	public static int[] max = new int[NUM_PHILS];
	
	public static void init() {
		for (int k = 0; k < NUM_PHILS; k++) {
			phil[k] = lock.newCondition();
			states[k] = THINKING;
		}
	}

	public static void main(String a[]) {
		init();
		Philosopher p[] = new Philosopher[NUM_PHILS];
		
		long start = System.currentTimeMillis();
		for (int k = 0; k < p.length; k++) {
			p[k] = new Philosopher(lock, phil, states, priority, NUM_PHILS, k, counter, appetites, averageWait, max);
			p[k].start();
		}
		
		for (Thread t : p) {
			try
			{
				t.join();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		long stop = System.currentTimeMillis();
		
		
		
		int sum = 0;
		for (int i = 0; i < max.length; i++) {
			sum += max[i];
		}
		
		System.out.printf("Average Max Wait: %f\n", (double)sum / max.length);
		
//		System.out.println("\n\n === Averages ===");
//		for (int i = 0; i < averageWait.length; i++) {
//			System.out.printf("Philosopher: %d\tAverage Wait: %f\n",i, averageWait[i]);
//		}
		
		System.out.printf("\n\notal Time: %d\n\n", (stop - start));
	}
}