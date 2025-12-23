package exception;

public class AppareilDejaExistantException extends Exception {
	    
	    public AppareilDejaExistantException() {
	        super("Un appareil avec cet IMEI existe déjà");
	    }
	    
	    public AppareilDejaExistantException(String imei) {
	        super("Un appareil avec l'IMEI " + imei + " existe déjà");
	    }
	    
	    public AppareilDejaExistantException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
