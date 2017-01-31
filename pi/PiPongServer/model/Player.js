var db = require("../db.js");
var Model = require("./Model.js");
var schemas = require("./schemas.js");
function Player(data){
	var model = new Model();
	var player = {};
	player.__proto__ = Model();
	player.tableName = "players";
	player.schema = schemas.player;
	player.data = {};
	if(data)
		player.data = player.sanitize(data);
	player.clone = function(data){
		return new Player(data);
	}
	return player;
}
module.exports = Player;
