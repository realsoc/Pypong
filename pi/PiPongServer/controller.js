var db = require("./db.js");
var Player = require("./model/Player.js");
var Game = require("./model/Game.js");
var Model = require("./model/Model.js");
var PlayerDAO = require("./model/PlayerDAO.js");
var GameDAO = require("./model/GameDAO.js");
module.exports = {
    dropPlayers: function(req, res) {
        var playerDAO = new PlayerDAO();
        playerDAO.dropAll(function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        });
    },
    dropSinglePlayer: function(req, res) {
        var playerDAO = new PlayerDAO();
        playerDAO.dropId(req.params.id,function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        })
    },
    getPlayers: function(req, res) {
        var playerDAO = new PlayerDAO();
        playerDAO.getAll(function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        });
    },
    getSinglePlayer: function(req,res) {
        var playerDAO = new PlayerDAO();
        playerDAO.findById(req.params.id,function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        })
    },
    getPlayersInInterval: function(req,res){
        var playerDAO = new PlayerDAO();
        playerDAO.getInterval(req.params.bInf,req.params.bSup,function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        })
    },
    postPlayers: function(req, res) {
        var playerDAO = new PlayerDAO();
        playerDAO.bulkInsert(req.body,function(toSend){
            if(toSend.length > 0){
                res.status(418).send(toSend);
            }else{
                res.status(201).send(toSend);
            }
        });
    },
    dropGames: function(req, res) {
        var gameDAO = new GameDAO();
        gameDAO.dropAll(function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        });
    },
    dropSingleGame: function(req, res) {
        var gameDAO = new GameDAO();
        gameDAO.dropId(req.params.id,function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        })
    },
    getGames: function(req, res) {
        var gameDAO = new GameDAO();
        gameDAO.getAll(function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        });
    },
    getSingleGame: function(req,res) {
        var gameDAO = new GameDAO();
        gameDAO.findById(req.params.id,function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        })
    },
    getGamesInInterval: function(req,res){
        var gameDAO = new GameDAO();
        gameDAO.getInterval(req.params.bInf,req.params.bSup,function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        })
    },
    postGames: function(req, res) {
        var gameDAO = new GameDAO();
        gameDAO.bulkInsert(req.body,function(toSend){
            if(toSend.length > 0){
                res.status(418).send(toSend);
            }else{
                res.status(201).send(toSend);
            }
        });
    }
}