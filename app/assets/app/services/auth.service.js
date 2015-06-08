// factory
angular
    .module('app')
    .factory('AuthService', authService);

authService.$inject = ['$http', '$q', '$cookieStore','$log', '$rootScope'];

function authService($http, $q, $cookieStore, $log, $rootScope){
    var user = {};
    var isLoggedIn = false;
    var urlBase = '/api/auth';
    var accessLevels = routingConfig.accessLevels
        , userRoles = routingConfig.userRoles
//        , currentUser = $cookieStore.get('user') || { username: '', role: userRoles.public };

    var service = {
        authorize: authorize,
        provider: getContacts,
        login: login,
        logout: logout,
        user: user,
        isLoggedIn: isLoggedIn
    };

    return service;

    $log.debug("AuthService > init");

    $cookieStore.remove('user');

    function changeUser(user) {
        angular.extend(currentUser, user);
        AuthService.user = user;
    }

    function currentUser() {
        return currentUser;
    }

    AuthService.authorize = function(accessLevel, role) {
        if(role === undefined) {
            if(!currentUser == null){
                role = currentUser.role;
            }
            else{
                return false;
            }
        }

        return accessLevel.bitMask & role.bitMask;
    };

    AuthService.getUser = function(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/user',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            switch(status){
                case 200:
                    angular.extend(AuthService.user,data);
                    AuthService.isLoggedIn = true;
                    console.log("status: ", status);
                    changeUser(data);
                    $log.debug("auth user service returned success: " + currentUser.name);
                    deferred.resolve(AuthService.user);
                    break;
                case 204: // no content
                default:
                    AuthService.isLoggedIn = false;
                    $log.debug("user not logged in");
                    deferred.reject("user not logged in");
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
    };


    AuthService.provider = function(provider, path){
        var deferred = $q.defer();
        // get the current path
        //var currentPath = $location.path();
        // redirect external url
        //$window.location.href = 'http://www.google.com';

        $http({
            method: 'POST',
            url: '/api/auth/provider/' + provider,
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
    };

    AuthService.login = function(user){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/login',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            angular.extend(AuthService.user,data);
            AuthService.isLoggedIn = true;
            $log.debug("login service returned success");
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            $log.debug("login service returned error");
            deferred.reject("failed to login");
        });

        $log.debug("login service returned promise");
        return deferred.promise;
    };

    AuthService.signup = function(user){
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: '/api/auth/signup',
            data: user,
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            angular.extend(AuthService.user,data);
            AuthService.isLoggedIn = true;
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject(data);
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    };

    AuthService.logout = function(){
        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: '/api/auth/logout',
            headers: {'Content-Type': 'application/json'}
        }).success(function(data, status, headers, config){
            // this callback will be called asynchronously
            // when the response is available
            AuthService.user = {};
            AuthService.isLoggedIn = false;
            deferred.resolve("success");
        }).error(function(data, status, headers, config){
            // called asynchronously if an error occurs
            // or server returns response with an error status.
            deferred.reject("could not logout");
        });

        $log.debug("auth signup service returned promise");
        return deferred.promise;
    };

    AuthService.accessLevels = accessLevels;

    AuthService.userRoles = userRoles;
};
