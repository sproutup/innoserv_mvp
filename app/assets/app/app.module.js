//'use strict';

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
    'angulartics.mixpanel',
    'angulartics.scroll',
    'ngAutocomplete',
    'ui.bootstrap',
    'ngSanitize',
    'infinite-scroll',
    'angularSpinner',
    'chart.js'
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

sproutupApp.config(function ($analyticsProvider) {
  // turn off automatic tracking
  $analyticsProvider.virtualPageviews(false);
  $analyticsProvider.firstPageview(true);
  $analyticsProvider.withBase(true);
  console.log("## Virtual page views are turned off");
});

sproutupApp.run(['$rootScope', '$state', '$stateParams',
        function ($rootScope,   $state,   $stateParams) {

            // It's very handy to add references to $state and $stateParams to the $rootScope
            // so that you can access them from any scope within your applications.For example,
            // <li ng-class="{ active: $state.includes('contacts.list') }"> will set the <li>
            // to active whenever 'contacts.list' or one of its decendents is active.
            $rootScope.$state = $state;
            $rootScope.$stateParams = $stateParams;

            // $rootScope.$on('$stateChangeSuccess',
            //     function(event, toState, toParams, fromState, fromParams){
            //         // do something
            //         $analytics.pageTrack($location.path());
            //     })
        }
    ]
);

sproutupApp.config([
        'FacebookProvider',
        function(FacebookProvider) {

            // read the appid from meta tags. meta tag is set with value from application.conf
            var myAppId = $('meta[property="fb:app_id"]').attr("b");

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

sproutupApp.config(['$routeProvider', '$stateProvider', '$locationProvider', '$urlRouterProvider', '$sceDelegateProvider',
    function($routeProvider, $stateProvider, $locationProvider, $urlRouterProvider, $sceDelegateProvider) {
        $locationProvider.html5Mode(true);
        var access = routingConfig.accessLevels;

        // Public routes
        $stateProvider
            .state('user' ,{
                url: '',
                abstract: true,
                views: {
                    '': {template: '<div ui-view autoscroll="true"></div>'},
                    'navbar': {
                        templateUrl: 'assets/app/layout/navbar.html',
                        controller: 'AuthCtrl',
                        controllerAs: 'auth'
                    },
                    'footer': {
                        templateUrl: 'assets/app/layout/footer.html'
                    }
                }
            })
            .state('user.home' ,{
                url: '/',
                templateUrl: 'assets/app/home/index.html',
                data: {
                    title: 'SproutUp - Together, we help products grow'
                }
            })
            .state('user.404' ,{
                url: '/404',
                templateUrl: 'assets/app/404/404.html',
                data: {
                    title: 'SproutUp - Oops'
                }
            })
            .state('user.login', {
                url: '/login',
                templateUrl: 'assets/app/user/login.html',
                controller: 'UserLoginController',
                controllerAs: 'vm',
                title: 'login - user',
                data: {
                    title: 'Login - User'
                }
            })
            .state('user.signup', {
                url: '/signup',
                templateUrl: 'assets/app/user/signup.html',
                controller: 'UserSignupController',
                controllerAs: 'vm',
                title: 'signup - user',
                data: {
                    title: 'Signup - User'
                }
            })
            .state('user.about' ,{
                url: '/about',
                templateUrl: 'views/about',
                data: {
                    title: 'About Us - SproutUp'
                }
            })
            .state('user.howitworks' ,{
                url: '/how-it-works',
                templateUrl: 'views/how-it-works',
                data: {
                    title: 'How Product Trial Works - SproutUp'
                }
            })
            .state('user.faq' ,{
                url: '/FAQs',
                templateUrl: 'views/FAQs',
                data: {
                    title: 'FAQs for Influencers and Enthusiasts - SproutUp'
                }
            })
            // .state('user.news' ,{
            //     url: '/news',
            //     templateUrl: 'views/news',
            //     data: {
            //         title: 'News and Press Releases - SproutUp'
            //     }
            // })
            .state('user.search' ,{
                url: '/products',
                templateUrl: 'views/search',
                //templateUrl: 'assets/app/search/search.html',
                //controller: 'SearchController',
                //controllerAs: 'vm',
                data: {
                    title: 'Products - Click and Try'
                }
            })
            .state('user.comingsoon' ,{
                url: '/more-products',
                templateUrl: 'assets/app/products/comingsoon.html',
                data: {
                    title: 'Products - Explore and Buy'
                }
            })
            .state('user.twitterauth' ,{
                url: '/authenticate/twitter',
                templateUrl: 'assets/app/user/login.html',
            })
            .state('user.facebookauth' ,{
                url: '/authenticate/facebook',
                templateUrl: 'assets/app/user/login.html',
            })
            .state('user.creator' ,{
                url: '/creator',
                templateUrl: 'views/creator',
                data: {
                    title: 'Creator - SproutUp'
                }
            })
            .state('user.subscribe' ,{
                url: '/subscribe',
                templateUrl: 'assets/app/creator/subscribe.html',
                data: {
                    title: 'Creator Subscription - SproutUp'
                }
            })
            .state('user.perk' ,{
                url: '/points',
                templateUrl: 'views/perk',
                data: {
                    title: 'Rewards - SproutUp'
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
                controller: 'ProductDashboardController',
                controllerAs: 'vm',
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
            .state('user.wizard' ,{
                url: '/wizard',
                abstract: false,
                templateUrl: 'assets/app/wizard/wizard.html',
                controller: 'SignupWizardController',
                controllerAs: 'vm',
                data: {
                    title: 'Wizard'
                }
            })
            .state('user.wizard.twitter' ,{
                url: '/social',
                templateUrl: 'assets/app/wizard/social.html',
                data: {
                    title: 'Twitter account'
                }
            })
            .state('user.wizard.email' ,{
                url: '/email',
                templateUrl: 'assets/app/wizard/email.html',
                data: {
                    title: 'Email'
                }
            })
            .state('user.wizard.socialConnection' ,{
                url: '/social-connection',
                templateUrl: 'assets/app/wizard/social.html',
                data: {
                    title: 'Social media connection'
                }
            })
            // .state('profile' ,{
            //     url: '/user/:nickname',
            //     abstract: true,
            //     templateUrl: 'views/profile',
            //     controller: 'userDetailCtrl',
            //     data: {
            //         title: 'User Profile'
            //     }
            // })
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
            .state('user.product', {
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
            .state('user.product.list' ,{
                url: '',
                templateUrl: 'views/product-list',
                onEnter: function(){
                    console.log("enter product list");
                },
                data: {
                    title: ''
                }
            })
            .state('user.product.add' ,{
                url: '/add',
                templateUrl: 'views/product-add',
                onEnter: function(){
                    console.log("enter product add");
                },
                data: {
                    title: 'Add product'
                }
            })
            .state('user.trial', {
                url: '/trial/:slug',
                template: '<div ui-view autoscroll="true"></div>',
                abstract: true,
                controller: 'TrialController',
                controllerAs: 'vm'
            })
            .state('user.trial.social', {
                url: '/social',
                templateUrl: 'assets/app/trial/social.html',
                data: {
                    title: 'Social media connection'
                }
            })
            .state('user.trial.request' ,{
                url: '',
                templateUrl: 'assets/app/trial/request.html',
//                controller: 'TrialController',
//                controllerAs: 'vm',
                data: {
                    title: 'Product Trial'
                }
            })
            .state('user.trial.confirmation' ,{
                url: '/confirmation',
                templateUrl: 'assets/app/trial/confirmation.html',
//                controller: 'TrialController',
//                controllerAs: 'vm',
                data: {
                    title: 'Product Trial'
                }
            })
            .state('user.mytrials', {
                url: '/mytrials',
                templateUrl: 'assets/app/mytrials/menu.html',
                abstract: true,
                controller: 'MyTrialController',
                controllerAs: 'vm'
            })
            .state('user.mytrials.current', {
                url: '',
                templateUrl: 'assets/app/mytrials/current.html',
                data: {
                    title: 'My Current Product Trials'
                }
            })
            .state('user.mytrials.past', {
                url: '/past',
                templateUrl: 'assets/app/mytrials/past.html',
                data: {
                    title: 'My Past Product Trials'
                }
            })
            .state('user.mytrials.cancelled', {
                url: '/cancelled',
                templateUrl: 'assets/app/mytrials/cancelled.html',
                data: {
                    title: 'My Cancelled Product Trials'
                }
            })
        ;


        // FIX for trailing slashes. Gracefully "borrowed" from https://github.com/angular-ui/ui-router/issues/50
        $urlRouterProvider.rule(function ($injector, $location) {
            var path = $location.url();

            if (path.indexOf('/_=_') === 0) {
                $location.replace().path($location.path().replace('/_=_', ''));
            }

            if (path.indexOf('/#_=_') === 0) {
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

        $urlRouterProvider.otherwise(function ($injector, $location) {
            var $state = $injector.get('$state');
            $state.go('user.404');
        });

        $sceDelegateProvider.resourceUrlWhitelist([
            // Allow same origin resource loads.
            'self',
            // Allow loading from our assets domain.  Notice the difference between * and **.
            'https://www.youtube.com/**'
        ]);
  }]);

sproutupApp.config(['usSpinnerConfigProvider', function (usSpinnerConfigProvider) {
    usSpinnerConfigProvider.setDefaults({
        lines: 8, // The number of lines to draw
        length: 16, // The length of each line
        width: 23, // The line thickness
        radius: 42, // The radius of the inner circle
        scale: 0.13, // Scales overall size of the spinner
        corners: 1, // Corner roundness (0..1)
        color: 'white', // #rgb or #rrggbb or array of colors
    });
}]);