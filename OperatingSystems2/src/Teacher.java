import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Semaphore;
 
/**
 * Teacher Thread
 * 
 * @author - Rebecca Katz
 * 
 */
public class Teacher implements Runnable {
 
    private final long arrivalTime = System.currentTimeMillis();
    private static String teacherName;
    private Thread thread;
    private Random rand;
    private static Vector<Question> questions;
    private static Vector<Student> queue;
    private static boolean onlineSession;
    private static boolean lastQuestion;
    private static Question currentQuestion;
    private static Semaphore emailedQuestion;
    private static Semaphore chatMutex;
    private static Semaphore waitForNextStudent;
    private static Semaphore answerNextQuestion;
    private static Semaphore askedQ;
 
    /**
     * Constructor 
     * 
     * @param n
     */
    public Teacher(String name) {
        teacherName = name;
        questions = new Vector<Question>();
        queue = new Vector<Student>();
        onlineSession = false;
        rand = new Random();
        emailedQuestion = new Semaphore(1, true);
        chatMutex = new Semaphore(1, true);
        waitForNextStudent = new Semaphore(0, false);
        answerNextQuestion = new Semaphore(0, false);
        askedQ = new Semaphore(0, false);
        thread = new Thread(this, name);
    }
 
    @Override
    public void run() {
        msg(" arrived in the office.");
        waiting();
        msg(" is answering email questions.");
        waitForQuestions();
        int currentIndex = 0;
        while (currentIndex < questions.size()) {
            if (!questions.get(currentIndex).getQAnswered()) {
 
                msg(" sent a response email to Student " + questions.get(currentIndex).getStudentName()
                        + " about QID: " + questions.get(currentIndex).getQID());
 
                questions.get(currentIndex).setQAnswered(true);
            }
            ++currentIndex;
        }
 
        try{
            Thread.sleep(4000);
        } catch (InterruptedException ie) {
            System.out.println(ie);
        }
 
        chatWithStudents();
        msg(" finished the session, students can leave.");
        Lab.signalToRelease();
        
        while (Project2.age() < Lab.getClassTime()
                && currentIndex != questions.size()){
            if (currentIndex < questions.size()) {
                if (!questions.get(currentIndex).getQAnswered()){
                    msg(" sent an email response to Student " + questions.get(currentIndex).getStudentName() 
                    		+ " about QID: " + questions.get(currentIndex).getQID());
                    questions.get(currentIndex).setQAnswered(true);
                }
                ++currentIndex;
            }
        }
 
        if (currentIndex == questions.size()) {
            msg(" answered every type 'A' question.");
 
        } else
            msg(" didn't answer every type 'A' question.");
    }
    
    private void chatWithStudents(){
        onlineSession = true;
        boolean studentFinished;
        msg(" is ready to chat.");
 
        for (int i = 0; i < queue.size(); i++){
            msg(" is waiting for the next student.");
            waitForNextStudent = new Semaphore(0, false);
            studentFinished = false;
            queue.get(i).readyToChat();
 
            try {
                waitForNextStudent.acquire();
            } catch (InterruptedException ie){
                System.out.println(ie);
            }
 
            msg(" is chatting with Student " + queue.get(i).getName());
 
            while (!studentFinished){
                try {
                    askedQ.acquire();
                } catch (InterruptedException ie){
                    System.out.println(ie);
                }
 
                if (!currentQuestion.getQAnswered()){
                    msg(" answered Student " + currentQuestion.getStudentName() + 
                    		" for question ID " + currentQuestion.getQID());
                    answerNextQuestion = new Semaphore(0, false);
                    askedQ = new Semaphore(0, false);
                    if (lastQuestion){
                        studentFinished = true;
                    }
                    currentQuestion.setQAnswered(true);
                    queue.get(i).waitToLeave();
                } 
                try{
                    answerNextQuestion.acquire();
                } catch (InterruptedException ie){
                    System.out.println(ie);
                }
            }
        }
    }
    
    public Vector<Student> getStudentQueue() {
        return queue;
    }
 
    public static Vector<Question> getStudentQuestions() {
        return questions;
    }
 
    public String getProfName() {
        return teacherName;
    }
    
    public long getProfArrivalTime(){
        return arrivalTime;
    }
 
    public static boolean chatSessionActive(){
        return onlineSession;
    }
 
    public static void releaseTeacher(){
        waitForNextStudent.release();
    }

    public static void askedQuestion(Question q, boolean lastQ){
 
        lastQuestion = lastQ;
        currentQuestion = q;
        askedQ.release();
    }

    public static void answeredQuestion(){
        answerNextQuestion.release();
    }
 
    private static void msg(String m) {
        System.out.println("{" + Project2.age() + "} " + teacherName + m);
    }

    public void waiting(){
        msg(" is waiting.");
 
        try{
            Thread.sleep(2000);
        } catch (InterruptedException ie){
            System.out.println(ie);
        }
    }
 
    public void waitForQuestions(){
        try {
            Thread.sleep(rand.nextInt(5000) + 1000);
        } catch (InterruptedException ie) {
            System.out.println(ie);
        }
    }

    public int answerQuestionTime(){
        return (int) (arrivalTime - System.currentTimeMillis());
    }
 
    public static void getQA(Question question){
 
        try{
            emailedQuestion.acquire();
        } catch (InterruptedException ie){
            System.out.println(ie);
            ie.printStackTrace();
        }
        msg(" received an email from student " + question.getStudentName()
                + " with Question ID: " + question.getQID());
 
        questions.add(question);
        emailedQuestion.release();
    }

    public static void answeredQ() {
        answerNextQuestion.release();
    }
 
    
    public static void waitingForSession(Student a){
        try {
            chatMutex.acquire();
        } catch (InterruptedException ie){
            System.out.println(ie);
            ie.printStackTrace();
        }
 
        queue.add(a);
        chatMutex.release();
    }

    public void start() {
        msg(" is going to the office.");
        thread.start();
    }
}