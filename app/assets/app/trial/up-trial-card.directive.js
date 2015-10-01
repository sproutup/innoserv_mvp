angular
    .module('sproutupApp')
    .directive('upTrialCard', upTrialCard);

function upTrialCard() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/trial/up-trial-card.html',
        scope: {
            trial: '=',
        },
        link: linkFunc,
        controller: upTrialCardController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {

    }
}

upTrialCardController.$inject = ['$state'];

function upTrialCardController($state) {
    var vm = this;
    vm.state = $state.current.name;

    function isRecievedEvent(item) {
        return item.status === 3;
    }

    // If this is an active trial, calculate how many days are left and such
    if (vm.trial.status === 3) {
        var recievedEvent = vm.trial.log.filter(isRecievedEvent);
        if (recievedEvent[0]) {
            vm.trial.recievedEvent = true;
            var start = moment(recievedEvent[0].createdAt);
            var end = moment(vm.trial.trialEndsAt);
            var duration = end.diff(start, 'days');
            vm.trial.daysLeft = end.diff(moment(), 'days');
            vm.trial.percentageLeft = (vm.trial.daysLeft / duration) * 100;

            // Check if this trial is overdue 
            if (vm.trial.daysLeft < 0) {
                vm.trial.overdue = true;
                vm.trial.percentageLeft = 100;
                vm.trial.daysLeft = Math.abs(vm.trial.daysLeft);
            }
        }
    }
}