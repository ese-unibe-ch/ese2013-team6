var _ = require('underscore');
var moment = require('moment');

var tok = '?tok=6112255ca02b3040711015bbbda8d955';

/*
This code is run directly on the parse server as so called "Cloud Code".
*/


/*
Deletes the content of the given table.
*/
function dropTable(name) {
	var query = new Parse.Query(name);
	return query.find().then(function (results) {
		return Parse.Object.destroyAll(results);
	});
}

/*
Downloads the menu plans for the given mensas.
*/
function getMenuPlan(mensaList) {
	console.log("getting menu plans...");

	var promise = Parse.Promise.as();
	var menuList = new Array();
	var menusMensas = new Array();
	var menuMap = {};
	var currentMenusTable;
	var query = new Parse.Query("Menu");
	
	promise = promise.then(function() {
		return query.find();
	});
	promise = promise.then(function(results) {
		_.each(results, function(result) {
			menuMap[result.get("description")] = result;
		});
		console.log("found ALL existing menus");
	});
	
	_.each(mensaList, function(mensa) {
				promise = promise.then(function() {
					console.log("initaiting http request for a menu plan");
					menuList = new Array();
					menusMensas = new Array();
					var weeklyPlanUrl = 'http://mensa.xonix.ch/v1/mensas/' + mensa.get("mensaId") + '/weeklyplan' + tok;
					return Parse.Cloud.httpRequest({url: weeklyPlanUrl});
				}).then( function(httpResponse) {
					console.log("http response got");
					if (parseInt(httpResponse.data.result.code) == 200) {
						var menus = httpResponse.data.result.content.menus;
						for (var i = 0; i < menus.length; ++i) {
							var Menu = Parse.Object.extend("Menu");
							var menuJson = menus[i];
							var desc = "";
							for (var j = 0; j < menuJson.menu.length; ++j) {
								desc += menuJson.menu[j] + "\n";
							} 
							var menu;
							if (menuMap[desc]) {
							 menu = menuMap[desc];
							} else {
								menu = new Menu();
								menu.set("title", menuJson.title);
								menu.set("description", desc);
								menu.set("ratingSum", 0);
								menu.set("ratingCount", 0);
								menuList.push(menu);
								menuMap[desc] = menu;
							}
							var MenuMensa = Parse.Object.extend("MenuMensa");
							var menuMensa = new MenuMensa();
							menuMensa.set("mensa", mensa);
							menuMensa.set("menu", menu);
							menuMensa.set("date", menuJson.date);
							menusMensas.push(menuMensa);
						}
					}
				}).then( function() {
					return Parse.Object.saveAll(menuList);
				}).then( function() {
					return Parse.Object.saveAll(menusMensas);
				});
		});
	return promise;
}

/*
Update job which is run periodically on the Parse server to update the menus and mensas on the server.
*/
Parse.Cloud.job("menuUpdate", function(request, status) {
	dropTable("Mensa")
	var mensaList = new Array();
	dropTable("MenuMensa").then(function() {
		return Parse.Cloud.httpRequest({url: 'http://mensa.xonix.ch/v1/mensas' + tok});
	}).then( function(httpResponse) {
		if (parseInt(httpResponse.data.result.code) == 200) {
			var mensas = httpResponse.data.result.content;
			for (var i = 0; i < mensas.length; ++i) {
				var Mensa = Parse.Object.extend("Mensa");
				var mensaJson = mensas[i];
				var m = new Mensa();
				m.set("mensaId", mensaJson.id);
				m.set("name", mensaJson.mensa);
				m.set("street", mensaJson.street);
				m.set("plz", mensaJson.plz);
				m.set("lat", mensaJson.lat);
				m.set("lon", mensaJson.lon);
				mensaList[i] = m;
			}
			Parse.Object.saveAll(mensaList);
			return getMenuPlan(mensaList);
		} else {
			status.error("failed to download");
		}
	}).then(function() {
		status.success("succeeded");
	});
});

/*
Executed when a new friendship is saved to ensure uniqueness.
*/
Parse.Cloud.beforeSave("Friendship", function(request, response) {
	var query = new Parse.Query("Friendship");
	var ct = 0;
	var existing;
	query.find({
		  success: function(results) {
			_.each(results, function(result) {
				if ((request.object.get("user1").id == result.get("user1").id && request.object.get("user2").id == result.get("user2").id) ||
					(request.object.get("user1").id == result.get("user2").id && request.object.get("user2").id == result.get("user2").id)) {
					result.destroy();
					ct += 1;
				}
			});
			if (ct == 1) {
				console.log("Added already existing friendship");
			}
			if (ct > 1){
				console.log("Friendship more than once in database, should not happen!");
			}
			response.success();
		  },
		  error: function(error) {
			response.error("Saving failed");
		  }
	});
});


/*
Executed when a new friendRequest is saved to ensure uniqueness.
*/
Parse.Cloud.beforeSave("FriendRequest", function(request, response) {
	var query = new Parse.Query("FriendRequest");
	var ct = 0;
	var existing;
	query.find({
		  success: function(results) {
			_.each(results, function(result) {
				if ((request.object.get("From").id == result.get("From").id && 
					 request.object.get("To").id == result.get("To").id)) {

					result.destroy();
					ct += 1;
				}
			});
			if (ct == 1) {
				console.log("Added already existing FriendRequest");
			}
			if (ct > 1){
				console.log("FriendRequest more than once in database, should not happen!");
			}
			response.success();
		  },
		  error: function(error) {
			response.error("Saving failed");
		  }
	});
});

/*
Update job which is run periodically on the Parse server to remove outdated invitations 
(older than one day).
*/
Parse.Cloud.job("invitationsUpdate", function(request, status) {
	var query = new Parse.Query("Invitation");
	var now = moment();
	now.subtract('days', 1);
	query.lessThan("Time", now.toDate());
	var invitations = new Array();
	query.find().then( function(results) {
		_.each(results, function(result) {
			invitations.push(result);
		});
	}).then( function() {
		var promise = Parse.Promise.as();
		_.each(invitations, function(invite) {				
			var invUserQuery = new Parse.Query("InvitationUser");
			invUserQuery.equalTo("Invitation", invite);
			promise = promise.then(function() {
					return invUserQuery.find({
						success: function(results2) {
							Parse.Object.destroyAll(results2);
						},
						error: function(error2) {
							status.error("Update failed (inner query)");
						}
					});
				});
		});
		promise = promise.then(function() {
			Parse.Object.destroyAll(invitations)
			status.success("done updating table");
		});
	});
});