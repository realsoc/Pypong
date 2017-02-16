var db = require("../db.js");
var _ = require("lodash");
var Model = require("./Model.js");
var async = require("async");
var DataUtils = require("../utils/DataUtils.js");

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
	function getAllForUser(hash,callbackTheFst){
		var UserDAO = require("./UserDAO.js");
		var userDAO = new UserDAO();
		userDAO.exists(hash,function(err,exists){
			if(!err && exists){
				async.parallel({
					games : function(callback){
						var query = "SELECT * FROM games WHERE user != '"+hash+"'";
						db.all(query,function(error,rows){
							if(error){
								console.log(error.message);
								callback(null);
							}else{
								console.log(rows);
								callback(null,rows);
							}	
						});
					},
					players : function(callback){
						var query = "SELECT * FROM players WHERE user != '"+hash+"'";
						db.all(query,function(error,rows){
							if(error){
								console.log(error.message);
								callback(null);
							}else{
								console.log(rows);
								callback(null,rows);
							}	
						});

					}},
					function(error,results){
						if(!error){
							callbackTheFst(200,results);
						}else{
							callbackTheFst(500,"Server error");
						}
					}
					);
			}
			else if(!exists){
				callbackTheFst(404,"User not found");
			}else{
				callbackTheFst(500, "Internal server error");
			}
		})
	}
	function getAllForUserBetween(hash,timeStart,timeEnd,callbackTheFst){
		var UserDAO = require("./UserDAO.js");
		var userDAO = new UserDAO();
		userDAO.exists(hash,function(err,exists){
			//console.log(err+" : "+exists);
			if(!err && exists){
				async.parallel({
					games : function(callback){
						var query = "SELECT * FROM games WHERE  timestamp >= "+timeStart+" AND timestamp <= "+timeEnd;		
						db.all(query,function(error,rows){
							if(error){
								callback(null);
							}else{
								callback(null,rows);
							}	
						});
					},
					players : function(callback){
						var query = "SELECT * FROM players WHERE  timestamp >= "+timeStart+" AND timestamp <= "+timeEnd;
						db.all(query,function(error,rows){
							if(error){
								callback(null);
							}else{
								callback(null,rows);
							}	
						});

					}},
					function(error,results){
						if(!error){
							callbackTheFst(200,results);
						}else
						callbackTheFst(500,"Server erroror");
					}
					);
			}
			else if(!exists){
				callbackTheFst(404,"User not found");
			}else{
				callbackTheFst(500, "Internal server error");
			}
		})

	}
	function getInterval(bInf,bSup,callback){
		var query = "SELECT * FROM "+this.tableName+" WHERE timestamp >= "+bInf+" and timestamp <= "+bSup;
		db.all(query,function(error,rows){
			if(error){
				callback(error);
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
	function dropFromDB(criter,val,callback){
		var query = "DELETE FROM "+ this.tableName +" WHERE "+criter+" = "+val;
		db.run(query,function(err){
			callback(err);
		})
	}
	function dropGeral(callback){
		var query1 = ""
	}
	return {
		getInterval:getInterval,
		findById: findById,
		getAll: getAll,
		dropAll:dropAll,
		dropId:dropId,
		dropFromDB: dropFromDB,
		getAllForUser: getAllForUser,
		getAllForUserBetween: getAllForUserBetween,
		dropGeral : dropGeral
	}
}

module.exports = DAO;