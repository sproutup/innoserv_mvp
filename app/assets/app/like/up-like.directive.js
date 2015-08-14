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
            type: '@upType'
        },
        link: linkFunc,
        controller: upLikeController,
        controllerAs: 'vm',
        bindToController: true // because the scope is isolated
    };

    return directive;

    function linkFunc(scope, el, attr, ctrl) {
        console.log('LINK: scope.vm.likes: ', scope.vm.likes);
        console.log('LINK: scope.vm.id = %s', scope.vm.id);
        console.log('LINK: scope.vm.type = %s', scope.vm.type);
    }
}
/*
function upLike(likesService, authService, $timeout) {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                likes: '=',
                id: '=upId',
                type: '@upType'
            },
            templateUrl: 'assets/app/like/up-like.html',
            link: function (scope, element, attrs) {
                attrs.$observe('likes', function (likes) {
                    didIlikeItAlready();
                });

                scope.$watch(function () {
                        return authService.loggedIn();
                    },
                    function(newVal, oldVal) {
                        didIlikeItAlready();
                    }, true);

                function didIlikeItAlready() {
                    if(authService.loggedIn()) {
                        var userid = authService.m.user.id;
                        if (scope.likes === undefined) {
                            return false;
                        }
                        for (var i = 0; i < scope.likes.length; i++) {
                            if (scope.likes.data[i].user.id == userid) {
                                scope.upvoted = true;
                                return true;
                            }
                        }
                    }
                    scope.upvoted = false;
                    return false;
                }

                element.on('click', function () {
                    console.log(scope) ;
                    console.log("#########################");
                    console.log("up-like > clicked id/type: " + scope.id + "/" + scope.type);
                    console.log("up-like > is-logged-in: " + authService.loggedIn());

                    if(!authService.loggedIn()){
                        scope.$emit('LoginEvent', {
                            someProp: 'Sending you an Object!' // send whatever you want
                        });
                        return;
                    }

                    if(scope.likes === undefined){
                        scope.likes = [];
                    }

                    console.log("user.id: " + authService.m.user.id);

                    if(didIlikeItAlready() === false){
                        console.log(scope);
                        likesService.addLike(scope.id, scope.type, authService.m.user.id).then(
                            function(data) {
                                console.log(data);
                                console.log("liked it: " + scope.id);
                                scope.likes.push(data);
                                scope.upvoted = true;
                            }, function(reason) {
                                console.log('up-files failed: ' + reason);
                            }
                        );
                    }

                });
            }
        };
    }
]);
*/

upLikeController.$inject = ['LikesService', 'AuthService', '$timeout'];

function upLikeController(likesService, authService, $timeout) {
    var vm = this;

    vm.upvoted = true;
    vm.like = [];
}
