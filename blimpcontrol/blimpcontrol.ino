int pin1 = 13;
int pin2 = 12;
int pin3 = 11;
int pin4 = 10;
int pin5 = 9;

void setup() {                
  pinMode(pin1, OUTPUT);
  pinMode(pin2, OUTPUT);
  pinMode(pin3, OUTPUT);
  pinMode(pin4, OUTPUT);
  Serial.begin(9600);  
}

void loop() {
  if (Serial.available() > 0) {
    // read theincoming byte:
    int in = Serial.read();
    // say what you got:
    Serial.print("I received: ");
    Serial.println(in);
    if (in == 97){
      digitalWrite(pin1, HIGH);
      Serial.println("13 HIGH");
    } else if (in == 98){
      digitalWrite(pin1, LOW);
      Serial.println("13 LOW");
    } else if (in == 99){
      digitalWrite(pin2, HIGH);
      Serial.println("12 HIGH");
    } else if (in == 100){
      digitalWrite(pin2, LOW);
      Serial.println("12 LOW");
    } else if (in == 101){
      digitalWrite(pin3, HIGH);
      Serial.println("11 HIGH");
    } else if (in == 102){
      digitalWrite(pin3, LOW);
      Serial.println("11 LOW");
    } else if (in == 103){
      digitalWrite(pin4, HIGH);
      Serial.println("10 HIGH");
    } else if (in == 104){
      digitalWrite(pin4, LOW);
      Serial.println("10 LOW");
    } else if (in == 105){
      digitalWrite(pin5, HIGH);
      Serial.println("9 HIGH");
    } else if (in == 106){
      digitalWrite(pin5, LOW);
      Serial.println("9 LOW");
    }
  }
  /* digitalWrite(pin1, HIGH);
     digitalWrite(pin2, LOW);
     delay(1000);
     digitalWrite(pin1, LOW);
     digitalWrite(pin2, HIGH);
     delay(1000);*/


}
