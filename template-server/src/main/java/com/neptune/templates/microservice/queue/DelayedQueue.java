package com.neptune.templates.microservice.queue;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeUtils;

public class DelayedQueue extends PriorityBlockingQueue<DelayedItem> {
	final static Logger logger = LogManager.getLogger(DelayedQueue.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Lock lock = new ReentrantLock();

	/**
	 * 
	 */
	//private ScheduledThreadPoolExecutor executor = null;
	private ExecutorService executor = null;

	/**
	 * 
	 */
	//private ScheduledFuture<?> futureConsumer = null;
	private DelayedConsumer futureConsumer = null;

	/**
	 * Dummy listener to avoid null pointer exceptions
	 */
	private static final OnTimeListener dummy = new OnTimeListener() {
		@Override
		public void onTime(DelayedItem e) {
		}
	};

	/**
	 * Timing listener. Starts with null, but is assigned to a dummy later
	 */
	private OnTimeListener onTimeListener = dummy;

	/**
	 * Thread state...
	 */
	private boolean started;

	/**
	 * Initialize the Queue with an Executor and a Thread Timer in it
	 */
	public DelayedQueue() {
		super();

		//executor = (ScheduledThreadPoolExecutor) Executors
		//		.newScheduledThreadPool(1);
		executor = Executors.newFixedThreadPool(2);
		//executor.setRemoveOnCancelPolicy(true);

		this.start();
	}

	/**
	 * Start the thread executor, and the thread itself
	 */
	public void start() {
		if (!started) {
			started = true;

			reschedule();
		}
	}

	/**
	 * Shutdown the thread executor, and the thread by consequence, by
	 * interrupting it
	 */
	public void stop() {
		started = false;

		if (futureConsumer != null) {
			futureConsumer.interrupt();
		}
	}

	private void reschedule() {

		if (!started) {
			return;
		}

		if (futureConsumer != null) {
			futureConsumer.interrupt();
		}

		if (peek() != null) {
			long waitingTime = peek().getTime()
					- DateTimeUtils.currentTimeMillis();

			//futureConsumer = executor.schedule(new DelayedConsumer(),
			//		waitingTime, TimeUnit.MILLISECONDS);

			futureConsumer = new DelayedConsumer(waitingTime);
			futureConsumer.start();
			
			logger.info("scheduling " + peek() + " to " + waitingTime + " ms");
		} else {
			futureConsumer = null;
			logger.info("Empty queue");
		}
	}

	/**
	 * Set the listener for when the timer for the top object is fired. Setting
	 * it to null will disable listening.
	 * 
	 * @param onTimeListener
	 */
	public void setOnTimeListener(OnTimeListener onTimeListener) {
		if (onTimeListener != null) {
			this.onTimeListener = onTimeListener;
		} else {
			// Empty listener that doesn't cause null pointer exceptions
			this.onTimeListener = dummy;
		}
	}

	/**
	 * Depends on offer() implementation
	 */
	@Override
	public void put(DelayedItem e) {
		super.put(e);
	}

	/**
	 * Depends on offer() implementation
	 */
	@Override
	public boolean add(DelayedItem e) {
		return super.add(e);
	}

	/**
	 * Depends on offer() implementation
	 */
	@Override
	public boolean addAll(Collection<? extends DelayedItem> c) {
		return super.addAll(c);
	}

	/**
	 * Depends on offer() implementation
	 */
	@Override
	public boolean offer(DelayedItem e, long timeout, TimeUnit unit) {
		return super.offer(e, timeout, unit);
	}

	@Override
	public boolean offer(DelayedItem e) {

		boolean returned = false;

		lock.lock();

		try {

			// If the time has passed already, doesn't even add to the queue
			if (e.getTime() < DateTimeUtils.currentTimeMillis() && started) {
				this.onTimeListener.onTime(e);
				logger.info("Consuming early " + e);
				return true;
			}

			returned = super.offer(e);

			if (returned == true && peek() == e) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	/**
	 * Depends on poll() implementation
	 */
	@Override
	public DelayedItem remove() {
		return super.remove();
	}

	@Override
	public boolean remove(Object o) {

		boolean returned = false;
		boolean shouldNotify = false;

		lock.lock();

		try {

			shouldNotify = o.equals(peek());
			returned = super.remove(o);

			if (returned == true && shouldNotify) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	@Override
	public DelayedItem poll(long timeout, TimeUnit unit)
			throws InterruptedException {

		DelayedItem returned;

		lock.lock();

		try {

			returned = super.poll(timeout, unit);

			if (returned != null) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	@Override
	public DelayedItem poll() {

		DelayedItem returned;

		lock.lock();

		try {

			returned = super.poll();

			if (returned != null) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	@Override
	public DelayedItem take() throws InterruptedException {

		DelayedItem returned;

		lock.lock();

		try {

			returned = super.take();

			if (returned != null) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	@Override
	public boolean removeAll(Collection<?> c) {

		boolean returned = false;
		boolean shouldNotify = false;

		lock.lock();

		try {

			shouldNotify = c.contains(peek());
			returned = super.removeAll(c);

			if (returned == true && shouldNotify) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		boolean returned = false;
		boolean shouldNotify = false;

		lock.lock();

		try {

			shouldNotify = !c.contains(peek());
			returned = super.retainAll(c);

			if (returned == true && shouldNotify) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	/**
	 * Depends on drainTo() implementation
	 */
	@Override
	public int drainTo(Collection<? super DelayedItem> c) {
		return super.drainTo(c);
	}

	@Override
	public int drainTo(Collection<? super DelayedItem> c, int maxElements) {

		int returned;

		lock.lock();

		try {

			returned = super.drainTo(c, maxElements);

			if (returned > 0) {
				reschedule();
			}

		} finally {
			lock.unlock();
		}

		return returned;
	}

	@Override
	public void clear() {

		lock.lock();

		try {

			super.clear();

			reschedule();

		} finally {
			lock.unlock();
		}
	}

	/**
	 * Interface for Listening on timings
	 */
	public static interface OnTimeListener {
		public void onTime(DelayedItem e);
	}

	/**
	 * Thread that will wait until the time for the top element, or a new
	 * element is inserted or removed
	 */
	private class DelayedConsumer extends Thread {
		
		private long waitingTime;

		public DelayedConsumer(long waitingTime) {
			this.waitingTime = waitingTime;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e1) {	}
			
			DelayedQueue.this.lock.lock();

			try {
				// A race condition that can happen is that the main thread can start polling an item just before the task fires.
				// In this case, the task will be stopped on .lock, and cancelled
				// We then check if this thread is interrupted. If it is, then the item was polled before we could consume it and we should just die silently
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				
				DelayedItem item = DelayedQueue.this.poll();
				
				DelayedQueue.this.onTimeListener.onTime(item);
				
				logger.info("Consumed " + item);
			} catch (InterruptedException e) {
				logger.info("Item was polled or deleted before we could consume it");
			} finally {
				DelayedQueue.this.lock.unlock();
			}
		}
	}

}