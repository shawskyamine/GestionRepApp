package exception;

public class DonneesInvalidesException  extends Exception {
	    
	    public DonneesInvalidesException() {
	        super("Les données fournies sont invalides");
	    }
	    
	    public DonneesInvalidesException(String message) {
	        super(message);
	    }
	    
	    public DonneesInvalidesException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
