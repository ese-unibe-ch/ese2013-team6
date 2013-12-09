package com.ese2013.mub.social;

import java.util.ArrayList;
import java.util.List;

import com.ese2013.mub.model.Menu;

/**
 * Represents the currently logged in User. An instance can be retrieved by
 * using LoginService.getLoggedInUser(). Stores also the friends list and the
 * unanswered friend requests.
 * 
 */
public class CurrentUser extends User {
	private List<User> friends = new ArrayList<User>();
	private List<FriendRequest> requests = new ArrayList<FriendRequest>();
	private List<String> ratedMenuIds;

	/**
	 * Creates a CurrentUser given an email. This should only be used if the
	 * CurrentUser object is used to query the database for the User Id and
	 * Nickname.
	 * 
	 * @param email
	 *            String containing the user's email. Must not be null.
	 */
	public CurrentUser(String email) {
		super(email);
	}

	/**
	 * Creates a CurrentUser given an email and nick name. Should only be used
	 * if the instance of CurrentUser is used to register a User.
	 * 
	 * @param email
	 *            String containing the user's email. Must not be null.
	 * @param nick
	 *            String containing the user's nick name. Must not be null.
	 */
	public CurrentUser(String email, String nick) {
		super(null, email, nick);
	}

	/**
	 * Creates a CurrentUser from id, email and nick name.
	 * 
	 * @param id
	 *            String id, should be the id retrieved from the Parse-Server.
	 * @param email
	 *            String email, should be the email associated to the id. Must
	 *            not be null.
	 * @param nick
	 *            String nick name, should be the nick name associated to the
	 *            id. Must not be null.
	 */

	public CurrentUser(String id, String email, String nick) {
		super(id, email, nick);
	}

	/**
	 * Adds a User as friend to this CurrentUser.
	 * 
	 * @param user
	 *            User to be added. Must not be null and must have a valid id
	 *            from Parse-Server. Also must be a friend of the User in the
	 *            Parse-Database.
	 */
	public void addFriend(User user) {
		friends.add(user);
	}

	/**
	 * Removes a User from the list of friends.
	 * 
	 * @param user
	 *            Removes the given User if contained in the list of friends.
	 */
	public void removeFriend(User user) {
		friends.remove(user);
	}

	/**
	 * Returns the list of friends of this CurrentUser. Should not be modified
	 * directly (instead the addFriend and removeFriend methods can be used).
	 * 
	 * @return List of Users which are friends of this CurrentUser. Can be
	 *         empty, but not null.
	 */
	public List<User> getFriends() {
		return friends;
	}

	/**
	 * Sets the list of friends.
	 * 
	 * @param friends
	 *            New List of User objects. Must not be null and the contained
	 *            Users must all have a valid id.
	 */
	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	/**
	 * List of FriendRequest the User received. Should not be modified directly
	 * (for this the removeFriendRequest method should be used).
	 * 
	 * @return
	 */
	public List<FriendRequest> getFriendRequests() {
		return requests;
	}

	/**
	 * Sets the list of FriendRequests.
	 * 
	 * @param requests
	 *            List of FriendRequests. Must not be null and not contain any
	 *            null values. Also all the FriendRequests need to have a valid
	 *            String id.
	 */
	public void setFriendRequests(List<FriendRequest> requests) {
		this.requests = requests;
	}

	/**
	 * Removes a FriendRequest.
	 * 
	 * @param request
	 *            FriendRequest to be removed.
	 */
	public void removeFriendRequest(FriendRequest request) {
		requests.remove(request);
	}

	/**
	 * Sets the list of Menus the CurrentUser has already rated.
	 * 
	 * @param ratedMenuIds
	 *            List of Menu Id (String) the user has already rated. Must not
	 *            be null.
	 */
	public void setRatedMenuIds(List<String> ratedMenuIds) {
		this.ratedMenuIds = ratedMenuIds;
	}

	/**
	 * Checks if the CurrentUser has already rated a certain Menu.
	 * 
	 * Before this method can be used, the method setRatedMenuIds() has to be
	 * used to set the list of rated menus. (the rated menus are always
	 * retrieved from the server, thus this method has no use if they were not
	 * retrieved).
	 * 
	 * @param menu
	 *            Menu to be checked. Must not be null and must have a valid id.
	 * @return true if the Menu has already been rated, false otherwise.
	 */
	public boolean hasBeenRated(Menu menu) {
		return ratedMenuIds.contains(menu.getId());
	}

	/**
	 * Adds a Menu's id to the list of rated Menus.
	 * 
	 * Before this method can be used, the method setRatedMenuIds() has to be
	 * used to set the list of rated menus. (the rated menus are always
	 * retrieved from the server, thus this method has no use if they were not
	 * retrieved).
	 * 
	 * @param menu
	 *            Menu to be added. Must not be null and have a valid id.
	 */
	public void addToRated(Menu menu) {
		ratedMenuIds.add(menu.getId());
	}

	/**
	 * Checks if the CurrentUser has already a friend with the given email
	 * address.
	 * 
	 * @param email
	 *            String of the email which should be checked.
	 * @return true if the CurrentUser has already a friend with the given email
	 *         address.
	 */
	public boolean hasFriendWith(String email) {
		for (User u : friends)
			if (u.getEmail().equals(email))
				return true;
		return false;
	}
}