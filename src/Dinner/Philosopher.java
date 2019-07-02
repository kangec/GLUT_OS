package Dinner;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public  class Philosopher extends Thread {
    Status status = Status.THINK;
    private Room room;
    private Chopstick left, right;
    private int personId;
    public final String wait = "等待中", eat = "就餐中", think = "思考中";
    private String message;
    private Random rand = new Random();

    public Philosopher(Room room, int id, Chopstick left, Chopstick right) {
        this.room = room;
        this.personId = id;
        this.left = left;
        this.right = right;
        message = wait;
    }

    public void eat() {
        left.takeup(Philosopher.this);
        right.takeup(Philosopher.this);
        setStatus(Status.EAT);
        try {
            TimeUnit.SECONDS.sleep(0x2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void think() {
        left.putdown();
        right.putdown();
        setStatus(Status.THINK);
        try {
            TimeUnit.SECONDS.sleep(0x2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param status 哲学家的状态消息
     */
    public void setStatus(Status status) {
        switch (status) {
            case WAIT:
                message = wait;
                break;
            case THINK:
                message = think;
                break;
            case EAT:
                message = eat;
                break;
        }
    }

    public void run() {
        while (true) {
            think();
            try {
                sleep((int) ((rand.nextFloat() + 0.3) * 5000));
            }
            catch (InterruptedException e) {
            }
            room.enter();
            eat();
            try {
                sleep((int) ((rand.nextFloat() + 0.5) * 4000));
            }
            catch (InterruptedException e) {
            }
            room.exit();
        }
    }

    public int getPersonId() {
        return personId;
    }
    public Chopstick getLeft() {
        return left;
    }
    public Chopstick getRight() {
        return right;
    }
    public String getStatus() {
        return message;
    }
}