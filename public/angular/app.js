'use strict';

var sproutupApp = angular.module('sproutupApp', [
    'wu.masonry',
    'ngRoute',
    'ngCookies',
    'ui.router',
    'angularFileUpload',
    'productControllers',
    'AuthControllers',
    'FileControllers',
    'productFilters',
    'productServices',
    'ngTagsInput'
]);

sproutupApp.config(function ($provide) {
    $provide.decorator('$uiViewScroll', function ($delegate) {
        return function (uiViewElement) {
            // var top = uiViewElement.getBoundingClientRect().top;
            window.scrollTo(0, 0);
            // Or some other custom behaviour...
        };
    });
});

sproutupApp.config(['$routeProvider', '$stateProvider', '$locationProvider',
    function($routeProvider, $stateProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
        var access = routingConfig.accessLevels;

        // Public routes
        $stateProvider
            .state('home' ,{
                url: '/',
                templateUrl: 'views/home'
            })
            .state('about' ,{
                url: '/about',
                templateUrl: 'views/about'
            })
            .state('creator' ,{
                url: '/creator',
                templateUrl: 'views/creator'
            })
            .state('profile' ,{
                url: '/profile',
                templateUrl: 'views/profile'
            })
            .state('product', {
                abstract: true,
                url: '/product',
                template: '<div ui-view></div>',
                onEnter: function(){
                    console.log("enter product abstract");
                }

            })
            .state('product.list' ,{
                url: '',
                templateUrl: 'views/product-list',
                controller: 'productListCtrl',
                onEnter: function(){
                    console.log("enter product list");
                }
            })
            .state('product.detail', {
                url: '/:slug',
                abstract: true,
                templateUrl: 'views/product-details',
                controller: 'productDetailCtrl',
                onEnter: function(){
                    console.log("enter product detail");
                }
            })
            .state('product.detail.about', {
                url: '',
                templateUrl: 'views/product-about',
                onEnter: function(){
                    console.log("enter product detail about");
                }
            })
            .state('product.detail.bar', {
                url: '/bar',
                templateUrl: 'views/product-bar',
                controller: 'ForumCtrl',
                onEnter: function(){
                    console.log("enter product detail bar");
                }
            })
            .state('product.detail.bar.suggestion', {
                url: '/suggestion',
                controller: function($scope){
                    $scope.changeCategory(1);
                },
                onEnter: function(){
                    console.log("enter product detail bar suggestion");
                }
            })
            .state('product.detail.bar.question', {
                url: '/question',
                controller: function($scope){
                    $scope.changeCategory(2);
                },
                onEnter: function(){
                    console.log("enter product detail bar question");
                }
            })
            .state('product.detail.gallery', {
                url: '/gallery',
                templateUrl: 'views/product-gallery',
                onEnter: function(){
                    console.log("enter product detail gallery");
                }
            })
        ;



        //$routeProvider.
        //when('/', {
        //    templateUrl: 'views/index',
        //    controller: 'productListCtrl'
        //}).
        //when('/product', {
        //    templateUrl: 'views/product-list',
        //    controller: 'productListCtrl'
        //}).
        //when('/product/:slug', {
        //templateUrl: 'views/product-details',
        //controller: 'productDetailCtrl'
        //}).
        //otherwise({
        //redirectTo: '/product'
        //});
  }]);
