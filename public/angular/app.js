'use strict';

var productApp = angular.module('productApp', [
  'ngRoute',
  'productControllers',
  'productFilters',
  'productServices',
  'ngTagsInput'
]);

//productApp.run(function($location, $rootElement) {
//  $rootElement.off('click');
//});

productApp.config(['$routeProvider', '$locationProvider',
  function($routeProvider,$locationProvider) {
    $locationProvider.html5Mode(true);

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
