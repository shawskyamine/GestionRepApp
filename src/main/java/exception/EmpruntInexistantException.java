package exception;

public class EmpruntInexistantException extends Exception {
	    
	    public EmpruntInexistantException() {
	        super("L'emprunt n'existe pas");
	    }
	    
	    public EmpruntInexistantException(Long id) {
	        super("L'emprunt avec l'ID " + id + " n'existe pas");
	    }
	    
	    public EmpruntInexistantException(String message) {
	        super(message);
	    }
	    
	    public EmpruntInexistantException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}

