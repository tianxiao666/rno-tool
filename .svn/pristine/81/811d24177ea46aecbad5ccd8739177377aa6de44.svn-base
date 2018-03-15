package com.hgicreate.rno.rnoareatool.commandline;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


@Component
public class GenerateAreaData implements CommandLineRunner {
    @Override
    public void run(String... strings) throws Exception {
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "1080");

//        final String IN_FILE_NAME = "E:\\JavaScratch\\all-area.csv";
        final String IN_FILE_NAME = strings[0];
//        final String OUT_FILE_NAME = "E:\\JavaScratch\\out-area.csv";
        final String OUT_FILE_NAME = strings[1];
        final String[] FILE_HEADER = {"id","\"code\"","\"name\"","parent_id","\"first_letter\"","level"};
        final String[] OUT_FILE_HEADER = {"id","name","level","parent_id","longitude","latitude"};

        CSVFormat format = CSVFormat.DEFAULT.withHeader(FILE_HEADER).withSkipHeaderRecord();
        List<Area> readRecords = new ArrayList<>();

        try(DataInputStream inputStream = new DataInputStream(new FileInputStream(new File(IN_FILE_NAME)));
            BufferedReader in= new BufferedReader(new InputStreamReader(inputStream,"UTF-8"))) {
            Iterable<CSVRecord> records = format.parse(in);
            for (CSVRecord record : records) {
                readRecords.add(new Area(Integer.parseInt(record.get("id")), Integer.parseInt(record.get("\"code\"")),record.get("\"name\""), Integer.parseInt(record.get("parent_id")), record.get("\"first_letter\""), Integer.parseInt(record.get("level"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try(DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(OUT_FILE_NAME)));
            BufferedWriter out= new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            CSVPrinter printer = new CSVPrinter(out, format)){
            printer.printRecord(OUT_FILE_HEADER);
        }catch (Exception ec){
            ec.printStackTrace();
        }

        for (int i=0; i<readRecords.size();i++){
            Area readRecord = readRecords.get(i);
            String name = "";
            name = getRecordName(name, readRecord, readRecords);
            try(DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(OUT_FILE_NAME),true));
                BufferedWriter out= new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                CSVPrinter printer = new CSVPrinter(out, format)){
                try {
                    List<String> records = new ArrayList<>();
                    String path = "https://www.google.com/search?q="+URLEncoder.encode(name, "UTF-8")+"%20"+URLEncoder.encode("经纬度", "UTF-8");
                    Document doc = Jsoup.connect(path).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36").get();
                    Elements newsHeadlines = doc.select("#rso ._XWk");
                    String s[] = newsHeadlines.html().trim().split("° N, ");
                    records.add(readRecord.getCode()+"");
                    records.add(readRecord.getName());
                    records.add((readRecord.getLevel()+1)+"");
                    if(readRecord.getLevel() == 0){
                        records.add("0");
                    }else{
                        records.add((readRecords.get(readRecord.getParent_id()-1)).getCode()+"");
                    }
                    records.add((s[1].split("° E"))[0]);
                    records.add(s[0]);
                    printer.printRecord(records);
                    System.out.println("正在执行操作，请等待。。。进度："+String.format("%.2f",100*i/Float.parseFloat(readRecords.size()+""))+"%");
                }catch (Exception e){
                    System.out.println(e + readRecord.getName());
                    List<String> records = new ArrayList<>();
                    records.add(readRecord.getCode()+"");
                    records.add(readRecord.getName());
                    records.add((readRecord.getLevel()+1)+"");
                    if(readRecord.getLevel() == 0){
                        records.add("0");
                    }else{
                        records.add((readRecords.get(readRecord.getParent_id()-1)).getCode()+"");
                    }
                    records.add("0");
                    records.add("0");
                    printer.printRecord(records);
                    System.out.println("正在执行操作，请等待。。。进度："+String.format("%.2f",100*i/Float.parseFloat(readRecords.size()+""))+"%");
                }
            }catch (Exception ec){
                System.out.println(ec + readRecord.getName());
            }
        }
        System.out.println("完成!");
    }

    String getRecordName(String name, Area readRecord, List readRecords){
        name = readRecord.getName() + name;
        if (readRecord.getLevel() == 0){
            return name;
        }else {
            return getRecordName(name, (Area) readRecords.get(readRecord.getParent_id()-1), readRecords);
        }
    }
}
