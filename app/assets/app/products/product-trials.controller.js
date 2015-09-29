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
    function isRecievedEvent(item) {
        return item.status === 3;
    }
    console.log(moment);
    vm.trials = TrialService.productTrials().query({
        slug: $stateParams.slug
    }, function() {
        for (t = 0; t < vm.trials.length; t++) {
            if (vm.trials[t].status === 1 || vm.trials[t].status === 2) {
                vm.next.push(vm.trials[t]);
            } else if (vm.trials[t].status === 3) {
                var recievedEvent = vm.trials[t].log.filter(isRecievedEvent);
                var start = moment(recievedEvent[0].createdAt);
                var end = moment(vm.trials[t].trialEndsAt);
                vm.trials[t].daysLeft = end.diff(moment(), 'days');
                var duration = end.diff(start, 'days');
                vm.trials[t].percentageLeft = (vm.trials[t].daysLeft / duration) * 100;
				vm.active.push(vm.trials[t]);
			} else if (vm.trials[t].status > 3) {
				vm.finished.push(vm.trials[t]);
			}
        }
        console.log(vm.next);
        console.log(vm.active);
        console.log(vm.finished);
    });
}

})();