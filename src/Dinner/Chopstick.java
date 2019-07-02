package Dinner;

public class Chopstick {
    private boolean avilable;
    private int id;
    private Philosopher taker;
    private Philosopher  owner;
    private DinnerCtrl.DinnerTable table;

    /**
     * @param id 哲学家的唯一身份信息ID
     * @param table 状态消息面板
     */
    public Chopstick(int id, DinnerCtrl.DinnerTable table) {
        this.id = id;
        this.table = table;
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
        table.repaint();
    }

    /*
     *  放下筷子，让筷子变为可占有态
     * */
    public synchronized void putdown() {
        avilable = true;
        this.setOwner(null);
        notify();
        table.repaint();
    }

    public int getOwnerId(){
        return this.getOwner().getPersonId();
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