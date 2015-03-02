import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
 
/**
 * 
 * @author - Rebecca Katz
 */
public class Lab implements Runnable {
 
    private static int studentCapacity;
    private static int studentsInLab;
    private static boolean isOpen;
    private Thread thread;
    private static String labName;
    private static boolean isActive;
    private static final int CLASS_TIME = 50000;
    private static Map<Student, String> exitSequence;
    private static Semaphore enterMutex;
    private static Semaphore enterLab;
    private static Semaphore releaseStudents;
 
 
    /**
     * Constructor
     * 
     * @param name
     * @param capacity
     */
    public Lab(String name, int capacity) {
        labName = name;
        studentCapacity = capacity;
        thread = new Thread(this, labName);
        isActive = false;
        exitSequence = new HashMap<Student, String>();
        studentsInLab = 0;
        enterMutex = new Semaphore(1, true);
        enterLab = new Semaphore(0, false);
        releaseStudents = new Semaphore(0, true);
    }
 

    @Override
    public void run() {
        msg("The lab is open.");
        isOpen = true;
        isActive = true;
        enterLab.release(enterLab.getQueueLength());
        lockLabThread();
        sortStudents();
        msg("The lab is closed.");
        isOpen = false;
        System.out.println("Total run time: " + Project2.age() / 1000 + " secs");
    }
    
    public static void enteredLab(Student a){
 
        try{
            enterMutex.acquire();
        }catch (InterruptedException ie){
            System.out.println(ie);
            ie.printStackTrace();
        }
         
        if (studentsInLab < studentCapacity){
            msg("Student: " + a.getName() + " entered the " + labName + ".");
            ++studentsInLab;
            exitSequence.put(a, a.getName());
            a.isInLab();
 
            if (studentsInLab == studentCapacity) {
                msg("Student: " + a.getName() + " is closing the door.");
                closeDoor();
            }
        }
        enterMutex.release();
    }
    
    public void start() {
        msg(" is starting.");
        thread.start();
    }
    
    public static boolean isOpen() {
        return isOpen;
    }
 
    public static boolean sessionActive() {
        return isActive;
    }

    public static String getLabName() {
        return labName;
    }
    
    public static boolean labIsFull() {
        return studentsInLab == studentCapacity;
    }
 
    public static int getClassTime() {
        return CLASS_TIME;
    }

    private void sortStudents() {
        ArrayList<Entry<Student, String>> stu_inorder = sortStudentsByName(exitSequence);
 
        for (int i = stu_inorder.size() - 1; i >= 0; i--) {
            if (stu_inorder.get(i).getKey().getThread().isAlive()) {
                try {
                    stu_inorder.get(i).getKey().waitToLeave();
                    //stu_inorder.get(i).getKey().getThread().interrupt();
                    stu_inorder.get(i).getKey().getThread().join();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        }
 
    }
 
    private ArrayList<Entry<Student, String>> sortStudentsByName(
            Map<Student, String> exitSequence2) {
        ArrayList<Map.Entry<Student, String>> l = new ArrayList<Entry<Student, 
        		String>>( ((Map<Student, String>) exitSequence2).entrySet());
 
        Collections.sort(l, new Comparator<Map.Entry<Student, String>>(){
            public int compare(Map.Entry<Student, String> o1,
                    Map.Entry<Student, String> o2){
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return l;
    }
 
    private void lockLabThread(){
        try {
            releaseStudents.acquire();
        } catch (InterruptedException ie){
            System.out.println(ie);
        }
    }
 
    public static void signalToRelease(){
        releaseStudents.release();
    }
 
    private static void msg(String a){
        System.out.println("{" + Project2.age() + "} " + labName + " " + a);
    }
 
    public static void waitQueue(){
        try {
            enterLab.acquire();
        } catch (InterruptedException ie) {
            System.out.println(ie);
            ie.printStackTrace();
        }
    }
 
    public static void closeDoor(){
        isOpen = false;
    }
}