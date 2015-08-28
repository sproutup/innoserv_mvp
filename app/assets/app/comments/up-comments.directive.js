angular
    .module('sproutupApp')
    .directive('upComments', upComments);

function upComments() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/comments/up-comments.html',
        scope: {
            comments: '=',
            id: '=upId',
            type: '@upType',
            commenting: '='
        },
        link: linkFunc,
        controller: upCommentsController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {
        


    }
}


upCommentsController.$inject = ['CommentService', 'AuthService', '$timeout', '$scope', '$rootScope', '$resource'];

function upCommentsController(CommentService, authService, $timeout, $scope, $rootScope, $resource) {
    var vm = this;

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
        else {
            init();
        }
    }

    function init() {
        vm.user = angular.copy(authService.m.user);
    }

    $scope.vm.addComment = function() {
        if (!vm.newComment) {
            vm.commentWarning = true;
        } else {
            var Comment = CommentService.comment(vm.type, vm.id);
            var newComment = new Comment();
            newComment.body = vm.newComment;
            newComment.$save(function(res) {
                vm.comments.data.push(res);
                vm.newComment = '';
                vm.commenting = false;
                vm.commentWarning = false;
            }, function(err) {
                vm.commentWarning = false;
                console.log(err);
            });
        }
    };
}