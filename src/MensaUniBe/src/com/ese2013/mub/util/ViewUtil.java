package com.ese2013.mub.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for views, contains a method to generate unique view IDs from a
 * newer Android version (v17).
 */
public class ViewUtil {

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in view.setId(). This value will not
	 * collide with ID values generated at build time by aapt for R.id. All the
	 * values in R.id have the higher order bits set to 0. This implementation
	 * is equal to the one in View.class for Android v17 and above. This is a
	 * copy because this project is intended to target v14.
	 * 
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range
			// under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}
}
