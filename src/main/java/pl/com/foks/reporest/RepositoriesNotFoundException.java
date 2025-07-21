package pl.com.foks.reporest;

public class RepositoriesNotFoundException extends RuntimeException {
  public RepositoriesNotFoundException(String message) {
    super(message);
  }
}
