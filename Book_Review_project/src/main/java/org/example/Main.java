
package org.example;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static Application app;
    private static Library library;  // New instance of Library to manage categories and books
    private static User currentUser;
    private static OutputDevice outputDevice;

    public static void main(String[] args) {
        // Initialize InputDevice and OutputDevice for handling input/output
        InputDevice inputDevice = new InputDevice();
         outputDevice = new OutputDevice();
        app = new Application(inputDevice, outputDevice);
        library = new Library();  // Initialize the Library instance

        Properties config = loadConfigurations("C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\java\\org\\example\\config.properties");
        app.loadBooksFromJson(config.getProperty("books.filepath"));
        app.loadUsersFromJson(config.getProperty("users.filepath"));
        loadBooksIntoLibrary(); // Load books into Library

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            // Display main menu
            outputDevice.writeMessage("\n--- Main Menu ---");
            outputDevice.writeMessage("1. Log in");
            outputDevice.writeMessage("2. Search for a book");
            outputDevice.writeMessage("3. View all books by category");
            outputDevice.writeMessage("4. Display users sorted by reading list size");
            outputDevice.writeMessage("5. Exit");
            outputDevice.writeMessage("Choose an option: ");

            int choice = -1;

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline left after nextInt
            } else {
                outputDevice.writeMessage("Invalid choice. Please enter a valid number.");
                scanner.nextLine(); // clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    login(scanner, outputDevice);
                    break;
                case 2:
                    searchBook(scanner, outputDevice, false); // Non-logged-in user
                    break;
                case 3:
                    viewAllBooksByCategory(outputDevice); // New method to view books by category
                    break;
                case 4:
                    displayUsersSortedByReadingListSize(outputDevice); // New method to display users sorted by reading list size
                    break;
                case 5:
                    outputDevice.writeMessage("Exiting...");
                    app.saveUsersToJson(config.getProperty("users.filepath"));
                    exit = true;
                    break;
                default:
                    outputDevice.writeMessage("Invalid choice. Please choose a valid option.");
            }
        }

        scanner.close(); // Close scanner after handling the choice
    }

    private static void login(Scanner scanner, OutputDevice outputDevice) {
        outputDevice.writeMessage("Enter username: ");
        String username = scanner.nextLine();

        try {
            // Validate username
            if (username.trim().isEmpty()) {
                throw new InvalidUserException("Username cannot be empty.");
            }

            // Check if user already exists
            User user = library.getUserByUsername(username);
            if (user == null) {
                // Create a new user if it doesn't exist
                user = new User(username) {
                    @Override
                    public int compareTo(User o) {
                        return 0;
                    }

                    @Override
                    public void save() {}
                };
                library.addUser(user); // Add new user to library
            }

            currentUser = user; // Set the logged-in user
            outputDevice.writeMessage("Welcome, " + username + "!");
            userMenu(scanner, outputDevice); // Navigate to user-specific menu
        } catch (InvalidUserException e) {
            outputDevice.writeMessage("Error: " + e.getMessage());
        }
    }
    public static Properties loadConfigurations(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error loading configurations: " + e.getMessage());
        }
        return properties;
    }

    private static void loadBooksIntoLibrary() {
        for (Book book : app.getBooks()) {
            String categoryName;

            // Categorize books based on their titles or authors
            if (book.getAuthor().contains("J.K. Rowling") || book.getTitle().contains("Harry Potter")) {
                categoryName = "Fantasy";
            } else if (book.getAuthor().contains("Agatha Christie")) {
                categoryName = "Mystery";
            } else if (book.getAuthor().contains("George Orwell") || book.getAuthor().contains("F. Scott Fitzgerald") ||
                    book.getAuthor().contains("Oscar Wilde") || book.getAuthor().contains("Emily Bronte") ||
                    book.getAuthor().contains("Charlotte Bronte") || book.getAuthor().contains("Bram Stoker")) {
                categoryName = "Classic Literature";
            } else if (book.getAuthor().contains("Frank Herbert") || book.getAuthor().contains("Mary Shelley")) {
                categoryName = "Science Fiction";
            } else if (book.getAuthor().contains("Jane Austen") || book.getAuthor().contains("Margaret Atwood")) {
                categoryName = "Romance";
            } else {
                categoryName = "General"; // Default category
            }

            // Get or create the category
            Category category = library.getCategoryByName(categoryName);
            if (category == null) {
                category = new Category(categoryName) {
                    @Override
                    public void display() {
                        outputDevice.writeMessage("--- " + categoryName + " ---");
                        for (Book categoryBook : this.getBooks()) {
                            outputDevice.writeMessage("Book: " + categoryBook.getTitle() + " by " + categoryBook.getAuthor() +
                                    " - Average Rating: " + categoryBook.getAverageRating());
                        }
                    }
                };
                library.addCategory(category);
            }

            // Add the book to the category
            category.addBook(book);
        }

        outputDevice.writeMessage("Books loaded into categories successfully.");
    }


    private static void viewAllBooksByCategory(OutputDevice outputDevice) {
        outputDevice.writeMessage("\n--- Books by Category ---");

        for (Category category : library.getCategories()) {
            category.displayBooks(outputDevice);
        }
    }

    private static void displayUsersSortedByReadingListSize(OutputDevice outputDevice) {
        // Retrieve and sort users by reading list size
        List<User> users = library.getUsers();
        users.sort((u1, u2) -> Integer.compare(u2.getReadingList().size(), u1.getReadingList().size()));

        outputDevice.writeMessage("\n--- Users Sorted by Reading List Size ---");

        if (users.isEmpty()) {
            outputDevice.writeMessage("No users available.");
        } else {
            for (User user : users) {
                outputDevice.writeMessage(user.getUsername() + " - Reading List Size: " + user.getReadingList().size());
            }
        }
    }
    private static void addBookToReadingList(Scanner scanner, OutputDevice outputDevice) {
        outputDevice.writeMessage("\nEnter the title of the book to add to your reading list: ");
        String bookTitle = scanner.nextLine();

        Book book = app.searchBookByTitle(bookTitle); // Reuse the search functionality
        if (book != null) {
            currentUser.getReadingList().add(book); // Add the book to the user's reading list
            outputDevice.writeMessage("Book added to your reading list.");
        } else {
            outputDevice.writeMessage("Book not found.");
        }
    }
    private static void userMenu(Scanner scanner, OutputDevice outputDevice) {
        boolean logout = false;
        while (!logout) {
            outputDevice.writeMessage("\n--- User Menu ---");
            outputDevice.writeMessage("1. Search for a book");
            outputDevice.writeMessage("2. Add book to reading list");
            outputDevice.writeMessage("3. Log out");
            outputDevice.writeMessage("Choose an option: ");

            int choice = -1;

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline left after nextInt
            } else {
                outputDevice.writeMessage("Invalid choice. Please enter a valid number.");
                scanner.nextLine(); // clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    searchBook(scanner, outputDevice, true); // Logged-in user
                    break;
                case 2:
                    addBookToReadingList(scanner, outputDevice); // New method to add a book to the reading list
                    break;
                case 3:
                    outputDevice.writeMessage("Logging out...");
                    currentUser = null; // Clear the current user
                    logout = true;
                    break;
                default:
                    outputDevice.writeMessage("Invalid choice. Please choose a valid option.");
            }
        }
    }

    private static void searchBook(Scanner scanner, OutputDevice outputDevice, boolean isLoggedIn) {
        outputDevice.writeMessage("\n--- Search Book ---");
        outputDevice.writeMessage("1. Search by Title");
        outputDevice.writeMessage("2. Search by Author");
        outputDevice.writeMessage("3. Search by Title and Author");
        outputDevice.writeMessage("4. Search by Category");
        outputDevice.writeMessage("Choose an option: ");

        int choice = -1;

        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline left after nextInt
        } else {
            outputDevice.writeMessage("Invalid choice.");
            scanner.nextLine(); // clear invalid input
            return;
        }

        switch (choice) {
            case 1: // Search by Title
                outputDevice.writeMessage("Enter book title: ");
                String titleQuery = scanner.nextLine();
                Book bookByTitle = app.searchBookByTitle(titleQuery);
                displaySearchResult(outputDevice, bookByTitle, isLoggedIn, scanner);
                break;

            case 2: // Search by Author
                outputDevice.writeMessage("Enter book author: ");
                String authorQuery = scanner.nextLine();
                Book bookByAuthor = app.searchBookByAuthor(authorQuery);
                displaySearchResult(outputDevice, bookByAuthor, isLoggedIn, scanner);
                break;

            case 3: // Search by Title and Author
                outputDevice.writeMessage("Enter book title: ");
                String titleAndAuthorQuery = scanner.nextLine();
                outputDevice.writeMessage("Enter book author: ");
                String authorForTitleQuery = scanner.nextLine();
                Book bookByTitleAndAuthor = app.searchBookByTitleAndAuthor(titleAndAuthorQuery, authorForTitleQuery);
                displaySearchResult(outputDevice, bookByTitleAndAuthor, isLoggedIn, scanner);
                break;

            case 4: // Search by Category
                outputDevice.writeMessage("Available Categories: ");
                for (Category category : library.getCategories()) {
                    outputDevice.writeMessage("- " + category.getName());
                }

                outputDevice.writeMessage("Enter category name: ");
                String categoryQuery = scanner.nextLine();

                Category category = library.getCategoryByName(categoryQuery);
                if (category != null) {
                    outputDevice.writeMessage("\n--- Books in Category: " + category.getName() + " ---");
                    for (Book categoryBook : category.getBooks()) {
                        outputDevice.writeMessage("Book: " + categoryBook.getTitle() + " by " + categoryBook.getAuthor() +
                                " - Average Rating: " + categoryBook.getAverageRating());
                    }
                } else {
                    outputDevice.writeMessage("No such category found.");
                }
                break;

            default:
                outputDevice.writeMessage("Invalid choice.");
        }
    }
    private static void displaySearchResult(OutputDevice outputDevice, Book book, boolean isLoggedIn, Scanner scanner) {
        if (book != null) {
            outputDevice.writeMessage("Book found: " + book.getTitle() + " by " + book.getAuthor());
            displayBookOptions(scanner, outputDevice, book, isLoggedIn);
        } else {
            outputDevice.writeMessage("Book not found.");
        }
    }

    private static void displayBookOptions(Scanner scanner, OutputDevice outputDevice, Book book, boolean isLoggedIn) {
        boolean goBack = false;
        while (!goBack) {
            outputDevice.writeMessage("\n--- Book Options ---");
            outputDevice.writeMessage("1. View all reviews and average rating");
            if (isLoggedIn) {
                outputDevice.writeMessage("2. Add a review and a rating");
                outputDevice.writeMessage("3. Go back to previous menu");
            } else {
                outputDevice.writeMessage("2. Go back to previous menu");
            }
            outputDevice.writeMessage("Choose an option: ");

            int choice = -1;

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline left after nextInt
            } else {
                outputDevice.writeMessage("Invalid choice.");
                scanner.nextLine(); // clear invalid input
                continue;
            }

            if (choice == 1) {
                viewBookReviews(outputDevice, book);
            } else if (isLoggedIn) {
                switch (choice) {
                    case 2:
                        addReview(scanner, outputDevice, book);
                        break;
                    case 3:
                        goBack = true; // Set goBack to true to exit the loop
                        break;
                    default:
                        outputDevice.writeMessage("Invalid choice. Please try again.");
                }
            } else {
                if (choice == 2) {
                    goBack = true; // Set goBack to true to exit the loop
                } else {
                    outputDevice.writeMessage("Invalid choice. Please try again.");
                }
            }
        }
    }

    public static int getValidatedRating(Scanner scanner, OutputDevice outputDevice) {
        int rating;
        do {
            outputDevice.writeMessage("Enter rating (1-5): ");
            while (!scanner.hasNextInt()) {
                outputDevice.writeMessage("Invalid input. Please enter a number between 1 and 5.");
                scanner.next(); // discard invalid input
            }
            rating = scanner.nextInt();
            scanner.nextLine(); // consume newline
        } while (rating < 1 || rating > 5);
        return rating;
    }


    private static void addReview(Scanner scanner, OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("Enter your review: ");
        String reviewText = scanner.nextLine();

        outputDevice.writeMessage("Enter a rating for the review (1-5): ");
        int rating = getValidatedRating(scanner, outputDevice);
        scanner.nextLine(); // consume newline

        // Use the addReviewToBook method in Application to add and save the review
        Review review = new Review(reviewText, rating);
        book.addReview(review);  // Add review to the book
        outputDevice.writeMessage("Review added successfully.");

        // Save the updated book state to JSON
        app.saveBooksToJson("C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\resources\\Books.json");
    }

    private static void addRating(Scanner scanner, OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("Enter your rating for the book (1-5): ");
        int rating = getValidatedRating(scanner, outputDevice);
        scanner.nextLine(); // consume newline
        book.addRating(rating);
        outputDevice.writeMessage("Rating added successfully.");
        // Save the updated book state to JSON
        app.saveBooksToJson("C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\resources\\Books.json");
    }

    private static void viewBookReviews(OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("\n--- Book Reviews ---");
        outputDevice.writeMessage("Book: " + book.getTitle() + " by " + book.getAuthor());
        outputDevice.writeMessage("Average Rating: " + book.getAverageRating());

        if (book.getReviews().isEmpty()) {
            outputDevice.writeMessage("No reviews available.");
        } else {
            for (Review review : book.getReviews()) {
                outputDevice.writeMessage("- " + review.getReviewText() + " (Rating: " + review.getRating() + ")");
            }
        }
    }
}



