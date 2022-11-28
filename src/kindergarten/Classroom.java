package kindergarten;
/**
 * This class represents a Classroom, with:
 * - an SNode instance variable for students in line,
 * - an SNode instance variable for musical chairs, pointing to the last student in the list,
 * - a boolean array for seating availability (eg. can a student sit in a given seat), and
 * - a Student array parallel to seatingAvailability to show students filed into seats 
 * --- (more formally, seatingAvailability[i][j] also refers to the same seat in studentsSitting[i][j])
 * 
 * @author Ethan Chou
 * @author Kal Pandit
 * @author Maksims Kurjanovics Kravcenko
 */
public class Classroom {
    private SNode studentsInLine;             // when students are in line: references the FIRST student in the LL
    private SNode musicalChairs;              // when students are in musical chairs: references the LAST student in the CLL
    private boolean[][] seatingAvailability;  // represents the classroom seats that are available to students
    private Student[][] studentsSitting;      // when students are sitting in the classroom: contains the students

    /**
     * Constructor for classrooms. Do not edit.
     * @param l passes in students in line
     * @param m passes in musical chairs
     * @param a passes in availability
     * @param s passes in students sitting
     */
    public Classroom ( SNode l, SNode m, boolean[][] a, Student[][] s ) {
		studentsInLine      = l;
        musicalChairs       = m;
		seatingAvailability = a;
        studentsSitting     = s;
	}
    /**
     * Default constructor starts an empty classroom. Do not edit.
     */
    public Classroom() {
        this(null, null, null, null);
    }

    /**
     * This method simulates students coming into the classroom and standing in line.
     * 
     * Reads students from input file and inserts these students in alphabetical 
     * order to studentsInLine singly linked list.
     * 
     * Input file has:
     * 1) one line containing an integer representing the number of students in the file, say x
     * 2) x lines containing one student per line. Each line has the following student 
     * information separated by spaces: FirstName LastName Height
     * 
     * @param filename the student information input file
     */
    public void makeClassroom ( String filename ) {
        StdIn.setFile(filename);
        int count = Integer.parseInt(StdIn.readLine()); // # of students
        String read = StdIn.readLine();
        String str[] = read.split(" ");
        studentsInLine = new SNode(new Student(str[0], str[1], Integer.parseInt(str[2])), null);

        SNode ptr = studentsInLine;
        SNode prev = null;
        SNode temp = null;
        Student current;


        while (StdIn.hasNextLine()) {
            read = StdIn.readLine();
            str = read.split(" ");
            current = new Student(str[0], str[1], Integer.parseInt(str[2]));
            
            while (ptr != null && ptr.getStudent().compareNameTo(current) < 0) {
                // while first student precedes second student
                // less than 0 --> means first student precedes second student
                prev = ptr;
                if (ptr.getNext() == null) {
                    prev = null;
                    break;
                }
                ptr = ptr.getNext();
            }
            if (prev == null && ptr.getStudent().compareNameTo(current) < 0) {
                ptr.setNext(new SNode(new Student(str[0], str[1], Integer.parseInt(str[2])), null));
            } else if (prev == null && ptr == studentsInLine) {
                temp = studentsInLine;
                studentsInLine = new SNode(new Student(str[0], str[1], Integer.parseInt(str[2])), temp);
                ptr = studentsInLine;
            } else { 
                temp = prev.getNext();
                prev.setNext(new SNode(new Student(str[0], str[1], Integer.parseInt(str[2])), temp));
                ptr = studentsInLine;
                prev = null;
            } 
        }
    }

    /**
     * 
     * This method creates and initializes the seatingAvailability (2D array) of 
     * available seats inside the classroom. Imagine that unavailable seats are broken and cannot be used.
     * 
     * Reads seating chart input file with the format:
     * An integer representing the number of rows in the classroom, say r
     * An integer representing the number of columns in the classroom, say c
     * Number of r lines, each containing c true or false values (true denotes an available seat)
     *  
     * This method also creates the studentsSitting array with the same number of
     * rows and columns as the seatingAvailability array
     * 
     * This method does not seat students on the seats.
     * 
     * @param seatingChart the seating chart input file
     */
    public void setupSeats(String seatingChart) {
        StdIn.setFile(seatingChart);
        int r = Integer.parseInt(StdIn.readLine());
        int c = Integer.parseInt(StdIn.readLine());
        System.out.println(r);
        System.out.println(c);

        seatingAvailability = new boolean[r][c];
        studentsSitting = new Student[r][c];
        int count = 0;

        while (count < r) {
            for (int j = 0; j < c && count < r; j++) {
                seatingAvailability[count][j] = StdIn.readBoolean();
            }
            count++;
        }

    }

    /**
     * 
     * This method simulates students taking their seats in the classroom.
     * 
     * 1. seats any remaining students from the musicalChairs starting from the front of the list
     * 2. starting from the front of the studentsInLine singly linked list
     * 3. removes one student at a time from the list and inserts them into studentsSitting according to
     *    seatingAvailability
     * 
     * studentsInLine will then be empty
     */
    public void seatStudents () {
        // removing from the front

        for (int i = 0; i < seatingAvailability.length; i++) {
            for (int j = 0; j < seatingAvailability[i].length; j++) {
                if ( seatingAvailability[i][j] && musicalChairs != null ) {
                    studentsSitting[i][j] = musicalChairs.getStudent();
                    musicalChairs = null;
                    continue;
                } 
                if ( seatingAvailability[i][j] && studentsSitting[i][j] == null ) {
                    if ( studentsInLine == null )
                        break;
                    else {
                        Student temp = studentsInLine.getStudent();
                        studentsInLine = studentsInLine.getNext();
                        studentsSitting[i][j] = temp;
                    }
                }
            }
        }

    }

    private void addToLast(int r, int c) {
        SNode oldChair = musicalChairs;

        if ( musicalChairs != null ) {
            musicalChairs = new SNode();
            musicalChairs.setStudent(studentsSitting[r][c]);
            musicalChairs.setNext(oldChair.getNext());
            oldChair.setNext(musicalChairs);
        } else {
            musicalChairs = new SNode();
            musicalChairs.setStudent(studentsSitting[r][c]);
            musicalChairs.setNext(musicalChairs);
        }
    }
    /**
     * Traverses studentsSitting row-wise (starting at row 0) removing a seated
     * student and adding that student to the end of the musicalChairs list.
     * 
     * row-wise: starts at index [0][0] traverses the entire first row and then moves
     * into second row.
     */
    public void insertMusicalChairs () {
        // adding to last

        for (int i = 0; i < studentsSitting.length; i++) {
            for (int j = 0; j < studentsSitting[i].length; j++) {
                if (studentsSitting[i][j] != null) {
                    addToLast(i, j);
                    studentsSitting[i][j] = null;
                }
            }
        }
     }

    private void delete (int n) {
        int count = 0;

        // find pointer corresponding to target
        SNode ptr = musicalChairs.getNext();
        SNode prev = null;

        for (; count != n; count++) {
            prev = ptr;
            ptr = ptr.getNext();
        }

        // create an SNode object of pointer to add to studentsInLine
        SNode newNode = new SNode();
        newNode.setStudent(ptr.getStudent());

        // delete ptr from musicalChairs
        if ( ptr == musicalChairs ) { // if deleting last node
            prev.setNext(ptr.getNext());
            musicalChairs = prev;
        } else if ( ptr == musicalChairs.getNext() ) { // if deleting first node
            musicalChairs.setNext(ptr.getNext());
        } else {
            prev.setNext(ptr.getNext());
        }


        // determine where newNode belongs in studentsInLine 
        SNode itr = studentsInLine;
        SNode pre = null;

        if ( studentsInLine == null ) {
            studentsInLine = newNode;
            // studentsInLine.setNext(null);
            return;
        }

        while ( itr != null && newNode.getStudent().getHeight() > itr.getStudent().getHeight() ) {
            pre = itr;
            itr = itr.getNext();
        }

        if ( itr == studentsInLine ) { // add to front
            SNode current = studentsInLine;
            studentsInLine = new SNode(newNode.getStudent(), current);
        } else if ( itr == null ) {
            pre.setNext(new SNode(newNode.getStudent(), null));
            // itr = new SNode(newNode.getStudent(), null);
        } else if ( itr.getStudent().getHeight() > newNode.getStudent().getHeight() ) {
            pre.setNext(new SNode(newNode.getStudent(), itr));
        } else if ( itr.getStudent().getHeight() == newNode.getStudent().getHeight() ) {
            pre.setNext(new SNode(newNode.getStudent(), itr));
        }

    }

     /**
     * 
     * This method repeatedly removes students from the musicalChairs until there is only one
     * student (the winner).
     * 
     * Choose a student to be elimnated from the musicalChairs using StdRandom.uniform(int b),
     * where b is the number of students in the musicalChairs. 0 is the first student in the 
     * list, b-1 is the last.
     * 
     * Removes eliminated student from the list and inserts students back in studentsInLine 
     * in ascending height order (shortest to tallest).
     * 
     * The last line of this method calls the seatStudents() method so that students can be seated.
     */

    public void playMusicalChairs() {
        int n = 0;  // n rep initial # of students in musicalChairs CLL, which will decrease
        int target = 0;

        SNode ptr = musicalChairs.getNext();
        do {
            n++;
            ptr = ptr.getNext();
        } while ( ptr != musicalChairs.getNext() ); 

        while ( n > 1 ) {
            target = StdRandom.uniform(n);
            System.out.println(target);
            delete (target);
            n--;
        }

        seatStudents();

    } 

    /**
     * Insert a student to wherever the students are at (ie. whatever activity is not empty)
     * Note: adds to the end of either linked list or the next available empty seat
     * @param firstName the first name
     * @param lastName the last name
     * @param height the height of the student
     */
    public void addLateStudent ( String firstName, String lastName, int height ) {
        
        Student lateStudent = new Student(firstName, lastName, height);

        if (studentsInLine != null) { // means students are in line, add late student to end
            SNode ptr = studentsInLine;
            SNode prev = null;

            while (ptr != null) {
                prev = ptr;
                ptr = ptr.getNext();
            }

            prev.setNext(new SNode(lateStudent, null));
            return;

        } else if ( musicalChairs != null ) { // means students are in musical chairs line
            SNode prev = musicalChairs;
            SNode ptr = musicalChairs.getNext();

            prev.setNext(new SNode(lateStudent, ptr));
            musicalChairs = prev.getNext();
            return;

        } else { // means students are sitting
            for (int i = 0; i < seatingAvailability.length; i++) {
                for (int j = 0; j < seatingAvailability[0].length; j++) {
                    if (seatingAvailability[i][j] && (studentsSitting[i][j] == null)) {
                        studentsSitting[i][j] = lateStudent;
                        return;
                    }
                }
            }
        }
    }

    /**
     * A student decides to leave early
     * This method deletes an early-leaving student from wherever the students 
     * are at (ie. whatever activity is not empty)
     * 
     * Assume the student's name is unique
     * 
     * @param firstName the student's first name
     * @param lastName the student's last name
     */
    public void deleteLeavingStudent ( String firstName, String lastName ) {

        if (studentsInLine != null) { // means students are in line, find leaving student
            SNode ptr = studentsInLine;
            SNode prev = null;
            boolean broke = false;

            while (ptr != null) {
                if (ptr.getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getStudent().getLastName().equalsIgnoreCase(lastName)) {
                    broke = true;
                    break;
                }
                prev = ptr;
                ptr = ptr.getNext();
            }

            if ( ptr == studentsInLine ) { // if first student is leaving
                studentsInLine = studentsInLine.getNext();
            } else if ( ptr == null && (prev.getStudent().getFirstName().equalsIgnoreCase(firstName) && prev.getStudent().getLastName().equalsIgnoreCase(lastName))) { // if last student is leaving
                prev = null;
            } else if (broke) {
                prev.setNext(ptr.getNext());
            }
            return;

        } else if ( musicalChairs != null ) { // means students are in musical chairs line, delete leaving student
            SNode ptr = musicalChairs.getNext();
            SNode prev = musicalChairs;

            do {
                if ( ptr.getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getStudent().getLastName().equalsIgnoreCase(lastName) ) {
                    break;
                }
                prev = ptr;
                ptr = ptr.getNext();
            } while ( ptr != musicalChairs.getNext() );

            if ( ptr == musicalChairs.getNext() && ptr.getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getStudent().getLastName().equalsIgnoreCase(lastName) ) { // delete first node
                musicalChairs.setNext(ptr.getNext());
            } else if ( ptr == musicalChairs && ptr.getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getStudent().getLastName().equalsIgnoreCase(lastName) ) {
                prev.setNext(ptr.getNext());
                musicalChairs = prev;
            } else if ( ptr.getStudent().getFirstName().equalsIgnoreCase(firstName) && ptr.getStudent().getLastName().equalsIgnoreCase(lastName) ) {
                prev.setNext(ptr.getNext());
            }
            return;
        } else { // means students are sitting, delete leaving student 
            for (int i = 0; i < seatingAvailability.length; i++) {
                for (int j = 0; j < seatingAvailability[0].length; j++) {
                    if (seatingAvailability[i][j] && (studentsSitting[i][j] != null)) {
                        if (studentsSitting[i][j].getFirstName().equalsIgnoreCase(firstName) && studentsSitting[i][j].getLastName().equalsIgnoreCase(lastName)) {
                            studentsSitting[i][j] = null;
                            return;
                        }
                    }
                }
            }
        }
        

    }

    /**
     * Used by driver to display students in line
     * DO NOT edit.
     */
    public void printStudentsInLine () {

        //Print studentsInLine
        StdOut.println ( "Students in Line:" );
        if ( studentsInLine == null ) { StdOut.println("EMPTY"); }

        for ( SNode ptr = studentsInLine; ptr != null; ptr = ptr.getNext() ) {
            StdOut.print ( ptr.getStudent().print() );
            if ( ptr.getNext() != null ) { StdOut.print ( " -> " ); }
        }
        StdOut.println();
        StdOut.println();
    }

    /**
     * Prints the seated students; can use this method to debug.
     * DO NOT edit.
     */
    public void printSeatedStudents () {

        StdOut.println("Sitting Students:");

        if ( studentsSitting != null ) {
        
            for ( int i = 0; i < studentsSitting.length; i++ ) {
                for ( int j = 0; j < studentsSitting[i].length; j++ ) {

                    String stringToPrint = "";
                    if ( studentsSitting[i][j] == null ) {

                        if (seatingAvailability[i][j] == false) {stringToPrint = "X";}
                        else { stringToPrint = "EMPTY"; }

                    } else { stringToPrint = studentsSitting[i][j].print();}

                    StdOut.print ( stringToPrint );
                    
                    for ( int o = 0; o < (10 - stringToPrint.length()); o++ ) {
                        StdOut.print (" ");
                    }
                }
                StdOut.println();
            }
        } else {
            StdOut.println("EMPTY");
        }
        StdOut.println();
    }

    /**
     * Prints the musical chairs; can use this method to debug.
     * DO NOT edit.
     */
    public void printMusicalChairs () {
        StdOut.println ( "Students in Musical Chairs:" );

        if ( musicalChairs == null ) {
            StdOut.println("EMPTY");
            StdOut.println();
            return;
        }
        SNode ptr;
        for ( ptr = musicalChairs.getNext(); ptr != musicalChairs; ptr = ptr.getNext() ) {
            StdOut.print(ptr.getStudent().print() + " -> ");
        }
        if ( ptr == musicalChairs) {
            StdOut.print(musicalChairs.getStudent().print() + " - POINTS TO FRONT");
        }
        StdOut.println();
    }

    /**
     * Prints the state of the classroom; can use this method to debug.
     * DO NOT edit.
     */
    public void printClassroom() {
        printStudentsInLine();
        printSeatedStudents();
        printMusicalChairs();
    }

    /**
     * Used to get and set objects.
     * DO NOT edit.
     */

    public SNode getStudentsInLine() { return studentsInLine; }
    public void setStudentsInLine(SNode l) { studentsInLine = l; }

    public SNode getMusicalChairs() { return musicalChairs; }
    public void setMusicalChairs(SNode m) { musicalChairs = m; }

    public boolean[][] getSeatingAvailability() { return seatingAvailability; }
    public void setSeatingAvailability(boolean[][] a) { seatingAvailability = a; }

    public Student[][] getStudentsSitting() { return studentsSitting; }
    public void setStudentsSitting(Student[][] s) { studentsSitting = s; }

}