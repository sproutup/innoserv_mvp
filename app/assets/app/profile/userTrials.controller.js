(function() {

angular
    .module('sproutupApp')
    .controller('userTrialsController', userTrialsController);

userTrialsController.$inject = ['TrialService', '$stateParams'];

function userTrialsController(TrialService, $stateParams) {
    var vm = this;
    vm.trials = TrialService.userTrials().query({
        nickname: $stateParams.nickname
    }, function() {
        vm.trialsInit = true;
        console.log(vm.trials);
    });
}

})();