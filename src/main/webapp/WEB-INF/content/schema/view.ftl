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
<h1>Editing: ${schema.name}</h1>
<#if admin>
<a href="${contextPath}/a/admin/schema/template/${schema.id?c}">Edit Templates</a>
</#if>
 <div class="col-sm-12">
    <form method="POST" action="${contextPath}/a/schema/${schema.id?c}" enctype="multipart/form-data" class="form-horizontal">
		<input type="hidden" name="id" value="${schema.id?c}" />
		<div class="form-group">
	        <label for="schemaName" class="control-label">Display Name:</label>
	        <input type="text" id="schemaName" name="displayName" value="${schema.displayName}" class="form-control"/>
		</div>
        <div class="form-group">
            <label for="schemaUrl" class="control-label">Link to the Data Source:</label>
            <input type="text" id="schemaUrl" name="url" value="${schema.url!''}" class="form-control"/>
        </div>
        <div class="form-group">
            <label for="schemaUrl" class="control-label">Image Url for Data Source:</label>
            <input type="text" id="schemaLogoUrl" name="logoUrl" value="${schema.logoUrl!''}" class="form-control"/>
        </div>
		<div class="form-group">
	        <label for="schemaCategory" class="control-label">Category:</label>
	        
	        <select id="schemaCategory" name="category" class="form-control">
	           <option value=""></option>
	           <#list categories![] as category>
	               <option value="${category}" <#if category == schema.category!''>selected</#if>>${category}</option>
	           </#list>
	        </select>
		</div>
		
		<div class="form-group">
	        <label for="schemaDescription" class="control-label">Description:</label>
	        <textarea id="schemaDescription" name="description" class="form-control">${schema.description!''}</textarea>
		</div>
		
		
        <fieldset>
            <legend>Temporal:</legend>
            <div class='row'>
                <div class="col-md-6">
                <@_dateField name="startFieldId" label="Start Date Field" existing=startFieldId!-1 />
                </div>
                <div class="col-md-6">
                <@_dateField name="endFieldId" label="End Date Field" existing=endFieldId!-1 />
                </div>
            </div>
            <br>
            <p class="text-center"><i>~~ or ~~ </i></p>
            <@_dateField name="textFieldId" label="Textual Date Field" existing=textFieldId!-1 />
        </fieldset>
        
        <br/>
        <input type="submit" value="Save" class="button btn btn-primary"> 

    </form>
</div>
 <div class="col-sm-12">
    <form method="POST" action="${contextPath}/a/admin/uploadSourceFile" enctype="multipart/form-data">
    <h3>Update Data (GeoJSON)</h3>
    <p>Replace the data file for your DataARC source. This will <b>delete</b> all data for your data source, and then load it.  If you delete fields that are mapped to existing indicators, these indicators will be broken/removed.</p>
     <input type="file" name="file">
      <input type="hidden" name="name" value="${schema.name}"/>
      <br/>
        <input type="submit" value="Upload" class="button btn btn-primary"> 
      <br/>
    </form> 
</div>
 <div class="col-sm-12">
<h3>Fields</h3>
<p>Below is the full list of fields in the DataARC tool. You can update the name of a field that's used in the combinator tool as well as to end-users.</p>
<table class="table">
<thead>
	<tr>
	<th>id</th>
	<th>name</th>
	<th>Display Name</th>
	<th>Field Type</th>
<#-- 	<th>Start Date</th>
	<th>End Date</th> -->
	</tr>
</thead>
<#list schema.fields as field>
	   <form action="/a/fields/updateField" method="POST">
	<tr>
	<td>${field.id?c}</td>
	<td>${field.name}</td>
	<td>
         <input type="hidden" name="schemaId" value="${schema.id?c}" />
         <input type="hidden" name="fieldId" value="${field.id?c}"/>
         <input type="text" name="displayName" value="${field.displayName}" class="form-control"/>
	</td>
	<td>${field.type}</td>
<#-- 	<td><input type="checkbox" name="startField" value="true" <#if field.startField>checked</#if> ></td>
	<td><input type="checkbox" name="endField" value="true" <#if field.endField>checked</#if> ></td> -->
    <td><button class="btn btn-default button" type="submit">Save</button></td>
	</tr>
     </form>
</#list>
</table>
</div>
<form method="POST" action="${contextPath}/a/schema/delete/${schema.id?c}" enctype="multipart/form-data" class="form-horizontal">
    <button name="delete" class="button btn btn-danger" value="Delete">Delete Data Source</button>
</form>
</@body.body>

<#macro _dateField name="" label="" existing=-1>
            <label for="${name}" class="control-label">${label}:</label>
            <select id="${name}" name="${name}" class="form-control">
               <option value=""></option>
               <#list (schema.fields![])?sort_by('name') as field>
                   <option value="${field.id?c}" <#if field.id == existing!-1>selected</#if>>${field.displayName}</option>
               </#list>
            </select>
</#macro>       

</html>