package methods;

import models.Customer;
import models.Hotel;
import models.Room;
import models.TypeRoom;

import java.io.*;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageHotel {
    private Scanner sc = new Scanner(System.in);
    private static Map map = new HashMap();
    private static final String NAME_FILE = "ListCustomer.csv";
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "Room, TypeRoom, Customer, PriceRoom, UserId";
//    private static final List<Hotel> list = new ArrayList<>();

    public void createRoom() {
        map = initDataCustomer(sc);
        Hotel hotelCustomer = null;

        String dobCustomer = (String) map.get("dobCustomer");
        String userId = (String) map.get("userId");
        String nameCustomer = (String) map.get("nameCustomer");
        TypeRoom typeRoom = (TypeRoom) map.get("typeRoom");
        String dateOfRent = (String) map.get("dateOfRent");

        boolean result = checkValidate(dobCustomer, userId);

        if (result) {
            // demo create data hotel
            Customer customer = new Customer(nameCustomer, dobCustomer, userId);
            Room room = null;

            switch (typeRoom.name()) {
                case "VIP":
                    room = new Room(TypeRoom.VIP.value, typeRoom, dateOfRent, customer);
                    break;
                case "ECONOMY":
                    room = new Room(TypeRoom.ECONOMY.value, typeRoom, dateOfRent, customer);
                    break;
            }
            hotelCustomer = new Hotel(room);
        } else {
            System.out.println("Bạn nhập sai định dạng DOB hoặc user id");
        }

        //ghi ra file csv
        writeFile(hotelCustomer, NAME_FILE);
    }

    public void removeCustomer() {
        System.out.println("Thônt tin danh sách khách hàng thuê phòng:");
        showCusInfo();
        System.out.print("Nhập thông tin phòng(Room Id): ");
        int id = Integer.valueOf(sc.nextLine());

        List<Hotel> list = readFileCsv();
        boolean result = list.contains(list.get(id-1));
        if(result){
            System.out.println(list.get(id-1));
            list.remove(list.get(id-1));
            //update lại file...
            if(list.size() == 0){
                Hotel empty = new Hotel();
                updateFile(empty,NAME_FILE);
            }
            for(Hotel e : list)
                updateFile(e,NAME_FILE);
        }

    }

    private void updateFile(Hotel  hotel,String fileName){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);

            // viết ra thông tin phòng trọ của khách hàng ra file
            fileWriter.append(hotel.getRoom().getIdRoom());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotel.getRoom().getTypeRoom().name());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotel.getRoom().getCustomer().getName());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotel.getRoom().getPriceRoom() + "");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotel.getRoom().getCustomer().getUserId());
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.flush();
//          "Room, TypeRoom, Customer, PriceRoom, UserId"


            System.out.println("Data of Customer created complete!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }

    private List<Hotel> readFileCsv() {
        List<Hotel> list = new ArrayList<>();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String line = "";
        try {
            fileReader = new FileReader(NAME_FILE);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                list.addAll(parseCsvFile(line));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void showCusInfo() {
        System.out.println(readFileCsv());
    }

    private List<Hotel> parseCsvFile(String line) {
        List<Hotel> list = new ArrayList<>();
        String[] splitData = line.split(COMMA_DELIMITER);
        String roomId = splitData[0];
        TypeRoom typeRoom = TypeRoom.valueOf(splitData[1]);
        String nameCustomer = splitData[2];
        long priceRoom = Long.valueOf(splitData[3]);
        String userId = splitData[4];

        Room room = new Room(roomId, priceRoom, typeRoom, new Customer(userId, nameCustomer));
        Hotel hotelCustomer = new Hotel(room);
        list.add(hotelCustomer);

        return list;
    }


    private void writeFile(Hotel hotelCustomer, String fileName) {
        createFile(fileName);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName, true);

            // viết ra thông tin phòng trọ của khách hàng ra file
            fileWriter.append(hotelCustomer.getRoom().getIdRoom());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotelCustomer.getRoom().getTypeRoom().name());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotelCustomer.getRoom().getCustomer().getName());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotelCustomer.getRoom().getPriceRoom() + "");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hotelCustomer.getRoom().getCustomer().getUserId());
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.flush();
//          "Room, TypeRoom, Customer, PriceRoom, UserId"


            System.out.println("Data of Customer created complete!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }

    }

    private void createFile(String fileName) {

        File file = new File(fileName);
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkValidate(String dob, String userId) {

        boolean resultDob = isValidateDOB(dob);
        boolean resultUserId = isValidateUserId(userId);

        if (resultDob && resultUserId) {
            return true;
        }
        return false;
    }

    private Map initDataCustomer(Scanner sc) {

        Map<Object, Object> map = new HashMap<>();

        System.out.println("Nhập thông tin khách hàng");
        System.out.println("Tên khách hàng");
        String nameCustomer = sc.nextLine();
        System.out.print("DOB(dd-MM-yyyy): ");
        String dobCustomer = sc.nextLine();
        System.out.println("CMND: ");
        String userId = sc.nextLine();
        System.out.print("Loại phòng: ");
        String typeRoom = sc.nextLine();

        TypeRoom valType = selectTypeRoom(typeRoom);

        System.out.print("Số ngày thuê trọ của khách:");
        String dateOfRent = sc.nextLine();

        // khởi tạo customer and room

        map.put("nameCustomer", nameCustomer);
        map.put("dobCustomer", dobCustomer);
        map.put("userId", userId);
        map.put("typeRoom", valType);
//        map.put("priceRoom", priceRoom);


        map.put("dateOfRent", dateOfRent);

        return map;
    }

    private TypeRoom selectTypeRoom(String typeRoom) {
        String val = typeRoom.toUpperCase();
        switch (val) {
            case "VIP":
                return TypeRoom.VIP;
        }
        return TypeRoom.ECONOMY;
    }

    private boolean isValidateDOB(String dob) {
        String patterDOB = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";

        Pattern pattern = Pattern.compile(patterDOB);
        Matcher matcher = pattern.matcher(dob);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    private boolean isValidateUserId(String userId) {
        String patternUserId = "\\d+";

        Pattern pattern = Pattern.compile(patternUserId);
        Matcher matcher = pattern.matcher(userId);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
// xử lý listCustomer