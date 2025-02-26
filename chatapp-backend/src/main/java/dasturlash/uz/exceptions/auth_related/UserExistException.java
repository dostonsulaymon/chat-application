package dasturlash.uz.exceptions.auth_related;

public class UserExistException extends RuntimeException {
  public UserExistException(String message) {
    super(message);
  }
}
