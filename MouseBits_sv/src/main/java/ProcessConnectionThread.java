import javax.microedition.io.StreamConnection;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.InputStream;

/**
 * Created by joao on 4/11/14.
 */
public class ProcessConnectionThread implements Runnable{
    private StreamConnection mConnection;

    // Constant that indicate command from devices
    private static final int EXIT_CMD = -1;
    private static final int KEY_RIGHT = 1;
    private static final int KEY_LEFT = 2;
    private static final int MOVE_MOUSE=20;

    private static double sensability = 5;

    public ProcessConnectionThread(StreamConnection connection)
    {
        mConnection = connection;
    }

    @Override
    public void run() {
        try {
            // prepare to receive data
            InputStream inputStream = mConnection.openInputStream();

            System.out.println("waiting for input");

            while (true) {
                int command = inputStream.read();

                if (command == EXIT_CMD)
                {
                    System.out.println("finish process");
                    break;
                }

                try {
                    Robot robot = new Robot();
                    switch (command) {
                        case KEY_RIGHT:
                            robot.keyPress(KeyEvent.VK_RIGHT);
                            System.out.println("Right");
                            break;
                        case KEY_LEFT:
                            robot.keyPress(KeyEvent.VK_LEFT);
                            System.out.println("Left");
                            break;
                        case MOVE_MOUSE:
                            //System.out.println("Move Mouse");
                            int x = inputStream.read();
                            int y = inputStream.read();
                            int z = inputStream.read();

                            x= x<127? x : x -255;
                            y= y<127? y : y -255;
                            z= z<127? z : z -255;

                            System.out.println("(x->"+x+",y->"+y+",z->"+z+")");

                            movePointer(x, y);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void movePointer(int x, int y){
        Point current = MouseInfo.getPointerInfo().getLocation();;

        int currentX = (int) current.getX();
        int currentY = (int) current.getY();

       // System.out.println("monitor x="+currentX+ " monitor y="+currentY);
        try {
            Robot robot = new Robot();
            robot.mouseMove((int)(currentX - y * sensability),(int)(currentY - x * sensability));
        }catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void rightClick(){
        try {
            Robot robot = new Robot();
            robot.mousePress(1);
        }catch (AWTException e) {
            e.printStackTrace();
        }

    }
}

