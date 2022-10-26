package org.example;

import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 1;
                try{
                    lock.lock();
                    System.out.println(index+":"+lock);
                    while (true){
                        try {
                            lock.lock();
                            System.out.println((++index)+":"+lock);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (index==10){
                            break;
                        }
                    }
                }finally {
                    lock.unlock();
                }
            }
        }).start();
    }
}