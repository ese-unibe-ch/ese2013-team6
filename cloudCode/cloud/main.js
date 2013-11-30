var _ = require('underscore');
var tok = '?tok=6112255ca02b3040711015bbbda8d955';

function dropTable(name) {
	var query = new Parse.Query(name);
	query.find({
	  success: function(results) {
		Parse.Object.destroyAll(results);
	  },
	  error: function(error) {
	  }
	});
}

function getMenuPlan(mensaList) {
	console.log("getting menu plans...");
	
	var promise = Parse.Promise.as();
	var menuList = new Array();
	var menusMensas = new Array();
	var menuMap = {};
	var currentMenusTable;
	promise = promise.then(function() {
		var query = new Parse.Query("Menu");
		return query.find({
		  success: function(results) {
			_.each(results, function(result) {
				menuMap[result.get("description")] = result;
			});
		  },
		  error: function(error) {
		  }
		});
	});
	_.each(mensaList, function(mensa) {
				promise = promise.then(function() {
				menuList = new Array();
				menusMensas = new Array();
				var weeklyPlanUrl = 'http://mensa.xonix.ch/v1/mensas/' + mensa.get("mensaId") + '/weeklyplan' + tok;
				return Parse.Cloud.httpRequest({
					url: weeklyPlanUrl,
					success: function(httpResponse) {
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
								menu = new Menu
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
					},
					error: function(httpResponse) {
					console.log("Error retrieving menus");
					}
				}).then( function() {
					return Parse.Object.saveAll(menuList);
				}).then( function() {
					return Parse.Object.saveAll(menusMensas);
				});
		});
	});
	return promise;
}

Parse.Cloud.job("menuUpdate", function(request, status) {
		
	dropTable("Mensa");
	dropTable("MenuMensa");
	var mensaList = new Array();
	Parse.Cloud.httpRequest({
      url: 'http://mensa.xonix.ch/v1/mensas' + tok,
      success: function(httpResponse) {
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
		}
		Parse.Object.saveAll(mensaList);
      },
      error: function(httpResponse) {
        status.success('Request failed with response code ' + httpResponse.status);
      }
    }).then(function() {
		getMenuPlan(mensaList).then(function() {
			status.success("succeeded")
		});
	});
});

/*
Parse.Cloud.job("translateTest", function(request, status) {

	var text = "Use pixels to express measurements for padding and margins.";
	var from = "en";
	var to = "de";

	var uri = "http://api.microsofttranslator.com/v2/Http.svc/Translate?text=" + encodeURIComponent(text) + "&from=" + from + "&to=" + to;
	console.log(uri);
	var authToken = "Bearer" + " " + "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier=0166541233&http://schemas.microsoft.com/accesscontrolservice/2010/07/claims/identityprovider=https://datamarket.accesscontrol.windows.net/&Audience=http://api.microsofttranslator.com&ExpiresOn=1322323459&Issuer=https://datamarket.accesscontrol.windows.net/&HMACSHA256=0qIUvO513ttc4iBzJN+9An3GmUbnggQwT8R5Y3pVSWs=" + "T35oR9q6ukB/GbuYAg4nsL09yRsp9j5afWjULfWfmuY=";

	
	Parse.Cloud.httpRequest({
	method: 'GET',
		headers: {
			'Authorization': authToken
		},
      url: uri,
      success: function(httpResponse) {
		console.log(httpResponse.text);
		status.success('translate succeeded');
      },
      error: function(httpResponse) {
		console.log(httpResponse.text);
		status.success('translate failed');
      }
    });
});*/
		