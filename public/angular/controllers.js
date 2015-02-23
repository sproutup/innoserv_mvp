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

var authControllers = angular.module('AuthControllers', ['ui.bootstrap']);

authControllers.controller('AuthCtrl', function ($scope, $modal, $log) {

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
            $scope.signup = signup;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
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

        loginInstance.result.then(function (login) {
            $scope.login = login;
        }, function () {
            $log.info('Login dismissed at: ' + new Date());
        });
    };


    $scope.open = function (size) {

        var modalInstance = $modal.open({
            templateUrl: '/assets/templates/signup.html',
            controller: 'AuthInstanceCtrl',
            size: size,
            resolve: {
                items: function () {
                    return $scope.items;
                }
            }
        });

        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };
});

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

        var promise = AuthService.signup(dataObject);

        promise.then(
            function(payload){
                // this callback will be called asynchronously
                // when the response is available
                $modalInstance.close($scope.signup);
            },
            function(errorPayload){
                // called asynchronously if an error occurs
                // or server returns response with an error status.
                $log.info('Signup failed: ' + new Date());
            }
        );
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);

authControllers.controller('LoginInstanceCtrl', ['$scope', '$modalInstance', 'AuthService', '$log', 'login',
    function ($scope, $modalInstance, AuthService, $log, login) {

    $scope.login = login;

    $scope.ok = function () {
        var dataObject = {
            email : $scope.login.email,
            password  : $scope.login.password
        };

        $log.info('login attempt: ' + new Date());

        var promise = AuthService.login(dataObject)

        promise.then(
            function(payload){
                // this callback will be called asynchronously
                // when the response is available
                $modalInstance.close($scope.login);
            },
            function(errorPayload){
                // called asynchronously if an error occurs
                // or server returns response with an error status.
                $log.info('Login failed: ' + new Date());
            }
        );
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
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

productControllers.controller('productDetailCtrl', ['$scope', '$routeParams', 'ProductService',
  function($scope, $routeParams, ProductService) {
    $scope.product = ProductService.get({slug: $routeParams.slug}, function(product) {
//    $scope.product = ProductService.get({slug: 1}, function(product) {

//      $scope.mainImageUrl = phone.images[0];
    });

//    $scope.setImage = function(imageUrl) {
//      $scope.mainImageUrl = imageUrl;
//    }
  }]);

productControllers.controller('ForumCtrl', ['$scope', 'ForumService', 'LikesService', '$log',
  function($scope, ForumService, LikesService, log) {

    $scope.posts = [];
    $scope.forum = {
        showNewPost : false,
        selectedCategory : 0,   // default to 0 = compliments
        category : ["compliments","suggestions","questions"]
    }

    // create a blank object to hold our form information
    // $scope will allow this to pass between controller and view
    $scope.forum.newPostForm = {};
    $scope.forum.newCommentForm = {};

    // Load initial data
    getPosts($scope.product.id, $scope.forum.selectedCategory);

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
            log.debug("posts: " + $scope.posts);
        })
        .error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            log.debug("error: " + $scope.posts);
        });
    }

/*
    $scope.postsxx = [{
                     "id": 1,
                     "content": "first post",
                     "user": {"name": "peter"},
                     "tags": [{"name": "energy"},{"name": "led"}],
                     "comments": [
                        {
                        "id": 1,
                        "user": {"name": "bill"},
                        "content": "kjsadfh ksdafjhasdk fdksa f"
                        },
                        {
                        "id": 1,
                        "user": {"name": "josh"},
                        "content": "kjsadfh ksdafjhasdk fdksa f"
                        },
                        {
                        "id": 1,
                        "user": {"name": "alfred"},
                        "content": "kjsadfh ksdafjhasdk fdksa f"
                        }
                        ]
                     },
                     {
                     "id": 2,
                     "content": "second post",
                     "user": {"name": "peter"}
                     },
                     {
                     "id": 3,
                     "content": "third post",
                     "user": {"name": "peter"}
                     }
                   ];  */
}]);
