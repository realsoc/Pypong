
var routes = require('.');
var Player = require("config/player");
var schemas = require('config/models');    
var GameModel = schemas.Game;
var Game = require("config/game");

/*var auth = function(req, res, next) {
  if (req.session && req.session.user === "rdf" && req.session.admin)
    return next();
  else
    return res.sendStatus(401);
};*/

Date.prototype.toDateInputValue = (function() {
    var local = new Date(this);
    local.setMinutes(this.getMinutes() - this.getTimezoneOffset());
    return local.toJSON().slice(0,10);
});
function putInResult(res,data){
     res.result = data;
}
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
          Player.testAndAddPlayer(res,req.body.name);
     });
     app.delete('/api/players/:playerId', function(req,res){
          Player.removeById(playerId);
          sendBack(res,"{'result':'ok'}");
     });
     app.get('/api/games',function(req,res){
          Game.getAll(sendBack,res);
     });
     app.post('/api/games',function(req,res){
          var number,player1,player2,type,sPlayer1,sPlayer2;
          var newGame = new GameModel();
          newGame._id = req.body._id;
          if(req.body._id != null)
               newGame._id = req.body._id;
          if(req.body.player1 != null){
               if(req.body.player1.name)
                    newGame.player1 = req.body.player1.name
               else
                    newGame.player1 = req.body.player1;
               //TODO : test player1

          }
          if(req.body.player2 != null){
               if(req.body.player2.name)
                    newGame.player2 = req.body.player2.name
               else
                    newGame.player2 = req.body.player2;
               //TODO : test player 2
          }
          if(req.body.sPlayer2 != null){
                    newGame.sPlayer2 = req.body.sPlayer2;
                    console.log("ScoreP2 : "+newGame.sPlayer2);
               }
          if(req.body.sPlayer1 != null){
                    newGame.sPlayer1 = req.body.sPlayer1;
                    console.log("ScoreP1 : "+newGame.sPlayer1);
               }
          if(req.body.number != null)
               newGame.number = req.body.number;
          if(req.body.date != null){
               newGame.date = req.body.date;
          }else{
               newGame.date = new Date().toDateInputValue();
          }
          if(req.body.type != null){
               if(req.body.type ==="6"|| req.body.type == 6){
                    newGame.type = 6;
               }else if(req.body.type === "11"|| req.body.type == 11){
                    newGame.type = 11;
               }else if(req.body.type === "21"|| req.body.type == 21){
                    newGame.type = 21;
               }
          }
          Game.updateGame(newGame);
          res.sendStatus(200);
     });
     app.delete('/api/games/:id', function(req,res){
          var id = req.params.id;
          Game.removeById(id);
          res.sendStatus(200);
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
               rankingTmp[obj]["level"] = 100*rankingTmp[obj]["gamesWon"]/(rankingTmp[obj]["gamesWon"]+rankingTmp[obj]["gamesLost"]);
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
     app.get('/api/test', function(req,res){
          console.log("HELLO");
     });
     app.get('/api/ranking',function(req,res){
          Game.getRanking(processRanking,res);
     });
     app.get('/api/gamesInfos',function(req,res){
          Game.getGamesInfos(sendBack,res);
     });
     app.get('/api/getAll', function(req,res){
          Game.getAllInfos(sendBack,res);
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
          var name = req.params.name;
          Game.getInfosByName(name,sendBack,res);
     });
     app.get('/api/gamesInfosForDuel/:name1/:name2',function(req,res){
          var name1 = req.params.name1;
          var name2 = req.params.name2;
          Game.getGamesInfosByDuel(name1,name2,sendBack,res);
     });
/*     app.get('/login',function(req,res){
          if(!req.query.username || !req.query.password){
               res.send('login failed');
          }else if(req.query.username === "rdf" || req.query.password === "vidaloka69"){
               req.session.user = "rdf";
               req.session.admin = true;
               res.send("login success");
          }
     })
     app.get('/logout', function(req,res){
          req.session.destroy();
          res.send("logout success");
     })
     app.get('/test',auth,function(req,res){
          res.send("LOGIN");
     })*/
     app.get('*',routes.index);
};