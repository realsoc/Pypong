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
angular.module('PiPong.controllers', []).
  controller('ValidateCtrl', function ($scope, $http) {
    $scope.playerFormData = {};
    $scope.gameFormData = {};
    $http.get('/api/games/uninitGames')
      .success(function(data){
        $scope.games = data;
      })
      .error(function(data){
        console.log('Error: '+data);
      });
    $http.get('/api/players')
      .success(function(data){
        $scope.players = data;
      })
      .error(function(data){
        console.log('Error: '+data);
      });
      $scope.validateGames = function(){
        var player1,player2;
        var players = $scope.players;
        $(".gameToValidate").each(function(){
          if(!isNaN($(this).find(".player1Name").val()) && !isNaN($(this).find(".player2Name").val())){
            player1 = $(this).find(".player1Name").val();
            player2 = $(this).find(".player2Name").val();
            $http.post('/api/games',{_id:$(this).attr('id'),player1:players[parseInt(player1)],player2:players[parseInt(player2)]}).
              success(function(data){
                $scope.playerFormData = {};
                $scope.games = data;
              })
              .error(function(data){
                console.log('Error: '+data);
              });
            /*console.log($(this).attr('id'));*/
            }
          });
        }
      $scope.createPlayer = function(index){
        if(nameAlreadyExists($scope.players,$scope.playerFormData.name)){
          alert("The name already exists");
        }else{
        $("#addPlayerModal").modal('hide');
        $http.post('/api/players', $scope.playerFormData)
          .success(function(data){
            $scope.playerFormData = {};
            $scope.players = data;
            console.log(data);
          })
          .error(function(data){
            console.log('Error: '+data);
          }).then(function(){
            $http.get('/api/players')
              .success(function(data){
                $scope.players = data;
              })
              .error(function(data){
                console.log('Error: '+data);
              });
          });
        }
      }
      $scope.createGame = function(index){
        $("#addGameModal").modal('hide');
        $http.post('/api/games', $scope.gameFormData)
          .success(function(data){
            $scope.gameFormData = {};
            $scope.games = data;
          })
          .error(function(data){
            console.log('Error: '+data);
          }).then(function(){
            $http.get('/api/games')
              .success(function(data){
                $scope.games = data;
              })
              .error(function(data){
                console.log('Error: '+data);
              });
            });
      };
      $scope.deleteGame = function(id,index){
        $http.delete('/api/games/'+id)
          .success(function(data){
            $scope.games = data;
          })
          .error(function(data){
            console.log('Error: '+data);
          }).then(function(){
            $scope.games.splice(index, 1);
          });
      };
  }).
  controller('GeneralCtrl', function ($scope,$http) {
    $http.get('/api/ranking')
      .success(function(data){
        $scope.ranking = data;
      })
      .error(function(data){
        console.log('Error: '+data);
      });
    $http.get('/api/gamesInfos')
      .success(function(data){
        $scope.infos = data;
      })
      .error(function(data){
        console.log('Error: '+data);
      });
  }).
  controller('PlayerCtrl', function ($scope,$http) {
    $http.get('/api/players')
      .success(function(data){
        $scope.players = data;
      })
      .error(function(data){
        console.log('Error: '+data);
      });
      $('#player1Name').unbind('change');
      $("#player1Name").on('change', function(e) {
        var playerIndex = parseInt($(this).val());
        $http.get('/api/games/'+$scope.players[playerIndex].name)
          .success(function(data){
            console.log(data);
            $scope.games = data;
          })
          .error(function(data){
            console.log('Error: '+data);
          });
        $http.get('/api/gamesInfosFor/'+$scope.players[playerIndex].name)
          .success(function(data){
            console.log(data);
            $scope.infos = data;
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
        $http.get('/api/duel/'+$scope.players[playerIndex].name+'/'+$scope.players[player2Index].name)
          .success(function(data){
            console.log(data);
            $scope.games = data;
          })
          .error(function(data){
            console.log('Error: '+data);
          });
        $http.get('/api/gamesInfosForDuel/'+$scope.players[playerIndex].name+'/'+$scope.players[player2Index].name)
          .success(function(data){
            console.log(data);
            $scope.infos = data;
          })
          .error(function(data){
            console.log('Error: '+data);
          });
  });
});