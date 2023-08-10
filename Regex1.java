  // Do learn to insert your names and a brief description of
  // what the program is supposed to do!

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

  class Regex1 {

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
	  semiSym	   =  2,
	  orSym		   =  3,
	  dotSym	   =  4,
	  starSym	   =  5,
	  questionSym  =  6,
	  plusSym	   =  7,
	  lParenSym    =  8,
	  rParenSym    =  9,
	  lBrackSym	   =  10,
	  rBrackSym    =  11,
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
	  symLex.append(ch);
	  switch(ch){
		  case EOF:
			symLex = new StringBuilder ("EOF");
			symKind = EOFSym;
			break;
		  case ';':
			symKind = semiSym;
			getChar();
			break;
		  case '(':
		    symKind = lParenSym;
			getChar();
			break;
		  case ')':
		    symKind = rParenSym;
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
		    symKind = lBrackSym;
			getChar();
			break;
		  case ']':
		    symKind = rBrackSym;
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
				symLex.append(ch);
				getChar();
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
	  
	  
	  //build an escapedSym

      sym = new Token(symKind, symLex.toString());
    } // getSym

  /*  ++++ Commented out for the moment

    // +++++++++++++++++++++++++++++++ Parser +++++++++++++++++++++++++++++++++++

    static void accept(int wantedSym, String errorMessage) {
    // Checks that lookahead token is wantedSym
      if (sym.kind == wantedSym) getSym(); else abort(errorMessage);
    } // accept

  ++++++ */

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

      do {
        getSym();                                 // Lookahead symbol
        OutFile.StdOut.write(sym.kind, 3);
        OutFile.StdOut.writeLine(" " + sym.val);
      } while (sym.kind != EOFSym);

  /*  After the scanner is debugged, comment out lines 125 to 129 and uncomment lines 134 to 139. 
      In other words, replace the code immediately above with this code: */

  /*
      getSym();                             // Lookahead symbol
      RE();                                 // Start to parse from the goal symbol
      // if we get back here everything must have been satisfactory
      System.out.println("Parsed correctly");
  */
       output.close();
    } // main

  } // Regex
