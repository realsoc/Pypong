var schemas = require("./schemas.js");  
var Player = require("./Player.js");
var DAO = require("./DAO.js");
var db = require("../db.js");
var async = require("async");
var Utils = require("../utils/DataUtils.js");
function PlayerDAO(data) {
	var playerDAO = {};
	var utils = new Utils();
	playerDAO.__proto__ = DAO();
	playerDAO.tableName = "players";
	playerDAO.schema = schemas.player;
	playerDAO.model = new Player();
	playerDAO.insert = function (obj,callback) {
		nObj = obj.getSanitizedClone();
		var req = "";
		if(nObj.has("name")){
			if(nObj.has("id") && nObj.get("id")){
				req = "INSERT OR REPLACE into "+this.tableName+" (id,name,timestamp) values "+
				"((SELECT id FROM "+this.tableName+" WHERE id = "+nObj.get("id")+"), '"+nObj.get("name")+"',"+utils.getActualTimestamp()+");"
			}else{
				req = "INSERT into "+this.tableName+" (name,timestamp) VALUES "+
				"('"+nObj.get("name")+"',"+utils.getActualTimestamp()+")";
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
	playerDAO.bulkInsert = function(datas,callbackTheFst){
		var queryBase = "INSERT into players (name,timestamp) VALUES (?,?)";
		async.filter(datas,function(row,callback){
			db.run(queryBase,row.name,utils.getDelayedTimestamp(60),function(err){
				callback(null,err);
			});
		},function(err, result){
			callbackTheFst(result);
		}
		);
	}
	return playerDAO;
}
module.exports = PlayerDAO;