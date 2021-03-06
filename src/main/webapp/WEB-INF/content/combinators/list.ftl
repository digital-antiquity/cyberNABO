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
<h1> Combinators</h1>
<table class="table">
<thead>
<tr>
<th>id</th>
<th>name</th>
<th># of records mached</th>
<th>creator</th>
<th>schema</th>
<th>description</th>
<th>citation</th>
<th>query</th>
<th>topics</th>
</tr>
</thead>
<#list indicators as combinator>
    <tr>
        <td>${combinator.id?c}</td>
        <td>${combinator.name}</td>
        <td>${facets[combinator.id?c]!0}</td>
        <td>${combinator.user.displayName}</td>
        <td>${combinator.schema.name}</td>
        <td>${combinator.description!''}</td>
        <td>${combinator.citation!''}</td>
        <td>${combinator.query.raw}</td>
        <td><#list combinator.topics as topic><span class="label label-primary">${topic.name}</span> <#sep>, </#sep></#list></td>
    </tr>
</#list>
</table>

</@body.body>
</html>