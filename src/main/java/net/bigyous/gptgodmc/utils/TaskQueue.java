package net.bigyous.gptgodmc.utils;

import net.bigyous.gptgodmc.interfaces.Function;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 

class Task<T> implements Runnable
{
    private Function<T> task;
    private T object;
    public Task(Function<T> task, T object){
        this.task = task;
        this.object = object;
    }

    public void run(){
        this.task.run(object);
    }
}


public class TaskQueue<T> {
    private Function<T> task;
    private ExecutorService pool;
    public TaskQueue(Function<T> task){
        this.task = task;
        this.pool = Executors.newCachedThreadPool();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void insert(T object){
        pool.execute(new Task(task, object));
    }

}

