function DataUtils(data){

	function getActualTimestamp(){
		return Date.now();
	}
	function getDelayedTimestamp(sec){
		return Date.now()+sec*1000;
	}
	function getId()
	{
		var text = "";
		var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		for(var i=0; i < 8; i++ )
			text += possible.charAt(Math.floor(Math.random() * possible.length));
		return text;
	}
	function contains(jsonArray,key,val){
		var i;
		for(i=0;i<jsonArray.length;i++){
			if(jsonArray[i][key] === val){
				return true;
			}
			return false;
		}
	}
	return {
		getDelayedTimestamp:getDelayedTimestamp,
		getActualTimestamp:getActualTimestamp,
		getId:getId,
		contains:contains
	}
}
module.exports = DataUtils;
