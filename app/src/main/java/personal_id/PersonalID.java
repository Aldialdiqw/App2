package personal_id;

public class PersonalID {
    private int id;
    private String fullName;
    private String personalId;
    private String dateOfBirth;
    private String nationality;
    private String gender;
    private String dateOfExpiry;
    private String dateOfIssue;
    private String issuedBy;
    private String cardId;
    private String residence;

    // Constructor
    public PersonalID(int id, String fullName, String personalId, String dateOfBirth, String nationality, String gender,
                      String dateOfExpiry, String dateOfIssue, String issuedBy, String cardId, String residence) {
        this.id = id;
        this.fullName = fullName;
        this.personalId = personalId;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.gender = gender;
        this.dateOfExpiry = dateOfExpiry;
        this.dateOfIssue = dateOfIssue;
        this.issuedBy = issuedBy;
        this.cardId = cardId;
        this.residence = residence;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPersonalId() {
        return personalId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public String getCardId() {
        return cardId;
    }

    public String getResidence() {
        return residence;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public void setDateOfIssue(String dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }
}
