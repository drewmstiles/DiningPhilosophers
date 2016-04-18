import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class Philosopher extends Thread {
	
	public static int WAITING = 0, EATING = 1, THINKING = 2;
	public static final int TURNS = 20;	
	
	public Philosopher
		(Lock l, Condition p[], int st[], int num, int ID, AtomicInteger cnt, int[] apps, double[] max) {
		
		// default implementation
		lock = l;
		phil = p;
		states = st;
		NUM_PHILS = num;
		id = ID;
		
		// starvation free implementation
		this.counter = cnt; // reference to timestamp allocator
		this.appetites = apps; // reference to all others appetites
		this.max = max; // shared array for writing this philosophers max wait
		waits = new ArrayList<Integer>(); // reference to this philosophers waits
		appetites[id] = lastAte = counter.incrementAndGet(); // assign timestamp on entry
	}
	

	public void run() {
		for (int k = 0; k < TURNS; k++) {
			
			try { Thread.sleep(100); } 
			catch (Exception ex) {}
			
			takeSticksFairly(id);
			
			try { Thread.sleep(20); } 
			catch (Exception ex) {}
			
			putSticks(id);
		}
		
		// write max wait for this philosopher
		max[id] = Collections.max(waits);
	}
	
	
	public void takeSticks(int id) {
		lock.lock();
		states[id] = WAITING;
		try {
			if (!canEat(this.id)) {
				phil[id].await();
			} 
			else {
				// go eat
			}
			
			eat();
			
		} catch (InterruptedException e) {
			System.exit(-1);
		} finally {
			lock.unlock();
		}
	}
	

	public void takeSticksFairly(int id) {
		while (true) {
			states[id] = WAITING;
			boolean hungrierThanRight = (appetites[rightof(this.id)] >= appetites[this.id]);
			boolean hungrierThanLeft = (appetites[leftof(this.id)] >= appetites[this.id]);
			if (hungrierThanLeft && hungrierThanRight) {
				lock.lock();
				try {
					if (canEat(this.id)) {
						// go eat
					}
					else {
						phil[this.id].await(); // oppurtunity to eat
					}
					
					eat();
					
					break; // from while
					
				} catch (InterruptedException e) {
					System.exit(-1);
				} finally {
					lock.unlock();
				}
			} 
			else {
				continue; // trying to eat
			}
		}
	}
	
	
	private void putSticks(int id) {
		lock.lock();
		try {
			states[id] = THINKING;
			
			if (states[leftof(id)] == WAITING
					&& states[leftof(leftof(id))] != EATING) {
				phil[leftof(id)].signal();
			}
			if (states[rightof(id)] == WAITING
					&& states[rightof(rightof(id))] != EATING) {
				phil[rightof(id)].signal();
			}
		} finally {
			lock.unlock();
		}
	}
	

	private boolean canEat(int id) {
		return (states[id] == WAITING
				&& states[leftof(id)] != EATING
				&& states[rightof(id)] != EATING);
	}
		
	private void eat() {
		System.out.printf("Philosopher %3d is eating\n", id);
		int count = counter.incrementAndGet();
		waits.add(count - lastAte);
		lastAte = count;
		appetites[id] = lastAte;
		states[id] = EATING;	
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
	
	
	private Lock lock;
	private Condition phil[];
	private int states[];
	private int[] appetites;
	private int NUM_PHILS;
	private int id;
	private AtomicInteger counter;
	private int lastAte;
	private ArrayList<Integer> waits;
	private double[] max;

} // end class Philosopher


