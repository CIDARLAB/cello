function wiresJSPLUMB(circuit) {
    jsPlumb.ready(function() {

        var gate0 = $("#gate0");
        var gate1 = $("#gate1");
        var gate2 = $("#gate2");
        var gate3 = $("#gate3");
        var gate4 = $("#gate4");

        var instance1 = jsPlumb.getInstance();

        instance1.importDefaults({
            Connector : [ "Flowchart", { curviness: 150 } ],
            Anchor:["Continuous", { faces:[ "left", "right" ] } ],
            paintStyle:{lineWidth:2,strokeStyle:"#99999"},
            deleteEndpointsOnDetach: true
        });

        var wires = circuit["wires"];
        for(var i=0; i<wires.length; ++i) {
            var w = wires[i];
            var s = "gate"+w["to"]["index"];
            var t = "gate"+w["from"]["index"];
            /*instance1.connect( {
             source: s,
             target: t
             });*/
            console.log(w["name"] + ' ' + s + ' ' + t);
        }


        instance1.connect({
            source:"gate4",
            target:"out_div"
        });

        instance1.connect({
            source:"in3_div",
            target:"gate1"
        });
        instance1.connect({
            source:"in3_div",
            target:"gate3"
        });
        instance1.connect({
            source:"in2_div",
            target:"gate1"
        });
        instance1.connect({
            source:"in2_div",
            target:"gate0"
        });
        instance1.connect({
            source:"in1_div",
            target:"gate0"
        });
        instance1.connect({
            source:"gate0",
            target:"gate2"
        });
        instance1.connect({
            source:"gate3",
            target:"gate2"
        });
        instance1.connect({
            source:"gate1",
            target:"gate4"
        });
        instance1.connect({
            source:"gate2",
            target:"gate4"
        });


        instance1.draggable($(".window"));

    });
}
