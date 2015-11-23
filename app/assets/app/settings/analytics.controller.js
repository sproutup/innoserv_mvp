(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('AnalyticsController', AnalyticsController);

    AnalyticsController.$inject = ['$rootScope', '$state', '$log', 'AuthService','GoogleApiService', 'AnalyticsService', '$filter', 'OAuthService', '$window', '$cookieStore', 'usSpinnerService'];

    function AnalyticsController($rootScope, $state, $log, authService, googleApiService, analyticsService, $filter, oauth, $window, $cookieStore, usSpinnerService) {
        var vm = this;

        vm.user = {};
        vm.analytics = {};
        vm.ga = {};
        vm.yt = {};
        vm.googleAnalyticsAPI = false;
        vm.youtubeAnalyticsAPI = false;
        vm.requestGoogleApiToken = requestGoogleApiToken;
        vm.requestAnalyticsTokenUrl = '';
        vm.requestYoutubeTokenUrl = '';
        vm.revokeAuthorization = revokeAuthorization;
        vm.network = {
            yt: {connected: false, error: false, message: ''},
            ga: {connected: false, error: false, message: ''},
            tw: {connected: false, error: false, message: ''},
            ig: {connected: false, error: false, message: ''},
            fb: {connected: false, error: false, message: ''},
            pi: {connected: false, error: false, message: ''}
        };
        vm.connect = connect;
        vm.disconnect = disconnect;
        vm.reauthorize = reauthorize;

        activate();

        function activate() {
            if(!authService.ready()){
                var unbindWatch = $rootScope.$watch(authService.loggedIn, function (value) {
                    if ( value === true ) {
                      unbindWatch();
                      activate();
                    }
                });
            }
            else{
                init();
            }
        }

        function init() {
            // start spinner in view
            usSpinnerService.spin('spinner-4');

            vm.user = angular.copy(authService.m.user);
            vm.reach = analyticsService.userReach().get({
                userId: vm.user.id
            });

            vm.networks = [
                { provider: 'yt', status: 0, message: '' },
                { provider: 'ga', status: 0, message: '' },
                { provider: 'tw', status: 0, message: '' },
                { provider: 'ig', status: 0, message: '' },
                { provider: 'fb', status: 0, message: '' },
                { provider: 'pi', status: 0, message: '' }
            ];

            oauth.listNetwork(vm.user.id).then(function(data){
                vm.networkData = data;

                // if the user hasn't connected, we add a cookie 'disconnectedUser'
                if (vm.networkData.length < 1) {
                    $cookieStore.put('disconnectedUser', true);
                }

                // set oauth.socialMediaChecked to true in the service
                // we need this to run the log in and trial flows
                oauth.socialMediaChecked = true;
                oauth.networks = data;

                data.forEach(function(item){
                    var match = vm.networks.filter(function(arg, val){
                        return item.provider === vm.networks[val].provider;
                    });
                    if (match[0]) {
                        match[0].status = item.status;
                        match[0].message = item.message;
                        match[0].provider = item.provider;
                    }
                });

                // vm.networkData = [];
                if ($state.current.name === 'user.trial.social' && vm.networkData.length > 1 && !vm.userCookie) {
                    $state.go('user.trial.request', { slug: $state.params.slug });
                }

                // stop spinner in the view
                usSpinnerService.stop('spinner-4');

                // set variable for social network list 
                vm.networkInit = true;

            /*
                vm.analytics = data;
                vm.ga = $filter("filter")(data[0].summaries, {kind:"analytics#accountSummary"});
                console.log("vm.ga: ", vm.ga);
                vm.yt = $filter("filter")(data[0].summaries, {kind:"youtube#channel"});
                authService.m.user.analytics = data;
                console.log("vm.yt: ", vm.yt);
                vm.googleAnalyticsAPI = data[0].googleAnalyticsAPI;
                vm.youtubeAnalyticsAPI = data[0].youtubeAnalyticsAPI;
              */
            });
        }

        function connect(provider){
            oauth.createNetwork(provider, vm.user.id).then(function(data){
                if ($state.params) {
                    authService.setRedirect($state.current.name, $state.params);
                } else {
                    authService.setRedirect($state.current.name, '');
                }
                $window.location.href = data.url;
            });
        }

        function disconnect(provider){
            console.log('disconnect: ', provider);
            oauth.deleteNetwork(provider, vm.user.id).then(function(data){
                console.log('disconnect:', data);
                init();
            });
        }

        function reauthorize(provider){
            console.log('reauthorize: ', provider);
            oauth.createNetwork(provider, vm.user.id).then(function(data){
                if ($state.params) {
                    authService.setRedirect($state.current.name, $state.params);
                } else {
                    authService.setRedirect($state.current.name, '');
                }
                console.log('connect:', data);
                $window.location.href = data.url;
            });
        }

        function requestGoogleApiToken() {
            googleApiService.getAuthorizationParams().then(function(data) {
                vm.requestAnalyticsTokenUrl = data.ga_url;
                vm.requestYoutubeTokenUrl = data.yt_url;
            }, function(error) {
            });
        }

        function revokeAuthorization(){
            googleApiService.revokeAuthorization().then(function(data) {
                authService.m.user.analytics = data;
                vm.googleAnalyticsAPI = authService.m.user.analytics[0].googleAnalyticsAPI;
                vm.youtubeAnalyticsAPI = authService.m.user.analytics[0].youtubeAnalyticsAPI;
                $rootScope.$broadcast('alert:success', {
                    message: 'Authorization revoked'
                });
            }, function(error) {
                $rootScope.$broadcast('alert:error', {
                    message: 'Authorization revoke failed'
                });
            });
        }
    }
})();
