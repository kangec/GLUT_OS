package Dinner;

public class Room {
    static int room;

    /*
     * @param room 信号量，room属于共享资源
     * */
    public Room(int room) {
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