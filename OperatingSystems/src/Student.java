/**
 * Tracks student thread.
 * 
 * @author Rebecca Katz
 */

import java.util.Random;


public class Student implements Runnable{

	public static int num_Students = 15;	//number of students A -> O
	private String name;
	private Random random;

	//Number of A and B question
	private final int numQA = 4;
	private final int numQB = 2;
	private Thread thread;
	private Question[] qA;
	private Question[] qB;

	//Student and Teacher booleans
	private boolean inLab;
	private boolean canExit;
	private boolean wantsToChat;
	private boolean turnToChat;
	private boolean readyForChat;

	/**
	 * Constructor initializes variables for thread runtime
	 * 
	 * @param students name
	 */
	public Student(String n) {
		name = n;
		random = new Random();
		thread = new Thread(this, n);
		inLab = false;
		canExit = false;
		readyForChat = false;
		turnToChat = false;
	}

	private void msg(String a){ 
		System.out.println("{" + Project1.age() + "} Student " + name + " " + a); 
	}
	
	@Override
	public void run() {
		while(!Lab.LabIsOpen() && !Lab.isActive()){
			try{
				Thread.sleep(random.nextInt(4000));
			}catch (InterruptedException ie){
				System.out.println(ie);
			}
		}
		msg("is waiting for the lab to open.");
		Lab.enteredLab(this);
		
		if(this.inLab){
			idle();
			int questionID = 1;
			qA = new Question[random.nextInt(numQA + 1)];

			if (wantsToChat){
				qB = new Question[random.nextInt(numQB) + 1];
				Teacher.waitingForSession(this);
			}
			msg("has " + qA.length + " type A questions.");

			for (int i = 0; i < qA.length; i++) {
				qA[i] = new Question('A', questionID++, this);
				sendQ(qA[i]);
			}
			if(wantsToChat){
				msg("wants to chat.");

				while(!Teacher.chatSessionActive()){
					try{
						Thread.sleep(random.nextInt(1000));
					}catch (InterruptedException ie){
						System.out.println(ie);
					}
				}
				while(!turnToChat){
					try{
						Thread.sleep(random.nextInt(5000));
					}catch(InterruptedException ie){
						System.out.println(ie);
					}
				}
				readyForChat = true;
				chatWithTeacher();
				msg("is done chatting.");

			}else
				msg("has no more questions.");

			msg("is surfing the internet.");

			while(!canExit){
				try{
					Thread.sleep(random.nextInt(5000));
				}catch (InterruptedException ie){

				}
			}
		}else
			msg("is going home because the lab is full.");

		msg("went home.");
	}

	public void start(){
		wantsToChat = random.nextBoolean();
		msg("arrived at the lab.");
		thread.start();
	}

	public void okToLeave(){
		canExit = true;
	}

	public boolean readyToChat(){
		return readyForChat;
	}

	public boolean wantsToChat(){
		return wantsToChat;
	}

	private void chatWithTeacher(){
		msg("has " + qB.length + " type B questions.");

		for (int i = 0; i < qB.length; i++){
			qB[i] = new Question('B', i + 1, this);

			if (i != qB.length - 1){
				msg("has a new question");
				Teacher.askedQ(qB[i], false);
			}else{
				msg("asked last question.");
				Teacher.askedQ(qB[i], true);
			}

			while (!qB[i].getQAnswered()){
				try{
					Thread.sleep(random.nextInt(4000));
				}catch (InterruptedException ie) {
					System.out.println(ie);
				}
			}
		}
	}


	public void sendQ(Question question){
		msg("is sending a question.");
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

	public void idle(){
		msg("is thinking of a question.");
		Thread.yield();
	}
	
	public void isInLab(){
		inLab = true;
	}

	public void setToChat(boolean a){
		turnToChat = a;
	}
}