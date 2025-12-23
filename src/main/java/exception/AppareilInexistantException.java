package exception;

public class AppareilInexistantException extends Exception {
	    
	    public AppareilInexistantException() {
	        super("L'appareil n'existe pas");
	    }
	    
	    public AppareilInexistantException(String imei) {
	        super("L'appareil avec l'IMEI " + imei + " n'existe pas");
	    }
	    
	    public AppareilInexistantException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
