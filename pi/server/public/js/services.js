'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('PiPong.services', []).
  /*factory('Communication', ['$rootScope',function($rootScope){
    return {
      gamesUpdate : function(subsciption){
        
      }
    }
  }]).*/
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
  		},
      getUninit : function(){
        return $http.get('/api/games/uninitGames');
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
