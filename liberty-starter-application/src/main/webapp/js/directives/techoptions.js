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

 //custom directive for including options for a specific technology

 'use strict';

angular.module('appAccelerator')
.directive('techoptions', ['$log', 'appacc', '$compile',
                   function($log,   appacc,   $compile) {
  return {
    restrict: 'E',
    transclude: true,
    link: function(scope, elem, attr, appCtrl) {
      var tech = scope.getTechnology(attr.id);
      if(tech) {
        $log.debug("AppAccelertor : processing technology options for : %o ", tech);
        appacc.getTechOptions(tech).then(function(response) {
          //found the template
          if(response) {
            $log.debug("AppAccelerator : template response : %o",response);
            var compiled = $compile(response)(scope);
            elem.append(compiled);
          }
        }, function(error) {
          //error, so mark call as complete but show warning to user
          $scope.serverError = true;
          $scope.hasTechnologies = true;
        });
      } else {
        $log.error("AppAccelertor : unable to find technology options for : " + attr.id);
      }
    }
  };
}]);
