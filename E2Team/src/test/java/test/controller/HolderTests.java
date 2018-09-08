package test.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import controller.Holder;
/**
 * JUnit testing for Holder
 * @author mxw449
 */
public class HolderTests {

	private static final long CATCH_UP_TIME = 200L;

	/**
	 * Class that holds using Holder, and sets a flag at either side.
	 */
	private class HoldTest implements Runnable {
		private final Holder holder;
		public boolean testFlag = true;

		public HoldTest(Holder _holder) {
			this.holder = _holder;
		}

		@Override
		public void run() {
			this.testFlag = false;
			holder.hold();
			this.testFlag = true;
		}
	}

	@Test
	public void testWait() throws InterruptedException {
		Holder holder = new Holder();
		HoldTest holdTest = new HoldTest(holder);

		Thread t = new Thread(holdTest);

		t.start();
		Thread.sleep(CATCH_UP_TIME);

		assertFalse(holdTest.testFlag);

		holder.next();
		Thread.sleep(CATCH_UP_TIME);

		assertTrue(holdTest.testFlag);
	}
}
