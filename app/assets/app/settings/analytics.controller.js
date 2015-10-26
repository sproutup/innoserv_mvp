(function () {
    'use strict';

    angular
        .module('sproutupApp')
        .controller('AnalyticsController', AnalyticsController);

    AnalyticsController.$inject = ['$rootScope', '$state', '$log', 'AuthService','GoogleApiService', 'AnalyticsService', '$filter', 'OAuthService', '$window'];

    function AnalyticsController($rootScope, $state, $log, authService, googleApiService, analyticsService, $filter, oauth, $window) {
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
            ga: {connected: false, error: true, message: 'tada...'},
            yt: {connected: false, error: true, message: 'Connection error: please reauthorize or <a>disconnect</a>'},
            tw: {connected: false, error: false, message: 'tada...'}
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
            vm.user = angular.copy(authService.m.user);
            //requestGoogleApiToken();

            oauth.getNetwork(vm.user.id).then(function(data){
                data.forEach(function(item){
                    console.log('[analytics] provider:', item.provider);
                    switch(item.provider){
                    case 'ga':
                        vm.network.ga.connected = true;
                        break;
                    case 'yt':
                        vm.network.yt.connected = true;
                        break;
                    case 'tw':
                        vm.network.tw.connected = (item.status === 1);
                        vm.network.tw.message = item.token;
                        break;
                    }
                });
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
                vm.network.data = data;
            });

//            // right now we can have only one, but its prepared for more than one account
//            if(authService.m.user.analytics!==undefined){
//                vm.googleAnalyticsAPI = authService.m.user.analytics[0].googleAnalyticsAPI;
//                vm.youtubeAnalyticsAPI = authService.m.user.analytics[0].youtubeAnalyticsAPI;
//            }
        }

        function connect(provider){
            console.log('connect: ', provider);

            oauth.getAuthorizeUrl(provider, vm.user.id).then(function(data){
                authService.setRedirect('user.settings.social','');
                console.log('connect:', data);
                $window.location.href = data.url;
            });
        }

        function disconnect(provider){
            console.log('disconnect: ', provider);
            oauth.deleteNetwork(provider, vm.user.id).then(function(data){
                console.log('disconnect:', data);
                switch(provider){
                case 'ga':
                    vm.network.ga.connected = false;
                    break;
                case 'yt':
                    vm.network.yt.connected = false;
                    break;
                case 'tw':
                    vm.network.tw.connected = false;
                    break;
                }
            });
        }

        function reauthorize(provider){
            console.log('reauthorize: ', provider);
            switch(provider){
            case 'ga':
                vm.network.ga.error = false;
                break;
            case 'yt':
                vm.network.yt.error = false;
                break;
            case 'tw':
                vm.network.tw.error = false;
                break;
            }
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
