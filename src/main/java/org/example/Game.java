package org.example;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static org.example.GameDialog.*;


public final class Game {

    // 88
    private static final char CROSS = 'X';
    private static final int CROSS_SUM = CROSS * 3;
    // 79
    private static final char ZERO = 'O';
    private static final int ZERO_SUM = ZERO * 3;
    // 32
    private static final char BLANK = ' ';
    private static final int BLANK_SUM = BLANK * 3;
    private static final int CROSS_PREVENT_THRESHOLD = CROSS * 2 + BLANK;
    private static final int ZERO_BLANK2_SUM = ZERO + BLANK * 2;
    private static final int ZERO2_BLANK_SUM = ZERO * 2 + BLANK;
    private int stepCount;
    private final Scanner scan;
    private final Random random;
    private final char[] box;
    private static final int[][] BOX_MAP = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6},
            {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}
    };

    public Game() {
        this.stepCount = 0;
        this.scan = new Scanner(System.in);
        this.random  = new Random();
        this.box = new char[9];
        Arrays.fill(box, BLANK);
    }

    public void start() {
        output(MSG_START.get());
        output(MSG_START_BOX.get());
        mainProcess();
        scan.close();
    }

    private void mainProcess() {
        while (true) {
            doStepHuman();
            output(getBoxFormatted());
            if (isFinishReached()) {
                output(MSG_WON.get());
                return;
            }

            doStepMachine();
            output(getBoxFormatted());
            if (isFinishReached()) {
                output(MSG_LOST.get());
                return;
            }

            if (isStepsLimitReached()) {
                output(MSG_DRAW.get());
                return;
            }
        }
    }


    private void doStepHuman() {
        while (true) {
            int input = 0;
            if (scan.hasNextInt()) {
                input = scan.nextInt();
            } else {
                output(MSG_INVALID_INPUT.get());
                scan.next();
                continue;
            }
            --input;
            if (input >= 0 && input < 10) {
                if (isPositionBlank(input)) {
                    updateBox(input, CROSS);
                    return;
                } else {
                    output(MSG_ALREADY_USED.get());
                }
            } else {
                output(MSG_INVALID_INPUT.get());
            }
        }
    }

    private void doStepMachine() {
        if (isStepsLimitReached()) {
            return;
        }
        // set element to center
        if (isPositionBlank(4)) {
            updateBox(4, ZERO);
            return;
        }
        if (doStepSmart()) {
            return;
        }

        while (true) {
            int rand = (random.nextInt(0, 9));
            if (isPositionBlank(rand)) {
                updateBox(rand, ZERO);
                break;
            }
        }
    }

    private boolean doStepSmart() {

        boolean doPreventStep = false;
        int indexToPrevent = -1;
        int variantToStep = -1;
        int variant = 0;
        int mapIdx = 0;
        int lastMapBlankIdx = 0;

        for (int[] map : BOX_MAP) {
            int sum = box[map[0]] + box[map[1]] + box[map[2]];

            int lastBlankIdx = -1;
            for (int i = 0; i < 3; i++) {
                if (isPositionBlank(map[i])) {
                    lastMapBlankIdx = mapIdx;
                    lastBlankIdx = map[i];
                }
            }
            // 208 == X+X+_, prevent opponent to set 3rd symbol
            if (sum == CROSS_PREVENT_THRESHOLD) {
                doPreventStep = true;
                indexToPrevent = lastBlankIdx;
            }

            variant = getStepVariant(sum, variant);
            variantToStep = (variant == 0) ? lastMapBlankIdx : mapIdx;

            // do winner step
            if (variant == 3) {
                updateBox(getStepIndex(variantToStep), ZERO);
                return true;
            }
            mapIdx++;
        }

        // prevent winner step of opponent
        if (doPreventStep) {
            updateBox(indexToPrevent, ZERO);
            return true;
        }
        // typical step
        if (variant > 0) {
            updateBox(getStepIndex(variantToStep), ZERO);
            return true;
        }
        return false;
    }

    private int getStepVariant(int sum, int variant) {
        // Variants to do next step: _+_+_ 96, O+_+_ 143, O+O+_ 190
        if (sum == BLANK_SUM && variant == 0) {
            return  1;
        } else if (sum == ZERO_BLANK2_SUM && variant < 2) {
            return  2;
        } else if (sum == ZERO2_BLANK_SUM && variant < 3) {
            return  3;
        }
        return 0;
    }

    private int getStepIndex(int variantToStep) {
        for (int i = 0; i < 3; i++) {
            if (isPositionBlank(BOX_MAP[variantToStep][i])) {
                return BOX_MAP[variantToStep][i];
            }
        }
        return -1;
    }

    private boolean isPositionBlank(int index) {
        return box[index] == BLANK;
    }
    private boolean isFinishReached() {

        for (int[] map : BOX_MAP) {
            int sum = box[map[0]] + box[map[1]] + box[map[2]];
            if (sum == CROSS_SUM || sum == ZERO_SUM) {
                return true;
            }
        }
        return false;
    }

    private boolean isStepsLimitReached() {
        return stepCount >= 9;
    }

    private void updateBox(int index, char symbol) {
        box[index] = symbol;
        stepCount++;
    }

    private String getBoxFormatted() {
        return String.format("%n %s | %s | %s %n-----------%n %s | %s | %s %n-----------%n %s | %s | %s %n",
                box[0], box[1], box[2], box[3], box[4], box[5], box[6], box[7], box[8]);
    }
    private void output(String content) {
        System.out.println(content);
    }
}
