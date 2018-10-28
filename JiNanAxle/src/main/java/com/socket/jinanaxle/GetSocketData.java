package com.socket.jinanaxle;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Component
public class GetSocketData  implements ApplicationRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${mdc.socket.host}")
    private String socketHost;
    
    @Value("${mdc.socket.port}")
    private int socketPort;
    
    @Value("${mdc.socket2.host}")
    private String socket2Host;
    
    @Value("${mdc.socket2.port}")
    private int socket2Port;
    
//    力控采集信息存放的表名和TagName
    @Value("${mdc.socket2.tableName}")
    private String tableName;
    
    @Value("${mdc.socket2.TagName}")
    private String TagName;
    /*@Value("${mdc.socket3.host}")
    private String socket3Host;
    
    @Value("${mdc.socket3.port}")
    private int socket3Port;
    
    @Value("${mdc.socket2.sign}")
    private String socket2Sign;*/
    
    @Value("${mdc.socket.reconnecttime}")
    private int reconnecttime;
    
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        /*new Thread(new Runnable(){
            @Override
            public void run(){
                //扫码枪1接受数据
                scanData(socketHost,socketPort,1);
                System.out.println("方法1");
            }
        }).start();*/
    
        
        Runnable runable=new Runnable() {
            @Override
            public void run() {
                System.out.println("线程执行任务，方法1，当前时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                //扫码枪1接受数据
                scanData(socketHost,socketPort,1);
                System.out.println("方法1");
            }
        };
        Runnable runable2=new Runnable() {
            @Override
            public void run() {
                System.out.println("线程执行任务，方法2，当前时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                scanData(socket2Host,socket2Port,2);
                System.out.println("方法2");
            }
        };
        /*Runnable runable3=new Runnable() {
            @Override
            public void run() {
                System.out.println("线程执行任务，方法3，当前时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                scanData(socket3Host,socket3Port,3);
                System.out.println("方法3");
            }
        };*/
        //使用线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(runable);
        executorService.execute(runable2);
//        executorService.execute(runable3);
    }
    public void scanData(String host,int port,int voidSign){
    
        while(true) {
            String cacheResult = "";
    
            Socket socket = null;
            BufferedReader br = null;
            // PrintWriter pw=null;
            try {
//            socket=new Socket("192.168.0.112",2512);
//                socket = new Socket(socketHost, socketPort);
                socket = new Socket(host, port);
        
                boolean status = true;
                while(status){
                    try{
                        socket.sendUrgentData(0xFF);
                        Thread.sleep(1000);
                
                        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        // pw=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                        //接收结束
                        //长连接
                        while (true) {
                            String str = " ";
                            char chars[] = new char[256];
                            int len;
                            StringBuffer sb = new StringBuffer();
                            while ((len = br.read(chars)) != -1) {
//                    Thread.sleep(5000);
                                String result = new String(chars, 0, len);
                                /**
                                 * 调用方法，保存接收到的数据
                                 */
//                                this.saveData(result);
                                switch(voidSign){
                                    case 1:
                                        this.saveData(result);
                                        break;
                                    case 2:
                                        this.save2Data(result);
                                        break;
                                    /*case 3:
                                        this.save3Data(result);
                                        break;*/
                                    default:;
                                }
                                System.out.println("from server"+voidSign+": " + host + port + " : " + result);
                            }
                            System.out.println("out while reuslt from server"+voidSign+": " + host + port + " : " + sb);
                            //pw.println(str);
                            //pw.flush();
                        }
                
                    }catch(Exception e1){
                        System.out.println("服务器断开！！"+"from server"+voidSign);
                        status = false;
                    }// socket.sendUrgentData(0xFF); try catch
                }//第二个while(status)
                
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(voidSign+"连接失败，"+reconnecttime/1000+"秒后重新连接"+e.getMessage());
            } finally {
                System.out.println("finally closed......");
                try {
                    br.close();
                    //pw.close();
                    socket.close();
                } catch (Exception e) {
//                    e.printStackTrace();
                    System.out.println("finally closed Exception......");
                }
            }
    
            /**
             * 建哥指定时间之后再次执行
             */
            try{
                Thread.sleep(reconnecttime);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }//第一个while(true)
    }
    /**
     * 保存数据
     * Save Data
     * @param args
     * @throws Exception
     */
    public void  saveData(Object... args)throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" insert into JNQX_SCAN1 values (?,?,sysdate)");
        jdbcTemplate.update(sql.toString(),BaseDomain.createId(),args[0]);
    }
    /**
     * 扫码枪2保存数据
     * Save Data
     * @param args
     * @throws Exception
     */
    public void  save2Data(Object... args)throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" insert into JNQX_SCAN2 values (?,?,?,sysdate,?)");
        String scanData = args[0].toString();
        /**
         * 主齿编码 和 备齿编码 是一条数据，用特殊符号连接
         */
        /*String partCode = scanData.split(socket2Sign)[0];
        String bindCode = scanData.split(socket2Sign)[1];*/
        String partCode = scanData;
        /**
         * 2018-10-23绑定编码，获取数据库中最新的一条数据，将当前编码和这条数据进行绑定
         */
        List<Map<String,Object>> historyResult = scan2SearchHsitoryData();
        /**
         * 获取最新的记录 中的数据是备齿还是备齿
         */
        String history_isPrimary = null;
        String ISPRIMARY = "ISPRIMARY";
        if(historyResult.get(0).get(ISPRIMARY) !=null){
            history_isPrimary = historyResult.get(0).get(ISPRIMARY).toString();
        }
        /**
         * 获取最新记录中的零件编码
         */
        String history_partcode = null;
        String PARTCODE = "PARTCODE";
        if(historyResult.get(0).get(PARTCODE) !=null){
            history_partcode = historyResult.get(0).get(PARTCODE).toString();
        }
        /**
         * 获取最新记录是否已经绑定了编码
         */
        String isBindCode = null;
        String BINDCODE = "BINDCODE";
        if(historyResult.get(0).get(BINDCODE) != null){
            isBindCode = historyResult.get(0).get(BINDCODE).toString();
        }
        /**
         * 获取当前扫码的零件是主齿还是备齿
         */
        List<Map<String,Object>> isPrimaryMap = new ArrayList<>(10);
        if(! scan2SearchDBData().isEmpty()){
            /**
             * plc采集和socket之间相隔10秒钟左右，所以先把线程等待15秒再去取值
             */
            Thread.sleep(15000);
            isPrimaryMap = scan2SearchDBData();
        }
        String isPrimary = null;
        String PV = "PV";
        if(isPrimaryMap.size()>0 && isPrimaryMap.get(0).get(PV) != null){
            isPrimary = isPrimaryMap.get(0).get(PV).toString();
        }
    
        String bindCode = "";
        try {
            if( (isBindCode == null || "".equals(isBindCode)) ){
                //绑定编码
                bindCode = history_partcode;
                StringBuilder sql_update = new StringBuilder();
                sql_update.append(" update JNQX_SCAN2 t set t.bindcode ='");
                sql_update.append(scanData);
                sql_update.append("'");
                sql_update.append(" where t.partcode = '");
                sql_update.append(history_partcode);
                sql_update.append("'");
                jdbcTemplate.update(sql_update.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jdbcTemplate.update(sql.toString(),BaseDomain.createId(),partCode,bindCode,isPrimary);
    }
    
    /**
     * 查询扫码枪2最新一条数据
     * @param
     * @throws Exception
     */
    public List<Map<String,Object>> scan2SearchHsitoryData() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from (select * from JNQX_SCAN2 t  order by t.updatetime desc) where rownum=1");
        /**
         * 主齿编码 和 备齿编码 是一条数据，用特殊符号连接
         * 2018-10-23 数据变更为偶从socket连接获取，根据PLC数据判定主备齿，将相邻的两个主备齿进行绑定
         */
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql.toString());
        return result;
    }
    
    /**
     * Description：
     * 查询根据TagName查询扫码枪传递的数据是主齿还是备齿
     * Created by IDEA
     * @author wbc
     * @date 2018-10-23 0023
     * @time 下午 3:22
     */
    public List<Map<String,Object>> scan2SearchDBData() {
        //TODO 使用测试表,实际使用时换用对应的表
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from "+tableName+ " where \"TagName\"= '"+TagName+"' ");
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql.toString());
        return result;
    }
    /**
     * 扫码枪3保存数据
     * 2018-10-23 14:19:00 不再接收扫码枪3的数据，直接读取access数据库数据
     * Save Data
     * @param args
     * @throws Exception
     */
    public void  save3Data(Object... args)throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" insert into JNQX_SCAN3 values (?,?,sysdate)");
        jdbcTemplate.update(sql.toString(),BaseDomain.createId(),args[0]);
    }
}
