package main;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by bse71 on 07.03.2017.
 */
public class Pool<E extends Pool.IPoolObject> {
    private static final int OPTIMAL_CAPACITY = 10;
    private static final int CRITICAL_CAPACITY = 15;

    private LinkedBlockingQueue<E> objects = new LinkedBlockingQueue<E>(OPTIMAL_CAPACITY);
    private AtomicInteger count = new AtomicInteger(0);
    private ReentrantLock lock = new ReentrantLock(true);

    public E get() throws InterruptedException {
        if (getFreeCount() == 0){
            addToPool();
        }
        return objects.take();
    }

    public void ret(E object) throws InterruptedException {
        if (getFreeCount() < OPTIMAL_CAPACITY){
            object.toInitState();
            objects.put(object);
        } else count.decrementAndGet();
    }

    public int getFreeCount(){
        return objects.size();
    }

    private void addToPool(){
        lock.lock();
        if (isMustAdd()){
            count.incrementAndGet();
            objects.add( (E) new Object());
        }
        lock.unlock();
    }

    private boolean isMustAdd(){
        return ((getFreeCount() < 1) && (count.intValue() < CRITICAL_CAPACITY));
    }

    interface IPoolObject {
        void toInitState();
    }
}
