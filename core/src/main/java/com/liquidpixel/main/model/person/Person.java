package com.liquidpixel.main.model.person;

public class Person {
    private String lastName;
    private String firstName;
    private String gender;

    // Default constructor for Jackson
    public Person() {
    }

    public Person(Person person) {
        this.lastName = person.getLastName();
        this.firstName = person.getFirstName();
        this.gender = person.getGender();
    }

    public Person(String lastName, String firstName, String gender) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
