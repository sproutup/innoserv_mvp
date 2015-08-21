(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ContentController', ContentController);

ContentController.$inject = ['$stateParams', '$state', 'FeedService', 'AuthService', '$rootScope'];

function ContentController($stateParams, $state, FeedService, AuthService, $rootScope) {
    var vm = this;
    vm.content = [];

    activate();

    function activate() {
        if(!AuthService.ready()){
            var unbindWatch = $rootScope.$watch(AuthService.loggedIn, function (value) {
                if ( value === true ) {
                  unbindWatch();
                  activate();
                }
            });
        }
        else {
            init();
        }
    }

    function init() {
        vm.user = angular.copy(AuthService.m.user);
    }

    vm.content = FeedService.query();
    console.log(vm.content);
    vm.busy = false;
    var position = 11;

    vm.loadMore = function() {
        vm.busy = true;
        console.log('more');
        var more = [];
        more = FeedService.query({
            start: position
        }, function() {
            for (var a = 0; a < more.length; a++) {
                vm.content.push(more[a]);
                if ((a + 1) === more.length) {
                    vm.busy = false;
                }
            }
            position += 11;
        });
    };

}

})();