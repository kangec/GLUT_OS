package Dinner;

public class Semaphore {
    static int room;

    /*
     * @param room 信号量，room属于共享资源
     * */
    public Semaphore(int room) {
        this.room = room;
    }

    public synchronized void enter() {
        while (room == 0) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        room--;
    }

    public synchronized void exit() {
        room++;
        notify();
    }
}