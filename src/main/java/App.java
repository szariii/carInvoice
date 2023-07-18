import static spark.Spark.*;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.Random;
//import java.time.LocalDate;
import java.time.LocalDateTime;
//Data





public class App {
    static int getHerokuPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    private static ArrayList<FullCar> carlist = new ArrayList<>();
    private static Integer sid = 0;
    private static Gson gson = new Gson();

    public static void main(String[] args) {

        port(getHerokuPort());
        //String[] auta = {};

        staticFiles.location("/public");
        //get("/test", (req, res) -> testFunction(req, res));
        post("/add", (req, res) -> add(req, res));
        post("/getdata", (req, res) -> getdane(req, res));
        post("/delete", (req, res) -> delete(req, res));
        post("/update",(req,res)->update(req,res));
        post("/generate",(req, res)->generate(req,res));
        post("/invoice",(req,res)->invoice(req,res));
        get("/invoices", (req, res) -> invoices(req,res));
        post("/invoicev2",(req,res)->invoicev2(req,res));
    }



    static String invoicev2(Request req, Response res) throws DocumentException, FileNotFoundException {

        TypeInvoice invoicedata = gson.fromJson(req.body(), TypeInvoice.class);
        System.out.println(invoicedata);

        String action = invoicedata.getAction();
        String namek = "";


        if (action.equals("all")){
            System.out.println(System.currentTimeMillis());
            long times = System.currentTimeMillis();
            Invoice invoice = new Invoice(times, "Faktura za wszystkie auta", "Sprzedawca", "Nabywca", carlist);
            System.out.println(invoice.nrFaktury());

            ArrayList<Float>cenyZVat = new ArrayList<>();
            System.out.println("To tu");
            //System.out.println(carlist.get(0).getVat()/100);
            for (int i = 0 ;i<carlist.size();i++){
                float procent = (carlist.get(i).getVat());
                float cenaZVat = carlist.get(i).getCena() + (carlist.get(i).getCena() * (procent/100));
                cenyZVat.add(cenaZVat);
            }
            System.out.println(cenyZVat);

            Float sumaZaCalosc = 0.0f;

            for (int i = 0 ; i <cenyZVat.size();i++){
                sumaZaCalosc = sumaZaCalosc + cenyZVat.get(i);
            }

            System.out.println(sumaZaCalosc);
            String namedoZapisu = "invoice_all_cars_" + times;
            namek = new Invoices().fakturaAll(namedoZapisu, invoice.nrFaktury(), "Sprzedawca", "Nabywca","Faktura za wszystkie auta",carlist,cenyZVat,sumaZaCalosc);

        }else if(action.equals("year")){
            System.out.println(System.currentTimeMillis());
            long times = System.currentTimeMillis();
            Invoice invoice = new Invoice(times, "Faktura za wszystkie auta", "Sprzedawca", "Nabywca", carlist);
            System.out.println(invoice.nrFaktury());

            ArrayList<FullCar>listacar = new ArrayList<>();

            for (int i = 0; i<carlist.size();i++){
                if (carlist.get(i).getRok().equals(String.valueOf(invoicedata.getYear()))){
                    listacar.add(carlist.get(i));
                }
            }

            ArrayList<Float>cenyZVat = new ArrayList<>();
            System.out.println("To tu");
            //System.out.println(listacar.get(0).getVat()/100);
            for (int i = 0 ;i<listacar.size();i++){
                float procent = (listacar.get(i).getVat());
                float cenaZVat = listacar.get(i).getCena() + (listacar.get(i).getCena() * (procent/100));
                cenyZVat.add(cenaZVat);
            }
            System.out.println(cenyZVat);

            Float sumaZaCalosc = 0.0f;

            for (int i = 0 ; i <cenyZVat.size();i++){
                sumaZaCalosc = sumaZaCalosc + cenyZVat.get(i);
            }



            System.out.println(sumaZaCalosc);
            String namedoZapisu = "invoice_all_cars_by_year_" + times;
            namek = new Invoices().fakturaYear(namedoZapisu, invoice.nrFaktury(), "Sprzedawca", "Nabywca","Faktura za auta z roku: "+invoicedata.getYear(),listacar,cenyZVat,sumaZaCalosc);
        }else if (action.equals("prize")){
            System.out.println(System.currentTimeMillis());
            long times = System.currentTimeMillis();
            Invoice invoice = new Invoice(times, "Faktura za wszystkie auta", "Sprzedawca", "Nabywca", carlist);
            System.out.println(invoice.nrFaktury());

            ArrayList<FullCar>listacar = new ArrayList<>();

            for (int i = 0; i<carlist.size();i++){
                if (carlist.get(i).getCena()>=invoicedata.getOd()&&carlist.get(i).getCena()<=invoicedata.getDoo()){
                    listacar.add(carlist.get(i));
                }
            }

            ArrayList<Float>cenyZVat = new ArrayList<>();
            //System.out.println("To tu");
            //System.out.println(listacar.get(0).getVat()/100);
            for (int i = 0 ;i<listacar.size();i++){
                float procent = (listacar.get(i).getVat());
                float cenaZVat = listacar.get(i).getCena() + (listacar.get(i).getCena() * (procent/100));
                cenyZVat.add(cenaZVat);
            }
            System.out.println(cenyZVat);

            Float sumaZaCalosc = 0.0f;

            for (int i = 0 ; i <cenyZVat.size();i++){
                sumaZaCalosc = sumaZaCalosc + cenyZVat.get(i);
            }



            System.out.println(sumaZaCalosc);
            String namedoZapisu = "invoice_all_cars_by_price_" + times;
            namek = new Invoices().fakturaPrize(namedoZapisu, invoice.nrFaktury(), "Sprzedawca", "Nabywca","Faktura za auta z w cenach: "+invoicedata.getOd()+"-"+invoicedata.getDoo()+" PLN",listacar,cenyZVat,sumaZaCalosc);

        }

        ArrayList<String>odeslane = new ArrayList<>();
        odeslane.add(action);
        odeslane.add(namek);

        return gson.toJson(odeslane, ArrayList.class);
    }


    static String invoices(Request req, Response res) throws IOException {
        System.out.println("weszlo w invoices");
        System.out.println(req.queryParams("name"));
        String namefile = req.queryParams("name") + ".pdf";
        res.type("application/octet-stream");
        res.header("Content-Disposition", "attachment; filename="+namefile+""); // nagłówek

        OutputStream outputStream = res.raw().getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of("./katalogpdf/"+namefile+""))); // response pliku do przeglądarki
        return "bla";
    }

    static Integer invoice(Request req, Response res) throws IOException, DocumentException {
        System.out.println("weszlo do invoice");
        System.out.println(req.body());
        Integer pojid = gson.fromJson(req.body(), TakeId.class).getIdpoj();
        System.out.println(pojid);
        for (int i = 0; i < carlist.size(); i++) {
            if (carlist.get(i).getId().equals(pojid)){
                if (carlist.get(i).getFaktura()==false){
                    //System.out.println("Cos usunie");
                    HashMap<String, BaseColor> mapakolorow = new HashMap<>(){{
                            put("red",BaseColor.RED);
                            put("cyan",BaseColor.CYAN);
                            put("yellow",BaseColor.YELLOW);
                            put("orange", BaseColor.ORANGE);
                            put("gray",BaseColor.GRAY);
                            put("green",BaseColor.GREEN);
                            put("pink",BaseColor.PINK);
                    }};

                    HashMap<String, String> mapkazdjec = new HashMap<>(){{
                        put("BMW","./target/classes/public/img/BMW.jpg");
                        put("Audi","./target/classes/public/img/Audi.jpg");
                        put("Dacia","./target/classes/public/img/Dacia.jpg");
                        put("Fiat","./target/classes/public/img/Fiat.jpg");
                        put("Kia","./target/classes/public/img/Kia.jpg");
                        put("Citroen","./target/classes/public/img/Citroen.jpg");
                        put("Hyundai","./target/classes/public/img/Hyundai.jpg");
                    }};

                    carlist.get(i);
                        Document document = new Document(); // dokument pdf
                        System.out.println("Przed path");
                        String path = "./katalogpdf/" + carlist.get(i).getId()+".pdf"; // lokalizacja zapisu

                        PdfWriter.getInstance(document, new FileOutputStream(path));
                        System.out.println("Po path");

                        document.open();
                        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                        Chunk chunk = new Chunk("tekst", font); // akapit
                        Paragraph fakturauuid = new Paragraph("Faktura dla: "+carlist.get(i).getUuid(), font);
                        document.add(fakturauuid);

                    Paragraph fakturamodel = new Paragraph("model: "+carlist.get(i).getModel(), font);
                    document.add(fakturamodel);

                    BaseColor kolorekczcionki = mapakolorow.get(carlist.get(i).getKolor());
                    Paragraph fakturakolor = new Paragraph("kolor: "+carlist.get(i).getKolor(), FontFactory.getFont(FontFactory.COURIER, 16, kolorekczcionki));
                    document.add(fakturakolor);

                    Paragraph fakturarok= new Paragraph("rok: "+carlist.get(i).getRok(), font);
                    document.add(fakturarok);

                    Paragraph fakturakierowca= new Paragraph("poduszka: kierowca - "+carlist.get(i).getAirbags().get(0).isValue(), font);
                    document.add(fakturakierowca);

                    Paragraph fakturapasazer= new Paragraph("poduszka: pasazer - "+carlist.get(i).getAirbags().get(1).isValue(), font);
                    document.add(fakturapasazer);

                    Paragraph fakturapakanapa= new Paragraph("poduszka: pasazer - "+carlist.get(i).getAirbags().get(2).isValue(), font);
                    document.add(fakturapakanapa);

                    Paragraph fakturaboczna= new Paragraph("poduszka: boczna - "+carlist.get(i).getAirbags().get(3).isValue(), font);
                    document.add(fakturaboczna);

                    Image img = Image.getInstance(mapkazdjec.get(carlist.get(i).getModel()));
                    document.add(img);

                        document.close();
                        carlist.get(i).setFaktura(true);
                }

            }
        }

        return 1;
    }

    static Integer generate(Request req, Response res){
        Random rand = new Random();
        ArrayList<String>modele = new ArrayList<>(
                Arrays.asList(
                        "Audi",
                        "BMW",
                        "Dacia",
                        "Fiat",
                        "Kia",
                        "Citroen",
                        "Hyundai")
        );

        ArrayList<String>roki = new ArrayList<>(
                Arrays.asList(
                        "2002",
                        "2003",
                        "2004",
                        "2005",
                        "2006",
                        "2007",
                        "2008")
        );

        ArrayList<String>kolorki = new ArrayList<>(
                Arrays.asList(
                        "red",
                        "cyan",
                        "yellow",
                        "orange",
                        "gray",
                        "green",
                        "pink"
                )
        );



        for (int i = 0; i <10; i++){
            Boolean val1 = true;
            Integer los = rand.nextInt(2);
            if (los==1){
                 val1 = true;
            }else {
                 val1 = false;
            }


            Airbag airbag1 = new Airbag("kierowca", val1);
            Boolean val2 = true;
            los = rand.nextInt(2);
            if (los==1){
                val2 = true;
            }else {
                val2 = false;
            }

            Airbag airbag2 = new Airbag("pasazer", val2);

            Boolean val3 = true;
            los = rand.nextInt(2);
            if (los==1){
                val3 = true;
            }else {
                val3 = false;
            }

            Airbag airbag3 = new Airbag("tylna", val3);

            Boolean val4 = true;
            los = rand.nextInt(2);
            if (los==1){
                val4 = true;
            }else {
                val4 = false;
            }

            Airbag airbag4 = new Airbag("boczna", val4);

            ArrayList<Airbag> arr = new ArrayList<>(){
                {
                    add(airbag1);
                    add(airbag2);
                    add(airbag3);
                    add(airbag4);

                }
            };

            los = rand.nextInt(7);
            String color = kolorki.get(los);

            los = rand.nextInt(7);
            String model = modele.get(los);

            los = rand.nextInt(7);
            String rok = roki.get(los);

            los = rand.nextInt(2022 - 2000 +1)+2000;
            Integer los1 = rand.nextInt(12-1+1)+1;
            Integer los2 = rand.nextInt(31-1+1)+1;
            CustomDate dataSprzedazy = new CustomDate(los,los1,los2);

            los = rand.nextInt(30000-10000+1)+10000;
            Integer cena = los;

            Map<Integer, Integer> vaty = new HashMap<>(){
                {
                    put(0, 0);
                    put(1, 7);
                    put(2, 22);
                }
            };

            los = rand.nextInt(3);
            Integer vat = vaty.get(los);

            sid=sid+1;
            FullCar fullCar = new FullCar(model, rok, color, arr,dataSprzedazy, cena,vat);
            fullCar.setId(sid);
            fullCar.setFaktura(Boolean.FALSE);
            fullCar.setUuid(Generators.randomBasedGenerator().generate());

            //System.out.println(fullCar);
            ArrayList<Airbag> modelek = fullCar.getAirbags();
            //System.out.println(modelek);

            carlist.add(fullCar);
            res.type("application/json");
            //return (gson.toJson(fullCar));
        }
        return 1;

    }



    static Integer update(Request req, Response res){
        //System.out.println("Doszlo do update na serwie");
        //System.out.println(req.body());
        TakeUpdate daneupdate = gson.fromJson(req.body(), TakeUpdate.class);
        System.out.println(daneupdate);
        for (int i = 0; i < carlist.size(); i++) {
            System.out.println(carlist.get(i));
            System.out.println("tooo");
            if (carlist.get(i).getId().equals(daneupdate.getIdpoj())){
                carlist.get(i).setModel(daneupdate.getModel());
                carlist.get(i).setRok(daneupdate.getRok());
            }
        }

        Integer jeden = 1;

        return jeden;
    }

    static Integer delete(Request req, Response res) {
        Integer pojid = gson.fromJson(req.body(), TakeId.class).getIdpoj();
        //System.out.println(pojid);
        for (int i = 0; i < carlist.size(); i++) {
            System.out.println(carlist.get(i));
            System.out.println("tooo");
            if (carlist.get(i).getId().equals(pojid)){
                System.out.println("Cos usunie");
                carlist.remove(i);
            }
        }
        return pojid;
    }

    static String getdane(Request req, Response res) {
        System.out.println(carlist);


        return gson.toJson(carlist, ArrayList.class);
    }

    static String add(Request req, Response res) {
        //System.out.println(req.body());
        //System.out.println(req.body());
        sid=sid+1;

        FullCar fullCar = (gson.fromJson(req.body(), FullCar.class));
        fullCar.setId(sid);
        fullCar.setFaktura(Boolean.FALSE);
        fullCar.setUuid(Generators.randomBasedGenerator().generate());

        System.out.println(fullCar);
        ArrayList<Airbag> model = fullCar.getAirbags();
        //System.out.println(model);

        carlist.add(fullCar);
        res.type("application/json");
        return (gson.toJson(fullCar));
    }
}

class Invoice{
    private long time;
    private String title;
    private String seller;
    private String buyer;
    private ArrayList list;

    public Invoice(long time, String title, String seller, String buyer, ArrayList list){
        this.time = time;
        this.title = title;
        this.seller = seller;
        this.buyer = buyer;
        this.list = list;
    }

    public String nrFaktury(){
        Date date=new Date(this.time);
        //LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.time), ZoneId.systemDefault());
        return "Faktura: VAT/"+localDate.getYear()+"/"+localDate.getMonthValue()+"/"+localDate.getDayOfMonth()+"/"+localDate.getHour()+"/"+localDate.getMinute()+"/"+localDate.getSecond();
    }

}

class Invoices{
    public String fakturaAll(String filename, String title, String nabywca, String sprzedawca, String subtitle, ArrayList<FullCar> usecar, ArrayList prizecar, Float prize) throws FileNotFoundException, DocumentException {

        Document document = new Document(); // dokument pdf
        String path = "./katalogpdf/"+filename+".pdf"; // lokalizacja zapisu
        PdfWriter.getInstance(document, new FileOutputStream(path));

        document.open();
        Font bigfont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Font redfont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.RED);
        //Chunk chunk = new Chunk("tekst", font); // akapit
        Paragraph p = new Paragraph(title, bigfont);
        document.add(p);

        Paragraph p1 = new Paragraph("Nabywca: " + nabywca, font);
        document.add(p1);

        Paragraph p2 = new Paragraph("Sprzedawca: " + sprzedawca, font);
        document.add(p2);

        Paragraph sub = new Paragraph(subtitle, redfont);
        document.add(sub);

        Paragraph sub1 = new Paragraph(" ", redfont);
        document.add(sub1);

        PdfPTable table = new PdfPTable(4);

        PdfPCell c = new PdfPCell(new Phrase("lp", font));
        table.addCell(c);
        //document.add(table);

        PdfPCell c1 = new PdfPCell(new Phrase("cena", font));
        table.addCell(c1);
        //document.add(table);

        PdfPCell c2 = new PdfPCell(new Phrase("vat", font));
        table.addCell(c2);
        //document.add(table);

        PdfPCell c3 = new PdfPCell(new Phrase("wartosc", font));
        table.addCell(c3);
        //document.add(table);



        for (int i = 0; i < usecar.size();i++){
            PdfPCell c4 = new PdfPCell(new Phrase(String.valueOf(i+1), font));
            table.addCell(c4);
            //document.add(table);

            PdfPCell c5 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getCena()), font));
            table.addCell(c5);
            //document.add(table);

            PdfPCell c6 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getVat()), font));
            table.addCell(c6);
            //document.add(table);

            PdfPCell c7 = new PdfPCell(new Phrase(String.valueOf(prizecar.get(i)), font));
            table.addCell(c7);
            //document.add(table);
        }

        document.add(table);

        Paragraph finaly = new Paragraph("Do zaplaty: "+prize, bigfont);
        document.add(finaly);

        document.close();


        return filename;
    }

    public String fakturaYear(String filename, String title, String nabywca, String sprzedawca, String subtitle, ArrayList<FullCar> usecar, ArrayList prizecar, Float prize) throws FileNotFoundException, DocumentException {

        Document document = new Document(); // dokument pdf
        String path = "./katalogpdf/"+filename+".pdf"; // lokalizacja zapisu
        PdfWriter.getInstance(document, new FileOutputStream(path));

        document.open();
        Font bigfont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Font redfont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.RED);
        //Chunk chunk = new Chunk("tekst", font); // akapit
        Paragraph p = new Paragraph(title, bigfont);
        document.add(p);

        Paragraph p1 = new Paragraph("Nabywca: " + nabywca, font);
        document.add(p1);

        Paragraph p2 = new Paragraph("Sprzedawca: " + sprzedawca, font);
        document.add(p2);

        Paragraph sub = new Paragraph(subtitle, redfont);
        document.add(sub);

        Paragraph sub1 = new Paragraph(" ", redfont);
        document.add(sub1);

        PdfPTable table = new PdfPTable(5);

        PdfPCell c = new PdfPCell(new Phrase("lp", font));
        table.addCell(c);

        PdfPCell c1 = new PdfPCell(new Phrase("rok", font));
        table.addCell(c1);
        //document.add(table);

        PdfPCell c10 = new PdfPCell(new Phrase("cena", font));
        table.addCell(c10);
        //document.add(table);

        PdfPCell c2 = new PdfPCell(new Phrase("vat", font));
        table.addCell(c2);
        //document.add(table);

        PdfPCell c3 = new PdfPCell(new Phrase("wartosc", font));
        table.addCell(c3);
        //document.add(table);



        for (int i = 0; i < usecar.size();i++){
            PdfPCell c4 = new PdfPCell(new Phrase(String.valueOf(i+1), font));
            table.addCell(c4);
            //document.add(table);

            PdfPCell c50 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getRok()), font));
            table.addCell(c50);

            PdfPCell c5 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getCena()), font));
            table.addCell(c5);
            //document.add(table);

            PdfPCell c6 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getVat()), font));
            table.addCell(c6);
            //document.add(table);

            PdfPCell c7 = new PdfPCell(new Phrase(String.valueOf(prizecar.get(i)), font));
            table.addCell(c7);
            //document.add(table);
        }

        document.add(table);

        Paragraph finaly = new Paragraph("Do zaplaty: "+prize, bigfont);
        document.add(finaly);

        document.close();


        return filename;
    }

    public String fakturaPrize(String filename, String title, String nabywca, String sprzedawca, String subtitle, ArrayList<FullCar> usecar, ArrayList prizecar, Float prize) throws FileNotFoundException, DocumentException {

        Document document = new Document(); // dokument pdf
        String path = "./katalogpdf/"+filename+".pdf"; // lokalizacja zapisu
        PdfWriter.getInstance(document, new FileOutputStream(path));

        document.open();
        Font bigfont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Font redfont = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.RED);
        //Chunk chunk = new Chunk("tekst", font); // akapit
        Paragraph p = new Paragraph(title, bigfont);
        document.add(p);

        Paragraph p1 = new Paragraph("Nabywca: " + nabywca, font);
        document.add(p1);

        Paragraph p2 = new Paragraph("Sprzedawca: " + sprzedawca, font);
        document.add(p2);

        Paragraph sub = new Paragraph(subtitle, redfont);
        document.add(sub);

        Paragraph sub1 = new Paragraph(" ", redfont);
        document.add(sub1);

        PdfPTable table = new PdfPTable(5);

        PdfPCell c = new PdfPCell(new Phrase("lp", font));
        table.addCell(c);

        PdfPCell c1 = new PdfPCell(new Phrase("rok", font));
        table.addCell(c1);
        //document.add(table);

        PdfPCell c10 = new PdfPCell(new Phrase("cena", font));
        table.addCell(c10);
        //document.add(table);

        PdfPCell c2 = new PdfPCell(new Phrase("vat", font));
        table.addCell(c2);
        //document.add(table);

        PdfPCell c3 = new PdfPCell(new Phrase("wartosc", font));
        table.addCell(c3);
        //document.add(table);



        for (int i = 0; i < usecar.size();i++){
            PdfPCell c4 = new PdfPCell(new Phrase(String.valueOf(i+1), font));
            table.addCell(c4);
            //document.add(table);

            PdfPCell c50 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getRok()), font));
            table.addCell(c50);

            PdfPCell c5 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getCena()), font));
            table.addCell(c5);
            //document.add(table);

            PdfPCell c6 = new PdfPCell(new Phrase(String.valueOf(usecar.get(i).getVat()), font));
            table.addCell(c6);
            //document.add(table);

            PdfPCell c7 = new PdfPCell(new Phrase(String.valueOf(prizecar.get(i)), font));
            table.addCell(c7);
            //document.add(table);
        }

        document.add(table);

        Paragraph finaly = new Paragraph("Do zaplaty: "+prize, bigfont);
        document.add(finaly);

        document.close();


        return filename;
    }
}

class TypeInvoice{
    private String action;
    private Integer year;
    private Integer od;
    private Integer doo;

    public TypeInvoice(String action, int year, int od, int doo){
        this.action = action;
        this.year = year;
        this.od = od;
        this.doo = doo;
    }

    public Integer getDoo() {
        return doo;
    }

    public Integer getOd() {
        return od;
    }

    public Integer getYear() {
        return year;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "action='" + action + '\'' +
                ", year=" + year +
                ", od=" + od +
                ", doo=" + doo +
                '}';
    }
}

class TakeUpdate{
    private Integer idpoj;
    private String model;
    private String rok;

    public TakeUpdate(Integer idpoj, String model, String rok){
        this.idpoj = idpoj;
        this.model = model;
        this.rok = rok;
    }

    public Integer getIdpoj() {
        return idpoj;
    }

    public String getModel() {
        return model;
    }

    public String getRok() {
        return rok;
    }

    @Override
    public String toString() {
        return "TakeUpdate{" +
                "idpoj=" + idpoj +
                ", model='" + model + '\'' +
                ", rok='" + rok + '\'' +
                '}';
    }
}

class TakeId{
    private Integer idpoj;

    public TakeId(Integer idpoj){
        this.idpoj=idpoj;
    }

    public Integer getIdpoj() {
        return idpoj;
    }
}

class CustomDate {
    private int day;
    private int month;
    private int year;

    public CustomDate(Integer year, Integer month, Integer day){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "CustomDate{" +
                "day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }

    // konstruktor
    // gettery i settery
}

class FullCar {
    private Integer id;
    private String model;
    private String rok;
    private String kolor;
    private ArrayList<Airbag> airbags;
    private UUID uuid;
    private Boolean faktura;
    private CustomDate dataSprzedazy;
    private int cena;
    private int vat;


    public FullCar(String model, String rok, String kolor, ArrayList<Airbag> airbags, CustomDate dataSprzedazy, int cena, int vat) {
        this.model = model;
        this.rok = rok;
        this.kolor = kolor;
        this.airbags = airbags;
        this.dataSprzedazy = dataSprzedazy;
        this.cena = cena;
        this.vat = vat;
    }

    public int getCena() {
        return cena;
    }

    public int getVat() {
        return vat;
    }

    public CustomDate getDataSprzedazy() {
        return dataSprzedazy;
    }

    public Boolean getFaktura() {
        return faktura;
    }

    public void setFaktura(Boolean faktura) {
        this.faktura = faktura;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setRok(String rok) {
        this.rok = rok;
    }

    public void setId(Integer id){
        this.id=id;
    }


    public String getModel() {
        return model;
    }

    public String getRok() {
        return rok;
    }

    public void setUuid(UUID data) {
        this.uuid = data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getKolor() {
        return kolor;
    }

    public Integer getId() {
        return this.id;
    }

    public ArrayList<Airbag> getAirbags() {
        return airbags;
    }

    @Override
    public String toString() {
        return "FullCar{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", rok='" + rok + '\'' +
                ", kolor='" + kolor + '\'' +
                ", airbags=" + airbags +
                ", uuid=" + uuid +
                ", faktura=" + faktura +
                ", dataSprzedazy='" + dataSprzedazy + '\'' +
                ", cena=" + cena +
                ", vat=" + vat +
                '}';
    }
}

class Airbag {
    private String description;
    private boolean value;

    public Airbag(String description, Boolean value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Airbag{" +
                "description='" + description + '\'' +
                ", value=" + value +
                '}';
    }
}
