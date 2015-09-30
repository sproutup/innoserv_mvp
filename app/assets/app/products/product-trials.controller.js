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
        vm.trialInit = true;
        // Divide trials into active, next, and finished
        for (t = 0; t < vm.trials.length; t++) {
            if (vm.trials[t].status === 1 || vm.trials[t].status === 2) {
                vm.next.push(vm.trials[t]);
            } else if (vm.trials[t].status === 3) {
				vm.active.push(vm.trials[t]);
			} else if (vm.trials[t].status > 3) {
				vm.finished.push(vm.trials[t]);
			}
        }
        // console.log(vm.active);
        // console.log(vm.next);
        // console.log(vm.finished);
    });
}

})();