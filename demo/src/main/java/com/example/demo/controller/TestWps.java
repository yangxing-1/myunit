package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wps.api.tree.wps.Application;
import com.wps.api.tree.wps.ClassFactory;
import com.wps.api.tree.wps.Document;
import com.wps.api.tree.wps.Documents;
import com.wps.api.tree.wps.WdStatistic;

import com4j.Variant;


@RestController
public class TestWps {
    //public final static String sourceFilePath = "C:\\testFile\\测试word页数20.doc";
    public final static String sourceFilePath = "/testFile/testwps.docx";
    public final static String targetFilePath = "C:\\testFile\\测试word页数200.pdf";
    
    @RequestMapping("/test/Command")
    public void testCommand(){
        try {
            String cmd = "wps " + sourceFilePath;
            Process process = Runtime.getRuntime().exec(cmd );
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping("/test/wps")
    public void test(){
        try {
            System.out.println("开始...");
            String arch = System.getProperty("os.arch");
            String libName = "libnetty_transport_native_epoll_unknown.so";
            if (arch.equals("amd64")) {
                libName = "libnetty_transport_native_epoll_x86_64.so";
            } else if (arch.equals("aarch64")) {
                libName = "libnetty_transport_native_epoll_aarch_64.so";
            }
            String libPath = "META-INF/native/" + arch + "/" + libName;
            InputStream is = ClassLoader.getSystemResourceAsStream(libPath);
            if (is == null) {
                System.out.println("未找到："+libPath);
                return;
            }
            is.close();
            // 创建WPS Application对象
            Application app = ClassFactory.createApplication();
            if (app != null) {
                System.out.println("创建WPS Application对象成功");
            }
            //打开文档，参数依次为：
            //(必填)1.文件路径 2.不是 WPS 格式时，将显示“文件转换”对话框 3.是否只读 4.是否将文件名添加到“文件”菜单底部最近使用过的文件列表中
            //5.打开文档时所需的密码 6.打开模板时所需的密码 
            //7.控制当 FileName 是一篇打开的文档的名称时应采取的操作。如果该属性值为 True，则放弃对打开文档进行的所有尚未保存的更改，并将重新打开该文件。如果该属性值为 False，则激活打开的文档
            //8.用于保存文档更改的密码 9.用于保存模板更改的密码
            //10.用于打开文档的文件转换器。可以是 WdOpenFormat 常量之一。默认值为 wdOpenFormatAuto。
            //(必填)11.用于打开文档的文件转换器。可为以下 WdOpenFormat 常量之一。要指定外部文件格式，请将 OpenFormat 属性应用于 FileConverter 对象，以确定要用于该参数的值。
            //12.当您查看保存的文档时 WPS 所使用的文档编码（代码页或字符集）。可以是任何有效的 MsoEncoding 常量。要查看有效 MsoEncoding 常量的列表，请参阅“Visual Basic 编辑器”中的“对象浏览器”。默认值为系统代码页。
            //13.如果该属性值为 True，则可在可见窗口中打开文档。默认值为 True。
            //14.指定是否打开具有脱机冲突的文档的冲突文件。
            //15.如果该属性为 True，则修复文档，以防止文档毁坏
            //16.表示文档中的横排文字。默认值为 wdLeftToRight。
            //17.如果该属性值为 True，则跳过显示当文字编码无法识别时 WPS 所显示的“编码”对话框。默认值为 False。
            Document document1 = app.get_ActiveDocument();
            if (document1 != null) {
                System.out.println("app.get_ActiveDocument()成功");
            }
            Documents documents = app.get_Documents();
            if (documents != null) {
                System.out.println("app.get_Documents()成功");
            }
            Document document = documents.Open(sourceFilePath, false, false, false, Variant.getMissing(), Variant.getMissing(), false, Variant.getMissing(), Variant.getMissing(), 0, 0, Variant.getMissing(), false, Variant.getMissing(), Variant.getMissing(), Variant.getMissing());
            if (document != null) {
                System.out.println("打开文档成功");
            }
            int page = document.ComputeStatistics(WdStatistic.wdStatisticPages, false);
            System.out.println("页数："+page);
            //获取版本号
            String version = app.get_Build();
            System.out.println("打开wps文字的版本号是：" + version);
            //光标位置插入文本，此处写入版本号
            app.get_Selection().TypeText(version);
            //关闭文档
            app.get_ActiveDocument().Close(false, 
            Variant.getMissing(), Variant.getMissing());
        } catch (Exception e) {
            System.out.println("执行出错");
            e.printStackTrace();
        }
    }

}
