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
.controller('swagCtrl', ['$scope', '$log', 'appacc', '$http',
                function($scope,    $log,   appacc,   $http) {

  $log.debug("Swagger : using controller 'swagCtrl'");

  $scope.options = {useSwaggerDoc : false};
  $scope.allowConfig = false;
  $scope.fileStatus = undefined;
  $scope.fileOperationsDisabled = false;

  var ctrl = this;    //used for scoping in promises
  var restId = "rest";
  var microprofileId = "microprofile";
  var msBuilderId = "ms-builder";
  var swaggerId = "swagger";
  var swaggerFileSelect = undefined;
  var file = undefined;
  var techOptions = "swagger:server";
  var fileUploadedOK = false;

  appacc.addListener(function() {
    $log.debug("Swagger : checking service state.");
    
    $scope.allowConfig = appacc.isSelected(restId) || appacc.isSelected(microprofileId) || appacc.isSelected(msBuilderId);
    if(appacc.isSelected(swaggerId) && $scope.options.useSwaggerDoc && ctrl.fileUploadedOK && $scope.allowConfig) {
      appacc.addTechOption(techOptions);
    } else {
      appacc.removeTechOption(techOptions);
    }
    $log.debug("Swagger : allowConfig set to " + $scope.allowConfig);
    init();
  });

  function init() {
    if(!swaggerFileSelect) {
      swaggerFileSelect = document.getElementById('step3SwaggerFileSelect');
      swaggerFileSelect.onchange = function() {
        file = swaggerFileSelect.files[0];
        $scope.fileStatus = (swaggerFileSelect.files.length > 0) ? "Selected " + file.name : undefined;
        $log.debug("Swagger : updating file selection : " + file.name);
        $scope.$apply();
      };
      appacc.retrieveWorkspaceId().then(function(id) {
        $log.debug("Swagger : workspace ID : " + id);
      }, function() {
        $log.debug("An error occurred while retrieving the workspace ID");
      });
    }
  }

  $scope.updateTechOptions = function() {
    appacc.notifyListeners();
  }

  $scope.upload = function() {
    $scope.fileStatus = "Uploading ...";
    $scope.fileOperationsDisabled = true;
    appacc.retrieveWorkspaceId().then(function(id) {
      var formData = new FormData();
      // Add the file to the request.
      formData.append('swaggerDefinition', file, file.name);
      $http({
        url: 'api/v1/upload?tech=swagger&cleanup=true&process=true&workspace=' + id,
        method: 'POST',
        transformRequest: angular.identity,
        headers: {
                    'Content-Type': undefined
                  },
        data: formData
        }).then(function(response) {
          $scope.fileStatus = "Successfully generated server code from " + file.name;
          ctrl.fileUploadedOK = true;
          appacc.addTechOption(techOptions);
          $scope.fileOperationsDisabled = false;
        }, function(response) {
          $scope.fileStatus = "An error occurred while generating server code from " + file.name + " : " + response.status;
          $log.debug("Swagger : Error with response: %o", response);
          $scope.fileOperationsDisabled = false;
        });
    }, function() {
      $scope.fileStatus = "An error occurred while retrieving the workspace ID";
      $scope.fileOperationsDisabled = false;
    });
  }

}]);
