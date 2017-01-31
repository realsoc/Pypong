var fs = require("fs");
var dbFile = "pipong.db";
var exists = fs.existsSync(dbFile);
var sqlite3 = require("sqlite3").verbose();
var db = new sqlite3.Database(dbFile);
//var db = new sqlite3.Database(":memory:");
var player_table_creator = "CREATE TABLE players "+
"(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
"name TEXT NOT NULL, "+
"timestamp INTEGER NOT NULL, unique (name) on conflict fail)";

var game_table_creator = "CREATE TABLE games "+
"(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
"type INTEGER NOT NULL, "+
"player1 TEXT NOT NULL, "+
"player2 TEXT NOT NULL, "+
"sPlayer1 INTEGER NOT NULL, "+
"sPlayer2 INTEGER NOT NULL, "+
"timestamp INTEGER NOT NULL, "+ 
"date TEXT)";


if(!exists){
	db.run(player_table_creator);
	db.run(game_table_creator);
}

module.exports = db;