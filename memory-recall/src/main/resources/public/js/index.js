$(document).ready(function() {
	
	var timeOut = 200;
	var timeoutID = 0;
	var ignoreSingleClicks = false;
	
	var  creationTime = null;
	
	$("input:text").click(function() {
		$(this).parent().find("input:file").click();
	});
	
	$('input:file', '.input.import').on('change', function(e) {
		var fileNum = e.target.files.length;
		$('input:text', $(e.target).parent()).val("You have selected " + fileNum + " image(s).");
	});
	
	$('.button.import').click(function(event) {
		var form = $('.input.import').eq(0).eq(0)[0];
		var data = new FormData(form);
		$.ajax({
			type: 'POST',
    		enctype: 'multipart/form-data',
    		url: '/images/upload',
    		data: data,
    		processData: false,
    		contentType: false,
    		cache: false,
    		timeout: 600000,
    		success: function(data) {
    			window.location.href = "/";
    		}
		});
	});
	
	$('.cue.personcue').click(function(event) {
		$('.recognization > .cue.personcue').empty();
	});
	
	$('.cue.objectcue').click(function(event) {
		$('.recognization > .cue.objectcue').empty();
	});
	
	$('.cue.locationcue').click(function(event) {
		$('.recognization > .cue.locationcue').empty();
	});
	
	$('.image.avatars').each(function(index){
		$(this).dblclick(function(event) {
			clearTimeout(timeoutID);
		    ignoreSingleClicks = true;
		    setTimeout(function() {
		      ignoreSingleClicks = false;
		    }, timeOut);
			var avatarUrl = $(this)[0].src;
			var id = getIdFromUrl(avatarUrl);
			$.ajax({
				type: 'GET',
				url: '/avatars/' + id + '/images',
				datatype: 'json',
				success: function(data) {
					var _pictures = data;
					if(_pictures === undefined || _pictures.length === 0) {
						$('.modal.error > .content').text('There are no results for you!');
						$('.modal.error').modal({
							onHide: function(value) {
								resetCues();
							}
						}).modal('show');
						return;
					}
					$('.modal.images').modal({
						onHide: function(value) {
							$('.modal.images > .actions > .pre').css('visibility', 'hidden');
						}
					}).modal('show');
					var imagesContainer = $('.modal.images > .content > .images');
					var descriptionContainer = $('.modal.images > .description'); 
					imagesContainer.empty();
					descriptionContainer.empty();
					$('.modal.images > .actions > .pre').css('visibility', 'hidden');
					if(_pictures.length == 1) {
						$('.modal.images > .actions > .next').text('Close');
					} else {
						$('.modal.images > .actions > .next').text('Next');
					}
					for(var i = 0; i < _pictures.length; i++) {
						imagesContainer.append('<img class="ui image card" src="/images/download/' + _pictures[i].id + 
								'"></img>');
						descriptionContainer.append('<div class="ui label datetime">' + _pictures[i].creationTimeString + '</p>');
					}
					var images = $('.modal.images > .content > .images > .image');
					var descriptions = $('.modal.images > .description > div')
					images.eq(0).show();
					images.eq(0).siblings().hide();
					descriptions.eq(0).show();
					descriptions.eq(0).siblings().hide();
				}
			});
		});
	});
	
	$('.image.objects').each(function(index){
		$(this).dblclick(function(event) {
			clearTimeout(timeoutID);
		    ignoreSingleClicks = true;
		    setTimeout(function() {
		      ignoreSingleClicks = false;
		    }, timeOut);
			var objectUrl = $(this)[0].alt;
			var id = getIdFromUrl(objectUrl);
			$.ajax({
				type: 'GET',
				url: '/objects/' + id + '/images',
				datatype: 'json',
				success: function(data) {
					var _pictures = data;
					if(_pictures === undefined || _pictures.length === 0) {
						$('.modal.error > .content').text('There are no results for you!');
						$('.modal.error').modal({
							onHide: function(value) {
								resetCues();
							}
						}).modal('show');
						return;
					}
				    $('.modal.images').modal({
						onHide: function(value) {
							$('.modal.images > .actions > .pre').css('visibility', 'hidden');
						}
					}).modal('show');
					var imagesContainer = $('.modal.images > .content > .images');
					var descriptionContainer = $('.modal.images > .description'); 
					imagesContainer.empty();
					descriptionContainer.empty();
					$('.modal.images > .actions > .pre').css('visibility', 'hidden');
					if(_pictures.length == 1) {
						$('.modal.images > .actions > .next').text('Close');
					} else {
						$('.modal.images > .actions > .next').text('Next');
					}
					for(var i = 0; i < _pictures.length; i++) {
						imagesContainer.append('<img class="ui image card" src="/images/download/' + _pictures[i].id + 
								'"></img>');
						descriptionContainer.append('<div class="ui label datetime">' + _pictures[i].creationTimeString + '</p>');
					}
					var images = $('.modal.images > .content > .images > .image');
					var descriptions = $('.modal.images > .description > div')
					images.eq(0).show();
					images.eq(0).siblings().hide();
					descriptions.eq(0).show();
					descriptions.eq(0).siblings().hide();
				}
			});
		});
	});
	
	$('.image.locations').each(function(index){
		$(this).dblclick(function(event) {
			clearTimeout(timeoutID);
		    ignoreSingleClicks = true;
		    setTimeout(function() {
		      ignoreSingleClicks = false;
		    }, timeOut);
			var locationUrl = $(this)[0].src;
			var id = getIdFromUrl(locationUrl);
			$.ajax({
				type: 'GET',
				url: '/locations/' + id + '/images',
				datatype: 'json',
				success: function(data) {
					var _pictures = data;
					if(_pictures === undefined || _pictures.length === 0) {
						$('.modal.error > .content').text('There are no results for you!');
						$('.modal.error').modal({
							onHide: function(value) {
								resetCues();
							}
						}).modal('show');
						return;
					}
				    $('.modal.images').modal({
						onHide: function(value) {
							$('.modal.images > .actions > .pre').css('visibility', 'hidden');
						}
					}).modal('show');
					var imagesContainer = $('.modal.images > .content > .images');
					var descriptionContainer = $('.modal.images > .description'); 
					imagesContainer.empty();
					descriptionContainer.empty();
					$('.modal.images > .actions > .pre').css('visibility', 'hidden');
					if(_pictures.length == 1) {
						$('.modal.images > .actions > .next').text('Close');
					} else {
						$('.modal.images > .actions > .next').text('Next');
					}
					for(var i = 0; i < _pictures.length; i++) {
						imagesContainer.append('<img class="ui image card" src="/images/download/' + _pictures[i].id + 
								'"></img>');
						descriptionContainer.append('<div class="ui label datetime">' + _pictures[i].creationTimeString + '</p>');
					}
					var images = $('.modal.images > .content > .images > .image');
					var descriptions = $('.modal.images > .description > div')
					images.eq(0).show();
					images.eq(0).siblings().hide();
					descriptions.eq(0).show();
					descriptions.eq(0).siblings().hide();
				}
			});
		});
	});
	
	$('.image.avatars').each(function(index){
		$(this).click(function() {
			var that = this;
			if (!ignoreSingleClicks) {
				clearTimeout(timeoutID);
			    timeoutID = setTimeout(function() {
			    	var url = $(that)[0].src;
			    	$('.recognization > .cue.personcue').empty();
			    	$('.recognization > .cue.personcue').append('<img class="ui tiny rounded image person" src="' + url + '"></img>');
			    }, timeOut);
			}
		});
	});
	
	$('.image.objects').each(function(index){
		$(this).click(function() {
			var that = this;
			if (!ignoreSingleClicks) {
				clearTimeout(timeoutID);
			    timeoutID = setTimeout(function() {
			    	var url = $(that)[0].src;
			    	var alt = $(that)[0].alt;
			    	$('.recognization > .cue.objectcue').empty();
			    	$('.recognization > .cue.objectcue').append('<img class="ui tiny rounded image object" src="' + 
			    			url + '" alt="' + alt + '"></img>');
			    }, timeOut);
			}
		});
	});
	
	$('.image.locations').each(function(index){
		$(this).click(function() {
			var that = this;
			if (!ignoreSingleClicks) {
				clearTimeout(timeoutID);
			    timeoutID = setTimeout(function() {
			    	var url = $(that)[0].src;
			    	$('.recognization > .cue.locationcue').empty();
			    	$('.recognization > .cue.locationcue').append('<img class="ui tiny rounded image location" src="' + url + '"></img>');
			    }, timeOut);
			}
		});
	});
	
	$('.thirteen.creationtime > .buttons > .button').each(function(index) {
		$(this).click(function() {
			var that = this;
			if($(that).attr('class').indexOf('blue') != -1) {
				$('.thirteen.creationtime > .buttons > .button').removeClass('blue');
				$(that).removeClass('blue');
				creationTime = null;
			} else {
				$('.thirteen.creationtime > .buttons > .button').removeClass('blue');
				$(that).addClass('blue');
				creationTime = Number($(that).attr('id'));
			}
		});
	});
	
	$('.modal.images > .actions > .pre').click(function() {
		var current = $('.modal.images > .content > .images > img:not([style="display: none;"])').index();
		var images = $('.modal.images > .content > .images >.image');
		var descriptions = $('.modal.images > .description > div');
		if(current == 1) {
			$('.modal.images > .actions > .pre').css('visibility', 'hidden');
		} else {
			$('.modal.images > .actions > .pre').css('visibility', 'visible');
		}
		$('.modal.images > .actions > .next').text('Next');
		images.eq(current - 1).show();
		images.eq(current - 1).siblings().hide();
		descriptions.eq(current - 1).show();
		descriptions.eq(current - 1).siblings().hide();
	});
	
	$('.modal.images > .actions > .next').click(function() {
		var current = $('.modal.images > .content > .images > img:not([style="display: none;"])').index();
		var images = $('.modal.images > .content > .images >.image');
		var descriptions = $('.modal.images > .description > div');
		$('.modal.images > .actions > .pre').css('visibility', 'visible');
		if(current >= images.length - 2) {
			$('.modal.images > .actions > .next').text('Close');
		} else {
			$('.modal.images > .actions > .next').text('Next');
		}
		if(current === images.length - 1) {
			$('.modal.images').modal('hide');
		}
		images.eq(current + 1).show();
		images.eq(current + 1).siblings().hide();
		descriptions.eq(current + 1).show();
		descriptions.eq(current + 1).siblings().hide();
	});
	
	function getIdFromUrl(url) {
		return url.substring(url.lastIndexOf('/') + 1);
	};
	
	$('.button.query').click(function() {
		var faceImage = $('.image.cue > .image.person');
		var faceId = faceImage.length === 0 ? null : getIdFromUrl(faceImage[0].src);
		var objectImage = $('.image.cue > .image.object');
		var objectId = objectImage.length === 0 ? null : getIdFromUrl(objectImage[0].alt);
		var locationImage = $('.image.cue > .image.location');
		var locationId = locationImage.length === 0 ? null : getIdFromUrl(locationImage[0].src);
		if(faceImage.length === 0 && objectImage.length === 0 && locationImage.length === 0) {
			$('.modal.error > .content').text('You must choose a cue to query.');
			$('.modal.error').modal({
				onHide: function(value) {
					resetCues();
				}
			}).modal('show');
			return;
		}
		var hour = creationTime;
		var startHour = null;
		var endHour = null;
		if(hour !== null) {
			startHour = Number(hour) - 0.5;
			endHour = Number(hour) + 0.5;
		}
		if(startHour !== null && endHour !== null && startHour > endHour) {
			alert('End Time must be larger than or equal with Start Time.')
			return;
		}
		var data = {"faceId": faceId, "objectId": objectId, "locationId": locationId, "startHour": startHour, "endHour": endHour};
		$.ajax({
			url: '/images/query',
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(data),
			type: 'POST',
			dataType: 'json',
			success: function(data) {
				var _pictures = data;
				if(_pictures === undefined || _pictures.length === 0) {
					$('.modal.error > .content').text('There are no results for you! Please try other combination.');
					$('.modal.error').modal({
						onHide: function(value) {
							resetCues();
						}
					}).modal('show');
					return;
				}
				$('.modal.error > .content').text('There are no results for you! Please try other combination.');
				$('.modal.images').modal({
					onHide: function(value) {
						resetCues();
					}
				}).modal('show');
				$('.modal.images > .actions > .pre').css('visibility', 'hidden');
				if(_pictures.length == 1) {
					$('.modal.images > .actions > .next').text('Close');
				} else {
					$('.modal.images > .actions > .next').text('Next');
				}
				var imagesContainer = $('.modal.images > .content > .images');
				var descriptionContainer = $('.modal.images > .description'); 
				imagesContainer.empty();
				descriptionContainer.empty();
				for(var i = 0; i < _pictures.length; i++) {
					imagesContainer.append('<img class="ui image card" src="/images/download/' + _pictures[i].id + '"></img>');
					descriptionContainer.append('<div class="ui label datetime">' + _pictures[i].creationTimeString + '</p>');
				}
				var images = $('.modal.images > .content > .images > .image');
				var descriptions = $('.modal.images > .description > div')
				images.eq(0).show();
				images.eq(0).siblings().hide();
				descriptions.eq(0).show();
				descriptions.eq(0).siblings().hide();
			}
		});
	});
	
	$('.ui.selection.dropdown').dropdown({
	    useLabels: true
	});
	
	function resetCues() {
		$('.recognization > .cue.personcue').empty();
		$('.recognization > .cue.objectcue').empty();
		$('.recognization > .cue.locationcue').empty();
		$('.modal.images > .actions > .pre').css('visibility', 'hidden');
		$('.thirteen.creationtime > .buttons > .button').removeClass('blue');
		creationTime = null;
	};
});