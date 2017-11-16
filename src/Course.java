/**
 * Course
 */

public class Course {
    private String department;
    private int courseNum;
    private String lecture;
    private String tutorial;

    public Course(String line) {
        line = line.trim().replaceAll(" +", " ");
        String[] parts = line.split(" ");
        this.department = parts[0];
        this.courseNum = Integer.parseInt(parts[1]);
        if (parts[2].equals("LEC")) {
            this.lecture = parts[2] + " " + parts[3];
            if (parts.length == 6) {
                this.tutorial = parts[4] + " " + parts[5];
            }
        } else {
            this.tutorial = parts[2] + " " + parts[3];
        }
    }

    public String toString() {
        String name = department + " " + Integer.toString(courseNum);
        if (lecture != null) {
            name = name + " " + lecture;
        }
        if (tutorial != null) {
            name = name + " " + tutorial;
        }
        return name;
    }

    public boolean equals(Course c) {
        return ((this.department == null ? c.department == null : this.department.equals(c.department))
                && (this.lecture == null ? c.lecture == null : this.lecture.equals(c.lecture))
                && (this.tutorial == null ? c.tutorial == null : this.tutorial.equals(c.tutorial))
                && this.courseNum == c.courseNum);
    }

    //TODO Getters

}