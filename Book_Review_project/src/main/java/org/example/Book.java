/*package org.example;
import java.util.ArrayList;
import java.util.List;

public abstract class Book implements Displayable, Comparable<Book> {
    private String title;
    private String author;
    private List<Review> reviews;
    private List<Integer> ratings;
    private int rating;
    private double averageRating;

    public Book(String title, String author, Double rating) {
        this.title = title;
        this.author = author;
        this.reviews = new ArrayList<Review>();
        this.ratings = new ArrayList<>();
        this.averageRating = averageRating;
    }

    // Add a rating to the list
    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) { // Assuming a rating range of 1 to 5
            ratings.add(rating);
        } else {
            System.out.println("Invalid rating. Please provide a rating between 1 and 5.");
        }
    }

    // Calculate the average rating
    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0; // No ratings, so return 0.0
        }

        int sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }

        return (double) sum / ratings.size();
    }

    // Optional: Get the number of ratings for the book
    public int getNumberOfRatings() {
        return ratings.size();
    }

    // Getter methods for title and author
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public int compareTo(Book other) {
        return Double.compare(this.getAverageRating(), other.getAverageRating());
    }

    public void setRating(int rating) {
        // Ensure the rating is valid (e.g., between 1 and 5)
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        } else {
            System.out.println("Invalid rating. Please provide a rating between 1 and 5.");
        }
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public double setAverageRating(double averageRating) {
        this.averageRating = averageRating;
        if (reviews.isEmpty()) {
            return 0.0; // Return 0 if there are no reviews
        }

        double totalRating = 0.0;
        for (Review review : reviews) {
            totalRating += review.getRating(); // Assuming `Review` has a `getRating()` method
        }

        return totalRating / reviews.size(); // Calculate the average rating
    }


    public void addReview(Review review) {
        // Add a new review to the book's review list
        if (reviews == null) { // Safeguard to ensure the list is not null
            reviews = new ArrayList<>();
        }
        reviews.add(review);
        saveBooksToJson("C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\resources\\Books.json");
    }

    private void saveBooksToJson(String s) {


    }
}*/
package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String title;
    private String author;
    private List<Review> reviews;
    private List<Integer> ratings;
    private double averageRating;

    public Book(String title, String author, Double averageRating) {
        this.title = title;
        this.author = author;
        this.reviews = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.averageRating = averageRating != null ? averageRating : 0.0;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public void addReview(Review review) {
        reviews.add(review);
        updateAverageRating();
    }

    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            ratings.add(rating);
            this.averageRating = getAverageRating(); // Update stored average rating
        } else {
            System.out.println("Invalid rating. Please provide a rating between 1 and 5.");
        }
    }
    private void updateAverageRating() {
        int total = ratings.stream().mapToInt(Integer::intValue).sum();
        averageRating = ratings.isEmpty() ? 0.0 : (double) total / ratings.size();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0; // No ratings, so return 0.0
        }

        int sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }

        return (double) sum / ratings.size();
    }

    public int getNumberOfRatings() {
        return ratings.size();
    }

    public JSONObject toJson() {
        JSONObject bookJson = new JSONObject();
        bookJson.put("title", title);
        bookJson.put("author", author);
        bookJson.put("averageRating", averageRating);

        JSONArray reviewsArray = new JSONArray();
        for (Review review : reviews) {
            JSONObject reviewJson = new JSONObject();
            reviewJson.put("reviewText", review.getReviewText());
            reviewJson.put("rating", review.getRating());
            reviewsArray.add(reviewJson);
        }
        bookJson.put("reviews", reviewsArray);

        return bookJson;
    }
}
