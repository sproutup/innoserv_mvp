angular
    .module('sproutupApp')
    .factory('PostService', PostService);

PostService.$inject = ['$resource'];

function PostService($resource){
  var service = {
    post: post,
    timeline: timeline,
    myTimeline: myTimeline,
    userTimeline: userTimeline,
    groupTimeline: groupTimeline
  };
  return service;

  function post() {
    return $resource('/api/post/:id', {id:'@id'}, {update:{method:'PUT'}} );
  }

  function timeline() {
    return $resource('/api/post/timeline/:start', {start:'@start'});
  }

  function myTimeline() {
    return $resource('/api/post/timeline/me/:start', {start:'@start'});
  }

  function userTimeline() {
    return $resource('/api/post/timeline/user/:userId/:start', {start:'@start', userId:'@userId'});
  }

  function groupTimeline() {
    return $resource('/api/post/timeline/group/:groupId/:start', {start:'@start', groupId:'@groupId'});
  }
}