var Bridge = require('bridge-js');
var bridge = new Bridge({
  apiKey : "0d21e491ce3a2af4"
});
bridge.connect();
var blimp = require('./blimp.js');
console.log(blimp);
var handler = {
  accelerate : function() {
                 console.log("accel");
    blimp.forwardOn();
  },
  reverse : function() {
                 console.log("reverse");
    blimp.backwardOn();
  },
  stop:function(){
                 console.log("stop");
    blimp.forwardOff();
    blimp.backwardOff();
  },
  leftOn:function(){
           console.log("left on");
    blimp.leftOn();
  },leftOff:function(){
           console.log("left on");
    blimp.leftOff();
  },rightOn:function(){
           console.log("right on");
    blimp.rightOn();
  },rightOff:function(){
           console.log("right off");
    blimp.rightOff();
  }
}
bridge.publishService("caradson", handler);
