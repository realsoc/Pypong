'use strict';

// Declare app level module which depends on filters, and services

angular.module('PiPong', [
  'ngRoute',

  'PiPong.controllers',
  'PiPong.filters',
  'PiPong.services',
  'PiPong.directives'
]).
config(function ($routeProvider, $locationProvider) {
  $routeProvider.
    when('/validateView', {
       templateUrl: 'partials/validate'
       //,       controller: 'MyCtrl1'
     }).
    when('/generalView', {
      templateUrl: 'partials/general'
      //, controller: 'MyCtrl2'
    }).
    when('/playerView', {
      templateUrl: 'partials/playerView'
      //, controller: 'MyCtrl2'
    }).
    otherwise({
      redirectTo: '/validateView'
    });

  $locationProvider.html5Mode(true);
});
