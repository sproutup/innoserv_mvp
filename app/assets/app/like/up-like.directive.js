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
            params: "@"
        },
        link: linkFunc,
        controller: upLikeController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {
        // console.log('LINK: scope.vm.likes: ', scope.vm.likes);
        // console.log('LINK: scope.vm.id = %s', scope.vm.id);
        // console.log('LINK: scope.vm.type = %s', scope.vm.type);

    }
}


upLikeController.$inject = ['LikesService', 'AuthService', '$timeout', '$scope', '$rootScope'];

function upLikeController(likesService, authService, $timeout, $scope, $rootScope) {
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
            for (var i = 0; i < $scope.vm.likes.data.length; i++) {
                if ($scope.vm.likes.data[i].user.id == userid) {
                    $scope.vm.upvoted = true;
                    return true;
                }
            }
        }
        $scope.upvoted = false;
        return false;
    }


    vm.handleUpvoteClick = function() {
        console.log($scope);
        if (!authService.loggedIn()) {
            $scope.$emit('LoginEvent', {
                someProp: 'Sending you an Object!' // send whatever you want
            });
            return;
        }

        console.log("user.id: " + authService.m.user.id);

        if (didIlikeItAlready() === false) {
            console.log($scope.vm.id + $scope.vm.type + authService.m.user.id);
            likesService.addLike($scope.vm.id, $scope.vm.type, authService.m.user.id).then(
                function(data) {
                    $scope.vm.likes.data.push(data);
                    $scope.vm.upvoted = true;
                    $scope.vm.likes.count += 1;
                }, function(reason) {
                    console.log('up-files failed: ' + reason);
                }
            );
        } else {
            likesService.deleteLike($scope.vm.id, $scope.vm.type, authService.m.user.id).then(
                function(data) {
                    console.log(data);
                    console.log("did not like it: " + $scope.id);
                    console.log($scope.vm.likes.data);
                    console.log(authService.m.user.id);
                    $scope.vm.likes.data.filter(function(like){
                        if (like.user.id === authService.m.user.id) {
                            var index = $scope.vm.likes.data.indexOf(like);
                            $scope.vm.likes.data.splice(index, 1);
                            $scope.vm.likes.count -= 1;
                            $scope.vm.upvoted = false;
                        }
                    });
                    // $scope.likes.push(data);
                    // $scope.vm.upvoted = true;
                    // $scope.vm.likes.count += 1;
                }, function(reason) {
                    console.log('up-files failed: ' + reason);
                }
            );
        }
    };
    // element.on('click', function () {
    //     console.log($scope) ;
    //     console.log("#########################");
    //     console.log("up-like > clicked id/type: " + $scope.id + "/" + $scope.type);
    //     console.log("up-like > is-logged-in: " + authService.loggedIn());

    //     if (!authService.loggedIn()) {
    //         $scope.$emit('LoginEvent', {
    //             someProp: 'Sending you an Object!' // send whatever you want
    //         });
    //         return;
    //     }

    //     if ($scope.likes === undefined) {
    //         $scope.likes = [];
    //     }

    //     console.log("user.id: " + authService.m.user.id);

    //     if (didIlikeItAlready() === false) {
    //         console.log($scope);
    //         likesService.addLike($scope.id, $scope.type, authService.m.user.id).then(
    //             function(data) {
    //                 console.log(data);
    //                 console.log("liked it: " + $scope.id);
    //                 $scope.likes.push(data);
    //                 $scope.upvoted = true;
    //             }, function(reason) {
    //                 console.log('up-files failed: ' + reason);
    //             }
    //         );
    //     }

    // });
}