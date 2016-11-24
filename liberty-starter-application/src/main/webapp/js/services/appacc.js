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

 //service for communicating with the app accelerator

angular.module('appAccelerator')
   .factory('appacc',
    [      '$log','$q','$http',
  function ($log,  $q,  $http) {
     $log.debug("Initialising AppAccelerator service");

     var serviceURL = "/start/api/v1";
     var techURL = serviceURL + "/tech";  //where to get the technology types from

     var getTechnologies = function() {
        $log.debug("AppAccelerator : GET : available technology list");

        var q = $q.defer();
        $http({
          url: techURL,
          method: 'GET'
          }).then(function(response) {
            // 200: technologies discovered
            $log.debug(response.status + ' ' + response.statusText + " %o - OK", response.data);
            var data = [];
            if ( response.status === 200 ) {
              data = angular.fromJson(response.data);
            }
            $log.debug("getTechnologies returing %o", data);
            q.resolve(data);
          }, function(response) {
            $log.debug(response.status + ' ' + response.statusText + " %o - FAILED", response.data);
            // TODO: Alert -- problem occurred
            // return empty set for decent experience, or could insert some default values here ...
            q.resolve([]);
          });

          return q.promise;
      };

      return {
        getTechnologies: getTechnologies
      };
  }]);
