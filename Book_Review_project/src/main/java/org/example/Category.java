package org.example;
import java.util.ArrayList;

public abstract class Category implements Displayable {
    private String name;
    private ArrayList<Book> books;

    public Category(String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }


}

