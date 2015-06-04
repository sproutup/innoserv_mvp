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
    'ngAnimate',
    'facebook',
    'angulartics',
    'angulartics.google.analytics',
    'angulartics.mixpanel'
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

sproutupApp.run(['$rootScope', '$state', '$stateParams', '$analytics', '$location',
        function ($rootScope,   $state,   $stateParams, $analytics, $location) {

            // It's very handy to add references to $state and $stateParams to the $rootScope
            // so that you can access them from any scope within your applications.For example,
            // <li ng-class="{ active: $state.includes('contacts.list') }"> will set the <li>
            // to active whenever 'contacts.list' or one of its decendents is active.
            $rootScope.$state = $state;
            $rootScope.$stateParams = $stateParams;

            $rootScope.$on('$stateChangeSuccess',
                function(event, toState, toParams, fromState, fromParams){
                    // do something
                    $analytics.pageTrack($location.path());
                })
        }
    ]
);

sproutupApp.config([
        'FacebookProvider',
        function(FacebookProvider) {

            // read the appid from meta tags. meta tag is set with value from application.conf
            var myAppId = $('meta[property="fb:app_id"]').attr("content");

            // You can set appId with setApp method
            // FacebookProvider.setAppId('myAppId');

            /**
             * After setting appId you need to initialize the module.
             * You can pass the appId on the init method as a shortcut too.
             */
            FacebookProvider.init(myAppId);

        }
    ]
);

sproutupApp.config(['$routeProvider', '$stateProvider', '$locationProvider', '$urlRouterProvider',
    function($routeProvider, $stateProvider, $locationProvider, $urlRouterProvider ) {
        $locationProvider.html5Mode(true);
        var access = routingConfig.accessLevels;

        // Public routes
        $stateProvider
            .state('home' ,{
                url: '/',
                templateUrl: 'views/home',
                data: {
                    title: 'SproutUp - Together, we help products grow'
                }
            })
            .state('about' ,{
                url: '/about',
                templateUrl: 'views/about',
                data: {
                    title: 'About Us - SproutUp'
                }
            })
            .state('terms' ,{
                url: '/terms',
                templateUrl: 'views/terms',
                data: {
                    title: 'Terms of Service - SproutUp'
                }
            })
            .state('privacy' ,{
                url: '/privacy',
                templateUrl: 'views/privacy',
                data: {
                    title: 'Privacy Policy - SproutUp'
                }
            })
            .state('news' ,{
                url: '/news',
                templateUrl: 'views/news',
                data: {
                    title: 'News and Press Releases - SproutUp'
                }
            })
            .state('search' ,{
                url: '/search',
                templateUrl: 'views/search',
                data: {
                    title: 'Search'
                }
            })
            .state('community' ,{
                url: '/community',
                templateUrl: 'views/community',
                data: {
                    title: 'Community - SproutUp'
                }
            })
            .state('creator' ,{
                url: '/creator',
                templateUrl: 'views/creator',
                data: {
                    title: 'Creator - SproutUp'
                }
            })
            .state('settings' ,{
                url: '/settings',
                abstract: true,
                templateUrl: 'views/settings',
                data: {
                    title: 'Settings'
                }
            })
            .state('settings.profile' ,{
                url: '/profile',
                templateUrl: 'views/settings/profile',
                controller: 'settingsProfileCtrl',
                data: {
                    title: 'Profile'
                }
            })
            .state('settings.social' ,{
                url: '/social',
                templateUrl: 'views/settings/social',
                controller: 'settingsSocialCtrl',
                data: {
                    title: 'Social Profile'
                }
            })
            .state('settings.trials' ,{
                url: '/trials',
                templateUrl: 'views/settings/trials',
                data: {
                    title: 'Trials'
                }
            })
            .state('dashboard' ,{
                url: '/dashboard',
                abstract: false,
                templateUrl: 'views/dashboard',
                data: {
                    title: 'Dashboard'
                }
            })
            .state('dashboard.products' ,{
                url: '/products/:slug',
                abstract: true,
                controller: 'productDashboardCtrl',
                templateUrl: 'views/dashboard/products',
                data: {
                    title: ''
                }
            })
            .state('dashboard.products.info' ,{
                url: '/info',
                templateUrl: 'views/dashboard/products/info',
                data: {
                    title: ''
                }
            })
            .state('dashboard.products.analytics' ,{
                url: '/analytics',
                templateUrl: 'views/dashboard/products/analytics',
                data: {
                    title: ''
                }
            })
            .state('dashboard.products.users' ,{
                url: '/users',
                templateUrl: 'views/dashboard/products/users',
                data: {
                    title: ''
                }
            })
            .state('dashboard.products.feedback' ,{
                url: '/feedback',
                templateUrl: 'views/dashboard/products/feedback',
                data: {
                    title: ''
                }
            })
            .state('wizard' ,{
                url: '/wizard',
                abstract: true,
                templateUrl: 'views/wizard',
                controller: 'signupWizardCtrl',
                data: {
                    title: 'Wizard'
                }
            })
            .state('wizard.start' ,{
                url: '',
                template: '<div>Starting wizard...</div>',
                data: {
                    title: 'Twitter account'
                }
            })
            .state('wizard.twitter' ,{
                url: '/twitter',
                templateUrl: 'views/wizard/twitter',
                controller: 'signupWizardTwitterCtrl',
                data: {
                    title: 'Twitter account'
                }
            })
            .state('wizard.email' ,{
                url: '/email',
                templateUrl: 'views/wizard/email',
                controller: 'signupWizardEmailCtrl',
                data: {
                    title: 'Email'
                }
            })
            .state('profile' ,{
                url: '/user/:nickname',
                abstract: true,
                templateUrl: 'views/profile',
                controller: 'userDetailCtrl',
                data: {
                    title: 'User Profile'
                }
            })
            .state('profile.photos' ,{
                url: '',
                templateUrl: 'views/profile/photos',
                data: {
                    title: ''
                }
            })
            .state('profile.videos' ,{
                url: '/videos',
                templateUrl: 'views/profile/videos',
                data: {
                    title: ''
                }
            })
            .state('profile.products' ,{
                url: '/products',
                templateUrl: 'views/profile/products',
                data: {
                    title: ''
                }
            })
            .state('profile.followers' ,{
                url: '/followers',
                templateUrl: 'views/profile/followers',
                data: {
                    title: ''
                }
            })
            .state('profile.followings' ,{
                url: '/followings',
                templateUrl: 'views/profile/followings',
                data: {
                    title: ''
                }
            })
            .state('profile.account' ,{
                url: '/account',
                templateUrl: 'views/profile/account',
                data: {
                    title: ''
                }
            })
            .state('product', {
                abstract: true,
                url: '/product',
                template: '<div ui-view></div>',
                onEnter: function(){
                    console.log("enter product abstract");
                },
                data: {
                    title: ''
                }
            })
            .state('product.list' ,{
                url: '',
                templateUrl: 'views/product-list',
                onEnter: function(){
                    console.log("enter product list");
                },
                data: {
                    title: ''
                }
            })
            .state('product.add' ,{
                url: '/add',
                templateUrl: 'views/product-add',
                onEnter: function(){
                    console.log("enter product add");
                },
                data: {
                    title: 'Add product'
                }
            })
            .state('product.detail', {
                url: '/:slug',
                abstract: true,
                templateUrl: 'views/product-details',
                controller: 'productDetailCtrl',
                onEnter: function(){
                    console.log("enter product detail");
                },
                data: {
                    title: ''
                }
            })
            .state('product.detail.about', {
                url: '',
                templateUrl: 'views/product-about',
                onEnter: function(){
                    console.log("enter product detail about");
                },
                data: {
                    title: 'Product - About'
                }
            })
            .state('product.detail.bar', {
                url: '/bar',
                templateUrl: 'views/product-bar',
                controller: 'ForumCtrl',
                onEnter: function(){
                    console.log("enter product detail bar");
                },
                data: {
                    title: 'Product - Geekout'
                }
            })
            .state('product.detail.bar.question', {
                url: '/question',
                controller: function($scope){
                    $scope.changeCategory(1);
                },
                onEnter: function(){
                    console.log("enter product detail bar question");
                },
                data: {
                    title: 'Product - Geekout'
                }
            })
            .state('product.detail.bar.compliment', {
                url: '/compliment',
                controller: function($scope){
                    $scope.changeCategory(2);
                },
                onEnter: function(){
                    console.log("enter product detail bar compliment");
                },
                data: {
                    title: 'Product - Geekout'
                }
            })
            .state('product.detail.gallery', {
                url: '/gallery',
                templateUrl: 'views/product-gallery',
                onEnter: function(){
                    console.log("enter product detail gallery");
                },
                data: {
                    title: 'Product - Gallery'
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
