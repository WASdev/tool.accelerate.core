/*
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
 */

$(document).ready(function() {

    var serviceURL = "/start/api/v1";
    var technologies = [];
    var workspaceId="";
    var currentStep = 1;
    var googleAnalytics = new Boolean("true");
	var optionalConfigEnabled = false;

    var populateTechnologies = function() {
    	toggleOptionalStep(false);
        var step1TechnologiesContainer = $("#step1TechnologiesContainer");
        step1TechnologiesContainer.empty();
        for(var i = 0; i < technologies.length; i++) {
            var technology = technologies[i];
            var technologyTag = $("<a href=\"#\" class=\"step1Technology\" data-technologyname=\""+ technology.name +"\" data-technologyid=\"" + technology.id + "\" title=\"" + technology.description + "\" data-technologyconfiguration=\"" + technology.configuration + "\">" + technology.name + "</a>");
            technologyTag.append('<span class="state-checkmark"></span>');
            step1TechnologiesContainer.append(technologyTag);
        }
    };

	var toggleOptionalStep = function(on) {
		if (on == true) {
			if($("#step2DeployLocationsContainer .step2DeployLocation.selected").size() > 0){
				$("#step3").removeClass("hidden");
			}
			$("#navigationBottom2").removeClass("hidden");
			$("#navigationTop3").removeClass("hidden");
			$(".numberOfSteps").html("4");
			document.querySelector("#step4").style.paddingTop = "167px";
		} else {
		    $("#step3").addClass("hidden");
			$("#navigationBottom2").addClass("hidden");
			$("#navigationTop3").addClass("hidden");
			$(".numberOfSteps").html("3");
			document.querySelector("#step4").style.paddingTop = "111px";
		}
	};

	var checkOptionalConfig = function() {
	    var selectedTechnologies = $("#step1TechnologiesContainer .step1Technology.selected");
        for(var i = 0; i < selectedTechnologies.size(); i++) {
        	var optionalConfig = selectedTechnologies.get(i).dataset.technologyconfiguration;
        	if(optionalConfig == null || optionalConfig.trim().length == 0){
        		continue;
        	}else if(optionalConfig == "true") {
				optionalConfigEnabled = true;
				return;
			} else {
				var optionalConfigArray = optionalConfig.split(",");
				if(optionalConfigArray.length > 0){
					for(var j = 0; j < optionalConfigArray.length; j++) {
						var requiredTechId = optionalConfigArray[j];
						var techSelected = false;
						var selectedTechnologiesInner = $("#step1TechnologiesContainer .step1Technology.selected");
						for(var k = 0; k < selectedTechnologiesInner.size(); k++) {
							if(requiredTechId == selectedTechnologiesInner.get(k).dataset.technologyid){
								if(j == (optionalConfigArray.length - 1)){
									optionalConfigEnabled = true;
									return;
								}
								techSelected = true;
								break;
							}
						}
						if(techSelected == false){
							break;
						}
					}
				}
			}
        }
		optionalConfigEnabled = false;
	};

	var updateStepNumbers = function() {
		checkOptionalConfig();
		if(optionalConfigEnabled){
			$(".numberOfSteps").html("4");
		}else{
			$(".numberOfSteps").html("3");
		}
    };

    var getCurrentStep = function() {
        var windowScroll = $(window).scrollTop();
        for(var i = 0; i < 4; i ++) {
            var shift = 55 * (i + 1) + i;
            var step = $("#step" + (i + 1));
            var stepOffset = step.offset();
            var height = step.outerHeight();
            if(windowScroll + shift < stepOffset.top + height) {
                return i + 1;
            }
        }
    };

    var updateSteps = function() {

		checkOptionalConfig();
        var step = getCurrentStep();
        if(step != currentStep) {
            if (googleAnalytics) {
                if (step > currentStep) {
                    // We are moving forwards
                    // Google Analytics
                    ga('send', 'event', 'Navigation', 'move-forward', String(step));
                }
            }
            var navigationBottomContainer = $("#navigationBottomContainer");
            var navigationTop1 = $("#navigationTop1");
            var navigationTop2 = $("#navigationTop2");
            var navigationTop3 = $("#navigationTop3");
            switch(step) {
            case 1:
                if(navigationTop1.is(":visible")) {
                    navigationTop1.fadeOut();
                }
                if(navigationTop2.is(":visible")) {
                    navigationTop2.fadeOut();
                }
                if(navigationTop3.is(":visible")) {
                    navigationTop3.fadeOut();
                }
                if(!navigationBottomContainer.is(":visible")) {
                    navigationBottomContainer.fadeIn();
                }
                $("#navigationBottom2").fadeOut(function() {
                    $("#navigationBottom1").fadeIn();
                });
                $("navigationBottom3").fadeOut(function() {
                    $("#navigationBottom1").fadeIn();
                });
                break;
            case 2:
                if(!navigationTop1.is(":visible")) {
                    navigationTop1.fadeIn();
                }
                if(navigationTop2.is(":visible")) {
                    navigationTop2.fadeOut();
                }
                if(!navigationBottomContainer.is(":visible")) {
                    navigationBottomContainer.fadeIn();
                }
				if (optionalConfigEnabled == true) {
	                $("#navigationBottom1").fadeOut(function() {
	                    $("#navigationBottom2").fadeIn();
	                });
	                $("navigationBottom3").fadeOut(function() {
	                    $("#navigationBottom2").fadeIn();
	                });
				}else{
					$("#navigationBottom1").fadeOut(function() {
						$("#navigationBottom3").fadeIn();
					});
				}
                break;
            case 3:
                if(!navigationTop1.is(":visible")) {
                    navigationTop1.fadeIn();
                }
                if(!navigationTop2.is(":visible")) {
                    navigationTop2.fadeIn();
                }
                if(navigationTop3.is(":visible")) {
                    navigationTop3.fadeOut();
                }
				if (optionalConfigEnabled == true) {
					if(!navigationBottomContainer.is(":visible")) {
	                    navigationBottomContainer.fadeIn();
	                }
	                $("#navigationBottom1").fadeOut(function() {
	                    $("#navigationBottom3").fadeIn();
	                });
	                $("#navigationBottom2").fadeOut(function() {
	                    $("#navigationBottom3").fadeIn();
	                });
				}else{
				    if(navigationBottomContainer.is(":visible")) {
						navigationBottomContainer.fadeOut();
					}
				}
                break;
            case 4:
                if(!navigationTop1.is(":visible")) {
                    navigationTop1.fadeIn();
                }
                if(!navigationTop2.is(":visible")) {
                    navigationTop2.fadeIn();
                }
                if(!navigationTop3.is(":visible")) {
                    navigationTop3.fadeIn();
                }
                if(navigationBottomContainer.is(":visible")) {
                    navigationBottomContainer.fadeOut();
                }
                break;
            }
            currentStep = step;
            updateNextSectionEnablement();
            toggleOptionalStep(optionalConfigEnabled);
        }
    };

    var updateStep1Summary = function() {
        var selectedTechnologies = $("#step1TechnologiesContainer .step1Technology.selected");
        var selectedTechnologiesText = "";
        for(var i = 0; i < selectedTechnologies.size(); i++) {
            selectedTechnologiesText += selectedTechnologies.get(i).dataset.technologyname;
            if(i + 1 < selectedTechnologies.size()) {
                selectedTechnologiesText += ", ";
            }
        }
        $("#navigationTop1 .variableContent").text(selectedTechnologiesText);
    };

    var updateSwaggerCodeGen = function() {
        var selectedTechnologies = $("#step1TechnologiesContainer .step1Technology.selected");
        var swagger = false;
        var rest = false;
        for(var i = 0; i < selectedTechnologies.size(); i++) {
            if(selectedTechnologies.get(i).dataset.technologyid == "swagger") {
				swagger = true;
            }else if(selectedTechnologies.get(i).dataset.technologyid == "rest") {
            	rest = true;
            }
        }
        if(swagger == true && rest == true){
        	$("#step3SwaggerCodeGenSection").removeClass("hidden");
        }else{
        	$("#step3SwaggerCodeGenSection").addClass("hidden");
        }
    };

    $("#step3SwaggerServerCodegen").on("click", function(event) {
        if (this.checked) {
        	$("#step3SwaggerLocalUpload").removeClass("hidden");
        	if($("#step3SelectFileButton").is(':visible')){
        		$("#step3SwaggerFileState").addClass("hidden");
        	}
        } else {
        	$("#step3SwaggerLocalUpload").addClass("hidden");
        }
    });

    var isSwaggerCodeGenerated = function() {
    	if($("#step3SwaggerLocalUpload").is(':visible') && uploadedSwaggerFile){
    		return true;
    	}
    	return false;
    };

    var refreshSectionVisibility = function() {
        var currentlyVisibleSections = $(".step:not(.hidden)").size();
        var selectedTechnologies = $("#step1TechnologiesContainer .step1Technology.selected");
        if(selectedTechnologies.size() == 0) {
            $("#step2, #step3, #step4").addClass("hidden");
        } else {
            $("#step2").removeClass("hidden");
            var selectedLocation = $("#step2DeployLocationsContainer .step2DeployLocation.selected").size() > 0;
            if(!selectedLocation) {
                $("#step3, #step4").addClass("hidden");
            } else {
            	updateSwaggerCodeGen();
                $("#step3, #step4").removeClass("hidden");
            }
        }
        var visibleSections = $(".step:not(.hidden)").size();
        if(currentlyVisibleSections != visibleSections) {
            if(currentStep == 1 && $.scrollify.isDisabled()) {
                window.setTimeout(function() {
                    $.scrollify.enable();
                }, 500);

            }
            $.scrollify.update();
            updateNextSectionEnablement();
        }
    };

    var updateNextSectionEnablement = function() {
    	var stepIncrement = 1;
    	if(currentStep == 2 && optionalConfigEnabled == false){
    		stepIncrement = 2;
    	}

        if($("#step" + (currentStep + stepIncrement)).hasClass("hidden")) {
            $("#navigationBottomContainer").addClass("disabled");
        } else {
            $("#navigationBottomContainer").removeClass("disabled");
        }
    };

    document.getElementById('step3SwaggerFileSelect').onchange = function() {
    	uploadedSwaggerFile = false;
    	swaggerUploadButton.value = 'Upload';
    	var files = swaggerFileSelect.files;
    	if(files.length > 0){
			$(".step3SwaggerFileStatus").html("Selected " + files[0].name);
        	$("#step3SelectFileButton").addClass("hidden");
        	$("#step3SwaggerUploadButton").removeClass("hidden");
        	$("#step3SwaggerFileState").removeClass("hidden");
        	$("#step3SelectDifferentFileButton").removeClass("hidden");
    	}else{
    		$(".step3SwaggerFileStatus").html("No file is selected");
        	$("#step3SelectFileButton").removeClass("hidden");
        	$("#step3SwaggerUploadButton").addClass("hidden");
        	$("#step3SwaggerFileState").removeClass("hidden");
        	$("#step3SelectDifferentFileButton").addClass("hidden");
    	}
    };

	var swaggerForm = document.getElementById('step3SwaggerFileForm');
	var swaggerFileSelect = document.getElementById('step3SwaggerFileSelect');
	var swaggerUploadButton = document.getElementById('step3SwaggerUploadButton');
	var uploadedSwaggerFile = false;
	swaggerForm.onsubmit = function(event) {
		uploadedSwaggerFile = false;
		event.preventDefault();
		// Update button text.
		swaggerUploadButton.value = 'Uploading...';
		var files = swaggerFileSelect.files;
		// Create a new FormData object.
		var formData = new FormData();
		var file = files[0];
		// Add the file to the request.
		formData.append('swaggerDefinition', file, file.name);
		// Set up the request.
		var xhr = new XMLHttpRequest();
		// Open the connection.
		xhr.open('POST', 'api/v1/upload?tech=swagger&cleanup=true&process=true&workspace=' + workspaceId, true);
		// Set up a handler for when the request finishes.
		xhr.onload = function () {
			swaggerUploadButton.value = 'Upload';
			if (xhr.status != 200) {
				$(".step3SwaggerFileStatus").html("An error occurred while generating server code from " + file.name + " : " + xhr.responseText);
				swaggerFileSelect.value = "";
			}else{
				$(".step3SwaggerFileStatus").html("Successfully generated server code from " + file.name);
				uploadedSwaggerFile = true;
			}
			$("#step3SwaggerUploadButton").addClass("hidden");
		};
		// Send the Data.
		xhr.send(formData);
	}

    var retrieveTechnologiesFromServer = function() {
        return $.ajax({
            url: serviceURL + "/tech",
            success: function(response) {
                technologies = response;
            }
        });
    };

    var retrieveWorkspaceId = function() {
        return $.ajax({
            url: serviceURL + "/workspace",
            success: function(response) {
                workspaceId = response;
            }
        });
    };

    var submitRequest = function() {

        // Selected technologies
        var selectedTechnologies = $("#step1TechnologiesContainer .step1Technology.selected");
        var url = serviceURL + "/data?tech=";
        for(var i = 0; i < selectedTechnologies.size(); i++) {
            url += selectedTechnologies.get(i).dataset.technologyid;
            if(i + 1 < selectedTechnologies.size()) {
                url += "&tech=";
            }
        }

        // Deploy location
        var deployLocation = $("#step2DeployLocationsContainer .step2DeployLocation.selected").data("value");
        url += "&deploy=" + deployLocation;

        // Project name
        var projectName = $("#step4NameInput").val();
        if(projectName != "") {
            url += "&name=" + projectName;
        } else {
            url += "&name=" + "libertyProject";
        }
		url += "&workspace=" + workspaceId;
		if(isSwaggerCodeGenerated()){
			url += "&techoptions=swagger:server";
		}
        window.location.assign(url);
    };

    var trackOutboundLink = function(url, linkLocation) {
        if (googleAnalytics) {
            // Google analytics event
            ga('send', 'event', 'Outbound Link', url, linkLocation)
        }
    };

    $("#step1TechnologiesContainer").on("click", ".step1Technology", function(event) {
        event.preventDefault();
        $(event.currentTarget).toggleClass("selected");
        updateStep1Summary();
        updateStepNumbers();
        refreshSectionVisibility();
    });

    $("#step2DeployLocationsContainer .step2DeployLocation").on("click", function(event) {
        event.preventDefault();
        $("#step2DeployLocationsContainer .step2DeployLocation").removeClass("selected");
        $(event.currentTarget).addClass("selected");
        $("#navigationTop2 .variableContent").text(event.currentTarget.dataset.text);
        if(event.currentTarget.dataset.value == "local") {
            $("#step4Bluemix").addClass("hidden");
            $("#step4Local").removeClass("hidden");
        } else {
            $("#step4Local").addClass("hidden");
            $("#step4Bluemix").removeClass("hidden");
        }

        refreshSectionVisibility();
    });

    $("#navigationBottomContainer").click(function(event) {
        event.preventDefault();
        if(!$("#navigationBottomContainer").hasClass("disabled")) {
            $.scrollify.next();
        }
    });

    $(".navigationTop").click(function(event) {
        event.preventDefault();
        $.scrollify.move(Number(event.currentTarget.dataset.targetsection));
    });

    $(window).scroll(function() {
        updateSteps();
    });

    $("#step4DownloadButton").click(function(event) {
        event.preventDefault();
        if (googleAnalytics) {
            // Google analytics
            ga('send', 'event', 'Downloads', 'button-clicked');
        }
        submitRequest();
    });

    $("#helpUsToHelpYouGetInTouchButton").click(function(event) {
        event.preventDefault();
        window.open("https://github.com/WASdev/tool.accelerate.core/issues/", "_blank");
    });

    $("#gitHubLink").click(function(event) {
        event.preventDefault();
        window.open("https://github.com/WASdev/tool.accelerate.core/", "_blank");
    });

    $("#step4NameInput").on("keypress", function(event) {
        if(!(/[a-z0-9]/i.test(event.key) || event.key == "-" || event.key == "_")) {
            event.preventDefault();
        }
    });

    $("#step4OptionalWDTTrigger a").click(function(event){
        event.preventDefault();

        if (googleAnalytics) {
            // Google analytics
            ga('send', 'event', 'Page assistance', 'clicked', 'WDT Section');
        }

        var container = $(this).parent().parent();
        var headerSection = container.find("#step4OptionalWDTTrigger");
        var openArrow = container.find(".wdtContentActive");
        var closeArrow = container.find(".wdtContentDeactive");
        var contentSection = container.find("#step4OptionalWDTContent");

        if ( contentSection.hasClass("hidden") ) {
            openArrow.addClass("hidden");
            closeArrow.removeClass("hidden");
            contentSection.removeClass("hidden");
        } else {
            openArrow.removeClass("hidden");
            closeArrow.addClass("hidden");
            contentSection.addClass("hidden");
        }

        // Update Scrollify
        // The .update() method recalculates the height of the 'section'
        // We need this method to be called because when you initially get
        // to the 'Download' screen the height does not consider the hidden
        // steps found in the 'Optional WDT' Section. This method will
        // recalculate the height after the content is displayed.
        $.scrollify.update();
    });

    var changedAppName = $("#step4NameInput");
    var reflectChangedAppName = $(".step4NameOfProject");
    $("#step4NameInput").on("input", function(event) {
        var value = $(this).val();
        reflectChangedAppName.text(value);
    });
/*
    retrieveTechnologiesFromServer().done(function() {
    	retrieveWorkspaceId();
        populateTechnologies();

        $("#navigationTopContainer, #mainContent, #navigationBottomContainer").removeClass("hidden");

        $.scrollify({
            section: ".step:not(.hidden)"
        });


        $.scrollify.instantMove(0);
        $.scrollify.disable();

    }).fail(function() {
        $("#serviceError").fadeIn();
    });
*/
});
