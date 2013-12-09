package com.ese2013.mub.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ese2013.mub.model.Menu;
import com.ese2013.mub.social.CurrentUser;
import com.ese2013.mub.social.User;

public class CurrentUserTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testConstructors() {
		CurrentUser currentUser = new CurrentUser("a.b@c.com");
		assertEquals("a.b@c.com", currentUser.getEmail());
		assertNull(currentUser.getNick());
		assertNull(currentUser.getId());

		CurrentUser currentUser2 = new CurrentUser("a.b@c.com", "some nick");
		assertEquals("a.b@c.com", currentUser2.getEmail());
		assertEquals("some nick", currentUser2.getNick());
		assertNull(currentUser2.getId());

		CurrentUser currentUser3 = new CurrentUser("a", "a.b@c.com", "some nick");
		assertEquals("a.b@c.com", currentUser3.getEmail());
		assertEquals("some nick", currentUser3.getNick());
		assertEquals("a", currentUser3.getId());
	}

	public void testAddAndRemoveFriends() {
		CurrentUser currentUser = new CurrentUser("a", "a.b@c.com", "some nick");
		User friend1 = new User("x", "xy@a.com", "some other nick");
		User friend2 = new User("y", "z@a.com", "some other nick 2");

		currentUser.addFriend(friend1);
		assertEquals(currentUser.getFriends().size(), 1);
		assertTrue(currentUser.getFriends().contains(friend1));

		currentUser.addFriend(friend2);
		assertEquals(currentUser.getFriends().size(), 2);
		assertTrue(currentUser.getFriends().contains(friend1));
		assertTrue(currentUser.getFriends().contains(friend2));

		currentUser.removeFriend(friend1);
		assertEquals(currentUser.getFriends().size(), 1);
		assertFalse(currentUser.getFriends().contains(friend1));
		assertTrue(currentUser.getFriends().contains(friend2));

		// remove again should not change anything
		currentUser.removeFriend(friend1);
		assertEquals(currentUser.getFriends().size(), 1);
		assertFalse(currentUser.getFriends().contains(friend1));
		assertTrue(currentUser.getFriends().contains(friend2));

		currentUser.removeFriend(friend2);
		assertEquals(currentUser.getFriends().size(), 0);
		assertFalse(currentUser.getFriends().contains(friend1));
		assertFalse(currentUser.getFriends().contains(friend2));
	}

	public void testUserRatingList() {
		CurrentUser currentUser = new CurrentUser("a", "a.b@c.com", "some nick");
		Menu.MenuBuilder builder = new Menu.MenuBuilder().setId("m").setTitle("title").setDescription("desc");
		Menu menu1 = builder.build();
		Menu menu2 = builder.setId("n").build();
		List<String> rated = new ArrayList<String>();
		rated.add(menu2.getId());
		currentUser.setRatedMenuIds(rated);
		assertFalse(currentUser.hasBeenRated(menu1));
		assertTrue(currentUser.hasBeenRated(menu2));
		currentUser.addToRated(menu1);
		assertTrue(currentUser.hasBeenRated(menu1));

		Menu menu3 = builder.setId("o").build();
		assertFalse(currentUser.hasBeenRated(menu3));
		currentUser.addToRated(menu3);
		assertTrue(currentUser.hasBeenRated(menu3));
	}

	public void testHasFriendWith() {
		CurrentUser currentUser = new CurrentUser("a", "a.b@c.com", "some nick");
		User friend = new User("x", "xy@a.com", "some other nick");

		assertFalse(currentUser.hasFriendWith("xy@a.com"));
		currentUser.addFriend(friend);
		assertTrue(currentUser.hasFriendWith("xy@a.com"));
	}
}