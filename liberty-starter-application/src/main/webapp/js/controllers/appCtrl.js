/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

'use strict';

angular.module('appAccelerator')
.controller('appCtrl', ['$scope', '$log', 'appacc',
                function($scope,   $log,   appacc) {

  $log.debug("AppAccelerator : using controller 'appCtrl'");

  // Reference to this for use in promises
  var appCtrl = this;

  $scope.technologies = [];

  this.getTech = function() {
    appacc.getTechnologies().then(function(response) {
      $log.debug('AppAccelerator : getTechnologies %o', response);
      appCtrl.technologies = response;
    });
  };


  //trigger an initial population of technologies
  this.getTech();
}]);
