'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('PiPong.services', []).
  factory('Games',function($http){
  	return {
  		get : function(){
  			return $http.get('/api/games');
  		},
  		create : function(gameData){
  			return $http.post('/api/games',gameData);
  		},
  		delete : function(id){
  			return $http.delete('/api/games/'+id);
  		}
  	}}).
  	factory('Players',function($http){
  	return {
  		get : function(){
  			return $http.get('/api/players');
  		},
  		create : function(gameData){
  			return $http.post('/api/players',playerData);
  		},
  		delete : function(id){
  			return $http.delete('/api/players/'+id);
  		}
  	}
  });
