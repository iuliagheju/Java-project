package org.example;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.List;

public class Library implements Savable, Displayable {
    private ArrayList<Book> books;
    private List<Category> categories;
    private ArrayList<User> users; // Added users list for sorting
    private Double averageRating;

    public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public void loadBooks(String filePath) {
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));
            for (Object obj : jsonArray) {
                JSONObject bookObject = (JSONObject) obj;
                String title = (String) bookObject.get("title");
                String author = (String) bookObject.get("author");
                int review = (Integer) bookObject.get("review");
                int rating = ((Long) bookObject.get("rating")).intValue();

                books.add(new Book(title, author, averageRating) {

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void listBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void sortUsersByReadingListSize() {
        Collections.sort(users);
        for (User user : users) {
            System.out.println(user);
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null; // Return null if the category does not exist
    }

    public ArrayList<User> getUsers() {
        return users;
    }
    public void addBook(Book book) {
        books.add(book);
    }
    public void addCategory(Category category) {
        categories.add(category);

    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public void displayAllCategories(OutputDevice outputDevice) {
        for (Category category : categories) {
            category.displayBooks(outputDevice);
        }
    }

    @Override
    public void save() {
        System.out.println("Saving library data...");
    }

    @Override
    public void display() {

    }
}
