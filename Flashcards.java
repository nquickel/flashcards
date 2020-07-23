package flashcards;
import java.io.*;
import java.util.*;

public class Flashcards {
    private Map<String, String> cards;
    private Map<String, Integer> errors;
    private Scanner scanner;
    private List<String> log;
    private String exportOnExit;

    final private String IMPORT = "-import";
    final private String EXPORT = "-export";

    public Flashcards(String...args) {
        cards = new LinkedHashMap<>();
        errors = new LinkedHashMap<>();
        scanner = new Scanner(System.in);
        log = new ArrayList<>();

        for (int i = 0; i < args.length; i = i + 2) {
            if (args[i] == IMPORT) {
                importCards(args[i + 1]);
            } else if (args[i] == EXPORT) {
                exportOnExit = args[i + 1];
            }
        }

    }

    public String getTerm(String definition) {
        String term = "";
        for (String key : cards.keySet()) {
            if (cards.get(key).equals(definition)) {
                term = key;
            }
        }
        return term;
    }

    public void addCard() {
        String term;
        String definition;

        System.out.println("The card:");
        term = readAndLog();
        if (cards.containsKey(term)) {
            printAndLog("The card \"" + term + "\" already exists.");
            return;
        }
        System.out.println("The definition of the card:");
        definition = scanner.nextLine();
        if (cards.containsValue(definition)) {
            printAndLog("The definition \"" + definition + "\" already exists.");
            return;
        }
        cards.put(term, definition);
        errors.put(term, 0);
        printAndLog("The card (\"" + term + "\":\"" + definition + "\") has been added.");
    }

    public void removeCard() {
        String term;

        printAndLog("The card:");
        term = readAndLog();
        if (!cards.containsKey(term)) {
            printAndLog("Can't remove \"" + term + "\": There is no such card.");
        } else {
            cards.remove(term);
            errors.remove(term);
            printAndLog("The card has been removed.");
        }
    }

    public void importCards() {
        printAndLog("File name:");
        File file = new File(readAndLog());
        String[] card;

        int cardsLoaded = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                card = scanner.nextLine().strip().split(";");
                cards.put(card[0], card[1]);
                errors.put(card[0], Integer.parseInt(card[2]));
                cardsLoaded++;
            }
            printAndLog(cardsLoaded + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            printAndLog("File not found.");
        }
    }

    public void importCards(String fileName) {
        File file = new File(fileName);
        String[] card;

        int cardsLoaded = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                card = scanner.nextLine().strip().split(";");
                cards.put(card[0], card[1]);
                errors.put(card[0], Integer.parseInt(card[2]));
                cardsLoaded++;
            }
            printAndLog(cardsLoaded + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            printAndLog("File not found.");
        }
    }

    public void exportCards() {
        System.out.println("File name:");
        File file = new File(readAndLog());
        int cardsSaved = 0;

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String key : cards.keySet()) {
                writer.format("%s;%s;%d\n", key, cards.get(key), errors.get(key));
                cardsSaved++;
            }
            printAndLog(cardsSaved + " cards have been saved.");
        } catch (IOException e) {
            System.out.println("An exception occurs " + e.getMessage());
        }
    }

    public void exportCards(String fileName) {
        File file = new File(fileName);
        int cardsSaved = 0;

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String key : cards.keySet()) {
                writer.format("%s;%s;%d\n", key, cards.get(key), errors.get(key));
                cardsSaved++;
            }
            printAndLog(cardsSaved + " cards have been saved.");
        } catch (IOException e) {
            System.out.println("An exception occurs " + e.getMessage());
        }
    }

    public void askCards() {
        String term;
        String answer;

        printAndLog("How many times to ask?");
        int timesToAsk = Integer.parseInt(readAndLog());
        for (int i = 0; i < timesToAsk; i++) {
            // Select a random card to use
            Random random = new Random();
            Object[] keys = cards.keySet().toArray();
            term = (String) keys[random.nextInt(keys.length)];

            printAndLog("Print the definition of \"" + term + "\":");
            answer = readAndLog();
            if (answer.equalsIgnoreCase(cards.get(term))) {
                printAndLog("Correct answer.");
            } else if (cards.containsValue(answer)) {
                // The user answered with an answer that exists but belongs to a different card.
                printAndLog("Wrong answer. the correct one is \"" + cards.get(term) + "\", you've just written the definition of \"" + getTerm(answer) + "\".");
                errors.put(term, errors.get(term) + 1);
            } else {
                printAndLog("Wrong answer. The correct one is \"" + cards.get(term) + "\".");
                errors.put(term, errors.get(term) + 1);
            }
        }
    }

    public void saveLog() {
        printAndLog("File name:");
        File file = new File(readAndLog());

        try (PrintWriter writer = new PrintWriter(file)) {
            printAndLog("The log has been saved.");
            for (String s : log) {
                writer.println(s);
            }
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }

    public void hardestCard() {
        List<String> hardestCards = new ArrayList<>();
        int maxErrors = 0;

        for (String key : errors.keySet()) {
            if (errors.get(key) > maxErrors) {
                hardestCards.clear();
                hardestCards.add(key);
                maxErrors = errors.get(key);
            } else if (errors.get(key) == maxErrors && maxErrors != 0) {
                hardestCards.add(key);
            }
        }

        if (hardestCards.size() == 0) {
            printAndLog("There are no cards with errors.");
        } else if (hardestCards.size() == 1) {
            printAndLog("The hardest card is \"" + hardestCards.get(0) + "\". You have " + maxErrors + " errors answering it.");
        } else if (hardestCards.size() > 1) {
            printAndLog("The hardest cards are \"" + String.join("\", \"", hardestCards) + "\". You have " + maxErrors + " errors answering them.");
        }
    }

    public void resetStats() {
        for (String key : errors.keySet()) {
            errors.put(key, 0);
        }
        printAndLog("The log has been reset.");
    }

    public void exit() {
        printAndLog("Bye bye!");
        if (exportOnExit != null) {
            exportCards(exportOnExit);
        }
    }

    // prints a message to the console and adds it to the log
    public void printAndLog(String msg) {
        System.out.println(msg);
        log.add(msg);
    }

    // reads console input and adds it to the log
    public String readAndLog() {
        String input = scanner.nextLine();
        log.add("> " + input);
        return input;
    }
}