$(document).ready(function() {
	
	var timeOut = 200;
	var timeoutID = 0;
	var ignoreSingleClicks = false;
	
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
	
	$('.image.avatars').each(function(index){
		$(this).dblclick(function(event) {
			clearTimeout(timeoutID);
		    ignoreSingleClicks = true;
		    setTimeout(function() {
		      ignoreSingleClicks = false;
		    }, timeOut);
		    
			$('.modal.images').modal('show');
			var avatarUrl = $(this)[0].src;
			var id = getIdFromUrl(avatarUrl);
			var imagesContainer = $('.modal.images > .content > .images');
			var descriptionContainer = $('.modal.images > .description'); 
			imagesContainer.empty();
			descriptionContainer.empty();
			$.ajax({
				type: 'GET',
				url: '/avatars/' + id + '/images',
				datatype: 'json',
				success: function(data) {
					var _pictures = data;
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
		    
			$('.modal.images').modal('show');
			var objectUrl = $(this)[0].src;
			var id = getIdFromUrl(objectUrl);
			var imagesContainer = $('.modal.images > .content > .images');
			var descriptionContainer = $('.modal.images > .description'); 
			imagesContainer.empty();
			descriptionContainer.empty();
			$.ajax({
				type: 'GET',
				url: '/objects/' + id + '/images',
				datatype: 'json',
				success: function(data) {
					var _pictures = data;
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
		    
			$('.modal.images').modal('show');
			var objectUrl = $(this)[0].src;
			var id = getIdFromUrl(objectUrl);
			var imagesContainer = $('.modal.images > .content > .images');
			var descriptionContainer = $('.modal.images > .description'); 
			imagesContainer.empty();
			descriptionContainer.empty();
			$.ajax({
				type: 'GET',
				url: '/locations/' + id + '/images',
				datatype: 'json',
				success: function(data) {
					var _pictures = data;
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
					$('.recognization > .cue > .person')[0].src = url;
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
					$('.recognization > .cue > .object')[0].src = url;
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
					$('.recognization > .cue > .location')[0].src = url;
			    }, timeOut);
			}
		});
	});
	
	$('.modal.images > .actions > .pre').click(function() {
		var current = $('.modal.images > .content > .images > img:not([style="display: none;"])').index();
		var images = $('.modal.images > .content > .images >.image');
		var descriptions = $('.modal.images > .description > div');
		if(current === 0) {
			current = images.length;
		}
		images.eq(current - 1).show();
		images.eq(current - 1).siblings().hide();
		descriptions.eq(current - 1).show();
		descriptions.eq(current - 1).siblings().hide();
	});
	
	$('.modal.images > .actions > .next').click(function() {
		var current = $('.modal.images > .content > .images > img:not([style="display: none;"])').index();
		var images = $('.modal.images > .content > .images >.image');
		var descriptions = $('.modal.images > .description > div');
		if(current === images.length - 1) {
			current = -1;
		}
		images.eq(current + 1).show();
		images.eq(current + 1).siblings().hide();
		descriptions.eq(current + 1).show();
		descriptions.eq(current + 1).siblings().hide();
	});
	
	function getIdFromUrl(url) {
		return url.substring(url.lastIndexOf('/') + 1);
	};
});