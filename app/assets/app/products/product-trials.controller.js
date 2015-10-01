(function() {

angular
    .module('sproutupApp')
    .controller('ProductTrialsController', ProductTrialsController);

ProductTrialsController.$inject = ['TrialService', '$stateParams'];

function ProductTrialsController(TrialService, $stateParams) {
    var vm = this;
    vm.active = [];
    vm.next = [];
    vm.finished = [];

    vm.trials = TrialService.productTrials().query({
        slug: $stateParams.slug
    }, function() {
        vm.next = vm.trials.filter(function (item) {
            return item.status === 1 || item.status === 2;
        });
        vm.active = vm.trials.filter(function (item) {
            return item.status === 3;
        });
        vm.finished = vm.trials.filter(function (item) {
            return item.status > 3;
        });
        vm.trialInit = true;
    });
}

})();