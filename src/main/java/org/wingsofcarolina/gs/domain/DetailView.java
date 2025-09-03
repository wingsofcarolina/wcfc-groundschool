package org.wingsofcarolina.gs.domain;

public class DetailView {

  // This is the view normally used to share user
  // information with the client.
  public static class Normal {}

  // This view exposes the 'token' field, which
  // is only to be done during the login operation.
  public static class Login extends Normal {}
}
