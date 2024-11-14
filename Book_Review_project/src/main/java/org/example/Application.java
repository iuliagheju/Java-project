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
    private static final String BOOKS_JSON_PATH = "C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\resources\\Books.json";

    public Application(InputDevice input, OutputDevice output) {
        this.inputDevice = input;
        this.outputDevice = output;
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    @Override
    public void display() {
        outputDevice.writeMessage("Displaying application content.");
    }

    public void loadBooksFromJson() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(BOOKS_JSON_PATH)) {
            JSONArray bookArray = (JSONArray) parser.parse(reader);
            books.clear(); // Clear existing books to avoid duplicates on reload
            for (Object obj : bookArray) {
                JSONObject bookJson = (JSONObject) obj;
                String title = (String) bookJson.get("title");
                String author = (String) bookJson.get("author");
                Double averageRating = (Double) bookJson.getOrDefault("averageRating", 0.0);

                Book book = new Book(title, author, averageRating);
                JSONArray reviewsArray = (JSONArray) bookJson.get("reviews");

                if (reviewsArray != null) {
                    for (Object reviewObj : reviewsArray) {
                        JSONObject reviewJson = (JSONObject) reviewObj;
                        String reviewText = (String) reviewJson.get("reviewText");
                        Long rating = (Long) reviewJson.get("rating");
                        Review review = new Review(reviewText, rating != null ? rating.intValue() : 0);
                        book.addReview(review);
                    }
                }
                books.add(book);
            }
            outputDevice.writeMessage("Books loaded successfully from JSON.");
        } catch (IOException | ParseException e) {
            outputDevice.writeMessage("Error loading books from JSON: " + e.getMessage());
        }
    }

  /*  public void saveBooksToJson() {
        JSONArray bookListJson = new JSONArray();
        for (Book book : books) {
            bookListJson.add(book.toJson());
        }
        try (FileWriter fileWriter = new FileWriter(BOOKS_JSON_PATH)) {
            fileWriter.write(bookListJson.toJSONString());
            fileWriter.flush();
            outputDevice.writeMessage("Books saved to JSON successfully.");
        } catch (IOException e) {
            outputDevice.writeMessage("Error saving books to JSON: " + e.getMessage());
        }
    }*/
  public void saveBooksToJson(String filePath) {
      JSONArray bookListJson = new JSONArray();

      for (Book book : books) {
          JSONObject bookJson = new JSONObject();
          bookJson.put("title", book.getTitle());
          bookJson.put("author", book.getAuthor());
          bookJson.put("averageRating", book.getAverageRating());

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
          System.out.println("Books saved successfully to " + filePath);
      } catch (IOException e) {
          System.err.println("Error while saving books to JSON: " + e.getMessage());
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

        public void addBook(Book newBook) throws DuplicateBookException {
            for (Book book : books) {
                if (book.getTitle().equalsIgnoreCase(newBook.getTitle()) &&
                        book.getAuthor().equalsIgnoreCase(newBook.getAuthor())) {
                    throw new DuplicateBookException("A book with this title and author already exists.");
                }
            }
            books.add(newBook);
            System.out.println("Book added successfully.");
        }


        public void run() {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter book title:");
                String title = scanner.nextLine();
                System.out.println("Enter author:");
                String author = scanner.nextLine();

                Book book = new Book(title, author, rating) {

                };
                addBook(book);
            } catch (DuplicateBookException e) {
                System.out.println(e.getMessage());
                System.out.println("Would you like to update the existing book? (yes/no)");
                Scanner scanner = new Scanner(System.in);
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("yes")) {
                    System.out.println("Updating existing book...");
                    // Logic to update the book can be added here
                } else {
                    System.out.println("Book addition canceled.");
                }
            }
        }
    }

