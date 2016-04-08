import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
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
	private final int TURNS = 10;
	private AtomicInteger counter;
	private int lastAte = 0;
	private ArrayList<Integer> waits;
	private double[] avgs;
	private int[] max;
	public Philosopher(Lock l, Condition p[], int st[], int priority[], int num, int ID, AtomicInteger counter, 
				int[] appetites, double[] averageWait, int[] max) {
		lastAte = 0;
		lock = l;
		phil = p;
		states = st;
		NUM_PHILS = num;
		id = ID;
		this.counter = counter;
		this.appetites = appetites;
		waits = new ArrayList<Integer>();
		avgs = averageWait;
		this.max = max;
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
			putSticksFairly(id);
		}

//		System.out.println(id + " wait times: " + waits + " size: "
//				+ waits.size());
		
		int sum  = 0;
		for (int i = 0; i < waits.size(); i++) {
			sum += waits.get(i);
		}
		
		double average = (double)sum / waits.size();
		
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

	public boolean canEat(int id) {
		return (states[id] == WAITING
				&& states[leftof(id)] != EATING
				&& states[rightof(id)] != EATING);
	}
		
	private void eat() {
		System.out.println("id : " + id + " is eating");
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
			int hungriest = (int)(Math.random() * appetites.length);
			for (int i = 0; i < appetites.length; i ++) {
				if (appetites[i] < appetites[hungriest]) {
					hungriest = i;
				}
			}
			
//			System.out.println("\n\n");
//			System.out.printf("Current iteration: %d\n", counter.get());
//			System.out.printf("%d is hungriest\n", hungriest);
//			for (int i = 0; i < appetites.length; i++) {
//				System.out.printf("Philospher: %d\tAppetite: %d\n", i, appetites[i]);
//			}
//			System.out.println("\n\n");
//			
			if (canEat(hungriest)) {
				phil[hungriest].signal();
			}
			else {
				// do not alter state at risk of further starving hungriest
				if (states[leftof(id)] == WAITING
							&& states[leftof(leftof(id))] != EATING) {
						phil[leftof(id)].signal();
					}
					if (states[rightof(id)] == WAITING
							&& states[rightof(rightof(id))] != EATING) {
						phil[rightof(id)].signal();
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