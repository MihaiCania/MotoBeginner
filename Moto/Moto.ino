#include <Wire.h>
#include <SoftwareSerial.h>

//set up a new serial object
const byte rxPin = 10;
const byte txPin = 11;
SoftwareSerial BTserial(rxPin,txPin);

//The ADXL345 sensor I2C address
int ADXL345 = 0x53;
//ADXL345 outputs
float Xout, Yout, Zout;

//Left hand output
//Flex Sensor output
const int lHandFinger = A3;
int lHandFingerOut = 0;

//Right hand outputs
//Pressure Sensor output
const int rHandPressure = A0;
int rHandPressureOut = 0;
//Acceleration output
const int rHandAccel = A1;
int rHandAccelOut = 0;
//Flex Sensor output
const int rHandFinger = A2;
int rHandFingerOut = 0;

void setup() {
  // put your setup code here, to run once:
  BTserial.begin(9600); //Initiate serial communication for printing the results
  Wire.begin(); //Initiate the Wire library

  //Set ADXIL345 in measuring mode
  Wire.beginTransmission(ADXL345); //Start communication with the device
  Wire.write(0x2D); //Acces /talk to POWER_CTL Register - 0x2D
  //Enable measurement
  Wire.write(8);
  Wire.endTransmission();
  delay(10);
}

void loop() {
  // put your main code here, to run repeatedly:
  /*
  rHandPressureOut = analogRead(rHandPressure);
  rHandAccelOut = analogRead(rHandAccel);
  rHandFingerOut = analogRead(rHandFinger);

  BTserial.print("P=");
  BTserial.print(rHandPressureOut);

  BTserial.print("  Accel=");
  BTserial.print(rHandAccelOut);

  BTserial.print("  Finger=");
  BTserial.println(rHandFingerOut);

  delay(500);
  */
  Wire.beginTransmission(ADXL345);
  Wire.write(0x32); // Start with register 0x32 (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(ADXL345, 6, true); // Read 6 registers total, each axis value is stored in 2 registers

  Xout = (Wire.read() | Wire.read() << 8); // X-axis value
  Xout = Xout/256; //For a range of +-2g, we need to divide the raw values by 256, according to the datasheet

  Yout = (Wire.read() | Wire.read() << 8); // Y-axis value
  Yout = Yout/256; //For a range of +-2g, we need to divide the raw values by 256, according to the datasheet

  Zout = (Wire.read() | Wire.read() << 8); // Z-axis value
  Zout = Zout/256; //For a range of +-2g, we need to divide the raw values by 256, according to the datasheet

  BTserial.print("X= ");
  BTserial.print(Xout);
  BTserial.print("   Y= ");
  BTserial.print(Yout);
  BTserial.print("   Z= ");
  BTserial.println(Zout);
  delay(200);
}
