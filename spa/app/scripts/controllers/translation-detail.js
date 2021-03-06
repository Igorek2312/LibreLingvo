'use strict';

/**
 * @ngdoc function
 * @name libreLingvoApp.controller:TranslationDetailCtrl
 * @description
 * # TranslationDetailCtrl
 * Controller of the libreLingvoApp
 */
angular.module('libreLingvoApp')
  .controller('TranslationDetailCtrl', function ($scope, $state, $stateParams, Translations) {
    Translations.get(
      {
        userId: 'me',
        translationId: $stateParams.translationId
      },
      function (response) {
        $scope.translation = response;
      }
    );
  });
