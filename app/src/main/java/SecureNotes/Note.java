package SecureNotes;

public class Note {

    private int n_id;
    private String notetitle;
    private String note;

    // Constructor
    public Note(int n_id, String notetitle, String note) {
        this.n_id = n_id;
        this.notetitle = notetitle;
        this.note = note;
    }

    // Getter and Setter for n_id
    public int getN_id() {
        return n_id;
    }

    public void setN_id(int n_id) {
        this.n_id = n_id;
    }

    // Getter and Setter for notetitle
    public String getNotetitle() {
        return notetitle;
    }

    public void setNotetitle(String notetitle) {
        this.notetitle = notetitle;
    }

    // Getter and Setter for note
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Note{" +
                "n_id=" + n_id +
                ", notetitle='" + notetitle + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}