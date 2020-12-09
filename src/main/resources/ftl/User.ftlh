<#ftl encoding='UTF-8'>
<html lang="en">
<body>
<form method="${formAnnotationAttr.method}" action="${formAnnotationAttr.action}">
    <#list formInnerLines as lines>
    <input type="${lines.type}" name="${lines.name}" placeholder="${lines.placeholder}">
    </#list>
</form>
</body>
</html>