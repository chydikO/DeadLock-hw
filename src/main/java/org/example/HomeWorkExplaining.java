package org.itstep;

import java.util.Arrays;
import java.util.Random;

/*
3. Додадкове завдання: Напишіть клас, реалізуючий інтерфейс Runnable, метод run(),
який обчислює файл на жорсткому диску та виводить у вказаний потік будь-які дані.
Для виведення кожної "порції" даних має бути використано кілька операцій виведення.
Операції виведення повинні бути розділені викликами sleep(10).
Запишіть 10 класів цього класу в різних потоках так, щоб вони вивели екземпляри в один
і той самий потік виведення. Вивід інформації повинен бути синхронізований так,
щоб у результуючому вихідному потоці порції даних не "перемішувалися".
Створіть запис загальних даних із потоку в окремому файлі на жорсткому диску.
 */
public class HomeWorkExplaining {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start program");
        Reader reader = new Reader(100);
        Writer writer = new Writer();

        System.out.println(reader);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new CopyFileInThread(reader, writer));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("End of program");
    }
}

class CopyFileInThread implements Runnable {
    private final Reader reader;
    private final Writer writer;

    public CopyFileInThread(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        int count;
        byte[] bytes = new byte[10];
        while (true) {
            synchronized (reader) {
                count = reader.read(bytes);
                if (count <= 0) break;
                writer.write(Arrays.copyOf(bytes, count));
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Reader {
    private final byte[] data;
    public int index = 0;

    Reader() {
        this(1024);
    }

    Reader(int size) {
        data = new byte[size];
        init();
    }

    private void init() {
        new Random().nextBytes(data);
    }

    int read(byte[] bytes) {
        int count = Math.min(data.length - index, bytes.length);
        System.arraycopy(data, index, bytes, 0, count);
        if (index + count <= data.length) {
            index += count;
            return count;
        }
        return 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}

class Writer {

    void write(byte[] bytes) {
        System.err.println(Thread.currentThread().getName() + " " + Arrays.toString(bytes));
    }
}
