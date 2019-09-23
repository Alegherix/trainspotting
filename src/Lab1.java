import TSim.*;

import java.util.concurrent.Semaphore;

public class Lab1 {

  TSimInterface tsi = TSimInterface.getInstance();


  private final Sensor[] endSensors = {
          new Sensor(15,3),
          new Sensor(15,5),
          new Sensor(15,11),
          new Sensor(15,13),
  };

  public Lab1(int speed1, int speed2) {


    try {
      Thread train1 = new Thread(new Train(speed1));
      Thread train2 = new Thread(new Train(speed2));


      tsi.setSpeed(1, train1.get);
//      simpleRun();
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }
  }


//  void simpleRun(){
//    try {
//      tsi.setSwitch(SWITCH.TOP.X, SWITCH.TOP.Y, TSimInterface.SWITCH_RIGHT);
//      tsi.setSwitch(4,9, TSimInterface.SWITCH_RIGHT);
//      tsi.setSwitch(3,11, TSimInterface.SWITCH_RIGHT);
//      if (tsi.getSensor(1).getStatus() == SensorEvent.ACTIVE && ){
//          tsi.setSpeed(1, 0);
//          Thread.sleep(1500);
//          tsi.setSpeed(1, -20);
//      }
//
//
//
//
//    } catch (CommandException | InterruptedException e) {
//      e.printStackTrace();
//    }
//  }
}

class Sensor{
    int x;
    int y;
    // Semaphore som delas mellan
    private Semaphore semaphore;

    public Sensor(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Sensor(int x, int y, Semaphore semaphore) {
        this.x = x;
        this.y = y;
        this.semaphore = semaphore;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}

class Train implements Runnable {


    public Train(int velocity) {
        this.velocity = velocity;
    }

    private int velocity = 15;
    private final int waitTime = 1000 * (20 * velocity);
    // Som den ibland kan hålla -> Möjligt att den inte behöver en egen
    private Sensor currentSensor;


    public int getVelocity() {
        return velocity;
    }


    @Override
    public synchronized void run() {

        while (true){
            if (currentSensor.getSemaphore().tryAcquire()){
                try {
                    // När vi har Semaphoren
                    currentSensor.getSemaphore().acquire();

                    // Om vi bör ändra switches så ändra dem
                    if (shouldChangeSwitches()) changeSwitches();


                    currentSensor.getSemaphore().notify();
                    currentSensor.getSemaphore().release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    shouldWaitAtSensor()
                    currentSensor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeSwitches() {

    }


    // 3 Checks som bör göra varje gång vi kommer till en Sensor.

    public boolean shouldChangeSwitches(){
        return false;
    }

    public boolean shouldWaitAtSensor(){
        // Kanske bör lägga in currentSensor.getSemaphore.wait();
        return false;
    }

    public boolean shouldAllowForPassing(){
        return false;
    }





}




/**
 * Detta är klassen som koordinerar
 */
class TrainCoordinator implements Runnable{

    // TODO: 2019-09-19 - Skapa constructor för att passera hastigheten till tågen

    final Thread train1 = new Thread(new Train());
    final Thread train2 = new Thread(new Train());

    final Semaphore firstSemaphore = new Semaphore(1);
    final Semaphore secondSemaphore = new Semaphore(1);
    final Semaphore thirdSemaphore = new Semaphore(1);
    final Semaphore fourthSemaphore = new Semaphore(1);
    final Semaphore fifthSemaphore = new Semaphore(1);

    private final Sensor[] crossingSensors = {
            new Sensor(6,6, firstSemaphore),
            new Sensor(9,6, firstSemaphore),
            new Sensor(10,8, firstSemaphore),
            new Sensor(11,7, firstSemaphore),
    };


    final TSimInterface tSimInterface = TSimInterface.getInstance();


    @Override
    public void run() {
        train1.start();
        train2.start();
    }

}

enum SWITCH{
  TOP(17,7), RIGHT(15,9), LEFT(4,10), BOTTOM(3,11);
  int X;
  int Y;

  SWITCH(int x, int y){
    this.X = x;
    this.Y = y;
  }
}



// TODO: 2019-09-19 - Fixa Sensorer på Mappen
// TODO: 2019-09-19  - Klura ut hur sensorerna ska fungera

// Calla
// De är Sensorerna som håller semaphorer och som de ibland kan dela på,
// De är sedan upp till tågen att calla aquire på, sensorn den står på, dvs Tåget behöver ingen egen referens
// Till en semaphore utan den använder sensorerns semaphore.

// När man har semaphore, kolla riktning, och slå sedan nästkommande switch i rätt riktning - lägg den
// Funktionalitetn hos tågen

// Tanken är att vi ska calla getsensor() när vi redan är på en sensor, och tåget är noll, och sedan så kör vi denna
// Därefter väntar vi på att tråden ska kunna återupptas. 


