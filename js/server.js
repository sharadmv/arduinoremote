var express = require('express');
var app = express.createServer();
app.use(express.static(__dirname+"/static"));
var Bridge = require('bridge-js');
var bridge = new Bridge({
  apiKey : "0d21e491ce3a2af4"
});
bridge.connect();
var blimp = require('./blimp.js');
var state = {
  auto : false,
  accelerate : false,
  reverse : false,
  left : false,
  right : false
}
var handler = {
  accelerate : function() {
    if (!state.accelerate) {
      blimp.forwardOn();
      console.log("accelerating ON");
    } else {
      blimp.forwardOff();
      console.log("accelerating OFF");
    }
    state.accelerate = !state.accelerate;
  },
  reverse : function() {
    if (!state.reverse) {
      blimp.backwardOn();
      console.log("reversing ON");
    } else {
      blimp.backwardOff();
      console.log("reversing OFF");
    }
    state.reverse = !state.reverse;
  },
  stop:function(){
    blimp.forwardOff();
    blimp.backwardOff();
    blimp.autoOff();
    state.accelerate = false;
    state.reverse = false;
    console.log("STOP");
  },
  left :function(state){
    if (state) {
      blimp.leftOn();
      console.log("left ON");
    } else {
      blimp.leftOff();
      console.log("left OFF");
    }
  },
  right:function(state){
    if (state) {
      blimp.rightOn();
      console.log("right ON");
    } else {
      blimp.rightOff();
      console.log("right OFF");
    }
  },
  auto : function() {
    blimp.autoOn();
  }
}
bridge.publishService("caradson", handler);
for (var i in handler) {
  (function(method) {
    app.get("/api/"+i+"/:val", function(req, res) {
      console.log(req.params.val);
      handler[method]((req.params.val == "true"));
      res.send(200);
    });
  })(i);
}
app.listen(8080);
