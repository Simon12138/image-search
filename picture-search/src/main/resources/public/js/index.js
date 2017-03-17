$(document).ready(function() {
	$('#cues-selection').click(function() {
    	$('.dropdown-toggle').dropdown('toggle');
    });
    
    $('#specific-search').click(function() {
    	$('#specific-search-modal').modal('show');
    });
    $('#specific-search-modal').on('hidden.bs.modal', function (e) {
    	$(this).removeData("bs.modal");
    });
    $('.carousel').carousel({
		  interval: 5000
	});
    $('#myCarousel').on('slid.bs.carousel', function () {
    	var imagesmodals = $('.picture-detail-label');
    	for(var i = 0; i < imagesmodals.length; i++) {
    		if(i === $('#myCarousel').find('.item.active').index()) {
    			$('.picture-detail-label').eq(i).removeClass('hidden').addClass('show');
    		} else {
    			$('.picture-detail-label').eq(i).removeClass('show').addClass('hidden');
    		}
    	}
    });
    
    $('.all-images').each(function(index) {
    	$(this).click(function() {
    		var images = $(".all-images");
    		$('.carousel-inner').empty();
    		var imgSrcs = images.find('#images-box');
    		if($('.image-detail-container').find('.picture-detail-label').length != 0) {
    			$('.image-detail-container').children('.picture-detail-label').remove();
    		}
    		for(var i = 0; i < images.length; i++) {
    			$('.carousel-inner').append('<div class="item"><img id="modal-image-details" src="'+ images.find('#images-box').get(i).src + '"></img></div>');
    			$('.image-detail-container').append('<div class="picture-detail-label caption hidden"></div>');
    			
    		}
    		var image_details_containers = $('.picture-detail-label');
    		var async_requests = [];
    		var datas = [];
    		for(var i = 0; i < image_details_containers.length; i++) {
    			var imgSrc = imgSrcs.get(i).src;
    			var imgId = imgSrc.substring(imgSrc.lastIndexOf('/') + 1, imgSrc.length);
    			async_requests.push($.ajax({
    				type: "POST",
    	    		contentType: "application/json",
    	    		url: "/pictures/" + imgId + "/tags",
    	    		cache: false,
    	    		timeout: 60000,
    	    		success: function(data) {
    	    			datas.push(data);
    	    		}
    			}));
    		}
    		$.when.apply(null, async_requests).done( function(){
    			for(var i = 0; i < datas.length; i++) {
    				for(var j = 0; j < datas[i].content.length; j++) {
    					image_details_containers.eq(i).append('<span class="label label-primary">' + datas[i].content[j].description + '</span>')
    				}
    			}
    		});
    		$('.carousel-inner').find('.item').get(index).className += ' active';
    		$('.picture-detail-label').eq(index).removeClass('hidden').addClass('show');
        	$('#image-details-modal').modal('show');
    	});
    });
    
    $('#search').click(function() {
    	var key = $('.key-search').val();
    	searchPictures(key);
    });
    
    function bindImageClickEvent() {
    	$('.all-images').each(function(index) {
        	$(this).click(function() {
        		var images = $(".all-images");
        		$('.carousel-inner').empty();
        		var imgSrcs = images.find('#images-box');
        		if($('.image-detail-container').find('.picture-detail-label').length != 0) {
        			$('.image-detail-container').find('.picture-detail-label').empty();
        		}
        		for(var i = 0; i < images.length; i++) {
        			$('.carousel-inner').append('<div class="item"><img id="modal-image-details" src="'+ images.find('#images-box').get(i).src + '"></img></div>');
        			$('.image-detail-container').append('<div class="picture-detail-label caption"></div>');
        		}
        		var image_details_containers = $('.picture-detail-label');
        		var async_requests = [];
        		var datas = [];
        		for(var i = 0; i < image_details_containers.length; i++) {
        			/*
        				var imgSrc = imgSrcs.get(i).src;
            			var imgId = imgSrc.substring(imgSrc.lastIndexOf('/') + 1, imgSrc.length);
            			$.ajax({
            				type: "POST",
            	    		contentType: "application/json",
            	    		url: "/pictures/" + imgId + "/tags",
            	    		cache: false,
            	    		timeout: 60000,
            	    		success: function(data) {
            	    			var tags = data.content;
            	    			for(var j = 0; j < tags.length; j++) {
            	    				image_details_containers.eq(i).append('<span class="label label-primary">' + tags[j].description + '</span>');
            	    			}
            	    		}
            			});
            			*/
        			async_request.push($.ajax({
        				type: "POST",
        	    		contentType: "application/json",
        	    		url: "/pictures/" + imgId + "/tags",
        	    		cache: false,
        	    		timeout: 60000,
        	    		success: function(data) {
        	    			datas.push(data);
        	    		}
        			}));
        		}
        		$.when.apply(null, async_request).done( function(){
        			for(var i = 0; i < datas.length; i++) {
        				for(var j = 0; j < datas[i].tags.length; j++) {
        					image_details_containers.eq(i).append('<span class="label label-primary">' + datas[i].tags[j].description + '</span>')
        				}
        			}
        		});
        		
        		$('.carousel-inner').find('.item').get(index).className += ' active';
            	$('#image-details-modal').modal('show');
        	});
        });
    };
    
    function searchPictures(key) {
    	$.ajax({
    		type: "POST",
    		contentType: "application/json",
    		url: "/pictures/search/" + key,
    		cache: false,
    		timeout: 60000,
    		success: function(data) {
    			var image_container = $('.images-container');
    			image_container.empty();
    			var datedPictures = data.content;
    			for(var i=0; i < datedPictures.length; i++) {
    				var datedPicture = datedPictures[i];
    				image_container.append('<div class="images-container-row row"></div>');
    				var image_container_row = $('.images-container-row');
    				image_container_row.append('<h3 id="group-time">After ' + $.format.date(datedPicture.groupTime, "yyyy-MM-dd HH:mm") +'</h3>');
    				var pictures = datedPicture.pictures;
    				for(var i = 0; i < pictures.length; i++) {
    					image_container_row.append('<div class="col-md-2 col-sm-3 col-xs-5 all-images"></div>');
    					var image_box = $('.all-images').eq(i);
    					image_box.append('<a id="image-details" href="javascript:void(0);" class="thumbnail"><img id="images-box" src="/images/download/'
    							+ pictures[i].id+ '"/><div class="picture-label caption">'
    							+ '<span class="label label-primary">Book</span>'
    							+ '<span class="label label-success">Huang Simon</span>'
    							+ '<span class="label label-info">Reading</span></div></a>')
    				}
    			}
    			bindImageClickEvent();
    		},
    		error: function(e) {
    			
    		}
    	})
    };
});