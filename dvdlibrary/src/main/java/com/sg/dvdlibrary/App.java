/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.sg.dvdlibrary;

import com.sg.dvdlibrary.dao.DvdLibraryAuditDao;
import com.sg.dvdlibrary.dao.DvdLibraryAuditDaoFileImpl;
import com.sg.dvdlibrary.dao.DvdLibraryDao;
import com.sg.dvdlibrary.dao.DvdLibraryDaoFileImpl;
import com.sg.dvdlibrary.service.DvdLibraryServiceLayer;
import com.sg.dvdlibrary.service.DvdLibraryServiceLayerImpl;
import com.sg.dvdlibrary.ui.DvdLibraryView;
import com.sg.dvdlibrary.ui.UserIO;
import com.sg.dvdlibrary.ui.UserIOConsoleImpl;

/**
 *
 * @author Idata
 */
public class App {

    public static void main(String[] args) {
        // Instantiate the UserIO implementation
        UserIO myIo = new UserIOConsoleImpl();
        // Instantiate the View and wire the UserIO implementation into it
        DvdLibraryView myView = new DvdLibraryView(myIo);
        // Instantiate the DAO
        DvdLibraryDao myDao = new DvdLibraryDaoFileImpl();
        // Instantiate the Audit DAO
        DvdLibraryAuditDao myAuditDao = new DvdLibraryAuditDaoFileImpl();
        // Instantiate the Service Layer and wire the DAO and Audit DAO into it
        DvdLibraryServiceLayer myService = new DvdLibraryServiceLayerImpl(myDao, myAuditDao);
        // Instantiate the Controller and wire the Service Layer into it
        DvdLibraryController controller = new DvdLibraryController(myService, myView);
        // Kick off the Controller
        controller.run();
    }
}
