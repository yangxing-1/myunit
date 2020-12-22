package com.example.demo.jacob;

import java.io.File;

import org.jodconverter.DocumentConverter;
import org.jodconverter.LocalConverter;
import org.jodconverter.boot.autoconfigure.JodConverterLocalAutoConfiguration;
import org.jodconverter.boot.autoconfigure.JodConverterLocalProperties;
import org.jodconverter.office.LocalOfficeUtils;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeUrl;
import org.jodconverter.process.ProcessManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sun.star.beans.PropertyValue;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.script.provider.XScript;
import com.sun.star.script.provider.XScriptProvider;
import com.sun.star.script.provider.XScriptProviderSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

@RestController
public class TestLibreOffice {
    public final static String sourceFilePath = "D:\\资料\\测试word页数200.docx";
    public final static String targetFilePath = "D:\\资料\\测试word页数200.pdf";
    public final static String exeFolder = "D:\\Program Files\\LibreOffice";
    
    @Autowired
    private DocumentConverter documentConverter;

    @RequestMapping("/test/libreoffice")
    public void test(){
        File sourceFile = new File(sourceFilePath);
        File targetFile = new File(targetFilePath);
        try {
            long time1 = System.currentTimeMillis();
            documentConverter.convert(sourceFile).to(targetFile).execute();
            long time2 = System.currentTimeMillis();
            System.out.println("LibreOffice执行word转pdf，耗时："+ (time2 - time1) +"毫秒");
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void initDocumentConverter() {
        int[] ports = {8100};
        File officeHome = new File(exeFolder);
        File workingDir = new File(System.getProperty("java.io.tmpdir"));
        ProcessManager processManager = LocalOfficeUtils.findBestProcessManager();
        OfficeUrl[] officeUrls = LocalOfficeUtils.buildOfficeUrls(ports, null);
        //        OfficeProcessManagerPoolConfig config =
//                new OfficeProcessManagerPoolConfig(officeHome, workingDir, processManager);
//            config.setRunAsArgs(runAsArgs);
//            config.setTemplateProfileDir(templateProfileDir);
//            config.setKillExistingProcess(killExistingProcess);
//            config.setProcessTimeout(processTimeout);
//            config.setProcessRetryInterval(processRetryInterval);
//            config.setMaxTasksPerProcess(maxTasksPerProcess);
//            config.setDisableOpengl(disableOpengl);
//            config.setTaskExecutionTimeout(taskExecutionTimeout);
//            config.setTaskQueueTimeout(taskQueueTimeout);
        JodConverterLocalProperties properties = null;
        JodConverterLocalAutoConfiguration jodConverterLocalAutoConfiguration = new JodConverterLocalAutoConfiguration(properties);
        LocalConverter localConverter = LocalConverter.builder().build();
    }
//    public static void main(String[] args) {
//        try {
//            XComponentContext xLocalContext = Bootstrap.createInitialComponentContext(null);
//            System.out.println("xLocalContext");
//
//            XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
//            System.out.println("xLocalServiceManager");
//
//            Object urlResolver = xLocalServiceManager.createInstanceWithContext(
//                    "com.sun.star.bridge.UnoUrlResolver", xLocalContext);
//            System.out.println("urlResolver");
//
//            XUnoUrlResolver xUrlResolver =
//                    (XUnoUrlResolver) UnoRuntime.queryInterface(XUnoUrlResolver.class, urlResolver);            
//            System.out.println("xUrlResolve");
//            String unoMode = "";
//            String unoHost = "";
//            String unoPort = "";
//            String unoProtocol = "";
//            String unoObjectName = "";
//            String uno = "uno:" + unoMode + ",host=" + unoHost + ",port=" + unoPort + ";" + unoProtocol + ";" + unoObjectName;
//            Object rInitialObject = xUrlResolver.resolve(uno);
//            System.out.println("rInitialObject");
//
//            if (null != rInitialObject) {
//                XMultiComponentFactory xOfficeFactory = (XMultiComponentFactory) UnoRuntime.queryInterface(
//                        XMultiComponentFactory.class, rInitialObject);
//                System.out.println("xOfficeFactory");
//
//                Object desktop = xOfficeFactory.createInstanceWithContext("com.sun.star.frame.Desktop", xLocalContext);
//                System.out.println("desktop");
//
//                XComponentLoader xComponentLoader = (XComponentLoader)UnoRuntime.queryInterface(
//                        XComponentLoader.class, desktop);
//                System.out.println("xComponentLoader");
//
//                PropertyValue[] loadProps = new PropertyValue[3];
//
//                loadProps[0] = new PropertyValue();
//                loadProps[0].Name = "Hidden";
//                loadProps[0].Value = Boolean.FALSE;
//
//                loadProps[1] = new PropertyValue();
//                loadProps[1].Name = "ReadOnly";
//                loadProps[1].Value = Boolean.FALSE;
//
//                loadProps[2] = new PropertyValue();
//                loadProps[2].Name = "MacroExecutionMode";
//                loadProps[2].Value = new Short(com.sun.star.document.MacroExecMode.ALWAYS_EXECUTE_NO_WARN);
//                try {
//                    String inputFileUrl = "file:///" + sourceFilePath;
//                    XComponent xComponent = xComponentLoader.loadComponentFromURL(inputFileUrl, "_blank", 0, loadProps);
//                    System.out.println("xComponent from " + sourceFilePath);
//
//                    String macroName = "Standard.Module1.MYMACRONAME?language=Basic&amp;location=application";
//                    Object[] aParams = null;
//
//                    XScriptProviderSupplier xScriptPS = (XScriptProviderSupplier) UnoRuntime.queryInterface(XScriptProviderSupplier.class, xComponent);
//                    XScriptProvider xScriptProvider = xScriptPS.getScriptProvider(); 
//                    XScript xScript = xScriptProvider.getScript("vnd.sun.star.script:"+macroName); 
//
//                    short[][] aOutParamIndex = new short[1][1]; 
//                    Object[][] aOutParam = new Object[1][1];
//
//                    @SuppressWarnings("unused")
//                    Object result = xScript.invoke(aParams, aOutParamIndex, aOutParam);
//                    System.out.println("xScript invoke macro" + macroName);
//
//                    XStorable xStore = (XStorable)UnoRuntime.queryInterface(XStorable.class, xComponent);
//                    System.out.println("xStore");
//
//                    String outputFileType = "pdf";
//                    if (outputFileType .equalsIgnoreCase("pdf")) {
//                        System.out.println("writer_pdf_Export");
//                        loadProps[0].Name = "FilterName";
//                        loadProps[0].Value = "writer_pdf_Export";
//                    }
//                    String outputFileUrl = "file:///" + targetFilePath;
//                    xStore.storeToURL(outputFileUrl, loadProps);
//                    System.out.println("storeToURL to file " + targetFilePath);
//
//                    xComponent.dispose();
//
//                    xComponentLoader = null;
//                    rInitialObject = null;
//
//                    System.out.println("done.");
//
//                    System.exit(0);
//
//                } catch(IllegalArgumentException e) {
//                    System.err.println("Error: Can't load component from url " + sourceFilePath);
//                }
//            } else {
//                System.err.println("Error: Unknown initial object name at server side");
//            }           
//        } catch(java.lang.Exception e) {
//            System.err.println("Error: Java exception:");
//            e.printStackTrace();
//        }
//    }

}