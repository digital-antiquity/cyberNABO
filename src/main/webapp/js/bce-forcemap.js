function getWidth() {
    width = $('#infobox').width();
    return width;
}

var ordIdXref = {};
var nds = [], lns = new Array();

var width = getWidth();
var aspect = 500 / 950;
var circleWidth = 5;
var node;
var whatever;
var LINK_STRENGTH = 1;
var LINK_DISTANCE = 8;

function getHeight() {
    return getWidth() * aspect;
}

function resize() {
    width = getWidth();
    var height = getHeight();
    vis.attr("width", width);
    vis.attr("height", height);
    parent.attr("width", width);
    parent.attr("height", height);
    force.size([ width, height ]).resume();
};

var centerNode;
var parent, force, vis, zoom, color, root;
var urls_;
function initForceMap() {
    zoom = d3.behavior.zoom().on("zoom", redraw);
    color = d3.scale.category20();
    parent = d3.select("#forcemap").append("svg:svg").attr("preserveAspectRatio", "xMidYMid").attr("width", width).attr("height", getHeight());
    vis = parent.attr("pointer-events", "all").append('svg:g').append('svg:g');
    vis.attr("x", 0);
    vis.attr("y", 0);
    $(window).resize(resize);

    vis.append('svg:rect').attr('fill', 'white');
    force = d3.layout.force().linkDistance(LINK_DISTANCE).linkStrength(LINK_STRENGTH).charge(-100).size([ width, getHeight() ]);
    d3.json("data.json", function(error, graph) {
        getLeafNodes(nds, graph.mindmap.root, lns);
        
        
        var nodes = nds.slice(0), links = [], bilinks = [];
        nds.forEach(function(node, idx) {
            ordIdXref[node.id] = idx;
        });
        
        var urls = graph.pluginData.url;
        urls_ = graph;
        for (var id in urls) {
            if (urls.hasOwnProperty(id)) {
                if (urls[id].urls) {
                    var node = nds[ordIdXref[id]];
                    node.urls = urls[id].urls;
                }
            }
        }

        lns.forEach(function(link) {
            var s = nodes[ordIdXref[link.source]], t = nodes[ordIdXref[link.target]], i = {
                weight : 0,
                id : link.source + "--" + link.target,
                name : 'connector'
            }; // intermediate node
            if (s != undefined && t != undefined) {
                nodes.push(i);
                links.push({
                    source : s,
                    target : i,
                }, {
                    source : i,
                    target : t
                });
                bilinks.push([ s, i, t ]);
            }
        });

        force.nodes(nodes).links(links).start();

        var link = vis.selectAll(".link").data(bilinks).enter().append("path").attr("class", "link").attr("id", function(l) {
            return "l-" + l[1].id;
        });

        node = vis.selectAll("circle.node").data(nds).enter().append("g").attr("class", "node").attr("id", function(d) {
            return "n-" + d.id;
        }).on("mouseover", mouseOverNode).on("mouseout", mouseOutNode);
        node.call(force.drag);

        node.append("svg:circle").attr("class","nodeCircle").attr("x", function(d) {
            return d.x;
        }).attr("y", function(d) {
            return d.y;
        }).attr("r", circleWidth).attr("fill", function(d) {
            return color(d.weight);
        }).on('click', nodeLabelClick)
        .on("dblclick", function(d) { 
            if (d.urls && d.urls.length > 0) {
                
                console.log(d);
                var html = "<b>Links for: "+d.name+"</b><ul>";
                for (var i =0; i < d.urls.length ; i++) {
                    var url = d.urls[i];
                    html += "<li><a href='"+url+"' target='_blank'>" + url + "</a></li>";
                }
                html += "</ul>";
                $("#infodetail").html(html);

//                vis.append("g")       // attach a rectangle
//                .attr("x", 100)         // position the left of the rectangle
//                .attr("y", 50)          // position the top of the rectangle
//                .attr("height", "75%")    // set the height
//                .attr("width", "75%")     // set the width
//                .attr("rx", 10)         // set the x corner curve radius
//                .attr("ry", 10).
//                append("foreignobject")
//                .append("xhtml:body").append("xhtml:p")
//                .append("b").text("Links:"); 
            }
        });
        node.append("text").text(function(d, i) {
            return d.name;
        }).attr("x", function(d, i) {
            if (i > 0) {
                return circleWidth + 5;
            } else {
                return -10
            }
        }).attr("y", function(d, i) {
            if (i > 0) {
                return circleWidth + 0
            } else {
                return 8
            }
        }).attr("fill", "black").on('click', nodeLabelClick);

        var nodelabels = vis.selectAll(".nodelabel");

        root = graph.mindmap.root;
        root.children.forEach(function(c) {
                showHideBranch(c);
        });

        // http://jsfiddle.net/vfu78/16/
        node.append("title").text(function(d) {
            return d.name;
        });

        centerNode = nds[ordIdXref[root.id]];
        force.on("tick", function() {
            var w_ = getWidth() / 2.5;
            var h_ = getHeight() / 2.5;
            if (centerNode.x < w_) {
                centerNode.x += 1;
            } 
            if (centerNode.x > getWidth() - w_) {
                centerNode.x -= 1;
            } 
            if (centerNode.y < h_) {
                centerNode.y += 1;
            } 
            if (centerNode.y > getHeight() - h_) {
                centerNode.y -= 1;
            } 

            link.attr("d", function(d) {
                return "M" + d[0].x + "," + d[0].y + "S" + d[1].x + "," + d[1].y + " " + d[2].x + "," + d[2].y;
            });
            node.attr("transform", function(d) {
                return "translate(" + d.x + "," + d.y + ")";
            });
            nodelabels.attr("x", function(d) {
                return d.x;
            }).attr("y", function(d) {
                return d.y;
            });

        });

        node.forEach(function(d) {
            if (d._children || d.children) {
                d.x = width / 2, d.y = (getHeight()) / 2;
                d.fixed = false;
            }
        });
    });

    d3.select('#up').on("click", moveUp);
    d3.select('#down').on("click", moveDown);
    d3.select('#left').on("click", moveLeft);
    d3.select('#right').on("click", moveRight);
    d3.select('#zoom_in').on('click', zoomClick);
    d3.select('#zoom_out').on('click', zoomClick);

    $("#expand").click(function() {
        var $info = $("#infobox");
        var $map = $("#mapbox");
        $info.removeClass("col-md-4");
        $info.addClass("col-md-8");
        $map.removeClass("col-md-8");
        $map.addClass("col-md-4");
        resize();
    });

    $("#contract").click(function() {
        var $info = $("#infobox");
        var $map = $("#mapbox");
        $map.removeClass("col-md-4");
        $map.addClass("col-md-8");
        $info.removeClass("col-md-8");
        $info.addClass("col-md-4");
        resize();
    });

}


function nodeLabelClick(d) {
    if (d3.event.defaultPrevented)
        return; // click suppressed
    var $term = $("#term");
    $term.val(d.name);
    $term.trigger("keyup");
    centerNode = d;
    showHideBranch(d,2);
    force.start();
}

function showHideBranch(d, depth) {
    var $el = $("#n-" + d.id + " circle");
    var $tx = $("#n-" + d.id + " text");
    var cls = $el.attr("class");
    var className = "hiddenChildren";
    if (d.children && d.children.length > 0) {
        if (cls.indexOf(className) > 0 ) {
            removeClass($el, className);
            $tx.text(d.name);
        } else {
            addClass($el, className);
            $tx.text("+ " + d.name);
        }
        var hide = $el.attr("class").indexOf(className) > 0;
        if (hide) {
            depth = undefined;
        }
        hideChildren(d, hide, depth);
    }
}

function removeClass($el, className) {
    var cls = $el.attr("class");
    if (cls == undefined) {
        cls = "";
    }
    cls = cls.replace(className,"");
    $el.attr("class",cls);
}

function addClass($el, className) {
    var cls = $el.attr("class");
    if (cls == undefined) {
        cls = "";
    }
    cls +=  " " + className;
    $el.attr("class",cls);
}

function hideChildren(d, hide, depth) {
    if (depth == 0) {
        return;
    }
    d.children.forEach(function(e) {
        var node = $("#n-" + e.id + " circle");
        var text = $("#n-" + e.id + " text");
        var path  = $("#l-" + e.id + "--" + d.id);
        var className = "hiddenChildren";
        if (hide)  {
            node.hide();
            removeClass(node,className);
            text.hide();
            text.text(e.name);
            path.hide();
        } else {
            node.show();
            text.show();
            path.show();
//            console.log(depth, e.children);
            if (depth == 1 && e.children && e.children.length > 0) {
                //removeClass(node, className);
                addClass(node, className);
                text.text("+ " + e.name);
            }
        }
        hideChildren(e, hide, depth -1);
    });

}

function redraw() {
    vis.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
}

function getLeafNodes(leafNodes, obj, links) {
    if (obj.children) {
        obj.children.forEach(function(child) {
            getLeafNodes(leafNodes, child, links)
        });
    }
    if (obj.id) {
        obj["weight"] = 1;
        obj['name'] = obj.text.caption;
        leafNodes.push(obj);
        var link = {
            source : obj.id,
            target : obj.parentId,
            weight : 1
        };
        links.push(link);
    }
}

function interpolateZoom(translate, scale) {
    var self = this;
    return d3.transition().duration(350).tween("zoom", function() {
        var iTranslate = d3.interpolate(zoom.translate(), translate), iScale = d3.interpolate(zoom.scale(), scale);
        return function(t) {
            zoom.scale(iScale(t)).translate(iTranslate(t));
            zoomed();
        };
    });
}

function mouseOverNode(d, i) {
    var $this = d3.select(this);
    if (i > 0) {
        // CIRCLE
        $this.selectAll("circle").transition().duration(250).style("cursor", "none").attr("r", circleWidth + 5);

        $this.select("text").transition().style("cursor", "none").duration(250).style("cursor", "none").attr("font-size", "1.5em").attr("x", 15).attr("y", 5)
    } else {
        // CIRCLE
        $this.selectAll("circle").style("cursor", "none")

        // TEXT
        $this.select("text").style("cursor", "none")
    }
}

function mouseOutNode(d, i) {
    var $this = d3.select(this);

    if (i > 0) {
        // CIRCLE
        $this.selectAll("circle").transition().duration(250).attr("r", circleWidth);

        // TEXT
        $this.select("text").transition().duration(250).attr("font-size", "1em").attr("x", 8).attr("y", 4)
    }
};

function zoomed() {
    vis.attr("transform", "translate(" + zoom.translate() + ")" + "scale(" + zoom.scale() + ")");
    vis.attr("x", zoom.translate()[0]);
    vis.attr("y", zoom.translate()[1]);
}

function getCenter() {
    return [ width / 2, (getHeight()) / 2 ];
}

function zoomClick() {
    var clicked = d3.event.target, direction = 1, factor = 0.2, target_zoom = 1, center = getCenter(), extent = zoom.scaleExtent(), translate = zoom
            .translate(), translate0 = [], l = [], view = {
        x : translate[0],
        y : translate[1],
        k : zoom.scale()
    };

    d3.event.preventDefault();
    direction = (this.id === 'zoom_in') ? 1 : -1;
    target_zoom = zoom.scale() * (1 + factor * direction);

    if (target_zoom < extent[0] || target_zoom > extent[1]) {
        return false;
    }

    translate0 = [ (center[0] - view.x) / view.k, (center[1] - view.y) / view.k ];
    view.k = target_zoom;
    l = [ translate0[0] * view.k + view.x, translate0[1] * view.k + view.y ];

    view.x += center[0] - l[0];
    view.y += center[1] - l[1];

    interpolateZoom([ view.x, view.y ], view.k);
}

function moveUp() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x, y + 20);
}

function moveDown() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x, y - 20);
}

function moveLeft() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x + 20, y);
}

function moveRight() {
    var x = parseInt(vis.attr('x'));
    var y = parseInt(vis.attr('y'));
    updatePos(x - 20, y);
}

function updatePos(x, y) {
    vis.attr('x', x);
    vis.attr('y', y);
    vis.attr("transform", "translate(" + x + "," + y + ") " + "scale(" + zoom.scale() + ")");
}

function hashCode(str) {
    var hash = 0;
    if (str.length == 0)
        return hash;
    for (i = 0; i < str.length; i++) {
        char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
}

$(function() {
    initForceMap();
});