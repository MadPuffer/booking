package com.company;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            ArrayList<Client> clientsList = readClientsFromFile("clients.txt");
            ArrayList<Room> bookedRooms = null;

            System.out.println("Добро пожаловать в отель!");
            System.out.print("Вы хотите забронировать новый номер или отменить бронь?\n" +
                    "   * Введите 'забронировать' или 'отменить': ");
            switch (chooseWhatToDo(scanner.nextLine())) {
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

                        chooseRoom(client);

                        clientsList.add(client);
                        writeClientsInFile(clientsList, "clients.txt");

                    } else {
                        System.out.println("Сожалеем, но вы не можете забронировать номер в силу своего возраста.");
                    }
                    break;
                case "отменить":
                    System.out.println(2);
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
    static void chooseRoom(Client client) throws IOException {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Room> availableRooms = findAvailableRooms("existingRooms.txt",
                "availableRooms.txt");
        ArrayList<Integer> availableRoomNums = new ArrayList<Integer>();
        ArrayList<Room> bookedRooms = readBookedRooms("bookedRooms.txt");

        System.out.println("Доступные комнаты: ");
        for (Room room : availableRooms) {
            availableRoomNums.add(room.getId() + 1);
            System.out.println(room.toString());
        }


        System.out.print("Введите номер желаемой комнаты: ");

        int roomNum = askForValidRoomNum(availableRoomNums);

        Room room = null;
        for (Room availableRoom : availableRooms) {
            if (availableRoom.getId() + 1 == roomNum) {
                room = availableRoom;
            }
        }

        LocalDate[] date = askForValidDate();

        LocalDate bookedDate = date[0];
        LocalDate exitRoomDate = date[1];
        Period p = Period.between(bookedDate, exitRoomDate);
        System.out.println(p.getDays());
//        System.out.println(exitRoomDate.getDayOfMonth() - bookedDate.getDayOfMonth());


        switch (room.getRoomType()) {
            case SINGLE:
                if (client.getBalance() >= room.getPrice()) {
                    System.out.println("Комната забронирована на ваше имя!");
                    client.bookRoom(room);
                    availableRooms.remove(room);
                    availableRoomNums.remove(roomNum);
                    bookedRooms.add(room);
                    writeBookedRooms("bookedRooms.txt", bookedRooms);
                    writeAvailableRooms("availableRooms.txt", availableRooms);

                }  else {
                    System.out.println("Сожалеем, но у вас недостаточно средств для бронирования данной комнаты." +
                            "\nХотите посмотреть другую комнату?");
                    System.out.println("  * Введите да или нет");
                    switch (chooseLookForAnotherRoom(scanner.nextLine())) {
                        case "да":
                            chooseRoom(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
                break;
            case DOUBLE:
                if (client.getBalance() >= room.getPrice()) {
                    System.out.println("Введите данные человека, с которым собираетесь проживать.");
                    registerPerson();
                    System.out.println("Комната забронирована на ваше имя!");

                    client.bookRoom(room);
                    availableRooms.remove(room);
                    availableRoomNums.remove(roomNum);
                    bookedRooms.add(room);
                    writeBookedRooms("bookedRooms.txt", bookedRooms);
                    writeAvailableRooms("availableRooms.txt", availableRooms);

                }  else {
                    System.out.println("Сожалеем, но у вас недостаточно средств для бронирования данной комнаты." +
                            "\nХотите посмотреть другую комнату?");
                    System.out.println("  * Введите да или нет");
                    switch (chooseLookForAnotherRoom(scanner.nextLine())) {
                        case "да":
                            chooseRoom(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
                break;
            case WITHBABY:
                if (client.getBalance() >= room.getPrice()) {
                    System.out.println("Введите данные человека, с которым собираетесь проживать.");
                    registerPerson();
                    System.out.println("Введите данные ребенка: ");
                    registerPerson();
                    System.out.println("Комната забронирована на ваше имя!");

                    client.bookRoom(room);
                    availableRooms.remove(room);
                    availableRoomNums.remove(roomNum);
                    bookedRooms.add(room);
                    writeBookedRooms("bookedRooms.txt", bookedRooms);
                    writeAvailableRooms("availableRooms.txt", availableRooms);

                }  else {
                    System.out.println("Сожалеем, но у вас недостаточно средств для бронирования данной комнаты." +
                            "\nХотите посмотреть другую комнату?");
                    System.out.println("  * Введите да или нет");
                    switch (chooseLookForAnotherRoom(scanner.nextLine())) {
                        case "да":
                            chooseRoom(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
                break;
            case GENERAL:
                if (client.getBalance() >= room.getPrice()) {
                    if (room.getLivingClientsCount() < 6) {
                        System.out.println("Место в комнате забронировано на ваше имя.");
                        room.setLivingClientsCount(room.getLivingClientsCount() + 1);
                        if (room.getLivingClientsCount() == 6) {
                            room.setBooked(true);
                            client.bookRoom(room);
                            availableRooms.remove(room);
                            availableRoomNums.remove(roomNum);
                            bookedRooms.add(room);
                            writeBookedRooms("bookedRooms.txt", bookedRooms);
                            writeAvailableRooms("availableRooms.txt", availableRooms);
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
                            chooseRoom(client);
                            break;
                        case "нет":
                            System.out.println("Удачного дня!");
                            break;
                    }
                }
        }
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
        return new LocalDate[] {LocalDate.of(2021, bookedMonth, bookedDay),
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
            fileNotFoundException.printStackTrace();
        } catch (EOFException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return bookedRooms;
    }

    static void writeAvailableRooms(String fileName, ArrayList<Room> availableRooms) {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(String.format("data/%s", fileName)));
            objectOutputStream.writeObject(availableRooms);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // поиск свободных комнат
    static ArrayList<Room> findAvailableRooms(String inputFileName, String outputFileName) throws IOException {
        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        ArrayList<Room> availableRooms = new ArrayList<Room>();

        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(String.format("data/%s", inputFileName)));

            ArrayList<Room> allRooms = (ArrayList<Room>) objectInputStream.readObject();
            objectInputStream.close();

            try {
                objectInputStream = new ObjectInputStream(new FileInputStream(String.format("data/%s", outputFileName)));
                availableRooms = (ArrayList<Room>) objectInputStream.readObject();
            } catch (EOFException e) {

            }


            for (Room room : allRooms) {
                if (room.isBooked()) {
                    availableRooms.remove(room);
                }
            }

            objectOutputStream = new ObjectOutputStream(new FileOutputStream(String.format("data/%s", outputFileName)));
            objectOutputStream.writeObject(availableRooms);

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
        return availableRooms;
    }

    // рекурсивная функция выбора первичного действия
    static String chooseWhatToDo(String input) {
        Scanner scanner = new Scanner(System.in);

        switch (input.toLowerCase()) {
            case "забронировать":
            case "отменить":
                return input.toLowerCase();
            default:
                System.out.print("Некорректный ответ, введите новый: ");
                return chooseWhatToDo(scanner.nextLine());

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

        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        }


        return arrayList;

    }
}
