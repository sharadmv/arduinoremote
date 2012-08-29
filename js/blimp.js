var sp = require('serialport');
var SerialPort = sp.SerialPort;
port = "/dev/ttyACM0";
var serialPort = new SerialPort(port,{
  parser:sp.parsers.readline('\n')
});
serialPort.on('data',function(data){
  console.log("INCOMING SERIAL", data);
});
var blimp = {
  leftOn:function(){
    serialPort.write("e");
  },
  leftOff:function(){
    serialPort.write("f");
  },
  rightOn:function(){
    serialPort.write("g");
  },
  rightOff:function(){
    serialPort.write("h");
  },
  forwardOn:function(){
    serialPort.write("a");
  },
  forwardOff:function(){
    serialPort.write("b");
  },
  backwardOn:function(){
    serialPort.write("c");
  },
  backwardOff:function(){
    serialPort.write("d");
  },
  autoOn:function(){
    serialPort.write("i");
  },
  autoOff:function(){
    serialPort.write("j");
  }
}
module.exports = blimp;
