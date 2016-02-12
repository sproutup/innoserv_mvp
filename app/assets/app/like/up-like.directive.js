angular
    .module('sproutupApp')
    .directive('upLike', upLike);

function upLike() {
    var directive = {
        restrict: 'EA',
        templateUrl: 'assets/app/like/up-like.html',
        scope: {
            likes: '=',
            id: '=upId',
            type: '@upType',
            state: "@",
            params: "="
        },
        link: linkFunc,
        controller: upLikeController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {
    }
}


upLikeController.$inject = ['LikesService', 'AuthService', '$timeout', '$scope', '$rootScope'];

function upLikeController(likesService, authService, $timeout, $scope, $rootScope) {
    var vm = this;
    vm.upvoteMouseEnter = upvoteMouseEnter;
    vm.upvoteMouseLeave = upvoteMouseLeave;

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

    $scope.$watch(function () {
        return authService.loggedIn();
    }, function(newVal, oldVal) {
        didIlikeItAlready();
    }, true);

    function didIlikeItAlready() {
        if(authService.loggedIn()) {
            var userid = authService.m.user.id;
            if ($scope.vm.likes === undefined) {
                return false;
            }
            for (var i = 0; i < $scope.vm.likes.length; i++) {
                if ($scope.vm.likes[i].userId == userid) {
                    $scope.vm.upvoted = true;
                    $scope.vm.likeId = $scope.vm.likes[i].id;
                    $scope.vm.likeIndex = i;
                    return true;
                }
            }
        }
        $scope.upvoted = false;
        return false;
    }

    function upvoteMouseEnter() {
        $timeout(function() {
            if (!vm.justLeft) {
                vm.hovering = true;
            }
        }, 300);
    }

    function upvoteMouseLeave() {
        vm.hovering = false;
        vm.justLeft = true;
        $timeout(function() {
            vm.justLeft = false;
        }, 300);
    }

    vm.handleUpvoteClick = function() {
        if (!authService.loggedIn()) {
            $scope.$emit('LoginEvent', {
                someProp: 'Sending you an Object!' // send whatever you want
            });
            return;
        }

        if (didIlikeItAlready() === false) {
            likesService.addLike($scope.vm.id, $scope.vm.type).then(
                function(data) {
                  if(typeof $scope.vm.likes == 'undefined') $scope.vm.likes = [];
                    $scope.vm.likes.push(data);
                    $scope.vm.upvoted = true;
                }, function(reason) {
                    console.log('up-files failed: ' + reason);
                }
            );
        } else {
          likesService.deleteLike($scope.vm.likeId).then(
            function(data) {
              // remove the entry from local array
              $scope.vm.likes.splice(vm.likeIndex, 1);
            }, function(reason) {
                console.log('up-files failed: ' + reason);
            }
          );
        }
    };
}
