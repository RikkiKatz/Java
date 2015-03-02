/**
 * Lab class sorts students into lab up to capacity
 * 
 * @author- Rebecca Katz
 */

import java.util.Map.Entry;
import java.util.*;

public class Lab implements Runnable{

	private static int capacity = 10;		//lab capacity
	private static int studentsInLab;		//tracks number of students currently in the lab
	private static boolean isOpen; 			//check if lab is open

	private Thread thread;
	private Random rand;
	private static String lab;
	private static boolean isActive;   
	private static final int chatTime = 50000;


	private static Map<Student, String> exit;
	private static boolean studentExits;

	/**
	 * Constructor initializes variables for thread runtime
	 * 
	 * @param lab
	 */
	public Lab(String lab) {
		this.lab = lab;
		rand = new Random();
		thread = new Thread(this, lab);
		setActive(false);
		studentExits = false;
		exit = new HashMap<Student, String>();
		studentsInLab = 0;
	}

	@Override
	public void run() {
		msg("The lab is open.");
		isOpen = true;
		setActive(true);

		while(!studentExits){
			try{
				Thread.sleep(rand.nextInt(4000));
			}catch (InterruptedException ie){
				System.out.println(ie);
			}
		}
		sortStudents();
				msg(" is closed.");
				isOpen = false;
				System.out.println("Total run time: " + Project1.age()/1000 + " seconds");
	}
	
	public void start(){
		msg(" session is starting.");
		thread.start();
	}
	

	public static synchronized boolean LabIsOpen(){
		return isOpen;
	}

	private ArrayList<Entry<Student, String>> sortByName(Map<Student, String> exit2){
		ArrayList<Map.Entry<Student, String>> l = new ArrayList<Entry<Student, String>>(
				((Map<Student, String>) exit2).entrySet());

		Collections.sort(l, new Comparator<Map.Entry<Student, String>>(){
			public int compare(Map.Entry<Student, String> o1,
					Map.Entry<Student, String> o2){
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		return l;
	}
	
	private void sortStudents(){
		ArrayList<Entry<Student, String>> stu_inorder = sortByName(exit);

		for(int i = stu_inorder.size() - 1; i >= 0; i--){
			if (stu_inorder.get(i).getKey().getThread().isAlive()) {
				try{
					stu_inorder.get(i).getKey().okToLeave();
					stu_inorder.get(i).getKey().getThread().interrupt();
					stu_inorder.get(i).getKey().getThread().join();
				} catch (InterruptedException ie) {
					System.out.println(ie);
				}
			}
		}
	}
	
	public synchronized static void enteredLab(Student a) {
		if(studentsInLab < capacity){
			msg("Student: " + a.getName() + " entered the " + lab);
			++studentsInLab;
			exit.put(a, a.getName());
			a.isInLab();

			if(studentsInLab == capacity){
				msg("Student: " + a.getName() + " is closing the door.");
				closeDoor();
			}
		}
	}

	public synchronized static void signalToRelease(){
		studentExits = true;
	}

	private static void msg(String m){ 
		System.out.println("{" + Project1.age() + "} " + lab + " " + m);
	}

	public static void closeDoor(){
		isOpen = false;
	}

	public static boolean isActive(){
		return isActive;
	}

	public static void setActive(boolean isActive){
		Lab.isActive = isActive;
	}
	
	public static int getChatTime() {
		return chatTime;
	}
}