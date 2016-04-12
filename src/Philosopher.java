import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

public class Philosopher extends Thread {
	private static int WAITING = 0, EATING = 1, THINKING = 2, OFFSET = 0;
	private Lock lock;
	private Condition phil[];
	private int states[];
	private int[] appetites;
	private int NUM_PHILS;
	private int id;
	private final int TURNS = 20;
	private AtomicInteger counter;
	private int lastAte = 0;
	private ArrayList<Integer> waits;
	private double[] avgs;
	private int[] max;
	public Philosopher(Lock l, Condition p[], int st[], int priority[], int num, int ID, AtomicInteger counter, 
				int[] appetites, double[] averageWait, int[] max) {
		lock = l;
		phil = p;
		states = st;
		NUM_PHILS = num;
		id = ID;
		this.counter = counter;
		this.appetites = appetites;
		lastAte = counter.getAndIncrement();
		appetites[id] = lastAte; 		//  to accurately reflect wait times
		waits = new ArrayList<Integer>();
		avgs = averageWait;
		this.max = max;
	}

	public void run() {
		for (int k = 0; k < TURNS; k++) {
			try {
				Thread.sleep(100);
			} catch (Exception ex) { /* lazy */
			}
			takeSticksFairly(id);
			try {
				Thread.sleep(20);
			} catch (Exception ex) {
			}
			
			putSticksFairly(id);
		}
		
		
		int sum  = 0;
		for (int i = 0; i < waits.size(); i++) {
			sum += waits.get(i);
		}
		
		double average = (double)sum / waits.size();
		
		System.out.println("Phil: " + this.id + " waits: " + waits);
		
		max[id] = Collections.max(waits);
		avgs[id] = average;
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
			lock.lock();
			states[id] = WAITING;	
			int app = appetites[this.id];
			try {
				if ((appetites[rightof(this.id)] >= app) && (appetites[leftof(this.id)] >= app)) {
					if (canEat(this.id)) {
						// go eat
					}
					else {
						phil[this.id].await();
					}
					
					eat();
					break; // from while
				} 
				else {
					continue; // trying to eat
				}
			} catch (InterruptedException e) {
				System.exit(-1);
			} finally {
				lock.unlock();
			}
		}
	}

	public boolean canEat(int id) {
		return (states[id] == WAITING
				&& states[leftof(id)] != EATING
				&& states[rightof(id)] != EATING);
	}
		
	private void eat() {
		System.out.println(id + " is eating");
		int count = counter.incrementAndGet();
		waits.add(count - lastAte);
		lastAte = count;
		appetites[id] = lastAte;
		states[id] = EATING;	
	}	
	
	public void putSticks(int id) {
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
	
		
	public void putSticksFairly(int id) {
		lock.lock();
		try {
			states[id] = THINKING;
			
			TreeMap<Integer, Integer> pool = new TreeMap<Integer, Integer>();
			for (int pid = 0; pid < phil.length; pid++) {
				pool.put(appetites[pid], pid);
			}

			int i = 0;
			int[] order = new int[phil.length];
			Iterator<Integer> it = pool.keySet().iterator();
			while (it.hasNext()) {
				Integer app = it.next();
				Integer pid = pool.get(app);
				order[i++] = pid;
			}

			
			for (int pid = 0; pid < phil.length; pid++) {
				if (canEat(order[pid])) {
					phil[order[pid]].signal();
				}
				else {
					// do nothing
				}
			}
		}
		finally {
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