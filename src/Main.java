/**
 * Main class for the Project Gutenberg Book Analyzer.
 * It lets the user 1)list books, 2)analyze books, 3) view/save saved analyses.
 * 
 */

import java.io.*;
import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);

    // Storing  in a fixed array to track positions
    static BookAnalysis[] analyses = new BookAnalysis[100];

    // Keeps track of how many spots are actually being used
    static int analysisCount = 0;

    public static void main(String[] args) {

        // First try to load old saved data
        loadData();

        int choice = 0;

        // Keep showing menu until user chooses 4
        while (choice != 4) {

            System.out.println();
            System.out.println("==============================");
            System.out.println(" PROJECT GUTENBERG ANALYZER");
            System.out.println("==============================");

            System.out.println("1) List book files");
            System.out.println("2) Analyze a book");
            System.out.println("3) View saved analyses");
            System.out.println("4) Exit");

            System.out.print("Enter choice: ");

            // I use nextLine because if I use nextInt,
            // I can get problems with leftover newline
            String userInput = input.nextLine();

            try {
                choice = Integer.parseInt(userInput);
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }

            if (choice == 1) { listBooks(); }
            else if (choice == 2) {analyzeBook(); }
            else if (choice == 3) { viewAnalyses();}
            else if (choice == 4) {System.out.println("Goodbye!");}
            
            else {
                System.out.println("Please choose 1-4.");
            }
        }

        input.close();
    }


    // This gets the txt files from the books folder
    public static File[] getBooks() {

        File folder = new File("books");

        // First I check if the folder is even there
        if (!folder.exists()) {
            System.out.println("Books folder was not found.");
            return null;
        }

        if (!folder.isDirectory()) {
            System.out.println("Books is not a folder.");
            return null;
        }

        File[] allFiles = folder.listFiles();

        if (allFiles == null) {
            System.out.println("Could not read the books folder.");
            return null;
        }

        // First count how many txt files there are
        int count = 0;

        for (File file : allFiles) {

            if (file.isFile() &&
                    file.getName().toLowerCase().endsWith(".txt")) {

                count++;
            }
        }

        if (count == 0) {
            System.out.println("There are no txt books in the folder.");
            return null;
        }

        // Now make another array with only txt files
        File[] books = new File[count];

        int index = 0;

        for (File file : allFiles) {

            if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {

                books[index] = file;
                index++;
            }
        }

        return books;
    }


    // Option 1
    public static void listBooks() {

        File[] books = getBooks();

        if (books == null) {
            return;
        }

        System.out.println();
        System.out.println("Books found:");

        for (int i = 0; i < books.length; i++) {
            System.out.println((i + 1) + ". " + books[i].getName());
        }
    }


    // Option 2
    public static void analyzeBook() {

        // We only have room for 100 analyses, so cover this posibility
        if (analysisCount >= analyses.length) {
            System.out.println("No more room for analyses.");
            return;
        }

        File[] books = getBooks();

        if (books == null) {return;}

        System.out.println();
        System.out.println("Choose a book:");

        for (int i = 0; i < books.length; i++) {
            System.out.println((i + 1) + ". " + books[i].getName());
        }

        System.out.print("Enter file number: ");

        String userInput = input.nextLine();

        int bookNumber;

        try {
            bookNumber = Integer.parseInt(userInput);
        }
        catch (NumberFormatException e) {
            System.out.println("You need to enter a number.");
            return;
        }

        // User sees numbers starting from 1,but arrays start from 0

        if (bookNumber < 1 || bookNumber > books.length) {
            System.out.println("That book number does not exist.");
            return;
        }

        File selectedBook = books[bookNumber - 1];

        if (!selectedBook.canRead()) {
            System.out.println("This book cannot be read.");
            return;
        }

        int lines = 0;
        int words = 0;
        int characters = 0;

        System.out.println("Opening file...");
        System.out.println("Analyzing text...");

        try {

        Scanner bookScanner = new Scanner(selectedBook);

        while (bookScanner.hasNextLine()) {

            String line = bookScanner.nextLine();
            lines++;
            characters += line.length();

            Scanner lineScanner = new Scanner(line);

            while (lineScanner.hasNext()) {
                lineScanner.next();
                words++;
            }

            lineScanner.close();
        }

        bookScanner.close();
    }

        catch (FileNotFoundException e) {
            System.out.println("Could not open the book.");
            return;
        }

        // Get filename
        String title = selectedBook.getName();

        // Remove .txt from the title
        if (title.endsWith(".txt")) {
            title = title.substring(0, title.length() - 4);
        }

        // Now create the object
        BookAnalysis analysis = new BookAnalysis(title, lines, words, characters);

        // Put it into next empty spot
        analyses[analysisCount] = analysis;

        analysisCount++;

        System.out.println();
        System.out.println("Analysis complete!");
        System.out.println();
        System.out.println("-------------------------");
        System.out.println("Book Analysis");
        System.out.println("-------------------------");
        System.out.println(analysis);
        System.out.println("-------------------------");

        // Save after every analysis
        saveData();

        System.out.println("Analysis saved.");
        System.out.println("Total analyses: " + analysisCount);
    }


    // Option 3
    public static void viewAnalyses() {

        if (analysisCount == 0) {
            System.out.println("There are no saved analyses.");
            return;
        }

        System.out.println();
        System.out.println("Saved Book Analyses");
        System.out.println("-------------------------");

        for (int i = 0; i < analysisCount; i++) {

            System.out.println();
            System.out.println("[" + (i + 1) + "]");

            System.out.println(analyses[i]);
        }

        System.out.println();
        System.out.println("Press ENTER to return.");

        input.nextLine();
    }


    // Saves the array and count into books.dat
    public static void saveData() {

        try {

            FileOutputStream file = new FileOutputStream("books.dat");

            ObjectOutputStream output = new ObjectOutputStream(file);

            output.writeObject(analyses);
            output.writeInt(analysisCount);

            output.close();
        }
        catch (IOException e) {
            System.out.println("There was a problem saving the data.");
        }
    }


    // Loads old data when the program starts
    public static void loadData() {

        File dataFile = new File("books.dat");

        // First time running, this file will not exist
        if (!dataFile.exists()) {
            return;
        }

        try {

            FileInputStream file =new FileInputStream("books.dat");

            ObjectInputStream inputStream = new ObjectInputStream(file);

            analyses = (BookAnalysis[]) inputStream.readObject();

            analysisCount = inputStream.readInt();

            inputStream.close();

            System.out.println("Loaded " + analysisCount + " saved analyses.");
        }
        // If there is a problem reading the saved file
        catch (IOException e) {

            System.out.println("Could not load books.dat. Starting empty.");

            analyses = new BookAnalysis[100];
            analysisCount = 0;
        }
        // If Java cannot find the BookAnalysis class from  saved data
        catch (ClassNotFoundException e) {

            System.out.println("Saved BookAnalysis data could not be read.");

            analyses = new BookAnalysis[100];
            analysisCount = 0;
        }
    }
}