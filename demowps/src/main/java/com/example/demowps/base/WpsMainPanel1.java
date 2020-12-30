package com.example.demowps.base;

import com.wps.api.tree.wps.Application;
import com.wps.api.tree.wps.ClassFactory;
import com.wps.runtime.utils.WpsArgs;
import com.wps.runtime.utils.Platform;
import sun.awt.WindowIDProvider;
import org.springframework.stereotype.Component;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JPanel;

public class WpsMainPanel1 extends JPanel {
    private static final long serialVersionUID = 2360230113992867464L;
    
    private OfficePanel1 officePanel;
    private Application app;
    
    public WpsMainPanel1() {
        this.setLayout(new BorderLayout());
        officePanel = new OfficePanel1();
        this.add(officePanel, BorderLayout.CENTER);
    }
    
    @SuppressWarnings("deprecation")
    public Application getApplication() {
        if (app != null) {
            return app;
        }
      //初始化
        Canvas client = officePanel.getCanvas();
        long nativeWinId = 0;
        try {
            if (Platform.isWindows()) {
                ComponentPeer peer = client.getPeer();
                Class<?> clsCanvasPeer = Class.forName("sun.awt.windows.WComponentPeer");
                Method getHWnd = clsCanvasPeer.getDeclaredMethod("getHWnd");
                getHWnd.setAccessible(true);

                nativeWinId = (long)getHWnd.invoke(peer);
            } else {
                WindowIDProvider pid = (WindowIDProvider) client.getPeer();

                Class<?> clsCanvasPeer = Class.forName("sun.awt.X11.XEmbedCanvasPeer");
                Method removeXEmbedDropTarget = clsCanvasPeer.getDeclaredMethod("removeXEmbedDropTarget");
                removeXEmbedDropTarget.setAccessible(true);
                removeXEmbedDropTarget.invoke(pid);
                Method detachChiled = clsCanvasPeer.getDeclaredMethod("detachChild");
                detachChiled.setAccessible(true);
                Method isXEmbedActive = clsCanvasPeer.getDeclaredMethod("isXEmbedActive");
                isXEmbedActive.setAccessible(true);
                Boolean isActive = (Boolean) isXEmbedActive.invoke(pid);
                if(isActive){
                    detachChiled.invoke(pid);
                }
                nativeWinId = pid.getWindow();
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        WpsArgs args = WpsArgs.ARGS_MAP.get(WpsArgs.App.WPS);
//      args.setPath("/home/wps/workspace/wps_2016/build_debug/WPSOffice/office6/wps"); //手动指定的wps程序路径（默认调用/usr/bin/wps）
        args.setWinid(nativeWinId);
        args.setHeight(client.getHeight());
        args.setWidth(client.getWidth());
//        args.setCrypted(false); //wps2016需要关闭加密
        app = ClassFactory.createApplication();
        app.put_Visible(true);
        return app;
    }

}
