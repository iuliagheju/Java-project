package org.example;
import java.util.ArrayList;
import java.util.List;



public abstract class Category implements Displayable {
    private String name;
    private List<Book> books;
    private OutputDevice outputDevice;

    public Category(String name, OutputDevice outputDevice) {
        this.name = name;
        this.outputDevice = outputDevice;
        this.books = new ArrayList<>();
    }

    public Category(String categoryName) {
        this.name = categoryName;
        this.books = new ArrayList<>();

    }

    public void addBook(Book book) {
        books.add(book);
    }
    public String getName() {
        return name;
    }

    // Getter for books in this category
    public List<Book> getBooks() {
        return books;
    }

    // Method to display all books in this category
    public void displayBooks(OutputDevice outputDevice) {
        outputDevice.writeMessage("\n--- " + name + " Category ---");

        if (books.isEmpty()) {
            outputDevice.writeMessage("No books available in this category.");
        } else {
            for (Book book : books) {
                outputDevice.writeMessage("Book: " + book.getTitle() + " by " + book.getAuthor() +
                        " - Average Rating: " + book.getAverageRating());
            }
        }
    }
}

