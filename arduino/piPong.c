/*
A traffic light for an intersection of a
highway and a country road. Normally, the light
should be green for the highway and red for the
country road, but when traffic approaches on
the country road, the highway gets a red light
and the country road gets a green light.
 
When a light turns red it transitions from green to
red it goes through yellow, but a red light changing
to green transitions directly to green.
 
A pushbutton represents a car approaching on
the country road.
 
Implement the solution with a Finite State
Machine or FSM.
 
State Machine Structure :
http://hacking.majenko.co.uk/finite-state-machine
first break down the problem into states.
 
Identify which states are Transitional (T) and
which are Static (S). A Static state is one in
which the FSM is waiting for stimulus, and is
taking no actions. A Transitional State is a
state which causes actions, but doesn't look
for stimulus.
 
A Transitional State runs once and immediately
moves on to a Static State.
 
State 0: highway = green, country = red; (T)
State 1: wait for car on country road (S)
State 2: highway = yellow, make note of current time (T)
State 3: wait for yellow time to pass (S)
State 4: highway = red, country =  green, make note of current time (T)
State 5: wait for highway red time to pass (S)
State 6: country = yellow, make note of current time (T)
state 7: wait for yellow time to pass (S) then go to 0
*/
 
// Use names for states so it's easier to
// understand what the code is doing
const int S_GNUMBERREQ_I = 0;
const int S_GNUMBERREQ_SEND = 1;
const int S_GNUMBERREQ_RECEIVE = 2;
const int S_GNUMBERREQ_CHECK = 3;
const int S_CHOOSENUMBER = 4;
const int S_CHOOSETYPE = 5;
const int S_RANDOMSERVE = 6;
const int S_CHOOSESERVE = 7;
const int S_GAMESTART = 8;
const int S_SENDRESULT_I = 9;
const int S_SENDRESULT_SEND = 10;
const int S_SENDRESULT_RECEIVE = 11;
const int S_SENDRESULT_CHECK = 12;
const int S_SENDPERSIST = 13;
 
// Pin numbers
// const int countrySensorPin = 2;
 
// const int highwayGreenLEDPin = 3;
// const int highwayYellowLEDPin = 4;
// const int highwayRedLEDPin = 5;
 
// const int countryGreenLEDPin = 6;
// const int countryYellowLEDPin = 7;
// const int countryRedLEDPin = 8;
 
void setup()
{
  // pinMode(highwayGreenLEDPin, OUTPUT);
  // pinMode(highwayYellowLEDPin, OUTPUT);
  // pinMode(highwayRedLEDPin, OUTPUT);
  // pinMode(countryGreenLEDPin, OUTPUT);
  // pinMode(countryYellowLEDPin, OUTPUT);
  // pinMode(countryRedLEDPin, OUTPUT);

}
 
void loop()
{
 
  // start off with the highway getting green
  // The keyword "static" makes sure the variable
  // isn't destroyed after each loop
  static int state = S_GNUMBERREQ_I;

 
  switch (state)
  {
    case S_GNUMBERREQ_I:
      state =S_GNUMBERREQ_SEND;  
      break;
    case S_GNUMBERREQ_SEND:
      state = S_GNUMBERREQ_RECEIVE;
      break;
 
    case S_GNUMBERREQ_RECEIVE:
      state = S_GNUMBERREQ_CHECK;
      break;
 
    case S_GNUMBERREQ_CHECK:
      if(){
        state = S_CHOOSENUMBER;
      }else{
        state = S_GNUMBERREQ_SEND;
      } 
      break;
 
    case S_CHOOSENUMBER:
      state = S_CHOOSETYPE;
      break;
 
    case S_CHOOSETYPE:
      state = S_RANDOMSERVE;
      break;
 
    case S_RANDOMSERVE:
      state = S_CHOOSESERVE;
      break;

    case S_CHOOSESERVE:
      state = S_GAMESTART;
      break;
 
    case S_GAMESTART:
      state = S_SENDRESULT;
      break;
 
    case S_SENDRESULT_I:
      state = S_SENDRESULT_SEND;
      break;
    case S_SENDRESULT_SEND:
      state = S_SENDRESULT_RECEIVE;
      break;
    case S_SENDRESULT_RECEIVE:      
        state = S_SENDRESULT_CHECK;
      break;
    case S_SENDRESULT_CHECK:
      if(){
        state = S_SENDPERSIST;
      }else{
        state = S_SENDRESULT_SEND;
      }
      break;
    case S_SENDPERSIST:
      break;
 
  } // end of switch 
} 

