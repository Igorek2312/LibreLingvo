'use strict';

/**
 * @ngdoc service
 * @name libreLingvoApp.enableUser
 * @description
 * # enableUser
 * Factory in the libreLingvoApp.
 */
angular.module('libreLingvoApp')
  .factory('EnableUser', function ($resource, HostUrl) {
    return $resource(HostUrl+'/api/users/enable/:verificationToken',{verificationToken:'@id'});
  });