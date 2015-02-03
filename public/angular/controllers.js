'use strict';

/* Controllers */

/*
sproutupApp.controller('productTabCtrl', function($scope) {
  $scope.phones = [
    {'name': 'Nexus S',
     'snippet': 'Fast just got faster with Nexus S.'},
    {'name': 'Motorola XOOM™ with Wi-Fi',
     'snippet': 'The Next, Next Generation tablet.'},
    {'name': 'MOTOROLA XOOM™',
     'snippet': 'The Next, Next Generation tablet.'}
  ];
});

function My1Ctrl() {}
My1Ctrl.$inject = [];

function My2Ctrl() {}
My2Ctrl.$inject = [];

function My3Ctrl() {}
My3Ctrl.$inject = [];
*/




// products
var productControllers = angular.module('productControllers', []);

productControllers.controller('productListCtrl', ['$scope', 'ProductService',
  function($scope, ProductService) {
    $scope.products = ProductService.query();
 //   $scope.orderProp = 'age';
  }]);

productControllers.controller('productDetailCtrl', ['$scope', '$routeParams', 'ProductService',
  function($scope, $routeParams, ProductService) {
    $scope.product = ProductService.get({slug: $routeParams.slug}, function(product) {
//    $scope.product = ProductService.get({slug: 1}, function(product) {

//      $scope.mainImageUrl = phone.images[0];
    });

//    $scope.setImage = function(imageUrl) {
//      $scope.mainImageUrl = imageUrl;
//    }
  }]);

