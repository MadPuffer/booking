package com.company;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        createHostel("existingRooms.txt");

        while (true) {
            LocalDate currentDate = nextDay();
            ArrayList<Client> clientsList = readClientsFromFile("clients.txt");
            ArrayList<Room> bookedRooms = readBookedRooms("actualBooking.txt");

            bookedRooms.removeIf(bookedRoom -> bookedRoom.getExitDate().equals(currentDate));
            writeBookedRooms("actualBooking.txt", bookedRooms);

            System.out.println("Добро пожаловать в отель!");
            System.out.print("Вы хотите забронировать новый номер или найти свою бронь?\n" +
                    "   * Введите 'забронировать' или 'найти': ");
            switch (chooseWhatToDo("забронировать", "найти")) {
                case "забронировать":
                    System.out.println("Отлично! Нам потребуется некоторые данные о вас.");

                    System.out.print("Введите вашу фамилию: ");
                    String surname = scanner.nextLine();

                    System.out.print("Введите ваше имя: ");
                    String name = scanner.nextLine();

                    System.out.print("Введите ваше отчество: ");
                    String lastname = scanner.nextLine();

                    System.out.print("Введите ваш возраст: ");
                    int age;
                    age = askForValidIntData();

                    if (age >= 16) {
                        System.out.println("Теперь нам потребуются ваши паспортные данные.");

                        System.out.print("Введите номер вашего паспорта: ");

                        int num = askForValidIntData();

                        System.out.print("Введите серию вашего паспорта: ");
                        int series = askForValidIntData();


                        System.out.println("И последний вопрос, какими средствами вы располагаете?");
                        System.out.print("Введите баланс вашей банковской карты: ");
                        int balance = askForValidIntData();

                        Client client = new Client(surname, name, lastname, age, new Passport(num, series, surname,
                                name, lastname), balance);

                        checkOut(client);

                        clientsList.add(client);
                        writeClientsInFile(clientsList, "clients.txt");

                    } else {
                        System.out.println("Сожалеем, но вы не можете забронировать номер в силу своего возраста.");
                    }
                    break;
                case "найти":
                    findBookedRoomsByClient(currentDate);
                    break;

                default:
                    break;
            }
        }

    }

    // создание хостела, вызывается при пересоздании отеля по желанию разраба
    static void createHostel(String filename) {
        ObjectOutputStream objectOutputStream = null;
        ArrayList<Room> rooms = new ArrayList<Room>();
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(String.format("data/%s", filename)));

            for (int i = 0; i < 5; i++) {
                rooms.add(new Room(i, RoomType.SINGLE));
            }

            for (int i = 5; i < 11; i++) {
                rooms.add(new Room(i, RoomType.DOUBLE));
            }

            for (int i = 11; i < 15; i++) {
                rooms.add(new Room(i, RoomType.WITHBABY));
            }

            for (int i = 15; i < 20; i++) {
                rooms.add(new Room(i, RoomType.GENERAL));
            }
            objectOutputStream.writeObject(rooms);

        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

    }

    // выбор комнаты клиентом
    static void checkOut(Client client) throws IOException {
        Scanner scanner = new Scanner(System.in);

//        ArrayList<Room> availableRooms = findAvailableRooms();
        ArrayList<Room> availableRooms = readBookedRooms("existingRooms.txt");
        ArrayList<Integer> availableRoomNums = new ArrayList<>();
        ArrayList<Room> bookedRooms = readBookedRooms("actualBooking.txt");
        System.out.println("Доступные комнаты: ");
        System.out.println(availableRooms.size());
        for (Room room : availableRooms) {
            boolean flag = true;
            for (Room bookedRoom : bookedRooms) {
                if (room.getId() == bookedRoom.getId()) {
                    flag = false;
                }
            }

            if (flag) {
                availableRoomNums.add(room.getId() + 1);
                System.out.println(room.toString());
            } else {
                continue;
            }
        }


        System.out.print("Введите номер желаемой комнаты: ");

        int roomNum = askForValidRoomNum(availableRoomNums);

        Room room = null;
        for (Room availableRoom : availableRooms) {
            if (availableRoom.getId() + 1 == roomNum) {
                room = availableRoom;
            }
        }

        room.setBooked(true);

        LocalDate[] date = askForValidDate();

        LocalDate bookedDate = date[0];
        LocalDate exitRoomDate = date[1];
        int days = Period.between(bookedDate, exitRoomDate).getDays();
        switch (room.getRoomType()) {
            case SINGLE:
                if (client.getBalance() >= room.getPrice() * days) {
                    System.out.println("Комната забронирована на ваше имя!");
                    formCheck(room.getPrice() * days, client);

                    room.setExitDate(exitRoomDate);
                    client.bookRoom(room, days);
                    availableRooms.remove(room);
                    availableRoomNums.remove(roomNum);
                    bookedRooms.add(room);

                    writeBookedRooms("actualBooking.txt", bookedRooms);

                } else {
                    System.out.println("Сожалеем, но у вас недостаточно средств для бронирования данной комнаты." +
                            "\nХотите посмотреть другую комнату?");
                    System.out.println("  * Введите да или нет");
                    switch (chooseLookForAnotherRoom(scanner.nextLine())) {
                        case "да":
                            checkOut(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
                break;
            case DOUBLE:
                if (client.getBalance() >= room.getPrice() * days) {
                    System.out.println("Введите данные человека, с которым собираетесь проживать.");
                    registerPerson();
                    System.out.println("Комната забронирована на ваше имя!");
                    formCheck(room.getPrice() * days, client);

                    client.bookRoom(room, days);
                    availableRooms.remove(room);
                    availableRoomNums.remove(roomNum);
                    bookedRooms.add(room);
                    writeBookedRooms("actualBooking.txt", bookedRooms);

                } else {
                    System.out.println("Сожалеем, но у вас недостаточно средств для бронирования данной комнаты." +
                            "\nХотите посмотреть другую комнату?");
                    System.out.println("  * Введите да или нет");
                    switch (chooseLookForAnotherRoom(scanner.nextLine())) {
                        case "да":
                            checkOut(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
                break;
            case WITHBABY:
                if (client.getBalance() >= room.getPrice() * days) {
                    System.out.println("Введите данные человека, с которым собираетесь проживать.");
                    registerPerson();
                    System.out.println("Введите данные ребенка: ");
                    registerPerson();
                    System.out.println("Комната забронирована на ваше имя!");
                    formCheck(room.getPrice() * days, client);

                    client.bookRoom(room, days);
                    availableRooms.remove(room);
                    availableRoomNums.remove(roomNum);
                    bookedRooms.add(room);
                    writeBookedRooms("actualBooking.txt", bookedRooms);

                } else {
                    System.out.println("Сожалеем, но у вас недостаточно средств для бронирования данной комнаты." +
                            "\nХотите посмотреть другую комнату?");
                    System.out.println("  * Введите да или нет");
                    switch (chooseLookForAnotherRoom(scanner.nextLine())) {
                        case "да":
                            checkOut(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
                break;
            case GENERAL:
                if (client.getBalance() >= room.getPrice() * days) {
                    if (room.getLivingClientsCount() < 6) {
                        System.out.println("Место в комнате забронировано на ваше имя.");
                        formCheck(room.getPrice() * days, client);
                        room.setLivingClientsCount(room.getLivingClientsCount() + 1);
                        if (room.getLivingClientsCount() == 6) {
                            room.setBooked(true);
                            client.bookRoom(room, days);
                            availableRooms.remove(room);
                            availableRoomNums.remove(roomNum);
                            bookedRooms.add(room);
                            writeBookedRooms("actualBooking.txt", bookedRooms);
                        }
                    } else {
                        System.out.println("Все места в этой комнате уже заняты");
                    }

                } else {
                    System.out.println("Сожалеем, но у вас недостаточно средств для бронирования данной комнаты." +
                            "\nХотите посмотреть другую комнату?");
                    System.out.println("  * Введите да или нет");
                    switch (chooseLookForAnotherRoom(scanner.nextLine())) {
                        case "да":
                            checkOut(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
        }
    }

    static LocalDate nextDay() throws IOException {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            objectInputStream = new ObjectInputStream(new FileInputStream("data/currentDateInfo.txt"));
            LocalDate currentDate = (LocalDate) objectInputStream.readObject();

            objectOutputStream = new ObjectOutputStream(new FileOutputStream("data/currentDateInfo.txt"));
            objectOutputStream.writeObject(currentDate.plusDays(1));

            return currentDate.plusDays(1);

        } catch (EOFException e) {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream("data/currentDateInfo.txt"));
            objectOutputStream.writeObject(LocalDate.of(2021, 1, 1));

        } catch (IOException | ClassNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        }

        return LocalDate.of(2021, 6, 1);
    }

    static void findBookedRoomsByClient(LocalDate currentDate) throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Введите вашу фамилию: ");
        String surname = sc.nextLine();
        System.out.print("Введите номер вашего паспорта: ");
        int num = askForValidIntData();
        for (Client client : readClientsFromFile("clients.txt")) {
            if (client.getSurname().equals(surname) && client.getPassport().getNumber() == num) {

                ArrayList<Room> bookedRoomsByClient = client.getBookedRooms();
                ArrayList<Room> bookedRooms = readBookedRooms("actualBooking.txt");
                ArrayList<Integer> bookedRoomsNums = new ArrayList<Integer>();

                for (Room bookedRoom : bookedRoomsByClient) {
                    System.out.printf("Номер комнаты: %d\n" +
                            "тип комнаты: %s\n", bookedRoom.getId() + 1, bookedRoom.getName());
                    bookedRoomsNums.add(bookedRoom.getId() + 1);
                }

                System.out.println("Вы хотите продлить бронь или отменить его?\n" +
                        "   * введите 'продлить' или 'отменить'");

                switch (chooseWhatToDo("продлить", "отменить")) {
                    case "отменить":
                        cancel(bookedRoomsByClient, bookedRooms, bookedRoomsNums, client, currentDate);
                        return;
                    case "продлить":
                        break;
                }


                writeBookedRooms("bookedRooms", bookedRooms);
                return;
            }
        }
        System.out.println("Брони на ваше имя не найдено!");
    }

    static void cancel(ArrayList<Room> bookedRoomsByClient, ArrayList<Room> bookedRooms,
                       ArrayList<Integer> bookedRoomsNums, Client client, LocalDate currentDate) throws IOException {
        System.out.print("Введите номер комнаты, бронь которой вы хотите отменить: ");
        int roomNum = askForValidRoomNum(bookedRoomsNums);
        for (Room room : bookedRoomsByClient) {

            if (room.getId() == roomNum - 1) {
                if (Period.between(room.getEnteringDate(), room.getExitDate()).getDays() <= 3) {
                    bookedRooms.remove(room);
                    writeLog(String.format("%s отменил бронь на комнату номер %d", client.getName(), room.getId() + 1));
                    client.setBookedRooms(bookedRoomsByClient);
                } else {
                    System.out.println("Бронь можно отменить только в течение трех дней.");
                }

            }
        }
        writeBookedRooms("actualBooking.txt", bookedRooms);
    }

    static LocalDate[] askForValidDate() {
        Scanner validDateScanner = new Scanner(System.in);
        System.out.print("Введите номер месяца брони: ");
        int bookedMonth = validDateScanner.nextInt();
        System.out.print("Введите номер дня брони: ");
        int bookedDay = validDateScanner.nextInt();
        System.out.print("Введите номер месяца выезда: ");
        int exitRoomMonth = validDateScanner.nextInt();
        System.out.print("Введите номер дня выезда: ");
        int exitRoomDay = validDateScanner.nextInt();
        return new LocalDate[]{LocalDate.of(2021, bookedMonth, bookedDay),
                LocalDate.of(2021, exitRoomMonth, exitRoomDay)};
    }

    static void registerPerson() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите фамилию: ");
        String surname = scanner.nextLine();

        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите отчество: ");
        String lastname = scanner.nextLine();

        System.out.print("Введите возраст: ");
        int age;
        age = askForValidIntData();

        if (age >= 16) {
            System.out.println("Теперь нам потребуются ваши паспортные данные.");

            System.out.print("Введите номер вашего паспорта: ");

            int num = askForValidIntData();

            System.out.print("Введите серию вашего паспорта: ");
            int series = askForValidIntData();
        }
    }

    static void writeBookedRooms(String filename, ArrayList<Room> arrayList) {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(String.format("data/%s", filename)));
            objectOutputStream.writeObject(arrayList);
        } catch (IOException fileNotFoundException) {
            System.out.println("Файл не найден");

        }
    }

    static ArrayList<Room> readBookedRooms(String filename) {
        ObjectInputStream objectInputStream = null;
        ArrayList<Room> bookedRooms = new ArrayList<Room>();
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(String.format("data/%s", filename)));
            bookedRooms = (ArrayList<Room>) objectInputStream.readObject();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Файл не найден");

        } catch (EOFException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return bookedRooms;
    }


    // поиск свободных комнат
    static ArrayList<Room> findAvailableRooms() {
        ArrayList<Room> allRooms = readBookedRooms("existingRooms.txt");
        ArrayList<Room> bookedRooms = readBookedRooms("actualBooking.txt");
        for (Room room : allRooms) {
        }
        return allRooms;
    }

    // рекурсивная функция выбора первичного действия
    static String chooseWhatToDo(String v1, String v2) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        if (input.toLowerCase().equals(v1) || input.toLowerCase().equals(v2)) {
            return input;
        } else {
            System.out.print("Некорректный ответ, введите новый: ");
            return chooseWhatToDo(v1, v2);
        }
    }

    // рекурсивная функция выбора дальнейшего просмотра комнат
    static String chooseLookForAnotherRoom(String input) {
        Scanner scanner = new Scanner(System.in);

        switch (input.toLowerCase()) {
            case "да":
            case "нет":
                return input.toLowerCase();
            default:
                System.out.print("Некорректный ответ, введите новый: ");
                return chooseLookForAnotherRoom(scanner.nextLine());

        }
    }

    // рекурсивная функция запроса valid int data
    static int askForValidIntData() {
        Scanner validScanner = new Scanner(System.in);
        int data;
        try {
            data = validScanner.nextInt();
            return data;
        } catch (Exception ex) {
            System.out.println("Некорректные данные.");
            System.out.print("Введите данные снова: ");
            return askForValidIntData();
        }
    }

    // рекурсивная функция запроса valid room num
    static int askForValidRoomNum(ArrayList<Integer> availableRoomNums) {
        int num = askForValidIntData();

        for (Integer availableRoomNum : availableRoomNums) {
            if (num == availableRoomNum) {
                return num;
            }
        }
        System.out.print("Неверный номер комнаты. Попробуйте ещё раз: ");
        return askForValidRoomNum(availableRoomNums);
    }

    static void writeClientsInFile(ArrayList<Client> clientArrayList, String filename) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(String.format("data/%s", filename)));
            objectOutputStream.writeObject(clientArrayList);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        }
    }

    static ArrayList<Client> readClientsFromFile(String filename) throws IOException, ClassNotFoundException {
        ArrayList<Client> arrayList = new ArrayList<>();
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(String.format("data/%s", filename)));
            arrayList = (ArrayList<Client>) objectInputStream.readObject();

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Файл не найден");
        } catch (EOFException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        }


        return arrayList;

    }

    static void writeLog(String message) throws IOException {

        PrintWriter pw = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("data/logs.txt"));
            String data = "";
            String temp;
            while ((temp = br.readLine()) != null) {
                data += temp + "\n";
            }
            pw = new PrintWriter("data/logs.txt");
            pw.println(String.format("%s[ %s ] %s", data, LocalDateTime.now().toString(), message));
        } catch (FileNotFoundException fnfe) {
            pw = new PrintWriter("data/logs.txt");
            pw.println(String.format("[ %s ] %s", LocalDateTime.now(), message));
            pw.flush();
        } finally {
            if (pw != null) {
                pw.close();
            }

            if (br != null) {
                br.close();
            }
        }
    }

    static void formCheck(int price, Client client) {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(String.format("Check for %s %s %s.txt", client.getSurname(),
                    client.getName(), client.getLastname()));
            printWriter.print(String.format("--Кассовый чек--\nИмя: %s\nФамилия: %s\nОтчество: %s\nОплачено: %d",
                    client.getName(), client.getSurname(), client.getLastname(), price));
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Файл не найден");
        }
    }


}