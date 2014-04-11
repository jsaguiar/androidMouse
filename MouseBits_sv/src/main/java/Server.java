import robot.MouseMove;

/**
 * Created by joao on 4/11/14.
 */
public class Server {

    public static void main(String[] args) {
        Thread waitThread = new Thread(new WaitThread());
        waitThread.start();
        //MouseMove mouseMove = new MouseMove();
    }

}
