var schemas = require("./schemas.js");  
var Game = require("./Game.js");
var DAO = require("./DAO.js");
var db = require("../db.js");
var UserDAO = require("./UserDAO.js");
var Utils = require("../utils/DataUtils.js");
var async = require("async");

function GameDAO(data){
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
				req = "INSERT OR REPLACE into "+this.tableName+" (id,type,player1_name,player2_name,player1_score,player2_score,date,timestamp,user) VALUES "+
				"((SELECT id FROM "+this.tableName+" WHERE id = "+nObj.get("id")+"), ?,?,?,?,?,?,?,?);"
			}else{
				req = "INSERT into "+this.tableName+" (type,player1_name,player2_name,player1_score,player2_score,date,timestamp,user) VALUES "+
				"(?,?,?,?,?,?,?,?)";
				if(!nObj.has("date")){
					var date = new Date();
					nObj.set("date",date);
				}
			}
			db.run(req,nObj.get("type"),nObj.get("player1_name"),nObj.get("player2_name"),nObj.get("player1_score"),nObj.get("player2_score"),nObj.get("date"),nObj.get("user"),utils.getActualTimestamp(),function(err,res){
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
	gameDAO.bulkInsert = function(hash,datas,callbackTheFst){
		console.log("gamebulkinsert");
		var userDAO = new UserDAO();
		userDAO.exists(hash,function(err,exists){
			console.log(err);
			if(!err && exists){
				console.log("gamebulkinsert exists");
				var queryBase = "INSERT into games (type,player1_name,player2_name,player1_score,player2_score,date,timestamp,user) VALUES (?,?,?,?,?,?,?,?)";
				async.filter(datas,function(row,callback){
					if(row.type && row.player1_name && row.player2_name && row.player1_score >=0 && row.player2_score>=0 ){
						if(!row.date){
							row.date = Date.now();
						}
						var user = row.user|| hash;
						console.log("rowus "+row.user," hash "+hash+" user "+user);
						db.run(queryBase,row.type,row.player1_name,row.player2_name,row.player1_score,row.player2_score,row.date,utils.getDelayedTimestamp(60),user,function(err){
							console.log(err);
							callback(null,err);
						});
					}else{
						//console.log("tarace");
						callback(null,true);
					}
				},function(err, result){
					console.log("Error");
					console.log(err);
					console.log("Result");
					console.log(result);
					var code = 201;
					if(result.length >0){
						code = 206;
					}
					callbackTheFst(code,result);
				});
			}else if(!exists){
				console.log("gamebulkinsert does not exist");

				callbackTheFst(404,{});
			}else{
				console.log("gamebulkinsert internal error");

				callbackTheFst(500,{});
			}
		})
	}
	gameDAO.test = function(){
		db.all("PRAGMA table_info(games)",function(err,result){
			if(err)
				console.log(err);
			else
				console.log(result);
		});
	}
	return gameDAO;
}
module.exports = GameDAO;