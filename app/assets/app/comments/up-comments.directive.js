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
            commenting: '=',
            state: "@",
            params: "="
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

    vm.commentCount = 0;
    $scope.vm.addComment = function() {
        if (vm.commentCount !== 1) {
            console.log('postijg');
            if (!vm.newComment) {
                vm.commentWarning = true;
            } else {
                var Comment = CommentService.comment(vm.type, vm.id);
                var newComment = new Comment();
                newComment.body = vm.newComment;
                vm.commentCount = 1;

                newComment.$save(function(res) {
                    res.body = urlify(res.body);
                    vm.comments.data.push(res);
                    vm.newComment = '';
                    vm.commenting = false;
                    vm.commentWarning = false;
                    vm.commentCount = 0;
                }, function(err) {
                    vm.commentWarning = false;
                    vm.commentCount = 0;
                    console.log(err);
                });
            }
        }
        
    };

    function urlify(text) {
        var urlRegex = /(https?:\/\/[^\s]+)/g;
        return text.replace(urlRegex, function(url) {

            var displayedUrl;
            if (url.length > 50) {
                displayedUrl = url.substring(0, 50);
                displayedUrl += '...';
            } else {
                displayedUrl = url;
            }
            console.log(url);
            return '<a href="' + url + '" target="_blank">' + displayedUrl + '</a>';
        });
    }

    // logic for a spinner after the save—should be moved to a directive 
    var opts = {
          lines: 8, // The number of lines to draw
          length: 16, // The length of each line
          width: 23, // The line thickness
          radius: 42, // The radius of the inner circle
          scale: 0.13, // Scales overall size of the spinner
          corners: 1, // Corner roundness (0..1)
          color: 'white', // #rgb or #rrggbb or array of colors
          opacity: 0.25, // Opacity of the lines
          rotate: 0, // The rotation offset
          direction: -1, // 1: clockwise, -1: counterclockwise
          speed: 0.8, // Rounds per second
          trail: 60, // Afterglow percentage
          fps: 20, // Frames per second when using setTimeout() as a fallback for CSS
          zIndex: 2e9, // The z-index (defaults to 2000000000)
          className: 'spinner', // The CSS class to assign to the spinner
          top: '50%', // Top position relative to parent
          left: '50%', // Left position relative to parent
          shadow: false,// Whether to render a shadow
          hwaccel: false, // Whether to use hardware acceleration
          position: 'absolute' // Element positioning
    };
}