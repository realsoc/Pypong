var schemas = require("./schemas.js");  
var Model = require("./Model.js");
function Game(data) {

	var game = {};
	game.data = {};
	if(data){
		if(data.id)
			game.data.id = data.id;
		if(data.player1)
			game.data.player1 = data.player1;
		if(data.type)
			game.data.type = data.type;
		if(data.player2)
			game.data.player2 = data.player2;
		if(data.sPlayer2)
			game.data.sPlayer2 = data.sPlayer2;
		if(data.sPlayer1)
			game.data.sPlayer1 = data.sPlayer1;
		if(data.date)
			game.data.date = data.date;
	}
	game.__proto__ = Model();
	game.tableName = "games";
	game.schema = schemas.game;
	game.clone = function(data){
		var obj = new Game(data);
		return obj;
	}
	game.toJson = function(){
		var obj = {};
		if(this.data){
			if(this.data.id)
				obj.id = this.data.id;
			if(this.data.player1)
				obj.player1 = this.data.player1;
			if(this.data.type)
				obj.type = this.data.type;
			if(this.data.player2)
				obj.player2 = this.data.player2;
			if(this.data.sPlayer2)
				obj.sPlayer2 = this.data.sPlayer2;
			if(this.data.sPlayer1)
				obj.sPlayer1 = this.data.sPlayer1;
			if(this.data.date)
				obj.date = this.data.date;
		}
		return JSON.stringify(obj);
	}
	game.fromOBJ = function(obj){
		if(obj.id)
			this.data.id = obj.id;
		if(obj.player1)
			this.data.player1 = obj.player1;
		if(obj.player2)
			this.data.player2 = obj.player2;
		if(obj.sPlayer1)
			this.data.sPlayer1 = obj.sPlayer1;
		if(obj.sPlayer2)
			this.data.sPlayer2 = obj.sPlayer2;
		if(obj.type)
			this.data.type = obj.type;
		if(obj.date)
			this.data.date = obj.date;
		return this;
	}
	game.fromJson = function(jsonString){
		var obj = JSON.parse(jsonString);
		fromOBJ(obj);
		return this;
	}
	
	return game;
}
module.exports = Game;

var db = require("../db.js");
var Model = require("./Model.js");
var schemas = require("./schemas.js");
function Game(data){
	var game = {};
	game.__proto__ = Model();
	game.tableName = "games";
	game.schema = schemas.game;
	game.data = {};
	if(data)
		game.data = game.sanitize(data);
	game.clone = function(data){
		return new Game(data);
	}

	return game;
}
module.exports = Game;