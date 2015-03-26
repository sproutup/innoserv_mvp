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
    'ngTagsInput',
    'ngAnimate'
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

sproutupApp.config(['$routeProvider', '$stateProvider', '$locationProvider', '$urlRouterProvider',
    function($routeProvider, $stateProvider, $locationProvider, $urlRouterProvider ) {
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
            .state('search' ,{
                url: '/search',
                templateUrl: 'views/search'
            })
            .state('creator' ,{
                url: '/creator',
                templateUrl: 'views/creator'
            })
            .state('profile' ,{
                url: '/profile',
                abstract: true,
                templateUrl: 'views/profile'
            })
            .state('profile.photos' ,{
                url: '',
                templateUrl: 'views/profile/photos'
            })
            .state('profile.videos' ,{
                url: '/videos',
                templateUrl: 'views/profile/videos'
            })
            .state('profile.products' ,{
                url: '/products',
                templateUrl: 'views/profile/products'
            })
            .state('profile.followers' ,{
                url: '/followers',
                templateUrl: 'views/profile/followers'
            })
            .state('profile.followings' ,{
                url: '/followings',
                templateUrl: 'views/profile/followings'
            })
            .state('profile.account' ,{
                url: '/account',
                templateUrl: 'views/profile/account'
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


        // FIX for trailing slashes. Gracefully "borrowed" from https://github.com/angular-ui/ui-router/issues/50
        $urlRouterProvider.rule(function ($injector, $location) {
            var path = $location.url();

            if (path.indexOf('/_=_') == 0) {
                $location.replace().path($location.path().replace('/_=_', ''));
            }

            if (path.indexOf('/#_=_') == 0) {
                $location.replace().path($location.path().replace('/#_=_', ''));
            }

            // check to see if the path has a trailing slash
            if ('/' === path[path.length - 1]) {
                return path.replace(/\/$/, '');
            }

            if (path.indexOf('/?') > 0) {
                return path.replace('/?', '?');
            }

            return false;
        });
  }]);
