package mx.unam.fc.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import mx.unam.fc.concurrent.callabletasks.CoarseCallable;
import mx.unam.fc.concurrent.callabletasks.LazyCallable;
import mx.unam.fc.concurrent.callabletasks.LockFreeCallable;
import mx.unam.fc.concurrent.lists.CoarseGrained;
import mx.unam.fc.concurrent.lists.LazyList;
import mx.unam.fc.concurrent.lists.NonBlocking;
import mx.unam.fc.concurrent.tasks.TaskCoarse;
import mx.unam.fc.concurrent.tasks.TaskLazy;
import mx.unam.fc.concurrent.tasks.TaskLockFree;
import mx.unam.fc.concurrent.text.Set;

import java.util.Random;


public class App {

    public static void main(String[] args) throws ExecutionException {
        
        int numProcessors = Runtime.getRuntime().availableProcessors(); // El num de procesos que tiene tu compu 
        ExecutorService executor = Executors.newFixedThreadPool(90);//Creamos una pool de procesos de n hilos, intenta primero con tu num de procesos

        CoarseGrained coarselist = new CoarseGrained(); //instancia de la clase
        LazyList lazylist = new LazyList();//instancia de la clase
        NonBlocking locklist = new NonBlocking();//instancia de la clase


        

        Random rand = new Random(); //Para crear num randoms
        Set set = new Set("alphabet");
        System.out.println("Number of operations: "+ set.numOperations()); 
        
		List<String> alphabetList = set.get();

            try {
                List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
                for (int i = 0; i < alphabetList.size(); i++) {//Vamos a crear distintas instancias de Tareas en el tamaño de la lista
                    int numRand = rand.nextInt(100);//Genero un numero random
                    futures.add(executor.submit(new CoarseCallable(alphabetList.get(i), coarselist,numRand))); //Para ejecutar CoarseList
                    //futures.add(executor.submit(new LazyCallable(alphabetList.get(i), lazylist,numRand))); // Para ejecutar LazyList
                    //futures.add(executor.submit(new LockFreeCallable(alphabetList.get(i), locklist,numRand))); //Para ejecutar LockFree
                }
                for (int i = 0; i < futures.size(); i++) {
                    Boolean result = futures.get(i).get();
                    //System.out.printf("\n Result: "+result);
                }
                
            } catch (Exception e) {
                System.out.printf("Error %s\n", e.getMessage());
            }
          
        executor.execute(new TaskCoarse("non", coarselist,110));//ejecuto la tarea con num 110 que corresponde a imprimir después de 100ms para CoarseList
        //executor.execute(new TaskLazy("non", lazylist,110));//ejecuto la tarea con num 110 que corresponde a imprimir después de 100ms para LazyList
        //executor.execute(new TaskLockFree("non", locklist,110));//ejecuto la tarea con num 110 que corresponde a imprimir después de 100ms para LockFree

        executor.shutdown();//Detengo la pool de hilos
    }
    
}