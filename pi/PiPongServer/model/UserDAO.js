var schemas = require("./schemas.js");  
var User = require("./User.js");
var DAO = require("./DAO.js");
var db = require("../db.js");
var async = require("async");
var Utils = require("../utils/DataUtils.js");

function UserDAO(data) {
	var userDAO = {};
	userDAO.__proto__ = new DAO();
	userDAO.tableName = "users";
	userDAO.schema = schemas.user;
	userDAO.model = new User();
	userDAO.insert = function (obj,callback) {
		nObj = obj.getSanitizedClone();
		var req = "";
		if(nObj.has("hash")){
			if(nObj.has("id") && nObj.get("id")){
				req = "INSERT OR REPLACE into "+this.tableName+" (id,hash) values "+
				"((SELECT id FROM "+this.tableName+" WHERE id = "+nObj.get("id")+"), '"+nObj.get("hash")+");"
			}else{
				req = "INSERT into "+this.tableName+" (hash) VALUES "+
				"('"+nObj.get("hash")+"')";
			}
		}
		db.run(req,function(err,res){
			if(err){
				console.log(err);
				callback(err);
			}else{
				callback(null,res);
			}
		});
	}
	userDAO.getUniqueId = function(callback){
		var utils = new Utils();
		this.getAll(function(err,rows){
			var id = utils.getId();
			var ret = {};
			if(rows){
				while(utils.contains(rows,"hash",id)){
					id = utils.getId();
				}
			}
			ret.hash = id;
			callback(err,ret);
		});
	}
	userDAO.exists= function(hash,callback){
		var query = "SELECT * FROM "+ this.tableName+" WHERE hash = ?";
		db.get(query,hash,function(err, rows){
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
	return userDAO;
}
module.exports = UserDAO;