package com.hgicreate.rno;

import com.linuxense.javadbf.DBFReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class RnoGridToolApplication {

    public static void main(String[] args) {

        SpringApplication.run(RnoGridToolApplication.class, args);

        String path = "E:/gridData";

        traverseFolder(path);

    }

    public static Map<Integer, String> readDBF(String pathName) {
        Map<Integer, String> datObj = new HashMap<>();
        InputStream fis = null;
        String gridType = pathName.substring(
                (pathName.lastIndexOf(System.getProperty("file.separator")) + 3),
                (pathName.lastIndexOf(System.getProperty("file.separator")) + 4));
        try {
            // 读取文件的输入流
            fis = new FileInputStream(pathName);
            // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息
            DBFReader reader = new DBFReader(fis);
            reader.setCharactersetName("GBK");

            Object[] rowValues;
            int index = 0;
            // 一条条取出path文件中记录
            while ((rowValues = reader.nextRecord()) != null) {
                for (int i = 0; i < rowValues.length; i++) {
                    if(gridType.equals("A")) {
                        if(i%3==0){
                            index++;
                            datObj.put(index, rowValues[i].toString().trim());
                        }
                    }else if(gridType.equals("B")) {
                        if(i%2==0){
                            index++;
                            datObj.put(index, rowValues[i].toString().trim());
                        }
                    } else if(gridType.equals("C")) {
                        if(i%2==0){
                            index++;
                            datObj.put(index, rowValues[i].toString().trim());
                        }
                    }
                }
            }
            System.out.println(datObj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return datObj;
    }

    private static boolean parseMifFile(String pathName, Map<Integer, String> datObj) {

        String areaName = pathName.substring(pathName.lastIndexOf(
                System.getProperty("file.separator")) + 1,
                pathName.lastIndexOf(System.getProperty("file.separator")) + 3);
        String gridType = pathName.substring(pathName.lastIndexOf(
                System.getProperty("file.separator")) + 3,
                pathName.lastIndexOf(System.getProperty("file.separator")) + 4);

        Connection con = null;
        Statement st = null;
        PreparedStatement stmt1 = null, stmt2 = null;
        ResultSet res1 = null, res2 = null, res3 = null;
        String url = "jdbc:oracle:thin:@192.168.50.20:1521:rnodb";
        String user = "RNO4DEV";
        String password = "123456";

        InputStreamReader isr = null;
        File file = new File(pathName);
        long areaId = 0;
        long gridId = 0;
        long coordId = 0;
        boolean ok = true;

        String areaSql = "SELECT id, name FROM rno_sys_area WHERE area_level = 2";
        String dataSeqSql = "select SEQ_LTE_GRID_DATA.nextval from Dual";
        String coordSeqSql = "select SEQ_LTE_GRID_COORD.nextval from Dual";
        String sql1 = "insert into RNO_LTE_GRID_DATA (ID, AREA_ID, GRID_TYPE," +
                "GRID_CODE, CENTER_COORD) values (?,?,?,?,?)";
        String sql2 = "insert into RNO_LTE_GRID_COORD(ID, GRID_ID, LONGITUDE, LATITUDE) values" +
                "(?,?,?,?)";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);
            st = con.createStatement();
            res1 = st.executeQuery(areaSql);
            while(res1.next()) {
                if(res1.getString("NAME").startsWith(areaName)) {
                    areaId = Long.parseLong(res1.getString("ID"));
                    break;
                }
            }
            stmt1 = con.prepareStatement(sql1);
            stmt2 = con.prepareStatement(sql2);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                isr = new InputStreamReader(new FileInputStream(file),
                        "gb18030");
                BufferedReader br = new BufferedReader(isr);
                String lineText;
                String lineArr[];
                String centerArr[];
                boolean beggin = false;
                int cnt = 0;
                //System.out.println("MIF文件===========>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始解析");
                //循环读取每一行
                while ((lineText = br.readLine()) != null) {
                    if (lineText.contains("Region")) {
                        res2 = st.executeQuery(dataSeqSql);
                        while(res2.next()) {
                            gridId = res2.getLong(1);
                        }
                        //System.out.println("gridId=" + gridId);
                        beggin = true;
                        cnt++;
                    } else {
                        if (beggin) {
                            if (lineText.split(" ").length == 2) {
                                //去除前后导空格后，以空格分裂网络经纬度数组
                                lineArr = lineText.trim().split(" ");
                                if (isNum(lineArr[0]) && isNum(lineArr[1])) {
                                    //System.out.println("lineText==" + lineText);
                                    res3 = st.executeQuery(coordSeqSql);
                                    while(res3.next()) {
                                        coordId = res3.getLong(1);
                                    }
                                    //System.out.println("coordSeqId=" + coordId);
                                    stmt2.setLong(1, coordId);
                                    stmt2.setLong(2, gridId);
                                    stmt2.setDouble(3, Double.parseDouble(lineArr[0]));
                                    stmt2.setDouble(4, Double.parseDouble(lineArr[1]));
                                    stmt2.addBatch();
                                }
                            }
                            if (lineText.contains("Center")) {
                                //去除前后导空格后，以空格分裂网格中心点经纬度数组
                                centerArr = lineText.trim().split(" ");
                                stmt1.setLong(1, gridId);
                                stmt1.setLong(2, areaId);
                                stmt1.setString(3, gridType);
                                stmt1.setString(4, datObj.get(cnt));
                                stmt1.setString(5, centerArr[1] + "," + centerArr[2]);
                                stmt1.addBatch();
                            }
                        }
                    }
                }
                stmt1.executeBatch();
                stmt2.executeBatch();
                con.commit();
            } else {
                ok = false;
                System.out.println("找不到指定文件！！！！");
            }
        } catch (Exception e) {
            System.out.println("读取MIF文件出错！！！！！！！！！！");
            ok = false;
            e.printStackTrace();
        } finally {
            try {
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                res1.close();
                res2.close();
                res3.close();
                st.close();
                stmt1.close();
                stmt2.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return ok;
    }

    private static boolean isNum(String s) {
        return s.matches("[1-9]\\d*\\.?\\d*");
    }

    private static void traverseFolder(String path) {
        File file = new File(path);
        String dat = "", mif = "";
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolder(file2.getAbsolutePath());
                    } else {
                        //System.out.println("正在入库文件:" + file2.getAbsolutePath());
                        if(file2.getAbsolutePath().endsWith(".DAT")) {
                            System.out.println("正在解析文件:" + file2.getAbsolutePath());
                            dat = file2.getAbsolutePath();
                        } else if(file2.getAbsolutePath().endsWith(".MIF")) {
                            System.out.println("正在解析文件:" + file2.getAbsolutePath());
                            mif = file2.getAbsolutePath();
                        }
                        if(dat != "" && mif != "") {
                            parseMifFile(mif, readDBF(dat));
                            dat = "";
                            mif = "";
                        }
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }

}
