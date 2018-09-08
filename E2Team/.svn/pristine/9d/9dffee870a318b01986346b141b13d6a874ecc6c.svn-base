package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to control moving forward a step. When hold() is called, a flag is set
 * to true, and the thread waits for the GUI thread to call next(), at which
 * point the thread is able to continue. This is used for controlling the speed
 * of the continous mode, and to move forward in step-by-step mode.
 * 
 * @author mxw449
 */
public class Holder {
	private static final Logger log = LoggerFactory.getLogger(Holder.class);

	/**
	 * Registers if something's holding
	 */
	private boolean isHeld = false;

	/**
	 * Wait until next is called by different thread
	 */
	public synchronized void hold() {
		try {
			log.info("Holding");
			isHeld = true;
			this.wait();
			isHeld = false;
		} catch (InterruptedException e) {
			log.info("Interrupted holding, but trying again");
			hold();
		}
	}

	/**
	 * Release the hold
	 */
	public synchronized void next() {
		if (isHeld) {
			log.info("Continuing");
			this.notify();
		} else {
			log.info("Told to continue, but not being held");
		}
	}

	public void done() {
	}
}
