/**
 * Question thread
 * 
 * @author - Rebecca Katz
 *
 */
public class Question{
	private char type; 					//Question Type A or B
	private boolean isAnswered; 		//Is the question answered
	private long timeStamp;				//Time that question was created
	private Student current;			//The student who asked question
	private int QID;					//ID for question
     
    /**
     * Constructor
     * 
     * @param thread, ID, student
     */
    public Question(char thread, int ID, Student student){
		type = thread;
		current = student;
		QID = ID;
		timeStamp = Project2.age();
		isAnswered = false;
	}
    
    
    public char getType(){
        return type;
    }
     
    public void setQAnswered(boolean answer){
        isAnswered = answer;
    }

    public boolean getQAnswered(){
        return isAnswered;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public Student getStudent(){
        return current;
    }

    public String getStudentName(){
        return current.getName();
    }
     
    public int getQID(){
        return QID;
    }
    
    public String toString(){
        String out = "Student: " + current.getName() + " Question# " + QID; 
        return out;
    }
}