'use strict';

/* Directives */
angular.module('PiPong.directives', []).
  directive('appVersion', function (version) {
    return function(scope, elm, attrs) {
      elm.text(version);
    };
  });
