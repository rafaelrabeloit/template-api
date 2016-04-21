package com.neptune.templates.microservice.queue;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

import com.neptune.templates.microservice.queue.DelayedQueue.OnTimeListener;

public class DelayedQueueTest extends TestCase {
	
	int callCount = 0;

	// Helper lists
	List<DelayedItem> addList = new LinkedList<>();
	List<DelayedItem> removeList = new LinkedList<>();
	List<DelayedItem> retainList = new LinkedList<>();

	DelayedItem result;
	
	OnTimeListener listener = new OnTimeListener() {
		@Override
		public void onTime(DelayedItem e) {
			callCount++;
			result = e;
		}
	};
	
	private DelayedQueue queue;
	
	public DelayedQueueTest() { }

	@Override
	protected void setUp() throws Exception {
		callCount = 0;
		result = null;
		queue = new DelayedQueue();
		queue.setOnTimeListener(listener);
		
		
		for(int i = 0; i < 10; i++) {
			addList.add(new DelayedItem(DateTime.now().plus((i + 1) * 500 + 1000).getMillis(), new Integer(i)));
		}
		
		removeList.addAll(addList.subList(0, 4));
		retainList.addAll(addList.subList(3, 7));

		
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		queue.stop();
		
		addList.clear();
		removeList.clear();
		retainList.clear();
		
		super.tearDown();
	}
	
	@Test
	public void test_start_stop() throws InterruptedException {
		// Tests with the Queue stopped
		queue.stop();

		queue.add(new DelayedItem(DateTime.now().plus(10).getMillis()));
		queue.add(new DelayedItem(DateTime.now().plus(11).getMillis()));
		Thread.sleep(12);
		
		assertNotNull("Element was mistenkly removed with stoped timer", queue.peek());
		assertEquals("Listener method was called with timer stoped", 0, callCount);
		
		queue.add(new DelayedItem(DateTime.now().minusSeconds(1).getMillis()));
		assertEquals("Listener method was called with timer stoped with passed time", 0, callCount);

		// Start again to check if the elements are consumed
		queue.start();

		Thread.sleep(5);

		assertNull("Failed to remove the elements", queue.peek());
		assertEquals("All element should fire", 3, callCount);
	}
	
	@Test
	public void test_simpleAdds() throws InterruptedException {

		DelayedItem ontime = new DelayedItem(DateTime.now().plusSeconds(1).getMillis(), 1L);
		queue.add(ontime);
		Thread.sleep(1010);
		
		assertNull("Failed to remove the element", queue.peek());
		assertEquals("Listener method was not called", 1, callCount);
		assertEquals("The right element was not passed to the listener", ontime, result);
		
		DelayedItem late = new DelayedItem(DateTime.now().minusSeconds(1).getMillis(), 2L);
		queue.add(late);
		assertEquals("Listener not fired when add an already passed time", 2, callCount);
		assertEquals("The right element was not passed to the listener", late, result);

		queue.add(new DelayedItem(Long.MAX_VALUE));
		queue.add(new DelayedItem(Long.MAX_VALUE));
		assertTrue("Wrong size", queue.size() == 2);
	}

	@Test
	public void test_listAdds() throws InterruptedException {
		System.out.println("listAdds");
		queue.addAll(addList);
		System.out.println("listAdds end");
		assertEquals("Wrong size based on List", addList.size(), queue.size());
	}
	
	@Test
	public void test_simpleRemoves() throws InterruptedException {

		//This process is kind of slow...
		queue.addAll(addList);

		assertTrue("Removing the first item from the queue failed", queue.remove(addList.get(0)));
		assertFalse("The right element was removed", queue.contains(addList.get(0)));
		assertEquals("Wrong size based on List after removed", addList.size() - 1, queue.size());
		
		Thread.sleep(queue.peek().getTime() - DateTimeUtils.currentTimeMillis() + 30);
		assertEquals("Listener method was not called", 1, callCount);
		assertEquals("The right element was not passed to the listener", addList.get(1), result);

		assertEquals("Polled element failed", addList.get(2), queue.poll());
		assertEquals("Wrong size based on List after removed", addList.size() - 3, queue.size());

		Thread.sleep(queue.peek().getTime() - DateTimeUtils.currentTimeMillis() + 30);
		assertEquals("Listener method was not called", 2, callCount);
		assertEquals("The right element was not passed to the listener", addList.get(3), result);
		
		assertTrue("Removing an arbitrary item from the queue failed", queue.remove(addList.get(4)));
		assertFalse("The right element was removed", queue.contains(addList.get(4)));
		assertEquals("Wrong size based on List after removed", addList.size() - 5, queue.size());
		
	}
}