package flashcards;

public class Main {
    public static void main(String[] args) {
        Flashcards flashcards = new Flashcards(args);
        Boolean run = true;

        while (run) {
            flashcards.printAndLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");

            switch (flashcards.readAndLog()) {
                case "add":
                    flashcards.addCard();
                    break;
                case "remove":
                    flashcards.removeCard();
                    break;
                case "import":
                    flashcards.importCards();
                    break;
                case "export":
                    flashcards.exportCards();
                    break;
                case "ask":
                    flashcards.askCards();
                    break;
                case "log":
                    flashcards.saveLog();
                    break;
                case "hardest card":
                    flashcards.hardestCard();
                    break;
                case "reset stats":
                    flashcards.resetStats();
                    break;
                case "exit":
                    flashcards.exit();
                    run = false;
                    break;
                default:
                    System.out.println("Unrecognized command.");
            }
        }
    }

}

