package com.example;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static Connection connect() {
        String url = "jdbc:sqlite:/Users/raffimedz/Desktop/Raffi/Skola/SQLite/SQLiteDemo.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void printMenu() {
        System.out.println("\nVälj:");
        System.out.println("0 - Stäng av");
        System.out.println("1 - Visa alla recept");
        System.out.println("2 - Lägg till nytt recept");
        System.out.println("3 - Uppdatera ett recept");
        System.out.println("4 - Ta bort ett recept");
        System.out.println("5 - Visa ingredienser för ett recept");
        System.out.println("6 - Gör ett recept till favorit");
        System.out.println("7 - Visa favoritrecept");
        System.out.println("8 - Visa statistik");
    }

    private static void showAllRecipes() {
        String sql = "SELECT * FROM recipes";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Alla recept:");
            while (rs.next()) {
                System.out.printf("ID: %d, Namn: %s, Kök: %s, Tillagningstid: %d min, Svårighet: %s, Portioner: %d, Favorit: %s\n",
                        rs.getInt("recipeId"), rs.getString("name"), rs.getString("cuisine"),
                        rs.getInt("prepTime"), rs.getString("difficulty"),
                        rs.getInt("serves"), rs.getBoolean("isFavorite") ? "Ja" : "Nej");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addRecipe() {
        System.out.println("Ange receptnamn:");
        String name = scanner.nextLine();
        System.out.println("Ange kök:");
        String cuisine = scanner.nextLine();
        System.out.println("Ange tillagningstid (min):");
        int prepTime = scanner.nextInt();
        System.out.println("Ange svårighetsgrad:");
        scanner.nextLine(); // consume newline
        String difficulty = scanner.nextLine();
        System.out.println("Ange antal portioner:");
        int serves = scanner.nextInt();

        String sql = "INSERT INTO recipes (name, cuisine, prepTime, difficulty, serves) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, cuisine);
            pstmt.setInt(3, prepTime);
            pstmt.setString(4, difficulty);
            pstmt.setInt(5, serves);
            pstmt.executeUpdate();
            System.out.println("Receptet har lagts till.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateRecipe() {
        System.out.println("Ange ID på receptet som ska uppdateras:");
        int recipeId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ange nytt namn:");
        String name = scanner.nextLine();
        System.out.println("Ange nytt kök:");
        String cuisine = scanner.nextLine();
        System.out.println("Ange ny tillagningstid:");
        int prepTime = scanner.nextInt();
        System.out.println("Ange ny svårighetsgrad:");
        scanner.nextLine();
        String difficulty = scanner.nextLine();
        System.out.println("Ange nya portioner:");
        int serves = scanner.nextInt();

        String sql = "UPDATE recipes SET name = ?, cuisine = ?, prepTime = ?, difficulty = ?, serves = ? WHERE recipeId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, cuisine);
            pstmt.setInt(3, prepTime);
            pstmt.setString(4, difficulty);
            pstmt.setInt(5, serves);
            pstmt.setInt(6, recipeId);
            pstmt.executeUpdate();
            System.out.println("Receptet har uppdaterats.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteRecipe() {
        System.out.println("Ange ID på receptet som ska tas bort:");
        int recipeId = scanner.nextInt();

        String sql = "DELETE FROM recipes WHERE recipeId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
            System.out.println("Receptet har tagits bort.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void markAsFavorite() {
        System.out.println("Ange ID på receptet som ska markeras som favorit:");
        int recipeId = scanner.nextInt();

        String sql = "UPDATE recipes SET isFavorite = 1 WHERE recipeId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
            System.out.println("Receptet har markerats som favorit.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showFavorites() {
        String sql = "SELECT * FROM recipes WHERE isFavorite = 1";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Favoritrecept:");
            while (rs.next()) {
                System.out.printf("ID: %d, Namn: %s\n", rs.getInt("recipeId"), rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showStatistics() {
        String sql = "SELECT COUNT(*) AS totalRecipes FROM recipes";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Totalt antal recept: " + rs.getInt("totalRecipes"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        boolean quit = false;
        printMenu();

        while (!quit) {
            System.out.println("\nVälj (0 för att avsluta):");
            int action = scanner.nextInt();
            scanner.nextLine();

            switch (action) {
                case 0 -> quit = true;
                case 1 -> showAllRecipes();
                case 2 -> addRecipe();
                case 3 -> updateRecipe();
                case 4 -> deleteRecipe();
                case 5 -> showFavorites();
                case 6 -> markAsFavorite();
                case 7 -> showStatistics();
                default -> printMenu();
            }
        }
    }
}

