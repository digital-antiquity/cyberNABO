<!DOCTYPE html>
<html lang="en">
  <head>
  <title>DataARC - Linking Data from Archaeology, the Sagas, and Climate</title>

    <#include "includes/public-header.ftl" />
	<script>
	var testing = false;
	// Global variables

	    function getContextPath() {
        return "${contextPath}";
    }
    
    var FIELDS = {
    <#list fields as field>"${field.name}": "${field.displayName}"<#sep>,
</#sep></#list>
    };
    var SCHEMA = {<#list schema as schema>"${schema.name}": "${schema.displayName}"<#sep>,</#sep></#list>};
    
   var geoJsonInputs =	[
   <#list files as file>
     {id:"${file.id?c}", name:"${file.name}", title:"${file.title!'untitled'}", url:"/geojson/${file.id?c}"}<#sep>, </#sep> 
   </#list>
   	];
   	

   
      
            
	</script>
  </head>
<body id="page-top">
  <!-- Navigation -->
  <nav class="navbar navbar-expand-lg navbar-light fixed-top" id="mainNav">
    <div class="container">
      <a class="navbar-brand js-scroll-trigger" href="#page-top">DataARC</a>
      <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarResponsive">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#page-top">Start</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#explore-section">Explore</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#timeline-section">Timeline</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#map-section">Map</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#concept-section">Concept</a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#results-section">Results <span id="results-count" class="badge badge-dark"><i class="fa fa-spinner text-white fa-spin"></i></span></a>
          </li>
          <li class="nav-item">
            <a class="nav-link js-scroll-trigger" href="#why-section">Why</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <header class="masthead home-shore">
    <div class="header-content">
      <div class="header-content-inner">
        <h1 id="homeHeading">Simple Interface, Data That Matters</h1>
        <hr>
        <p>Enter a word or phrase to begin filtering the result data or select a preconfigured example.</p>
        <p>
          <div class="row justify-content-md-center">
            <div class="col-lg-6 text-center">
              <div class="input-group">
                <input id="keywords-field" type="text" class="form-control" placeholder="Search for..." aria-label="Search for...">
                <span class="input-group-btn">
                    <a id="keywords-btn" class="btn btn-primary btn-xl js-scroll-trigger" href="#explore-section">Explore!</a>
                  </span>
              </div>
            </div>
          </div>
        </p>
        <p>
          <div class="row">
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-superpowers text-primary sr-icons"></i>
                <h3>Keyword Example</h3>
                <p>Using the keyword example, you can see how a simple phrase in the keyword box will filter our results.</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-compass text-primary sr-icons"></i>
                <h3>Spatial Example</h3>
                <p>Want to see results only with a specific bounding box?</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-clock-o text-primary sr-icons"></i>
                <h3>Temporal Example</h3>
                <p>This example uses our timeline to filter the result data.</p>
              </div>
            </div>
            <div class="col-lg-3 col-md-6 text-center">
              <div class="service-box">
                <i class="fa fa-4x fa-sitemap text-primary sr-icons"></i>
                <h3>Concept Example</h3>
                <p>Looking for a way to view results that only relate to specific concapts?</p>
              </div>
            </div>
          </div>
        </p>
      </div>
    </div>
  </header>
  <section id="explore-section" class="bg-primary">
    <div class="container">
      <div class="row">
        <div class="col-lg-8 mx-auto text-center">
          <h2 class="section-heading text-white">Explore the data!</h2>
          <hr class="light">
          <p class="text-faded">Make your way through the different types of data using the tools below.</p>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#timeline-section"><i class="fa fa-clock-o text-white sr-icons"></i> Timeline</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#map-section"><i class="fa fa-map-o text-white sr-icons"></i> Map</a>
          <a class="btn btn-dark btn-xl js-scroll-trigger" href="#concept-section"><i class="fa fa-sitemap text-white sr-icons"></i> Concept</a>
        </div>
      </div>
    </div>
  </section>
  <section id="timeline-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Timeline</h2>
          <hr class="primary">
          <div class="legend justify-content-md-center">
            <ul class="list-inline">
              <li class="list-inline-item"><span class="legend-item legend-item-one">&nbsp;&nbsp;</span> Archaeological</li>
              <li class="list-inline-item"><span class="legend-item legend-item-two">&nbsp;&nbsp;</span> Textual</li>
              <li class="list-inline-item"><span class="legend-item legend-item-three">&nbsp;&nbsp;</span> Environmental</li>
            </ul>
          </div>
          <div id="timeline">
            <div class="loader h-100 justify-content-center align-items-center">
              <h1><span class="fa fa-cog fa-spin fa-2x"></span></h1>
            </div>
          </div>
          <button id="filter-timeline-apply" class="btn btn-primary"><i class="fa fa-clock-o text-white sr-icons"></i> Apply Filter</button>
          <button id="filter-timeline-clear" class="btn btn-secondary"><i class="fa fa-clock-o text-white sr-icons"></i> Clear Filter</button>
        </div>
      </div>
    </div>
  </section>
  <section id="map-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Map</h2>
          <hr class="primary">
          <div class="legend justify-content-md-center">
            <ul class="list-inline">
              <li class="list-inline-item"><span class="legend-item legend-item-one">&nbsp;&nbsp;</span> Archaeological</li>
              <li class="list-inline-item"><span class="legend-item legend-item-two">&nbsp;&nbsp;</span> Textual</li>
              <li class="list-inline-item"><span class="legend-item legend-item-three">&nbsp;&nbsp;</span> Environmental</li>
            </ul>
          </div>
          <div id="map">
            <div id="mapSpinner"><span class="fa fa-spinner fa-spin"></span></div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <section id="concept-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-12 text-center">
          <h2 class="section-heading">Concept</h2>
          <hr class="primary">
        </div>
        <div class="col-lg-12">
          <div id="topicControls" class="btn-toolbar justify-content-between">
              <div class="btn-group">
                <button title="zoom in" id="topicmapZoomIn" class="btn btn-secondary"><span class="fa fa-search-plus"></span></button>
                <button title="zoom out" id="topicmapZoomOut" class="btn btn-secondary"><span class="fa fa-search-minus"></span></button>
                <button title="reset" id="topicmapReset" class="btn btn-secondary"><span class="fa fa-repeat"></span></button>
                <button title="pause" id="topicmapPause" class="btn btn-secondary"><span class="fa fa-pause"></span></button>
                <button title="continue" id="topicmapProceed" class="btn btn-secondary"><span class="fa fa-play"></span></button>
              </div>
              <div id="topicSearch" class="input-group"></div>
            </div>
          <div id="conceptContainer" style="width:100%;">
            
            <div id="topicmap"></div>
          </div>
        </div>
        <div class="col-lg-12 text-center"><button id="filter-topicmap-clear" class="btn btn-secondary"><i class="fa fa-times-rectangle text-white sr-icons"></i> Clear Filter</button></div>
      </div>
    </div>
  </section>
  <section id="results-section">
    <div class="call-to-action bg-dark">
      <div class="container text-center">
        <h2>Results</h2>
        <hr class="primary">
        <div id="results">
          <div class="result-loader col-sm-12 text-center">
            <h1><i class="fa fa-cog fa-spin fa-2x"></i></h1>
          </div>
        </div>
        <button id="filter-save" class="btn btn-light"><i class="fa fa-bookmark sr-icons"></i> Save Results</button>
        <button id="filter-share" class="btn btn-light"><i class="fa fa-print sr-icons"></i> Print Results</button>
      </div>
    </div>
  </section>
  <section id="why-section">
    <div class="container">
      <div class="row">
        <div class="col-lg-8 mx-auto text-center">
          <h2 class="section-heading">Why</h2>
          <hr class="primary">
          <p>Why did you get these results? We will explain how the results were obtained in order to provide a level of confidence for how the data was processed to produce what you are seeing.</p>
        </div>
      </div>
    </div>
  </section>
  <!-- Vendor scripts -->

    <#include "includes/public-footer.ftl">

  <!-- Custom scripts -->
  <script src="js/global.js"></script>
  <script src="js/search.js"></script>
  <script src="js/timeline.js"></script>
  <script src="js/geography.js"></script>
  <script src="js/concepts.js"></script>
  <script src="js/results.js"></script>
  <!-- Page Level Javascript Actions -->
  <script type="text/javascript">
  
      Handlebars.registerHelper("fieldName", function(name) {
    if (name != undefined) {
        if (FIELDS[name.trim()] != undefined) {
            return FIELDS[name.trim()];
        } 
        return name;
      }
      return "";
    });


  
  
  var config = {
    source: "/api/search",
    recordSource: "/api/getId",
    delay: 100, // delay before search checks in ms
    before: function() { // actions to run before search query begins
      Geography.wait();
      $('#results-count').html('<i class="fa fa-spinner text-white fa-spin"></i>');
    },
    after: function() { // actions to run after search query is finished
      Timeline.refresh();
      Geography.refresh();
      Concepts.refresh();
      ResultsHandler = new Results('#results');
      $('#results-count').text(Search.results.length);
    }
  };

  if (testing) {
    config.source = "search.php";
  }

  $(document).ready(function() {
    Search.init(config);
  });
  </script>
    <!-- everything below this is automatically generated -->

    <!-- Either leave this template here or incorporate into the ones that are autmoatically generated -->
    <script id="title-template-polygon" type="text/x-handlebars-template">
      <div class="title">
        {{#each this}}<b>{{@key}}</b>: {{this}}<br/>{{/each}}
      </div>
      <button class="btn btn-sm" onclick="Geography.regionFilter('{{this.region}}')">Filter by this polygon</button>
    </script>

   <!--  handlebar templates http://handlebarsjs.com
     -->
  <#list schema as schemum>    
    <script id="title-template-${schemum.id?c}" type="text/x-handlebars-template">
	  <div class="title">
        <#if schemum.titleTemplate?has_content && schemum.titleTemplate != ''>
	  	${schemum.titleTemplate}
	  	<#else>
	  	{{#each this}}<b>{{fieldName @key}}</b>: {{this}}<br/>{{/each}}
	  	</#if>
	  </div>
	</script>
    <script id="results-template-${schemum.id?c}" type="text/x-handlebars-template">
	  <div class="description">
	  	<#if schemum.resultTemplate?has_content && schemum.resultTemplate != ''>
	  	${schemum.resultTemplate}
	  	<#else>
	  	{{#each this}}<b>{{fieldName @key}}</b>: {{this}}<br/>{{/each}}
	  	</#if>
	  </div>
	</script>
  </#list>

  </body>
</html>