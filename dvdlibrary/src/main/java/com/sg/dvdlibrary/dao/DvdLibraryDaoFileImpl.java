package com.sg.dvdlibrary.dao;

import com.sg.dvdlibrary.dto.Dvd;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Idata
 */
public class DvdLibraryDaoFileImpl implements DvdLibraryDao {

    public static final String ROSTER_FILE = "roster.txt";
    public static final String DELIMITER = "::";

    @Override
    public Dvd addDvd(String title, Dvd dvd)
            throws DvdLibraryPersistenceException {
        loadRoster();
        Dvd newDvd = dvds.put(title, dvd);
        writeRoster();
        return newDvd;
    }

    @Override
    public List<Dvd> getAllDvds()
            throws DvdLibraryPersistenceException {
        loadRoster();
        return new ArrayList(dvds.values());
    }

    @Override
    public Dvd getDvd(String title)
            throws DvdLibraryPersistenceException {
        loadRoster();
        return dvds.get(title);
    }

    @Override
    public Dvd removeDvd(String title)
            throws DvdLibraryPersistenceException {
        loadRoster();
        Dvd removedDvd = dvds.remove(title);
        writeRoster();
        return removedDvd;
    }

    private Map<String, Dvd> dvds = new HashMap<>();

    private Dvd unmarshallDvd(String dvdAsText) {

        String[] dvdTokens = dvdAsText.split(DELIMITER);

        String title = dvdTokens[0];

        Dvd dvdFromFile = new Dvd(title);


        dvdFromFile.setReleaseDate(dvdTokens[1]);

        // Index 2 - LastName
        dvdFromFile.setMpaaRating(dvdTokens[2]);

        // Index 3 - Cohort
        dvdFromFile.setDirectorName(dvdTokens[3]);

        dvdFromFile.setStudio(dvdTokens[4]);

        dvdFromFile.setUserRatingNote(dvdTokens[5]);

        return dvdFromFile;
    }

    private void loadRoster() throws DvdLibraryPersistenceException {
        Scanner scanner;

        try {
            // Create Scanner for reading the file
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(ROSTER_FILE)));
        } catch (FileNotFoundException e) {
            throw new DvdLibraryPersistenceException(
                    "-_- Could not load roster data into memory.", e);
        }

        String currentLine;
        
        Dvd currentDvd;
        
        
        while (scanner.hasNextLine()) {
            // get the next line in the file
            currentLine = scanner.nextLine();
 
            currentDvd = unmarshallDvd(currentLine);

            
            dvds.put(currentDvd.getTitle(), currentDvd);
        }
        // close scanner
        scanner.close();
    }

    private String marshallDvd(Dvd aDvd) {

        String dvdAsText = aDvd.getTitle() + DELIMITER;


        dvdAsText += aDvd.getReleaseDate() + DELIMITER;

        dvdAsText += aDvd.getMpaaRating() + DELIMITER;

        dvdAsText += aDvd.getDirectorName() + DELIMITER;

        dvdAsText += aDvd.getStudio() + DELIMITER;

        dvdAsText += aDvd.getUserRatingNote();

        return dvdAsText;
    }


    private void writeRoster() throws DvdLibraryPersistenceException {
        // NOTE FOR APPRENTICES: We are not handling the IOException - but
        // we are translating it to an application specific exception and 
        // then simple throwing it (i.e. 'reporting' it) to the code that
        // called us.  It is the responsibility of the calling code to 
        // handle any errors that occur.
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(ROSTER_FILE));
        } catch (IOException e) {
            throw new DvdLibraryPersistenceException(
                    "Could not save Dvd data.", e);
        }


        String dvdAsText;
        List<Dvd> dvdList = this.getAllDvds();
        for (Dvd currentDvd : dvdList) {

            dvdAsText = marshallDvd(currentDvd);

            out.println(dvdAsText);
            // force PrintWriter to write line to the file
            out.flush();
        }
        // Clean up
        out.close();
    }

}
