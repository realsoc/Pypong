'use strict';

/* Controllers */
function nameAlreadyExists(players,name){
  var ret = false;
  var player,i;
  for(i=0;i<players.length;i++){
    if(players[i].name === name){
      ret = true;
      break;
    }
  }
  return ret;
}
function initValidate($http,$scope){
  $scope.data = {};
  $scope.playerFormData = {};
  $scope.gameFormData = {};
  $http.get('/api/games/uninitGames')
  .success(function(data){
    $scope.data.games = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  $http.get('/api/players')
  .success(function(data){
    $scope.data.players = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
}
function setWatchUpdateGamesuninit($http,$scope){
  $(".updateGamesUninit").unbind('click').on('click', function(e) {
    setTimeout(function(){
      $http.get('/api/games/uninitGames')
      .success(function(data){
        $scope.data.games = data;
      })
      .error(function(data){
        console.log('Error: '+data);
      });
      $http.get('/api/players')
      .success(function(data){
        $scope.data.players = data;
      })
      .error(function(data){
        console.log('Error: '+data);
      });

    },100);
  });
}
function validateGamesRoutine($http,$scope){
  $scope.validateGames = function(){
    var player1,player2;
    var players = $scope.data.players;
    $(".gameToValidate").each(function(){
      console.log("1 : "+isNaN($(this).find(".player1Name").val())+" 2 : "+isNaN($(this).find(".player2Name").val()));
      if(!isNaN($(this).find(".player1Name").val()) && !isNaN($(this).find(".player2Name").val())){
        player1 = $(this).find(".player1Name").val();
        player2 = $(this).find(".player2Name").val();
        console.log("Player1 : "+player1+"- player2 : "+player2);
        $http.post('/api/games',{_id:$(this).attr('id'),player1:players[parseInt(player1)],player2:players[parseInt(player2)]}).
        success(function(data){
          $scope.playerFormData = {};
          $scope.data.games = data;
        })
        .error(function(data){
          console.log('Error: '+data);
        });
      }
    });
  }
   $scope.createPlayer = function(index){
    if(nameAlreadyExists($scope.data.players,$scope.playerFormData.name)){
      alert("The name already exists");
    }else{
      $("#addPlayerModal").modal('hide');
      $http.post('/api/players', $scope.playerFormData)
      .success(function(data){
        $scope.playerFormData = {};
        $scope.data.players = data;
  
      })
      .error(function(data){
        console.log('Error: '+data);
      });
    }
  }
  $scope.createGame = function(index){
  if($scope.gameFormData.sPlayer1 ==null||$scope.gameFormData.sPlayer2 == null){
    alert("Scores can't be left empty");
  }else{
     $("#addGameModal").modal('hide');
  $http.post('/api/games', $scope.gameFormData)
  .success(function(data){
    $scope.gameFormData = {};
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  }
 
};
$scope.deleteGame = function(id,index){
  $http.delete('/api/games/'+id)
  .success(function(data){
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  $http.get('/api/games/uninitGames')
  .success(function(data){
    $scope.data.games = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
}

$scope.flushSelects = function(){
  $('select').prop('selectedIndex', 0);
}
}

function initGeneral($http,$scope){
  $scope.data = {};
  $http.get('/api/ranking')
  .success(function(data){
    $scope.data.ranking = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  $http.get('/api/gamesInfos')
  .success(function(data){
    $scope.data.infos = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
}
function initPlayer($http,$scope){
  $scope.data = {};
  $http.get('/api/players')
  .success(function(data){
    $scope.data.players = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
}
function playersSelectWatch($scope,$http){
 $('#player1Name').unbind('change');
 $("#player1Name").on('change', function(e) {
  console.log($scope.data.players);
  $("#player2Name").prop('selectedIndex', 0);
  var playerIndex = parseInt($(this).val());
  $http.get('/api/games/'+$scope.data.players[playerIndex].name)
  .success(function(data){
    $scope.data.games = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  $http.get('/api/gamesInfosFor/'+$scope.data.players[playerIndex].name)
  .success(function(data){
    $scope.data.infos = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  e.stopPropagation();
  $('#player2Name option').prop('selected', function() {
    return playerIndex;
  });
});
 $('#player2Name').unbind('change');
 $("#player2Name").on('change', function(e) {
  var player2Index = parseInt($(this).val());
  var playerIndex = parseInt($("#player1Name").val());
  console.log(player2Index);
  if(!isNaN(player2Index) && !isNaN(playerIndex)){
    $http.get('/api/duel/'+$scope.data.players[playerIndex].name+'/'+$scope.data.players[player2Index].name)
    .success(function(data){

      $scope.data.games = data;
    })
    .error(function(data){
      console.log('Error: '+data);
    });
    $http.get('/api/gamesInfosForDuel/'+$scope.data.players[playerIndex].name+'/'+$scope.data.players[player2Index].name)
    .success(function(data){

      $scope.data.infos = data;
    })
    .error(function(data){
      console.log('Error: '+data);
    });
  }else if(isNaN(player2Index) && !isNaN(playerIndex)){
    $http.get('/api/games/'+$scope.data.players[playerIndex].name)
  .success(function(data){
    $scope.data.games = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  $http.get('/api/gamesInfosFor/'+$scope.data.players[playerIndex].name)
  .success(function(data){
    $scope.data.infos = data;
  })
  .error(function(data){
    console.log('Error: '+data);
  });
  e.stopPropagation();
  $('#player2Name option').prop('selected', function() {
    return playerIndex;
  });
  }
});
}

angular.module('PiPong.controllers', []).
controller('ValidateCtrl', function ($scope, $http) {
  initValidate($http,$scope);
  setWatchUpdateGamesuninit($http,$scope);
  validateGamesRoutine($http,$scope);
}).
controller('GeneralCtrl', function ($scope,$http) {
  initGeneral($http,$scope);
}).
controller('PlayerCtrl', function ($scope,$http) {
  initPlayer($http,$scope);
  playersSelectWatch($scope,$http);


});