package com.example.demowps.controller;

import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demowps.base.WpsMainPanel1;
import com.example.demowps.wps.EtMainPanel;
import com.example.demowps.wps.WppMainPanel;
import com.example.demowps.wps.WpsMainPanel;
import com.wps.api.tree.wps.Application;
import com.wps.api.tree.wps.Document;
import com.wps.api.tree.wps.Documents;
import com.wps.api.tree.wps.WdSaveFormat;
import com.wps.api.tree.wps.WdStatistic;

import com4j.Variant;

@RestController
public class TestWps {
    //public final static String sourceFilePath = "C:\\testFile\\测试word页数20.doc";
    public final static String sourceFilePath = "/testFile/testwps.docx";
    //public final static String targetFilePath = "C:\\testFile\\测试word页数200.pdf";
    public final static String targetFilePath = "/testFile/testwps.pdf";
//    @Autowired
//    private WpsMainPanel wpsMainPanel;
    
//    public static void main(String[] args) {
//        TestWps testWps = new TestWps();
//        testWps.testCommand(null, null);
//    }
    
    
    @RequestMapping("/test/wps")
    public String testCommand(@RequestParam String wordPath, @RequestParam String pdfPath) {
        StringBuilder ret = new StringBuilder();
        String arch = System.getProperty("os.arch");
        String libName = "libnetty_transport_native_epoll_unknown.so";
        if (arch.equals("amd64")) {
            libName = "libnetty_transport_native_epoll_x86_64.so";
        } else if (arch.equals("aarch64")) {
            libName = "libnetty_transport_native_epoll_aarch_64.so";
        }
        System.out.println("META-INF/native/" + arch + "/" + libName);
        ret.append("META-INF/native/" + arch + "/" + libName+"\r");

        if (wordPath == null || "".equals(wordPath)) {
            wordPath = sourceFilePath;
        }
        if (pdfPath == null || "".equals(pdfPath)) {
            pdfPath = targetFilePath;
        }
        
        //System.setProperty("sun.awt.xembedserver", "true");           //Linux下必须加这一句才能调用
        //System.setProperty("java.awt.headless", "true");
        System.out.println("开始...");
        ret.append("开始..."+"\r");
        
        JFrame mainFrame = new JFrame();
        WpsMainPanel1 wpsMainPanel = new WpsMainPanel1();
        JTabbedPane tabbedPane = new JTabbedPane();
        mainFrame.setTitle("WPS JAVA接口调用演示");                       //设置显示窗口标题
        mainFrame.setSize(1524, 768);                           //设置窗口显示尺寸
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       //置窗口是否可以关闭
        mainFrame.setResizable(false);//禁止缩放
        tabbedPane.add(wpsMainPanel, "WPS文字");
        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);                                     //设置窗口是否可见
        Application app = wpsMainPanel.getApplication();
      //打开文档
        Documents documents = app.get_Documents();
        if (documents != null) {
            System.out.println("app.get_Documents()成功："+documents);
            ret.append("app.get_Documents()成功："+documents+"\r");
        } else {
            System.out.println("app.get_Documents()失败");
            return "app.get_Documents()失败";
        }
        Document document = documents.Open(sourceFilePath, false, false, false, Variant.getMissing(), Variant.getMissing(), false, Variant.getMissing(), Variant.getMissing(), 0, 0, Variant.getMissing(), false, Variant.getMissing(), Variant.getMissing(), Variant.getMissing());
        if (document != null) {
            System.out.println("打开文档成功："+document);
            ret.append("打开文档成功："+document+"\r");
        } else {
            System.out.println("打开文档失败");
            return "打开文档失败";
        }
        int page = document.ComputeStatistics(WdStatistic.wdStatisticPages, false);
        System.out.println("页数："+page);
        ret.append("页数："+page+"\r");
        //获取版本号
        String version = app.get_Build();
        System.out.println("打开wps文字的版本号是：" + version);
        ret.append("打开wps文字的版本号是：" + version+"\r");
        //光标位置插入文本，此处写入版本号
        app.get_Selection().TypeText(version);
        //转pdf
        document.SaveAs2(targetFilePath, WdSaveFormat.wdFormatPDF, Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing(), Variant.getMissing());
        System.out.println("转pdf文件全路径："+pdfPath);
        ret.append("转pdf文件全路径："+pdfPath+"\r");
        //关闭文档
        app.get_ActiveDocument().Close(false, Variant.getMissing(), Variant.getMissing());
        //关闭wps进程并关闭窗口
        //document.Close(Variant.getMissing(), Variant.getMissing(), Variant.getMissing());
        documents.Close(Variant.getMissing(), Variant.getMissing(), Variant.getMissing());
        app.dispose();
        //mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.dispose();
        return ret.toString();
    }
    
    @RequestMapping("/wps/create")
    public boolean create() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("WPS JAVA接口调用演示");                       //设置显示窗口标题
        mainFrame.setSize(1524, 768);                           //设置窗口显示尺寸
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       //置窗口是否可以关闭
        mainFrame.setResizable(false);//禁止缩放
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(new WpsMainPanel(), "WPS文字");
        tabbedPane.add(new EtMainPanel(), "WPS表格");
        tabbedPane.add(new WppMainPanel(), "WPS演示");
        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);                                     //设置窗口是否可见
        return true;
    }
    
    @RequestMapping("/wps/init")
    public void open() {
        
    }

}
