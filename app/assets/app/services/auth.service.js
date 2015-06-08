// factory
angular
    .module('sproutupApp')
    .factory('AuthService', authService);

authService.$inject = ['$http', '$q', '$cookieStore', '$log', 'UserService'];

function authService($http, $q, $cookieStore, $log, userService){
    var user = {};
    var isLoggedIn = false;
    var urlBase = '/api/auth';
    var accessLevels = routingConfig.accessLevels,
        userRoles = routingConfig.userRoles;
//        , currentUser = $cookieStore.get('user') || { username: '', role: userRoles.public };

    var model = {
      user: {name: 'Peter'},
      isLoggedIn: false
    };

    var service = {
        m: model,
        authorize: authorize,
        getUser: getAuthenticatedUser,
        save: save,
        provider: provider,
        login: login,
        logout: logout
    };

    activate();

    return service;

    function activate() {
        /**
         * Step 1
         * Ask the getAvengers function for the
         * avenger data and wait for the promise
         */

        return getAuthenticatedUser().then(function() {
            /**
             * Step 4
             * Perform an action on resolve of final promise
             */
            console.log('Return User');
        });
    }


//    $log.debug("AuthService > init");

//    $cookieStore.remove('user');

    function changeUser(user) {
        angular.extend(currentUser, user);
        AuthService.user = user;
    }

    function currentUser() {
        return currentUser;
    }

    function authorize(accessLevel, role) {
        if(role === undefined) {
            if(currentUser !== null){
                role = currentUser.role;
            }
            else{
                return false;
            }
        }

        return accessLevel.bitMask & role.bitMask;
    }

    function getAuthenticatedUser(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/user',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            switch(status){
                case 200:
                    //angular.extend(user, data);
                    //isLoggedIn = true;
                    model.isLoggedIn = true;
                    angular.extend(model.user, data);
                    console.log("status: ", status);
                    //changeUser(data);
                    $log.debug("auth user service returned success: " + model.user.name);
                    deferred.resolve(model.user);
                    break;
                case 204: // no content	
                    model.isLoggedIn = false;
                    $log.debug("user not logged in");
                    deferred.reject("user not logged in");
                    break;
                default:
                    $log.debug("unhandled return value");
                    deferred.reject("auth user - unhandled return value");
                    break;
            }
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("auth user service returned error");
            deferred.reject("auth user - failed to get user");
        });

        $log.debug("auth user service returned promise");
        return deferred.promise;
    }

    function save(){
        var deferred = $q.defer();
        $log.debug("auth - save()");

        $http({
            method: 'POST',
            url: '/api/users/' + model.user.nickname,
            headers: {'Content-Type': 'application/json'},
            data: model.user
        }).success(function(data, status, headers, config){
            $log.debug("productSuggestionService > add success");
            deferred.resolve(data);
        }).error(function(data, status, headers, config){
            $log.debug("productSuggestionService > failed to update");
            deferred.reject("productSuggestionService > failed to add");
        });

        return deferred.promise;
    }


    function provider(authprovider, path){
        var deferred = $q.defer();
        // get the current path
        //var currentPath = $location.path();
        // redirect external url
        //$window.location.href = 'http://www.google.com';

        $http({
            method: 'POST',
            url: '/api/auth/provider/' + authprovider,
            data: path,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            $log.debug("provider returned success");
            deferred.resolve(data.redirect);
        }).error(function(data, status, headers, config){
            $log.debug("provider returned error");
            deferred.reject("failed to login");
        });

        $log.debug("provider returned promise");
        return deferred.promise;
    }

    function login(user) {
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/login',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            $log.debug("login service returned success");
            getAuthenticatedUser().then(function () {
                $log.debug("login service get user success");
                }
            );

            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("login service returned error");
            deferred.reject("failed to login");
        });

        $log.debug("login service returned promise");
        return deferred.promise;
    }

    function signup(user){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/signup',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            angular.extend(model.user,data);
            model.isLoggedIn = true;
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject(data);
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    }

    function logout(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/logout',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            model.isLoggedIn = false;
            model.user = {};
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject("could not logout");
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    }

    AuthService.accessLevels = accessLevels;

    AuthService.userRoles = userRoles;
}
