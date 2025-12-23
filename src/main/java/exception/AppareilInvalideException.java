package exception;

public class AppareilInvalideException extends Exception {
	    
	    public AppareilInvalideException() {
	        super("Les informations de l'appareil sont invalides");
	    }
	    
	    public AppareilInvalideException(String message) {
	        super(message);
	    }
	    
	    public AppareilInvalideException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}

