package com.sg.dvdlibrary;

import com.sg.dvdlibrary.dao.DvdLibraryDao;
import com.sg.dvdlibrary.dao.DvdLibraryPersistenceException;
import com.sg.dvdlibrary.dto.Dvd;
import com.sg.dvdlibrary.service.DvdLibraryDataValidationException;
import com.sg.dvdlibrary.service.DvdLibraryDuplicateIdException;
import com.sg.dvdlibrary.service.DvdLibraryServiceLayer;
import com.sg.dvdlibrary.ui.DvdLibraryView;
import com.sg.dvdlibrary.ui.UserIO;
import java.util.List;

public class DvdLibraryController {

    private DvdLibraryServiceLayer service;
    private DvdLibraryView view;
    private UserIO io;

    public DvdLibraryController(DvdLibraryServiceLayer service, DvdLibraryView view) {
        this.service = service;
        this.view = view;

    }

    public void run() {
        boolean keepGoing = true;
        int menuSelection = 0;
        try {
            while (keepGoing) {

                menuSelection = getMenuSelection();

                switch (menuSelection) {
                    case 1:
                        listDvds();
                        break;
                    case 2:
                        createDvd();
                        break;
                    case 3:
                        viewDvd();
                        break;
                    case 4:
                        removeDvd();
                        break;
                    case 5:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }

            }
            exitMessage();
        } catch (DvdLibraryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private int getMenuSelection() {
        return view.printMenuAndGetSelection();
    }

    private void createDvd() throws DvdLibraryPersistenceException {
        view.displayCreateDvdBanner();
        boolean hasErrors = false;
        do {
            Dvd currentDvd = view.getNewDvdInfo();
            try {
                service.createDvd(currentDvd);
                view.displayCreateSuccessBanner();
                hasErrors = false;
            } catch (DvdLibraryDuplicateIdException | DvdLibraryDataValidationException e) {
                hasErrors = true;
                view.displayErrorMessage(e.getMessage());
            }
        } while (hasErrors);
    }

    private void listDvds() throws DvdLibraryPersistenceException {
        view.displayDisplayAllBanner();
        List<Dvd> dvdList = service.getAllDvds();
        view.displayDvdList(dvdList);
    }

    private void viewDvd() throws DvdLibraryPersistenceException {
        view.displayDisplayDvdBanner();
        String title = view.getTitleChoice();
        Dvd dvd = service.getDvd(title);
        view.displayDvd(dvd);
    }

    private void removeDvd() throws DvdLibraryPersistenceException {
        view.displayRemoveDvdBanner();
        String title = view.getTitleChoice();
        Dvd removedDvd = service.removeDvd(title);
        view.displayRemoveResult(removedDvd);
    }

    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }

    private void exitMessage() {
        view.displayExitBanner();
    }

}
