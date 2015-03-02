/**
 * CS340
 * Project 1- Online Office Hours
 * 
 * Due: 8/1/14
 * @author- Rebecca Katz
 */

public class Project1{
	private static long startTime = System.currentTimeMillis();

	protected static final long age(){
		return System.currentTimeMillis() - startTime;
	}
	    
	public static void main(String args[]){
		
		Student[] students = new Student[15];	//Student thread containing 15 students
        Teacher teacher;						//Teacher thread
        Lab lab;								//Lab thread
        int ch = 65;
        char letter;
 
        for(int i = 0; i < students.length; i++){
            letter = (char) (ch + i);
            students[i] = new Student(Character.toString(letter));
        }
 
        teacher = new Teacher("Teacher");
        lab = new Lab("Lab");
 
        for(int i = 0; i < students.length; i++){
            students[i].start();
        }
 
        lab.start();
        teacher.start();
 
    }
 }