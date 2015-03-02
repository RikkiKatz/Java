/**
 * Tracks teacher thread.
 * 
 * @author - Rebecca Katz
 */

import java.util.Random;
import java.util.Vector;

public class Teacher implements Runnable {

	private final long arrivalTime = System.currentTimeMillis();	//Tracks student arrival time
	private static String teacherName;								//String for teachers name
	private Thread thread;
	private Random rand;
	
	private static Vector<Question> question;						//Vector for Student Questions from question thread
	private static Vector<Student> student;					//Vector for Student Queue from student thread
	
	private static boolean startingOnlineSession;					//True/false for starting an online session
	private static boolean studentLastQuestion;						//True/false for students last question
	private static Question currentQuestion;						//Tracks current question thread


	/**
	 * Constructor initializes variables for thread runtime
	 * 
	 * @param s
	 */
	public Teacher(String s) {
		teacherName = s;
		question = new Vector<Question>();
		student = new Vector<Student>();
		startingOnlineSession = false;
		rand = new Random();
		thread = new Thread(this, s);
	}
	
	private static void msg(String m) { 
		System.out.println("{" + Project1.age()+ "} The " + teacherName + " " + m);
	}

	@Override
	public void run(){
		msg("is in the office.");
		waiting();
		msg("is answering emails.");
		waitForQ();
		int currentIndex = 0;
		while (currentIndex < question.size()){
			if(!question.get(currentIndex).getQAnswered()){
				msg("is sending an email "+ question.get(currentIndex).getStudentName() 
						+ " about Question: "+ question.get(currentIndex).getQID());
				
				question.get(currentIndex).setQAnswered(true);
			}
			++currentIndex;
		}
		try{
			Thread.sleep(14000);
		}catch(InterruptedException ie){
			System.out.println(ie);
		}
		chatWithStudents();
		msg("is finished chatting, student can leave.");
		Lab.signalToRelease();
		while (Project1.age() < Lab.getChatTime() && currentIndex != question.size()){
			if(currentIndex < question.size()){
				if(!question.get(currentIndex).getQAnswered()){
					msg("is sending an email " + question.get(currentIndex).getStudentName() 
							+ " about Question: " + question.get(currentIndex).getQID());
					question.get(currentIndex).setQAnswered(true);
				}
				++currentIndex;
			}
		}

		if(currentIndex == question.size()){
			msg("is finished answering type A questions.");

		}else
			msg("is not finished answering type A questions.");
	}
	
	public void start() {
		msg("going to the office.");
		thread.start();
	}
	
	public Vector<Student> getStudentQueue(){
		return student;
	}

	public static Vector<Question> getStudentQuestions(){
		return question;
	}
	
	public String getTeacherName() {
		return teacherName;
	}

	public long getTeacherArrivalTime() {
		return arrivalTime;
	}
	public static boolean chatSessionActive() {
		return startingOnlineSession;
	}
	
	private void chatWithStudents() {
		startingOnlineSession = true;
		boolean studentIsDone;
		msg("is ready to chat.");
		for (int i = 0; i < student.size(); i++){
			msg("is waiting for the next student.");
			studentIsDone = false;
			student.get(i).setToChat(true);
			while (!student.get(i).readyToChat()
					&& student.get(i).wantsToChat()){
				try{
					Thread.sleep(rand.nextInt(1000) + 1000);
				}catch (InterruptedException ie){
					System.out.println(ie);
				}
			}
			msg("is chatting with Student " + student.get(i).getName());
			
			while(!studentIsDone){
				if(!currentQuestion.getQAnswered()){
					msg("is answering Student " + currentQuestion.getStudentName() + " about question " + currentQuestion.getQID());
					currentQuestion.setQAnswered(true);
				}
				if(studentLastQuestion){
					studentIsDone = true;
				}
				try{
					Thread.sleep(rand.nextInt(4000));
				} catch (InterruptedException ie) {
					System.out.println(ie);
				}
			}
		}
	}

	public void waiting() {
		msg("is waiting.");
		try{
			Thread.sleep(2000);
		}catch (InterruptedException ie){
			System.out.println(ie);
		}
	}

	public void waitForQ(){
		try{
			Thread.sleep(rand.nextInt(6000) + 4000);
		}catch(InterruptedException ie){
			System.out.println(ie);
		}
	}

	public synchronized static void askedQ(Question q, boolean lastQuestion) {
		studentLastQuestion = lastQuestion;
		currentQuestion = q;
	}

	public int answerQTime() {
		return (int) (arrivalTime - System.currentTimeMillis());
	}

	public synchronized static void getQA(Question q){
		msg("is receiving student email " + q.getStudentName() + " with QID: " + q.getQID());
		question.add(q);
	}

	public synchronized static void waitingForSession(Student a){
		student.add(a);
	}
}