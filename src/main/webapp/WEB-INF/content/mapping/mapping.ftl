<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.1 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>
            Connect your data to DataARC's core shared concepts 
        </title>

<style>
.autocomplete input[type=text] {
 border:0px solid #fff;
 }
</style>
    <#include "/includes/header.ftl"/>
        <script data-main="/js/app/mapping/main" src="${contextPath}/components/requirejs/require.js"></script>
    <#import "/macros/body.ftl" as body />
    </head>
    <@body.body>
        <h1 class="page-header">Connect your data to DataARC's core shared concepts</h1>
<script type="text/x-template" id="autocomplete">
  <div class="autocomplete form-control" ref="autocompleteroot" @mouseover="addFocus('mouse')" @mouseout="removeFocus('mouse')">
    <input type="hidden" v-model="id"  :name="idname" v-if="idname != undefined" />
    <input type="text" @input="onChange" v-model="search" @keyup.down="onArrowDown" @keyup.up="onArrowUp" v-on:keyup.enter.self.stop="onEnter"
        @keydown.delete="deleteKey" @keyup.enter="enterKey" @keyup="anyKey" autocomplete="off" :disabled="disabled" 
         ref="searchfield" :class="span" :name="fieldname" @focus="addFocus('cursor')" @blur="removeFocus('cursor')"/>
    <ul id="autocomplete-results" v-show="isOpen" class="autocomplete-results"  :style="getStyleTop()" ref="autoresults">
      <li v-if="!isLoading" v-for="(result, i) in results" :key="i" @click="setResult(result)" class="autocomplete-result" :class="{ 'is-active': i === arrowCounter }" :style="getStyleWidth()">
          <span v-html="render(result)" v-if="isCustomRender()"></span>
          <span v-if="!isCustomRender()">{{ getDisplay(result) }}  ({{ result.occurrence}})</span>
      </li>
      <li class="status text-center center" :style="getStyleWidth()">
        <span v-if="isLoading">Loading results...</span>
        <span v-if="!isLoading"> Showing 1-{{recordsPerPage}} of {{totalRecords}} </span>
      </li>
    </ul>

  </div>
</script>
        <div class="container-fluid form-inline" id="schema">
			<div class="row">
				<div class="col-sm-12">
				<p>DataARC's concept map is a network of high-level ideas such as 'land degradation' or 'exchange' that are 
				important when we think about human ecodynamics in the North Atlantic. On this page you can connect individual
				 categories of base-level data, usually represented as individual fields in your database or spreadsheet, or 
				 combinations of categories of base-level data, to these high-level concepts. These connections are created by 
				 defining a mid-level idea, which we call a combinator, which acts as a bridge between your data and the concept 
				 map.</p>
				</div>
            </div>

			<div class="row">
			<div class="col-sm-8">
			<form>
            <div class="row">
                <div class="col-sm-11">
                    <label for="datasource" class="control-label col-sm-4"><span class="badge badge-info">1</span> Choose a Data Source*:</label> 
                    <select v-model="currentSchema" id="datasource" class="form-control" >
                        <option v-for="(option, index) in schema" v-bind:value="index"> {{ option.displayName }} </option>
                    </select>
                </div>
                <div class="col-sm-1">
                    <span class="glyphicon glyphicon-question-sign" v-popover:right="'#help_choose_schema'"></span>
                </div>
            </div>
            <div class="row border" v-show="currentSchema != undefined">
                <div class="col-sm-11">
                        <label for="choose-indicator" class="control-label col-sm-4"><span class="badge badge-info">2</span> Create /  Select Combinator*:</label> 
                        <select v-model="currentIndicator" id="choose-indicator" class="form-control">
                            <optgroup label="Existing Combinators" v-if="indicators.length > 0 ">
                                <option v-for="(option, index) in indicators" v-bind:value="index"> {{ option.name }} </option>
                            </optgroup>
                            <option value="new">Create New Combinator</option>
                        </select>
                </div>
                <div class="col-sm-1">
                    <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                    v-popover:right="'#help_choose_indicator'" ></span>
                </div>
            </div>


            <div class="row border"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
            <div class="col-sm-11"><label class="control-label col-sm-4"><span class="badge badge-info">3</span> Query*: </label></div> 
            <div class="col-sm-1">
                <span class="glyphicon glyphicon-question-sign" v-popover:right="'#help_indicator_query'"></span>
                </div>

            <div class="col-sm-11 col-sm-offset-1">
                    <ul class="list-group" v-for="(part, rowNum) in indicators[currentIndicator].query.conditions">
                        <spart :rowindex="rowNum" :schema="schemaName" :fields="fields" :part="indicators[currentIndicator].query.conditions[rowNum]" :parts="indicators[currentIndicator].query.conditions"
                        @select="onValidChange" @run-query="runQuery()"
                        ></spart>
                    </ul>
                    <div v-if="indicators[currentIndicator].query.conditions.length > 1">
                    <select name="operator" v-model="indicators[currentIndicator].query.operator" v-on:change="runQuery()" >
                    	<option value="AND" selected="{{'AND' == indicators[currentIndicator].query.operator}}" >And</option>
                    	<option value="OR" selected="{{'OR' == indicators[currentIndicator].query.operator}}">Or</option>
                    	<option value="EXCEPT" selected="{{'EXCEPT' == indicators[currentIndicator].query.operator}}">Except</option>
                	</select>
                    </div>
                </div>
            </div>

            <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-10 col-sm-offset-1">
                    
                </div>
                <div class="col-sm-1">
                </div>
            </div>

            <span class='debug hidden'>{{ indicators[currentIndicator] }}</span>



                <div class="row border"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-11">
                        <label for="indicatorDescription" class="control-label col-sm-4 "><span class="badge badge-info">4</span> Description*:</label>
                        <textarea id="indicatorDescription" name="indicatorDescription" v-model="indicators[currentIndicator].description" class="col-sm-7">
                        </textarea>
                    </div>
                    <div class="col-sm-1">
                    <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
                    v-popover:right="'#help_descript_query'"></span>
                    </div>
                </div>


            <div class="row border"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-11">
                    <label for="chooseTopic" class="control-label col-sm-4"><span class="badge badge-info">5</span> Assign Topic*:</label>
                    <div class="col-sm-7">
                    <!-- fixme: was indicators[currentIndicator].topicIdentifiers[_idx]  -->
              
					<ul class="list-unstyled row">
                        <li v-for="(ident, _idx) in selectedTopics"  class="col-12" >
                            <div class="row">
                                <select id="chooseTopic" name='topic' v-model="selectedTopics[_idx]"  class="form-control col-10">
                                    <#list topicCategories as cat>
                                    <optgroup label="${cat}">
                                        <#list topics?sort_by('name') as topic>
                                            <#if topic.category == cat>
                                                <option value="${topic.identifier}" :selected='"${topic.identifier}" == selectedTopics[_idx].identifier'>${topic.name} (${topic.count?c})</option>
                                            </#if>
                                        </#list>
                                    </optgroup>
                                    </#list>
                                </select>
                    <div class="btn-group col-2" role="group" aria-label="add/remove buttons">
                            <button type="button" class="btn btn-xs btn-default" v-on:click="removeTopic(_idx)">-</button>
                            <button v-show="_idx == selectedTopics.length -1" type="button" class="btn btn-xs btn-default" v-on:click="addTopic()">+</button>
                    </div>
</div>
                        </li>
                    </ul>
                    </div>
                </div>
                <div class="col-sm-1">
                    <span class="glyphicon glyphicon-question-sign" v-popover:right="'#help_assign_topic'"></span>
                </div>
            </div>
            
            <span  v-if="fields != undefined && fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
	            <div class="row border" >
	            <div class="col-sm-11">
	                    <label for="indicatorName" class="control-label col-sm-4"><span class="badge badge-info">6</span> Combinator Name:</label>
	                    <input id="indicatorName" name="indicatorName" v-model="indicators[currentIndicator].name" class="col-sm-7"/>
	
	                </div>
	                <div class="col-sm-1">
	                <span class="glyphicon glyphicon-question-sign" aria-hidden="true"
	                        v-popover:right="'#help_choose_name'"></span>
	                </div>
	            </div>
	            <div class="row border" >
	            <div class="col-sm-11">
	                    <label for="indicatorCitation" class="control-label col-sm-4"><span class="badge badge-info">7</span> Citation:</label>
                        <textarea  id="indicatorCitation" name="indicatorCitation" v-model="indicators[currentIndicator].citation"  class="col-sm-7">
                        </textarea>
	                </div>
	                <div class="col-sm-1">
	                <span class="glyphicon glyphicon-question-sign" aria-hidden="true" v-popover:right="'#help_citation'" ></span>
	                </div>
	            </div>
            </span>
            <br/>
            <div class="row"  v-if="fields.length > 0 && currentIndicator === parseInt(currentIndicator)">
                <div class="col-sm-12">
                    <button type="button" class="btn btn-xs btn-success" v-on:click="saveIndicator()" v-bind:disabled='cannotSubmit'>Save Indicator</button>
                    <button type="button" class="btn btn-xs btn-danger" v-on:click="deleteIndicator()" >Delete Indicator</button> {{saveStatus}}
                </div>
            </div>
            <br/>

            </div>
            <div class="col-sm-4">
                <div class="well row">
                <h5>Matching Data</h5>
                <div v-if="results != undefined" >
                <table class="table">
                <thead>
                    <tr>
                        <td colspan=10">Search Results: {{ results.total }} out of {{ schema[currentSchema].rows }} total records</td>
                    </tr>
                    <tr>
                        <th>id</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(result, index) in results.results" class="table" v-if="index < 25">
                        <td><div v-jsonpretty="result.properties">{{result.id}} <span class="link">(info)</span></div>
                        <div class="hidden col-sm-4" style="height:150px;overflow:scroll">
                        </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div v-else>
                <p>To get started:
                <ol> 
                 <li v-bind:class="{'strikethrough': currentSchema != undefined }">Select a data source</li>
                 <li v-bind:class="{'strikethrough': currentIndicator != undefined }">Create a new query, or select an existing one</li>
                 <li class="">Select a field and begin to build your query, as you go, results will show here</li>
            </div>
            </div>
            </div>
            </div>
            
            </div>
            </div>

        <template id="spart-template">
            <div >  
                <select name='fieldName' v-model="part.fieldName"  v-on:change="updateTest()" class="form-control">
                    <option v-for="(field, index) in fields "  v-bind:value="field.name"> {{ field.displayName }} </option>
                </select>
                <select name='type' v-model="part.type" class="form-control" v-on:change="onValidChange()" >
                    <option v-for="(limit, index) in getLimits()" v-bind:value="limit.value"> {{ limit.text }} </option>
                </select>
                <select  v-show="part.compare || part.compare == undefined && part.fieldNameSecond != undefined " 
                		name='fieldNameSecond' v-model="part.fieldNameSecond"  v-on:change="updateTest()" class="form-control">
                    <option v-for="(field, index) in fields "  v-bind:value="field.name"> {{ field.displayName }} </option>
                </select>

                <span v-show="part.compare == false || part.compare == undefined && part.fieldNameSecond == undefined ">
        <autocomplete :items="getFieldValues(part.fieldName)" :is-async="false"  @autocompletevalueset="updateTest" ref="input"
        fieldname="title" :allowCreate="true" idname="value" valuename="value"  :initial_id="part.value" :initial_value="part.value" />
				</span>
				
                <span v-show="rowindex > 0 || parts.length > 1">
                <button type="button" class="btn btn-xs btn-default" v-on:click="removePart(rowindex)">-</button>
                </span>
                <span v-show="rowindex == parts.length -1">
                <button type="button" class="btn btn-xs btn-default" v-on:click="addPart()">+</button>
                </span>
                <br/>
                <span class="link" v-show="part.compare == false" v-on:click="showCompare()">compare two fields?</span>
                <span class="link" v-show="part.compare == true" v-on:click="hideCompare()">compare field and value?</span>
            </div>
        </template>

    </span>
</template>
<div id="helpsection" class="hidden">
    <div id="help_choose_schema">
        Each combinator is built using base-level data from a single data source. 
        Connections data sources are created via the concept map and displayed on the user interface (?). 
        You should only create combinators for data sources you know well provide background literature or informed reasoning to support the reasoning behind your combinator. 
        When you choose a data source, a set of sample data from source will appear as a table at the bottom of the page as a reference. 
        Refer to this table to remind yourself of the data categories available and how the data in each category is formatted.
    </div>
    <div id="help_indicator_query">
    Queries for Combinators can be simple or complex, and utilize the combination of fields and values
    </div>
    <div id="help_choose_indicator">
     In the drop down list, you will see combinators, or mid-level concepts, that have already been defined. Hover over an combinator to see a short description.  
     Select an combinator to which you want to connect data or define a new combinator. 
     Remember, the combinator should summarize or combine in some way individual base-level data categories.
      These will generally be metrics that would be readily recognized by a specialist in your field. 
      For a pollen or insects specialist, this might be an ecological area type. For a zooarchaeologist, this might be a metric like NISP or an age/gender ratio. 
     
    </div>
    <div id="help_descript_query">
    Write a brief description and explanation of the combinator you have created, intended for a non-specialist audience. 
    This text will appear in the general search results as part of the 'results' section. You
     can cite widely available documents here to provide more precise information to fellow specialists. 
     Be careful to avoid technical jargon or abbreviations when writing this text.
    </div>
    <div id="help_assign_topic">
        The combinator you have defined should speak to one or more of DataARC's core shared concepts. 
        You can select concepts from the dropdown list. Each concept will appear with a number next to it that tells you how many combinators are connected to it.
        You can connect your combinator to any number of concepts. Add more connections by pressing the "+" button. 
        It is recommended that you connect your combinator to the topics to which it is most directly relevant. 
        The topics are connected to one another, so those 'second degree' connections are made through the topic map.

    </div>
    <div id="help_citation">
     Write a brief explanation of the connection you see between the combinator and the concept. 
     This text will appear in the 'why' section of the general search results. You can provide citations here for work you feel supports the connection. 
     Be careful to avoid technical jargon or abbreviations when writing this text.
    </div>
    <div id="help_choose_name">
        Choose a name.
    </div>
</div>

  <#list schema as schemum>    
    <script id="results-template-${schemum.id?c}" type="text/x-handlebars-template">
      <div class="description">
        ${schemum.titleTemplate!'{{#each this}}<b>{{@key}}</b>: {{this}}<br/>{{/each}}'}
      </div>
    </script>
  </#list>

<script>
TDAR.vuejs.autocomplete.init();
</script>
        </@body.body>
</html>