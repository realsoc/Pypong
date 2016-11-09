
var routes = require('.');
var Player = require("config/player");
var schemas = require('config/models');    
var GameModel = schemas.Game;
var Game = require("config/game");

function sendBack(res,data){
     res.json(data);
}
module.exports = function(app) {  
     // VIEWS
     app.get('/', routes.index);
     app.get('/partials/:name', routes.partials);

     app.get('/api/players',function(req,res){
          Player.getAll(sendBack,res);
     });
     app.post('/api/players',function(req,res){
          Player.addPlayer(req.body.name);
          Player.getAll(sendBack,res);
     });
     app.delete('/api/players/:playerId', function(req,res){
          Player.removeById(playerId);
          Player.getAll(sendBack,res);
     });
     app.get('/api/games',function(req,res){
          Game.getAll(sendBack,res);
     });
     app.post('/api/games',function(req,res){
          var number,player1,player2,type,sPlayer1,sPlayer2;
          if((req.body.sPlayer1 == null || req.body.sPlayer2 == null) && req.body.player1 != null && req.body.player2 !=null && req.body._id !=null){
               var newGame = new GameModel({
               _id            :    req.body._id,
               player1        :    req.body.player1.name,
               player2        :    req.body.player2.name

          });
               Game.updateGame(newGame);
               Game.getUninit(sendBack,res);
               return;
          }else if(req.body.sPlayer1 == null || req.body.sPlayer2 == null){
               res.json("{'error':'Game empty}'");
               return;
          }
          if(req.body.player1 == null || req.body.player1.name == null){
               player1 = "";
          }else{
               player1 = req.body.player1.name;
          }
          if(req.body.player2 == null || req.body.player2.name == null){
               player2 = "";
          }else{
               player2 = req.body.player2.name;
          }
          if(req.body.number == null){
               number = 0;
          }else{
               number = req.body.number;
          }if(req.body.type ==="6"|| req.body.type == 6){
               type = 6;
          }else if(req.body.type == "11"|| req.body.type == 11){
               type = 11;
          }else if(req.body.type == "21"|| req.body.type == 21){
               type = 21;
          }
          var newGame = new GameModel({
               number         :    number,
               player1        :    player1,
               player2        :    player2,
               sPlayer1       :    req.body.sPlayer1,
               sPlayer2       :    req.body.sPlayer2,
               date           :    req.body.date,
               type           :    type

          });
          Game.updateGame(newGame);
          Game.getUninit(sendBack,res);
     });
     app.delete('/api/games/:id', function(req,res){
          var id = req.params.id;
          Game.removeById(id);
          Game.getAll(sendBack,res);
     });

     // VALIDATE 
     app.get('/api/games/uninitGames',function(req,res){
          Game.getUninit(sendBack,res);
     });

     // GENERAL
function processRanking(res,results){
     var rankingTmp = new Array();
     var player1,player2;
     var sPlayer1,sPlayer2;
     var i,j;
     var obj, copy = [], ranking;
     var players = results[1];
     var games = results[0];

     for(i=0;i<players.length;i++){
          rankingTmp[players[i].name] = {"name":players[i].name,"gamesLost":0,"gamesWon":0,"pointsLost":0,"pointsScored":0,"level":0};
     }
     for(i=0;i<games.length;i++){
          player1 = games[i].player1;
          player2 = games[i].player2;
          sPlayer1 = games[i].sPlayer1;
          sPlayer2 = games[i].sPlayer2;
          if(sPlayer2>sPlayer1){
               rankingTmp[player1]["gamesLost"] += 1;
               rankingTmp[player2]["gamesWon"] += 1;
          }else{
               rankingTmp[player2]["gamesLost"] += 1;
               rankingTmp[player1]["gamesWon"] += 1;
          }
          rankingTmp[player1]["pointsLost"] += sPlayer2;
          rankingTmp[player2]["pointsLost"] += sPlayer1;
          rankingTmp[player1]["pointsScored"] += sPlayer1;
          rankingTmp[player2]["pointsScored"] += sPlayer2;
     }
     for(obj in rankingTmp){
          if(rankingTmp[obj]["gamesWon"] == 0 && rankingTmp[obj]["gamesLost"] == 0){
               rankingTmp[obj]["level"] = 0;
          }else{
               rankingTmp[obj]["level"] = rankingTmp[obj]["gamesWon"]/(rankingTmp[obj]["gamesWon"]+rankingTmp[obj]["gamesLost"]);
          }
     }
     i=0;
     for(obj in rankingTmp){
          copy[i] = rankingTmp[obj];
          i++;
     }
     ranking = copy.slice(0);
     ranking.sort(function(a,b) {
          return b.level - a.level;
     });
     sendBack(res,ranking);
}
     app.get('/api/ranking',function(req,res){
          Game.getRanking(processRanking,res);
     });
     app.get('/api/gamesInfos',function(req,res){
          Game.getGamesInfos(sendBack,res);
     });

     // PLAYER
     app.get('/api/duel/:name1/:name2',function(req,res){
          var name1 = req.params.name1;
          var name2 = req.params.name2;
          Game.getByDuel(name1,name2,sendBack,res);
     });
     app.get('/api/games/:name',function(req,res){
          var name = req.params.name;
          Game.getByName(name,sendBack,res);
     })
     app.get('/api/gamesInfosFor/:name',function(req,res){
          console.log("AQUI");
          var name = req.params.name;
          Game.getInfosByName(name,sendBack,res);
     });
     app.get('/api/gamesInfosForDuel/:name1/:name2',function(req,res){
          var name1 = req.params.name1;
          var name2 = req.params.name2;
          Game.getGamesInfosByDuel(name1,name2,sendBack,res);
     });
     app.get('*',routes.index);
};