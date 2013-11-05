package com.ese2013.mub.test;

import static junit.framework.Assert.assertFalse;

public class Util {

	public static void assertNotEquals(Object obj1, Object obj2) {
		assertFalse(obj2.equals(obj1));
		assertFalse(obj1.equals(obj2));
	}
}
