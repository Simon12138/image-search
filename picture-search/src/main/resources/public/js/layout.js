$(function() {
	$('#import-pictures').click(function() {
    	$('#import-pictures-modal').modal('show');
    });
    $('#import-pictures-modal').on('hidden.bs.modal', function (e) {
    	$(this).removeData("bs.modal");
    	$("#file-input").val('');
    	$("#label-input").val('');
    });
    
    $('#start-import').click(function() {
    	import_images();
    });
    
    $('#define-person').click(function() {
    	$('#predeined-modal').modal('show');
    });
    
    $("#create-person").click(function() {
    	create_person();
    });
    
    function create_person() {
    	var formData = new FormData();
    	var image = $("#person-image")[0].files[0];
    	var name = JSON.stringify($('#person-name').val());
    	formData.append("image", image);
        formData.append("name",name);
    	
    	$.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "/images/person/create",
            data: formData,
            headers: {'Content-Type': undefined},
            processData: false,
    		contentType: false,
    		cache: false,
            timeout: 600000,
            success: function (data) {
            	alert("Success");
            },
            error: function (e) {
            }
        });
    };
    
    function import_images() {
    	var form = $('#images-chose')[0];
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
    			$('#import-pictures-modal').modal('hide');
    			window.location.href = "/";
    		}
    	});
    };
    
    $(document).on('change', ':file', function() {
        var input = $(this);
        var images = input.get(0).files;
        var numFiles = images ? images.length : 1;
        var label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
        input.trigger('fileselect', [numFiles, label]);
    });
    $(document).ready( function() {
    	$(':file').on('fileselect', function(event, numFiles, label) {
    		var input = $(this).parents('.input-group').find(':text'),
    		log = numFiles > 1 ? numFiles + ' files selected' : label;
    		if( input.length ) {
    			input.val(log);
    		} else {
    			if( log ) alert(log);
    		}
    	});
    });
})