angular
    .module('sproutupApp')
    .factory('UserService', UserService);

UserService.$inject = ['$resource'];

function UserService($resource){
	return $resource('/api/users/:nickname', {nickname:'@nickname'}, {update:{method:'PUT'}} ); // Note the full endpoint address
}