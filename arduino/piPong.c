
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
const int nextButton = 0;
const int switchButton = 15;
const int plusButton = 2;
const int lessButton = 4;

int nextLastState = LOW;
int switchLastState = LOW;
int plusLastState = LOW;
int lessLastState = LOW;

bool nextPressed = false;
bool switchPressed = false;
bool plusPressed = false;
bool lessPressed = false;

 
const int ASevSegScore = 3;
const int BSevSegScore = 3;
const int CSevSegScore = 3;
const int DSevSegScore = 3;
const int ESevSegScore = 3;
const int FSevSegScore = 3;
const int GSevSegScore = 3;
const int dig1SevSegScore = 3;
const int dig2SevSegScore = 3;
const int dig3SevSegScore = 3;
const int dig4SevSegScore = 3;

int scorePlayer1 = 0;
int scorePlayer2 = 0;

const int ASevSegNumber = 3;
const int BSevSegNumber = 3;
const int CSevSegNumber = 3;
const int DSevSegNumber = 3;
const int ESevSegNumber = 3;
const int FSevSegNumber = 3;
const int GSevSegNumber = 3;
const int dig1SevSegNumber = 3;
const int dig2SevSegNumber = 3;

int gameNumber = 0;
 
const int confPlayer1Led = 6;
const int confPlayer2Led = 6;
const int confServeLed = 6;
const int confNumberLed = 6;
const int confGameTypeLed = 6;

int confScore = -1;

const int servePlayer1 = 6;
const int servePlayer2 = 6;

int serve = -1;

const int gameType6 = 6;
const int gameType11 = 6;
const int gameType21 = 6;
int gameType = 6;

static int state = S_GNUMBERREQ_I;

const int gameStarted = 6;
void routine(){
      //Serial.println("bem vindo moleke");
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

  pinMode(servePlayer1,OUTPUT);
  pinMode(servePlayer2,OUTPUT);
  pinMode(gameType6,OUTPUT);
  pinMode(gameType11,OUTPUT);
  pinMode(gameType21,OUTPUT);

  pinMode(gameStarted,OUTPUT);
  routine();
}
void getButtonPressed(){
  int plusState = digitalRead(plusButton);
  int nextState = digitalRead(nextButton);
  int switchState = digitalRead(switchButton);
  int lessState = digitalRead(lessButton);

  if (lessState != lessLastState) {
    if (lessState == HIGH) {
      Serial.println("Less Pressed");
      lessPressed = true;
    }
    delay(50);
  }else  if (plusState != plusLastState) {
    if (plusState == HIGH) {
      Serial.println("Plus Pressed");
      plusPressed = true;
      }
    delay(50);
  }else  if (switchState != switchLastState) {
    if (switchState == HIGH) {
      Serial.println("Switch Pressed");
      switchPressed = true;
    } 
    delay(50);
  }else  if (nextState != nextLastState) {
    if (nextState == HIGH) {
      Serial.println("Next Pressed");
      nextPressed = true;
    }
    delay(50);
  }
  plusLastState = plusState;
  nextLastState = nextState;
  switchLastState = switchState;
  lessLastState = lessState;
}
void g_number_I(){
  Serial.println("Game number init");
  delay(500);
}
void g_number_S(){
  Serial.println("Game number send");
  delay(1000);
}
void g_number_R(){
  Serial.println("Game number receive");
  delay(1000);
}
int g_number_check(){
  Serial.println("Checking number received");
  delay(1000);
  Serial.println("Number ok");
  return S_CHOOSENUMBER;
}
int choose_number(){
  int ret = S_CHOOSENUMBER;
  //Serial.println("TARACE");
  if(switchPressed ||plusPressed){
    Serial.println("+");
    gameNumber++;
  }else if(lessPressed){
    Serial.println("-");
    gameNumber--;
  }else if(nextPressed){
    Serial.println("Number choosed, passing");
    delay(500);
    ret = S_CHOOSETYPE;
  }
  return ret;
}
int choose_type(){
  //Serial.println("hey");
  int ret = S_CHOOSETYPE;
  if(plusPressed||switchPressed){
    if(gameType == 6){
      gameType = 11;
    }else if(gameType == 11){
      gameType = 21;
    }else{
      gameType = 6;
    }
  }else if(lessPressed){
    if(gameType == 6){
      gameType = 21;
    }else if(gameType == 11){
      gameType = 6;
    }else{
      gameType = 11;
    }
  }else if(nextPressed){
    Serial.println("Type choosed, passing");
    delay(500);
    ret = S_RANDOMSERVE;
  }
  return ret;
}
void random_serve(){
  int serveRand = random(1234);
  serveRand = serveRand %2;
  if(serveRand == 0){
    serve = -1;
  }else{
    serve = 1;
  }
}
int choose_serve(){
  int ret = S_CHOOSESERVE;
  if(lessPressed||plusPressed||switchPressed){
    Serial.println("Switching service");
    if(serve == -1){
      serve = 1;
    }else{
      serve = -1;
    }
  }else if(nextPressed){
    Serial.println("Starting Game");
    ret = S_GAMESTART;
  }
  return ret;
}
int game_start(){
  int ret = S_GAMESTART;
  if(plusPressed){
    updated = true;
    Serial.println("Augmenting score");
    if(confScore == -1){
      scorePlayer1++;
    }else{
      scorePlayer2++;
    }
  }else if(switchPressed){
    updated = true;
    Serial.println("Switching player");
    if(confScore == -1){
      confScore = 1;
    }else{
      confScore = -1;
    }
  }else if(lessPressed){
    updated = true;
    Serial.println("Reducing score");
    if(confScore == -1){
      scorePlayer1--;
    }else{
      scorePlayer2--;
    }
  }else if(nextPressed){
    Serial.println("Game over");
    ret = S_SENDRESULT_I;
  }
  return ret;
}
void send_result_I(){
  Serial.println("Send result init");
}
void send_result_S(){
  Serial.println("Send result send");
  delay(1000);
}
void send_result_R(){
  Serial.println("Send result receive");
  delay(1000);
}
int send_result_C(){
  Serial.println("Send result check");
  delay(500);
  Serial.println("result ok");
  return S_SENDPERSIST;
}
void send_persist(){
  Serial.println("Send persist");
  delay(500);
} 
void ledUpdate(){
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
  /*//Serial.println("serve :"+serve ==-1 ? "Player 1 ":"Player 2");
  //Serial.println("serve :"+serve ==-1 ? "Player 1 ":"Player 2");*/
  switch(gameType){
    case 6:
      //Serial.println("Game type 6");
      break;
    case 11:
      //Serial.println("Game type 11");
      break;
    case 21:
      //Serial.println("Game type 21");
      break;
  }
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
  sevSegUpdate();
  resetButton();
}
void loop()
{
  getButtonPressed();
  if(OLD_STATE != state){
    Serial.println(state);
    OLD_STATE = state;
  }
  // start off with the highway getting green
  // The keyword "static" makes sure the variable
  // isn't destroyed after each loop

 
  switch (state)
  {
    case S_GNUMBERREQ_I:
      //Serial.println("number req init");
      g_number_I();
      state =S_GNUMBERREQ_SEND;  
      break;
    case S_GNUMBERREQ_SEND:
      //Serial.println("number req send");
      g_number_S();
      state = S_GNUMBERREQ_RECEIVE;
      break;
 
    case S_GNUMBERREQ_RECEIVE:
      //Serial.println("number req receive");
      g_number_R();
      state = S_GNUMBERREQ_CHECK;
      break;
 
    case S_GNUMBERREQ_CHECK:
      //Serial.println("number req check");
      state = g_number_check();
      break;
 
    case S_CHOOSENUMBER:
      //Serial.println("choose number");
      state = choose_number();
      break;
 
    case S_CHOOSETYPE:
      //Serial.println("choose type");
      state = choose_type();
      break;
 
    case S_RANDOMSERVE:
      //Serial.println("random serve");
      random_serve();
      state = S_CHOOSESERVE;
      break;

    case S_CHOOSESERVE:
      //Serial.println("choose serve");
      state = choose_serve();
      break;
 
    case S_GAMESTART:
      //Serial.println("game start");
      state = game_start();
      break;
 
    case S_SENDRESULT_I:
      //Serial.println("send result init");
      send_result_I();
      state = S_SENDRESULT_SEND;
      break;
    case S_SENDRESULT_SEND:
      //Serial.println("send result send");
      send_result_S();
      state = S_SENDRESULT_RECEIVE;
      break;
    case S_SENDRESULT_RECEIVE:      
      //Serial.println("send result receive");
        send_result_R();
        state = S_SENDRESULT_CHECK;
      break;
    case S_SENDRESULT_CHECK:
      //Serial.println("send result check");
      state = send_result_C();
      break;
    case S_SENDPERSIST:
      //Serial.println("send persist");
      send_persist();
      state = S_GNUMBERREQ_I;
      break;
 
  }
  //Serial.println("updating IO");
  update();
} 

