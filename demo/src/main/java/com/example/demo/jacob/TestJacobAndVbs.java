package com.example.demo.jacob;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.cluster.metadata.AliasAction.Add;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

@RestController
public class TestJacobAndVbs {
    // 退出或关闭时保存的参数
    public static final int wdDoNotSaveChanges = 0;// 不保存待定的更改
    public static final int wdPromptToSaveChanges = -2;// 询问是否保存
    public static final int wdSaveChanges = -1;// 不询问，自动保存
    //ComputeStatistics参数
    public static final int wdStatisticPages = 2;// word文档统计信息-页数
    //word转pdf
    public static final int wdFormatPDF = 17;// word转PDF 格式
    
    public final static String sourceFilePath = "C:\\testFile\\测试word页数200.doc";
    public final static String targetFilePath = "C:\\testFile\\测试word页数200.pdf";
    public final static String targetFilePath1 = "C:\\testFile\\测试word页数211_1.pdf";
    public final static String scriptGetPagePath = "Cscript.exe C:\\testFile\\getWordPage.vbs";
    public final static String scriptToPdfPath = "Wscript.exe C:\\testFile\\wordToPdf.vbs";
    
    private StringBuilder responseBody;

    public TestJacobAndVbs() {
        responseBody = new StringBuilder();
    }
    
    public static void main(String[] args) throws IOException {
        TestJacobAndVbs testJacobAndVbs = new TestJacobAndVbs();
        testJacobAndVbs.officeJacobGetPage();//jacob调用office接口获取页数
        //testJacobAndVbs.officeJacobToPdf();//jacob调用office接口转pdf
        //testJacobAndVbs.vbsGetPage();//vbs获取页数
        //testJacobAndVbs.vbsToPdf();//vbs转pdf
        //testJacobAndVbs.wpsJacobGetPage();//jacob调用wps接口获取页数
        //testJacobAndVbs.wpsJacobToPdf();//jacob调用wps接口转pdf
    }
    
    @RequestMapping("/http/test")
    public String httpTest() {
        officeJacobGetPage();//jacob调用office接口获取页数
        //officeJacobToPdf();//jacob调用office接口转pdf
        //vbsGetPage();//vbs获取页数
        //vbsToPdf();//vbs转pdf
        //wpsJacobGetPage();//jacob调用wps接口获取页数
        //wpsJacobToPdf();//jacob调用wps接口转pdf
        return responseBody.toString();
    }
    
    public void vbsGetPage() {
        long time1 = System.currentTimeMillis();
        try {
            String cmd = scriptGetPagePath + " " + sourceFilePath;
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            InputStream in = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            List<String> returnList = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                returnList.add(line);
            }
            in.close();
            reader.close();
            process.destroy();
            String page = returnList.get(returnList.size() - 1);
            long time2 = System.currentTimeMillis();
            String str = "vbs获取word文件页数：" + page + "页，耗时："+(time2 - time1)+"毫秒";
            System.out.println(str);
            responseBody.append(str).append("\r\n");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void vbsToPdf() {
        long time1 = System.currentTimeMillis();
        try {
            String cmd = scriptToPdfPath + " " + sourceFilePath + " " + targetFilePath;
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            process.destroy();
            long time2 = System.currentTimeMillis();
            String str = "vbs转pdf，耗时："+(time2 - time1)+"毫秒";
            System.out.println(str);
            responseBody.append(str).append("\r\n");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unused")
    public void officeJacobGetPage() {
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        try {
            app = new ActiveXComponent("Word.Application");
            //app.setProperty("Visible", false);
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", sourceFilePath, false, false, Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING, 
                    Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING, 
                    Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING, Variant.VT_MISSING).toDispatch();
            // 获取统计信息-页数统计
            Variant variant = Dispatch.call(doc, "ComputeStatistics", wdStatisticPages, Variant.VT_MISSING);
            // 获取selection
            Dispatch selection = Dispatch.call(app, "Selection").toDispatch();
            // 从selection所在位置开始查询   
            Dispatch find = Dispatch.call(selection, "Find").toDispatch();  
            // 设置要查找的内容   
            Dispatch.put(find, "Text", "这是要测试添加批注的地方");  
            // 向前查找   
            Dispatch.put(find, "Forward", "True");  
            // 设置格式   
            Dispatch.put(find, "Format", "True");  
            // 大小写匹配   
            Dispatch.put(find, "MatchCase", "True");  
            // 全字匹配   
            Dispatch.put(find, "MatchWholeWord", "True");  
            // 查找并选中   
//            Dispatch.call(find, "Execute").getBoolean();  
//            Dispatch.call(selection, "Collapse", "wdCollapseEnd");
//            Dispatch comments = Dispatch.call(doc, "Comments").toDispatch();
//            Dispatch range = Dispatch.get(selection, "Range").toDispatch();  
//            Variant addComment = Dispatch.call(comments, "add", range, "aaa");
            Dispatch.call(doc, "SaveAs", sourceFilePath);
            Dispatch.call(doc, "Close", false);
            long end = System.currentTimeMillis();
            String str = "jacob调用office获取word文件页数：" + variant.getInt() + "页，用时：" + (end - start) + "毫秒";
            System.out.println(str);
            responseBody.append(str).append("\r\n");
        } catch (Exception e) {
            String error = "jacob调用office获取Word页数出错：" + e.getMessage();
            System.out.println(error);
            responseBody.append(error).append("\r\n");
        } finally {
            if (app != null) {
                //app.invoke("Quit", wdDoNotSaveChanges);
                Dispatch.call(app, "Quit", wdDoNotSaveChanges);  
            }
        }
    }
    
    public void officeJacobToPdf() {
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", false);
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", sourceFilePath, false, true).toDispatch();
            File tofile = new File(targetFilePath1);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "SaveAs", targetFilePath1, wdFormatPDF);
            Dispatch.call(doc, "Close", false);
            long end = System.currentTimeMillis();
            String str = "jacob调用office转pdf" + "，用时：" + (end - start) + "毫秒";
            System.out.println(str);
            responseBody.append(str).append("\r\n");
        } catch (Exception e) {
            String error = "jacob调用office转PDF出错：" + e.getMessage();
            System.out.println(error);
            responseBody.append(error).append("\r\n");
        } finally {
            if (app != null) {
                app.invoke("Quit", wdDoNotSaveChanges);
            }
        }
    }
    
    public void wpsJacobGetPage() {
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        try {
            app = new ActiveXComponent("kwps.application");
            app.setProperty("Visible", false);
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", sourceFilePath, false, true).toDispatch();
            Variant variant = Dispatch.call(doc, "ComputeStatistics", 2);
            Dispatch.call(doc, "Close", false);
            long end = System.currentTimeMillis();
            String str = "jacob调用wps获取word文件页数：" + variant.getInt() + "页，用时：" + (end - start) + "毫秒";
            System.out.println(str);
            responseBody.append(str).append("\r\n");
        } catch (Exception e) {
            String error = "jacob调用wps获取Word页数出错：" + e.getMessage();
            System.out.println(error);
            responseBody.append(error).append("\r\n");
        } finally {
            if (app != null) {
                app.invoke("Quit", wdDoNotSaveChanges);
            }
        }
    }
    
    public void wpsJacobToPdf() {
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        try {
            app = new ActiveXComponent("kwps.Application");
            app.setProperty("Visible", false);
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", sourceFilePath, false, true).toDispatch();
            File tofile = new File(targetFilePath1);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "SaveAs", targetFilePath1, wdFormatPDF);
            Dispatch.call(doc, "Close", false);
            long end = System.currentTimeMillis();
            String str = "jacob调用office转pdf" + "，用时：" + (end - start) + "毫秒";
            System.out.println(str);
            responseBody.append(str).append("\r\n");
        } catch (Exception e) {
            String error = "jacob调用office转PDF出错：" + e.getMessage();
            System.out.println(error);
            responseBody.append(error).append("\r\n");
        } finally {
            if (app != null) {
                app.invoke("Quit", wdDoNotSaveChanges);
            }
        }
    }
}
