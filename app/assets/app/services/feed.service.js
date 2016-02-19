angular
    .module('sproutupApp')
    .factory('FeedService', FeedService);

FeedService.$inject = ['$resource'];

function FeedService($resource){
	var service = {
		buzzAll: buzzAll,
		buzzGroup: buzzGroup,
		buzzSingle: buzzSingle,
		buzzUser: buzzUser
	};

	return service;

	function buzzAll() {
        return $resource('/api/buzz/:start', {start:'@start'}, {update:{method:'PUT'}} );
	}

	function buzzGroup() {
        return $resource('/api/post/timeline/group/:groupId/:start', {groupId:'@groupId', start:'@start'}, {update:{method:'PUT'}} );
	}

	function buzzSingle() {
		return $resource('/api/post/:id', {id:'@id'}, {update:{method:'PUT'}});
	}

	function buzzUser() {
		return $resource('/api/buzz/user/:nickname/:start', {nickname:'@nickname', start:'@start'}, {update:{method:'PUT'}});
	}

}
