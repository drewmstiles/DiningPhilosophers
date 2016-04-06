import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class PhilTest {
	private static int WAITING = 0, EATING = 1, THINKING = 2;
	private static final int NUM_PHILS = 5;
	private static Lock lock = new ReentrantLock();
	private static Condition phil[] = new Condition[NUM_PHILS];
	private static int states[] = new int[NUM_PHILS];
	private static int priority[] = new int[NUM_PHILS];
	public static AtomicInteger counter = new AtomicInteger(0);

	public static void init() {
		for (int k = 0; k < NUM_PHILS; k++) {
			phil[k] = lock.newCondition();
			states[k] = THINKING;
		}
	}

	public static void main(String a[]) {
		init();
		Philosopher p[] = new Philosopher[NUM_PHILS];
		for (int k = 0; k < p.length; k++) {
			p[k] = new Philosopher(lock, phil, states, priority, NUM_PHILS, k, counter);
			p[k].start();
		}
	}
}