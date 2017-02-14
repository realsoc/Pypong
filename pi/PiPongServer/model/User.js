var db = require("../db.js");
var Model = require("./Model.js");
var schemas = require("./schemas.js");
function User(data){
	var model = new Model();
	var user = {};
	user.__proto__ = Model();
	user.tableName = "users";
	user.schema = schemas.user;
	user.data = {};
	if(data)
		user.data = user.sanitize(data);
	user.clone = function(data){
		return new User(data);
	}
	return user;
}
module.exports = User;
