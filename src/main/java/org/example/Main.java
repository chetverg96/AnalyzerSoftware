package org.example;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static BlockingQueue<String> queueA = new LinkedBlockingQueue<>(100);
    private static BlockingQueue<String> queueB = new LinkedBlockingQueue<>(100);
    private static BlockingQueue<String> queueC = new LinkedBlockingQueue<>(100);

    public static void main(String[] args) {
        Thread textGenerationThread = new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < 1000; i++) {
                String text = generateText("abc", 100000);
                try {
                    if (text.contains("a")) queueA.put(text);
                    if (text.contains("b")) queueB.put(text);
                    if (text.contains("c")) queueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                queueA.put("end");
                queueB.put("end");
                queueC.put("end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread analysisThreadA = analysisThread('a', queueA);
        Thread analysisThreadB = analysisThread('b', queueB);
        Thread analysisThreadC = analysisThread('c', queueC);

        textGenerationThread.start();
        analysisThreadA.start();
        analysisThreadB.start();
        analysisThreadC.start();

        try {
            textGenerationThread.join();
            analysisThreadA.join();
            analysisThreadB.join();
            analysisThreadC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countOccurrences(String text, char character) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }

    public static Thread analysisThread(char symbol, BlockingQueue<String> queue) {
        return new Thread(() -> {
            int maxCount = 0;
            String text;
            try {
                while (!(text = queue.take()).equals("end")) {
                    int count = countOccurrences(text, symbol);
                    if (count > maxCount) {
                        maxCount = count;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Максимальное количество символов '" + symbol + "' в строке: " + maxCount);
        });
    }
}


