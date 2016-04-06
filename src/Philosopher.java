import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class Philosopher extends Thread {
	private static int WAITING = 0, EATING = 1, THINKING = 2, OFFSET = 0;
	private Lock lock;
	private Condition phil[];
	private int states[];
	private int priority[];
	private int NUM_PHILS;
	private int id;
	private final int TURNS = 20;
	private AtomicInteger counter;
	private int lastAte = 0;
	private long lastTime = 0;
	private ArrayList<Integer> waits;
	private ArrayList<Long> times;

	public Philosopher(Lock l, Condition p[], int st[], int priority[], int num, int ID,
			AtomicInteger counter) {
		this.priority = priority;
		lock = l;
		phil = p;
		states = st;
		NUM_PHILS = num;
		id = ID;
		this.counter = counter;
		waits = new ArrayList<Integer>();
		times = new ArrayList<Long>();
	}
	
	public int getLastAte() {
		return lastAte;
	}

	public void run() {
		for (int k = 0; k < TURNS; k++) {
			try {
				sleep(100);
			} catch (Exception ex) { /* lazy */
			}
			takeSticks(id);
			try {
				sleep(20);
			} catch (Exception ex) {
			}
			putSticks(id);
		}
		System.out.println(id + " wait times: " + waits + " size: "
				+ waits.size());
	}

	public void takeSticks(int id) {
//		while (true) {
			lock.lock();
			try {
				if (states[leftof(id)] != EATING
						&& states[rightof(id)] != EATING) {
					states[id] = EATING;
					System.out.println(id + " is eating");
					waits.add(counter.get() - priority[id]);
					priority[id] = counter.incrementAndGet();
//					times.add(System.nanoTime() - lastTime);
//					lastTime = System.nanoTime();

//					break;
				} else {
					states[id] = WAITING;
					phil[id].await();
					if(states[id] == EATING) {
						waits.add(counter.get() - priority[id]);
						priority[id] = counter.incrementAndGet();
					}
				}
			} catch (InterruptedException e) {
				System.out.println(id + "interruped");
				System.exit(-1);
			} finally {
				lock.unlock();
			}
//		}
	}

	// public void output(String s) {
	// lock.lock();
	// for (int k = 0; k < states.length; k++)
	// System.out.print(states[k] + ",");
	// lock.unlock();
	// System.out.println();
	// System.out.println();
	// }

	public void putSticks(int id) {
		lock.lock();
		try {
			states[id] = THINKING;
			int min = priority[0], mindex;
			for(int i = 0; i < priority.length; i++) {
				if(priority[i] < min) {
					min = priority[i];
					mindex = i;
				}
			}
			
			
//			if (states[leftof(id)] == WAITING
//					&& states[leftof(leftof(id))] != EATING) {
//				phil[leftof(id)].signal();
//				states[leftof(id)] = EATING;
//			}
//			if (states[rightof(id)] == WAITING
//					&& states[rightof(rightof(id))] != EATING) {
//				phil[rightof(id)].signal();
//				states[rightof(id)] = EATING;
//			}
		} finally {
			lock.unlock();
		}
	}
	
	

	private int leftof(int id) { // clockwise
		int retval = id - 1;
		if (retval < 0) // not valid id
			retval = NUM_PHILS - 1;
		return retval;
	}

	private int rightof(int id) {
		int retval = id + 1;
		if (retval == NUM_PHILS) // not valid id
			retval = 0;
		return retval;
	}
}