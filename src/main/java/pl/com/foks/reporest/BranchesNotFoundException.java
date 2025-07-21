package pl.com.foks.reporest;

public class BranchesNotFoundException extends RuntimeException {
  public BranchesNotFoundException(String message) {
    super(message);
  }
}
