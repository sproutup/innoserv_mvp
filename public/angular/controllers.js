'use strict';

/* Controllers */

/*
sproutupApp.controller('productTabCtrl', function($scope) {
  $scope.phones = [
    {'name': 'Nexus S',
     'snippet': 'Fast just got faster with Nexus S.'},
    {'name': 'Motorola XOOM™ with Wi-Fi',
     'snippet': 'The Next, Next Generation tablet.'},
    {'name': 'MOTOROLA XOOM™',
     'snippet': 'The Next, Next Generation tablet.'}
  ];
});

function My1Ctrl() {}
My1Ctrl.$inject = [];

function My2Ctrl() {}
My2Ctrl.$inject = [];

function My3Ctrl() {}
My3Ctrl.$inject = [];
*/

var fileControllers = angular.module('FileControllers', ['ui.bootstrap']);

fileControllers.controller('FileCtrl', ['$scope', '$rootScope', '$upload', 'FileService', '$timeout', '$modal', '$log',
    function($scope, $rootScope, $upload, FileService, $timeout, $modal, $log){

        $scope.gallery = {
            'showUpload': false
        };

        $scope.upload = {
            progress : 0
        };

        $scope.getAllFiles = function (refId, refType) {
            FileService.getAllFiles(refId, refType).then(
                function(result){
                    $log.debug("fileCtrl - getAllFiles - result: " + result.size);
                    $scope.files = result;
                },
                function(error){
                    $scope.files = null;
                }
            );
        };

        $scope.$watch('product', function(product) {
            $log.debug("watch product found: " + product.id);
        });

        $scope.$watch('files', function(files) {
            $log.debug("watch files...");
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $scope.errorMsg = null;
                    (function(file) {
                        $log.debug("generate thumbnail");
                        generateThumb(file);
                    })(files[i]);
                }
            }
        });

        function generateThumb(file) {
            if (file != null) {
                $log.debug("set thumbnail...found file of type: " + file.type);
                if (file.type.indexOf('image') > -1) {
                    $log.debug("set thumbnail...reader supported");
                    $timeout(function() {
                        $log.debug("set thumbnail...timeout entered");
                        var fileReader = new FileReader();
                        fileReader.readAsDataURL(file);
                        fileReader.onload = function(e) {
                            $timeout(function() {
                                $log.debug("set thumbnail url");
                                file.dataUrl = e.target.result;
                            });
                        }
                    });
                }
                else
                if (file.type.indexOf('video') > -1) {
                    $log.debug("set thumbnail...reader supported");
                    file.dataUrl = "/assets/images/video-thumbnail.png";
                }
            }
        };

        $scope.toggle = function() {
            $log.debug("cancel");
            $scope.gallery.showUpload = !$scope.gallery.showUpload;
            $scope.reset();
        };

        $scope.reset = function() {
            $log.debug("reset");
            $scope.files = null;
            $scope.upload.text = "";
        };

        $scope.upload = function (files, message, refId, refType) {
            $log.debug("upload: " + refId);

            var startTime = +new Date();

            if (files && files.length) {
                for (var i = 0; i < files.length; i++) {
                    FileService.authenticate(files[i], message, refId, refType).then(
                        function (result) {
                            $log.debug("upload auth returned");
                            result.file.progress = 50;

                            FileService.upload(result.file, result.data).then(
                                function (result) {
                                    // verify that upload to s3 is done
                                    FileService.verify(result.file, result.uuid).then(
                                        function (result) {
                                            result.file.progress = 100;
                                            var endTime = +new Date();
                                            $log.debug('upload...finished in ' + (endTime - startTime) + ' ms');

                                            $log.debug("broadcast fileUploadEvent");
                                            // firing an event downwards
                                            $rootScope.$broadcast('fileUploadEvent', {
                                                someProp: 'Sending you an Object!' // send whatever you want
                                            });
                                            $scope.reset();
                                        }
                                    )
                                },
                                function (error) {
                                    // todo handle error
                                },
                                function (result) {
                                    result.file.progress = (50 + (result.progress / 2)).toFixed(1);
                                }
                            );
                        },
                        function (errorPayload) {
                        },
                        function (result) {
                            $log.debug("auth progress - " + result.progress);
                            result.file.progress = (result.progress / 2).toFixed(1);
                        }
                    );
                }
            }
        };
}]);

var authControllers = angular.module('AuthControllers', ['ui.bootstrap']);

authControllers.controller('AuthCtrl', ['$scope', '$modal', '$log', 'AuthService',
    function ($scope, $modal, $log, AuthService) {

    $scope.signup = {
        'email': '',
        'password': '',
        'confirm': ''
    };
    $scope.login = {
        'email': '',
        'password': '',
        'confirm': ''
    };

    $scope.user ={
        isLoggedIn : false,
        name : 'Test'
    }

    var isLoggedIn = false;

    updateUser();

    function updateUser() {
        AuthService.user()
            .then(
            function(payload){
                // this callback will be called asynchronously
                // when the response is available
                angular.extend($scope.user, payload);
                isLoggedIn = true;
                $scope.user.isLoggedIn = true;
            },
            function(errorPayload){
                // called asynchronously if an error occurs
                // or server returns response with an error status.
                isLoggedIn = false;
                $scope.user.isLoggedIn = false;
            }
        );
    }

    $scope.isLoggedIn = function(){return isLoggedIn}

    $scope.currentUser = function() {
        return $scope.user;
    }

    $scope.signup = function (size) {

        var signupInstance = $modal.open({
            templateUrl: '/assets/templates/signup.html',
            controller: 'SignupInstanceCtrl',
            size: size,
            resolve: {
                signup: function () {
                    return $scope.signup;
                }
            }
        });

        signupInstance.result.then(function (signup) {
            updateUser();
        }, function (result) {
            $log.info('Modal dismissed at: ' + new Date());
            if(result == "login"){
                $scope.login('sm');
            }
        });
    };

    $scope.login = function (size) {

        var loginInstance = $modal.open({
            templateUrl: '/assets/templates/login.html',
            controller: 'LoginInstanceCtrl',
            size: size,
            resolve: {
                login: function () {
                    return $scope.login;
                }
            }
        });

        loginInstance.result.then(function (result) {
            $log.debug("login return value: " + result);
            updateUser();
        }, function (result) {
            $log.info('Login dismissed at: ' + new Date());
            if(result == "signup"){
                $scope.signup('sm');
            }
        });
    };


    $scope.logout = function (size) {
        var promise = AuthService.logout();

        promise.then(
            function(payload){
                // this callback will be called asynchronously
                // when the response is available
                $scope.user.isLoggedIn = false;
                $scope.user.name = '';
                $log.info('logout success: ' + new Date());
            },
            function(errorPayload){
                // called asynchronously if an error occurs
                // or server returns response with an error status.
                $log.info('logout failed: ' + new Date());
            }
        );
    };
}]);

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

authControllers.controller('SignupInstanceCtrl', ['$scope', '$modalInstance', 'AuthService', '$log', 'signup',
    function ($scope, $modalInstance, AuthService, $log, signup) {

    $scope.ok = function () {
        var dataObject = {
            "name" : $scope.signup.name,
            "email" : $scope.signup.email,
            "password" : $scope.signup.password
        };

        // reset error message
        $scope.signup.error = "";

        var promise = AuthService.signup(dataObject);

        promise.then(
            function(payload){
                // this callback will be called asynchronously
                // when the response is available
                $modalInstance.close($scope.signup);
            },
            function(errorPayload){
                $log.info('Signup failed: ' + errorPayload + " " + new Date());
                if(errorPayload.status=="USER_EXISTS"){
                    $scope.signup.error = "Oops, email's been taken. If it's you, please log in.";
                }
                else{
                    $scope.signup.error = "Signup failed";
                }
            }
        );
    };

    $scope.login = function () {
        $modalInstance.dismiss('login');
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);

authControllers.controller('LoginInstanceCtrl', ['$scope', '$location', '$window', '$modalInstance', 'AuthService', '$log', 'login',
    function ($scope, $location, $window, $modalInstance, AuthService, $log, login) {

    $scope.ok = function () {
        var dataObject = {
            email : $scope.login.email,
            password  : $scope.login.password
        };

        $log.info('login attempt: ' + new Date());

        // reset error message
        $scope.login.error = "";

        var promise = AuthService.login(dataObject);

        promise.then(
            function(payload){
                $modalInstance.close(payload);
            },
            function(errorPayload){
                $log.info('Login failed: ' + new Date());
                $scope.login.error = true;
            }
        );
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.signup = function () {
        $modalInstance.dismiss('signup');
    };

    $scope.provider = function (provider) {
        $log.debug("provider: " + provider);

        var currentPath = $location.path();
        $log.debug("path: " + currentPath);

        var dataObject = {
            originalUrl : currentPath
        };

        var promise = AuthService.provider(provider, dataObject);

        promise.then(
            function(payload){
                $log.debug(payload);
                $window.location.href = payload;
                $modalInstance.close("ok");
            },
            function(errorPayload){
                $log.info('Login failed: ' + new Date());
            }
        );
    };
}]);

// products
var productControllers = angular.module('productControllers', []);

productControllers.controller('ProductTabCtrl', function($scope, $window, $location, $log) {
    // Tab directive
    $scope.tabs = [
        {title:'About', page: '/views/product-about'},
        {title:'Bar', page: '/views/product-bar'},
        {title:'Gallery', page: '/views/product-gallery'}
        ];
    $scope.tabs.activeTab = 0;
    $log.debug("activeTab = " + $scope.tabs.activeTab );
    $location.search('tab', '0');
});

productControllers.controller('productListCtrl', ['$scope', 'ProductService',
  function($scope, ProductService) {
    $scope.products = ProductService.query();
 //   $scope.orderProp = 'age';
  }]);

productControllers.controller('productDetailCtrl', ['$scope', '$stateParams', '$log', 'ProductService',
  function($scope, $stateParams, $log, ProductService) {
    $log.debug("entered product details ctrl. slug=" + $stateParams.slug);
    $scope.product = ProductService.get({slug: $stateParams.slug}, function(product) {
    });
  }]);

productControllers.controller('ForumCtrl', ['$scope', 'ForumService', 'LikesService', '$log',
  function($scope, ForumService, LikesService, $log) {

    $scope.posts = [];
    $scope.forum = {
        showNewPost : false,
        selectedCategory : 0,   // default to 0 = compliments
        category : ["compliments","suggestions","questions"]
    };

      $log.debug("forum ctrl loaded");

      $scope.$watch('product.id', function(files) {
          $log.debug("watch product.id...");
          getPosts($scope.product.id, $scope.forum.selectedCategory);
      });

          // create a blank object to hold our form information
    // $scope will allow this to pass between controller and view
    $scope.forum.newPostForm = {};
    $scope.forum.newCommentForm = {};

    // process the new post form
    $scope.processNewPostForm = function() {
        console.log("post: ", $scope.forum.newPostForm);
        $scope.forum.newPostForm.product_id = $scope.product.id;
        $scope.forum.newPostForm.category = $scope.forum.selectedCategory;
        ForumService.addPost($scope.forum.newPostForm)
        .success(function(data){
            // reload data
            getPosts($scope.product.id, $scope.forum.selectedCategory);
            $scope.forum.showNewPost = false;
            $scope.forum.newPostForm.title = "";
            $scope.forum.newPostForm.content = "";
            while($scope.forum.newPostForm.tags.length > 0) {
                $scope.forum.newPostForm.tags.pop();
            }
        });
    };

    // add like
    $scope.addLike = function(post, userId) {
        console.log("like: ", userId);
        LikesService.addLikes(post.id, "models.Post", userId)
        .success(function(data){
            // reload data
            getPosts($scope.product.id, $scope.forum.selectedCategory);
        });
    };

    // process the new comment form
    $scope.processNewCommentForm = function(post) {
        $scope.forum.newCommentForm[post.id].parent = post.id;
        console.log("comment: ", $scope.forum.newCommentForm[post.id]);
        ForumService.addPost($scope.forum.newCommentForm[post.id])
        .success(function(data){
            // reload data
            getPosts($scope.product.id, $scope.forum.selectedCategory);
            $scope.forum.newCommentForm[post.id].content = "";
            $scope.forum.showNewComment[post.id] = false;
        });
    };

    $scope.toogleComment = function(id){
        $scope.forum.showNewComment[id] = !$scope.forum.showNewComment[id];
    }

    // change category
    $scope.changeCategory = function(category_id) {
        $scope.forum.selectedCategory = category_id;
        getPosts($scope.product.id, $scope.forum.selectedCategory);
    };

    function getPosts(product_id, category_id) {
        ForumService.getPosts(product_id, category_id)
        .success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            $scope.posts = data;
            $log.debug("posts: " + $scope.posts);
        })
        .error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("error: " + $scope.posts);
        });
    }
}]);
