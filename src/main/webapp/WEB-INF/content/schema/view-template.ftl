<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.1 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Admin</title>
    <#include "/includes/header.ftl"/>
    <#import "/macros/body.ftl" as body />
    </head>
    
    <@body.body>
<h1>Editing: ${schema.name} Templates</h1>
 <div class="col-sm-12">
    <form method="POST" action="${contextPath}/a/schema/${schema.name}" enctype="multipart/form-data" class="form-horizontal">
		<input type="hidden" name="id" value="${schema.id?c}" />
		<div class="form-group">
	        <label for="titleTemplate" class="control-label">Title Template:</label>
	        <textarea id="titleTemplate" name="titleTemplate" class="form-control">${schema.titleTemplate!''}</textarea>
		</div>
		<div class="form-group">
	        <label for="resultTemplate" class="control-label">Results Template:</label>
	        <textarea id="resultTemplate" name="resultTemplate" class="form-control">${schema.resultTemplate!''}</textarea>
		</div>
        <input type="submit" value="Save" class="button btn btn-primary"> 

    </form>
    </div>

</@body.body>
</html>