  // Caron, Litha, Luke
  // Task4 Regex3: develop parser routines for each non-terminal 
  // accept routine
  // error message, close output file, abort parsing
  // This is a skeleton program for developing a parser for Regular Expressions
  // P.D. Terry, Rhodes University; Modified by KL Bradshaw 2023

  import java.util.*;
  import library.*;

  class Token {
    public int kind;
    public String val;

    public Token(int kind, String val) {
      this.kind = kind;
      this.val = val;
    }

  } // Token

  class Regex3 {

    // +++++++++++++++++++++++++ File Handling and Error handlers ++++++++++++++++++++

    static InFile input;
    static OutFile output;

    static String newFileName(String oldFileName, String ext) {
    // Creates new file name by changing extension of oldFileName to ext
      int i = oldFileName.lastIndexOf('.');
      if (i < 0) return oldFileName + ext; else return oldFileName.substring(0, i) + ext;
    }

    static void reportError(String errorMessage) {
    // Displays errorMessage on standard output and on reflected output
      System.out.println(errorMessage);
      output.writeLine(errorMessage);
    }

    static void abort(String errorMessage) {
    // Abandons parsing after issuing error message
      reportError(errorMessage);
      output.close();
      System.exit(1);
    }

    // +++++++++++++++++++++++  token kinds enumeration +++++++++++++++++++++++++

    static final int
      noSym        =  0,
      EOFSym       =  1,
      // and others like this (added syms 2-14)
	  semicolonSym =  2,
	  orSym		   =  3,
	  dotSym	   =  4,
	  starSym	   =  5,
	  questionSym  =  6,
	  plusSym	   =  7,
	  lparenSym    =  8,
	  rparenSym    =  9,
	  lbrackSym	   =  10,
	  rbrackSym    =  11,
	  minusSym     =  12,
	  atomicSym    =  13,
	  escapedSym   =  14;
	  

    // +++++++++++++++++++++++++++++ Character Handler ++++++++++++++++++++++++++

    static final char EOF = '\0';
    static boolean atEndOfFile = false;

    // Declaring ch as a global variable is done for expediency - global variables
    // are not always a good thing

    static char ch;    // look ahead character for scanner

    static void getChar() {
    // Obtains next character ch from input, or CHR(0) if EOF reached
    // Reflect ch to output
      if (atEndOfFile) ch = EOF;
      else {
        ch = input.readChar();
        atEndOfFile = ch == EOF;
        if (!atEndOfFile) output.write(ch);
      }
    } // getChar

    // +++++++++++++++++++++++++++++++ Scanner ++++++++++++++++++++++++++++++++++

    // Declaring sym as a global variable is done for expediency - global variables
    // are not always a good thing

    static Token sym;
	
    static void getSym() {
    // Scans for next sym from input
      while (ch > EOF && ch <= ' ') getChar(); //skip whitespace
      StringBuilder symLex = new StringBuilder();
      int symKind = noSym;
      // over to you!
	  //check for comments to ignore:
	  if (ch =='\\'){ //this represents a single backslash character for a comment
		getChar();
		while (ch!=EOF){
			getChar();
			if (ch=='\\'){
				getChar();
				while (ch > EOF && ch <= ' ') getChar(); //skip whitespace
				if (ch!='\\'){
					break;
				}
			}
		}//while
	  }//if comment, ignore
	  symLex.append(ch);
	  switch(ch){	
		  case EOF:
			symLex = new StringBuilder ("EOF");
			symKind = EOFSym;
			break;
		  case ';':
			symKind = semicolonSym;
			getChar();
			break;
		  case '(':
		    symKind = lparenSym;
			getChar();
			break;
		  case ')':
		    symKind = rparenSym;
			getChar();
			break;
		  case '|':
		    symKind = orSym;
			getChar();
			break;
		  case '.':
		    symKind = dotSym;
			getChar();
			break;
		  case '*':
		    symKind = starSym;
			getChar();
			break;
		  case '?':
		    symKind = questionSym;
			getChar();
			break;
		  case '+':
		    symKind = plusSym;
			getChar();
			break;
		  case '-':
		    symKind = minusSym;
			getChar();
			break;
		  case '[':
		    symKind = lbrackSym;
			getChar();
			break;
		  case ']':
		    symKind = rbrackSym;
			getChar();
			break;
		  //build up an escaped character
		  case '"':
			getChar();
			symLex.append(ch);
			if (ch != '"'){
				getChar();
				symLex.append(ch);
				if (ch== '"'){
					symKind = escapedSym;
					getChar();
					break;
				}
			}
			symKind = noSym;
			getChar();
			break;
		  case '\'':
			getChar();
			symLex.append(ch);
			if (ch != '\''){
				getChar();
				symLex.append(ch);
				if (ch== '\''){
					symKind = escapedSym;
					getChar();
					break;
				}
			}
			symKind = noSym;
			getChar();
			break;	
		  default:
		    symKind = atomicSym;
			getChar();
			break;
	  }//switch
      sym = new Token(symKind, symLex.toString());
    } // getSym

  /*  ++++ uncommented out for task 4*/

    // +++++++++++++++++++++++++++++++ Parser +++++++++++++++++++++++++++++++++++

    static void accept(int wantedSym, String errorMessage) {
    // Checks that lookahead token is wantedSym
      if (sym.kind == wantedSym) getSym(); else abort(errorMessage);
    } // accept

	/*parsing routines* added for task 4*/
	static void RE(){
		//RE = { Expression ";" } EOF .
		Expression();
		while (sym.kind == semicolonSym){
			accept(semicolonSym, "; expected");
			Expression();
		}
		accept(EOFSym, "EOF expected");
	}//RE
	static void Expression(){
		//Expression = Term {"|" Term} .
		Term();
		while (sym.kind == orSym){
			accept(orSym, "| expected");
			Term();
		}
	}//Expression
	static void Term (){
		//Term = Factor { ["."] Factor } .
		Factor();
		while ((sym.kind != orSym) && (sym.kind != semicolonSym) && (sym.kind != EOFSym)){ //is this correct? keep getting factors
			if (sym.kind == dotSym){
				accept(dotSym, ". expected");
			}
			Factor();
		}//while	
	}//Term 
	static void Factor(){
		//Factor = Element [ "*" | "?" | "+" ].
		Element();
		if (sym.kind == starSym){
			accept(starSym, "* expected");
		}
		else if(sym.kind == questionSym){
			accept(questionSym, "? expected");
		}
		else{
			accept (plusSym, "+ expected");
		}
	}
	static void Element(){
		//Element = Atom | Range | "(" Expression ")".
		if (sym.kind==lparenSym){
			accept (lparenSym, "( expected");
			Expression();
			accept (rparenSym, ") expected");
		}
		else if (sym.kind == lbrackSym){
			Range();
		}
		else{
			Atom();
		}
	}//Element
	static void Range(){
		//Range = "[" OneRange { OneRange } "]".
		accept(lbrackSym, "[ expected");
		OneRange();
		while (sym.kind != lbrackSym){ //need a while loop here as there might be many
			OneRange();
		}
		accept (rbrackSym, "] expected");	
	}//Range
	static void OneRange(){
		Atom();
		if (sym.kind==minusSym){
			accept(minusSym, "- expected");
			Atom();
		}
	}//OneRange
	static void Atom (){
		//Atom = atomic | escaped .
		switch(sym.kind){
			case escapedSym:
				accept(escapedSym, "escaped character expected");
				break;
			default:
				accept (atomicSym, "atomic character expected");
				break;
		}//switch
	}//Atom
	
  /*++++++ */

    // +++++++++++++++++++++ Main driver function +++++++++++++++++++++++++++++++

    public static void main(String[] args) {
      // Open input and output files from command line arguments
      if (args.length == 0) {
        System.out.println("Usage: RegExp FileName");
        System.exit(1);
      }
      input = new InFile(args[0]);
      output = new OutFile(newFileName(args[0], ".out"));

      getChar();                                  // Lookahead character

  /*  To test the scanner we can use a loop like the following: */
/**
      do {
        getSym();                                 // Lookahead symbol
        OutFile.StdOut.write(sym.kind, 3);
        OutFile.StdOut.writeLine(" " + sym.val);
      } while (sym.kind != EOFSym);
**/
  /*  After the scanner is debugged, comment out lines 125 to 129 and uncomment lines 134 to 139. 
      In other words, replace the code immediately above with this code: */


      getSym();                             // Lookahead symbol
      RE();                                 // Start to parse from the goal symbol
      // if we get back here everything must have been satisfactory
      System.out.println("Parsed correctly");
	  
       output.close();
    } // main

  } // Regex
