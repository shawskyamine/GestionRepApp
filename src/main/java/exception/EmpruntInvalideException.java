package exception;

public class EmpruntInvalideException  extends Exception {
	    
	    public EmpruntInvalideException() {
	        super("Les informations de l'emprunt sont invalides");
	    }
	    
	    public EmpruntInvalideException(String message) {
	        super(message);
	    }
	    
	    public EmpruntInvalideException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}

