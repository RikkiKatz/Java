/**
 * The main thread which creates the students, lab, and Teacher threads.
 * 
 * @author - Rebecca Katz
 * 
 */
public class Project2{
 
    private static long openingTime = System.currentTimeMillis();
 
    protected static final long age(){
        return System.currentTimeMillis() - openingTime;
    }
 
    public static void main(String[] args){
 
        Student[] students;
        Teacher teacher;
        Lab lab;
        int ch = 65;
        char letter;
        int value[] = new int[4];
 
        if (args.length < 4) {
            value[0] = 15;
            value[1] = 11;
            value[2] = 4;
            value[3] = 3;
        } else {
            for (int i = 0; i < 4; i++){
                if (i == 0){
                    try {
                        value[i] = Integer.parseInt(args[i]);
                    } catch (NumberFormatException e) {
                        System.out
                                .println("Default set to 15 for capacity");
                        value[i] = 15;
                    }
                } else if (i == 1) {
                    try {
                        value[i] = Integer.parseInt(args[i]);
                    } catch (NumberFormatException e) {
                        System.out
                                .println("Default set to 11 for Students");
                        value[i] = 11;
                    }
                } else if (i == 2) {
                    try {
                        value[i] = Integer.parseInt(args[i]);
                    } catch (NumberFormatException e) {
                        System.out
                                .println("Default set to 4 for question_A");
                        value[i] = 4;
                    }
                } else if (i == 3) {
                    try {
                        value[i] = Integer.parseInt(args[i]);
                    } catch (NumberFormatException e) {
                        System.out
                                .println("Last arg is not a number getting default 3 for question_B");
                        value[i] = 3;
                    }
                }
            }
        }
 
        students = new Student[value[1]];
 
        for (int i = 0; i < students.length; i++) {
            letter = (char) (ch + i);
            students[i] = new Student(Character.toString(letter), value[2], value[3]);
        }
 
        teacher = new Teacher("Teacher:");
        lab = new Lab("Lab", value[0]);
 
        for (int i = 0; i < students.length; i++) {
            students[i].start();
        }
 
        lab.start();
        teacher.start();
 
    }
 
}