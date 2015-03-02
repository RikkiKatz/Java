/**
 * Returns status of a question
 * 
 * @author- Rebecca Katz
 *
 */

public class Question{
	private char type; 					//Question Type A or B
	private boolean isAnswered; 		//Is the question answered
	private long timeStamp;				//Time that question was created
	private Student current;			//The student who asked question
	private int QID;					//ID for question

	/**
	 * Constructor sets the type of question and creation time.
	 * 
	 * @param thread
	 */
	public Question(char thread, int ID, Student student){
		type = thread;
		current = student;
		QID = ID;
		timeStamp = Project1.age();
		isAnswered = false;
	}

	public String toString(){
		String output = "Student: " + current.getName() + " Question Number " + QID; 
		return output;
	}

	public long getTimeStamp(){
		return timeStamp;
	}

	public int getQID(){
		return QID;
	}

	public char getType(){
		return type;
	}

	public Student getStudent(){
		return current;
	}

	public String getStudentName(){
		return current.getName();
	}

	public boolean getQAnswered(){
		return isAnswered;
	}

	public void setQAnswered(boolean answer){
		isAnswered = answer;
	}
}