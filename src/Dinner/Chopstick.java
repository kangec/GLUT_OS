package Dinner;

public class Chopstick {
    private boolean avilable;
    private int id;
    private Philosopher taker;
    private Philosopher  owner;
    /**
     * @param id 哲学家的唯一身份信息ID
     */
    public Chopstick(int id) {
        this.id = id;
        avilable = true;
    }

    /**
     * @param taker 当前筷子的所有者
     * @exception InterruptedException
     * 让筷子变成被占有态
     */
    public synchronized void takeup(Philosopher taker) {
        this.setTaker(taker);
        while (!avilable) {
            try {
                taker.setStatus(Status.WAIT);
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        avilable = false;
        this.setOwner(taker);
    }

    /*
     *  放下筷子，让筷子变为可占有态
     * */
    public synchronized void putdown() {
        avilable = true;
        this.setOwner(null);
        notify();
    }

    public int getOwnerId(){
        return getOwner().getid();
    }

    public void setTaker(Philosopher taker) {
        this.taker = taker;
    }

    public Philosopher getOwner() {
        return owner;
    }

    public void setOwner(Philosopher owner) {
        this.owner = owner;
    }
}