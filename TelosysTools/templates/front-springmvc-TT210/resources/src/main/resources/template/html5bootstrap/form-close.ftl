<#include "/${parameters.templateDir}/html5bootstrap/control-close.ftl" />
<#include "/${parameters.templateDir}/simple/form-close.ftl" />
<#include "/${parameters.templateDir}/html5bootstrap/form-close-validate.ftl" />
<#if parameters.focusElement?if_exists != "">
<script type="text/javascript">
    StrutsUtils.addOnLoad(function() {
        var element = document.getElementById("${parameters.focusElement?html}");
        if(element) {
            element.focus();
        }
    });
</script>
</#if>
