var db = require("../db.js");
var _ = require("lodash");
var Model = require("./Model.js");
var async = require("async");

function DAO(){

	this.tableName = null;
	this.schema = null;
	this.model = new Model();

	function getAll(callback){
		var query = "SELECT * FROM "+this.tableName;
		db.all(query,function(err,rows){
			if(err){
				callback(err);
			}else{
				callback(null,rows);
			}	
		});
	}
	function getInterval(bInf,bSup,callback){
		var query = "SELECT * FROM "+this.tableName+" WHERE timestamp >= "+bInf+" and timestamp <= "+bSup;
		db.all(query,function(err,rows){
			if(err){
				callback(err);
			}else{
				callback(null,rows);
			}	
		});
	}
	function findById(id,callback){
		var query = "SELECT * FROM "+ this.tableName+" WHERE id = "+id;
		db.get(query,function(err, data){
			if(err){
				callback(err);
			}else{
				callback(null,data);
			}
		});
	}
	function dropAll(callback){
		var query = "DELETE FROM "+this.tableName;
		db.run(query,function(err){
			if(err){
				callback();
			}else{
				callback();
			}
		});
	}
	function dropId(id,callback){
		var query = "DELETE FROM " + this.tableName+" WHERE id = "+id;
		db.run(query,function(err){
			callback(err);
		});
	}
	return {
		getInterval:getInterval,
		findById: findById,
		getAll: getAll,
		dropAll:dropAll,
		dropId:dropId
	}
}

module.exports = DAO;