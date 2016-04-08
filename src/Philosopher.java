import java.util.ArrayList;

import java.util.Arrays;
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
	private final int TURNS = 20;
	private AtomicInteger counter;
	private int lastAte = 0;
	private ArrayList<Integer> waits;

	public Philosopher(Lock l, Condition p[], int st[], int priority[], int num, int ID, AtomicInteger counter, int[] appetites) {
		lastAte = 0;
		lock = l;
		phil = p;
		states = st;
		NUM_PHILS = num;
		id = ID;
		this.counter = counter;
		this.appetites = appetites;
		waits = new ArrayList<Integer>();
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
//		System.out.println("id : " + id + " is eating");
		int count = counter.incrementAndGet();
		waits.add(count - lastAte);
		lastAte = count;
		appetites[id] = lastAte;
		states[id] = EATING;	
	}
		
//		if (states[leftof(id)] == WAITING
//					&& states[leftof(leftof(id))] != EATING) {
//				phil[leftof(id)].signal();
//				states[leftof(id)] = EATING;
//			}
//			if (states[rightof(id)] == WAITING
//					&& states[rightof(rightof(id))] != EATING) {
//				phil[rightof(id)].signal();
//				states[rightof(id)] = EATING;
//			}
			
		
	public void putSticks(int id) {
		lock.lock();
		try {
			states[id] = THINKING;	
			int hungriest = (int)(Math.random() * appetites.length);
			for (int i = 0; i < appetites.length; i ++) {
				if (appetites[i] < appetites[hungriest]) {
					hungriest = i;
//					for (int j = 0; j < order.length - 1; j++) {
//						order[j + 1] = order[j];
//						order[j] = hungriest;
//					}
				}
			}
			
//			System.out.println("\n\n\n");
			System.out.printf("%d is hungriest\n", hungriest);
			for (int i = 0; i < appetites.length; i++) {
				System.out.printf("Philospher: %d\tAppetite: %d\n", i, appetites[i]);
			}
			
			System.out.println("\n\n\n");
			
//			for (int i = 0; i < order.length; i++) {
				if (canEat(hungriest)) {
					phil[hungriest].signal();
//					break;
				}
				else {
					// signal a philospher that can eat
				}
//			}
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