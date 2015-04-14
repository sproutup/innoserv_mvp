package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class UserNickNameTest {
	
	public String email;

	public String name;

	public String nickname;

	public String firstName;

	public String lastName;

	public boolean randomNumberflag= false;
	
    public String getFirstName(){
    	if (firstName == null || firstName == "") {
            //fetch it from the fullname
    		String[] bits = name.split(" ");
    		firstName = bits[0];
        }
    	return firstName;
    }
    
    public String getLastName(){
    	if (lastName == null || lastName == "") {
            //fetch it from the fullname
    		if (name != null && name.length() > 0) {
	    		String[] bits = name.split(" ");
	    		if (bits.length==2){//name with no middle name
	    			lastName = bits[1];
	    		} else if (bits.length==3){//name with Middle Name
	    			lastName = bits[2];
	    		}
    		}
        }
    	return lastName;
    }
	
    public boolean isNickNameUnique(String nickname) {
    	System.out.println("Is Unique " + nickname + " ?");
    	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    	String str = "n";
    	try {
			str = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if (str.equals("n"))
    		return false;
    	else return true;
    	
    }
    
    public String generateUniqueUserNickName() {
         
    	ArrayList<String> list = generateListOfUserNickNames();
    	
        //iterate through the list while we get the unique name
    	if(!randomNumberflag){	
	        for (String temp: list){
	        	if(isNickNameUnique(temp)){
	            	nickname = temp;
	        		return nickname;
	    	    }
	        }
        }
        /**
		 * Unique name didn't work without numbers
		 * generate nick name suffix with random numbers
		 */
        Random generator = new Random();
        /**
		 * generate random number
		 */
		int numberGen = generator.nextInt(99);
	     for (String temp: list){
	        	if(isNickNameUnique(temp+numberGen)){
	            	nickname = temp+numberGen;
	        		return nickname;
	    	    }
	        }
        
	     randomNumberflag= true;
        return generateUniqueUserNickName();
        
    }
    
    
    
    /**
     * Policy - Extracted and enhanced from Twitter
     * 
     * Username cannot be longer than 15 characters. Though Full name can be longer (20 characters); 
     * Usernames are kept shorter for the sake of ease. Minimum size is 3 characters
     * A username can only contain alphanumeric characters (letters A-Z, numbers 0-9) with the exception of underscores (No symbols, dashes, or spaces).  
     * @return
     */
    
    public ArrayList<String> generateListOfUserNickNames() {
    	
    	ArrayList<String> usernames = new ArrayList<String>();
      		
        lastName = getLastName();
        
        String lName = null;
        String fName = null;
        
        if (lastName!=null){
        	lName = lastName.replaceAll("[^\\w\\s\\_]", "");
        }
        
        fName = getFirstName().replaceAll("[^\\w\\s\\_]", "");
        
        if (email != null && email.length() > 0) {
            usernames.add(email.substring(0, email.indexOf("@")).toLowerCase());
            //usernames.add(email.substring(0, email.indexOf("@")).toLowerCase() + numberGen);
        }
        
        if (fName != null && fName.length() > 0 && lName != null && lName.length() > 0) {
        	usernames.add((fName + lName).toLowerCase());
        	usernames.add((lName + fName).toLowerCase());
        	usernames.add((fName.charAt(0) + lName).toLowerCase());
            usernames.add((fName.charAt(0) + "." + lName).toLowerCase());
            usernames.add((lName + fName.charAt(0)).toLowerCase());
            //usernames.add((lName + "." + fName.charAt(0) + Integer.toString(numberGen)).toLowerCase());
            //usernames.add((fName + lName + Integer.toString(numberGen)).toLowerCase());
            usernames.add((fName + "." + lName).toLowerCase());
            usernames.add((fName + lName.charAt(0)).toLowerCase());
            usernames.add((fName + "." + lName.charAt(0)).toLowerCase());
            usernames.add((lName.charAt(0) + fName).toLowerCase());
            usernames.add((lName.charAt(0) + "." + fName).toLowerCase());
            //usernames.add((lName.charAt(0) + fName.charAt(0)) + Integer.toString(numberGen).toLowerCase());
            //usernames.add((lName.charAt(0) + "." + fName.charAt(0)).toLowerCase()+Integer.toString(numberGen));
            //usernames.add((fName.charAt(0) + lName.charAt(0) + Integer.toString(numberGen)).toLowerCase());
            //usernames.add((fName.charAt(0) + "." + lName.charAt(0) + Integer.toString(numberGen)).toLowerCase());
            
            if (lName!=null && lName.length() > 4) {
                usernames.add((fName + lName.substring(0, 4)).toLowerCase());
                usernames.add((fName + "." + lName.substring(0, 4)).toLowerCase());
            }
            if (lName!=null && lName.length() > 5) {
                usernames.add((fName + lName.substring(0, 5)).toLowerCase());
                usernames.add((fName + "." + lName.substring(0, 5)).toLowerCase());
            }
            if (lName!=null && lName.length() > 6) {
                usernames.add((fName + lName.substring(0, 6)).toLowerCase());
                usernames.add((fName + "." + lName.substring(0, 6)).toLowerCase());
            }
  
        }
        
        if (fName != null && fName.length() > 2 && (lName == null || lName.length() == 0)){
        	usernames.add((fName).toLowerCase());
        }
        
        return usernames;
    }

    public static void main(String[] args){
    	UserNickNameTest usr = new UserNickNameTest();
    	usr.name = "Ni' O'?&$#@(N)eily";
    	//usr.firstName = "Nitin";
    	//usr.lastName = "Jain";
    	usr.email = "nitinj76@gmail.com";
    	System.out.println("First Name " + usr.getFirstName());
    	System.out.println("Last Name " + usr.getLastName());

    	System.out.println("List of suggested Nick Names " + usr.generateListOfUserNickNames());
    	System.out.println("Unique Name " + usr.generateUniqueUserNickName());
    	
    	
    }
    
    
}
