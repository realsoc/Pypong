
var routes = require('.');


module.exports = function(app) {  
     app.get('/', routes.index);
     app.get('/partials/:name', routes.partials);
     app.get('*',routes.index);


/**
 * Start Server
 */

// server.listen(app.get('port'), function () {
//   console.log('Express server listening on port ' + app.get('port'));
// });

     // app.get('/', function(req, res) {       
     //      res.end("Node-Android-Project");    
     // });     
     // app.post('/login',function(req,res){        
     //      var email = req.body.email;             
     //           var password = req.body.password;       
     //      login.login(email,password,function (found) {           
     //           console.log(found);             
     //           res.json(found);    
     // });    
     // });     
     // app.post('/register',function(req,res){         
     //      var email = req.body.email;             
     //           var password = req.body.password;       
     //      register.register(email,password,function (found) {             
     //           console.log(found);             
     //           res.json(found);    
     // });     
     // });     
     // app.post('/api/chgpass', function(req, res) {       
     //      var id = req.body.id;                 
     //           var opass = req.body.oldpass;         
     //      var npass = req.body.newpass;       
     //      chgpass.cpass(id,opass,npass,function(found){           
     //           console.log(found);             
     //           res.json(found);    
     // });     
     // });     
     // app.post('/api/resetpass', function(req, res) {         
     //      var email = req.body.email;         
     //      chgpass.respass_init(email,function(found){             
     //           console.log(found);             
     //           res.json(found);    
     // });     
     // });     
     // app.post('/api/resetpass/chg', function(req, res) {         
     //      var email = req.body.email;         
     //      var code = req.body.code;       
     //      var npass = req.body.newpass;       
     // chgpass.respass_chg(email,code,npass,function(found){           
     //      console.log(found);             
     //      res.json(found);    
     // });     
     // }); 
     // app.post('/addData',function(req,res){         
     //      var data = req.body.data;             
     //      var date = req.body.date;
     //      var value = req.body.value;
     //      var origin = req.body.origin;
     //      dataCtrl.addData(data,date,value,origin,function(found) {             
     //           console.log(found);
     //           res.json(found);
     //      });     
     // });  
};
