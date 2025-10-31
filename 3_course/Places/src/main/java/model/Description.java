package model;

public class Description {
    private String addressName;
    private String fullName;
    private String purposeName;

    public Description(String addressName, String fullName, String purposeName) {
        this.addressName = addressName;
        this.fullName = fullName;
        this.purposeName = purposeName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddressName() {
        return addressName;
    }
    public String getPurposeName() {
        return purposeName;
    }
}
