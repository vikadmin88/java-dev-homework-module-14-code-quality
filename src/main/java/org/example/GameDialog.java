package org.example;

public enum GameDialog {
    MSG_START("\nEnter box number to select. Enjoy!\n"),
    MSG_START_BOX("""
            1 | 2 | 3
           -----------
            4 | 5 | 6
           -----------
            7 | 8 | 9
           """),
    MSG_WON("You won the game!\nCreated by Shreyas Saha. Thanks for playing!"),
    MSG_LOST("You lost the game!\nCreated by Shreyas Saha. Thanks for playing!"),
    MSG_DRAW("It's a draw!\nCreated by Shreyas Saha. Thanks for playing!"),
    MSG_ALREADY_USED("That one is already in use. Enter another."),
    MSG_INVALID_INPUT("Invalid input. Please enter again.");

    private final String message;

    GameDialog(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }
}
