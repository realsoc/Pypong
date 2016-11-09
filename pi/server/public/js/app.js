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
       ,       controller: 'ValidateCtrl'
     }).
    when('/generalView', {
      templateUrl: 'partials/general'
      , controller: 'GeneralCtrl'
    }).
    when('/playerView', {
      templateUrl: 'partials/player'
      , controller: 'PlayerCtrl'
    }).
    otherwise({
      redirectTo: '/validateView'
    });

  $locationProvider.html5Mode(true);
});
