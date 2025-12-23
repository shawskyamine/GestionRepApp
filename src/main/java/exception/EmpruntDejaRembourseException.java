package exception;

public class EmpruntDejaRembourseException extends Exception {
	    
	    public EmpruntDejaRembourseException() {
	        super("L'emprunt est déjà remboursé");
	    }
	    
	    public EmpruntDejaRembourseException(Long id) {
	        super("L'emprunt avec l'ID " + id + " est déjà remboursé");
	    }
	    
	    public EmpruntDejaRembourseException(String message) {
	        super(message);
	    }
	    
	    public EmpruntDejaRembourseException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}

