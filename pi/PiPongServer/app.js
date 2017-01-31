var express    = require("express");
var bodyParser = require("body-parser");
var http       = require("http");
var Controller = require("./controller.js");
var db = require("./db.js");
require("./utils/DataUtils.js");
var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.set('port', process.env.PORT || 3000);

app.get('/drop/players/:id', Controller.dropSinglePlayer.bind(Controller));
app.get('/drop/players', Controller.dropPlayers.bind(Controller));
app.get('/players', Controller.getPlayers.bind(Controller));
app.get('/players/:id', Controller.getSinglePlayer.bind(Controller));
app.get('/players/:bInf/:bSup', Controller.getPlayersInInterval.bind(Controller));
app.post('/players', Controller.postPlayers.bind(Controller));

app.get('/drop/games/:id', Controller.dropSingleGame.bind(Controller));
app.get('/drop/games', Controller.dropGames.bind(Controller));
app.get('/games', Controller.getGames.bind(Controller));
app.get('/games/:id', Controller.getSingleGame.bind(Controller));
app.get('/games/:bInf/:bSup', Controller.getGamesInInterval.bind(Controller));
app.post('/games', Controller.postGames.bind(Controller));


http.createServer(app).listen(app.get('port'), function(){
	console.log('Express listens on port ' + app.get('port'));
});