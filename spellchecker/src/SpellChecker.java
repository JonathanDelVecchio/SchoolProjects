import java.io.*;
import javax.swing.*;
import java.util.Scanner;
import java.util.HashSet;
import java.util.TreeSet;
public class SpellChecker
{


public static void main(String[] args)
{
  Scanner filein;
  HashSet<String> dict = new HashSet<String>();  
  Scanner userFile;
  try
  {
  //add path to words.txt below
     filein = new Scanner(new File("")); //Enter File Path here
   while (filein.hasNext())
   {
    String tempWord = filein.next();
    dict.add(tempWord.toLowerCase());
   }
   userFile = new Scanner(getInputFileNameFromUser());
   userFile.useDelimiter("[^a-zA-Z]+");
   HashSet<String> badWordsIn = new HashSet<String>();
   while (userFile.hasNext())
   {
    String userToken = userFile.next();
    userToken = userToken.toLowerCase();
    if (!dict.contains(userToken) && !badWordsIn.contains(userToken))
    {


     badWordsIn.add(userToken);
     TreeSet<String> goodWordIn = new TreeSet<String>();
     goodWordIn = corrections(userToken, dict);
     System.out.print(userToken + ": ");
     if (goodWordIn.isEmpty())
      System.out.println("(no suggestions)");
     else
     {
      int count = 0;
      for (String goodToken : goodWordIn)
      {
       System.out.print(goodToken);
       if (count < goodWordIn.size() - 1)
        System.out.print(", ");
       else
        System.out.print("\n");
       count++;
      }
     }


    }


   }


  } catch (FileNotFoundException e)
  {
      System.out.println("The Word.txt File not Found Please check the path");
      System.exit(0);
  }


}
static File getInputFileNameFromUser()
{


  JFileChooser fileDialog = new JFileChooser();
  fileDialog.setDialogTitle("Select File for Input");
  int option = fileDialog.showOpenDialog(null);
  if (option != JFileChooser.APPROVE_OPTION)
   return null;
  else
   return fileDialog.getSelectedFile();


}




static TreeSet corrections(String badWord, HashSet dictionary)
{


  TreeSet<String> possibleWordsFile = new TreeSet<String>();
  String subString1, subString2, possibility;


  for (int i = 0; i < badWord.length(); i++)
  {
   subString1 = badWord.substring(0, i);
   subString2 = badWord.substring(i + 1);


   possibility = subString1 + subString2;
   if (dictionary.contains(possibility))
    possibleWordsFile.add(possibility);


   for (char ch = 'a'; ch <= 'z'; ch++)
   {
    possibility = subString1 + ch + subString2;
    if (dictionary.contains(possibility))
     possibleWordsFile.add(possibility);
   }


   subString1 = badWord.substring(0, i);
   subString2 = badWord.substring(i);


   for (char ch = 'a'; ch <= 'z'; ch++)
   {
    possibility = subString1 + ch + subString2;
    if (dictionary.contains(possibility))
    {
     possibleWordsFile.add(possibility);
    }     
   }


   char ch = ' ';
   possibility = subString1 + ch + subString2;
   if (dictionary.contains(subString1) && dictionary.contains(subString2))
   {
    possibleWordsFile.add(possibility);
   }    


  }


  for (int i = 1; i < badWord.length(); i++)
  {
   subString1 = badWord.substring(0, i - 1);
   char ch1 = badWord.charAt(i - 1);
   char ch2 = badWord.charAt(i);
   subString2 = badWord.substring(i + 1);
   possibility = subString1 + ch2 + ch1 + subString2;
   if (dictionary.contains(possibility))
    possibleWordsFile.add(possibility);
  }


  return possibleWordsFile;


  }
}