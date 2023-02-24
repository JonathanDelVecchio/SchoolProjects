/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sg.dvdlibrary.service;

import com.sg.dvdlibrary.dao.DvdLibraryPersistenceException;
import com.sg.dvdlibrary.dto.Dvd;
import java.util.List;

/**
 *
 * @author Idata
 */
public interface DvdLibraryServiceLayer {
 
    void createDvd(Dvd dvd) throws
           DvdLibraryDuplicateIdException,
           DvdLibraryDataValidationException,
           DvdLibraryPersistenceException;
 
    List<Dvd> getAllDvds() throws
           DvdLibraryPersistenceException;
 
    Dvd getDvd(String title) throws
           DvdLibraryPersistenceException;
 
    Dvd removeDvd(String title) throws
           DvdLibraryPersistenceException;
 
}
