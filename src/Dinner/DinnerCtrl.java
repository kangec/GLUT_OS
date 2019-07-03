package Dinner;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

public class DinnerCtrl extends JFrame {
    private static final int personCount = 5;
    private static Semaphore semaphore = new Semaphore(personCount - 1);
    boolean started = false;
    boolean stopped = false;
    final static Chopstick[] chopstick = new Chopstick[personCount];
    final static Philosopher[] philos = new Philosopher[personCount];
    private Image offScreenImage;

    class DinnerTable extends JLayeredPane
    {
        private int counts;
        private int r1 = 20;
        private int CHOPS_LENGTH = 38;
        private int DISTANCE = 100;
        private int VERTICAL_DIS = -20;
        private int r2 = r1 + CHOPS_LENGTH;
        private int r3 = r2 + DISTANCE;
        private int delta;
        private int delta0 = -18;

        public DinnerTable(int counts) {
            setOpaque(false);
            this.setPreferredSize(new Dimension(600, 400));
            this.counts = counts;
            this.delta = 360 / counts;
        }

        @Override
        public void paintComponent(Graphics page) {
            super.paintComponent(page);
            int x1, y1, x2, y2, x3, y3;
            page.fillOval(200, 150, 150, 150);
            ImageIcon img = new ImageIcon("640.png");
            img.setImage(img.getImage().getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_AREA_AVERAGING));
            page.drawImage(img.getImage(),0,0,this);
            Color c = page.getColor();
            page.setColor(Color.GREEN);
            page.fillOval(250, 200, 50, 50);
            page.setColor(c);

            for (int i = 0; i < counts; i++) {
                x1 = 275 + (int) (r1 * Math.cos(((delta * i)+ delta0*(-5)  ) * Math.PI / 180));
                y1 = 225 + (int) (r1 * Math.sin(((delta * i) + delta0*(-5)  ) * Math.PI / 180));
                x2 = 275 + (int) (r2 * Math.cos(((delta * i) + delta0 *(-5) ) * Math.PI / 180));
                y2 = 225 + (int) (r2 * Math.sin(((delta * i) + delta0 *(-5) ) * Math.PI / 180));
                x3 = 275 + (int) (r3 * Math.cos(((delta * i) + delta0 ) * Math.PI / 180));
                y3 = 225 + (int) (r3 * Math.sin(((delta * i) + delta0 ) * Math.PI / 180));

                //pint chopstick
                page.setColor(new Color(128,64,0));
                if (chopstick[i].getOwner() != null && philos[i].getPersonId() == chopstick[i].getOwnerId()
                        && philos[i].getLeft() == chopstick[i]) {
                    page.drawLine(x3 - 45, y3, x3 - 45, y3 + CHOPS_LENGTH);
                }
                if (chopstick[(i + 1) % personCount].getOwner() != null
                        && philos[i].getPersonId()  == chopstick[(i + 1) % personCount].getOwnerId()
                        && philos[i].getRight() == chopstick[(i + 1) % personCount]) {

                    page.drawLine(x3 + 45, y3, x3 + 45, y3 + CHOPS_LENGTH);
                }
                if (chopstick[i].getOwner() == null) {
                    page.drawLine(x1, y1, x2, y2);
                }
                page.setColor(Color.RED);
                page.setFont(new Font("黑体",0,16));
                String str= philos[i].getStatus();
                page.drawString(str,x3-30,y3+VERTICAL_DIS);

                page.setColor(Color.BLACK);
                page.drawString("哲学家" + (i + 1), x3 - 30, y3 +40+  VERTICAL_DIS);

                page.setColor(new Color(192,192,192));
                if(i+1 == 1)
                    page.fillOval(x3 - 105, y3 + VERTICAL_DIS + 40, 22, 22);
                if(i+1 == 2)
                    page.fillOval(x3 - 70, y3 + VERTICAL_DIS - 70 , 22, 22);
                if(i+1 == 3)
                    page.fillOval(x3 + 40, y3 + VERTICAL_DIS - 75, 22, 22);
                if(i+1 == 4)
                    page.fillOval(x3 + 80, y3 + VERTICAL_DIS + 35, 22, 22);
                if(i+1 == 5)
                    page.fillOval(x3 - 10, y3 + VERTICAL_DIS + 105, 22, 22);

            }
        }
    }

    public void launch() {
        setFont(new Font("黑体",0,15));
        setLayout(new BorderLayout());
        setTitle("哲学家就餐问题GUI演示");
        setPreferredSize(new Dimension(600, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        JPanel btnPanel = new JPanel();
        btnPanel.setBounds(100, 20, 400, 200);
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new FlowLayout());
        JButton startButton = new JButton("START");
        JButton stopButton = new JButton("STOP");
        JButton continueButton = new JButton("CONTINUE");
        JButton endButton = new JButton("END");
        btnPanel.add(startButton);
        btnPanel.add(stopButton);
        btnPanel.add(continueButton);
        btnPanel.add(endButton);
        startButton.addActionListener(new StartListener());
        continueButton.addActionListener(new ContinueListener());
        stopButton.addActionListener(new StopListener());
        endButton.addActionListener(new EndListener());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        DinnerTable table = new DinnerTable(personCount);
        table.setBounds(20, 50, 500, 420);
        //初始化筷子
        for (int i = 0; i < chopstick.length; i++) {
            chopstick[i] = new Chopstick(i);
        }
        //初始化哲学家
        for (int i = 0; i < philos.length; i++) {
            philos[i] = new Philosopher(semaphore, i,
                    chopstick[i], chopstick[(i + 1) % personCount]);
        }
        mainPanel.add(table);
        add(btnPanel,BorderLayout.SOUTH);
        getContentPane().add(mainPanel,BorderLayout.CENTER);
        setResizable(false);
        setVisible(true);
        while(true) {
            table.repaint();
            try {
                Thread.sleep(1000/120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class StartListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (!started) {
                started=true;
                for (int i = 0; i < personCount; i++) {
                    philos[i].start();
                }
            }
        }
    }
    private class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            for (int i = 0; i < personCount; i++) {
                philos[i].suspend();
            }
            stopped = true;
        }
    }
    private class ContinueListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (stopped) {
                for (int i = 0; i < personCount; i++) {
                    philos[i].resume();
                }
                stopped = false;
            }
        }
    }

    //双缓冲
    @Override
    public void update(Graphics g) {
        if(offScreenImage == null) {
            offScreenImage = this.createImage(this.getWidth(),this.getHeight());
        }
        Graphics off = offScreenImage.getGraphics();
        paint(off);
        g.drawImage(offScreenImage,0,0,null);
    }


    private class EndListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        new DinnerCtrl().launch();
    }
}
