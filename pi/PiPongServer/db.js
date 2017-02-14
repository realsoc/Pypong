var fs = require("fs");
var dbFile = "pipong.db";
var exists = fs.existsSync(dbFile);
var sqlite3 = require("sqlite3").verbose();
var db = new sqlite3.Database(dbFile);
var dropall = false;
//var db = new sqlite3.Database(":memory:");
var player_table_creator = "CREATE TABLE players "+
"(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
"name TEXT NOT NULL, "+
"timestamp INTEGER NOT NULL, "+
"user TEXT NOT NULL, "+
"unique (name) on conflict fail)";

var user_table_creator = "CREATE TABLE users "+
"(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
"hash TEXT NOT NULL, "+
"unique (hash) on conflict ignore)";

var game_table_creator = "CREATE TABLE games "+
"(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
"type INTEGER NOT NULL, "+
"player1_name TEXT NOT NULL, "+
"player2_name TEXT NOT NULL, "+
"player1_score INTEGER NOT NULL, "+
"player2_score INTEGER NOT NULL, "+
"timestamp INTEGER NOT NULL, "+ 
"date INTEGER NOT NULL, "+
"user TEXT NOT NULL, "+
"unique (date,user) on conflict ignore)";

var game_table_dropper = "DROP TABLE IF EXISTS games";
var user_table_dropper = "DROP TABLE IF EXISTS users";
var player_table_dropper = "DROP TABLE IF EXISTS players";
if(!exists){
	db.run(player_table_creator);
	db.run(game_table_creator);
	db.run(user_table_creator);
}
if(dropall){
	db.run(game_table_dropper);
	db.run(user_table_dropper);
	db.run(player_table_dropper);
}

module.exports = db;