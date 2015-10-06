(function() {

angular
    .module('sproutupApp')
    .controller('userTrialsController', userTrialsController);

userTrialsController.$inject = ['TrialService', '$stateParams'];

function userTrialsController(TrialService, $stateParams) {
    var vm = this;
    var allTrials = TrialService.userTrials().query({
        nickname: $stateParams.nickname
    }, function() {
        vm.trials = allTrials.filter(function(item) {
            return item.status > -1;
        });
        vm.trialsInit = true;
    });
}

})();