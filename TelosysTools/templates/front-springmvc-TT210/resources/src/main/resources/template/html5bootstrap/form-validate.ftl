<#if parameters.validate?default(false) == true>
	<script type="text/javascript" src="${base}/struts/html5bootstrap/validation.js"></script>
	<script type="text/javascript" src="${base}/struts/utils.js"></script>
	<#if parameters.onsubmit??>
		${tag.addParameter('onsubmit', "${parameters.onsubmit}; return validateForm_${parameters.id?replace('[^a-zA-Z0-9_]', '_', 'r')}();")}
	<#else>
		${tag.addParameter('onsubmit', "return validateForm_${parameters.id?replace('[^a-zA-Z0-9_]', '_', 'r')}();")}
	</#if>
</#if>
