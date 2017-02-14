var schemas = require("./schemas.js");  
var Player = require("./Player.js");
var DAO = require("./DAO.js");
var db = require("../db.js");
var async = require("async");
var UserDAO = require("./UserDAO.js");
var Utils = require("../utils/DataUtils.js");
function PlayerDAO(data) {
	var playerDAO = {};
	var utils = new Utils();
	playerDAO.__proto__ = DAO();
	playerDAO.tableName = "players";
	playerDAO.schema = schemas.player;
	playerDAO.model = new Player();
	playerDAO.insert = function (obj,callback) {
		var req = "";
		if(nObj.has("name")){
			if(nObj.has("id") && nObj.get("id")){
				req = "INSERT OR REPLACE into "+this.tableName+" (id,name,timestamp,user) values "+
				"((SELECT id FROM "+this.tableName+" WHERE id = "+nObj.get("id")+"), '"+nObj.get("name")+"',"+utils.getActualTimestamp()+", '"+nObj.get("user")+"');"
			}else{
				req = "INSERT into "+this.tableName+" (name,timestamp,user) VALUES "+
				"('"+nObj.get("name")+"',"+utils.getActualTimestamp()+",'"+get("hash")+"')";
			}
		}
		db.run(req,function(err,res){
			if(err){
				callback(err);
			}else{
				callback(null,res);
			}
		});
	}
	playerDAO.existsPlayer = function(hash,name,callback){
		var queryExists = "SELECT * FROM players WHERE name = ? AND user = ?";
		db.get(queryExists,name,hash,function(err,results){
			if(err){
				callback(err);
			}else{
				if(rows)
					callback(null,true);
				else
					callback(null,false);
			}
		});
	}
	playerDAO.bulkInsert = function(hash,datas,callbackTheFst){
		var userDAO = new UserDAO();
		//console.log("BulkInsert");
		//console.log(datas);
		userDAO.exists(hash,function(err,exists){
			//console.log(err+" : "+exists);
			if(!err && exists){
				console.log("exists bulkInsert");
				async.filter(datas,function(row,callback){
					var queryExists = "SELECT * FROM players WHERE name = ? AND user = ?";
					var user = row.user|| hash;
					console.log("rowus "+row.user," hash "+hash+" user "+user);
					db.get(queryExists,row.name,user,function(err,results){
						console.log("err");
						console.log(err);
						console.log("res");
						console.log(results);
						if(err){
							callback(err);
						}else{
							if(results)
								callback(null,false);
							else{
								var queryBase = "INSERT into players (name,timestamp,user) VALUES (?,?,?)";
								db.run(queryBase,row.name,utils.getDelayedTimestamp(60),row.user,function(erro){
									callback(null,erro);
								});
							}
						}
					});				
				},function(err, result){
					var code = 201;
					if(result.length >0){
						code = 206;
					}
					console.log("Error");
					console.log(err);
					console.log("Result");
					console.log(result);
					callbackTheFst(code,result);
				});
			}
			else if(!exists){
				console.log("Not exists");
				callbackTheFst(404,{});
			}else{
				callbackTheFst(500, {});
			}
		})
	}
	return playerDAO;
}
module.exports = PlayerDAO;