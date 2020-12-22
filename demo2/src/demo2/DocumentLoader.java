package demo2;

import java.io.File;
import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.container.XIndexAccess;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.text.XBookmarksSupplier;
import com.sun.star.text.XDocumentIndexesSupplier;
import com.sun.star.text.XEndnotesSupplier;
import com.sun.star.text.XFootnotesSupplier;
import com.sun.star.text.XPageCursor;
import com.sun.star.text.XText;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextFramesSupplier;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;

import ooo.connector.BootstrapSocketConnector;


/** This class opens a new or an existing office document.
 */
public class DocumentLoader {
    public final static String oooExeFolder = "D:\\Program Files\\LibreOffice\\program\\";
//    public final static String oooExeFolder = "/opt/libreoffice7.0/program/";
    public final static String convertType = "writer_pdf_Export";
    public final static String extension = "pdf";
    
    private String sourceFilePath;
    private String targetDir;
    private int port;
    
    private BootstrapSocketConnector bootstrapSocketConnector;
    private XComponentContext xContext;
    private XMultiComponentFactory xMCF;
    private Object oDesktop;
    private XComponentLoader xCompLoader;
    private XComponent xComp;
    private XTextDocument xTextDocument;
    private XStorable xStorable;
    
    public static void main(String[] args) {
        DocumentLoader documentLoader = new DocumentLoader();
        documentLoader.connect();
        documentLoader.getPage();
        documentLoader.convertToPdf();
        documentLoader.disConnect();
    }
    
    public DocumentLoader() {
        this.port = 8100;
        this.sourceFilePath = "C:\\testFile\\测试转pdf.doc";
        this.targetDir = "C:\\testFile";
//        this.sourceFilePath = "/testFile/测试转pdf.docx";
//        this.targetDir = "/testFile";
        
    }
    
    public void connect() {
        bootstrapSocketConnector = new BootstrapSocketConnector(oooExeFolder);
        try {
            xContext = bootstrapSocketConnector.connect("localhost", port);
        } catch (BootstrapException e) {
            e.printStackTrace();
        }
        System.out.println("Connected to a running office ...");
        xMCF = xContext.getServiceManager();
        try {
            oDesktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);
        } catch (com.sun.star.uno.Exception e) {
            e.printStackTrace();
        }
        xCompLoader = UnoRuntime.queryInterface(XComponentLoader.class, oDesktop);
        String sUrl = sourceFilePath;
        if ( sUrl.indexOf("private:") != 0) {
            File sourceFile = new File(sourceFilePath);
            StringBuffer sbTmp = new StringBuffer("file:///");
            try {
                sbTmp.append(sourceFile.getCanonicalPath().replace('\\', '/'));
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            sUrl = sbTmp.toString();
        }
        PropertyValue propertyValues[] = new PropertyValue[1];
        propertyValues[0] = new PropertyValue();
        propertyValues[0].Name = "Hidden";
        propertyValues[0].Value = Boolean.TRUE;
        try {
            xComp = xCompLoader.loadComponentFromURL(sUrl, "_blank", 0, propertyValues);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        xTextDocument = UnoRuntime.queryInterface(XTextDocument.class, xComp);
        xStorable = UnoRuntime.queryInterface(XStorable.class, xComp);
    }
    
    public void disConnect() {
        XCloseable xCloseable = UnoRuntime.queryInterface(XCloseable.class, xStorable);
        if ( xCloseable != null ) {
            try {
                xCloseable.close(false);
            } catch (CloseVetoException e) {
                e.printStackTrace();
            }
        } else {
            xComp.dispose();
        }
        //xTextDocument.dispose();
        bootstrapSocketConnector.disconnect();
    }
    
    public int getPage() {
        XModel xModel = UnoRuntime.queryInterface(XModel.class,xComp);
        XController xController = xModel.getCurrentController();
        XTextViewCursorSupplier xViewCursorSupplier = UnoRuntime.queryInterface(XTextViewCursorSupplier.class, xController);
        XTextViewCursor xViewCursor = xViewCursorSupplier.getViewCursor();
        XPageCursor xPageCursor = UnoRuntime.queryInterface(XPageCursor.class, xViewCursor);
        xPageCursor.jumpToLastPage();
        short page = xPageCursor.getPage();
        System.out.println("获取页数：" + page);
        return page;
    }
    
    public void convertToPdf() {
        File sourceFile = new File(sourceFilePath);
        File outdir = new File(targetDir);
        String sOutUrl = "file:///" + outdir.getAbsolutePath().replace( '\\', '/' );
        String sourceUrl = "file:///" + sourceFile.getAbsolutePath().replace( '\\', '/' );
//        PropertyValue propertyValues[] = new PropertyValue[1];
//        propertyValues[0] = new PropertyValue();
//        propertyValues[0].Name = "Hidden";
//        propertyValues[0].Value = Boolean.TRUE;
//        Object oDocToStore = null;
//        try {
//            oDocToStore = xCompLoader.loadComponentFromURL(sourceUrl, "_blank", 0, propertyValues);
//        } catch (IOException | IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//        XStorable xStorable = UnoRuntime.queryInterface(XStorable.class, oDocToStore );

        PropertyValue[] propertyValues = new PropertyValue[2];
        // Setting the flag for overwriting
        propertyValues[0] = new PropertyValue();
        propertyValues[0].Name = "Overwrite";
        propertyValues[0].Value = Boolean.TRUE;
        // Setting the filter name
        propertyValues[1] = new PropertyValue();
        propertyValues[1].Name = "FilterName";
        propertyValues[1].Value = convertType;

        // Appending the favoured extension to the origin document name
        int index1 = sourceUrl.lastIndexOf('/');
        int index2 = sourceUrl.lastIndexOf('.');
        String sStoreUrl = sOutUrl + sourceUrl.substring(index1, index2 + 1)+ extension;
        
        try {
            xStorable.storeToURL(sStoreUrl, propertyValues);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
//    @SuppressWarnings("unused")
//    public void test() {
//        try {
//            // get the remote office component context
//            //xContext = Bootstrap.bootstrap();
//            //xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
//            
//            PropertyValue[] propertyValues = xTextDocument.getArgs();
//            System.out.println("------------------------------------------------------------------------------------");
//            for (PropertyValue p : propertyValues) {
//                System.out.println(p.Name);
//            }
//            XText tt = xTextDocument.getText();
//
//            XPropertySet xPropertySet = UnoRuntime.queryInterface(XPropertySet.class, xTextDocument);
//            listXPropertySet(xPropertySet);
//
//            XFootnotesSupplier eee = UnoRuntime.queryInterface(XFootnotesSupplier.class, xTextDocument);
//            XIndexAccess f1 = eee.getFootnotes();
//            XPropertySet f2 = eee.getFootnoteSettings();
//            listXPropertySet(f2);
//
//            XEndnotesSupplier endnotes = UnoRuntime.queryInterface(XEndnotesSupplier.class, xTextDocument);
//            XPropertySet endPro = endnotes.getEndnoteSettings();
//            listXPropertySet(endPro);
//
//            XBookmarksSupplier bookmark = UnoRuntime.queryInterface(XBookmarksSupplier.class, xTextDocument);
//            System.out.println("------------------------------------------------------------------------------------");
//            for (String sbook : bookmark.getBookmarks().getElementNames()) {
//                System.out.println(sbook + " - " + bookmark.getBookmarks().getByName(sbook));
//            }
//
//            XDocumentIndexesSupplier ind = UnoRuntime.queryInterface(XDocumentIndexesSupplier.class, xTextDocument);
//            XIndexAccess doci = ind.getDocumentIndexes();
//            System.out.println("doci.getCount():"+doci.getCount());
//
//            XTextFramesSupplier xframe = UnoRuntime.queryInterface(XTextFramesSupplier.class, xTextDocument);
//            String[] eleNames = xframe.getTextFrames().getElementNames();
//
//
//            XModel xModel = UnoRuntime.queryInterface(XModel.class,xComp);
//            XController xController = xModel.getCurrentController();
//            XTextViewCursorSupplier xViewCursorSupplier = UnoRuntime.queryInterface(XTextViewCursorSupplier.class, xController);
//            XTextViewCursor xViewCursor = xViewCursorSupplier.getViewCursor();
//            XPropertySet xCursorPropertySet = UnoRuntime.queryInterface(XPropertySet.class, xViewCursor);
//            //xCursorPropertySet.setPropertyValue("CharStyleName", "Quotation");
//            //xCursorPropertySet.setPropertyValue("ParaStyleName", "Quotations");
//            XPageCursor xPageCursor = UnoRuntime.queryInterface(XPageCursor.class, xViewCursor);
//            boolean endPage = xPageCursor.jumpToLastPage();
//            short page = xPageCursor.getPage();
//            System.out.println("获取页数：" + page);
//            return;
//        }
//        catch( Exception e ) {
//            e.printStackTrace(System.err);
//        }finally {
//            xTextDocument.dispose();
//            bootstrapSocketConnector.disconnect();
//        }
//    }
//    
//    
//    public static void listXPropertySet(XPropertySet xPropertySet) {
//        System.out.println("------------------------------------------------------------------------------------");
//        Property[] property1s = xPropertySet.getPropertySetInfo().getProperties();
//        try {
//            for (Property p : property1s) {
//                Object value = xPropertySet.getPropertyValue(p.Name);
//                System.out.println(p.Name + " - " + value);
//            }
//        } catch (UnknownPropertyException | WrappedTargetException e) {
//            e.printStackTrace();
//        }
//    }
}

