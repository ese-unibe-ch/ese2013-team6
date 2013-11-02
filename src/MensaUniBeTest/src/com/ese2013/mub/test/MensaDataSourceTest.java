package com.ese2013.mub.test;

import android.test.AndroidTestCase;

import com.ese2013.mub.util.database.MensaDataSource;

public class MensaDataSourceTest extends AndroidTestCase {

	MensaDataSource dataSource = new MensaDataSource(getContext());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataSource = new MensaDataSource(getContext());
		dataSource.open();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		dataSource.close();
	}

	public void testPreConditions() {
		assertNotNull(dataSource);
	}
}
