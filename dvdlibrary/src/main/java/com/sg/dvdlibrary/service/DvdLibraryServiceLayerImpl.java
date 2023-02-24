/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sg.dvdlibrary.service;

import com.sg.dvdlibrary.dao.DvdLibraryAuditDao;
import com.sg.dvdlibrary.dao.DvdLibraryDao;
import com.sg.dvdlibrary.dao.DvdLibraryPersistenceException;
import com.sg.dvdlibrary.dto.Dvd;
import java.util.List;

/**
 *
 * @author Idata
 */
public class DvdLibraryServiceLayerImpl implements
        DvdLibraryServiceLayer {

    private DvdLibraryDao dao;
    private DvdLibraryAuditDao auditDao;

    public DvdLibraryServiceLayerImpl(DvdLibraryDao dao, DvdLibraryAuditDao auditDao) {
        this.dao = dao;
        this.auditDao = auditDao;
    }

    @Override
    public void createDvd(Dvd dvd) throws
            DvdLibraryDuplicateIdException,
            DvdLibraryDataValidationException,
            DvdLibraryPersistenceException {

        // First check to see if there is alreay a dvd 
        // associated with the given dvd's id
        // If so, we're all done here - 
        // throw a DvdLibraryDuplicateIdException
        if (dao.getDvd(dvd.getTitle()) != null) {
            throw new DvdLibraryDuplicateIdException(
                    "ERROR: Could not create dvd.  Dvd"
                    + dvd.getTitle()
                    + " already exists");
        }

        // Now validate all the fields on the given Dvd object.  
        // This method will throw an
        // exception if any of the validation rules are violated.
        validateDvdData(dvd);

        // We passed all our business rules checks so go ahead 
        // and persist the Dvd object
        dao.addDvd(dvd.getTitle(), dvd);

        auditDao.writeAuditEntry(
                "Dvd " + dvd.getTitle() + " CREATED.");

    }

    @Override
    public List<Dvd> getAllDvds() throws DvdLibraryPersistenceException {
        return dao.getAllDvds();
    }

    @Override
    public Dvd getDvd(String title) throws DvdLibraryPersistenceException {
        return dao.getDvd(title);
    }

    @Override
    public Dvd removeDvd(String title) throws DvdLibraryPersistenceException {
        Dvd removedDvd = dao.removeDvd(title);
        auditDao.writeAuditEntry("Dvd " + title + " REMOVED.");
        return removedDvd;
    }

    private void validateDvdData(Dvd dvd) throws
            DvdLibraryDataValidationException {

        if (dvd.getReleaseDate() == null
                || dvd.getReleaseDate().trim().length() == 0
                || dvd.getMpaaRating() == null
                || dvd.getMpaaRating().trim().length() == 0
                || dvd.getDirectorName() == null
                || dvd.getDirectorName().trim().length() == 0
                || dvd.getStudio() == null
                || dvd.getStudio().trim().length() == 0
                || dvd.getUserRatingNote() == null
                || dvd.getUserRatingNote().trim().length() == 0) {

            throw new DvdLibraryDataValidationException(
                    "ERROR: All fields ["
                    + "Title, "
                    + "Release Date, "
                    + "MPAA Rating, "
                    + "Director Name, "
                    + "Studio, "
                    + "User Rating"
                    + "] are required.");

        }
    }
}
