package net.bigyous.gptgodmc.interfaces;

@FunctionalInterface
public interface Function<T> {
    public void run(T object);
}