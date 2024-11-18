package org.example;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;



public class Application implements Displayable {
    private List <Book> books;
    private InputDevice inputDevice;
    private OutputDevice outputDevice;
    private List<User> users;
    private Double rating;
    private static Library library;
    private static final String BOOKS_JSON_PATH = "C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\resources\\Books.json";

    public Application(InputDevice input, OutputDevice output) {
        this.inputDevice = input;
        this.outputDevice = output;
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.library = new Library();
    }

    @Override
    public void display() {
        outputDevice.writeMessage("Displaying application content.");
    }
    public void viewAllBooksInLibrary() {
        outputDevice.writeMessage("--- Library Books ---");
        for (Book book : library.getBooks()) {
            outputDevice.writeMessage(book.getTitle() + " by " + book.getAuthor());
        }
    }

    // Method to display users sorted by reading list size
    public void displayUsersSortedByReadingListSize() {
        library.sortUsersByReadingListSize();
        for (User user : library.getUsers()) {
            outputDevice.writeMessage(user.getUsername() + " - Reading list size: " + user.getReadingList().size());
        }
    }

    public void loadUsersFromJson(String filePath) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            JSONArray userArray = (JSONArray) parser.parse(reader);
            library.getUsers().clear(); // Ensure no duplicates
            for (Object obj : userArray) {
                JSONObject userJson = (JSONObject) obj;
                String username = (String) userJson.get("username");

                // Create and add user
                User user = new User(username) {
                    @Override
                    public int compareTo(User o) {
                        return 0;
                    }
                    @Override
                    public void save() {}
                };
                library.addUser(user);
            }
            outputDevice.writeMessage("Users loaded successfully.");
        } catch (IOException | ParseException e) {
            outputDevice.writeMessage("Error loading users: " + e.getMessage());
        }
    }

    public void saveUsersToJson(String filePath) {
        JSONArray userListJson = new JSONArray();
        for (User user : library.getUsers()) {
            JSONObject userJson = new JSONObject();
            userJson.put("username", user.getUsername());
            userListJson.add(userJson);
        }
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(userListJson.toJSONString());
            fileWriter.flush();
            outputDevice.writeMessage("Users saved successfully.");
        } catch (IOException e) {
            outputDevice.writeMessage("Error saving users: " + e.getMessage());
        }
    }

    public void loadBooksFromJson(String filePath) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            JSONArray bookArray = (JSONArray) parser.parse(reader);
            books.clear(); // Clear existing books to avoid duplicates
            for (Object obj : bookArray) {
                JSONObject bookJson = (JSONObject) obj;
                String title = (String) bookJson.get("title");
                String author = (String) bookJson.get("author");

                // Create the book object
                Book book = new Book(title, author, 0.0);

                // Load reviews and calculate ratings
                JSONArray reviewsArray = (JSONArray) bookJson.get("reviews");
                if (reviewsArray != null) {
                    for (Object reviewObj : reviewsArray) {
                        JSONObject reviewJson = (JSONObject) reviewObj;
                        String reviewText = (String) reviewJson.get("reviewText");
                        Long rating = (Long) reviewJson.get("rating");
                        Review review = new Review(reviewText, rating != null ? rating.intValue() : 0);
                        book.addReview(review); // Add review to book
                    }
                }

                // Calculate average rating
                book.updateAverageRating();

                books.add(book);
            }
            outputDevice.writeMessage("Books loaded successfully from JSON.");
        } catch (IOException | ParseException e) {
            outputDevice.writeMessage("Error loading books from JSON: " + e.getMessage());
        }
    }



    public List<Book> getBooks() {
        return books;
    }

    public void saveBooksToJson(String filePath) {
        JSONArray bookListJson = new JSONArray();

        for (Book book : books) {
            JSONObject bookJson = new JSONObject();
            bookJson.put("title", book.getTitle());
            bookJson.put("author", book.getAuthor());
            bookJson.put("averageRating", book.getAverageRating()); // Save calculated average rating

            JSONArray reviewsArray = new JSONArray();
            for (Review review : book.getReviews()) {
                JSONObject reviewJson = new JSONObject();
                reviewJson.put("reviewText", review.getReviewText());
                reviewJson.put("rating", review.getRating());
                reviewsArray.add(reviewJson);
            }
            bookJson.put("reviews", reviewsArray);

            bookListJson.add(bookJson);
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(bookListJson.toJSONString());
            fileWriter.flush();
            outputDevice.writeMessage("Books saved successfully to JSON.");
        } catch (IOException e) {
            outputDevice.writeMessage("Error saving books to JSON: " + e.getMessage());
        }
    }




    public Book searchBookByTitle(String query) {
        if (books == null || books.isEmpty()) {
            System.out.println("No books available for search.");
            return null;
        }
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(query)) {
                return book;
            }
        }
        return null;
    }


    public Book searchBookByAuthor(String query) {
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(query)) {
                return book;  // Return the first match
            }
        }
        System.out.println("No book found by author: " + query);
        return null;  // Return null if no match is found
    }


    public Book searchBookByTitleAndAuthor(String query, String authorQuery) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(query) && book.getAuthor().equalsIgnoreCase(authorQuery)) {
                return book;  // Return the first match
            }
        }
        System.out.println("No book found with title: " + query + " and author: " + authorQuery);
        return null;  // Return null if no match is found
    }

    public void addRatingToBook(Book book, int rating) {
        book.addRating(rating); // Adds rating to book
        saveBooksToJson(BOOKS_JSON_PATH); // Save changes immediately
        outputDevice.writeMessage("Rating added to book: " + book.getTitle());
    }

    private static void searchBookByCategory(Scanner scanner, OutputDevice outputDevice) {
        try {
            outputDevice.writeMessage("\n--- Search by Category ---");
            outputDevice.writeMessage("Available Categories:");
            for (Category category : library.getCategories()) {
                outputDevice.writeMessage("- " + category.getName());
            }

            outputDevice.writeMessage("\nEnter category name: ");
            String categoryName = scanner.nextLine();

            // Check if category exists
            Category category = library.getCategoryByName(categoryName);
            if (category == null) {
                throw new InvalidCategoryException("Category '" + categoryName + "' does not exist.");
            }

            // Display books in the category
            outputDevice.writeMessage("\nBooks in '" + categoryName + "' category:");
            for (Book book : category.getBooks()) {
                outputDevice.writeMessage("Book: " + book.getTitle() + " by " + book.getAuthor());
            }
        } catch (InvalidCategoryException e) {
            outputDevice.writeMessage("Error: " + e.getMessage());
        }
    }

    public void addBook(Book newBook) throws DuplicateBookException {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(newBook.getTitle()) &&
                    book.getAuthor().equalsIgnoreCase(newBook.getAuthor())) {
                throw new DuplicateBookException("A book with this title and author already exists.");
            }
        }
        books.add(newBook);
        outputDevice.writeMessage("Book added successfully.");
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            outputDevice.writeMessage("Enter book title:");
            String title = scanner.nextLine();
            outputDevice.writeMessage("Enter author:");
            String author = scanner.nextLine();

            Book book = new Book(title, author, 0.0);
            addBook(book);
        } catch (DuplicateBookException e) {
            outputDevice.writeMessage("Error: " + e.getMessage());
        }
    }
    }

