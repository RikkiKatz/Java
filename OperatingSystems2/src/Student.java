/**
 * Tracks student thread.
 * 
 * @author Rebecca Katz
 */

import java.util.Random;
import java.util.concurrent.Semaphore;


public class Student implements Runnable{

	public static int num_Students = 15;	//number of students A -> O
	private String name;
	private Random random;

	//Student and Teacher booleans
	private final int q_A;
	private final int q_B;
	private Thread thread;
	private Question[] qA;
	private Question[] qB;
	private boolean inLab;
	private boolean wantsToChat;
	private boolean turnToChat;
	private boolean readyForChat;
	private Semaphore canExit;
	private Semaphore canChat;
    private static Semaphore waitToBeServed;

	/**
	 * Constructor initializes variables for thread runtime
	 * 
	 * @param students name
	 */
	public Student(String n, int question_A, int question_B) {
		name = n;
		q_A = question_A;
		q_B = question_B;
		random = new Random();
		inLab = false;
		readyForChat = false;
		turnToChat = false;
		canExit = new Semaphore(0, false);
		canChat = new Semaphore(0, false);
		thread = new Thread(this, n);
	}

	private void msg(String a){ 
		System.out.println("{" + Project2.age() + "} Student: " + name + a); 
	}

	@Override
	public void run() {

		if (!Lab.isOpen() && !Lab.sessionActive()) {
			Lab.waitQueue();
		}
		msg(" is trying to get into the lab.");
		Lab.enteredLab(this);

		if (this.inLab) {
			idle();
			int questionID = 1;
			qA = new Question[random.nextInt(q_A + 1)];
			if (wantsToChat) {
				qB = new Question[random.nextInt(q_B) + 1];
				Teacher.waitingForSession(this);
			}
			msg(" has " + qA.length + " type 'A' questions.");

			for (int i = 0; i < qA.length; i++) {
				qA[i] = new Question('A', questionID++, this);
				sendQ(qA[i]);
			}
			if (wantsToChat) {
				msg(" wants to chat with the Teacher.");
				if (!turnToChat) {
					try {
						canChat.acquire();
					} catch (InterruptedException ie) {
						System.out.println(ie);
					}
				}
				readyForChat = true;
				chatWithTeacher();
				msg(" is finished chatting.");
			} else
				msg(" did not want to speak with the Teacher");
			msg(" is surfing the internet until it's time to leave.");
			waitToLeave();
		} else
			msg(" went home, couldn't get into the lab.");
		msg(" went home.");
	}

	public void start(){
		wantsToChat = random.nextBoolean();
		msg(" arrived at the lab.");
		thread.start();
	}

	public void isServed() {
		waitToBeServed.release();
	}

	public boolean readyToChat(){
		return readyForChat;
	}

	public boolean wantsToChat(){
		return wantsToChat;
	}

	 private void chatWithTeacher() {
	        Teacher.releaseTeacher();
	        msg(" has " + qB.length
	                + " type 'B' questions to ask the Teacher.");
	        for (int i = 0; i < qB.length; i++) {
	            waitToBeServed = new Semaphore(0, false);
	            qB[i] = new Question('B', i + 1, this);
	            if (i != qB.length - 1) {
	                msg(" is asking a new question");
	                Teacher.askedQuestion(qB[i], false);
	            } else {
	                msg(" asked last question.");
	                Teacher.askedQuestion(qB[i], true);
	            }
	 
	            if (!qB[i].getQAnswered()) {
	                try {
	                    waitToBeServed.acquire();
	                } catch (InterruptedException e) {
	                    System.out.println(e);
	                }
	            }
	            Teacher.answeredQ();
	        }
	    }

	public void sendQ(Question question){
		msg(" is sending a question.");
		thread.setPriority(Thread.MAX_PRIORITY);
		try{
			Thread.sleep(random.nextInt(4000));
		}catch (InterruptedException ie){
			System.out.println(ie);
		}
		Teacher.getQA(question);
		if (thread.getPriority() == Thread.MAX_PRIORITY){
			thread.setPriority(Thread.NORM_PRIORITY);
		}
	}


	public Thread getThread(){
		return thread;
	}

	public String getName() {
		return name;
	}

	public void setName(String n){
		name = n;
	}

	public int compareTo(Student a){
		if(name.compareTo(a.getName()) < 0){
			return -1;
		}
		else if(name.compareTo(a.getName()) == 0){
			return 0;
		}
		else return 1;
	}

	void waitToLeave() {
		try {
			canExit.acquire();
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	public void idle(){
		msg(" is thinking of a question.");
		Thread.yield();
	}

	public void isInLab(){
		inLab = true;
	}

	public void setToChat(boolean a){
		turnToChat = a;
	}
}