package code;

import java.io.File;
import java.util.Scanner;

public class Board{
	
	/*The Sudoku Board is made of 9x9 cells for a total of 81 cells.
	 * In this program we will be representing the Board using a 2D Array of cells.
	 * 
	 */

	private Cell[][] board = new Cell[9][9];
	
	//The variable "level" records the level of the puzzle being solved.
	private String level = "";

	
	///TODO: CONSTRUCTOR
	//This must initialize every cell on the board with a generic cell.  It must also assign all of the boxIDs to the cells
	public Board()
	{
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				board[x][y] = new Cell(); //have to say new cell for each one
				board[x][y].setBoxID( 3*(x/3) + (y)/3+1);
			}
	}
	

	
	///TODO: loadPuzzle
	/*This method will take a single String as a parameter.  The String must be either "easy", "medium" or "hard"
	 * If it is none of these, the method will set the String to "easy".  The method will set each of the 9x9 grid
	 * of cells by accessing either "easyPuzzle.txt", "mediumPuzzle.txt" or "hardPuzzle.txt" and setting the Cell.number to 
	 * the number given in the file.  
	 * 
	 * This must also set the "level" variable
	 * TIP: Remember that setting a cell's number affects the other cells on the board.
	 */
	public void loadPuzzle(String level) throws Exception
	{
		this.level = level;
		String fileName = "easyPuzzle.txt";
		if(level.contentEquals("medium"))
			fileName = "mediumPuzzle.txt";
		else if(level.contentEquals("hard"))
			fileName = "hardPuzzle.txt";
		
		Scanner input = new Scanner (new File(fileName));
		
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				int number = input.nextInt();
				if(number != 0)
					solve(x, y, number);
			}
						
		input.close();
		
	}
	
	///TODO: isSolved
	/*This method scans the board and returns TRUE if every cell has been solved.  Otherwise it returns FALSE
	 * 
	 */
	public boolean isSolved()
	{
		boolean solved = true;
		
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board[0].length; y++)
			{
				if(board[x][y].getNumber() == 0)
				{
					solved = false;
				}
			}
		}
		
		return solved;
	}


	///TODO: DISPLAY
	/*This method displays the board neatly to the screen.  It must have dividing lines to show where the box boundaries are
	 * as well as lines indicating the outer border of the puzzle
	 */
	public void display()
	{
		System.out.println("_______________________");
		System.out.println(" ");
		
		for(int x = 0; x < board.length; x++)
		{
			System.out.print("|");
			for(int y = 0; y < board[0].length; y++)
			{
				System.out.print(board[x][y].getNumber() + " ");
				if(y % 3 == 2)
				{
					System.out.print("| ");
				}
			}
			if(x % 3 == 2)
			{
				System.out.println(" ");
				System.out.println("_______________________");
			}
			System.out.println(" ");
		}
		
		System.out.println(" ");
	}
	
	///TODO: solve 
	/*This method solves a single cell at x,y for number.  It also must adjust the potentials of the remaining cells in the same row,
	 * column, and box.
	 */
	public void solve(int x, int y, int number)
	{
		board[x][y].setNumber(number);
		
		for(int a = 0; a < x; a++)
		{
			board[a][y].cantBe(number);
		}
		for(int a = x + 1; a < board.length; a++)
		{
			board[a][y].cantBe(number);
		}
		
		for(int a = 0; a < y; a++)
		{
			board[x][a].cantBe(number);
		}
		for(int a = y + 1; a < board[0].length; a++)
		{
			board[x][a].cantBe(number);
		}
		
		for(int a = 0; a < board.length; a++)
		{
			for(int b = 0; b < board[0].length; b++)
			{
				if(board[a][b].getBoxID() == board[x][y].getBoxID() && a != x && b != y)
				{
					board[a][b].cantBe(number);	
				}
			}
		}
		
	}
	
	//logicCycles() continuously cycles through the different logic algorithms until no more changes are being made.
	public void logicCycles()throws Exception
	{
		int row = 0, column = 0, index = -1;
		while(isSolved() == false)
		{
			int changesMade = 0;
			do
			{
				changesMade = 0;

				changesMade += logic1();
				changesMade += logic2();
				changesMade += logic3();
				changesMade += logic4();
				System.out.println(changesMade);
				
				//delete
				System.out.println("_______________________");
				System.out.println(" ");
				for(int x = 0; x < board.length; x++)
				{
					System.out.print("|");
					for(int y = 0; y < board[0].length; y++)
					{
						System.out.print(board[x][y].getNumber() + " ");
						if(y % 3 == 2)
						{
							System.out.print("| ");
						}
					}
					if(x % 3 == 2)
					{
						System.out.println(" ");
						System.out.println("_______________________");
					}
					System.out.println(" ");
				}
				
				System.out.println(" ");
				//
				
				System.out.println(errorFound());//delete
				
				if(errorFound())
					break;
				
			}while(changesMade != 0);
			
			if(isSolved() == false && changesMade == 0)
			{
				column = (column % 9) + 1;
				if(column == 0)
				{
					row++;
				}
				
				logic5(row, column, index);
				break;
			}
		}			
		
	}
	
	
	///TODO: logic1
	/*This method searches each row of the puzzle and looks for cells that only have one potential.  If it finds a cell like this, it solves the cell 
	 * for that number. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic1() 
	{
		int changesMade = 0;

		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board[0].length; y++)
			{
				if(board[x][y].getNumber() == 0 && board[x][y].numberOfPotentials() == 1)
				{
					int number = 0;
					
					for(int z = 0; z < 10; z++)
					{
						if(board[x][y].canBe(z))
						{
							number = z;
						}
					}
					
					solve(x, y, number);
					changesMade++;
				}
			}
		}

		return changesMade;
					
	}
	
	///TODO: logic2
	/*This method searches each row for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell.  It then does the same thing for the columns.This also tracks the number of cells that 
	 * it solved as it traversed the board and returns that number.
	 */
	
	public int logic2() 
	{
		int changesMade = 0;
		
		for(int x = 0; x < board.length; x++)
		{
			for(int z = 0; z < 10; z++)
			{
				int canBe = 0;
				int column = 0;
				
				for(int y = 0; y < board[x].length; y++)
				{	
					if(board[x][y].getNumber() == 0 && board[x][y].canBe(z))
					{
						canBe++;
						column = y;
					}
				}
				
				if(canBe == 1)
				{
					solve(x, column, z);
					changesMade++;
				}	
			}
		}	
		
		for(int y = 0; y < board[0].length; y++)
		{
			for(int z = 0; z < 10; z++)
			{
				int canBe = 0;
				int row = 0;
				
				for(int x = 0; x < board.length; x++)
				{	
					if(board[x][y].getNumber() == 0 && board[x][y].canBe(z))
					{
						canBe++;
						row = x;
					}
				}
				
				if(canBe == 1)
				{
					solve(row, y, z);
					changesMade++;
				}	
			}
		}

		return changesMade;
	}
	
	///TODO: logic3
	/*This method searches each box for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic3()
	{
		int changesMade = 0;
		
		for(int z = 0; z < 9; z++)
		{
			int canBe = 0;
			int column = 0;
			int row = 0;
			
			for(int x = 0; x < board.length; x++)
			{
				for(int y = 0; y < board[0].length; y++)
				{	
					for(int n = 0; n < 10; n++)
					{
						if(board[x][y].getBoxID() == z && board[x][y].getNumber() == 0 && board[x][y].canBe(n))
						{
							canBe++;
							row = x;
							column = y;
						}		
					}	
					
					if(canBe == 1)
					{
						solve(row, column, z);
						changesMade++;
					}
				}
			}
		}
				
		return changesMade;	

	}
	
	
	///TODO: logic4
		/*This method searches each row for the following conditions:
		 * 1. There are two unsolved cells that only have two potential numbers that they can be
		 * 2. These two cells have the same two potentials (They can't be anything else)
		 * 
		 * Once this occurs, all of the other cells in the row cannot have these two potentials.  Write an algorithm to set these two potentials to be false
		 * for all other cells in the row.
		 * 
		 * Repeat this process for columns and rows.
		 * 
		 * This also tracks the number of cells that it solved as it traversed the board and returns that number.
		 */
	public int logic4() {
	    int changesMade = 0;

	    for (int row = 0; row < board.length; row++) 
	    {
	        for (int i = 0; i < board[row].length - 1; i++) 
	        {
	            if (board[row][i].getNumber() == 0 && board[row][i].numberOfPotentials() == 2) 
	            {
	                for (int j = i + 1; j < board[row].length; j++) 
	                {
	                    if (board[row][j].getNumber() == 0 && board[row][j].numberOfPotentials() == 2) 
	                    {
	                        if (board[row][i].getPotential() == board[row][j].getPotential()) 
	                        {
	                            for (int k = 0; k < board[row].length; k++) 
	                            {
	                                if (board[row][k].getNumber() == 0 && k != i && k != j) 
	                                {
	                                    if (board[row][k].canBe(board[row][i].getFirstPotential())) 
	                                    {
	                                        board[row][k].cantBe(board[row][i].getFirstPotential());
	                                        changesMade++;
	                                    }
	                                    if (board[row][k].canBe(board[row][i].getSecondPotential())) 
	                                    {
	                                        board[row][k].cantBe(board[row][i].getSecondPotential());
	                                        changesMade++;
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }
	    
	    for (int col = 0; col < board[0].length; col++) 
	    {
	        for (int i = 0; i < board.length - 1; i++) 
	        {
	            if (board[i][col].getNumber() == 0 && board[i][col].numberOfPotentials() == 2) 
	            {
	                for (int j = i + 1; j < board.length; j++) 
	                {
	                    if (board[j][col].getNumber() == 0 && board[j][col].numberOfPotentials() == 2) 
	                    {
	                        if (board[i][col].getPotential() == board[j][col].getPotential()) 
	                        {
	                            for (int k = 0; k < board.length; k++) 
	                            {
	                                if (board[k][col].getNumber() == 0 && k != i && k != j) 
	                                {
	                                    if (board[k][col].canBe(board[i][col].getFirstPotential())) 
	                                    {
	                                        board[k][col].cantBe(board[i][col].getFirstPotential());
	                                        changesMade++;
	                                    }
	                                    if (board[k][col].canBe(board[i][col].getSecondPotential())) 
	                                    {
	                                        board[k][col].cantBe(board[i][col].getSecondPotential());
	                                        changesMade++;
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }

	    return changesMade;
	}

	
	///TODO: errorFound
	/*This method scans the board to see if any logical errors have been made.  It can detect this by looking for a cell that no longer has the potential to be 
	 * any number.
	 */
	public boolean errorFound()
	{
		boolean errorFound = false;
		
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board[0].length; y++)
			{
				if(board[x][y].getFirstPotential() == -1)
				{
					errorFound = true;
				}	
			}
		}
		
		return errorFound;
	}
	
	//guessing
	Cell[][][] stack = new Cell[81][9][9];
	
	 public void logic5(int x, int y, int index) throws Exception
	 {
		 index++;
		
	     if (board[x][y].getNumber() == 0) 
	     {
	    	 copy(board, index);

	         board[x][y].setNumber(board[x][y].getFirstPotential());

	         logicCycles();
	               
	          if(!errorFound() && !isSolved())
	          {
	              solve(x, y, board[x][y].getFirstPotential());
	              logicCycles();
	          }
	          if(errorFound())
	          {
	        	  revert(board, index--);
	              if(board[x][y].getFirstPotential() != -1)
	            	  board[x][y].cantBe(board[x][y].getFirstPotential());
	          } 
	     }
	 }
	 
	 public void copy(Cell[][] board, int index)
	 {
		 stack[index] = board;
	 }
	 
	 public void revert(Cell[][] board, int index)
	 {
		 board = stack[index];
	 }
	
}

