(function () {
    'use strict';

angular
    .module('sproutupApp')
    .controller('ContentController', ContentController);

ContentController.$inject = ['$stateParams', '$state', 'FeedService'];

function ContentController($stateParams, $state, FeedService) {
    var vm = this;
    vm.content = [];

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
//                    console.log(vm.content);
                }
            }
            position += 11;
        });
    };

}

})();