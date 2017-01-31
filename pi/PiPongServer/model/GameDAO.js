var schemas = require("./schemas.js");  
var Game = require("./Game.js");
var DAO = require("./DAO.js");
var db = require("../db.js");
var Utils = require("../utils/DataUtils.js");
var async = require("async");

function GameDAO(data) {
	var gameDAO = {};
	var utils = new Utils();
	gameDAO.__proto__ = DAO();
	gameDAO.tableName = "games";
	gameDAO.schema = schemas.game;
	gameDAO.model = new Game();
	gameDAO.insert = function (obj,callback) {
		nObj = obj.getSanitizedClone();
		var req = "";
		if(nObj.has("type") && nObj.has("player1") && nObj.has("player2") && nObj.has("sPlayer1") && nObj.has("sPlayer2") ){
			if(nObj.has("id") && nObj.get("id")){
				req = "INSERT OR REPLACE into "+this.tableName+" (id,type,player1,player2,sPlayer1,sPlayer2,date,timestamp) VALUES "+
				"((SELECT id FROM "+this.tableName+" WHERE id = "+nObj.get("id")+"), ?,?,?,?,?,?,?);"
			}else{
				req = "INSERT into "+this.tableName+" (name) VALUES "+
				"(?,?,?,?,?,?,?)";
				if(!nObj.has("date"))
					var date = new Date();
				nObj.set("date",date.getFullYear()+"-"+date.getMonth()+"-"+date.getDate());
			}
			db.run(req,nObj.get("type"),nObj.get("player1"),nObj.get("player2"),nObj.get("sPlayer1"),nObj.get("sPlayer2"),nObj.get("date"),utils.getActualTimestamp(),function(err,res){
				if(err){
					callback(err);
				}else{
					callback(null,res);
				}
			});
		}else{
			callback("Missing parameters");
		}
	}
	gameDAO.bulkInsert = function(datas,callbackTheFst){
		var queryBase = "INSERT into games (type,player1,player2,sPlayer1,sPlayer2,date,timestamp) VALUES (?,?,?,?,?,?,?)";
		async.filter(datas,function(row,callback){
			if(row.type && row.player1 && row.player2 && row.sPlayer1 && row.sPlayer2 ){
				if(!row.date){
					var date = new Date();
					row.date = date.getFullYear()+"-"+date.getMonth()+"-"+date.getDate();
				}
				db.run(queryBase,row.type,row.player1,row.player2,row.sPlayer1,row.sPlayer2,row.date,utils.getDelayedTimestamp(60),function(err){
					callback(null,err);
				});
			}else{
				callback(null,true);
			}
		},function(err, result){
			callbackTheFst(result);
		}
		);
	}
	return gameDAO;
}
module.exports = GameDAO;