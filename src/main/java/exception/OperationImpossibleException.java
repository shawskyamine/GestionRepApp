package exception;

public class OperationImpossibleException  extends Exception {
	    
	    public OperationImpossibleException() {
	        super("L'opération ne peut pas être effectuée");
	    }
	    
	    public OperationImpossibleException(String message) {
	        super(message);
	    }
	    
	    public OperationImpossibleException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
