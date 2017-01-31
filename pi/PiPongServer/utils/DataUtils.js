function DataUtils(data){

	function getActualTimestamp(){
		return Date.now();
	}
	function getDelayedTimestamp(sec){
		return Date.now()+sec*1000;
	}
	return {
		getDelayedTimestamp:getDelayedTimestamp,
		getActualTimestamp:getActualTimestamp
	}
}
module.exports = DataUtils;
