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
            type: '@upType'
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
        var Comment = CommentService.comment('models.content', $scope.vm.id);
        var newComment = new Comment();
        newComment.body = $scope.vm.newComment;
        newComment.$save(function(res) {
            $scope.vm.comments.data.push(res);
            $scope.vm.newComment = '';
        }, function(err) {
            console.log(err);
        });


    };
}