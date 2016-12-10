#include "SevSeg.h"
#include "RF24.h"

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

int OLD_STATE = 0;

bool updated = false;
const int nextButton = 1;
const int switchButton = 0;
const int plusButton = 2;
const int lessButton = 3;

int nextLastState = LOW;
int switchLastState = LOW;
int plusLastState = LOW;
int lessLastState = LOW;

bool nextPressed = false;
bool switchPressed = false;
bool plusPressed = false;
bool lessPressed = false;

/*SevSeg
PIN 5   => digit 0
PIN 10  => digit 1
PIN 1   => D 
PIN 3   => E 
PIN 4   => C 
PIN 6   => A 
PIN 7   => B 
PIN 8   => F 
PIN 9   => G 

PIN 2   => DP
*/

const int ASevSegScore = 27;
const int BSevSegScore = 26;
const int CSevSegScore = 28;
const int DSevSegScore = 29;
const int ESevSegScore = 30;
const int FSevSegScore = 31;
const int GSevSegScore = 32;
const int dig1SevSegScore = 25;
const int dig2SevSegScore = 24;
const int dig3SevSegScore = 23;
const int dig4SevSegScore = 22;

int scorePlayer1 = 0;
int scorePlayer2 = 0;

const int ASevSegNumber = 38;
const int BSevSegNumber = 39;
const int CSevSegNumber = 37;
const int DSevSegNumber = 36;
const int ESevSegNumber = 35;
const int FSevSegNumber = 34;
const int GSevSegNumber = 33;
const int dig1SevSegNumber = 49;
const int dig2SevSegNumber = 48;

static int gameNumber = 0;

const int confPlayer1Led = 8;
const int confPlayer2Led = 9;
const int confServeLed = 10;
const int confNumberLed = 18;
const int confGameTypeLed = 11;

int confScore = -1;

const int servePlayer1Led = 6;
const int servePlayer2Led = 7;

int serve = -1;
int firstServe = -1;
const int gameType6Led = 19;
const int gameType11Led = 20;
const int gameType21Led = 21;
int gameType = 6;

SevSeg scoreSevSeg;
SevSeg numberSevSeg;

const int NRFMOSI = 0;
const int NRFMISO = 0;
const int NRFCSN = 53;
const int NRFCE = 40;
const int NRFSCK = 0;

const uint64_t pipes[2] = { 0xF0F0F0F0E1LL, 0xF0F0F0F0D2LL };

RF24 radio(NRFCE,NRFCSN);

static int state = S_GNUMBERREQ_I;

const int gameStartedLed = 5;
void routine(){
      //Serial.println("bem vindo moleke");
}
String getState(){
  switch(state){
    case 0:
      return "STATE : Number Request Init";
      break;
    case 1:
      return "STATE : Number Request Send";
      break;
    case 2:
      return "STATE : Number Request Receive";
      break;
    case 3:
      return "STATE : Number Request Check";
      break;
    case 4:
      return "STATE : Choose Number";
      break;
    case 5:
      return "STATE : Choose Type";
      break;
    case 6:
      return "STATE : Random Serve";
      break;
    case 7:
      return "STATE : Choose Serve";
      break;
    case 8:
      return "STATE : Game Start";
      break;
    case 9:
      return "STATE : Send Result Init";
      break;
    case 10:
      return "STATE : Send Result Send";
      break;
    case 11:
      return "STATE : Send Result Receive";
      break;
    case 12:
      return "STATE : Send Result Check";
      break;
    case 13:
      return "STATE : Send Persist";
      break;
  }
}
void setup()
{
  Serial.begin(9600);
  pinMode(nextButton,INPUT);
  pinMode(switchButton,INPUT);
  pinMode(plusButton,INPUT);
  pinMode(lessButton,INPUT);

  pinMode(ASevSegScore,OUTPUT);
  pinMode(BSevSegScore,OUTPUT);
  pinMode(CSevSegScore,OUTPUT);
  pinMode(DSevSegScore,OUTPUT);
  pinMode(ESevSegScore,OUTPUT);
  pinMode(FSevSegScore,OUTPUT);
  pinMode(GSevSegScore,OUTPUT);
  pinMode(dig1SevSegScore,OUTPUT);
  pinMode(dig2SevSegScore,OUTPUT);

  pinMode(ASevSegNumber,OUTPUT);
  pinMode(BSevSegNumber,OUTPUT);
  pinMode(CSevSegNumber,OUTPUT);
  pinMode(DSevSegNumber,OUTPUT);
  pinMode(ESevSegNumber,OUTPUT);
  pinMode(FSevSegNumber,OUTPUT);
  pinMode(GSevSegNumber,OUTPUT);
  pinMode(dig1SevSegNumber,OUTPUT);
  pinMode(dig2SevSegNumber,OUTPUT);

  pinMode(confPlayer1Led,OUTPUT);
  pinMode(confPlayer2Led,OUTPUT);
  pinMode(confServeLed,OUTPUT);
  pinMode(confNumberLed,OUTPUT);
  pinMode(confGameTypeLed,OUTPUT);

  pinMode(servePlayer1Led,OUTPUT);
  pinMode(servePlayer2Led,OUTPUT);
  pinMode(gameType6Led,OUTPUT);
  pinMode(gameType11Led,OUTPUT);
  pinMode(gameType21Led,OUTPUT);

  pinMode(gameStartedLed,OUTPUT);

  byte numDigitsScoreSS = 4;   
  byte digitScoreSS[] = {dig1SevSegScore, dig2SevSegScore, dig3SevSegScore, dig4SevSegScore};
  byte segmentScoreSS[] = {ASevSegScore, BSevSegScore, CSevSegScore, DSevSegScore, ESevSegScore, FSevSegScore, GSevSegScore, 13};
  scoreSevSeg.begin(COMMON_ANODE, numDigitsScoreSS, digitScoreSS, segmentScoreSS);
  scoreSevSeg.setBrightness(100);

  byte numDigitsNumberSS = 2;   
  byte digitNumberSS[] = {dig1SevSegNumber, dig2SevSegNumber};
  byte segmentNumberSS[] = {ASevSegNumber, BSevSegNumber, CSevSegNumber, DSevSegNumber, ESevSegNumber, FSevSegNumber, GSevSegNumber, 36};
  numberSevSeg.begin(COMMON_ANODE, numDigitsNumberSS, digitNumberSS, segmentNumberSS);
  numberSevSeg.setBrightness(100);

  radio.begin();
  //  radio.setAutoAck(1);                    // Ensure autoACK is enabled
  radio.setRetries(15,15);
  radio.openWritingPipe(pipes[1]);
  radio.openReadingPipe(1,pipes[0]);

  routine();
}
void getButtonPressed(){
  int plusState = digitalRead(plusButton);
  int nextState = digitalRead(nextButton);
  int switchState = digitalRead(switchButton);
  int lessState = digitalRead(lessButton);

  if (lessState != lessLastState) {
    if (lessState == HIGH) {
      Serial.println("INPUT : Less Pressed");
      lessPressed = true;
    }
    delay(50);
  }else  if (plusState != plusLastState) {
    if (plusState == HIGH) {
      Serial.println("INPUT : Plus Pressed");
      plusPressed = true;
    }
    delay(50);
  }else  if (switchState != switchLastState) {
    if (switchState == HIGH) {
      Serial.println("INPUT : Switch Pressed");
      switchPressed = true;
    } 
    delay(50);
  }else  if (nextState != nextLastState) {
    if (nextState == HIGH) {
      Serial.println("INPUT : Next Pressed");
      nextPressed = true;
    }
    delay(50);
  }
  plusLastState = plusState;
  nextLastState = nextState;
  switchLastState = switchState;
  lessLastState = lessState;
}

/*
char * convertNumberIntoArray(unsigned short number, unsigned short length) {
    
    char * arr = (char *) malloc(length * sizeof(char)), * curr = arr;
    do {
        *curr++ = number % 10;
      number /= 10;
    } while (number != 0);
    return arr;
}

unsigned short getId(char * rawMessage, unsigned short length){
    unsigned short i = 0;
    unsigned short id = 0;
    for( i=1; i< length; i++){
        id += rawMessage[i]*pow( 10, i-1 );
    }
    return id;
}

unsigned short getMessage( char * rawMessage){
    unsigned short message = rawMessage[0];
    return (unsigned short)message;
}
unsigned short getLength( unsigned int rudeMessage){
    unsigned short length = (unsigned short)(log10((float)rudeMessage)) + 1;
    return length;
}

int getState(unsigned short pin){
  boolean state = digitalRead(pin);
  return state == true ? 0 : 1;
}

void sendCallback(unsigned short callback){
   // First, stop listening so we can talk
      radio.stopListening();

      // Send the final one back.
      radio.write( &callback, sizeof(unsigned short) );
      printf("Sent response.\n\r");

      // Now, resume listening so we catch the next packets.
      radio.startListening();
}

void performAction(unsigned short rawMessage){
  unsigned short action, id, length, callback;
  char * castedMessage;
  
  length = getLength(rawMessage);
  castedMessage = convertNumberIntoArray(rawMessage, length);
  action = getMessage(castedMessage);
  id = getId(castedMessage, length);

  if (action == 0 || action ==1){
      callback = action;
      doAction(id, action);
  }else if(action == 2){
      callback = getState(id);
  }
  sendCallback(callback);


 
}
void loop(void)
{

    // if there is data ready
    if ( radio.available() )
    {
      // Dump the payloads until we've gotten everything
      unsigned short message;
      bool done;
//      char * new;
      unsigned short rawMessage; 
      done = false;
      while ( radio.available() )
      {
        // Fetch the payload, and see if this was the last one.
        radio.read( &rawMessage, sizeof(unsigned long) );

        // Spew it
        Serial.println(rawMessage);
        printf("Got message %d...",rawMessage); 

        performAction(rawMessage);

        delay(10);
      }

     
    }

}*/
void g_number_I(){
  Serial.println("TODO : Game number init");
  delay(500);
}
void g_number_S(){
  Serial.println("TODO : Game number send");
  //radio.write( &callback, sizeof(unsigned short) );
  //radio.startListening();
  delay(1000);
}
void g_number_R(){
 /* if (radio.available()){
      // Dump the payloads until we've gotten everything
    unsigned short message;
    bool done;
    //  char * new;
    unsigned short rawMessage; 
    done = false;
    while(radio.available())
    {
        // Fetch the payload, and see if this was the last one.
      radio.read( &rawMessage, sizeof(unsigned long) );

        // Spew it
      Serial.println(rawMessage);
      printf("Got message %d...",rawMessage); 

      performAction(rawMessage);

      delay(10);
    }*/
    delay(1000);
  }
  int g_number_check(){
  Serial.println("TODO : Checking number received");
  delay(1000);
  Serial.println("Number ok");
  return S_CHOOSENUMBER;
}
int choose_number(){
  String toShow = "";
  int ret = S_CHOOSENUMBER;
  if(switchPressed ||plusPressed){
    gameNumber++;
    toShow = "VAR : gameNumber = "+String(gameNumber);
  }else if(lessPressed){
    gameNumber--;
    toShow = "VAR : gameNumber = "+String(gameNumber);
  }else if(nextPressed){
    toShow = "VAR : gameNumber = "+String(gameNumber);
    delay(500);
    ret = S_CHOOSETYPE;
  }
  if(switchPressed||plusPressed||lessPressed||nextPressed)
    Serial.println(toShow);
  return ret;
}
int choose_type(){
  String toShow = "";
  int ret = S_CHOOSETYPE;
  if(plusPressed||switchPressed){
    if(gameType == 6){
      gameType = 11;
      toShow = "VAR : gameType = "+String(gameType);
    }else if(gameType == 11){
      gameType = 21;
      toShow = "VAR : gameType = "+String(gameType);
    }else{
      gameType = 6;
      toShow = "VAR : gameType = "+String(gameType);
    }
  }else if(lessPressed){
    if(gameType == 6){
      gameType = 21;
      toShow = "VAR : gameType = "+String(gameType);
    }else if(gameType == 11){
      gameType = 6;
      toShow = "VAR : gameType = "+String(gameType);
    }else{
      gameType = 11;
      toShow = "VAR : gameType = "+String(gameType);
    }
  }else if(nextPressed){
    toShow = "Type choosed, passing";
    delay(500);
    ret = S_RANDOMSERVE;
  }
  if(switchPressed||plusPressed||lessPressed||nextPressed)
    Serial.println(toShow);
  return ret;
}
int random_serve(){
  String toShow = "";
  int ret = S_CHOOSESERVE;
  int serveRand = random(1234);
  serveRand = serveRand %2;
  delay(50);
  if(serveRand == 0){
    serve = -1;
    toShow = "VAR : serve = "+String(serve);
  }else{
    serve = 1;
    toShow = "VAR : serve = "+String(serve);
  }
  Serial.println(toShow);
  return ret;
}
int choose_serve(){
  String toShow = "";
  int ret = S_CHOOSESERVE;
  if(lessPressed||plusPressed||switchPressed){
    if(serve == -1){
      serve = 1;
    toShow = "VAR : serve = "+String(serve);
    }else{
      serve = -1;
    toShow = "VAR : serve = "+String(serve);
    }
  }else if(nextPressed){
    Serial.println("DEBUG "+String(nextPressed));
    firstServe = serve;
    toShow = "Starting Game";
    delay(500);
    ret = S_GAMESTART;
  }
  if(switchPressed||plusPressed||lessPressed||nextPressed)
    Serial.println(toShow);
  return ret;
}
void updateServe(){
  int scoreTotal = scorePlayer1 + scorePlayer2;
  if((scoreTotal%4) < 2 ){
    serve = firstServe;
  }else{
    serve = firstServe*-1;
  }
}
int game_start(){
  String toShow = "";
  int ret = S_GAMESTART;
  if(plusPressed){
    updated = true;
    if(confScore == -1){
      scorePlayer1++;
      updateServe();
      toShow = "VAR : scorePlayer1 = "+String(scorePlayer1);
    }else{
      scorePlayer2++;
      updateServe();
      toShow = "VAR : scorePlayer2 = "+String(scorePlayer2);
    }
  }else if(switchPressed){
    updated = true;
    if(confScore == -1){
      confScore = 1;
      toShow = "VAR : confScore = "+String(confScore);
    }else{
      confScore = -1;
      toShow = "VAR : confScore = "+String(confScore);
    }
  }else if(lessPressed){
    updated = true;
    if(confScore == -1){
      scorePlayer1--;
      updateServe();
      toShow = "VAR : scorePlayer1 = "+String(scorePlayer1);
    }else{
      scorePlayer2--;
      updateServe();
      toShow = "VAR : scorePlayer2 = "+String(scorePlayer2);
    }
  }else if(nextPressed){
    toShow = "Game over";
    delay(500);
    ret = S_SENDRESULT_I;
  }
  if(switchPressed||plusPressed||lessPressed||nextPressed)
    Serial.println(toShow);
  return ret;
}
void send_result_I(){
  Serial.println("TODO : Send result init");
}
void send_result_S(){
  Serial.println("TODO : Send result send");
  delay(1000);
}
void send_result_R(){
  Serial.println("TODO : Send result receive");
  delay(1000);
}
int send_result_C(){
  Serial.println("TODO : Send result check");
  delay(500);
  Serial.println("result ok");
  return S_SENDPERSIST;
}
void send_persist(){
  Serial.println("TODO : Send persist");
  delay(500);
} 
void showConfScore(){
  switch(confScore){
    case -1:
      digitalWrite(confPlayer1Led,HIGH);
      digitalWrite(confPlayer2Led,LOW);
      break;
    case 1:
      digitalWrite(confPlayer1Led,LOW);
      digitalWrite(confPlayer2Led,HIGH);
      break;
    default:
      digitalWrite(confPlayer1Led,HIGH);
      digitalWrite(confPlayer2Led,LOW);
      break;
  }
}
void showScore(){
  String score = "";
  String score1 = "";
  String score2 = "";
  if(scorePlayer1<0){
    scorePlayer1 = 0;
    score1 = "00";
  }else if(scorePlayer1<10){
    score1 = "0"+String(scorePlayer1);
  }
  if(scorePlayer2<0){
    scorePlayer2 = 0;
    score2 = "00";
  }else if(scorePlayer2<10){
    score2 = "0"+String(scorePlayer2);
  }
  score = score1+score2;
  showConfScore();
  scoreSevSeg.setNumber(score.toInt(), 4);
  scoreSevSeg.refreshDisplay();
}
void showNumber(){
  numberSevSeg.setNumber(gameNumber, 1);
  numberSevSeg.refreshDisplay();
}
void showState(){
  switch(state){
    case S_CHOOSETYPE:
      digitalWrite(confGameTypeLed,HIGH);
      digitalWrite(confServeLed,LOW);
      digitalWrite(confNumberLed,LOW);
      digitalWrite(gameStartedLed,LOW);
      break;
    case S_CHOOSENUMBER:
      digitalWrite(gameStartedLed,LOW);
      digitalWrite(confNumberLed,HIGH);
      digitalWrite(confGameTypeLed,LOW);
      digitalWrite(confServeLed,LOW);
      break;
    case S_GAMESTART:
      digitalWrite(gameStartedLed,HIGH);
      digitalWrite(confNumberLed,LOW);
      digitalWrite(confServeLed,LOW);
      digitalWrite(confGameTypeLed,LOW);
      break;
    case S_CHOOSESERVE:
      digitalWrite(confServeLed,HIGH);
      digitalWrite(confNumberLed,LOW);
      digitalWrite(gameStartedLed,LOW);
      digitalWrite(confGameTypeLed,LOW);
      break;
    default:
      digitalWrite(confServeLed,HIGH);
      digitalWrite(confNumberLed,LOW);
      digitalWrite(gameStartedLed,LOW);
      digitalWrite(confGameTypeLed,LOW);
      break;
  }
}
void showServe(){
  switch(serve){
    case -1:
      digitalWrite(servePlayer1Led,HIGH);
      digitalWrite(servePlayer2Led,LOW);
      break;
    case 1:
      digitalWrite(servePlayer1Led,LOW);
      digitalWrite(servePlayer2Led,HIGH);
      break;
    default:
      digitalWrite(servePlayer1Led,HIGH);
      digitalWrite(servePlayer2Led,LOW);
      break;
  }
}
void showGameType(){
   switch(gameType){
    case 6:
      digitalWrite(gameType6Led,HIGH);
      digitalWrite(gameType11Led,LOW);
      digitalWrite(gameType21Led,LOW);
      break;
    case 11:
      digitalWrite(gameType6Led,LOW);
      digitalWrite(gameType11Led,HIGH);
      digitalWrite(gameType21Led,LOW);
      break;
    case 21:
      digitalWrite(gameType6Led,LOW);
      digitalWrite(gameType11Led,LOW);
      digitalWrite(gameType21Led,HIGH);
      break;
    default:
      digitalWrite(gameType6Led,LOW);
      digitalWrite(gameType11Led,LOW);
      digitalWrite(gameType21Led,LOW);
      break;
  }
}
void showScoreConf(){
  if(confScore == -1){
    digitalWrite(confPlayer1Led, HIGH);
    digitalWrite(confPlayer2Led, LOW);
  }else{
    digitalWrite(confPlayer1Led, LOW);
    digitalWrite(confPlayer2Led, HIGH);
  }
}
void ledUpdate(){
  showScore();
  showNumber();
  showServe();
  showGameType();
  showState();
  showScoreConf();
  if(updated){
    String info = "",tmp="";
    if(serve ==-1){
      tmp = "player1";
    }else{
      tmp = "player2";
    }
    info = info + gameType+"pts game, player 1 "+scorePlayer1+" - "+scorePlayer2+" player 2. Next serve :"+tmp;
    Serial.println(info);
  }
/*
int scorePlayer1 = 0;
int scorePlayer2 = 0;

static int gameNumber = 0;

const int confPlayer1Led = 6;
const int confPlayer2Led = 6;
const int confServeLed = 6;
const int confNumberLed = 6;
const int confGameTypeLed = 6;

int confScore = -1;

const int servePlayer1Led = 6;
const int servePlayer2Led = 6;

int serve = -1;

const int gameType6Led = 6;
const int gameType11Led = 6;
const int gameType21Led = 6;
int gameType = 6;

static int state = S_GNUMBERREQ_I;

const int gameStartedLed = 6;*/
 
}
void sevSegUpdate(){
 /* //Serial.println("Player 1 "+scorePlayer1+" - "+scorePlayer2+" Player 2");
  //Serial.println("Game Number "+gameNumber);*/
}
void resetButton(){
  nextPressed = false;
  switchPressed = false;
  plusPressed = false;
  lessPressed = false;
  updated = false;
}
void update(){
  ledUpdate();
  //sevSegUpdate();
  resetButton();
}
void loop()
{
/*  scoreSevSeg.setNumber(1234, 0);
  scoreSevSeg.refreshDisplay();
  numberSevSeg.setNumber(11, 0);
  numberSevSeg.refreshDisplay();*/
/*  digitalWrite(confNumberLed,HIGH);
  digitalWrite(gameType21Led,HIGH);
  digitalWrite(gameType11Led,HIGH);
  digitalWrite(gameType6Led,HIGH);
  digitalWrite(confGameTypeLed,HIGH);
  digitalWrite(gameStartedLed,HIGH);
  digitalWrite(servePlayer2Led,HIGH);
  digitalWrite(servePlayer1Led,HIGH);
  digitalWrite(confServeLed,HIGH);
  digitalWrite(confPlayer1Led,HIGH);
  digitalWrite(confPlayer2Led,HIGH);*/
/*  digitalWrite(confNumberLed,LOW);
  digitalWrite(gameType21Led,LOW);
  digitalWrite(gameType11Led,LOW);
  digitalWrite(gameType6Led,LOW);
  digitalWrite(confGameTypeLed,LOW);
  digitalWrite(gameStartedLed,LOW);
  digitalWrite(servePlayer2Led,LOW);
  digitalWrite(servePlayer1Led,LOW);
  digitalWrite(confServeLed,LOW);
  digitalWrite(confPlayer1Led,LOW);
  digitalWrite(confPlayer2Led,LOW);*/
  delay(20);
  getButtonPressed();
  if(OLD_STATE != state){
    Serial.println(getState());
    OLD_STATE = state;
  }
  // start off with the highway getting green
  // The keyword "static" makes sure the variable
  // isn't destroyed after each loop


  switch (state)
  {
    case S_GNUMBERREQ_I:{
          //Serial.println("number req init");
          g_number_I();
          state =S_GNUMBERREQ_SEND;  
          break;
        }
    case S_GNUMBERREQ_SEND:{
          //Serial.println("number req send");
          g_number_S();
          state = S_GNUMBERREQ_RECEIVE;
          break;
    }
    case S_GNUMBERREQ_RECEIVE:{
          //Serial.println("number req receive");
          g_number_R();
          state = S_GNUMBERREQ_CHECK;
          break;
    }
    case S_GNUMBERREQ_CHECK:{
          //Serial.println("number req check");
            state = g_number_check();
            break;
    }
    case S_CHOOSENUMBER:{
      //Serial.println("choose number");
      state = choose_number();
      break;
    }
    case S_CHOOSETYPE:
    {  //Serial.println("choose type");
        state = choose_type();
        break;
    }
    case S_RANDOMSERVE:
     { //Serial.println("random serve");
           
           state = random_serve();
           break;
     }
    case S_CHOOSESERVE:{
          state = choose_serve();
          break;
    }
    case S_GAMESTART:{
          state = game_start();
          break;
    }
    case S_SENDRESULT_I:{
          //Serial.println("send result init");
          send_result_I();
          state = S_SENDRESULT_SEND;
          break;
        }
    case S_SENDRESULT_SEND:{
          //Serial.println("send result send");
        send_result_S();
        state = S_SENDRESULT_RECEIVE;
        break;
      }
    case S_SENDRESULT_RECEIVE:  {    
          //Serial.println("send result receive");
        send_result_R();
        state = S_SENDRESULT_CHECK;
        break;
      }
    case S_SENDRESULT_CHECK:{
          //Serial.println("send result check");
        state = send_result_C();
        break;
      }
    case S_SENDPERSIST:{
          //Serial.println("send persist");
        send_persist();
        state = S_GNUMBERREQ_I;
        break;
    }
  }
  //Serial.println("updating IO");
  update();
} 

