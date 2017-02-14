var db = require("./db.js");
var Player = require("./model/Player.js");
var Game = require("./model/Game.js");
var Model = require("./model/Model.js");
var User = require("./model/User.js");
var PlayerDAO = require("./model/PlayerDAO.js");
var GameDAO = require("./model/GameDAO.js");
var UserDAO = require("./model/UserDAO.js");
var DAO = require("./model/DAO.js");
var DataUtils = require("./utils/DataUtils.js");
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
        console.log("PostPlayers");
        var playerDAO = new PlayerDAO();
        console.log(req.body);
        if(!req.body.hash || (req.body.hash && req.body.hash.length != 8 || !req.body.data)){
            //console.log("TARAce");
            res.status(400).send({});
        }else{
            //console.log("From : "+req.body.hash+" data : ");
            //console.log(req.body.data);
            playerDAO.bulkInsert(req.body.hash,req.body.data,function(code,toSend){
                res.status(code).send(toSend);
            });
        }
        
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
        console.log("PostGames");
        var gameDAO = new GameDAO();
        //console.log(req.body);
        if(!req.body.hash || (req.body.hash && req.body.hash.length != 8 ||!req.body.data)){
            res.status(400).send({});
        }else{

            //console.log("From : "+req.body.hash+" data : ");
            //console.log(req.body.data);
            gameDAO.bulkInsert(req.body.hash,req.body.data,function(code,toSend){
                res.status(code).send(toSend);
            });
        }
    },dropUsers: function(req, res) {
        var userDAO = new UserDAO();
        userDAO.dropAll(function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err);
        });
    },
    getId: function(req, res) {
        var userDAO = new UserDAO();
        console.log("GetId");
        userDAO.getUniqueId(function(err,result){
            if(!err){
                console.log(result);
                res.status(200).send(result);
            }
            else{
                console.log("Error");
                console.log(err);
                res.status(500).send(err);           
            }
        });
    },
    confirmSubscribe: function(req, res) {
        console.log("confirmSubscribe");
        var user = new User(req.body);
        var userDAO = new UserDAO();
        var ret = {};
        userDAO.insert(user,function(err){
            if(!err){
                //console.log("ok");
                res.status(201).send({});
            }
            else{
                //console.log("Error 500 confirm subscribe "+err.message);
                res.status(500).send({});
            }
        });
    },
    unsubscribe: function(req,res){
        var userDAO = new UserDAO();
        var ret = {};
        ret.result = "Wrong authentification";
        if(req.body.hash){
            var hash = "'"+req.body.hash+"'";
            userDAO.dropFromDB("hash",hash,function(err){
                if(err){
                    ret.result = "Error dropping user";
                    res.status(418).send(ret);
                }
                else{
                    ret.result = "ok";
                    res.status(201).send(ret);
                    /*
                    var gameDAO = new GameDAO();
                    gameDAO.dropFromDB("user",hash,function(err){
                        if(err){
                            ret.result = "Error dropping games";
                            res.status(418).send(ret);
                        }else{
                            ret.result = "ok";
                            res.status(201).send(ret);
                        }
                    })*/
                    
                }

            });
        }else{
            res.status(400).send({"result":"Bad request"});
        }
    },
    getUsers: function(req,res){
        var userDAO = new UserDAO();
        userDAO.getAll(function(err,results){
            if(!err)
                res.json(results);
            else
                res.json(err.message);
        });
    },
    getAll : function(req,res){
        var dao = new DAO();
        console.log(req.body);
        if(req.body.hash)
            dao.getAllForUser(req.body.hash,function(code,toSend){
                res.status(code).send(toSend);
            })
        else{
            res.status(400).send("Bad request");
        }
    },
    getAllFrom : function(req,res){
        var dao = new DAO();
        console.log("GetAllFROM");
        console.log(req.body);
        var dataUtils = new DataUtils();
        var timeEnd = dataUtils.getActualTimestamp();
        //console.log(req.body);
        if(req.body.hash && req.body.timeStart>=0){
            //console.log("from : "+req.body.hash+" ts : "+req.body.timeStart);
            dao.getAllForUserBetween(req.body.hash,req.body.timeStart,timeEnd,function(code,toSend){
                toSend.timestamp = timeEnd+1;
                res.status(code).send(toSend);
            })
        }else{
            res.status(400).send({});
        }
    },
    getAllBetween : function(req,res){
        var dao = new DAO();
        if(req.body.hash&& req.body.timeStart && req.body.timeEnd)
            dao.getAllForUserBetween(req.body.hash,req.body.timeStart,req.body.timeEnd,function(code,toSend){
                res.status(code).send(toSend);
            })
        else
            res.status(400).send({});
    },
    test : function(req,res){
        var gameDAO = new GameDAO();
        gameDAO.test();
    },
    exists : function(req,res){
        var userDAO = new UserDAO();
        console.log("exists");
        if(req.body.hash){
            userDAO.exists(req.body.hash,function(err,exist){
                if(exist)
                    res.status(200).send({});
                else
                    res.status(404).send({});
            });
        }
    },
    dropAll : function(req,res){
        var dao = new DAO();
        dao.dropGeral(function(err){
            if(!err)
                res.status(200).send({});
            else
                res.status(500).send({});
        })
    }
}