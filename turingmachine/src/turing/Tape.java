package turing;
/**
 * public Cell getCurrentCell() -- returns the pointer that points to the current cell.

public char getContent() -- returns the char from the current cell.

public void setContent(char ch) -- changes the char in the current cell to the specified value.

public void moveLeft() -- moves the current cell one position to the left along the tape. 
						  Note that if the current cell is the leftmost cell that exists, 
						  then a new cell must be created and added to the tape at the left of the current cell, 
						  and then the current cell pointer can be moved to point to the new cell. 
						  The content of the new cell should be a blank space. 
						  (Remember that the Turing machine's tape is conceptually infinite, so your linked list must be prepared to expand on-demand when the machine wants to move past the current end of the list.)

public void moveRight() -- moves the current cell one position to the right along the tape. 
                           Note that if the current cell is the rightmost cell that exists, 
                           then a new cell must be created and added to the tape at the right of the current cell, 
                           and then the current cell pointer can be moved to point to the new cell. 
                           The content of the new cell should be a blank space.
public String getTapeContents() -- returns a String consisting of the chars from all the cells on the tape, 
                                   read from left to right, except that leading or trailing blank characters should be discarded. 
                                   The current cell pointer should not be moved by this method; 
                                   it should point to the same cell after the method is called as it did before. 
                                   You can create a different pointer to move along the tape and get the full contents. 
                             

 */
public class Tape {   
	   
	private Cell CurrentCell;  // instance variable of type Cell that points to the current cell  
	   
	
	public Tape() {     // Tape Constructor to create a blank tape with a single cell       
		Cell BlankCell = new Cell(); 
		BlankCell.content = ' ';
		BlankCell.prev = null;
		BlankCell.next = null;
		CurrentCell = BlankCell;
	}


	public Cell getCurrentCell() { // returns the pointer that points to the current cell.
		return CurrentCell;
	}

	public char getContent() { // returns the char from the current cell.
		return CurrentCell.content;
	}

	public void setContent(char ch) { // changes the char in the current cell to the specified value.
		CurrentCell.content = ch;
	}

	public void moveLeft() { // moves the current cell one position to the left along the tape.
		if (CurrentCell.prev == null) {
			Cell NewCell = new Cell();
			NewCell.content = ' ';
			NewCell.prev = null;
			NewCell.next = CurrentCell;
			CurrentCell.prev = NewCell;
		}
		CurrentCell = CurrentCell.prev;
	}

	public void moveRight() { // moves the current cell one position to the right along the tape.
		if (CurrentCell.next == null) {
			Cell NewCell = new Cell();
			NewCell.content = ' ';
			NewCell.next = null;
			NewCell.prev = CurrentCell;
			CurrentCell.next = NewCell;
		}
		CurrentCell = CurrentCell.next;
	}

	public String getTapeContents() { // returns a String consisting of the chars from all the cells on the tape.
		Cell CellPointer = CurrentCell;
		while (CellPointer.prev != null)
			CellPointer = CellPointer.prev;
		String Content = "";
		while (CellPointer != null) {
			Content += CellPointer.content;
			CellPointer = CellPointer.next;
		}
		Content = Content.trim(); 
		return Content;
	}
}

