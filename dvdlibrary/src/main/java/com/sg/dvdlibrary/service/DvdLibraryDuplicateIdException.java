
package com.sg.dvdlibrary.service;

/**
 *
 * @author Idata
 */
public class DvdLibraryDuplicateIdException extends Exception {

    public DvdLibraryDuplicateIdException(String message) {
        super(message);
    }

    public DvdLibraryDuplicateIdException(String message,
            Throwable cause) {
        super(message, cause);
    }
}