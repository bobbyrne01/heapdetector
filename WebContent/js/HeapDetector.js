$(function() {
	
	$("#targetsDiv").hide();
	$("#settingsDiv").hide();

	$("li").click(function() {
		
		$("#heapdumpsDiv").hide();
		$("#targetsDiv").hide();
		$("#settingsDiv").hide();
		
		if ($(this).attr("id") == "heapdumps" || $(this).attr("id") == "targets"){
			
			HeapDetector_AJAX.getContent($(this).attr("id"));
			
		}else if ($(this).attr("id") == "settings"){
			
			HeapDetector_AJAX.getSettings("targets");
			HeapDetector_AJAX.getSettings("recipients");
			HeapDetector_AJAX.getSettings("schedule");
			
			$("#settingsDiv").show();
		}
	});
	
	$("#heapdumps").click();
});	

var HeapDetector_AJAX = {
		
	getContent: function(operation){
		
		var xmlhttp = new XMLHttpRequest();
		
		xmlhttp.onreadystatechange = callback;
		xmlhttp.open("GET", "Utils?operation=" + operation, true);
		xmlhttp.send(null);
		
		function callback() {

			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				
				$("#" + operation + "Div").html(xmlhttp.responseText);
				$("#" + operation + "Div").show();
			    
			} else {
			    // have not recieved data yet
				$("#" + operation + "Div").html("Loading ..");
				$("#" + operation + "Div").show();
			}
		}
	},
	
	getSettings: function(operation){
		
		var xmlhttp = new XMLHttpRequest();
		
		xmlhttp.onreadystatechange = callback;
		xmlhttp.open("GET", "Settings?operation=" + operation, true);
		xmlhttp.send(null);
		
		function callback() {

			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				
				$("#HeapDetectorSave").html("Save");
				$("#HeapDetectorSave").attr("disabled", true);
				
				if (operation == "targets"){
					
					$("#HeapDetectorTargets").val(xmlhttp.responseText);
					
				}else if (operation == "recipients"){
					
					$("#HeapDetectorRecipients").val(xmlhttp.responseText);
					
				}else if (operation == "schedule"){
					
					$("#HeapDetectorSchedule").val(xmlhttp.responseText);
				} 
			    
			} else {
				// have not recieved data yet
				$("#HeapDetectorSave").html("Loading settings ..");
				$("#HeapDetectorSave").attr("disabled", true);
			}
		}
	},
	
	updateSettings: function(){
		
		function revertButtonText(){
			$("#HeapDetectorSave").html("Save");
			$("#HeapDetectorSave").attr("disabled", false);		
		}
		
		var xmlhttp = new XMLHttpRequest();
		
		xmlhttp.onreadystatechange = callback;
		xmlhttp.open("POST","Settings",true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.send("targets=" + $("#HeapDetectorTargets").attr('value') + "&recipients=" + $("#HeapDetectorRecipients").attr('value') + "&schedule=" + $("#HeapDetectorSchedule").attr('value'));
		
		function callback() {

			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				
				$("#HeapDetectorSave").html("Settings updated!");
				$("#HeapDetectorSave").attr("disabled", true);
				
				setTimeout(revertButtonText,6000);
			    
			} else {
			    // have not recieved data yet
				$("#HeapDetectorSave").html("Saving ..");
				$("#HeapDetectorSave").attr("disabled", true);
			}
		}
	}
};