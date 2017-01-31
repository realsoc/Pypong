var db = require("../db.js");
var _ = require("lodash");
var utils = require("../utils/DataUtils.js");
function Model(data){
	this.tableName = null;
	this.data = data || {};
	this.schema = null;
	function initWithTimestamp(){
		this.data.timestamp = utils.getActualTimestamp();
	}
	function initWithDelayedTimestamp(){
		this.data.timestamp = utils.getDelayedTimestamp();
	}
	function has(name){
		return this.data != undefined && this.data[name] != undefined;
	}
	function get(name){
		return this.data[name];
	}
	function set(name, value){
		this.data[name] = value;
	}
	function sanitize(data){
		if(this.schema){
			data = data || {};
			return _.pick(_.defaults(data, this.schema), _.keys(this.schema)); 
		}
	}
	function getSanitizedClone(){
		return this.clone(this.sanitize(this.data));
	}
	function clone(data){
		var obj = new Model(data);
		return obj;
	}
	function toJson(){
		return JSON.stringify(this.sanitize(this.data));
	}
	function fromJson (jsonString){
		var obj = JSON.parse(jsonString);
		return this.fromOBJ(obj);
	}
	function fromOBJ(obj){
		this.data = this.sanitize(obj);
		return this;
	}
	function createObjectArrayFromOBJ(rows){
		var objects = [];
		rows.forEach(function(row){
			objects.push(clone(row));
		});
		return objects;
	} 
	return {
		data:data,
		sanitize:sanitize,
		getSanitizedClone : getSanitizedClone,
		get: get,
		has:has,
		set: set,
		clone:clone,
		createObjectArrayFromOBJ: createObjectArrayFromOBJ,
		toJson:toJson,
		fromJson: fromJson,
		fromOBJ: fromOBJ
	}
}

module.exports = Model;