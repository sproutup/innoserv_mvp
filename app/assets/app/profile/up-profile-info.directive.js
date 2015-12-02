angular
    .module('sproutupApp')
    .directive('upProfileInfo', upProfileInfo);

function upProfileInfo() {
    var directive = {
        restrict: 'E',
        templateUrl: '/assets/app/profile/up-profile-info.html',
        scope: {
            user: "="
        },
        link: linkFunc,
        controller: UpProfileInfoController,
        controllerAs: 'vm',
        bindToController: true
    };

    return directive;

    function linkFunc(scope, element, attr, ctrl) {
    }
}

UpProfileInfoController.$inject = ['AuthService'];

function UpProfileInfoController(AuthService) {
    var vm = this;
    vm.network = AuthService.getNetwork().query({
        userId: vm.user.id
    });
}
