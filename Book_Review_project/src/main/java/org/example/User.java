package org.example;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class User implements Savable, Comparable<User> {
    private String username;

    private ArrayList<Book> booksRead; // List of books the user has read
    private ArrayList<ArrayList<Review>> reviews; // List of reviews for each book
    private List<Book> readingList = new ArrayList<>();

    public User(String username) {
        this.username = username.trim();

        this.booksRead = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }


    public ArrayList<Book> getBooksRead() {
        return booksRead;
    }
    public List<Book> getReadingList() {
               return readingList;

    }
    public Collection<Review> getReviews() {
        ArrayList<Review> allReviews = new ArrayList<>();
        for (ArrayList<Review> bookReviews : reviews) {
            allReviews.addAll(bookReviews);
        }
        return allReviews;
    }



    public String getUsername() {
        return username;

    }
}