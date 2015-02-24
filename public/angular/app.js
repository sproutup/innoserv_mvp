'use strict';

var sproutupApp = angular.module('sproutupApp', [
    'ngRoute',
    'ngCookies',
    'ui.router',
    'productControllers',
    'AuthControllers',
    'productFilters',
    'productServices',
    'ngTagsInput'
]);

sproutupApp.config(['$routeProvider', '$locationProvider',
  function($routeProvider,$locationProvider) {
    $locationProvider.html5Mode(true);
    var access = routingConfig.accessLevels;

    $routeProvider.
      when('/product', {
        templateUrl: 'views/product-list',
        controller: 'productListCtrl'
      }).
      when('/product/:slug', {
        templateUrl: 'views/product-details',
        controller: 'productDetailCtrl'
      }).
      otherwise({
        redirectTo: '/product'
      });
  }]);
